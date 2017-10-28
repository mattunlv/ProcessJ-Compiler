package SampleTests;


public class chanOne2ManyTest { 
//	public static class _writer extends Process {
//
//		Channel<Integer> _pd_chanW;
//		int _ld0_x;
//
//	    public _writer(Channel<Integer> _pd_chanW) {
//	    	this._pd_chanW = _pd_chanW;
//	    	runLabel = 0;
//	    }
//
//		@Override
//		public void run() {
//
//			_ld0_x = 2;
//			
//			switch(runLabel) {
//				case 0: resume(0); break;
//				case 1: resume(1);break;//this needs to jump to after if/else pt.L1
//			}
//			
//			label(0);
//			if (this._pd_chanW.isReadyToWrite()) {
//				_pd_chanW.write(this, _ld0_x);
//				System.out.println("wrote x=" + _ld0_x );
//				runLabel = 1;
//				yield(1);
//			} else {
//				setNotReady();
//				System.out.println("writer not ready");
//				runLabel = 0;
//				yield(0);
//			}
//			
//			label(1);
//			
//			_ld0_x++;
//
//			System.out.println("writer terminating");
//	      	terminate();
//		}
//	}
//	public static class _reader1 extends Process {
//
//		Channel<Integer> _pd_chanR;
//
//	    public _reader1(Channel<Integer> _pd_chanR) {
//	    	this._pd_chanR = _pd_chanR;
//	    	runLabel = 0;
//	    }
//
//		@Override
//		public void run() {
//
//			switch(runLabel) {
//				case 0: resume(0);break;
////				case 1: resume(1); break; //this should not jump
//			}
//			
//			label(0);
//			if(this._pd_chanR.isReadyToRead(this)) {
//				_pd_chanR.read(this);
//			} else {
//				System.out.println("reader not ready");
//				setNotReady();
//				runLabel = 0;
//				yield(0);
//			}
//
//			System.out.println("reader terminating");
//	      	terminate();
//		}
//	}
//	public static class _reader2 extends Process {
//
//		Channel<Integer> _pd_chanR;
//
//	    public _reader2(Channel<Integer> _pd_chanR) {
//	    	this._pd_chanR = _pd_chanR;
//	    	runLabel = 0;
//	    }
//
//		@Override
//		public void run() {
//
//			switch(runLabel) {
//				case 0: resume(0);break;
////				case 1: resume(1); break; //this should not jump
//			}
//			
//			label(0);
//			if(this._pd_chanR.isReadyToRead(this)) {
//				_pd_chanR.read(this);
//			} else {
//				System.out.println("reader not ready");
//				setNotReady();
//				runLabel = 0;
//				yield(0);
//			}
//
//			System.out.println("reader terminating");
//	      	terminate();
//		}
//	}
//	
//	public static class _foo extends Process {
//		Channel<Integer> _pd_a;
//		
//		public _foo(Channel<Integer> _pd_a) {
//			this._pd_a = _pd_a;
//			runLabel = 0;
//		}
//		
//		@Override
//		public void run() {
//			
//			switch (runLabel) {
//				case 0:
//					resume(0);
//					break;
//				case 1:
//					resume(1);
//					break;
//				default:
//					break;
//			}
//			
//			label(0);
//			final Par par1 = new Par(2, this);
//
//			//QA: do we need full path here? chanOne2ManyTest._reader1
//			new _reader1( _pd_a) {
//				public void finalize() {
//					par1.decrement();
//				}
//			}.schedule();
//
//			new _reader2( _pd_a) {
//				public void finalize() {
//					par1.decrement();
//				}
//			}.schedule();
//			
//			runLabel = 1;
//			setNotReady();
//			yield(1);
//			label(1);
//			
//			terminate();
//			
//		}
//	}
//
//	public static void main(String[] args) {
//		Scheduler scheduler = new Scheduler();
//		Process.scheduler = scheduler;
//		System.out.println("Added _main process to scheduler...");
//
//		Channel<Integer> a = new One2OneChannel<Integer>();
//		System.out.println("scheduled writer");
//		(new chanOne2ManyTest._writer( a )).schedule();
//
//		System.out.println("scheduled foo");
//		(new chanOne2ManyTest._foo(a)).schedule();
//
//		System.out.println("Starting the scheduler...");
//		Process.scheduler.start();
//
//		System.out.println("Scheduler thread.start() done.");
//	}
}
