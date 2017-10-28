package SampleTests;

import Generated.AltSimpleTest.AltSimpleTest;
import Generated.parFromPaper.parFromPaper;
import ProcessJ.runtime.PJAlt;
import ProcessJ.runtime.PJChannel;
import ProcessJ.runtime.PJOne2ManyChannel;
import ProcessJ.runtime.PJPar;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.Scheduler;
import ProcessJ.runtime.PJTimer;

public class AltSimpleTestCDS {

	public static class writer extends PJProcess {
		PJChannel<Integer> _pd_cw;

		public writer(PJChannel<Integer> _pd_cw) {
			this._pd_cw = _pd_cw;
			this.runLabel = 0;
		}

		@Override
		public void run() {

			switch (this.runLabel) {
				case 0:
					resume(0);
					break;
				case 1:
					resume(1);
					break;
			}

			label(0);
			if (this._pd_cw.isReadyToWrite()) {
				_pd_cw.write(this, 10);
				runLabel = 1;
				yield();
			} else {
				setNotReady();
				runLabel = 0;
				yield();
			}

			label(1);

			terminate();
		}
	}
	
	public static class main extends PJProcess {
		
		String[] _pd_args;
		PJChannel<Integer> _ld0_c;
		int _ld1_x;
		PJTimer _ld2_timer;

		public main(String[] _pd_args) {
			this._pd_args = _pd_args;
		}
		
		@Override
		public void run() {
			
			switch(this.runLabel) {
				case 0: break;
				case 1: resume(1); break;	
			}

			_ld0_c = new PJOne2ManyChannel<Integer>();

			label(0);	

			PJPar par1 = new PJPar(2, this);

			(new writer(_ld0_c) {
				public void finalize() {
					par1.decrement();	
				}
			}).schedule();

			new PJProcess() {
				public void run() {
					
					switch(this.runLabel) {
						case 0: break;
						case 2: resume(2); break;	
						case 3: resume(3); break;	
					}
					
					 //==========ALT CODE===============
					final PJAlt alt = new PJAlt(3, this); //3 is the number of cases.
					
					boolean btemp1 = _ld1_x > 5;
					boolean btemp2 = AltSimpleTest.getBool();
					boolean[] bg = {true,  btemp1, btemp2};

					_ld2_timer = new PJTimer(this, 10000);

					Object[] guards = {_ld0_c, _ld2_timer, PJAlt.SKIP_GUARD};
					boolean bRet = alt.setGuards(bg, guards);

					if (!bRet) {
						System.out.println("RuntimeException: One of the boolean guards needs to be true!!");
						System.exit(1);
					}

					label(2);
					int chosen = alt.getReadyGuardIndex();
					switch(chosen) {
						case -1: //this can also be done as default case.
							//if first time, and there are timers, 
							// start all the timers here.
							if (!_ld2_timer.started) {
								try {
									_ld2_timer.start();
								} catch (InterruptedException e) {
									System.out.println("InterruptedException!");
								}
							}
							break;
						case 0:  //Channel read guard
							_ld1_x = _ld0_c.read(this);//necessary unreserve done in read
							_ld1_x += 5;
							break;
						case 1: //Timer guard
							_ld1_x += 10;
							break;
						case 2: //skip guard
							_ld1_x += 15;
							break;
					}

					if (chosen == -1) {
						this.runLabel = 2;
						yield();
					} else { //Alt has ended by making a valid choice.
						/*
						 * If timer has started but hasn't timedout, we need to stop it.
						 * We cannot take (remove) it from DelayQueue until it times out. So,
						 * instead we just kill the runtime Timer object.
						 */
						if (_ld2_timer.started && !_ld2_timer.expired) {
							_ld2_timer.kill();
						}
						
						this.runLabel = 3;
						yield();
					}

					label(2);//When ALT is done, will jump here.

					terminate();
				}
				
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

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();
		PJProcess.scheduler = scheduler;
		System.out.println("Added _main process to scheduler...");

		(new parFromPaper.foo( 5 )).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
}
