abstract class Process {
    Object context;
    int runLabel = 0;
    
    public Process(Object context) {
        this.context = context;
    }

    public void schedule() { run(); }

    abstract void run() ;
}

public class test6 {
	int runLabel = 0;

	public void run() {
		
		this.runLabel = 1;

		new Process(this){
			void run(){
				System.out.println("hello " + this.runLabel);
				this.runLabel = 5;
				System.out.println("hello " + this.runLabel);
			}	
			
		}.schedule();

		System.out.println(this.runLabel);

	}

	public static void main(String[] args) {
		test6 o = new test6();
		o.run();
	}
}
