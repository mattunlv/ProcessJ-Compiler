package Generated.Alternative;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class Alternative { 
	public static class alternative extends PJProcess {
		PJChannel<Integer> _pd$intChan;
		PJChannel<Integer> _pd$intChan2;
		int _ld0$x;
		int _ld1$y;
		PJTimer _ld2$t;
		int _ld3$chosen;
		PJAlt _ld4$alt;

	    public alternative(PJChannel<Integer> _pd$intChan, PJChannel<Integer> _pd$intChan2) {
	    	this._pd$intChan = _pd$intChan;
	    	this._pd$intChan2 = _pd$intChan2;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			    case 3: resume(3); break;
			    case 4: resume(4); break;
			}
			_ld0$x = 0;;
			_ld1$y = 0;;
			_ld4$alt = new PJAlt(1, this);	
			Object[] guards = {_pd$intChan};
			boolean[] boolGuards = {true};	

			boolean bRet = _ld4$alt.setGuards(boolGuards, guards);

			if (!bRet) {
				System.out.println("RuntimeException: One of the boolean guards needs to be true!!");
				System.exit(1);
			}

			label(3);	

			_ld3$chosen = _ld4$alt.getReadyGuardIndex();
			switch(_ld3$chosen) {
				case 0: 
				  	_ld0$x = _pd$intChan.readPreRendezvous(this);
				  	label(1);
				  	if(_pd$intChan.isReadyToRead(this)) {
				  		_ld1$y = _pd$intChan.read(this);
				  		this.runLabel = 2;
				  		yield();
				  	} else {
				  		setNotReady();
				  		_pd$intChan.addReader(this);
				  		this.runLabel = 1;
				  		yield();
				  	}
				  	label(2);
				  	_pd$intChan.readPostRendezvous(this);
				  	;
				  	_ld1$y = (_ld0$x + _ld1$y);
				  	_ld0$x++;
					break;
			}


			if (_ld3$chosen == -1) {
				this.runLabel = 3;
				yield();	
			} else {
				this.runLabel = 4;
				yield();
			}

			label(4);;
			terminate();
			return;
		}
	}


	public static class main extends PJProcess {
		String[] _pd$args;
		PJOne2OneChannel<Integer> _ld0$c1;
		PJOne2OneChannel<Integer> _ld1$c2;

	    public main(String[] _pd$args) {
	    	this._pd$args = _pd$args;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			_ld0$c1 = new PJOne2OneChannel<Integer>();;
			_ld1$c2 = new PJOne2OneChannel<Integer>();;
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new Alternative.alternative( _ld0$c1, _ld1$c2 ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 2;
			yield();
			label(2);
			terminate();
		}
	}

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();

		PJProcess.scheduler = scheduler;
		System.out.println("Added _main process to scheduler...");

		(new Alternative.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
	public static boolean getTrue() {
		return true;
	}
}