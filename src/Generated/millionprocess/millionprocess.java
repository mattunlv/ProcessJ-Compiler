package Generated.millionprocess;
import java.util.*;
import ProcessJ.runtime.*;

public class millionprocess {
	public static class foo extends PJProcess {

	    public foo() {
	    }

		@Override
		public synchronized void run() {
	;
			terminate();
		}
	}


	public static class bar extends PJProcess {

	    public bar() {
	    }

		@Override
		public synchronized void run() {
	;
			terminate();
		}
	}


	public static class main extends PJProcess {
		String[] _pd$args;
		PJBarrier _ld0$a;
		PJBarrier _ld1$b;
		int _ld2$j;

	    public main(String[] _pd$args) {
	    	this._pd$args = _pd$args;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			_ld0$a = new PJBarrier();
			_ld1$b = new PJBarrier();
			final PJPar par1 = new PJPar(1, this);
			_ld0$a.enroll(1);

			new PJProcess(){
				@Override
				public synchronized void run() {
					switch(this.runLabel) {
						case 0: break;
					    case 2: resume(2); break;
					}
					final PJPar parfor2 = new PJPar(-1, this);
					int cnt = 0;	
					List<PJProcess> pp = new LinkedList<PJProcess>(); 

					for(_ld2$j = 0; (_ld2$j < 10); _ld2$j++){
						cnt++;
						pp.add(
							(new millionprocess.bar(){
							  public void finalize() {
							    parfor2.decrement();    
								_ld0$a.resign();
								_ld1$b.resign();
							  }
							})
						);
					}
					//set the process count	
					parfor2.setProcessCount(cnt);
					_ld0$a.enroll(cnt);
					_ld1$b.enroll(cnt);

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

				@Override
				public void finalize() {
					par1.decrement();	
					_ld0$a.resign();
				}
			}.schedule();

			setNotReady();
			this.runLabel = 1;
			yield();
			label(1);
			std.io.println( "done!!" );
			terminate();
		}
	}

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();

		PJProcess.scheduler = scheduler;
		System.out.println("Added _main process to scheduler...");

		(new millionprocess.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
	public static boolean getTrue() {
		return true;
	}
}