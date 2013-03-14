package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

/**
 * TODO: @miguel a nice description for the class is needed.
 * 
 * @author groupXX
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
	 * The "Assertion" annotations are now interpreted in the given class.
	 * 
	 * @param ctClass
	 * @throws CannotCompileException
	 */
	private void makeAssertable(CtClass ctClass) throws CannotCompileException {

		for (CtBehavior ctBehavior : ctClass.getDeclaredBehaviors()) {
			instrumentBehavior(ctBehavior);
			assertBehavior(ctBehavior);
		}
	}

	/**
	 * TODO: Rethink name. TODO: Check field initialization upon function entry.
	 * TODO: Check if reader exception is correct. Returns an ExprEditor with a
	 * given template for Assertion annotations. It has both cases when the
	 * Field Access is a read, or a write. The ExprEditor is later used to
	 * instrument a method/constructor.
	 * 
	 * @return
	 */
	/**
	 * TODO: Rethink name; AssertMethodFields perhaps?
	 * 
	 * @param ctBehavior
	 * @throws CannotCompileException
	 */
	private void instrumentBehavior(CtBehavior ctBehavior)
			throws CannotCompileException {
		ctBehavior.instrument(new ExprEditor() {

			public void edit(FieldAccess fa) throws CannotCompileException {
				try {
					final String template;
					CtField ctField = fa.getField();
					if (fa.isWriter() && ctField.hasAnnotation(Assertion.class)) {
						template = "  {"
								+ "  $0.%s = $1;"
								+ "  if(!(%s))"
								+ "    throw new RuntimeException(\"The assertion %s is false\");"
								+ "  variables$notInit.add(\"%s\");" + "} ";

						String name = fa.getField().getName();
						String annotation = ((Assertion) ctField
								.getAnnotation(Assertion.class)).value();
						fa.replace(String.format(template, name, annotation,
								annotation, name));
					} else if (fa.isReader()
							&& ctField.hasAnnotation(Assertion.class)) {
						template = "  {"
								+ "  $_ = $proceed($$);"
								+ "  if(!(variables$notInit.contains(\"%s\")))"
								+ "    throw new RuntimeException(\"Error: %s was not initialized\");"
								+ "} ";

						String name = fa.getField().getName();
						fa.replace(String.format(template, name, name, name));
					}

				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * TODO: Check method assertions upon function entry. TODO: Assert
	 * constructors? Instruments the given method to interpret the Assertion
	 * annotations.
	 * 
	 * @param ctBehavior
	 * @throws CannotCompileException
	 */

	private void assertBehavior(CtBehavior ctBehavior) throws CannotCompileException {
		final String template = "{"
				+ "  $_ = $proceed($$);"
				+ "  if(!(%s))"
				+ "    throw new RuntimeException(\"The assertion %s is false\");"
				+ "}";

		ctBehavior.instrument(new ExprEditor() {
			public void edit(MethodCall mc) throws CannotCompileException {
				try {
					if (mc.getMethod().hasAnnotation(Assertion.class)) {
						String annotation = ((Assertion) mc.getMethod()
								.getAnnotation(Assertion.class)).value();
						mc.replace(String.format(template, annotation,
								annotation));
					}
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}