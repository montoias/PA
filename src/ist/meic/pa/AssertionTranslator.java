package ist.meic.pa;

import java.io.IOException;

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
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

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
	 * @throws NotFoundException
	 */
	private void makeAssertable(CtClass ctClass) throws CannotCompileException,
			NotFoundException {

		for (CtBehavior ctBehavior : ctClass.getDeclaredBehaviors()) {
			instrumentBehavior(ctBehavior);
			assertBehavior(ctBehavior, ctClass);
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
								+ "  if(!(variables$notInit.contains(\"%s\")))"
								+ "    throw new RuntimeException(\"Error: %s was not initialized\");"
								+ "  $_ = $proceed($$);" + "} ";

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

	/*
	 * private void assertBehavior(CtBehavior ctBehavior) throws
	 * CannotCompileException { final String template = "{" + "  if(!(%s))" +
	 * "    throw new RuntimeException(\"The assertion %s is false\");" + "}";
	 * 
	 * final String ctemplate = "{" + "  if(!(%s))" +
	 * "    throw new RuntimeException(\"The assertion %s is false\");" // +
	 * "  $_ = $proceed($$);" + "}"; final String atemplate = "{" +
	 * "  if(!(%s))" +
	 * "    throw new RuntimeException(\"The assertion %s is false\");" // +
	 * "  $proceed($$);" + "}";
	 * 
	 * ctBehavior.instrument(new ExprEditor() {
	 * 
	 * public void edit(MethodCall mc) throws CannotCompileException {
	 * 
	 * String annotation; try { annotation = checkSuperclass(mc.getMethod()); if
	 * (!annotation.equals("")) { // String before = ""; // String after = "";
	 * // String parse[] = annotation.split(" && "); // for(String s: parse){ //
	 * if(s.contains("$_")){ // after += after.equals("") ? s : " && " + s; // }
	 * // else { // before += before.equals("") ? s : " && " + s; // } // } //
	 * before - mc.replace(String.format(template, before, annotation, after,
	 * annotation)); // after -
	 * mc.getMethod().insertAfter(String.format(template, annotation,
	 * annotation)); } } catch (NotFoundException e) {
	 * System.out.println("Shouldn't happen."); }
	 * 
	 * }
	 * 
	 * public void edit(ConstructorCall cc) throws CannotCompileException {
	 * 
	 * String annotation; try { annotation =
	 * checkSuperclass(cc.getConstructor()); if (!annotation.equals("")) { //
	 * antes cc.replace(String.format(atemplate, annotation, annotation));
	 * cc.getConstructor().insertBefore(String.format(atemplate, annotation,
	 * annotation)); } } catch (NotFoundException e) {
	 * System.out.println("Shouldn't happen."); }
	 * 
	 * }
	 * 
	 * public void edit(NewExpr cc) throws CannotCompileException { String
	 * annotation; try { annotation = checkSuperclass(cc.getConstructor());
	 * 
	 * if (!annotation.equals("")) { //antes cc.replace(String.format(ctemplate,
	 * annotation, annotation));
	 * cc.getConstructor().insertBefore(String.format(ctemplate, annotation,
	 * annotation)); } } catch (NotFoundException e) {
	 * System.out.println("Shouldn't happen."); }
	 * 
	 * }
	 * 
	 * private String checkSuperclass(CtBehavior ctBehavior) { String annotation
	 * = "";
	 * 
	 * try { CtClass nextClass = ctBehavior.getDeclaringClass().getSuperclass();
	 * 
	 * if(nextClass != null) { annotation =
	 * checkSuperclass(nextClass.getDeclaredMethod(ctBehavior.getName())); } }
	 * catch (NotFoundException e) { // If this exception is thrown, it means it
	 * doesn't exist in the superclass, so there's nothing to do }
	 * 
	 * try {
	 * 
	 * if (ctBehavior.hasAnnotation(Assertion.class)) { String value =
	 * ((Assertion) ctBehavior.getAnnotation(Assertion.class)).value();
	 * annotation = annotation.equals("") ? value : annotation + " && " + value;
	 * }
	 * 
	 * } catch (ClassNotFoundException e) { //Not supposed to happen }
	 * 
	 * return annotation; } }); }
	 */

	private void assertBehavior(CtBehavior ctBehavior, CtClass ctClass)
			throws CannotCompileException, NotFoundException {
		if (ctBehavior.hasAnnotation(Assertion.class)) {
			String annotation = checkSuperclass(ctBehavior);

			String before = "";
			String after = "";
			String parse[] = annotation.split(" && ");
			for (String s : parse) {
				if (s.contains("$_")) {
					after += after.equals("") ? s : " && " + s;
				} else {
					before += before.equals("") ? s : " && " + s;
				}
			}

			String annotationReplaced = annotation.replace("$_", "res");
			
			if (ctBehavior.getMethodInfo().isMethod()) {
				CtMethod oldMethod = (CtMethod) ctBehavior;
				CtMethod newMethod = CtNewMethod.copy(oldMethod, oldMethod.getName(), ctClass, null);

				oldMethod.setName(oldMethod.getName() + "$orig");
				
//				System.out.println("{ " 
//						+ oldMethod.getReturnType().getName() +" res = " + oldMethod.getName() + "($$);"
//						+ " if(!(" + annotationReplaced + "))"
//						+ "    throw new RuntimeException(\"The assertion " + annotationReplaced + " is false\");"
//						+ " return res; } ");
				
				if (!oldMethod.getReturnType().getName().equalsIgnoreCase("void")) {
					
					newMethod.setBody("{ " 
							+ oldMethod.getReturnType().getName() +" res = $proceed($$);"
							+ " if(!(" + annotationReplaced + "))"
							+ "    throw new RuntimeException(\"The assertion " + annotationReplaced + " is false\");"
							+ " return res; } ", "this", oldMethod.getName());
					
					ctClass.addMethod(newMethod);
					
				} else {
					
				}
			} else if (ctBehavior.getMethodInfo().isConstructor()) {

			}

			// String beforeBytecode = "{"
			// + "  if(!(" + before + "))"
			// + "    throw new RuntimeException(\"The assertion " + annotation
			// + " is false\");"
			// + "}";
			//
			// String afterBytecode = "{"
			// + "  if(!(" + after + "))"
			// + "    throw new RuntimeException(\"The assertion " + annotation
			// + " is false\");"
			// + "}";
			//
			// if(!before.equals("")) {
			// if(ctBehavior.getMethodInfo().isMethod()) {
			// ctBehavior.insertBefore(beforeBytecode);
			// }
			// else if (ctBehavior.getMethodInfo().isConstructor()) {
			// ((CtConstructor) ctBehavior).insertBeforeBody(beforeBytecode);
			// }
			// }
			// if(!after.equals(""))
			// ctBehavior.insertAfter(afterBytecode);
		}
	}

	private String checkSuperclass(CtBehavior ctBehavior) {
		String annotation = "";

		try {
			CtClass nextClass = ctBehavior.getDeclaringClass().getSuperclass();

			if (nextClass != null) {
				annotation = checkSuperclass(nextClass
						.getDeclaredMethod(ctBehavior.getName()));
			}
		} catch (NotFoundException e) {
			// If this exception is thrown, it means it doesn't exist in the
			// superclass, so there's nothing to do
		}

		try {

			if (ctBehavior.hasAnnotation(Assertion.class)) {
				String value = ((Assertion) ctBehavior
						.getAnnotation(Assertion.class)).value();
				annotation = annotation.equals("") ? value : annotation
						+ " && " + value;
			}

		} catch (ClassNotFoundException e) {
			// Not supposed to happen
		}

		return annotation;
	}
}