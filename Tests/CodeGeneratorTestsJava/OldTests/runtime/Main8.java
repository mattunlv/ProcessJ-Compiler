public class Main8 {
    static final Scheduler scheduler = new Scheduler();

    private abstract class _p1 extends Process {
	
	public int g() {
	    // Define paramters 
	    Channel<Integer> param$c;   // to avoid name clashes it should be named param$c
	    int result;
	    // Get activation
	    Activation _activation = getActivation();
	    // Get run label 
	    runLabel = _activation.getRunLabel();       
	    
	    // Re-establish parameters
	    param$c = (Channel<Integer>)_activation.getLocal(0);
	    result = (_activation.getLocal(1) == null ? 0 : (int)_activation.getLocal(1));
	    
	    switch (runLabel) {
	    case 0:
		if (param$c.isReadyToRead(this)) {
		    result = param$c.read(this);
		} else {
		    setNotReady();
		    yield(new Activation(new Object[] { param$c, new Integer(result) }, 2), 0);
		    return 0;
		}
		terminate();
		return result;
	    }
	    terminate();
	    return 0;
	}
	
	public _p1(Channel<Integer> c) {
	    addActivationFirst(new Activation(new Object[]{ c }, 3)); // c & a and tempg1
	}
	
	public void run() {
	    p1();
	}
	
	// Original name: p1
	// with the correct parameter interface: Channel<Integer> c
	void p1() {	    
	    // Define parameters
	    Channel<Integer> c; // should be param$c
	    // Get activation
	    Activation _activation = getActivation();
	    // Get runlabel
	    runLabel = _activation.getRunLabel();       	  
	    
	    // Re-establish parameters
	    c = (Channel<Integer>)_activation.getLocal(0);
	    
	    int a;
	    // Re-establish local (this is done after any declaration!)
	    switch (runLabel) {
	    case 0:
		a = (_activation.getLocal(1) == null ? 72 : (Integer)_activation.getLocal(1));
		// activation record for call to g!
		addActivationFirst(new Activation(new Object[] { c }, 2));
	    case 1:
		a = (_activation.getLocal(1) == null ? 72 : (Integer)_activation.getLocal(1));
		// if we arrive at 2 from 1 we will have a new activation for g at the head of the queue
		// if we arrive at 2 from the switch the activation will aleady be at the head of the queue
		a = g();
		if (yielded) {
		    // if we yielded make an activation and add it to the head of the queue
		    //addActivationFirst(new Activation("p1 (on yield)", new Object[] { c, new Integer(38) }, 2, 2));	       
		    yield(new Activation(new Object[] { c, new Integer(a) }, 2), 1);
		    return;
		}
		terminate();
	    }
	    terminate();
	}
    }

    private abstract class _p2 extends Process {
	public _p2(Channel<Integer> c) {
	    addActivationFirst(new Activation(new Object[] { c } , 1));
	}
	
	public void run() {
	    p2();
	}

	void p2() {
	    Activation _activation = getActivation();
	    runLabel = _activation.getRunLabel();

	    Channel<Integer> c = (Channel<Integer>)_activation.getLocal(0);
	    
	    switch (runLabel) {
	    case 0:
		if (c.isReadyToWrite()) {	          
		    c.write(this, 42);
		    yield(new Activation(new Object[] { c } , 1), 1);
                    return;
		} else {
		    setNotReady();
		    yield(new Activation(new Object[] { c } , 1), 0);		    
		    return;
		}
	    case 1:
		terminate();
		return;
	    }
	    terminate();
	}
    }

    /*
      proc void main(int n) {      
        par for (int i=0; i<n; i++) {
          chan<int> c;  
          par {
	    p1(c.read);
	    p2(c.write);
          }
        }
      }
     */
    private abstract class _main extends Process {
	public _main(int n) {
	    addActivationFirst(new Activation(new Object[]{ new Integer(n) }, 2));
	}
	public void run() {
	    main();
	}
	public void main() {   
	    Activation _activation = getActivation();
	    runLabel = _activation.getRunLabel();
	    
	    int param$n;
	    param$n = (_activation.getLocal(0) == null ? 0 : (Integer) _activation.getLocal(0));
	    
	    Channel<Integer> c;
	    // restore locals
	    c = (_activation.getLocal(1) == null ? null :  (Channel<Integer>)_activation.getLocal(1)); // no need for null checking - only for primitives
	    
	    switch (runLabel) {
	    case 0:
		for (int i =0; i<param$n; i++) {
		    c = new Channel<Integer>(); // hack
		    final Par _par1 = new Par(2, this);
		    
		    // this assure that this process will not get scheduled until it
		    // is marked ready by the par block!
		    setNotReady(); 
		    
		    // make the new processes and add them to the runqueue	       
		    Process p1 = new _p1(c) {
			    public void finalize() {
				_par1.decrement();
			    }
			};
		    scheduler.insert(p1);
		    Process p2 = new _p2(c) {
			    public void finalize() {
				_par1.decrement();
			    }
			};
		    scheduler.insert(p2);
		}
		// then suspend yourself to run the processes in the par!		
		yield(new Activation(new Object[] { new Integer(param$n), c }, 2 ), 1);
		return;
	    case 1:
		terminate();
		return;
	    }
	}	
    }

    public void run(int x) {
	scheduler.insert(new _main(x){}); 
	System.out.println("ready to run!");
	scheduler.run();
    }


    public static void main(String args[]) {
	new Main8().run(Integer.parseInt(args[0]));
    }
}