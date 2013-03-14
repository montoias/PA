package ist.meic.pa;

public class TestInheritance extends Test{

	public TestInheritance() {}
	
	@Override
	public int inheritanceTest1(int a){
		return a + 1;
	}

	@Override 
	@Assertion("$_ != 0")
	public int inheritanceTest2(int a){
		return a*a;
	}

	public int inheritanceTest3(int a, int b){
		return a+b;
	}

	@Assertion("($1 != 0) && ($_ > 0)")
	public int inheritanceTest4(int a, int b){
		return a - 1;
	}

	public static void main(String[] args) {
		TestInheritance t = new TestInheritance();

		try {
			t.inheritanceTest1(1000);		//works
		}
		catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}

		try {
			t.inheritanceTest2(1);			//works
			t.inheritanceTest2(0);			//fails
		}
		catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}

		try {
			t.inheritanceTest3(10, 9);		//works
			t.inheritanceTest3(1, 2);		//fails
		}
		catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}

		try {
			t.inheritanceTest4(5, 3);		//works
			t.inheritanceTest4(3, -1);		//fails
			//t.inheritanceTest4(0, 3);		//fails
		}
		catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}

	}
}
