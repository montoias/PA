package ist.meic.pa;

import ist.meic.pa.annotations.Assertion;
import ist.meic.pa.annotations.ExtendedAssertion;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

/**
 * This class allows the user to instrument fields, methods and constructors
 * that have the "Assertion/ExtendedAssertion" annotation".
 * 
 * @author group3
 * 
 */
public class AssertionTranslator implements Translator {

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
	CannotCompileException {

	}

	@Override
	public void onLoad(ClassPool pool, String className)
			throws NotFoundException, CannotCompileException {
		CtClass ctClass = pool.get(className);
		try {
			addHashSet(ctClass);
			makeAssertable(ctClass);
		} catch (CannotCompileException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds an HashSet to the given class, that is used to track not initialized
	 * variables
	 * 
	 * @param ctClass
	 * @throws CannotCompileException
	 */
	private void addHashSet(CtClass ctClass) throws CannotCompileException {
		CtField ctField = CtField
				.make("java.util.HashSet variables$notInit = new java.util.HashSet();",
						ctClass);
		ctClass.addField(ctField);
	}

	/**
	 * The "Assertion" annotations are interpreted in the given class. The
	 * "AssertionBefore" annotations are also interpreted in the given class.
	 * 
	 * @param ctClass
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	private void makeAssertable(CtClass ctClass) throws CannotCompileException,
	NotFoundException {

		for (CtBehavior ctBehavior : ctClass.getDeclaredBehaviors()) {
			assertBehaviorField(ctBehavior);
			assertBehavior(ctBehavior, ctClass);
		}
	}

	/**
	 * Returns an ExprEditor with a given template for Assertion annotations. It
	 * has both cases when the Field Access is a read, or a write. The
	 * ExprEditor is later used to instrument a method/constructor.
	 * 
	 * @param ctBehavior
	 * @throws CannotCompileException
	 */
	private void assertBehaviorField(CtBehavior ctBehavior)
			throws CannotCompileException {
		ctBehavior.instrument(new ExprEditor() {

			public void edit(FieldAccess fa) throws CannotCompileException {
				try {
					String template;
					CtField ctField = fa.getField();

					if (fa.isWriter() && ctField.hasAnnotation(Assertion.class)) {
						template = "  {"
								+ "  $0.%s = $1;"
								+ "  if(!(%s))"
								+ "    throw new RuntimeException(\"The assertion %s is false\");"
								+ "  variables$notInit.add(\"%s\");" + "} ";

						String name = fa.getField().getName();
						String assertion = ((Assertion) ctField
								.getAnnotation(Assertion.class)).value();
						fa.replace(String.format(template, name, assertion,
								assertion, name));
					} else if (fa.isReader()
							&& ctField.hasAnnotation(Assertion.class)) {
						template = "  {"
								+ "  if(!(variables$notInit.contains(\"%s\")))"
								+ "    throw new RuntimeException(\"Error: %s was not initialized\");"
								+ "  $_ = $proceed($$);" + "} ";

						String name = fa.getField().getName();
						fa.replace(String.format(template, name, name, name));
					}

				} catch (NotFoundException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Instruments the given behavior to interpret the "Assertion" annotations.
	 * Checks for the "Assertion" annotations in superclasses too. The same goes
	 * for "ExtendedAssertion" annotations.
	 * 
	 * @param ctBehavior
	 * @throws CannotCompileException
	 */
	private void assertBehavior(CtBehavior ctBehavior, CtClass ctClass)
			throws CannotCompileException, NotFoundException {

		String template = " if(!(%s))"
				+ "    throw new RuntimeException(\"The assertion %s is false\");";
		String assertion = null;

		if (ctBehavior.hasAnnotation(Assertion.class)) {
			assertion = checkSuperclass(ctBehavior, "Assertion");
		}
		else if (ctBehavior.hasAnnotation(ExtendedAssertion.class)) {
			assertion = checkSuperclass(ctBehavior, "ExtendedAssertion");
		}
		
		if(assertion != null) {
			if (ctBehavior.getMethodInfo().isMethod()) {
				assertMethod((CtMethod) ctBehavior, ctClass, template,
						assertion);
			} else if (ctBehavior.getMethodInfo().isConstructor()) {
				assertConstructor((CtConstructor) ctBehavior, ctClass,
						template, assertion);
			}
		}
	}

	/**
	 * Instruments the given constructor to interpret the "Assertion" annotations.
	 * The same goes for "ExtendedAssertion" annotations.
	 * 
	 * @param ctConstructor
	 * @param ctClass
	 * @param template
	 * @param assertion
	 * @throws CannotCompileException
	 */
	private void assertConstructor(CtConstructor ctConstructor, CtClass ctClass,
			String template, String assertion) throws CannotCompileException {

		if(ctConstructor.hasAnnotation(Assertion.class)){
			String constructorName = ctConstructor.getName();
			String newMethodName = constructorName + "$orig";

			CtMethod newMethod = ctConstructor.toMethod(newMethodName,
					ctClass);
			ctClass.addMethod(newMethod);

			ctConstructor.setBody("return " + newMethodName + "($$);");
			ctConstructor.insertAfter(String.format(template, assertion,
					assertion));
		}
		else if (ctConstructor.hasAnnotation(ExtendedAssertion.class)) {
			ctConstructor.insertBeforeBody(String.format(template,
					assertion, assertion));
		}
	}

	/**
	 * Instruments the given method to interpret the "Assertion" annotations.
	 * The same goes for "ExtendedAssertion" annotations.
	 * 
	 * @param originalMethod
	 * @param ctClass
	 * @param template
	 * @param assertion
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	private void assertMethod(CtMethod originalMethod, CtClass ctClass,
			String template, String assertion) throws CannotCompileException,
			NotFoundException {

		String originalMethodName = originalMethod.getName();
		String newMethodName = originalMethodName + "$orig";

		CtMethod newMethod = CtNewMethod.copy(originalMethod,
				newMethodName, ctClass, null);
		
		ctClass.addMethod(newMethod);

		originalMethod.setBody("return " + newMethodName + "($$);");
		
		if (originalMethod.hasAnnotation(Assertion.class)) {
			originalMethod.insertAfter(String.format(template, assertion, assertion));
		}
		else if (originalMethod.hasAnnotation(ExtendedAssertion.class)) {
			String parse[] = assertion.split(" && ");
			String assertionBefore = "";
			String assertionAfter = "";
			
			for (String s : parse) {
				if (!s.contains("$_")) {
					assertionBefore += assertionBefore.equals("") ? s : " && " + s;
				}
				else {
					assertionAfter += assertionAfter.equals("") ? s : " && " + s;
				}
			}
			
			originalMethod.insertBefore(String.format(template, assertionBefore,
					assertion));
			originalMethod.insertAfter(String.format(template, assertionAfter, assertion));
		}

	}

	/**
	 * Checks for the annotation given by assertionClass in the behavior
	 * passed by ctBehavior. It also traverses the superclasses to check
	 * for annotations in the same behavior, but of the superclass.
	 * 
	 * @param ctBehavior
	 * @param assertionClass
	 * @return
	 */
	private String checkSuperclass(CtBehavior ctBehavior, String assertionClass) {
		String assertion = null;

		try {
			CtClass nextClass = ctBehavior.getDeclaringClass().getSuperclass();

			if (nextClass != null) {
				assertion = checkSuperclass(nextClass
						.getDeclaredMethod(ctBehavior.getName()), assertionClass);
			}
		} catch (NotFoundException e) {
			// If this exception is thrown, it means it doesn't exist in the
			// superclass, so there's nothing to do
		}

		try {
			String value = null;
			
			if (assertionClass.equals("Assertion") && ctBehavior.hasAnnotation(Assertion.class)) {
				value = ((Assertion) ctBehavior.getAnnotation(Assertion.class)).value();
				
			}
			else if (assertionClass.equals("ExtendedAssertion") && ctBehavior.hasAnnotation(ExtendedAssertion.class)) {
				value = ((ExtendedAssertion) ctBehavior.getAnnotation(ExtendedAssertion.class)).value();
			}
			
			if(value != null) {
				assertion = (assertion == null) ? value : assertion + " && "
						+ value;
			}
		} catch (ClassNotFoundException e) {
			// Not supposed to happen
		}

		return assertion;
	}
}