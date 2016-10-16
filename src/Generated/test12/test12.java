package Generated.test12;
import java.util.*;
import ProcessJ.runtime.*;

public class test12 {
	public static class f extends PJProcess {
		PJChannel<Integer> _pd$in;
		int _pd$a;
		PJTimer _ld0$t;
		int _ld1$v;
		boolean _ld2$bTemp0;
		int _ld3$chosen;
		PJAlt _ld4$alt;
		boolean _ld5$bTemp0;
		int _ld6$chosen;
		PJAlt _ld7$alt;

	    public f(PJChannel<Integer> _pd$in, int _pd$a) {
	    	this._pd$a = _pd$a;
	    	this._pd$in = _pd$in;
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
			}
			_ld4$alt = new PJAlt(3, this);	
			_ld0$t = new PJTimer(this, 100);

			Object[] guards1 = {_pd$in, _ld0$t, PJAlt.SKIP_GUARD};
			_ld2$bTemp0 = (_pd$a > 0);
			boolean[] boolGuards1 = {_ld2$bTemp0, true, true};	

			boolean bRet1 = _ld4$alt.setGuards(boolGuards1, guards1);

			if (!bRet1) {
				System.out.println("RuntimeException: One of the boolean guards needs to be true!!");
				System.exit(1);
			}

			label(2);	

			_ld3$chosen = _ld4$alt.getReadyGuardIndex();
			switch(_ld3$chosen) {
				case 0: 
				  	_ld1$v = _pd$in.read(this);
				  	;
					break;
				case 1: 
					break;
				case 2: 
					break;
				case -1: 
				  		
				  	if (!_ld0$t.started) {
				  		try {
				  			_ld0$t.start();
				  		} catch (InterruptedException e) {
				  			System.out.println("InterruptedException!");
				  		}
				  	}
				  	;
					break;
			}


			if (_ld3$chosen == -1) {
				this.runLabel = 2;
				yield();	
			} else {
				if (_ld0$t.started && !_ld0$t.expired) {
					_ld0$t.kill();
				}	

				this.runLabel = 3;
				yield();
			}

			label(3);
			_ld7$alt = new PJAlt(3, this);	
			_ld0$t = new PJTimer(this, 100);

			Object[] guards2 = {_pd$in, _ld0$t, PJAlt.SKIP_GUARD};
			_ld5$bTemp0 = (_pd$a > 0);
			boolean[] boolGuards2 = {_ld5$bTemp0, true, true};	

			boolean bRet2 = _ld7$alt.setGuards(boolGuards2, guards2);

			if (!bRet2) {
				System.out.println("RuntimeException: One of the boolean guards needs to be true!!");
				System.exit(1);
			}

			label(5);	

			_ld6$chosen = _ld7$alt.getReadyGuardIndex();
			switch(_ld6$chosen) {
				case 0: 
				  	_ld1$v = _pd$in.read(this);
				  	;
					break;
				case 1: 
					break;
				case 2: 
					break;
				case -1: 
				  		
				  	if (!_ld0$t.started) {
				  		try {
				  			_ld0$t.start();
				  		} catch (InterruptedException e) {
				  			System.out.println("InterruptedException!");
				  		}
				  	}
				  	;
					break;
			}


			if (_ld6$chosen == -1) {
				this.runLabel = 5;
				yield();	
			} else {
				if (_ld0$t.started && !_ld0$t.expired) {
					_ld0$t.kill();
				}	

				this.runLabel = 6;
				yield();
			}

			label(6);
			terminate();
		}
	}


	public static boolean getTrue() {
		return true;
	}
}