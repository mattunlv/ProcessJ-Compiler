package SampleTests;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.*;

public class fullAdderCDS { 
//	public static class andGate extends Process {
//
//		Channel<Boolean> _pd_in1;
//		Channel<Boolean> _pd_in2;
//		Channel<Boolean> _pd_out;
//		boolean _ld0_x;
//		boolean _ld1_y;
//
//	    public andGate(Channel<Boolean> _pd_in1, Channel<Boolean> _pd_in2, Channel<Boolean> _pd_out) {
//	    	this._pd_in2 = _pd_in2;
//	    	this._pd_in1 = _pd_in1;
//	    	this._pd_out = _pd_out;
//	    }
//
//		@Override
//		public void run() {
//			switch(this.runLabel) {
//			    case 0: break;
//			    case 1: resume(1); break;
//			    case 6: resume(6); break;
//			    case 7: resume(7); break;
//			}
//			_ld0_x=false;
//			_ld1_y=false;
//			label(0);	
//			final Par par1 = new Par(2, this);
//
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 2: resume(2); break;
//					    case 3: resume(3); break;
//					}
//					label(2);
//					if(_pd_in1.isReadyToRead(this)) {
//						_ld0_x = _pd_in1.read(this);
//						this.runLabel = 3;
//						yield(3);
//					} else {
//						setNotReady();
//						_pd_in1.addReader(this);
//						this.runLabel = 2;
//						yield(2);
//					}
//					label(3);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 4: resume(4); break;
//					    case 5: resume(5); break;
//					}
//					label(4);
//					if(_pd_in2.isReadyToRead(this)) {
//						_ld1_y = _pd_in2.read(this);
//						this.runLabel = 5;
//						yield(5);
//					} else {
//						setNotReady();
//						_pd_in2.addReader(this);
//						this.runLabel = 4;
//						yield(4);
//					}
//					label(5);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//
//			setNotReady();
//			this.runLabel = 1;
//			yield(1);
//			label(1);
//
//			label(6);
//			if (_pd_out.isReadyToWrite()) {
//				_pd_out.write(this, (_ld0_x && _ld1_y));
//				this.runLabel = 7;
//				yield(7);
//			} else {
//				setNotReady();
//				this.runLabel = 6;
//				yield(6);
//			}
//
//			label(7);
//	      	terminate();
//		}
//	}
//	public static class orGate extends Process {
//
//		Channel<Boolean> _pd_in1;
//		Channel<Boolean> _pd_in2;
//		Channel<Boolean> _pd_out;
//		boolean _ld0_x;
//		boolean _ld1_y;
//
//	    public orGate(Channel<Boolean> _pd_in1, Channel<Boolean> _pd_in2, Channel<Boolean> _pd_out) {
//	    	this._pd_in2 = _pd_in2;
//	    	this._pd_in1 = _pd_in1;
//	    	this._pd_out = _pd_out;
//	    }
//
//		@Override
//		public void run() {
//			switch(this.runLabel) {
//			    case 0: break;
//			    case 1: resume(1); break;
//			    case 6: resume(6); break;
//			    case 7: resume(7); break;
//			}
//			_ld0_x=false;
//			_ld1_y=false;
//			label(0);	
//			final Par par1 = new Par(2, this);
//
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 2: resume(2); break;
//					    case 3: resume(3); break;
//					}
//					label(2);
//					if(_pd_in1.isReadyToRead(this)) {
//						_ld0_x = _pd_in1.read(this);
//						this.runLabel = 3;
//						yield(3);
//					} else {
//						setNotReady();
//						_pd_in1.addReader(this);
//						this.runLabel = 2;
//						yield(2);
//					}
//					label(3);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 4: resume(4); break;
//					    case 5: resume(5); break;
//					}
//					label(4);
//					if(_pd_in2.isReadyToRead(this)) {
//						_ld1_y = _pd_in2.read(this);
//						this.runLabel = 5;
//						yield(5);
//					} else {
//						setNotReady();
//						_pd_in2.addReader(this);
//						this.runLabel = 4;
//						yield(4);
//					}
//					label(5);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//
//			setNotReady();
//			this.runLabel = 1;
//			yield(1);
//			label(1);
//
//			label(6);
//			if (_pd_out.isReadyToWrite()) {
//				_pd_out.write(this, (_ld0_x || _ld1_y));
//				this.runLabel = 7;
//				yield(7);
//			} else {
//				setNotReady();
//				this.runLabel = 6;
//				yield(6);
//			}
//
//			label(7);
//	      	terminate();
//		}
//	}
//	public static class notGate extends Process {
//
//		Channel<Boolean> _pd_in;
//		Channel<Boolean> _pd_out;
//		boolean _ld0_x;
//
//	    public notGate(Channel<Boolean> _pd_in, Channel<Boolean> _pd_out) {
//	    	this._pd_in = _pd_in;
//	    	this._pd_out = _pd_out;
//	    }
//
//		@Override
//		public void run() {
//			switch(this.runLabel) {
//			    case 0: break;
//			    case 1: resume(1); break;
//			    case 2: resume(2); break;
//			    case 3: resume(3); break;
//			}
//			_ld0_x=false;
//			label(0);
//			if(_pd_in.isReadyToRead(this)) {
//				_ld0_x = _pd_in.read(this);
//				this.runLabel = 1;
//				yield(1);
//			} else {
//				setNotReady();
//				_pd_in.addReader(this);
//				this.runLabel = 0;
//				yield(0);
//			}
//			label(1);
//
//			label(2);
//			if (_pd_out.isReadyToWrite()) {
//				_pd_out.write(this, !_ld0_x);
//				this.runLabel = 3;
//				yield(3);
//			} else {
//				setNotReady();
//				this.runLabel = 2;
//				yield(2);
//			}
//
//			label(3);
//	      	terminate();
//		}
//	}
//	public static class nandGate extends Process {
//
//		Channel<Boolean> _pd_in1;
//		Channel<Boolean> _pd_in2;
//		Channel<Boolean> _pd_out;
//		One2OneChannel<Boolean> _ld0_a;
//
//	    public nandGate(Channel<Boolean> _pd_in1, Channel<Boolean> _pd_in2, Channel<Boolean> _pd_out) {
//	    	this._pd_in2 = _pd_in2;
//	    	this._pd_in1 = _pd_in1;
//	    	this._pd_out = _pd_out;
//	    }
//
//		@Override
//		public void run() {
//			switch(this.runLabel) {
//			    case 0: break;
//			    case 1: resume(1); break;
//			}
//			_ld0_a = new One2OneChannel<Boolean>();
//			label(0);	
//			final Par par1 = new Par(2, this);
//
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.andGate( _pd_in1, _pd_in2, _ld0_a ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.notGate( _ld0_a, _pd_out ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//
//			setNotReady();
//			this.runLabel = 1;
//			yield(1);
//			label(1);
//	      	terminate();
//		}
//	}
//	public static class muxGate extends Process {
//
//		Channel<Boolean> _pd_in;
//		Channel<Boolean> _pd_out1;
//		Channel<Boolean> _pd_out2;
//		boolean _ld0_x;
//
//	    public muxGate(Channel<Boolean> _pd_in, Channel<Boolean> _pd_out1, Channel<Boolean> _pd_out2) {
//	    	this._pd_out2 = _pd_out2;
//	    	this._pd_in = _pd_in;
//	    	this._pd_out1 = _pd_out1;
//	    }
//
//		@Override
//		public void run() {
//			switch(this.runLabel) {
//			    case 0: break;
//			    case 1: resume(1); break;
//			    case 2: resume(2); break;
//			    case 3: resume(3); break;
//			}
//			_ld0_x=false;
//			label(0);
//			if(_pd_in.isReadyToRead(this)) {
//				_ld0_x = _pd_in.read(this);
//				this.runLabel = 1;
//				yield(1);
//			} else {
//				setNotReady();
//				_pd_in.addReader(this);
//				this.runLabel = 0;
//				yield(0);
//			}
//			label(1);
//			label(2);	
//			final Par par1 = new Par(2, this);
//
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 4: resume(4); break;
//					    case 5: resume(5); break;
//					}
//
//					label(4);
//					if (_pd_out1.isReadyToWrite()) {
//						_pd_out1.write(this, _ld0_x);
//						this.runLabel = 5;
//						yield(5);
//					} else {
//						setNotReady();
//						this.runLabel = 4;
//						yield(4);
//					}
//
//					label(5);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 6: resume(6); break;
//					    case 7: resume(7); break;
//					}
//
//					label(6);
//					if (_pd_out2.isReadyToWrite()) {
//						_pd_out2.write(this, _ld0_x);
//						this.runLabel = 7;
//						yield(7);
//					} else {
//						setNotReady();
//						this.runLabel = 6;
//						yield(6);
//					}
//
//					label(7);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//
//			setNotReady();
//			this.runLabel = 3;
//			yield(3);
//			label(3);
//	      	terminate();
//		}
//	}
//	public static class xorGate extends Process {
//
//		Channel<Boolean> _pd_in1;
//		Channel<Boolean> _pd_in2;
//		Channel<Boolean> _pd_out;
//		One2OneChannel<Boolean> _ld0_a;
//		One2OneChannel<Boolean> _ld1_b;
//		One2OneChannel<Boolean> _ld2_c;
//		One2OneChannel<Boolean> _ld3_d;
//		One2OneChannel<Boolean> _ld4_e;
//		One2OneChannel<Boolean> _ld5_f;
//		One2OneChannel<Boolean> _ld6_g;
//		One2OneChannel<Boolean> _ld7_h;
//		One2OneChannel<Boolean> _ld8_i;
//
//	    public xorGate(Channel<Boolean> _pd_in1, Channel<Boolean> _pd_in2, Channel<Boolean> _pd_out) {
//	    	this._pd_in2 = _pd_in2;
//	    	this._pd_in1 = _pd_in1;
//	    	this._pd_out = _pd_out;
//	    }
//
//		@Override
//		public void run() {
//			switch(this.runLabel) {
//			    case 0: break;
//			    case 1: resume(1); break;
//			}
//			_ld0_a = new One2OneChannel<Boolean>();
//			_ld1_b = new One2OneChannel<Boolean>();
//			_ld2_c = new One2OneChannel<Boolean>();
//			_ld3_d = new One2OneChannel<Boolean>();
//			_ld4_e = new One2OneChannel<Boolean>();
//			_ld5_f = new One2OneChannel<Boolean>();
//			_ld6_g = new One2OneChannel<Boolean>();
//			_ld7_h = new One2OneChannel<Boolean>();
//			_ld8_i = new One2OneChannel<Boolean>();
//			label(0);	
//			final Par par1 = new Par(7, this);
//
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.muxGate( _pd_in1, _ld0_a, _ld1_b ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.muxGate( _pd_in2, _ld2_c, _ld3_d ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.nandGate( _ld1_b, _ld3_d, _ld4_e ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.muxGate( _ld4_e, _ld5_f, _ld6_g ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.nandGate( _ld0_a, _ld5_f, _ld7_h ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.nandGate( _ld2_c, _ld6_g, _ld8_i ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.nandGate( _ld7_h, _ld8_i, _pd_out ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//
//			setNotReady();
//			this.runLabel = 1;
//			yield(1);
//			label(1);
//	      	terminate();
//		}
//	}
//	public static class oneBitAdder extends Process {
//
//		Channel<Boolean> _pd_in1;
//		Channel<Boolean> _pd_in2;
//		Channel<Boolean> _pd_in3;
//		Channel<Boolean> _pd_result;
//		Channel<Boolean> _pd_carry;
//		One2OneChannel<Boolean> _ld0_a;
//		One2OneChannel<Boolean> _ld1_b;
//		One2OneChannel<Boolean> _ld2_c;
//		One2OneChannel<Boolean> _ld3_d;
//		One2OneChannel<Boolean> _ld4_e;
//		One2OneChannel<Boolean> _ld5_f;
//		One2OneChannel<Boolean> _ld6_g;
//		One2OneChannel<Boolean> _ld7_h;
//		One2OneChannel<Boolean> _ld8_i;
//		One2OneChannel<Boolean> _ld9_j;
//		One2OneChannel<Boolean> _ld10_k;
//
//	    public oneBitAdder(Channel<Boolean> _pd_in1, Channel<Boolean> _pd_in2, Channel<Boolean> _pd_in3, Channel<Boolean> _pd_result, Channel<Boolean> _pd_carry) {
//	    	this._pd_result = _pd_result;
//	    	this._pd_carry = _pd_carry;
//	    	this._pd_in2 = _pd_in2;
//	    	this._pd_in1 = _pd_in1;
//	    	this._pd_in3 = _pd_in3;
//	    }
//
//		@Override
//		public void run() {
//			switch(this.runLabel) {
//			    case 0: break;
//			    case 1: resume(1); break;
//			}
//			_ld0_a = new One2OneChannel<Boolean>();
//			_ld1_b = new One2OneChannel<Boolean>();
//			_ld2_c = new One2OneChannel<Boolean>();
//			_ld3_d = new One2OneChannel<Boolean>();
//			_ld4_e = new One2OneChannel<Boolean>();
//			_ld5_f = new One2OneChannel<Boolean>();
//			_ld6_g = new One2OneChannel<Boolean>();
//			_ld7_h = new One2OneChannel<Boolean>();
//			_ld8_i = new One2OneChannel<Boolean>();
//			_ld9_j = new One2OneChannel<Boolean>();
//			_ld10_k = new One2OneChannel<Boolean>();
//			label(0);	
//			final Par par1 = new Par(9, this);
//
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.muxGate( _pd_in1, _ld0_a, _ld1_b ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.muxGate( _pd_in2, _ld2_c, _ld3_d ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.xorGate( _ld0_a, _ld2_c, _ld4_e ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.muxGate( _ld4_e, _ld5_f, _ld6_g ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.muxGate( _pd_in3, _ld7_h, _ld8_i ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.xorGate( _ld5_f, _ld7_h, _pd_result ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.andGate( _ld6_g, _ld8_i, _ld9_j ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.andGate( _ld1_b, _ld3_d, _ld10_k ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.orGate( _ld9_j, _ld10_k, _pd_carry ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//
//			setNotReady();
//			this.runLabel = 1;
//			yield(1);
//			label(1);
//	      	terminate();
//		}
//	}
//	public static class fourBitAdder extends Process {
//
//		Channel<Boolean> _pd_inA0;
//		Channel<Boolean> _pd_inA1;
//		Channel<Boolean> _pd_inA2;
//		Channel<Boolean> _pd_inA3;
//		Channel<Boolean> _pd_inB0;
//		Channel<Boolean> _pd_inB1;
//		Channel<Boolean> _pd_inB2;
//		Channel<Boolean> _pd_inB3;
//		Channel<Boolean> _pd_inCarry;
//		Channel<Boolean> _pd_result0;
//		Channel<Boolean> _pd_result1;
//		Channel<Boolean> _pd_result2;
//		Channel<Boolean> _pd_result3;
//		Channel<Boolean> _pd_carry;
//		One2OneChannel<Boolean> _ld0_a;
//		One2OneChannel<Boolean> _ld1_b;
//		One2OneChannel<Boolean> _ld2_c;
//
//	    public fourBitAdder(Channel<Boolean> _pd_inA0, Channel<Boolean> _pd_inA1, Channel<Boolean> _pd_inA2, Channel<Boolean> _pd_inA3, Channel<Boolean> _pd_inB0, Channel<Boolean> _pd_inB1, Channel<Boolean> _pd_inB2, Channel<Boolean> _pd_inB3, Channel<Boolean> _pd_inCarry, Channel<Boolean> _pd_result0, Channel<Boolean> _pd_result1, Channel<Boolean> _pd_result2, Channel<Boolean> _pd_result3, Channel<Boolean> _pd_carry) {
//	    	this._pd_inB0 = _pd_inB0;
//	    	this._pd_inA1 = _pd_inA1;
//	    	this._pd_inB2 = _pd_inB2;
//	    	this._pd_inA0 = _pd_inA0;
//	    	this._pd_inB1 = _pd_inB1;
//	    	this._pd_inA3 = _pd_inA3;
//	    	this._pd_inA2 = _pd_inA2;
//	    	this._pd_inB3 = _pd_inB3;
//	    	this._pd_inCarry = _pd_inCarry;
//	    	this._pd_result0 = _pd_result0;
//	    	this._pd_carry = _pd_carry;
//	    	this._pd_result3 = _pd_result3;
//	    	this._pd_result2 = _pd_result2;
//	    	this._pd_result1 = _pd_result1;
//	    }
//
//		@Override
//		public void run() {
//			switch(this.runLabel) {
//			    case 0: break;
//			    case 1: resume(1); break;
//			}
//			_ld0_a = new One2OneChannel<Boolean>();
//			_ld1_b = new One2OneChannel<Boolean>();
//			_ld2_c = new One2OneChannel<Boolean>();
//			label(0);	
//			final Par par1 = new Par(4, this);
//
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.oneBitAdder( _pd_inA0, _pd_inB0, _pd_inCarry, _pd_result0, _ld0_a ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.oneBitAdder( _pd_inA1, _pd_inB1, _ld0_a, _pd_result1, _ld1_b ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.oneBitAdder( _pd_inA2, _pd_inB2, _ld1_b, _pd_result2, _ld2_c ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.oneBitAdder( _pd_inA3, _pd_inB3, _ld2_c, _pd_result3, _pd_carry ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//
//			setNotReady();
//			this.runLabel = 1;
//			yield(1);
//			label(1);
//	      	terminate();
//		}
//	}
//	public static class eightBitAdder extends Process {
//
//		Channel<Boolean> _pd_inA0;
//		Channel<Boolean> _pd_inA1;
//		Channel<Boolean> _pd_inA2;
//		Channel<Boolean> _pd_inA3;
//		Channel<Boolean> _pd_inA4;
//		Channel<Boolean> _pd_inA5;
//		Channel<Boolean> _pd_inA6;
//		Channel<Boolean> _pd_inA7;
//		Channel<Boolean> _pd_inB0;
//		Channel<Boolean> _pd_inB1;
//		Channel<Boolean> _pd_inB2;
//		Channel<Boolean> _pd_inB3;
//		Channel<Boolean> _pd_inB4;
//		Channel<Boolean> _pd_inB5;
//		Channel<Boolean> _pd_inB6;
//		Channel<Boolean> _pd_inB7;
//		Channel<Boolean> _pd_inCarry;
//		Channel<Boolean> _pd_result0;
//		Channel<Boolean> _pd_result1;
//		Channel<Boolean> _pd_result2;
//		Channel<Boolean> _pd_result3;
//		Channel<Boolean> _pd_result4;
//		Channel<Boolean> _pd_result5;
//		Channel<Boolean> _pd_result6;
//		Channel<Boolean> _pd_result7;
//		Channel<Boolean> _pd_outCarry;
//		One2OneChannel<Boolean> _ld0_a;
//
//	    public eightBitAdder(Channel<Boolean> _pd_inA0, Channel<Boolean> _pd_inA1, Channel<Boolean> _pd_inA2, Channel<Boolean> _pd_inA3, Channel<Boolean> _pd_inA4, Channel<Boolean> _pd_inA5, Channel<Boolean> _pd_inA6, Channel<Boolean> _pd_inA7, Channel<Boolean> _pd_inB0, Channel<Boolean> _pd_inB1, Channel<Boolean> _pd_inB2, Channel<Boolean> _pd_inB3, Channel<Boolean> _pd_inB4, Channel<Boolean> _pd_inB5, Channel<Boolean> _pd_inB6, Channel<Boolean> _pd_inB7, Channel<Boolean> _pd_inCarry, Channel<Boolean> _pd_result0, Channel<Boolean> _pd_result1, Channel<Boolean> _pd_result2, Channel<Boolean> _pd_result3, Channel<Boolean> _pd_result4, Channel<Boolean> _pd_result5, Channel<Boolean> _pd_result6, Channel<Boolean> _pd_result7, Channel<Boolean> _pd_outCarry) {
//	    	this._pd_inA1 = _pd_inA1;
//	    	this._pd_inA0 = _pd_inA0;
//	    	this._pd_inA3 = _pd_inA3;
//	    	this._pd_outCarry = _pd_outCarry;
//	    	this._pd_inA2 = _pd_inA2;
//	    	this._pd_inA5 = _pd_inA5;
//	    	this._pd_inA4 = _pd_inA4;
//	    	this._pd_inA7 = _pd_inA7;
//	    	this._pd_inA6 = _pd_inA6;
//	    	this._pd_result7 = _pd_result7;
//	    	this._pd_result6 = _pd_result6;
//	    	this._pd_result5 = _pd_result5;
//	    	this._pd_result0 = _pd_result0;
//	    	this._pd_result4 = _pd_result4;
//	    	this._pd_result3 = _pd_result3;
//	    	this._pd_result2 = _pd_result2;
//	    	this._pd_result1 = _pd_result1;
//	    	this._pd_inB0 = _pd_inB0;
//	    	this._pd_inB2 = _pd_inB2;
//	    	this._pd_inB1 = _pd_inB1;
//	    	this._pd_inB4 = _pd_inB4;
//	    	this._pd_inB3 = _pd_inB3;
//	    	this._pd_inB6 = _pd_inB6;
//	    	this._pd_inB5 = _pd_inB5;
//	    	this._pd_inB7 = _pd_inB7;
//	    	this._pd_inCarry = _pd_inCarry;
//	    }
//
//		@Override
//		public void run() {
//			switch(this.runLabel) {
//			    case 0: break;
//			    case 1: resume(1); break;
//			}
//			_ld0_a = new One2OneChannel<Boolean>();
//			label(0);	
//			final Par par1 = new Par(2, this);
//
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.fourBitAdder( _pd_inA0, _pd_inA1, _pd_inA2, _pd_inA3, _pd_inB0, _pd_inB1, _pd_inB2, _pd_inB3, _pd_inCarry, _pd_result0, _pd_result1, _pd_result2, _pd_result3, _ld0_a ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.fourBitAdder( _pd_inA4, _pd_inA5, _pd_inA6, _pd_inA7, _pd_inB4, _pd_inB5, _pd_inB6, _pd_inB7, _ld0_a, _pd_result4, _pd_result5, _pd_result6, _pd_result7, _pd_outCarry ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//
//			setNotReady();
//			this.runLabel = 1;
//			yield(1);
//			label(1);
//	      	terminate();
//		}
//	}
//	public static class myMain extends Process {
//
//		One2OneChannel<Boolean> _ld0_a0;
//		One2OneChannel<Boolean> _ld1_a1;
//		One2OneChannel<Boolean> _ld2_a2;
//		One2OneChannel<Boolean> _ld3_a3;
//		One2OneChannel<Boolean> _ld4_a4;
//		One2OneChannel<Boolean> _ld5_a5;
//		One2OneChannel<Boolean> _ld6_a6;
//		One2OneChannel<Boolean> _ld7_a7;
//		One2OneChannel<Boolean> _ld8_b0;
//		One2OneChannel<Boolean> _ld9_b1;
//		One2OneChannel<Boolean> _ld10_b2;
//		One2OneChannel<Boolean> _ld11_b3;
//		One2OneChannel<Boolean> _ld12_b4;
//		One2OneChannel<Boolean> _ld13_b5;
//		One2OneChannel<Boolean> _ld14_b6;
//		One2OneChannel<Boolean> _ld15_b7;
//		One2OneChannel<Boolean> _ld16_r0;
//		One2OneChannel<Boolean> _ld17_r1;
//		One2OneChannel<Boolean> _ld18_r2;
//		One2OneChannel<Boolean> _ld19_r3;
//		One2OneChannel<Boolean> _ld20_r4;
//		One2OneChannel<Boolean> _ld21_r5;
//		One2OneChannel<Boolean> _ld22_r6;
//		One2OneChannel<Boolean> _ld23_r7;
//		One2OneChannel<Boolean> _ld24_inCarry;
//		One2OneChannel<Boolean> _ld25_outCarry;
//		boolean _ld26_p0;
//		boolean _ld27_p1;
//		boolean _ld28_p2;
//		boolean _ld29_p3;
//		boolean _ld30_p4;
//		boolean _ld31_p5;
//		boolean _ld32_p6;
//		boolean _ld33_p7;
//		boolean _ld34_q0;
//		boolean _ld35_q1;
//		boolean _ld36_q2;
//		boolean _ld37_q3;
//		boolean _ld38_q4;
//		boolean _ld39_q5;
//		boolean _ld40_q6;
//		boolean _ld41_q7;
//		boolean _ld42_f0;
//		boolean _ld43_f1;
//		boolean _ld44_f2;
//		boolean _ld45_f3;
//		boolean _ld46_f4;
//		boolean _ld47_f5;
//		boolean _ld48_f6;
//		boolean _ld49_f7;
//		boolean _ld50_c;
//		boolean _ld51_inC;
//
//	    public myMain() {
//	    }
//
//		@Override
//		public void run() {
//
//			System.out.println("running myMain with runLabe = " + this.runLabel);
//
//			switch(this.runLabel) {
//			    case 0: break;
//			    case 1: resume(1); break;
//			}
//			_ld0_a0 = new One2OneChannel<Boolean>();
//			_ld1_a1 = new One2OneChannel<Boolean>();
//			_ld2_a2 = new One2OneChannel<Boolean>();
//			_ld3_a3 = new One2OneChannel<Boolean>();
//			_ld4_a4 = new One2OneChannel<Boolean>();
//			_ld5_a5 = new One2OneChannel<Boolean>();
//			_ld6_a6 = new One2OneChannel<Boolean>();
//			_ld7_a7 = new One2OneChannel<Boolean>();
//			_ld8_b0 = new One2OneChannel<Boolean>();
//			_ld9_b1 = new One2OneChannel<Boolean>();
//			_ld10_b2 = new One2OneChannel<Boolean>();
//			_ld11_b3 = new One2OneChannel<Boolean>();
//			_ld12_b4 = new One2OneChannel<Boolean>();
//			_ld13_b5 = new One2OneChannel<Boolean>();
//			_ld14_b6 = new One2OneChannel<Boolean>();
//			_ld15_b7 = new One2OneChannel<Boolean>();
//			_ld16_r0 = new One2OneChannel<Boolean>();
//			_ld17_r1 = new One2OneChannel<Boolean>();
//			_ld18_r2 = new One2OneChannel<Boolean>();
//			_ld19_r3 = new One2OneChannel<Boolean>();
//			_ld20_r4 = new One2OneChannel<Boolean>();
//			_ld21_r5 = new One2OneChannel<Boolean>();
//			_ld22_r6 = new One2OneChannel<Boolean>();
//			_ld23_r7 = new One2OneChannel<Boolean>();
//			_ld24_inCarry = new One2OneChannel<Boolean>();
//			_ld25_outCarry = new One2OneChannel<Boolean>();
//			
//			_ld26_p0=true;
//			_ld27_p1=false;
//			_ld28_p2=true;
//			_ld29_p3=true; //x: right
////			_ld29_p3=false; //y: wrong
//			_ld30_p4=false;
//			_ld31_p5=false;
//			_ld32_p6=true;
//			_ld33_p7=false;
//
//			_ld34_q0=true;
//			_ld35_q1=true;
//			_ld36_q2=false;
//			_ld37_q3=false; //x: right
////			_ld37_q3=true; //y: wrong
//			_ld38_q4=false;
//			_ld39_q5=true;
//			_ld40_q6=false;
//			_ld41_q7=true;
//			
//			_ld42_f0=false;
//			_ld43_f1=false;
//			_ld44_f2=false;
//			_ld45_f3=false;
//			_ld46_f4=false;
//			_ld47_f5=false;
//			_ld48_f6=false;
//			_ld49_f7=false;
//			_ld50_c=false;
//			_ld51_inC=false;
//			_ld33_p7 = false;
//			_ld32_p6 = false;
//			_ld31_p5 = false;
//			_ld30_p4 = false;
//			_ld29_p3 = false;
//			_ld28_p2 = false;
//			_ld27_p1 = false;
//			_ld26_p0 = false;
//			_ld41_q7 = true;
//			_ld40_q6 = true;
//			_ld39_q5 = true;
//			_ld38_q4 = true;
//			_ld37_q3 = true;
//			_ld36_q2 = true;
//			_ld35_q1 = true;
//			_ld34_q0 = true;
//			_ld51_inC = true;
//			label(0);	
//			final Par par1 = new Par(27, this);
//
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 2: resume(2); break;
//					    case 3: resume(3); break;
//					}
//
//					label(2);
//					if (_ld7_a7.isReadyToWrite()) {
//						_ld7_a7.write(this, _ld33_p7);
//						this.runLabel = 3;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(3);
//					} else {
//						setNotReady();
//						this.runLabel = 2;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(2);
//					}
//
//					label(3);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 4: resume(4); break;
//					    case 5: resume(5); break;
//					}
//
//					label(4);
//					if (_ld6_a6.isReadyToWrite()) {
//						_ld6_a6.write(this, _ld32_p6);
//						this.runLabel = 5;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(5);
//					} else {
//						setNotReady();
//						this.runLabel = 4;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(4);
//					}
//
//					label(5);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 6: resume(6); break;
//					    case 7: resume(7); break;
//					}
//
//					label(6);
//					if (_ld5_a5.isReadyToWrite()) {
//						_ld5_a5.write(this, _ld31_p5);
//						this.runLabel = 7;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(7);
//					} else {
//						setNotReady();
//						this.runLabel = 6;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(6);
//					}
//
//					label(7);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 8: resume(8); break;
//					    case 9: resume(9); break;
//					}
//
//					label(8);
//					if (_ld4_a4.isReadyToWrite()) {
//						_ld4_a4.write(this, _ld30_p4);
//						this.runLabel = 9;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(9);
//					} else {
//						setNotReady();
//						this.runLabel = 8;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(8);
//					}
//
//					label(9);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 10: resume(10); break;
//					    case 11: resume(11); break;
//					}
//
//					label(10);
//					if (_ld3_a3.isReadyToWrite()) {
//						_ld3_a3.write(this, _ld29_p3);
//						this.runLabel = 11;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(11);
//					} else {
//						setNotReady();
//						this.runLabel = 10;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(10);
//					}
//
//					label(11);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 12: resume(12); break;
//					    case 13: resume(13); break;
//					}
//
//					label(12);
//					if (_ld2_a2.isReadyToWrite()) {
//						_ld2_a2.write(this, _ld28_p2);
//						this.runLabel = 13;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(13);
//					} else {
//						setNotReady();
//						this.runLabel = 12;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(12);
//					}
//
//					label(13);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 14: resume(14); break;
//					    case 15: resume(15); break;
//					}
//
//					label(14);
//					if (_ld1_a1.isReadyToWrite()) {
//						_ld1_a1.write(this, _ld27_p1);
//						this.runLabel = 15;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(15);
//					} else {
//						setNotReady();
//						this.runLabel = 14;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(14);
//					}
//
//					label(15);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 16: resume(16); break;
//					    case 17: resume(17); break;
//					}
//
//					label(16);
//					if (_ld0_a0.isReadyToWrite()) {
//						_ld0_a0.write(this, _ld26_p0);
//						this.runLabel = 17;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(17);
//					} else {
//						setNotReady();
//						this.runLabel = 16;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(16);
//					}
//
//					label(17);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 18: resume(18); break;
//					    case 19: resume(19); break;
//					}
//
//					label(18);
//					if (_ld15_b7.isReadyToWrite()) {
//						_ld15_b7.write(this, _ld41_q7);
//						this.runLabel = 19;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(19);
//					} else {
//						setNotReady();
//						this.runLabel = 18;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(18);
//					}
//
//					label(19);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 20: resume(20); break;
//					    case 21: resume(21); break;
//					}
//
//					label(20);
//					if (_ld14_b6.isReadyToWrite()) {
//						_ld14_b6.write(this, _ld40_q6);
//						this.runLabel = 21;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(21);
//					} else {
//						setNotReady();
//						this.runLabel = 20;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(20);
//					}
//
//					label(21);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 22: resume(22); break;
//					    case 23: resume(23); break;
//					}
//
//					label(22);
//					if (_ld13_b5.isReadyToWrite()) {
//						_ld13_b5.write(this, _ld39_q5);
//						this.runLabel = 23;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(23);
//					} else {
//						setNotReady();
//						this.runLabel = 22;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(22);
//					}
//
//					label(23);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 24: resume(24); break;
//					    case 25: resume(25); break;
//					}
//
//					label(24);
//					if (_ld12_b4.isReadyToWrite()) {
//						_ld12_b4.write(this, _ld38_q4);
//						this.runLabel = 25;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(25);
//					} else {
//						setNotReady();
//						this.runLabel = 24;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(24);
//					}
//
//					label(25);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 26: resume(26); break;
//					    case 27: resume(27); break;
//					}
//
//					label(26);
//					if (_ld11_b3.isReadyToWrite()) {
//						_ld11_b3.write(this, _ld38_q4);
//						this.runLabel = 27;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(27);
//					} else {
//						setNotReady();
//						this.runLabel = 26;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(26);
//					}
//
//					label(27);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 28: resume(28); break;
//					    case 29: resume(29); break;
//					}
//
//					label(28);
//					if (_ld10_b2.isReadyToWrite()) {
//						_ld10_b2.write(this, _ld36_q2);
//						this.runLabel = 29;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(29);
//					} else {
//						setNotReady();
//						this.runLabel = 28;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(28);
//					}
//
//					label(29);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 30: resume(30); break;
//					    case 31: resume(31); break;
//					}
//
//					label(30);
//					if (_ld9_b1.isReadyToWrite()) {
//						_ld9_b1.write(this, _ld35_q1);
//						this.runLabel = 31;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(31);
//					} else {
//						setNotReady();
//						this.runLabel = 30;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(30);
//					}
//
//					label(31);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 32: resume(32); break;
//					    case 33: resume(33); break;
//					}
//
//					label(32);
//					if (_ld8_b0.isReadyToWrite()) {
//						_ld8_b0.write(this, _ld34_q0);
//						this.runLabel = 33;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(33);
//					} else {
//						setNotReady();
//						this.runLabel = 32;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(32);
//					}
//
//					label(33);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 34: resume(34); break;
//					    case 35: resume(35); break;
//					}
//
//					label(34);
//					if (_ld24_inCarry.isReadyToWrite()) {
//						_ld24_inCarry.write(this, _ld51_inC);
//						this.runLabel = 35;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(35);
//					} else {
//						setNotReady();
//						this.runLabel = 34;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(34);
//					}
//
//					label(35);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					(new fullAdderCDS.eightBitAdder( _ld0_a0, _ld1_a1, _ld2_a2, _ld3_a3, _ld4_a4, _ld5_a5, _ld6_a6, _ld7_a7, _ld8_b0, _ld9_b1, _ld10_b2, _ld11_b3, _ld12_b4, _ld13_b5, _ld14_b6, _ld15_b7, _ld24_inCarry, _ld16_r0, _ld17_r1, _ld18_r2, _ld19_r3, _ld20_r4, _ld21_r5, _ld22_r6, _ld23_r7, _ld25_outCarry ){
//					  public void finalize() {
//					    par1.decrement();    
//					  }
//					}).schedule();
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 36: resume(36); break;
//					    case 37: resume(37); break;
//					}
//					label(36);
//					if(_ld16_r0.isReadyToRead(this)) {
//						_ld42_f0 = _ld16_r0.read(this);
//						this.runLabel = 37;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(37);
//					} else {
//						setNotReady();
//						_ld16_r0.addReader(this);
//						this.runLabel = 36;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(36);
//					}
//					label(37);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 38: resume(38); break;
//					    case 39: resume(39); break;
//					}
//					label(38);
//					if(_ld17_r1.isReadyToRead(this)) {
//						_ld43_f1 = _ld17_r1.read(this);
//						this.runLabel = 39;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(39);
//					} else {
//						setNotReady();
//						_ld17_r1.addReader(this);
//						this.runLabel = 38;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(38);
//					}
//					label(39);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 40: resume(40); break;
//					    case 41: resume(41); break;
//					}
//					label(40);
//					if(_ld18_r2.isReadyToRead(this)) {
//						_ld44_f2 = _ld18_r2.read(this);
//						this.runLabel = 41;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(41);
//					} else {
//						setNotReady();
//						_ld18_r2.addReader(this);
//						this.runLabel = 40;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(40);
//					}
//					label(41);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 42: resume(42); break;
//					    case 43: resume(43); break;
//					}
//					label(42);
//					if(_ld19_r3.isReadyToRead(this)) {
//						_ld45_f3 = _ld19_r3.read(this);
//						this.runLabel = 43;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(43);
//					} else {
//						setNotReady();
//						_ld19_r3.addReader(this);
//						this.runLabel = 42;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(42);
//					}
//					label(43);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 44: resume(44); break;
//					    case 45: resume(45); break;
//					}
//					label(44);
//					if(_ld20_r4.isReadyToRead(this)) {
//						_ld46_f4 = _ld20_r4.read(this);
//						this.runLabel = 45;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(45);
//					} else {
//						setNotReady();
//						_ld20_r4.addReader(this);
//						this.runLabel = 44;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(44);
//					}
//					label(45);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 46: resume(46); break;
//					    case 47: resume(47); break;
//					}
//					label(46);
//					if(_ld21_r5.isReadyToRead(this)) {
//						_ld47_f5 = _ld21_r5.read(this);
//						this.runLabel = 47;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(47);
//					} else {
//						setNotReady();
//						_ld21_r5.addReader(this);
//						this.runLabel = 46;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(46);
//					}
//					label(47);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 48: resume(48); break;
//					    case 49: resume(49); break;
//					}
//					label(48);
//					if(_ld22_r6.isReadyToRead(this)) {
//						_ld48_f6 = _ld22_r6.read(this);
//						this.runLabel = 49;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(49);
//					} else {
//						setNotReady();
//						_ld22_r6.addReader(this);
//						this.runLabel = 48;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(48);
//					}
//					label(49);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 50: resume(50); break;
//					    case 51: resume(51); break;
//					}
//					label(50);
//					if(_ld23_r7.isReadyToRead(this)) {
//						_ld49_f7 = _ld23_r7.read(this);
//						this.runLabel = 51;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(51);
//					} else {
//						setNotReady();
//						_ld23_r7.addReader(this);
//						this.runLabel = 50;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(50);
//					}
//					label(51);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//			new Process(){
//				public void run() {
//					switch(this.runLabel) {
//					    case 52: resume(52); break;
//					    case 53: resume(53); break;
//					}
//					label(52);
//					if(_ld25_outCarry.isReadyToRead(this)) {
//						_ld50_c = _ld25_outCarry.read(this);
//						this.runLabel = 53;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(53);
//					} else {
//						setNotReady();
//						_ld25_outCarry.addReader(this);
//						this.runLabel = 52;
//						System.out.println("yield with runLabel = " + this.runLabel);
//						yield(52);
//					}
//					label(53);
//			      	terminate();
//				}
//
//				public void finalize() {
//					par1.decrement();	
//				}
//			}.schedule();
//
//			setNotReady();
//			this.runLabel = 1;
//			System.out.println("yield with runLabel = " + this.runLabel);
//			yield(1);
//			label(1);
//			
//			System.out.println("  " + $(_ld33_p7) + $(_ld32_p6) + $(_ld31_p5) + $(_ld30_p4) + $(_ld29_p3) + $(_ld28_p2) + $(_ld27_p1) + $(_ld26_p0) + " (In Carry: " + $(_ld51_inC) + ")");
//			System.out.println("+ " + $(_ld41_q7) + $(_ld40_q6) + $(_ld39_q5) + $(_ld38_q4) + $(_ld37_q3) + $(_ld36_q2) + $(_ld35_q1) + $(_ld34_q0));
//			System.out.println("----------");
//			System.out.println("  " + $(_ld49_f7) + $(_ld48_f6) + $(_ld47_f5) + $(_ld46_f4) + $(_ld45_f3) + $(_ld44_f2) + $(_ld43_f1) + $(_ld42_f0));
//
//			System.out.println("Carry was: " + $(_ld50_c));
//			
//			
//	      	terminate();
//		}
//	}
//	
//	public static int $(boolean b) {
//		return b?1:0;
//	}
//	public static void main(String[] args) {
//		Scheduler scheduler = new Scheduler();
//		Process.scheduler = scheduler;
//		System.out.println("Added _main process to scheduler...");
//
//		(new fullAdderCDS.myMain()).schedule();
//
//		System.out.println("Starting the scheduler...");
//		Process.scheduler.start();
//
//		System.out.println("Scheduler thread.start() done.");
//	}
}