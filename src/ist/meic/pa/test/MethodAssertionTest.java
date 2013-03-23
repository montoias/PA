package ist.meic.pa.test;

import ist.meic.pa.annotations.*;

import java.util.ArrayList;

/**
 * Test class for the method assertions.
 * Also serves as a base class for the inheritance tests.
 * Tested for the following qualifiers:
 * - public
 * - private
 * - protected
 * - package
 * with the static modifier for each one of the previous cases.
 * 
 * @author group3
 *
 */
public class MethodAssertionTest {

	private String xuu = "!";

	public MethodAssertionTest() {}
	
	@ExtendedAssertion("$1.length() < 5")
	public MethodAssertionTest(String test) {}

	@Assertion("$1.size() > $_.size()")
	public ArrayList<String> test1(ArrayList<String> a) {
		a.add("a");
		return a;
	}

	@Assertion("($1.size()>0) && ($1.size() > $_.size())")
	private ArrayList<String> test2(ArrayList<String> a, String s) {
		a.remove(s);
		a = new ArrayList<String>();
		return a;
	}
	
	@Assertion("$1.length() > xuu.length()")
	protected void test3(String str) {
		xuu = str;
	}
	
	@Assertion("($1>=0) && ($_%2 == 0)")
	int test4(int x) {
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
		
		
		MethodAssertionTest test = new MethodAssertionTest();
		ArrayList<String> array = new ArrayList<String>();
		array.add("data");
		

		try {
			test.test1(array); 						// will always fail, no matter what
		} catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}

		try {
			test.test2(array, "a"); 				// pass
			test.test2(array, "data"); 				// fail
		} catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}
		
		try {
			test.test3("aa"); 						// will always fail
		} catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}
		
		try {
			test.test4(0); 							// pass
			test.test4(1); 							// fail
		} catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}

	}
}
