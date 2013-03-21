package ist.meic.pa.test;

import ist.meic.pa.annotations.*;

import java.util.ArrayList;

public class TestMethodArguments {

	public TestMethodArguments() {
		// TODO Auto-generated constructor stub
	}
	
	@Assertion("$1.size() > $_.size()")
	public ArrayList<String> test1(ArrayList<String> a) {
		a.add("a");
		return a;
	}

	@Assertion("$1.size() > $_.size()")
	@AssertionBefore("$1.size()>0")
	public ArrayList<String> test2(ArrayList<String> a) {
		a.add("a");
		a = new ArrayList<String>();
		return a;
	}

	public static void main(String[] args) {
		
		try {
			ArrayList <String> a = new ArrayList<String>();
			TestMethodArguments test = new TestMethodArguments();
			test.test2(a);
		} catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}
		
		/*TestMethodArguments test = new TestMethodArguments();
		ArrayList<String> array = new ArrayList<String>();
		array.add("data");
		array.add("someMoreData");

		try {
			test.test1(array); // fail
		} catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}

		try {
			test.test2(array); // pass
		} catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}*/

	}
}
