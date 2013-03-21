package ist.meic.pa;

import java.util.ArrayList;

public class TestMethodArguments {

	@Assertion("$1.size() > $_.size()")
	public ArrayList<String> test1(ArrayList<String> a) {
		a.add("a");
		return a;
	}

	@Assertion("$1.size() > $_.size()")
	public ArrayList<String> test2(ArrayList<String> a) {
		a.add("a");
		a = new ArrayList<String>();
		return a;
	}

	public static void main(String[] args) {
		TestMethodArguments test = new TestMethodArguments();
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
		}

	}
}
