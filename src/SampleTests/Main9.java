package SampleTests;


public class Main9 {
//
//	//===========================================
//	public static class _p1 extends PJProcess {
//
//		Channel<Integer> _pd_c;
//		int _a;
//		int runLabel;
//		int result;
//
//		public _p1(Channel<Integer> c) {
//			this._pd_c = c;
//		}
//
//		public void run() {
//			//this is p1()
//			runLabel = 0;
//
//			// Re-establish local (this is done after any declaration!)
//			switch (runLabel) {
//			case 0:
//				//do something here??
//
//			case 1:
//				// if we arrive at 2 from 1 we will have a new activation for g at the head of the queue
//				// if we arrive at 2 from the switch the activation will aleady be at the head of the queue
//				_a = g();
//				runLabel = 1; //return from here
//				terminate();
//			}
//			terminate();
//		}
//
//		public int g() {
//			// Define paramters 
//			Channel<Integer> param$c; // to avoid name clashes it should be named param$c
//			int result;
//
//			param$c = new PJOne2OneChannel<Integer>();
//
//			switch (runLabel) {
//			case 0:
//				if (param$c.isReadyToRead(this)) {
//					result = param$c.read(this);
//					param$c.unreserve();
//				} else {
//					setNotReady();
//					//only add if it doesn't exist.
//					param$c.addReader(this);
////					param$c.reserve(); 
//					runLabel = 0;
//					return 0;
//				}
//				terminate();
//				return result;
//			}
//			terminate();
//			return 0;
//		}
//	}
//
//	//===========================================
//	public static class _p2 extends PJProcess {
//		Channel<Integer> _pd_c;
//
//		public _p2(Channel<Integer> c) {
//			this._pd_c = c;
//		}
//
//		public void run() {
//			runLabel = 0;
//
//			switch (runLabel) {
//			case 0:
//				if (this._pd_c.isReadyToWrite()) {
//					this._pd_c.write(this, 42);
//					runLabel = 1;
//					return;
//				} else {
//					setNotReady();
//					runLabel = 0;
//					return;
//				}
//			case 1:
//				terminate();
//				return;
//			}
//			terminate();
//		}
//
//	}
//
//	//===========================================
//
//	/*
//	  proc void main(int n) {      
//	    par for (int i=0; i<n; i++) {
//	      chan<int> c;  
//	      par {
//	    p1(c.read);
//	    p2(c.write);
//	      }
//	    }
//	  }
//	 */
//	public static class _main extends PJProcess {
//		int param$n;
//		Channel<Integer> c;
//
//		public _main(int n) {
//			param$n = n;
//		}
//
//		public void run() {
//			runLabel = 0;
//
//			c = new PJOne2OneChannel<Integer>();
//
//			switch (runLabel) {
//			case 0:
//				for (int i = 0; i < param$n; i++) {
//					c = new PJOne2OneChannel<Integer>(); // hack
//					final PJPar _par1 = new PJPar(2, this);
//
//					// this assure that this process will not get scheduled until it
//					// is marked ready by the par block!
//					setNotReady();
//
//					// make the new processes and add them to the runqueue	       
//					PJProcess p1 = new _p1(c) {
//						public void finalize() {
//							_par1.decrement();
//						}
//					};
//					System.out.println("Trying to insert a p1");
//
//					p1.schedule();
//
//					PJProcess p2 = new _p2(c) {
//						public void finalize() {
//							_par1.decrement();
//						}
//					};
//					p1.schedule();
//
//				}
//				// then suspend yourself to run the processes in the par!		
//
//				runLabel = 1;
//
//				return;
//			case 1:
//				terminate();
//				return;
//			}
//		}
//	}
//
//	//===========================================
//
//	//	public void run(int x) {
//	//		rt.insert(new _main(x) {
//	//		});
//	//		System.out.println("ready to run!");
//	//		rt.run();
//	//	}
//
//	public static void main(String args[]) {
//		Scheduler scheduler = new Scheduler();
//		PJProcess.scheduler = scheduler;
//		
//		System.out.println("Added _main process to scheduler...");
//		(new Main9._main(5)).schedule();
//		
//		System.out.println("Starting the scheduler...");
//		PJProcess.scheduler.start();
//		
//		System.out.println("Scheduler thread.start() done.");
//	}
//	
//	
}