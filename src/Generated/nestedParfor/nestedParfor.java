package Generated.nestedParfor;
import java.util.*;
import ProcessJ.runtime.*;

public class nestedParfor {
	public static class foo extends PJProcess {
		int _pd$j;

	    public foo(int _pd$j) {
	    	this._pd$j = _pd$j;
	    }

		@Override
		public synchronized void run() {
//			std.io.println( ("foo: " + _pd$j) );
			terminate();
		}
	}


	public static class main extends PJProcess {
		String[] _pd$args;
		int _ld0$i;
		int _ld1$j;

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

			for(_ld0$i = 0; (_ld0$i < 10); _ld0$i++){
				cnt++;
				pp.add(
					new PJProcess(){
						@Override
						public synchronized void run() {
							switch(this.runLabel) {
								case 0: break;
							    case 1: resume(1); break;
							}
//							std.io.println( _ld0$i );
							final PJPar parfor2 = new PJPar(-1, this);
							int cnt = 0;	
							List<PJProcess> pp = new LinkedList<PJProcess>(); 

							for(_ld1$j = 0; (_ld1$j < 5); _ld1$j++){
								cnt++;
								pp.add(
									(new nestedParfor.foo( _ld1$j ){
									  public void finalize() {
									    parfor2.decrement();    
									  }
									})
								);
							}
							//set the process count	
							parfor2.setProcessCount(cnt);

							//schedule all the processes
							for(PJProcess p : pp) {
								p.schedule();
							}
							setNotReady();
							this.runLabel = 1;
							yield();
							label(1);
							System.out.println("Inner parfor done");
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
			System.out.println("outer parfor done");
			terminate();
		}
	}

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();

		PJProcess.scheduler = scheduler;
		System.out.println("Added _main process to scheduler...");

		(new nestedParfor.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
	public static boolean getTrue() {
		return true;
	}
}