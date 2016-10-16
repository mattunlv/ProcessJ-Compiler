package Generated.simpleChannel;
import java.util.*;
import ProcessJ.runtime.*;

public class simpleChannel {
	public static class main extends PJProcess {
		String[] _pd$args;
		PJOne2OneChannel<Integer> _ld0$c;
		int _ld1$x;

	    public main(String[] _pd$args) {
	    	this._pd$args = _pd$args;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 3: resume(3); break;
			}
			_ld0$c = new PJOne2OneChannel<Integer>();
			while( getTrue() ) {
			  final PJPar par1 = new PJPar(2, this);

			  new PJProcess(){
			  	@Override
			  	public synchronized void run() {
			  		switch(this.runLabel) {
			  			case 0: break;
			  		    case 1: resume(1); break;
			  		    case 2: resume(2); break;
			  		}
			  		label(1);
			  		if(_ld0$c.isReadyToRead(this)) {
			  			_ld1$x = _ld0$c.read(this);
			  			this.runLabel = 2;
			  			yield();
			  		} else {
			  			setNotReady();
			  			_ld0$c.addReader(this);
			  			this.runLabel = 1;
			  			yield();
			  		}
			  		label(2);
			        terminate();
			  	}

			  	@Override
			  	public void finalize() {
			  		par1.decrement();	
			  	}
			  }.schedule();

			  new PJProcess(){
			  	@Override
			  	public synchronized void run() {
			  		switch(this.runLabel) {
			  			case 0: break;
			  		    case 4: resume(4); break;
			  		    case 5: resume(5); break;
			  		}

			  		label(4);
			  		if (_ld0$c.isReadyToWrite()) {
			  			_ld0$c.write(this, 10);
			  			this.runLabel = 5;
			  			yield();
			  		} else {
			  			setNotReady();
			  			this.runLabel = 4;
			  			yield();
			  		}
			  		label(5);
			        	terminate();
			  	}

			  	@Override
			  	public void finalize() {
			  		par1.decrement();	
			  	}
			  }.schedule();

			  setNotReady();
			  this.runLabel = 3;
			  yield();
			  label(3);
			};
		}
	}

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();

		PJProcess.scheduler = scheduler;
		System.out.println("Added _main process to scheduler...");

		(new simpleChannel.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
	public static boolean getTrue() {
		return true;
	}
}