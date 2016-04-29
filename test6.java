class A {}
class B extends A{}

public class test6 {

	public void foo(B b) {

	}

	public void bar() {
		A a = null;
		foo(a);

	}


}
