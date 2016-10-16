package SampleTests;

import ProcessJ.runtime.PJChannel;
import ProcessJ.runtime.PJProcess;

public class AlternativeCDS {
//
//	
//	public static class alternative extends Process {
//
//		Channel<Integer> _pd_intChan;
//		Channel<Integer> _pd_intChan2;
//		int _ld0_x;
//		int _ld1_y;
//
//	    public alternative(Channel<Integer> _pd_intChan, Channel<Integer> _pd_intChan2) {
//	    	this._pd_intChan = _pd_intChan;
//	    	this._pd_intChan2 = _pd_intChan2;
//	    }
//
//		@Override
//		public void run() {
//			switch(this.runLabel) {
//			    case 0: break;
//			    case 1: resume(1); break;
//			    case 2: resume(2); break;
//			    case 3: resume(3); break;
//			    case 4: resume(4); break;
//			    case 5: resume(5); break;
//			    case 6: resume(6); break;
//			    case 7: resume(7); break;
//			    case 8: resume(8); break;
//			    case 9: resume(9); break;
//			    case 10: resume(10); break;
//			    case 11: resume(11); break;
//			}
//			_ld0_x=0;
//			_ld1_y=0;
//			/*Start of Alternative!*/
//			Alt alt = new Alt(_pd_intChan, _pd_intChan2);
//
//			label(0);
//			switch(alt.getReadyIndex()){
//			  case 0:
//			    label(8);
//			    if(_pd_intChan.isReadyToRead(this)) {
//			    	_ld0_x = _pd_intChan.read(this);
//			    	this.runLabel = 9;
//			    	yield(9);
//			    } else {
//			    	setNotReady();
//			    	_pd_intChan.addReader(this);
//			    	this.runLabel = 8;
//			    	yield(8);
//			    }
//			    label(9);
//			    _ld1_y = 10;
//			    break;
//			  case 1:
//			    label(10);
//			    if(_pd_intChan2.isReadyToRead(this)) {
//			    	_ld0_x = _pd_intChan2.read(this);
//			    	this.runLabel = 11;
//			    	yield(11);
//			    } else {
//			    	setNotReady();
//			    	_pd_intChan2.addReader(this);
//			    	this.runLabel = 10;
//			    	yield(10);
//			    }
//			    label(11);
//			    _ld1_y = 20;
//			    break;
//			  default:
//			    System.out.println("Reached default case of switch statement from Alt\n");
//			    this.runLabel = 0;
//			    yield(0);
//			}
//
//			/*End of Alternative!*/;
//	      	terminate();
//		}
//	}
//	
}
