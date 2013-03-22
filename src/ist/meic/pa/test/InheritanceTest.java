package ist.meic.pa.test;

import ist.meic.pa.annotations.Assertion;

public class InheritanceTest extends MethodAssertionTest{

	public InheritanceTest() {}
	
	@Assertion("$1.length() > 0")
	public InheritanceTest(String test) {
		super(test);
	}
	
	@Override
	public int inheritanceTest1(int a){
		return a + 1;
	}

	@Override 
	@Assertion("($1 != 1) && ($_ != 0)")
	public int inheritanceTest2(int a){
		return a*a;
	}

	public int inheritanceTest3(int a, int b){
		return a+b;
	}

	@Assertion("($1 != 1) && ($_ > 0)")
	public int inheritanceTest4(int a, int b){
		return a - 1;
	}

	public static void main(String[] args) {
		InheritanceTest t = null;
		
		try {
			t = new InheritanceTest("Testing");		//FIXME: fail
		}
		catch (RuntimeException r) {
			t = new InheritanceTest("Test");		//pass
			System.err.println(r.getMessage());
		}
		
		try {
			t.inheritanceTest1(1000);				//pass
		}
		catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}

		try {
			t.inheritanceTest2(2);					//pass
			t.inheritanceTest2(0);					//fail
		}
		catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}

		/* FIXME : failing
		try {
			t.inheritanceTest3(10, 9);				//pass
			t.inheritanceTest3(1, 2);				//fail
		}
		catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}*/

		try {
			t.inheritanceTest4(5, 3);				//pass
			t.inheritanceTest4(3, -1);				//fail
		}
		catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}
		try {
			t.inheritanceTest4(0, 3);				//fail
		}
		catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}

	}
}
