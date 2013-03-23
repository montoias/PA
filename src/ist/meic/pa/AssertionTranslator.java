package ist.meic.pa;

import java.util.ArrayList;

import ist.meic.pa.annotations.Assertion;
import ist.meic.pa.annotations.ExtendedAssertion;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CodeConverter;
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
	
	private ArrayList<String> extendedAssertions = new ArrayList<String>();

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
	CannotCompileException {

	}

	@Override
	public void onLoad(ClassPool pool, String className)
			throws NotFoundException, CannotCompileException {
		if(className.equals("ist.meic.pa.ArrayAdvisor"))
			return;
		CtClass ctClass = pool.get(className);
		try {
			addHashSet(ctClass);
			inspectFields(ctClass);
			inspectArrays(pool, ctClass);
			makeAssertable(ctClass);
		} catch (ClassNotFoundException e) {
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
				.make("static java.util.HashSet variables$init = new java.util.HashSet();",
						ctClass);
		ctClass.addField(ctField);
	}
	
	private void inspectFields(CtClass ctClass) throws ClassNotFoundException {
		for(CtField ctField : ctClass.getDeclaredFields()) {
			if(ctField.hasAnnotation(ExtendedAssertion.class)) {
				extendedAssertions.add(((ExtendedAssertion)ctField.getAnnotation(ExtendedAssertion.class)).value());
			}
		}
	}
	
	private String getStoredAssertions(String fieldName) {
		String assertion = null;
		for(String annotation : extendedAssertions) {
			if(annotation.contains(fieldName)) {
				assertion = (assertion == null) ? annotation : assertion + " && " + annotation;
			}
		}
		return assertion;
	}

	/**
	 * The "Assertion" annotations are interpreted in the given class. The
	 * "AssertionBefore" annotations are also interpreted in the given class.
	 * 
	 * @param ctClass
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 * @throws ClassNotFoundException 
	 */
	private void makeAssertable(CtClass ctClass) throws CannotCompileException,
	NotFoundException, ClassNotFoundException {

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
			throws CannotCompileException, NotFoundException {
		ctBehavior.instrument(new ExprEditor() {

			public void edit(FieldAccess fa) throws CannotCompileException {
				try {
					String template;
					CtField ctField = fa.getField();

					if (fa.isWriter()) {

						if(ctField.hasAnnotation(Assertion.class)) {
							template = "{"
									+ "  $0.%s = $1;"
									+ "  if(!(%s))"
									+ "    throw new RuntimeException(\"The assertion %s is false\");"
									+ "  variables$init.add(\"%s\");"
									+ "}";

							String name = fa.getField().getName();
							String assertion = ((Assertion) ctField
									.getAnnotation(Assertion.class)).value();
							fa.replace(String.format(template, name, assertion,
									assertion, name));
						} else if(!extendedAssertions.isEmpty()){
							template = "{"
									+ "  $0.%s = $1;"
									+ "  if(!(%s))"
									+ "    throw new RuntimeException(\"The assertion %s is false\");"
									+ " }";

							String name = fa.getField().getName();
							String assertion = getStoredAssertions(name);
							if(assertion != null)
								fa.replace(String.format(template, name, assertion,
										assertion));
						}
					} else if (fa.isReader()) {
						template = "{"
								+ "  if(!(variables$init.contains(\"%s\")))"
								+ "    throw new RuntimeException(\"Error: %s was not initialized\");"
								+ "  $_ = $proceed($$);"
								+ "}";
						if(ctField.hasAnnotation(Assertion.class)) {

							String name = fa.getField().getName();
							fa.replace(String.format(template, name, name, name));
						} else {
							
						}

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
	 * @throws NotFoundException
	 * @throws ClassNotFoundException 
	 */
	private void assertBehavior(CtBehavior ctBehavior, CtClass ctClass)
			throws CannotCompileException, NotFoundException, ClassNotFoundException {

		String template = " if(!(%s))"
				+ "    throw new RuntimeException(\"The assertion %s is false\");";
		String assertion = null;
		assertion = checkSuperclass(ctBehavior);
		
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

		if (ctConstructor.hasAnnotation(ExtendedAssertion.class)) {
			assertion = assertion.substring(2);
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
		
		if (!assertion.contains("$<")) {
			originalMethod.insertAfter(String.format(template, assertion, assertion));
		}
		else {
			assertion = assertion.substring(2);
			System.out.println(assertion);
			
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
	 * @throws ClassNotFoundException 
	 */
	private String checkSuperclass(CtBehavior ctBehavior) throws ClassNotFoundException {
		String assertion = null;

		try {
			CtClass nextClass = ctBehavior.getDeclaringClass().getSuperclass();

			if (nextClass != null) {
				assertion = checkSuperclass(nextClass
						.getDeclaredMethod(ctBehavior.getName()));
			}
		} catch (NotFoundException e) {
			// If this exception is thrown, it means it doesn't exist in the
			// superclass, so there's nothing to do
		}

		String value = null;

		if (ctBehavior.hasAnnotation(Assertion.class)) {
			value = ((Assertion) ctBehavior.getAnnotation(Assertion.class)).value();

		} else if (ctBehavior.hasAnnotation(ExtendedAssertion.class)) {

			if(assertion == null)
				assertion = "$<" + ((ExtendedAssertion) ctBehavior.getAnnotation(ExtendedAssertion.class)).value();
			else
				value = ((ExtendedAssertion) ctBehavior.getAnnotation(ExtendedAssertion.class)).value();

		}

		if(value != null) {
			assertion = (assertion == null) ? value : assertion + " && "
					+ value;
		}

		return assertion;
	}
	
	private void inspectArrays(ClassPool pool, CtClass ctClass) 
			throws NotFoundException, CannotCompileException {
		CtClass arrayAdvisor = pool.get("ist.meic.pa.ArrayAdvisor");
		CodeConverter conv = new CodeConverter();
		conv.replaceArrayAccess(arrayAdvisor, new CodeConverter.DefaultArrayAccessReplacementMethodNames());
		ctClass.instrument(conv);
	}
}