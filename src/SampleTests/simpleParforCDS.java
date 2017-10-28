package SampleTests;

import java.util.LinkedList;
import java.util.List;

//import Generated.simpleParfor.simpleParfor;
//import ProcessJ.runtime.Par;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.Scheduler;

public class simpleParforCDS { 
////	public static class foo extends Process {
////
////		int _ld0$n;
////		int _ld1$i;
////
////	    public foo() {
////	    }
////
////		@Override
////		public synchronized void run() {
////			switch(this.runLabel) {
////				case 0: break;
////			    case 1: resume(1); break;
////			    case 2: resume(2); break;
////			}
////			_ld0$n=10;
////			
////			//------if parfor block is empty, maybe do not generate any of this
////			label(1);	
////			final Par par1 = new Par(-1, this);
////			int cnt = 0;
////			List<Process> pp = new LinkedList<Process>();
////
////			for(_ld1$i=0; (_ld1$i < _ld0$n); _ld1$i++){
////				cnt++;
////				pp.add(new Process(){
////					public synchronized void run() {
////						switch(this.runLabel) {
////							case 0: break;
////						    case 1: resume(1); break;
////						    case 2: resume(2); break;
////						    case 3: resume(3); break;
////						    case 4: resume(4); break;
////						}
////						label(1);	
////						final Par par1 = new Par(1, this);
////
////						(new simpleParfor.bar(){
////						  public void finalize() {
////						    par1.decrement();    
////						  }
////						}).schedule();
////
////						setNotReady();
////						this.runLabel = 2;
////						yield();
////						label(2);
////						
////						//-----------
////						label(3);	
////						final Par par2 = new Par(1, this);
////
////						(new simpleParfor.baz(){
////						  public void finalize() {
////						    par2.decrement();    
////						  }
////						}).schedule();
////
////						setNotReady();
////						this.runLabel = 4;
////						yield();
////						label(4);
////						//-----------
////				      	terminate();
////					}
////
////					public void finalize() {
////						par1.decrement();	
////					}
////				});
////			}
////
////			//set the process count
////			par1.setProcessCount(cnt);
////
////			//schedule all the processes.
////			for(Process p : pp) {
////				p.schedule();
////			}
////			setNotReady();
////			this.runLabel = 2;
////			yield();
////			label(2);
////			terminate();
////		}
////	}
//
//
//	public static class bar extends Process {
//
//
//	    public bar() {
//	    }
//
//		@Override
//		public synchronized void run() {
//			std.io.println( "inside bar" );
//			terminate();
//		}
//	}
//
//
//	public static class baz extends Process {
//
//
//	    public baz() {
//	    }
//
//		@Override
//		public synchronized void run() {
//			std.io.println( "inside baz" );
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
//			(new simpleParfor.foo(){
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
//		Process.scheduler = scheduler;
//		System.out.println("Added _main process to scheduler...");
//
//		(new simpleParfor.main(args)).schedule();
//
//		System.out.println("Starting the scheduler...");
//		Process.scheduler.start();
//
//		System.out.println("Scheduler thread.start() done.");
//	}
//
}
