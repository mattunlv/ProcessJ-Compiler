package SampleTests;


public class simpleBarrierCDS { 
//	public static class foo extends Process {
//
//		One2OneChannel<Integer> _ld0$c;
//		Barrier _ld1$b;
//
//	    public foo() {
//	    }
//
//		@Override
//		public synchronized void run() {
//			switch(this.runLabel) {
//				case 0: break;
//			    case 1: resume(1); break;
//			    case 2: resume(2); break;
//			}
//			_ld0$c = new One2OneChannel<Integer>();
//			label(1);	
//			final Par par1 = new Par(3, this);
//			_ld1$b = new Barrier(3);
//
//			Process p1 = (new simpleBarrierCDS.reader( _ld0$c, _ld1$b ){
//						public void finalize() {
//							par1.decrement();    
//						}
//					});
//			_ld1$b.enroll(p1);
//			p1.schedule();
//
//			Process p2 = (new simpleBarrierCDS.writer(_ld0$c){
//						public void finalize() {
//							par1.decrement();    
//							_ld1$b.sync(this);
//						}
//					});
//			_ld1$b.enroll(p2);
//			p2.schedule();
//
//			Process p3 = (new simpleBarrierCDS.bar( _ld1$b ){
//						public void finalize() {
//							par1.decrement();    
//						}
//					});
//			_ld1$b.enroll(p3);
//			p3.schedule();
//
//			setNotReady();
//			this.runLabel = 2;
//			yield();
//			label(2);
//			terminate();
//		}
//	}
//
//
//	public static class reader extends Process {
//
//		Channel<Integer> _pd$cr;
//		Barrier _pd$b;
//		int _ld0$x;
//
//	    public reader(Channel<Integer> _pd$cr, Barrier _pd$b) {
//	    	this._pd$b = _pd$b;
//	    	this._pd$cr = _pd$cr;
//	    }
//
//		@Override
//		public synchronized void run() {
//			switch(this.runLabel) {
//				case 0: break;
//			    case 1: resume(1); break;
//			    case 2: resume(2); break;
//			}
//			label(1);
//			if(_pd$cr.isReadyToRead(this)) {
//				_ld0$x = _pd$cr.read(this);
//				this.runLabel = 2;
//				yield();
//			} else {
//				setNotReady();
//				_pd$cr.addReader(this);
//				this.runLabel = 1;
//				yield();
//			}
//			label(2);
//			std.io.println( ("x=" + _ld0$x) );
//			std.io.println( "syncing reader" );
//			_pd$b.sync(this);
//			this.runLabel = 3;
//			yield();
//			label(3);
//			terminate();
//		}
//	}
//
//
//	public static class writer extends Process {
//
//		Channel<Integer> _pd$cw;
////		Barrier _pd$b;
//
//	    public writer(Channel<Integer> _pd$cw) {
////	    	this._pd$b = _pd$b;
//	    	this._pd$cw = _pd$cw;
//	    }
//
//		@Override
//		public synchronized void run() {
//			switch(this.runLabel) {
//				case 0: break;
//			    case 1: resume(1); break;
//			    case 2: resume(2); break;
//			}
//
//			label(1);
//			if (_pd$cw.isReadyToWrite()) {
//				_pd$cw.write(this, 10);
//				this.runLabel = 2;
//				yield();
//			} else {
//				setNotReady();
//				this.runLabel = 1;
//				yield();
//			}
//
//			label(2);
//			std.io.println( "not syncing writer" );
////			_pd$b.sync(this);
//			terminate();
//		}
//	}
//
//
//	public static class bar extends Process {
//
//		Barrier _pd$b;
//
//	    public bar(Barrier _pd$b) {
//	    	this._pd$b = _pd$b;
//	    }
//
//		@Override
//		public synchronized void run() {
//			switch(this.runLabel) {
//				case 0: break;
//				case 1: resume(1); break; 
//			}
//
//			std.io.println( "syncing bar" );
//			_pd$b.sync(this);
//			this.runLabel = 1;
//			yield();
//			label(1);
//			terminate();
//		}
//	}
//	
//	public static class main extends Process {
//
//		String[] _pd$args;
//
//	    public main(String[] _pd$args) {
//	    	this._pd$args = _pd$args;
//	    }
//
//		@Override
//		public synchronized void run() {
//			switch(this.runLabel) {
//				case 0: break;
//			    case 1: resume(1); break;
//			    case 2: resume(2); break;
//			}
//			label(1);	
//			final Par par1 = new Par(1, this);
//
//			(new simpleBarrierCDS.foo(){
//			  public void finalize() {
//			    par1.decrement();    
//			  }
//			}).schedule();
//
//			setNotReady();
//			this.runLabel = 2;
//			yield();
//			label(2);
//			terminate();
//		}
//	}
//
//	public static void main(String[] args) {
//		Scheduler scheduler = new Scheduler();
//
//		Process.scheduler = scheduler;
//		System.out.println("Added _main process to scheduler...");
//
//		(new simpleBarrierCDS.main(args)).schedule();
//
//		System.out.println("Starting the scheduler...");
//		Process.scheduler.start();
//
//		System.out.println("Scheduler thread.start() done.");
//	}
//
//
}
