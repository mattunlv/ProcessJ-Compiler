package SampleTests;


/*
 * ParBlock
 * - this is to handle channel read in a par block where we need to set
 * value of x from the read.
 * - this is also for other statement blocks or invocations with return.
 * - we wrap everything inside a par block in processes.
 * - the problem we had was trying to set a variable value inside the 
 * anonymous class or lamdas or functions cannot be done as the variable
 * inside them needs to be final (constant).
 * -but since we are already creating fields for all locals, all we are 
 * doing is passing in the context of the container class to the anonymous
 * class so that they can access and set the values.
 *  
 */
abstract class Process {
    Object context;
    
    public Process(Object context) {
        this.context = context;
    }

    public void schedule() { run(); }

    abstract void run() ;
}

/*
  proc void A() {

    int x,y;
    par {
      x = f(3);
      if (x > 5)
        y = 7;
      else
        x = 2;
    }
  }
*/

class A extends Process {
    int _$x = 0;
    int _$y = 0;
    
    int f(int x) { 
    	return x + 10; 
    }

    public A(Object context) {
        super(context);
    }
  
    //creating the new processes are specifically for par blocks
    public void run() {
    	
        new Process(this){
            void run() {
                A myContext = (A)context;  // <--- must be the name of the class in which _$x lives!
                myContext._$x = f(4);
            }
        }.schedule();

        new Process(this){
            void run() {
                A myContext = (A)context;

                if (myContext._$x > 5)
                    myContext._$y = 7;
                else
                    myContext._$x= 2;
            }
        }.schedule();

        System.out.println("x: " + _$x);
        System.out.println("y: " + _$y);
    }
}

class test56 {


    public void foo() {
        new A(this).schedule();


    }

    public static void main(String args[]) {
        new test56().foo();
    }

}
