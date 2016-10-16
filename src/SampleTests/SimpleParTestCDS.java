package SampleTests;


public class SimpleParTestCDS {
//
//	//===========================================
//	public static class _p1 extends Process {
//		Channel<Integer> _pd_c;
//		int _a;
//
//		public _p1(Channel<Integer> c) {
//			this._pd_c = c;
//		}
//
//		public void run() {
//			System.out.println("Running _p1...");
//
//			terminate();
//		}
//
//	}
//
//	//===========================================
//
//	public static class _p2 extends Process {
//		Channel<Integer> _pd_c;
//
//		public _p2(Channel<Integer> c) {
//			this._pd_c = c;
//			runLabel = 0;
//		}
//
//		public void run() {
//			System.out.println("Running _p2...");
//
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
//	    	p1(c.read);
//	    	p2(c.write);
//	      }
//	    }
//	  }
//	 */
//	public static class _main extends Process {
//		int param$n;
//		Channel<Integer> c;
//
//		public _main(int n) {
//			param$n = n;
//		}
//
//		public void run() {
//
//			c = new One2OneChannel<Integer>();
//
//			runLabel = 0;
//			switch (runLabel) {
//				case 0:
//					break;
//				case 1:
//					resume(1);
//					break;
//				case 3:
//					resume(2);
//					break;
//				default:
//					break;
//			}
//
//			final Par _par1 = new Par(2, this);
//
//			// this assure that this process will not get scheduled until it
//			// is marked ready by the par block!
//			setNotReady();
//
//			// make the new processes and add them to the runqueue	       
//			new _p1(c) {
//				public void finalize() {
//					System.out.println("Decrementing par count for p1...");
//					_par1.decrement();
//				}
//			}.schedule();
//			System.out.println("Trying to insert a p1");
//
//			new _p2(c) {
//				public void finalize() {
//					System.out.println("Decrementing par count for p2...");
//					_par1.decrement();
//				}
//			}.schedule();
//			System.out.println("Trying to insert a p2");
//
//			// then suspend yourself to run the processes in the par!		
//			runLabel = 1;
//			yield(1);
////			label(1);
//			
//			System.out.println("label1 code");
//			
//			runLabel = 2;
//			yield(3);
////			label(2);
//			
//
//			System.out.println("Terminating main...");
//			terminate();
//		}
//
//	}
//	
//	//=================
//
//	public static void main(String args[]) {
//		Scheduler scheduler = new Scheduler();
//		Process.scheduler = scheduler;
//
//		System.out.println("Added _main process to scheduler...");
//		(new SimpleParTestCDS._main(5)).schedule();
//
//		System.out.println("Starting the scheduler...");
//		Process.scheduler.start();
//		
//		System.out.println("Scheduler thread.start() done.");
//	}
//
}