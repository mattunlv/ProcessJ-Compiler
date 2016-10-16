package Generated.fullAdder;
import java.util.*;
import ProcessJ.runtime.*;

public class fullAdder {
	public static class andGate extends PJProcess {
		PJChannel<Boolean> _pd$in1;
		PJChannel<Boolean> _pd$in2;
		PJChannel<Boolean> _pd$out;
		boolean _ld0$x;
		boolean _ld1$y;

	    public andGate(PJChannel<Boolean> _pd$in1, PJChannel<Boolean> _pd$in2, PJChannel<Boolean> _pd$out) {
	    	this._pd$in2 = _pd$in2;
	    	this._pd$in1 = _pd$in1;
	    	this._pd$out = _pd$out;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 6: resume(6); break;
			    case 7: resume(7); break;
			}
			_ld0$x = false;
			_ld1$y = false;
			final PJPar par1 = new PJPar(2, this);

			new PJProcess(){
				@Override
				public synchronized void run() {
					switch(this.runLabel) {
						case 0: break;
					    case 2: resume(2); break;
					    case 3: resume(3); break;
					}
					label(2);
					if(_pd$in1.isReadyToRead(this)) {
						_ld0$x = _pd$in1.read(this);
						this.runLabel = 3;
						yield();
					} else {
						setNotReady();
						_pd$in1.addReader(this);
						this.runLabel = 2;
						yield();
					}
					label(3);
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
					    case 4: resume(4); break;
					    case 5: resume(5); break;
					}
					label(4);
					if(_pd$in2.isReadyToRead(this)) {
						_ld1$y = _pd$in2.read(this);
						this.runLabel = 5;
						yield();
					} else {
						setNotReady();
						_pd$in2.addReader(this);
						this.runLabel = 4;
						yield();
					}
					label(5);
			      	terminate();
				}

				@Override
				public void finalize() {
					par1.decrement();	
				}
			}.schedule();

			setNotReady();
			this.runLabel = 1;
			yield();
			label(1);

