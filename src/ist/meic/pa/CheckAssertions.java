package ist.meic.pa;
import javassist.*;

/**
 * TODO: class description
 * @author groupXX
 *
 */
public class CheckAssertions {

	public static void main(String[] args) {
		if(args.length < 1) {
			System.err.println("Usage: java CheckAssertions <class> <args>");
			System.exit(1);
		} else {
			try {
				Translator translator = new AssertionTranslator();
				ClassPool pool = ClassPool.getDefault();
				Loader classLoader = new Loader();
				classLoader.addTranslator(pool, translator);
				
				String[] restArgs = new String[args.length - 1];
				System.arraycopy(args, 1, restArgs, 0, restArgs.length);
				classLoader.run(args[0], restArgs);
			}
			catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CannotCompileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}