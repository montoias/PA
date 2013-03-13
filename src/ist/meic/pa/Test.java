package ist.meic.pa;

public class Test {
	@Assertion("foo>0")
	int foo = 1;
	@Assertion("bar%2==0")
	long bar;
	@Assertion("baz>foo")
	int baz;
	@Assertion("quux.length()>1")
	String quux;
	
	public Test() {
		myTest();
	}

	private void myTest() {
		bar++;
		bar = 2;
		baz = 3;
		bar += 2;
		quux = "foo";
//		bar++;
		testAssertions(2);
		testAssertions(4);
//		testAssertions(0); // suposto falhar
	}

	@Assertion("($1 > 0) && ($_%2 == 0)")
	public int testAssertions(int x) {
		return x + 2;
	}

	public static void main(String[] args) {
		try {
			Test t = new Test();
//			t.myTest();
		} catch (RuntimeException r) {
			System.err.println(r.getMessage());
		}
	}
}
