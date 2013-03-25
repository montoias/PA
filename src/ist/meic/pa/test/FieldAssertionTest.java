package ist.meic.pa.test;
import ist.meic.pa.annotations.Assertion;

/**
 * Test class for the field assertions. 
 * Tested for the fields below, in constructors, methods,
 * and also in the field declaration space.
 * Also testing for field initialization.
 * 
 * @author group3
 *
 */
/* TODO: test fields in static methods screw this up*/
public class FieldAssertionTest {
	
	@Assertion("foo>0")
	private int foo = 1;
	
	@Assertion("bar%2==0")
	private static long bar = 0;

	@Assertion("baz>foo")
	public int baz = 5;

	@Assertion("quux.length()>1")
	public static String quux;

	@Assertion("abc > 0")
	static int abc;

	@Assertion("xuu.length() > 1")
	String xuu = "xyz";
	
	@Assertion("true")
	int aaa;
	
	int[] arr = new int[2];
//	int[] arr = {1, 2, 3};

	public FieldAssertionTest() {}

	/**
	 * Since constructors are different entities than methods,
	 * we use it also to test fields. We use overloading for debug purposes.
	 */
	public FieldAssertionTest(String test) {
		testClassFieldsInMethods();
	}
 
	/**
	 * Testing for field assertion inside methods.
	 * Tested for the following qualifiers:
	 * public
	 * private
	 * protected 
	 * package 
	 * with modifier static for all the above
	 */
	void testClassFieldsInMethods() {
		arr[0] = 2;
//		try {
//			int a = arr[0];
//		} catch (RuntimeException e) {
//			System.out.println(e.getMessage());
//		}
		for(int i = 0; i < java.lang.Integer.MAX_VALUE; i++)	//pass!
			(new int[1000])[0] = 3;
		int a = arr[0];
		try {
			bar = 2;					//pass
			bar++; 						//fail
		} catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}
		try {
			foo = 2;					//pass
			foo = 0; 					//fail
		} catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}
		try {
			baz = 3;					//pass	
			baz -= 3;					//fail
		} catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}
		try {
			quux = "foo"; 				//pass
			quux = "!";					//fail;
		} catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}
		try {
			abc = 1; 					//pass
			abc = 0; 					//fail
		} catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}
		try {
			xuu = xuu + "uu"; 			//pass
			xuu = "!"; 					//fail
		} catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}
	}
	
	void testFieldInitialization(){
		try {
			abc = aaa;					//fail;
		} catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}
	}
	

	public static void main(String[] args) {
		FieldAssertionTest t1 = new FieldAssertionTest();
		t1.testClassFieldsInMethods();
		t1.testFieldInitialization();

	}
}
