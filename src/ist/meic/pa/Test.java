package ist.meic.pa;

/**
 * TODO: Make multiple tests.
 * Test class where the multiple problems of the project are tested.
 * @author groupXX
 *
 */
public class Test{
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
		bar = methodAssertions(2);	//works
		baz = 3;					//works
		bar += 2;   				//works
		quux = "foo"; 				//works
		methodAssertions(baz);  	//fails
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
		methodAssertions(2); 	//works
		methodAssertions(3);  //fails
		//baz = 0; 				//fails
	}

	@Assertion("($1>=0) && ($_%2 == 0)")
	public int methodAssertions(int x) {
		return x + 2;
	}
	
	/*
	 * Inheritance Tests
	 * 
	 */	
	public int inheritanceTest1(int a){
		return a + 1;
	}

	public int inheritanceTest2(int a){
		return a+a;
	}
	
	@Assertion("($1 > $2) && ($_ < 20)")
	public int inheritanceTest3(int a, int b){
		return 0;
	}
	
	@Assertion("($1 + $2) != 2")
	public int inheritanceTest4(int a, int b){
		return b;
	}

	public static void main(String[] args) {
		Test t1 = null;
		Test t2 = null;

		try {
			t1 = new Test();
			t2 = new Test("Testing");
		}
		catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}

		try {
			t1.initializationTest();
		}
		catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}

		try {
			t1.assertionTest();
		}
		catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}

	}

}
