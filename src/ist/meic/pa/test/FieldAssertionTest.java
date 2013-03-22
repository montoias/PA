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
	public long bar;

	@Assertion("baz>foo")
	private static int baz;

	@Assertion("quux.length()>1")
	public static String quux;

	@Assertion("abc > 0")
	static int abc;

	@Assertion("xuu.length() > 1")
	String xuu = "xyz";
	
	@Assertion("true")
	int aaa;

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
	 */
	void testClassFieldsInMethods() {
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
