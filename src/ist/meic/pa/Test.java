package ist.meic.pa;

/**
 * TODO: Make multiple tests.
 * Test class where the multiple problems of the project are tested.
 * @author groupXX
 *
 */
public class Test {
	@Assertion("foo>0")
	int foo = 1;
	@Assertion("bar%2==0")
	long bar;
	@Assertion("baz>foo")
	int baz;
	@Assertion("quux.length()>1")
	String quux;
	@Assertion("true")
	int abc;
	
	
	public Test() {}
	
	/**
	 * Since constructors are different entities than methods,
	 * we use it also to test fields. We use overloading for debug purposes.
	 */
	public Test(String test) {
		bar = testAssertions(2);	//works
		baz = 3;					//works
		bar += 2;   				//works
		quux = "foo"; 				//works
		testAssertions(baz);  		//fails
		//baz = 0; 					//fails
	}

	/**
	 * Testing for the variables initialization.
	 */
	private void initializationTest() {
		abc++;					//fails
	}
	
	/**
	 * Testing for field assertion inside methods.
	 */
	private void assertionTest() {
		bar = 2;				//works
		baz = 3;				//works
		bar += 2;   			//works
		quux = "foo"; 			//works
		testAssertions(2); 		//works
		testAssertions(3);  	//fails
		//baz = 0; 				//fails
	}

	/**
	 * Testing for method assertion at entry and exit of the method.
	 * @param x
	 * @return
	 */
	@Assertion("($1 > 0) && ($_%2 == 0)")
	public int testAssertions(int x) {
		return x + 2;
	}

	public static void main(String[] args) {
		Test t = null;
		
		try {
			t = new Test();
			//t = new Test("Testing");
		}
		catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}
		
		try {
			t.initializationTest();
		}
		catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}
		
		try {
			t.assertionTest();
		}
		catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}
		
	}
		
}