			label(6);
			if (_pd$out.isReadyToWrite()) {
				_pd$out.write(this, (_ld0$x && _ld1$y));
				this.runLabel = 7;
				yield();
			} else {
				setNotReady();
				this.runLabel = 6;
				yield();
			}
			label(7);
			terminate();
		}
	}


	public static class orGate extends PJProcess {
		PJChannel<Boolean> _pd$in1;
		PJChannel<Boolean> _pd$in2;
		PJChannel<Boolean> _pd$out;
		boolean _ld0$x;
		boolean _ld1$y;

	    public orGate(PJChannel<Boolean> _pd$in1, PJChannel<Boolean> _pd$in2, PJChannel<Boolean> _pd$out) {
	    	this._pd$in2 = _pd$in2;
	    	this._pd$in1 = _pd$in1;
	    	this._pd$out = _pd$out;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 6: resume(6); break;
			    case 7: resume(7); break;
			}
			_ld0$x = false;
			_ld1$y = false;
			final PJPar par1 = new PJPar(2, this);

			new PJProcess(){
				@Override
				public synchronized void run() {
					switch(this.runLabel) {
						case 0: break;
					    case 2: resume(2); break;
					    case 3: resume(3); break;
					}
					label(2);
					if(_pd$in1.isReadyToRead(this)) {
						_ld0$x = _pd$in1.read(this);
						this.runLabel = 3;
						yield();
					} else {
						setNotReady();
						_pd$in1.addReader(this);
						this.runLabel = 2;
						yield();
					}
					label(3);
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
					    case 4: resume(4); break;
					    case 5: resume(5); break;
					}
					label(4);
					if(_pd$in2.isReadyToRead(this)) {
						_ld1$y = _pd$in2.read(this);
						this.runLabel = 5;
						yield();
					} else {
						setNotReady();
						_pd$in2.addReader(this);
						this.runLabel = 4;
						yield();
					}
					label(5);
			      	terminate();
				}

				@Override
				public void finalize() {
					par1.decrement();	
				}
			}.schedule();

			setNotReady();
			this.runLabel = 1;
			yield();
			label(1);

			label(6);
			if (_pd$out.isReadyToWrite()) {
				_pd$out.write(this, (_ld0$x || _ld1$y));
				this.runLabel = 7;
				yield();
			} else {
				setNotReady();
				this.runLabel = 6;
				yield();
			}
			label(7);
			terminate();
		}
	}


	public static class notGate extends PJProcess {
		PJChannel<Boolean> _pd$in;
		PJChannel<Boolean> _pd$out;
		boolean _ld0$x;

	    public notGate(PJChannel<Boolean> _pd$in, PJChannel<Boolean> _pd$out) {
	    	this._pd$in = _pd$in;
	    	this._pd$out = _pd$out;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			    case 3: resume(3); break;
			    case 4: resume(4); break;
			}
			_ld0$x = false;
			label(1);
			if(_pd$in.isReadyToRead(this)) {
				_ld0$x = _pd$in.read(this);
				this.runLabel = 2;
				yield();
			} else {
				setNotReady();
				_pd$in.addReader(this);
				this.runLabel = 1;
				yield();
			}
			label(2);

			label(3);
			if (_pd$out.isReadyToWrite()) {
				_pd$out.write(this, !_ld0$x);
				this.runLabel = 4;
				yield();
			} else {
				setNotReady();
				this.runLabel = 3;
				yield();
			}
			label(4);
			terminate();
		}
	}


	public static class nandGate extends PJProcess {
		PJChannel<Boolean> _pd$in1;
		PJChannel<Boolean> _pd$in2;
		PJChannel<Boolean> _pd$out;
		PJOne2OneChannel<Boolean> _ld0$a;

	    public nandGate(PJChannel<Boolean> _pd$in1, PJChannel<Boolean> _pd$in2, PJChannel<Boolean> _pd$out) {
	    	this._pd$in2 = _pd$in2;
	    	this._pd$in1 = _pd$in1;
	    	this._pd$out = _pd$out;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			_ld0$a = new PJOne2OneChannel<Boolean>();
			final PJPar par1 = new PJPar(2, this);

			(new fullAdder.andGate( _pd$in1, _pd$in2, _ld0$a ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.notGate( _ld0$a, _pd$out ){
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


	public static class muxGate extends PJProcess {
		PJChannel<Boolean> _pd$in;
		PJChannel<Boolean> _pd$out1;
		PJChannel<Boolean> _pd$out2;
		boolean _ld0$x;

	    public muxGate(PJChannel<Boolean> _pd$in, PJChannel<Boolean> _pd$out1, PJChannel<Boolean> _pd$out2) {
	    	this._pd$out2 = _pd$out2;
	    	this._pd$in = _pd$in;
	    	this._pd$out1 = _pd$out1;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			    case 3: resume(3); break;
			}
			_ld0$x = false;
			label(1);
			if(_pd$in.isReadyToRead(this)) {
				_ld0$x = _pd$in.read(this);
				this.runLabel = 2;
				yield();
			} else {
				setNotReady();
				_pd$in.addReader(this);
				this.runLabel = 1;
				yield();
			}
			label(2);
			final PJPar par1 = new PJPar(2, this);

			new PJProcess(){
				@Override
				public synchronized void run() {
					switch(this.runLabel) {
						case 0: break;
					    case 4: resume(4); break;
					    case 5: resume(5); break;
					}

					label(4);
					if (_pd$out1.isReadyToWrite()) {
						_pd$out1.write(this, _ld0$x);
						this.runLabel = 5;
						yield();
					} else {
						setNotReady();
						this.runLabel = 4;
						yield();
					}
					label(5);
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
					    case 6: resume(6); break;
					    case 7: resume(7); break;
					}

					label(6);
					if (_pd$out2.isReadyToWrite()) {
						_pd$out2.write(this, _ld0$x);
						this.runLabel = 7;
						yield();
					} else {
						setNotReady();
						this.runLabel = 6;
						yield();
					}
					label(7);
			      	terminate();
				}

				@Override
				public void finalize() {
					par1.decrement();	
				}
			}.schedule();

			setNotReady();
			this.runLabel = 3;
			yield();
			label(3);
			terminate();
		}
	}


	public static class xorGate extends PJProcess {
		PJChannel<Boolean> _pd$in1;
		PJChannel<Boolean> _pd$in2;
		PJChannel<Boolean> _pd$out;
		PJOne2OneChannel<Boolean> _ld0$a;
		PJOne2OneChannel<Boolean> _ld1$b;
		PJOne2OneChannel<Boolean> _ld2$c;
		PJOne2OneChannel<Boolean> _ld3$d;
		PJOne2OneChannel<Boolean> _ld4$e;
		PJOne2OneChannel<Boolean> _ld5$f;
		PJOne2OneChannel<Boolean> _ld6$g;
		PJOne2OneChannel<Boolean> _ld7$h;
		PJOne2OneChannel<Boolean> _ld8$i;

	    public xorGate(PJChannel<Boolean> _pd$in1, PJChannel<Boolean> _pd$in2, PJChannel<Boolean> _pd$out) {
	    	this._pd$in2 = _pd$in2;
	    	this._pd$in1 = _pd$in1;
	    	this._pd$out = _pd$out;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			_ld0$a = new PJOne2OneChannel<Boolean>();
			_ld1$b = new PJOne2OneChannel<Boolean>();
			_ld2$c = new PJOne2OneChannel<Boolean>();
			_ld3$d = new PJOne2OneChannel<Boolean>();
			_ld4$e = new PJOne2OneChannel<Boolean>();
			_ld5$f = new PJOne2OneChannel<Boolean>();
			_ld6$g = new PJOne2OneChannel<Boolean>();
			_ld7$h = new PJOne2OneChannel<Boolean>();
			_ld8$i = new PJOne2OneChannel<Boolean>();
			final PJPar par1 = new PJPar(7, this);

			(new fullAdder.muxGate( _pd$in1, _ld0$a, _ld1$b ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.muxGate( _pd$in2, _ld2$c, _ld3$d ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.nandGate( _ld1$b, _ld3$d, _ld4$e ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.muxGate( _ld4$e, _ld5$f, _ld6$g ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.nandGate( _ld0$a, _ld5$f, _ld7$h ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.nandGate( _ld2$c, _ld6$g, _ld8$i ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.nandGate( _ld7$h, _ld8$i, _pd$out ){
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


	public static class oneBitAdder extends PJProcess {
		PJChannel<Boolean> _pd$in1;
		PJChannel<Boolean> _pd$in2;
		PJChannel<Boolean> _pd$in3;
		PJChannel<Boolean> _pd$result;
		PJChannel<Boolean> _pd$carry;
		PJOne2OneChannel<Boolean> _ld0$a;
		PJOne2OneChannel<Boolean> _ld1$b;
		PJOne2OneChannel<Boolean> _ld2$c;
		PJOne2OneChannel<Boolean> _ld3$d;
		PJOne2OneChannel<Boolean> _ld4$e;
		PJOne2OneChannel<Boolean> _ld5$f;
		PJOne2OneChannel<Boolean> _ld6$g;
		PJOne2OneChannel<Boolean> _ld7$h;
		PJOne2OneChannel<Boolean> _ld8$i;
		PJOne2OneChannel<Boolean> _ld9$j;
		PJOne2OneChannel<Boolean> _ld10$k;

	    public oneBitAdder(PJChannel<Boolean> _pd$in1, PJChannel<Boolean> _pd$in2, PJChannel<Boolean> _pd$in3, PJChannel<Boolean> _pd$result, PJChannel<Boolean> _pd$carry) {
	    	this._pd$result = _pd$result;
	    	this._pd$carry = _pd$carry;
	    	this._pd$in2 = _pd$in2;
	    	this._pd$in1 = _pd$in1;
	    	this._pd$in3 = _pd$in3;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			_ld0$a = new PJOne2OneChannel<Boolean>();
			_ld1$b = new PJOne2OneChannel<Boolean>();
			_ld2$c = new PJOne2OneChannel<Boolean>();
			_ld3$d = new PJOne2OneChannel<Boolean>();
			_ld4$e = new PJOne2OneChannel<Boolean>();
			_ld5$f = new PJOne2OneChannel<Boolean>();
			_ld6$g = new PJOne2OneChannel<Boolean>();
			_ld7$h = new PJOne2OneChannel<Boolean>();
			_ld8$i = new PJOne2OneChannel<Boolean>();
			_ld9$j = new PJOne2OneChannel<Boolean>();
			_ld10$k = new PJOne2OneChannel<Boolean>();
			final PJPar par1 = new PJPar(9, this);

			(new fullAdder.muxGate( _pd$in1, _ld0$a, _ld1$b ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.muxGate( _pd$in2, _ld2$c, _ld3$d ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.xorGate( _ld0$a, _ld2$c, _ld4$e ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.muxGate( _ld4$e, _ld5$f, _ld6$g ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.muxGate( _pd$in3, _ld7$h, _ld8$i ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.xorGate( _ld5$f, _ld7$h, _pd$result ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.andGate( _ld6$g, _ld8$i, _ld9$j ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.andGate( _ld1$b, _ld3$d, _ld10$k ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.orGate( _ld9$j, _ld10$k, _pd$carry ){
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


	public static class fourBitAdder extends PJProcess {
		PJChannel<Boolean> _pd$inA0;
		PJChannel<Boolean> _pd$inA1;
		PJChannel<Boolean> _pd$inA2;
		PJChannel<Boolean> _pd$inA3;
		PJChannel<Boolean> _pd$inB0;
		PJChannel<Boolean> _pd$inB1;
		PJChannel<Boolean> _pd$inB2;
		PJChannel<Boolean> _pd$inB3;
		PJChannel<Boolean> _pd$inCarry;
		PJChannel<Boolean> _pd$result0;
		PJChannel<Boolean> _pd$result1;
		PJChannel<Boolean> _pd$result2;
		PJChannel<Boolean> _pd$result3;
		PJChannel<Boolean> _pd$carry;
		PJOne2OneChannel<Boolean> _ld0$a;
		PJOne2OneChannel<Boolean> _ld1$b;
		PJOne2OneChannel<Boolean> _ld2$c;

	    public fourBitAdder(PJChannel<Boolean> _pd$inA0, PJChannel<Boolean> _pd$inA1, PJChannel<Boolean> _pd$inA2, PJChannel<Boolean> _pd$inA3, PJChannel<Boolean> _pd$inB0, PJChannel<Boolean> _pd$inB1, PJChannel<Boolean> _pd$inB2, PJChannel<Boolean> _pd$inB3, PJChannel<Boolean> _pd$inCarry, PJChannel<Boolean> _pd$result0, PJChannel<Boolean> _pd$result1, PJChannel<Boolean> _pd$result2, PJChannel<Boolean> _pd$result3, PJChannel<Boolean> _pd$carry) {
	    	this._pd$inB0 = _pd$inB0;
	    	this._pd$inA1 = _pd$inA1;
	    	this._pd$inB2 = _pd$inB2;
	    	this._pd$inA0 = _pd$inA0;
	    	this._pd$inB1 = _pd$inB1;
	    	this._pd$inA3 = _pd$inA3;
	    	this._pd$inA2 = _pd$inA2;
	    	this._pd$inB3 = _pd$inB3;
	    	this._pd$inCarry = _pd$inCarry;
	    	this._pd$result0 = _pd$result0;
	    	this._pd$carry = _pd$carry;
	    	this._pd$result3 = _pd$result3;
	    	this._pd$result2 = _pd$result2;
	    	this._pd$result1 = _pd$result1;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			_ld0$a = new PJOne2OneChannel<Boolean>();
			_ld1$b = new PJOne2OneChannel<Boolean>();
			_ld2$c = new PJOne2OneChannel<Boolean>();
			final PJPar par1 = new PJPar(4, this);

			(new fullAdder.oneBitAdder( _pd$inA0, _pd$inB0, _pd$inCarry, _pd$result0, _ld0$a ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.oneBitAdder( _pd$inA1, _pd$inB1, _ld0$a, _pd$result1, _ld1$b ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.oneBitAdder( _pd$inA2, _pd$inB2, _ld1$b, _pd$result2, _ld2$c ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.oneBitAdder( _pd$inA3, _pd$inB3, _ld2$c, _pd$result3, _pd$carry ){
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


	public static class eightBitAdder extends PJProcess {
		PJChannel<Boolean> _pd$inA0;
		PJChannel<Boolean> _pd$inA1;
		PJChannel<Boolean> _pd$inA2;
		PJChannel<Boolean> _pd$inA3;
		PJChannel<Boolean> _pd$inA4;
		PJChannel<Boolean> _pd$inA5;
		PJChannel<Boolean> _pd$inA6;
		PJChannel<Boolean> _pd$inA7;
		PJChannel<Boolean> _pd$inB0;
		PJChannel<Boolean> _pd$inB1;
		PJChannel<Boolean> _pd$inB2;
		PJChannel<Boolean> _pd$inB3;
		PJChannel<Boolean> _pd$inB4;
		PJChannel<Boolean> _pd$inB5;
		PJChannel<Boolean> _pd$inB6;
		PJChannel<Boolean> _pd$inB7;
		PJChannel<Boolean> _pd$inCarry;
		PJChannel<Boolean> _pd$result0;
		PJChannel<Boolean> _pd$result1;
		PJChannel<Boolean> _pd$result2;
		PJChannel<Boolean> _pd$result3;
		PJChannel<Boolean> _pd$result4;
		PJChannel<Boolean> _pd$result5;
		PJChannel<Boolean> _pd$result6;
		PJChannel<Boolean> _pd$result7;
		PJChannel<Boolean> _pd$outCarry;
		PJOne2OneChannel<Boolean> _ld0$a;

	    public eightBitAdder(PJChannel<Boolean> _pd$inA0, PJChannel<Boolean> _pd$inA1, PJChannel<Boolean> _pd$inA2, PJChannel<Boolean> _pd$inA3, PJChannel<Boolean> _pd$inA4, PJChannel<Boolean> _pd$inA5, PJChannel<Boolean> _pd$inA6, PJChannel<Boolean> _pd$inA7, PJChannel<Boolean> _pd$inB0, PJChannel<Boolean> _pd$inB1, PJChannel<Boolean> _pd$inB2, PJChannel<Boolean> _pd$inB3, PJChannel<Boolean> _pd$inB4, PJChannel<Boolean> _pd$inB5, PJChannel<Boolean> _pd$inB6, PJChannel<Boolean> _pd$inB7, PJChannel<Boolean> _pd$inCarry, PJChannel<Boolean> _pd$result0, PJChannel<Boolean> _pd$result1, PJChannel<Boolean> _pd$result2, PJChannel<Boolean> _pd$result3, PJChannel<Boolean> _pd$result4, PJChannel<Boolean> _pd$result5, PJChannel<Boolean> _pd$result6, PJChannel<Boolean> _pd$result7, PJChannel<Boolean> _pd$outCarry) {
	    	this._pd$inA1 = _pd$inA1;
	    	this._pd$inA0 = _pd$inA0;
	    	this._pd$inA3 = _pd$inA3;
	    	this._pd$outCarry = _pd$outCarry;
	    	this._pd$inA2 = _pd$inA2;
	    	this._pd$inA5 = _pd$inA5;
	    	this._pd$inA4 = _pd$inA4;
	    	this._pd$inA7 = _pd$inA7;
	    	this._pd$inA6 = _pd$inA6;
	    	this._pd$result7 = _pd$result7;
	    	this._pd$result6 = _pd$result6;
	    	this._pd$result5 = _pd$result5;
	    	this._pd$result0 = _pd$result0;
	    	this._pd$result4 = _pd$result4;
	    	this._pd$result3 = _pd$result3;
	    	this._pd$result2 = _pd$result2;
	    	this._pd$result1 = _pd$result1;
	    	this._pd$inB0 = _pd$inB0;
	    	this._pd$inB2 = _pd$inB2;
	    	this._pd$inB1 = _pd$inB1;
	    	this._pd$inB4 = _pd$inB4;
	    	this._pd$inB3 = _pd$inB3;
	    	this._pd$inB6 = _pd$inB6;
	    	this._pd$inB5 = _pd$inB5;
	    	this._pd$inB7 = _pd$inB7;
	    	this._pd$inCarry = _pd$inCarry;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			_ld0$a = new PJOne2OneChannel<Boolean>();
			final PJPar par1 = new PJPar(2, this);

			(new fullAdder.fourBitAdder( _pd$inA0, _pd$inA1, _pd$inA2, _pd$inA3, _pd$inB0, _pd$inB1, _pd$inB2, _pd$inB3, _pd$inCarry, _pd$result0, _pd$result1, _pd$result2, _pd$result3, _ld0$a ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new fullAdder.fourBitAdder( _pd$inA4, _pd$inA5, _pd$inA6, _pd$inA7, _pd$inB4, _pd$inB5, _pd$inB6, _pd$inB7, _ld0$a, _pd$result4, _pd$result5, _pd$result6, _pd$result7, _pd$outCarry ){
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


	public static class myMain extends PJProcess {
		PJOne2OneChannel<Boolean> _ld0$a0;
		PJOne2OneChannel<Boolean> _ld1$a1;
		PJOne2OneChannel<Boolean> _ld2$a2;
		PJOne2OneChannel<Boolean> _ld3$a3;
		PJOne2OneChannel<Boolean> _ld4$a4;
		PJOne2OneChannel<Boolean> _ld5$a5;
		PJOne2OneChannel<Boolean> _ld6$a6;
		PJOne2OneChannel<Boolean> _ld7$a7;
		PJOne2OneChannel<Boolean> _ld8$b0;
		PJOne2OneChannel<Boolean> _ld9$b1;
		PJOne2OneChannel<Boolean> _ld10$b2;
		PJOne2OneChannel<Boolean> _ld11$b3;
		PJOne2OneChannel<Boolean> _ld12$b4;
		PJOne2OneChannel<Boolean> _ld13$b5;
		PJOne2OneChannel<Boolean> _ld14$b6;
		PJOne2OneChannel<Boolean> _ld15$b7;
		PJOne2OneChannel<Boolean> _ld16$r0;
		PJOne2OneChannel<Boolean> _ld17$r1;
		PJOne2OneChannel<Boolean> _ld18$r2;
		PJOne2OneChannel<Boolean> _ld19$r3;
		PJOne2OneChannel<Boolean> _ld20$r4;
		PJOne2OneChannel<Boolean> _ld21$r5;
		PJOne2OneChannel<Boolean> _ld22$r6;
		PJOne2OneChannel<Boolean> _ld23$r7;
		PJOne2OneChannel<Boolean> _ld24$inCarry;
		PJOne2OneChannel<Boolean> _ld25$outCarry;
		boolean _ld26$p0;
		boolean _ld27$p1;
		boolean _ld28$p2;
		boolean _ld29$p3;
		boolean _ld30$p4;
		boolean _ld31$p5;
		boolean _ld32$p6;
		boolean _ld33$p7;
		boolean _ld34$q0;
		boolean _ld35$q1;
		boolean _ld36$q2;
		boolean _ld37$q3;
		boolean _ld38$q4;
		boolean _ld39$q5;
		boolean _ld40$q6;
		boolean _ld41$q7;
		boolean _ld42$f0;
		boolean _ld43$f1;
		boolean _ld44$f2;
		boolean _ld45$f3;
		boolean _ld46$f4;
		boolean _ld47$f5;
		boolean _ld48$f6;
		boolean _ld49$f7;
		boolean _ld50$c;
		boolean _ld51$inC;

	    public myMain() {
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			_ld0$a0 = new PJOne2OneChannel<Boolean>();
			_ld1$a1 = new PJOne2OneChannel<Boolean>();
			_ld2$a2 = new PJOne2OneChannel<Boolean>();
			_ld3$a3 = new PJOne2OneChannel<Boolean>();
			_ld4$a4 = new PJOne2OneChannel<Boolean>();
			_ld5$a5 = new PJOne2OneChannel<Boolean>();
			_ld6$a6 = new PJOne2OneChannel<Boolean>();
			_ld7$a7 = new PJOne2OneChannel<Boolean>();
			_ld8$b0 = new PJOne2OneChannel<Boolean>();
			_ld9$b1 = new PJOne2OneChannel<Boolean>();
			_ld10$b2 = new PJOne2OneChannel<Boolean>();
			_ld11$b3 = new PJOne2OneChannel<Boolean>();
			_ld12$b4 = new PJOne2OneChannel<Boolean>();
			_ld13$b5 = new PJOne2OneChannel<Boolean>();
			_ld14$b6 = new PJOne2OneChannel<Boolean>();
			_ld15$b7 = new PJOne2OneChannel<Boolean>();
			_ld16$r0 = new PJOne2OneChannel<Boolean>();
			_ld17$r1 = new PJOne2OneChannel<Boolean>();
			_ld18$r2 = new PJOne2OneChannel<Boolean>();
			_ld19$r3 = new PJOne2OneChannel<Boolean>();
			_ld20$r4 = new PJOne2OneChannel<Boolean>();
			_ld21$r5 = new PJOne2OneChannel<Boolean>();
			_ld22$r6 = new PJOne2OneChannel<Boolean>();
			_ld23$r7 = new PJOne2OneChannel<Boolean>();
			_ld24$inCarry = new PJOne2OneChannel<Boolean>();
			_ld25$outCarry = new PJOne2OneChannel<Boolean>();
			_ld33$p7 = false;
			_ld32$p6 = true;
			_ld31$p5 = false;
			_ld30$p4 = false;
			_ld29$p3 = false;
			_ld28$p2 = true;
			_ld27$p1 = false;
			_ld26$p0 = false;
			_ld41$q7 = true;
			_ld40$q6 = false;
			_ld39$q5 = true;
			_ld38$q4 = false;
			_ld37$q3 = true;
			_ld36$q2 = false;
			_ld35$q1 = true;
			_ld34$q0 = true;
			_ld51$inC = true;
			final PJPar par1 = new PJPar(27, this);

			new PJProcess(){
				@Override
				public synchronized void run() {
					switch(this.runLabel) {
						case 0: break;
					    case 2: resume(2); break;
					    case 3: resume(3); break;
					}

					label(2);
					if (_ld7$a7.isReadyToWrite()) {
						_ld7$a7.write(this, _ld33$p7);
						this.runLabel = 3;
						yield();
					} else {
						setNotReady();
						this.runLabel = 2;
						yield();
					}
					label(3);
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
					    case 4: resume(4); break;
					    case 5: resume(5); break;
					}

					label(4);
					if (_ld6$a6.isReadyToWrite()) {
						_ld6$a6.write(this, _ld32$p6);
						this.runLabel = 5;
						yield();
					} else {
						setNotReady();
						this.runLabel = 4;
						yield();
					}
					label(5);
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
					    case 6: resume(6); break;
					    case 7: resume(7); break;
					}

					label(6);
					if (_ld5$a5.isReadyToWrite()) {
						_ld5$a5.write(this, _ld31$p5);
						this.runLabel = 7;
						yield();
					} else {
						setNotReady();
						this.runLabel = 6;
						yield();
					}
					label(7);
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
					    case 8: resume(8); break;
					    case 9: resume(9); break;
					}

					label(8);
					if (_ld4$a4.isReadyToWrite()) {
						_ld4$a4.write(this, _ld30$p4);
						this.runLabel = 9;
						yield();
					} else {
						setNotReady();
						this.runLabel = 8;
						yield();
					}
					label(9);
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
					    case 10: resume(10); break;
					    case 11: resume(11); break;
					}

					label(10);
					if (_ld3$a3.isReadyToWrite()) {
						_ld3$a3.write(this, _ld29$p3);
						this.runLabel = 11;
						yield();
					} else {
						setNotReady();
						this.runLabel = 10;
						yield();
					}
					label(11);
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
					    case 12: resume(12); break;
					    case 13: resume(13); break;
					}

					label(12);
					if (_ld2$a2.isReadyToWrite()) {
						_ld2$a2.write(this, _ld28$p2);
						this.runLabel = 13;
						yield();
					} else {
						setNotReady();
						this.runLabel = 12;
						yield();
					}
					label(13);
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
					    case 14: resume(14); break;
					    case 15: resume(15); break;
					}

					label(14);
					if (_ld1$a1.isReadyToWrite()) {
						_ld1$a1.write(this, _ld27$p1);
						this.runLabel = 15;
						yield();
					} else {
						setNotReady();
						this.runLabel = 14;
						yield();
					}
					label(15);
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
					    case 16: resume(16); break;
					    case 17: resume(17); break;
					}

					label(16);
					if (_ld0$a0.isReadyToWrite()) {
						_ld0$a0.write(this, _ld26$p0);
						this.runLabel = 17;
						yield();
					} else {
						setNotReady();
						this.runLabel = 16;
						yield();
					}
					label(17);
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
					    case 18: resume(18); break;
					    case 19: resume(19); break;
					}

					label(18);
					if (_ld15$b7.isReadyToWrite()) {
						_ld15$b7.write(this, _ld41$q7);
						this.runLabel = 19;
						yield();
					} else {
						setNotReady();
						this.runLabel = 18;
						yield();
					}
					label(19);
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
					    case 20: resume(20); break;
					    case 21: resume(21); break;
					}

					label(20);
					if (_ld14$b6.isReadyToWrite()) {
						_ld14$b6.write(this, _ld40$q6);
						this.runLabel = 21;
						yield();
					} else {
						setNotReady();
						this.runLabel = 20;
						yield();
					}
					label(21);
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
					    case 22: resume(22); break;
					    case 23: resume(23); break;
					}

					label(22);
					if (_ld13$b5.isReadyToWrite()) {
						_ld13$b5.write(this, _ld39$q5);
						this.runLabel = 23;
						yield();
					} else {
						setNotReady();
						this.runLabel = 22;
						yield();
					}
					label(23);
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
					    case 24: resume(24); break;
					    case 25: resume(25); break;
					}

					label(24);
					if (_ld12$b4.isReadyToWrite()) {
						_ld12$b4.write(this, _ld38$q4);
						this.runLabel = 25;
						yield();
					} else {
						setNotReady();
						this.runLabel = 24;
						yield();
					}
					label(25);
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
					    case 26: resume(26); break;
					    case 27: resume(27); break;
					}

					label(26);
					if (_ld11$b3.isReadyToWrite()) {
						_ld11$b3.write(this, _ld37$q3);
						this.runLabel = 27;
						yield();
					} else {
						setNotReady();
						this.runLabel = 26;
						yield();
					}
					label(27);
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
					    case 28: resume(28); break;
					    case 29: resume(29); break;
					}

					label(28);
					if (_ld10$b2.isReadyToWrite()) {
						_ld10$b2.write(this, _ld36$q2);
						this.runLabel = 29;
						yield();
					} else {
						setNotReady();
						this.runLabel = 28;
						yield();
					}
					label(29);
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
					    case 30: resume(30); break;
					    case 31: resume(31); break;
					}

					label(30);
					if (_ld9$b1.isReadyToWrite()) {
						_ld9$b1.write(this, _ld35$q1);
						this.runLabel = 31;
						yield();
					} else {
						setNotReady();
						this.runLabel = 30;
						yield();
					}
					label(31);
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
					    case 32: resume(32); break;
					    case 33: resume(33); break;
					}

					label(32);
					if (_ld8$b0.isReadyToWrite()) {
						_ld8$b0.write(this, _ld34$q0);
						this.runLabel = 33;
						yield();
					} else {
						setNotReady();
						this.runLabel = 32;
						yield();
					}
					label(33);
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
					    case 34: resume(34); break;
					    case 35: resume(35); break;
					}

					label(34);
					if (_ld24$inCarry.isReadyToWrite()) {
						_ld24$inCarry.write(this, _ld51$inC);
						this.runLabel = 35;
						yield();
					} else {
						setNotReady();
						this.runLabel = 34;
						yield();
					}
					label(35);
			      	terminate();
				}

				@Override
				public void finalize() {
					par1.decrement();	
				}
			}.schedule();

			(new fullAdder.eightBitAdder( _ld0$a0, _ld1$a1, _ld2$a2, _ld3$a3, _ld4$a4, _ld5$a5, _ld6$a6, _ld7$a7, _ld8$b0, _ld9$b1, _ld10$b2, _ld11$b3, _ld12$b4, _ld13$b5, _ld14$b6, _ld15$b7, _ld24$inCarry, _ld16$r0, _ld17$r1, _ld18$r2, _ld19$r3, _ld20$r4, _ld21$r5, _ld22$r6, _ld23$r7, _ld25$outCarry ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			new PJProcess(){
				@Override
				public synchronized void run() {
					switch(this.runLabel) {
						case 0: break;
					    case 36: resume(36); break;
					    case 37: resume(37); break;
					}
					label(36);
					if(_ld16$r0.isReadyToRead(this)) {
						_ld42$f0 = _ld16$r0.read(this);
						this.runLabel = 37;
						yield();
					} else {
						setNotReady();
						_ld16$r0.addReader(this);
						this.runLabel = 36;
						yield();
					}
					label(37);
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
					    case 38: resume(38); break;
					    case 39: resume(39); break;
					}
					label(38);
					if(_ld17$r1.isReadyToRead(this)) {
						_ld43$f1 = _ld17$r1.read(this);
						this.runLabel = 39;
						yield();
					} else {
						setNotReady();
						_ld17$r1.addReader(this);
						this.runLabel = 38;
						yield();
					}
					label(39);
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
					    case 40: resume(40); break;
					    case 41: resume(41); break;
					}
					label(40);
					if(_ld18$r2.isReadyToRead(this)) {
						_ld44$f2 = _ld18$r2.read(this);
						this.runLabel = 41;
						yield();
					} else {
						setNotReady();
						_ld18$r2.addReader(this);
						this.runLabel = 40;
						yield();
					}
					label(41);
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
					    case 42: resume(42); break;
					    case 43: resume(43); break;
					}
					label(42);
					if(_ld19$r3.isReadyToRead(this)) {
						_ld45$f3 = _ld19$r3.read(this);
						this.runLabel = 43;
						yield();
					} else {
						setNotReady();
						_ld19$r3.addReader(this);
						this.runLabel = 42;
						yield();
					}
					label(43);
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
					    case 44: resume(44); break;
					    case 45: resume(45); break;
					}
					label(44);
					if(_ld20$r4.isReadyToRead(this)) {
						_ld46$f4 = _ld20$r4.read(this);
						this.runLabel = 45;
						yield();
					} else {
						setNotReady();
						_ld20$r4.addReader(this);
						this.runLabel = 44;
						yield();
					}
					label(45);
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
					    case 46: resume(46); break;
					    case 47: resume(47); break;
					}
					label(46);
					if(_ld21$r5.isReadyToRead(this)) {
						_ld47$f5 = _ld21$r5.read(this);
						this.runLabel = 47;
						yield();
					} else {
						setNotReady();
						_ld21$r5.addReader(this);
						this.runLabel = 46;
						yield();
					}
					label(47);
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
					    case 48: resume(48); break;
					    case 49: resume(49); break;
					}
					label(48);
					if(_ld22$r6.isReadyToRead(this)) {
						_ld48$f6 = _ld22$r6.read(this);
						this.runLabel = 49;
						yield();
					} else {
						setNotReady();
						_ld22$r6.addReader(this);
						this.runLabel = 48;
						yield();
					}
					label(49);
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
					    case 50: resume(50); break;
					    case 51: resume(51); break;
					}
					label(50);
					if(_ld23$r7.isReadyToRead(this)) {
						_ld49$f7 = _ld23$r7.read(this);
						this.runLabel = 51;
						yield();
					} else {
						setNotReady();
						_ld23$r7.addReader(this);
						this.runLabel = 50;
						yield();
					}
					label(51);
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
					    case 52: resume(52); break;
					    case 53: resume(53); break;
					}
					label(52);
					if(_ld25$outCarry.isReadyToRead(this)) {
						_ld50$c = _ld25$outCarry.read(this);
						this.runLabel = 53;
						yield();
					} else {
						setNotReady();
						_ld25$outCarry.addReader(this);
						this.runLabel = 52;
						yield();
					}
					label(53);
			      	terminate();
				}

				@Override
				public void finalize() {
					par1.decrement();	
				}
			}.schedule();

			setNotReady();
			this.runLabel = 1;
			yield();
			label(1);
			std.io.println( ((((((((((("  " + _ld33$p7) + _ld32$p6) + _ld31$p5) + _ld30$p4) + _ld29$p3) + _ld28$p2) + _ld27$p1) + _ld26$p0) + " (In Carry: ") + _ld51$inC) + ")") );
			std.io.println( (((((((("+ " + _ld41$q7) + _ld40$q6) + _ld39$q5) + _ld38$q4) + _ld37$q3) + _ld36$q2) + _ld35$q1) + _ld34$q0) );
			std.io.println( "----------" );
			std.io.println( (((((((("  " + _ld49$f7) + _ld48$f6) + _ld47$f5) + _ld46$f4) + _ld45$f3) + _ld44$f2) + _ld43$f1) + _ld42$f0) );
			std.io.println( ("Carry was: " + _ld50$c) );
			terminate();
		}
	}


	public static int $(boolean _pd$b) {
		return _pd$b ? 1 : 0;
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
			}
			final PJPar par1 = new PJPar(1, this);

			(new fullAdder.myMain(){
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

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();

		PJProcess.scheduler = scheduler;
		System.out.println("Added _main process to scheduler...");

		(new fullAdder.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
	public static boolean getTrue() {
		return true;
	}
}