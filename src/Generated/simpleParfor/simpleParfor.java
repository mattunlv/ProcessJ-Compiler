package Generated.simpleParfor;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class simpleParfor { 
	public static class foo extends PJProcess {
		int _ld0$n;
		PJOne2OneChannel<Integer> _ld1$c;
		int _ld2$x;
		int _ld3$y;
		PJBarrier _ld4$b;
		int _ld5$i;

	    public foo() {
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 3: resume(3); break;
			    case 4: resume(4); break;
			}
			_ld0$n = 2;
			_ld1$c = new PJOne2OneChannel<Integer>();
			_ld4$b = new PJBarrier();
			label(3);	
			final PJPar parfor1 = new PJPar(-1, this);
			int cnt = 0;	
			List<PJProcess> pp = new LinkedList<PJProcess>(); 

			for(_ld5$i = 0; (_ld5$i < _ld0$n); _ld5$i++){
				cnt++;
				pp.add(
					new PJProcess(){
						@Override
						public synchronized void run() {
							switch(this.runLabel) {
								case 0: break;
							    case 1: resume(1); break;
							    case 2: resume(2); break;
							}
							label(1);	
							final PJPar par2 = new PJPar(1, this);

							(new simpleParfor.bar( _ld5$i ){
							  public void finalize() {
							    par2.decrement();    
							  }
							}).schedule();

							setNotReady();
							this.runLabel = 2;
							yield();
							label(2);
					      	terminate();
						}

						@Override
						public void finalize() {
							parfor1.decrement();	
							_ld4$b.resign();
						}
					}
				);
			}
			//set the process count	
			parfor1.setProcessCount(cnt);
			_ld4$b.enroll(cnt);

			//schedule all the processes
			for(PJProcess p : pp) {
				p.schedule();
			}
			setNotReady();
			this.runLabel = 4;
			yield();
			label(4);
			terminate();
		}
	}


	public static class bar extends PJProcess {
		int _pd$i;

	    public bar(int _pd$i) {
	    	this._pd$i = _pd$i;
	    }

		@Override
		public synchronized void run() {
			std.io.println( (_pd$i + ":inside bar") );
			terminate();
		}
	}


	public static class baz extends PJProcess {
		int _pd$i;

	    public baz(int _pd$i) {
	    	this._pd$i = _pd$i;
	    }

		@Override
		public synchronized void run() {
			std.io.println( (_pd$i + ":inside baz") );
			terminate();
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
			}
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new simpleParfor.foo(){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

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

		(new simpleParfor.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
	public static class doubleforenrolled extends PJProcess {
		PJBarrier _ld0$a;
		PJBarrier _ld1$b;
		PJBarrier _ld2$c;
		PJOne2OneChannel<Integer> _ld3$cr;
		int _ld4$i;
		int _ld5$j;

	    public doubleforenrolled() {
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 5: resume(5); break;
			    case 6: resume(6); break;
			}
			_ld0$a = new PJBarrier();
			_ld1$b = new PJBarrier();
			_ld2$c = new PJBarrier();
			_ld3$cr = new PJOne2OneChannel<Integer>();
			label(5);	
			final PJPar parfor1 = new PJPar(-1, this);
			int cnt = 0;	
			List<PJProcess> pp = new LinkedList<PJProcess>(); 

			for(_ld4$i = 0; (_ld4$i < 5); _ld4$i++){
				cnt++;
				pp.add(
					new PJProcess(){
						@Override
						public synchronized void run() {
							switch(this.runLabel) {
								case 0: break;
							    case 3: resume(3); break;
							    case 4: resume(4); break;
							}
							label(3);	
							final PJPar parfor2 = new PJPar(-1, this);
							int cnt = 0;	
							List<PJProcess> pp = new LinkedList<PJProcess>(); 

							for(_ld5$j = 0; (_ld5$j < 6); _ld5$j++){
								cnt++;
								pp.add(
									new PJProcess(){
										@Override
										public synchronized void run() {
											switch(this.runLabel) {
												case 0: break;
											    case 1: resume(1); break;
											    case 2: resume(2); break;
											}
											label(1);
											if(_ld3$cr.isReadyToRead(this)) {
												_ld3$cr.read(this);
												this.runLabel = 2;
												yield();
											} else {
												setNotReady();
												_ld3$cr.addReader(this);
												this.runLabel = 1;
												yield();
											}
											label(2);
									      	terminate();
										}

										@Override
										public void finalize() {
											parfor2.decrement();	
											_ld2$c.resign();
										}
									}
								);
							}
							//set the process count	
							parfor2.setProcessCount(cnt);
							_ld2$c.enroll(cnt);

							//schedule all the processes
							for(PJProcess p : pp) {
								p.schedule();
							}
							setNotReady();
							this.runLabel = 4;
							yield();
							label(4);
					      	terminate();
						}

						@Override
						public void finalize() {
							parfor1.decrement();	
							_ld0$a.resign();
							_ld1$b.resign();
						}
					}
				);
			}
			//set the process count	
			parfor1.setProcessCount(cnt);
			_ld0$a.enroll(cnt);
			_ld1$b.enroll(cnt);

			//schedule all the processes
			for(PJProcess p : pp) {
				p.schedule();
			}
			setNotReady();
			this.runLabel = 6;
			yield();
			label(6);
			terminate();
		}
	}


}