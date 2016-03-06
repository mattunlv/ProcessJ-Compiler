public class test extends Process {


    public static void main(String args[]) {


	new test().run();
    }

    
    public void run() {
	test();
    }

    public void test() {
	Channel<Integer> c1 = new Channel<Integer>();
	Channel<Integer> c2 = new Channel<Integer>();
	
	c1.write(this,42);
	int x1,x2;

	Alt alt = new Alt(c1, c2);
	switch(alt.getReadyIndex()) {
	case 0: 
	    c1.isReadyToRead(this);
	    x1 = c1.read(this);
	    System.out.println("read from c1 :" + x1);
	    break;
	case 1:
	    c2.isReadyToRead(this);
	    x2 = c2.read(this);
	    System.out.println("read from c2 :" + x2);
	    break;
	default:
	    System.out.println("No ready channels in ALT");
	}

	
	

    }



}