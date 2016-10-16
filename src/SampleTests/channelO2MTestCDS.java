package SampleTests;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.*;

public class channelO2MTestCDS { 
//	public static class _writer extends Process {
//
//		Channel<Integer> _pd_chanW;
//		int _ld0_x;
//
//	    public _writer(Object context, Channel<Integer> _pd_chanW) {
//	    	super(context);
//	    	this._pd_chanW = _pd_chanW;
//	    }
//
//		@Override
//		public void run() {
//
//			_ld0_x = 2;
//			switch(runLabel) {
//				case 0: resume(0);
//				case 1: resume(1);
//			}
//
//			label(0);
//			if (_pd_chanW.isReadyToWrite()) {
//				_pd_chanW.write(this, _ld0_x);
//				runLabel = 1;
//				yield(1);
//			} else {
//				setNotReady();
//				runLabel = 0;
//				yield(0);
//			}
//
//			label(1);
//			_ld0_x++;
//
//	      	terminate();
//		}
//	}
//	public static class _reader1 extends Process {
//
//		Channel<Integer> _pd_chanR;
//		int _ld0_x;
//		int tempVar;
//		
//		public _reader1(Object context) {
//			super(context);
//		}
//
//	    public _reader1(Object context, Channel<Integer> _pd_chanR) {
//	    	super(context);
//	    	this._pd_chanR = _pd_chanR;
//	    }
//
//		@Override
//		public void run() {
//			
//			_ld0_x = tempVar;
//
//			switch(runLabel) {
//				case 0: resume(0);break;
//				case 1: resume(1); break;
//			}
//
//			label(0);
//			if(_pd_chanR.isReadyToRead(this)) {
//				tempVar = _pd_chanR.read(this);
//				runLabel = 1;
//				yield(1);
//			} else {
//				_pd_chanR.addReader(this);
//				setNotReady();
//				runLabel = 0;
//				yield(0);
//			}
//
//			label(1);
//
//	      	terminate();
//		}
//	}
//	public static class _reader2 extends Process {
//
//		Channel<Integer> _pd_chanR;
//		int _ld0_y;
//		int tempVar;
//
//	    public _reader2(Object context, Channel<Integer> _pd_chanR) {
//	    	super(context);
//	    	this._pd_chanR = _pd_chanR;
//	    }
//
//		@Override
//		public void run() {
//
//			_ld0_y = tempVar;
//
//			switch(runLabel) {
//				case 0: resume(0);break;
//				case 1: resume(1); break;
//			}
//
//			label(0);
//			if(_pd_chanR.isReadyToRead(this)) {
//				tempVar = _pd_chanR.read(this);
//				runLabel = 1;
//				yield(1);
//			} else {
//				setNotReady();
//				runLabel = 0;
//				yield(0);
//			}
//
//			label(1);
//
//	      	terminate();
//		}
//	}
//	public static void main(String[] args) {
//		Scheduler scheduler = new Scheduler();
//		Process.scheduler = scheduler;
//		System.out.println("Added _main process to scheduler...");
//
//		channelO2MTestCDS context = new channelO2MTestCDS();
//
//		One2ManyChannel<Integer> _ld0_intChan = new One2ManyChannel<Integer>();
//		(new channelO2MTestCDS._writer( context, _ld0_intChan )).schedule();
//		(new channelO2MTestCDS._reader1( context, _ld0_intChan )).schedule();
//		(new channelO2MTestCDS._writer( context, _ld0_intChan )).schedule();
//		(new channelO2MTestCDS._reader2( context, _ld0_intChan )).schedule();
//
//		System.out.println("Starting the scheduler...");
//		Process.scheduler.start();
//
//		System.out.println("Scheduler thread.start() done.");
//	}
}