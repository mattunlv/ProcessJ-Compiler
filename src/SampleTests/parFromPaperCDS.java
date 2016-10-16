package SampleTests;

public class parFromPaperCDS { 
//	public static class p1 extends PJProcess {
//
//		Channel<Integer> _pd_c;
//		int _ld0_y;
//
//	    public p1(Channel<Integer> _pd_c) {
//	    	this._pd_c = _pd_c;
//	    }
//
//		@Override
//		public void run() {
//			switch(runLabel) {
//				case 0: resume(0);break;
//				case 1: resume(1); break;
//			}
//
//			label(0);
//			if(_pd_c.isReadyToRead(this)) {
//				_ld0_y = _pd_c.read(this);
//				runLabel = 1;
//				yield();
//			} else {
//				_pd_c.addReader(this);
//				setNotReady();
//				runLabel = 0;
//				yield();
//			}
//
//			label(1);
//
//	      	terminate();
//		}
//	}
//	public static class p2 extends PJProcess {
//
//		Channel<Integer> _pd_c;
//		int _pd_x;
//
//	    public p2(Channel<Integer> _pd_c, int _pd_x) {
//	    	this._pd_c = _pd_c;
//	    	this._pd_x = _pd_x;
//	    }
//
//		@Override
//		public void run() {
//
//			switch(runLabel) {
//				case 0: resume(0); break;
//				case 1: resume(1); break;
//			}
//
//			label(0);
//			if (_pd_c.isReadyToWrite()) {
//				_pd_c.write(this, _pd_x);
//				runLabel = 1;
//				yield();
//			} else {
//				setNotReady();
//				runLabel = 0;
//				yield();
//			}
//
//			label(1);
//
//	      	terminate();
//		}
//	}
//	public static int p3(int _pd_y) {
//		return _pd_y;
//	}
//	public static int p4(int _pd_z) {
//		return _pd_z;
//	}
//	public static class foo extends PJProcess {
//
//		public int _pd_x;
//		public PJOne2OneChannel<Integer> _ld0_c;
//		public int _ld1_p;
//		public int _ld2_k;
//
//	    public foo(int _pd_x) {
//	    	this._pd_x = _pd_x;
//	    }
//
//	    @Override
//		public void run() {
//
//			_ld0_c = new PJOne2OneChannel<Integer>();
//			switch(this.runLabel) {
//				case 0: break;
//				case 1: resume(1); break;	
//			}
//			label(0);	
//			final PJPar par1 = new PJPar(2, this);
//			setNotReady();
//
//			new PJProcess() {
//				public void run() {
////					foo ctx= (foo) this.context;
//					switch(this.runLabel) {
//						case 0: resume(0);break;
//						case 1: resume(1); break;
//					}
//					
//					label(0);
//					if(_ld0_c.isReadyToRead(this)) {
//						_pd_x = _ld0_c.read(this);
//						runLabel = 1;
//						yield();
//					} else {
//						_ld0_c.addReader(this);
//						setNotReady();
//						runLabel = 0;
//						yield();
//					}
//
//					label(1);
//					
//					terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();		
//				}
//			}.schedule();
//			
//			(new parFromPaper.p1( _ld0_c ){
//				public void finalize() {
//					par1.decrement();		
//				}
//			}).schedule();
//
//			runLabel = 3;
//			yield();
//			label(3);
//			parFromPaper.bar( 10 );
//			(new parFromPaper.p2( _ld0_c, _pd_x )).schedule();
//			
//			
//			
//			switch(runLabel) {
//				case 2: resume(2); break;	
//				case 3: resume(3); break;
//			}
//			label(2);	
//			final PJPar par2 = new PJPar(3, this);
//
//			setNotReady();
//
//			new PJProcess() {
//				public void run() {
//					_ld1_p = parFromPaperCDS.p3( 4 );
//				}
//				public void finalize() {
//					par2.decrement();		
//				}
//			}.schedule();
//
//			new PJProcess() {
//				public void run() {
//					_ld2_k = parFromPaperCDS.p4( 5 );
//				}
//				public void finalize() {
//					par2.decrement();		
//				}
//			}.schedule();
//			
//			new PJProcess() {
//				public void run() {
//					if( (_pd_x == 5) ) {
//						  _pd_x = 10;
//					};
//				}
//				public void finalize() {
//					par2.decrement();		
//				}
//			}.schedule();
//			
//			runLabel = 3;
//			yield();
//			label(3);
//
//	      	terminate();
//		}
//	}
//	public static void bar(int _pd_y) {
//		_pd_y = (_pd_y + 10);
//	}
//	public static void main(String[] args) {
//		Scheduler scheduler = new Scheduler();
//		PJProcess.scheduler = scheduler;
//		System.out.println("Added _main process to scheduler...");
//
//		 (new parFromPaperCDS.foo( 5 )).schedule();
//
//		System.out.println("Starting the scheduler...");
//		PJProcess.scheduler.start();
//
//		System.out.println("Scheduler thread.start() done.");
//	}
}