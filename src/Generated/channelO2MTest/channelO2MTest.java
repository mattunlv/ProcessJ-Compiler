package Generated.channelO2MTest;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class channelO2MTest { 
	public static class writer extends PJProcess {

		PJChannel<Integer> _pd$chanW;
		int _ld0$x;

	    public writer(PJChannel<Integer> _pd$chanW) {
	    	this._pd$chanW = _pd$chanW;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			_ld0$x=2;

			label(1);
			if (_pd$chanW.isReadyToWrite()) {
				_pd$chanW.write(this, _ld0$x);
				this.runLabel = 2;
				yield();
			} else {
				setNotReady();
				this.runLabel = 1;
				yield();
			}

			label(2);
			_ld0$x++;
			terminate();
		}
	}


	public static class reader1 extends PJProcess {

		PJChannel<Integer> _pd$chanR;
		int _ld0$x;

	    public reader1(PJChannel<Integer> _pd$chanR) {
	    	this._pd$chanR = _pd$chanR;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			label(1);
			if(_pd$chanR.isReadyToRead(this)) {
				_ld0$x = _pd$chanR.read(this);
				this.runLabel = 2;
				yield();
			} else {
				setNotReady();
				_pd$chanR.addReader(this);
				this.runLabel = 1;
				yield();
			}
			label(2)
	;
			terminate();
		}
	}


	public static class reader2 extends PJProcess {

		PJChannel<Integer> _pd$chanR;
		PJOne2OneChannel<Integer> _ld0$anotherChannel;
		int _ld1$y;

	    public reader2(PJChannel<Integer> _pd$chanR) {
	    	this._pd$chanR = _pd$chanR;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			_ld0$anotherChannel = new PJOne2OneChannel<Integer>();
			label(1);
			if(_pd$chanR.isReadyToRead(this)) {
				_ld1$y = _pd$chanR.read(this);
				this.runLabel = 2;
				yield();
			} else {
				setNotReady();
				_pd$chanR.addReader(this);
				this.runLabel = 1;
				yield();
			}
			label(2)
	;
			terminate();
		}
	}


	public static class main extends PJProcess {

		String[] _pd$args;
		int _ld0$x;
		int _ld1$y;
		int _ld2$z;
		PJMany2ManyChannel<Integer> _ld3$intChan;

	    public main(String[] _pd$args) {
	    	this._pd$args = _pd$args;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			    case 3: resume(3); break;
			    case 4: resume(4); break;
			    case 5: resume(5); break;
			    case 6: resume(6); break;
			    case 7: resume(7); break;
			    case 8: resume(8); break;
			}
			_ld1$y=5;
			_ld2$z=_ld1$y;
			_ld3$intChan = new PJMany2ManyChannel<Integer>();
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new channelO2MTest.writer( _ld3$intChan ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 2;
			yield();
			label(2);
			label(3);	
			final PJPar par2 = new PJPar(1, this);

			(new channelO2MTest.reader1( _ld3$intChan ){
			  public void finalize() {
			    par2.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 4;
			yield();
			label(4);
			label(5);	
			final PJPar par3 = new PJPar(1, this);

			(new channelO2MTest.writer( _ld3$intChan ){
			  public void finalize() {
			    par3.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 6;
			yield();
			label(6);
			label(7);	
			final PJPar par4 = new PJPar(1, this);

			(new channelO2MTest.reader2( _ld3$intChan ){
			  public void finalize() {
			    par4.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 8;
			yield();
			label(8);
			terminate();
		}
	}

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();
		PJProcess.scheduler = scheduler;
		System.out.println("Added _main process to scheduler...");

		(new channelO2MTest.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
}