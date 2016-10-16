package SampleTests;

public class test {
	public int x = 5;
	int runLabel = 0;

	public void foo() {
		switch(runLabel) {
			case 0: break;
			case 1: resume(1);break;
			case 2: resume(2);break;
			case 3: resume(3);break;
		}

		System.out.println("Ran case 0 code.");

		label(1);
		if (x < 10) {
			System.out.println("x<10...yield(1)");
			x = x+5;	
			yield(1);
		}

		System.out.println("Ran case 1 code and x=" + x);

		label(2);
		if (x <20) {
			System.out.println("x<20...yield(2)");
			x = x+5;
			yield(2);
		}

		System.out.println("Ran case 2 code and x=" + x);

		label(3);
		if (x <30) {
			System.out.println("x<30...yield(3)");
			x = x+5;
			yield(3);
		}

		x = x+5;

		System.out.println("Ran case 3 code and x=" + x);

	}


	public void label(int y){}
	public void resume(int y){}
	public void yield(int y){
		runLabel=y;
		System.out.println("set runlabel to:" + y);
	}


	public static void main(String[] args) {

		test t = new test();
		
		while(t.x < 35) {
			System.out.println("Main calling t.foo.....");
			t.foo();
		}	
		
		System.out.println("Main done!!!!");
	}

}
