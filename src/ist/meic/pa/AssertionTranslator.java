package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

public class AssertionTranslator implements Translator {

	@Override
	public void onLoad(ClassPool pool, String className)
			throws NotFoundException, CannotCompileException {
		CtClass ctClass = pool.get(className);
		try {
			addField(ctClass);
			makeAssertable(ctClass);
		} catch (CannotCompileException e) {
			e.printStackTrace();
		}
	}

	private void addField(CtClass ctClass) throws CannotCompileException {
		CtField ctField = CtField
				.make("java.util.HashSet variables$notInit = new java.util.HashSet();",
						ctClass);
		ctClass.addField(ctField);
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {

	}

	private void makeAssertable(CtClass ctClass) throws CannotCompileException {

		for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			instrumentMethod(ctMethod);
			assertMethod(ctMethod);
		}
		for (CtConstructor ctMethod : ctClass.getDeclaredConstructors()) {
			instrumentConstructor(ctMethod);
		}

	}

	private void instrumentMethod(CtMethod ctMethod)
			throws CannotCompileException {
		ctMethod.instrument(myExpressionEditor());
	}

	private void instrumentConstructor(CtConstructor ctConstructor)
			throws CannotCompileException {
		ctConstructor.instrument(myExpressionEditor());
	}
	

	private ExprEditor myExpressionEditor() {
		return new ExprEditor() {
			public void edit(FieldAccess fa) throws CannotCompileException {
				try {
					final String template;
					System.out.println(fa.getField().getName());
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
						System.out.println(name);
						fa.replace(String.format(template, name, annotation,
								annotation, name));
					} else if (fa.isReader()
							&& ctField.hasAnnotation(Assertion.class)) {
						template = "  {"
								+ "  $_ = $proceed($$);"
								+ "  System.out.println(\"array\" + %s);"
								+ "  if(!(variables$notInit.contains(\"%s\")))"
								+ "    throw new RuntimeException(\"The assertion %s is false\");"
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
		};
	}

	private void assertMethod(CtMethod ctMethod) throws CannotCompileException {
		final String template = "{"
				+ "  $_ = $proceed($$);"
				+ "  if(!(%s))"
				+ "    throw new RuntimeException(\"The assertion %s is false\");"
				+ "}";

		ctMethod.instrument(new ExprEditor() {
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