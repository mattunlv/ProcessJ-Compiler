package Generated.complexRecurseTest;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class complexRecurseTest { 
	public static class a extends PJProcess {

		int _pd$n;

	    public a(int _pd$n) {
	    	this._pd$n = _pd$n;
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
			System.out.println( ("a:" + _pd$n) );
			if( (_pd$n == 0) ) {
			  terminate();
			  return;
			};
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new complexRecurseTest.b( (_pd$n - 1) ){
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

			(new complexRecurseTest.p( (_pd$n - 1) ){
			  public void finalize() {
			    par2.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 4;
			yield();
			label(4);
			terminate();
		}
	}


	public static class b extends PJProcess {

		int _pd$n;

	    public b(int _pd$n) {
	    	this._pd$n = _pd$n;
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
			System.out.println( ("b:" + _pd$n) );
			if( (_pd$n == 0) ) {
			  terminate();
			  return;
			};
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new complexRecurseTest.a( (_pd$n - 1) ){
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

			(new complexRecurseTest.k( (_pd$n - 1) ){
			  public void finalize() {
			    par2.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 4;
			yield();
			label(4);
			terminate();
			return;
		}
	}


	public static class c extends PJProcess {

		int _pd$n;

	    public c(int _pd$n) {
	    	this._pd$n = _pd$n;
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
			System.out.println( ("c:" + _pd$n) );
			if( (_pd$n == 0) ) {
			  terminate();
			  return;
			};
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new complexRecurseTest.d( (_pd$n - 1) ){
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

			(new complexRecurseTest.m( (_pd$n - 1) ){
			  public void finalize() {
			    par2.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 4;
			yield();
			label(4);
			terminate();
			return;
		}
	}


	public static class d extends PJProcess {

		int _pd$i;

	    public d(int _pd$i) {
	    	this._pd$i = _pd$i;
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
			System.out.println( ("d:" + _pd$i) );
			if( (_pd$i == 0) ) {
			  terminate();
			  return;
			};
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new complexRecurseTest.c( (_pd$i - 1) ){
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

			(new complexRecurseTest.n( (_pd$i - 1) ){
			  public void finalize() {
			    par2.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 4;
			yield();
			label(4);
			terminate();
			return;
		}
	}


	public static class p extends PJProcess {

		int _pd$n;

	    public p(int _pd$n) {
	    	this._pd$n = _pd$n;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			System.out.println( ("p:" + _pd$n) );
			if( (_pd$n == 0) ) {
			  terminate();
			  return;
			};
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new complexRecurseTest.k( (_pd$n - 1) ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 2;
			yield();
			label(2);
			terminate();
			return;
		}
	}


	public static class k extends PJProcess {

		int _pd$n;

	    public k(int _pd$n) {
	    	this._pd$n = _pd$n;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			System.out.println( ("k:" + _pd$n) );
			if( (_pd$n == 0) ) {
			  terminate();
			  return;
			};
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new complexRecurseTest.p( (_pd$n - 1) ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 2;
			yield();
			label(2);
			terminate();
			return;
		}
	}


	public static class m extends PJProcess {

		int _pd$i;

	    public m(int _pd$i) {
	    	this._pd$i = _pd$i;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			System.out.println( ("m:" + _pd$i) );
			if( (_pd$i == 0) ) {
			  terminate();
			  return;
			};
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new complexRecurseTest.n( (_pd$i - 1) ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 2;
			yield();
			label(2);
			terminate();
			return;
		}
	}


	public static class n extends PJProcess {

		int _pd$i;

	    public n(int _pd$i) {
	    	this._pd$i = _pd$i;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			System.out.println( ("n:" + _pd$i) );
			if( (_pd$i == 0) ) {
			  terminate();
			  return;
			};
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new complexRecurseTest.m( (_pd$i - 1) ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 2;
			yield();
			label(2);
			terminate();
			return;
		}
	}


	public static class main extends PJProcess {

		String[] _pd$args;

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
			}
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new complexRecurseTest.a( 10 ){
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

			(new complexRecurseTest.c( 20 ){
			  public void finalize() {
			    par2.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 4;
			yield();
			label(4);
			terminate();
		}
	}

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();
		PJProcess.scheduler = scheduler;
		System.out.println("Added _main process to scheduler...");

		(new complexRecurseTest.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
}