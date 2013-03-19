package ist.meic.pa;

public class YetAnotherTest {

	public static void main(String[] args) {
		Test t1 = null;
		Test t2 = null;
		
		try {
			t2 = new Test();
			t1 = new Test("a");
		} catch(RuntimeException e) {
			System.err.println(e.getMessage());
		}
		
		try{
			t2.setXuu("a");
		} catch (RuntimeException e) {
			System.err.println(e.getMessage());
		}
	}

}