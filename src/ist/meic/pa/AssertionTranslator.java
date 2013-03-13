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
			makeAssertable(ctClass);

			for (CtField field : ctClass.getFields()) {
				instrumentField(field);
			}
		} catch (CannotCompileException e) {
			e.printStackTrace();
		}
	}

	private void instrumentField(CtField field) {
		

	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {

	}

	private void makeAssertable(CtClass ctClass) throws CannotCompileException {
		final String template = "{"
				+ "  $0.%s = $1;"
				+ "  if(!(%s))"
				+ "    throw new RuntimeException(\"The assertion %s is false\");"
				+ "}";

		for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			instrumentMethod(template, ctMethod);
			assertMethod(ctMethod);
		}
		for (CtConstructor ctMethod : ctClass.getDeclaredConstructors()) {
			instrumentMethod(template, ctMethod);
		}

	}

	private void instrumentMethod(final String template, CtMethod ctMethod)
			throws CannotCompileException {
		ctMethod.instrument(myExpressionEditor(template));
	}
	
	private void instrumentMethod(final String template,
			CtConstructor ctConstructor) throws CannotCompileException {
		ctConstructor.instrument(myExpressionEditor(template));
	}

	private ExprEditor myExpressionEditor(final String template) {
		return new ExprEditor() {
			public void edit(FieldAccess fa) throws CannotCompileException {
				try {
					CtField ctField = fa.getField();
					if (fa.isWriter() && ctField.hasAnnotation(Assertion.class)) {
						String name = fa.getFieldName();
						String annotation = ((Assertion) ctField
								.getAnnotation(Assertion.class)).value();
						fa.replace(String.format(template, name, annotation,
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
		};
	}

	private void assertMethod(CtMethod ctMethod) throws CannotCompileException {
		final String template = 
				  "{"
				+ "  $_ = $proceed($$);"
				+ "  if(!(%s))"
				+ "    throw new RuntimeException(\"The assertion %s is false\");"
				+ "}";

		ctMethod.instrument(new ExprEditor() {
			public void edit(MethodCall mc) throws CannotCompileException {
				try {
					if (mc.getMethod().hasAnnotation(Assertion.class)) {
						String annotation = ((Assertion) mc.getMethod().getAnnotation(Assertion.class)).value();
						mc.replace(String.format(template, annotation, annotation));
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