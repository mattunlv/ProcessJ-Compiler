package SampleTests;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.*;

public class channelTestCDS { 
//	public static class _writer extends Process {
//
//		Channel<Integer> _pd_chanW;
//		int _ld0_x;
//
//	    public _writer(Channel<Integer> _pd_chanW) {
//	    	this._pd_chanW = _pd_chanW;
//	    	runLabel = 0;
//	    }
//
//		@Override
//		public void run() {
//
//			_ld0_x = 2;
//			
//			switch(runLabel) {
//				case 0: resume(0); break;
//				case 1: resume(1);break;//this needs to jump to after if/else pt.L1
//			}
//			
//			label(0);
//			if (this._pd_chanW.isReadyToWrite()) {
//				_pd_chanW.write(this, _ld0_x);
//				System.out.println("wrote x=" + _ld0_x );
//				runLabel = 1;
//				yield(1);
//			} else {
//				setNotReady();
//				System.out.println("writer not ready");
//				runLabel = 0;
//				yield(0);
//			}
//			
//			label(1);
//			
//			_ld0_x++;
//
//			System.out.println("writer terminating");
//	      	terminate();
//		}
//	}
//	public static class _reader extends Process {
//
//		Channel<Integer> _pd_chanR; 
//		int _ld0_x;
//		int _temp_retval;
//
//	    public _reader(Channel<Integer> _pd_chanR) {
//	    	this._pd_chanR = _pd_chanR;
//	    	runLabel = 0;
//	    }
//
//		@Override
//		public void run() {
//
//			_ld0_x = _temp_retval;
//
//			switch(runLabel) {
//				case 0: resume(0);break;
//				case 1: resume(1); break; 
//			}
//			
//			label(0);
//			if(this._pd_chanR.isReadyToRead(this)) {
//				_temp_retval = _pd_chanR.read(this);
//				runLabel = 1;
//				yield(1);
//			} else {
//				System.out.println("reader not ready");
//				setNotReady();
//				runLabel = 0;
//				yield(0);
//			}
//
//			label(1);
//
//			System.out.println("reader terminating");
//	      	terminate();
//		}
//	}
//	public static void main(String[] args) {
//		Scheduler scheduler = new Scheduler();
//		Process.scheduler = scheduler;
//		System.out.println("Added _main process to scheduler...");
//
//		Channel<Integer> _ld0_intChan = new One2OneChannel<Integer>();
//		System.out.println("scheduled writer");
//		(new channelTestCDS._writer( _ld0_intChan )).schedule();
//
//		System.out.println("scheduled reader");
//		(new channelTestCDS._reader( _ld0_intChan )).schedule();
//
//		System.out.println("Starting the scheduler...");
//		Process.scheduler.start();
//
//		System.out.println("Scheduler thread.start() done.");
//	}
}