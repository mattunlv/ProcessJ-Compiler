package SampleTests.Instrumenter;

public class yieldtest extends Process{


	int x = 5;
	int y = 10;
	int runLabel = 0;

//	String hw = "hello";
//	float f1 = 3.4f;
//	double d1 = 3.4d;
//	long l1 = 34l;

//	public int iret(){
//		return x;
//	}
//
//	public String sret(){
//		return hw;
//	}
//
//	public float fret(){
//		return f1;
//	}
//
//	public double dret(){
//		return d1;
//	}
//
	public long lret(){
		return 0L;
	}


	public void foo() {
		System.out.println("runLabel=" + runLabel);
		switch(runLabel) {
			case 0: break;
			case 1: resume(1);break;
			case 2: resume(2);break;
			case 3: resume(3);break; //doesn't work
		}

		x = x+5;
		System.out.println("Ran case 0 code and yield(1):" + x);

		runLabel=1;
		super.yield();
		label(1);
		
		x = 2;
		System.out.println("Ran case 1 code and yield(2):" + x);
		runLabel=2;
		super.yield();
		label(2);

		x = y + 1;
		System.out.println("Ran case 2 code and yield(3):" + x);
		runLabel=3;
		super.yield();
		label(3);

		x = 100;
		System.out.println("Done yielding 1, 2, 3.");
	}


	

	public static void main(String[] args) {

		yieldtest t = new yieldtest();

		System.out.println("calling t.foo(0) ");
		t.foo();

		System.out.println("back to main --calling t.foo(1)");
		t.foo();	

		System.out.println("back to main --calling t.foo(2)");
		t.foo();	

		System.out.println("back to main --calling t.foo(3)");
		t.foo();	

		System.out.println("main done!");
	}

}
