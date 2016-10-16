package Generated.nestedParTest;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class nestedParTest { 
	public static class santa extends PJProcess {

	    public santa() {
	    }

		@Override
		public synchronized void run() {
	;
			terminate();
		}
	}


	public static class reindeer extends PJProcess {

	    public reindeer() {
	    }

		@Override
		public synchronized void run() {
	;
			terminate();
		}
	}


	public static class elf extends PJProcess {

	    public elf() {
	    }

		@Override
		public synchronized void run() {
	;
			terminate();
		}
	}


	public static class display extends PJProcess {

	    public display() {
	    }

		@Override
		public synchronized void run() {
	;
			terminate();
		}
	}


	public static class p_barrier_knock extends PJProcess {

	    public p_barrier_knock() {
	    }

		@Override
		public synchronized void run() {
	;
			terminate();
		}
	}


	public static class p_barrier extends PJProcess {

	    public p_barrier() {
	    }

		@Override
		public synchronized void run() {
	;
			terminate();
		}
	}


	public static class foo extends PJProcess {
		PJBarrier _ld0$a;
		PJBarrier _ld1$b;
		PJBarrier _ld2$c;
		int _ld3$i;
		int _ld4$i;

	    public foo() {
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			_ld0$a = new PJBarrier();
			_ld1$b = new PJBarrier();
			_ld2$c = new PJBarrier();
			label(1);	
			final PJPar par1 = new PJPar(5, this);

			new PJProcess(){
				@Override
				public synchronized void run() {
					switch(this.runLabel) {
						case 0: break;
					    case 3: resume(3); break;
					    case 4: resume(4); break;
					}
					label(3);	
					final PJPar par2 = new PJPar(2, this);
					_ld0$a.enroll(2);

					(new nestedParTest.santa(){
					  public void finalize() {
					    par2.decrement();    
						_ld0$a.resign();
					  }
					}).schedule();
					new PJProcess(){
						@Override
						public synchronized void run() {
							switch(this.runLabel) {
								case 0: break;
							    case 5: resume(5); break;
							    case 6: resume(6); break;
							}
							label(5);	
							final PJPar parfor3 = new PJPar(-1, this);
							int cnt = 0;	
							List<PJProcess> pp = new LinkedList<PJProcess>(); 

							for(_ld3$i = 0; (_ld3$i < 5); _ld3$i++){
								cnt++;
								pp.add(
									new PJProcess(){
										@Override
										public synchronized void run() {
											(new nestedParTest.reindeer(){
											  public void finalize() {
											    parfor3.decrement();    
											  }
											}).schedule();
									      	terminate();
										}

										@Override
										public void finalize() {
											parfor3.decrement();	
											_ld1$b.resign();
											_ld2$c.resign();
										}
									}
								);
							}
							//set the process count	
							parfor3.setProcessCount(cnt);
							_ld1$b.enroll(cnt);
							_ld2$c.enroll(cnt);

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

						@Override
						public void finalize() {
							par2.decrement();	
							_ld0$a.resign();
						}
					}.schedule();

					setNotReady();
					this.runLabel = 4;
					yield();
					label(4);
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
					    case 7: resume(7); break;
					    case 8: resume(8); break;
					}
					label(7);	
					final PJPar parfor4 = new PJPar(-1, this);
					int cnt = 0;	
					List<PJProcess> pp = new LinkedList<PJProcess>(); 

					for(_ld4$i = 0; (_ld4$i < 5); _ld4$i++){
						cnt++;
						pp.add(
							new PJProcess(){
								@Override
								public synchronized void run() {
									(new nestedParTest.elf(){
									  public void finalize() {
									    parfor4.decrement();    
									  }
									}).schedule();
							      	terminate();
								}

								@Override
								public void finalize() {
									parfor4.decrement();	
									_ld0$a.resign();
									_ld1$b.resign();
									_ld2$c.resign();
								}
							}
						);
					}
					//set the process count	
					parfor4.setProcessCount(cnt);
					_ld0$a.enroll(cnt);
					_ld1$b.enroll(cnt);
					_ld2$c.enroll(cnt);

					//schedule all the processes
					for(PJProcess p : pp) {
						p.schedule();
					}
					setNotReady();
					this.runLabel = 8;
					yield();
					label(8);
			      	terminate();
				}

				@Override
				public void finalize() {
					par1.decrement();	
				}
			}.schedule();
			(new nestedParTest.display(){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();
			(new nestedParTest.p_barrier_knock(){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();
			(new nestedParTest.p_barrier(){
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


}