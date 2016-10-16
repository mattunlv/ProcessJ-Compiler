package Generated.millionprocess1;
import java.util.*;
import ProcessJ.runtime.*;

public class millionprocess1 {
	public static class foo extends PJProcess {
		PJChannel<Integer> _pd$c1r;
		PJChannel<Integer> _pd$c2w;
		int _ld0$x;

	    public foo(PJChannel<Integer> _pd$c1r, PJChannel<Integer> _pd$c2w) {
	    	this._pd$c1r = _pd$c1r;
	    	this._pd$c2w = _pd$c2w;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			final PJPar par1 = new PJPar(2, this);

			new PJProcess(){
				@Override
				public synchronized void run() {
					switch(this.runLabel) {
						case 0: break;
					    case 2: resume(2); break;
					    case 3: resume(3); break;
					}
					label(2);
					if(_pd$c1r.isReadyToRead(this)) {
						_ld0$x = _pd$c1r.readPreRendezvous(this);
//						std.io.println( ("foo.x=" + _ld0$x) );
						_pd$c1r.readPostRendezvous(this);
						this.runLabel = 3;
						yield();
					} else {
						setNotReady();
						_pd$c1r.addReader(this);
						this.runLabel = 2;
						yield();
					}
					label(3);
			      	terminate();
				}

				@Override
				public void finalize() {
					par1.decrement();	
				}
			}.schedule();

			new PJProcess(){
				@Override
				public synchronized void run() {
					switch(this.runLabel) {
						case 0: break;
					    case 4: resume(4); break;
					    case 5: resume(5); break;
					}

					label(4);
					if (_pd$c2w.isReadyToWrite()) {
						_pd$c2w.write(this, 10);
						this.runLabel = 5;
						yield();
					} else {
						setNotReady();
						this.runLabel = 4;
						yield();
					}
					label(5);
			      	terminate();
				}

				@Override
				public void finalize() {
					par1.decrement();	
				}
			}.schedule();

			setNotReady();
			this.runLabel = 1;
			yield();
			label(1);
			terminate();
		}
	}


	public static class bar extends PJProcess {
		PJChannel<Integer> _pd$c1w;
		PJChannel<Integer> _pd$c2r;
		int _ld0$y;

	    public bar(PJChannel<Integer> _pd$c1w, PJChannel<Integer> _pd$c2r) {
	    	this._pd$c2r = _pd$c2r;
	    	this._pd$c1w = _pd$c1w;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			final PJPar par1 = new PJPar(2, this);

			new PJProcess(){
				@Override
				public synchronized void run() {
					switch(this.runLabel) {
						case 0: break;
					    case 2: resume(2); break;
					    case 3: resume(3); break;
					}
					label(2);
					if(_pd$c2r.isReadyToRead(this)) {
						_ld0$y = _pd$c2r.readPreRendezvous(this);
//						std.io.println( ("bar.y=" + _ld0$y) );
						_pd$c2r.readPostRendezvous(this);
						this.runLabel = 3;
						yield();
					} else {
						setNotReady();
						_pd$c2r.addReader(this);
						this.runLabel = 2;
						yield();
					}
					label(3);
			      	terminate();
				}

				@Override
				public void finalize() {
					par1.decrement();	
				}
			}.schedule();

			new PJProcess(){
				@Override
				public synchronized void run() {
					switch(this.runLabel) {
						case 0: break;
					    case 4: resume(4); break;
					    case 5: resume(5); break;
					}

					label(4);
					if (_pd$c1w.isReadyToWrite()) {
						_pd$c1w.write(this, 20);
						this.runLabel = 5;
						yield();
					} else {
						setNotReady();
						this.runLabel = 4;
						yield();
					}
					label(5);
			      	terminate();
				}

				@Override
				public void finalize() {
					par1.decrement();	
				}
			}.schedule();

			setNotReady();
			this.runLabel = 1;
			yield();
			label(1);
			terminate();
		}
	}


	public static class main extends PJProcess {
		String[] _pd$args;
		int _ld0$i;
		PJOne2OneChannel<Integer> _ld1$c1;
		PJOne2OneChannel<Integer> _ld2$c2;

	    public main(String[] _pd$args) {
	    	this._pd$args = _pd$args;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 2: resume(2); break;
			}
			final PJPar parfor1 = new PJPar(-1, this);
			int cnt = 0;	
			List<PJProcess> pp = new LinkedList<PJProcess>(); 
			
			int loopCnt = Integer.valueOf(_pd$args[0]);

			for(_ld0$i = 0; (_ld0$i < 4000000); _ld0$i++){
				cnt++;
				pp.add(
					new PJProcess(){
						@Override
						public synchronized void run() {
							switch(this.runLabel) {
								case 0: break;
							    case 1: resume(1); break;
							}
							_ld1$c1 = new PJOne2OneChannel<Integer>();
							_ld2$c2 = new PJOne2OneChannel<Integer>();
							final PJPar par2 = new PJPar(2, this);

							(new millionprocess1.foo( _ld1$c1, _ld2$c2 ){
							  public void finalize() {
							    par2.decrement();    
							  }
							}).schedule();

							(new millionprocess1.bar( _ld1$c1, _ld2$c2 ){
							  public void finalize() {
							    par2.decrement();    
							  }
							}).schedule();

							setNotReady();
							this.runLabel = 1;
							yield();
							label(1);
					      	terminate();
						}

						@Override
						public void finalize() {
							parfor1.decrement();	
						}
					}
				);
			}
			//set the process count	
			parfor1.setProcessCount(cnt);

			//schedule all the processes
			for(PJProcess p : pp) {
				p.schedule();
			}
			setNotReady();
			this.runLabel = 2;
			yield();
			label(2);
			terminate();
		}
	}

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();

		PJProcess.scheduler = scheduler;
		System.out.println("Added _main process to scheduler...");

		(new millionprocess1.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
	public static boolean getTrue() {
		return true;
	}
}