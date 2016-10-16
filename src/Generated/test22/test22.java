package Generated.test22;
import java.util.*;
import ProcessJ.runtime.*;

public class test22 {
	public static class f extends PJProcess {
		int _pd$x;

	    public f(int _pd$x) {
	    	this._pd$x = _pd$x;
	    }

		@Override
		public synchronized void run() {
			terminate();
		}
	}


	public static class g extends PJProcess {
		int _pd$y;

	    public g(int _pd$y) {
	    	this._pd$y = _pd$y;
	    }

		@Override
		public synchronized void run() {
			terminate();
		}
	}


	public static class main extends PJProcess {

	    public main() {
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			final PJPar par1 = new PJPar(2, this);

			(new test22.f( 8 ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new test22.g( 9 ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 1;
			yield();
			label(1);
			terminate();
		}
	}

}