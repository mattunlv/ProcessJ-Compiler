package SampleTests;



public class simpleRecurseTestCDS {
//
//	public static class f extends Process {
//
//		int n;
//		
//		public f(int n) {
//			this.n = n;
//		}
//		
//		public void run() {
//			switch(this.runLabel) {
//				case 0: break;
//				case 1: resume(1); break;
//			}
//			System.out.println("Hello! " + n);
//
//			if (n==0) {
//				terminate();
//				return;
//			}
//			
//			Par rec1 = new Par(1, this);
//			
//			(new simpleRecurseTestCDS.f(n-1) {
//				public void finalize() {
//						rec1.decrement();
//				}
//			}).schedule();
//			
//			setNotReady();
//			runLabel=1;
//			yield();
//			label(1);
//			
//			terminate();
//		}
//	}
//	
//	public static class main extends Process {
//		
//		String[] args;
//		simpleRecurseTestCDS.f obj;
//		
//		public main(String[] args) {
//			this.args = args;
//		}
//		
//		public void run() {
//			switch(this.runLabel) {
//				case 0: break;
//			    case 1: resume(1); break;
//			}
//			obj = new simpleRecurseTestCDS.f(10);
//			obj.setCaller(this);
//
//			label(1);
//			obj.yielded=false;
//			obj.run();  
//			if(obj.yielded) {
//				setNotReady();
//				this.runLabel = 1;
//				yield();
//			};
//	      	terminate();	
//		}
//		
//	}
//	
//	public static void main(String[] args) {
//		Scheduler scheduler = new Scheduler();
//		Process.scheduler = scheduler;
//		System.out.println("Added _main process to scheduler...");
//
//		(new simpleRecurseTestCDS.main( args )).schedule();
//
//		System.out.println("Starting the scheduler...");
//		Process.scheduler.start();
//
//		System.out.println("Scheduler thread.start() done.");
//	}
}
