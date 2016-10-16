import java.util.*;
import ProcessJ.runtime.*;

public class parinv {
	public static class g extends PJProcess {
		PJChannel<Integer> _pd$in;
		int _ld0$result;

	    public g(PJChannel<Integer> _pd$in) {
	    	this._pd$in = _pd$in;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			label(1);
			if(_pd$in.isReadyToRead(this)) {
				_ld0$result = _pd$in.read(this);
				this.runLabel = 2;
				yield();
			} else {
				setNotReady();
				_pd$in.addReader(this);
				this.runLabel = 1;
				yield();
			}
			label(2);
			terminate();
			return;
		}
	}


	public static class p1 extends PJProcess {
		PJChannel<Integer> _pd$in;
		int _ld0$a;

	    public p1(PJChannel<Integer> _pd$in) {
	    	this._pd$in = _pd$in;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			final PJPar par1 = new PJPar(1, this);

			(new parinv.g( _pd$in ){
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


	public static class p2 extends PJProcess {
		PJChannel<Integer> _pd$out;

	    public p2(PJChannel<Integer> _pd$out) {
	    	this._pd$out = _pd$out;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}

			label(1);
			if (_pd$out.isReadyToWrite()) {
				_pd$out.write(this, 42);
				this.runLabel = 2;
				yield();
			} else {
				setNotReady();
				this.runLabel = 1;
				yield();
			}
			label(2);
			terminate();
		}
	}


	public static class main extends PJProcess {
		String[] _pd$args;
		int _ld0$n;
		int _ld1$i;
		PJOne2OneChannel<Integer> _ld2$c;

	    public main(String[] _pd$args) {
	    	this._pd$args = _pd$args;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 2: resume(2); break;
			}
			_ld0$n=std.strings.string2int( (_pd$args[1]) );
			final PJPar parfor1 = new PJPar(-1, this);
			int cnt = 0;	
			List<PJProcess> pp = new LinkedList<PJProcess>(); 

			for(_ld1$i = 0; (_ld1$i < _ld0$n); _ld1$i++){
				cnt++;
				pp.add(
					new PJProcess(){
						@Override
						public synchronized void run() {
							switch(this.runLabel) {
								case 0: break;
							    case 1: resume(1); break;
							}
							_ld2$c = new PJOne2OneChannel<Integer>();
							final PJPar par2 = new PJPar(2, this);

							(new parinv.p1( _ld2$c ){
							  public void finalize() {
							    par2.decrement();    
							  }
							}).schedule();

							(new parinv.p2( _ld2$c ){
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

		(new parinv.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
	public static boolean getTrue() {
		return true;
	}
}