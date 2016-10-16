import java.util.*;
import ProcessJ.runtime.*;

public class mandelbrot {
	public static int cal_pixel(double _pd$real, double _pd$imag) {
		int _ld0$count;
		int _ld1$max;
		double _ld2$z_real;
		double _ld3$z_imag;
		double _ld4$temp;
		double _ld5$lengthsq;
		_ld1$max = 256;
		_ld2$z_real = 0;
		_ld3$z_imag = 0;
		_ld0$count = 0;
		do {
		  _ld4$temp = (((_ld2$z_real * _ld2$z_real) - (_ld3$z_imag * _ld3$z_imag)) + _pd$real);
		  _ld3$z_imag = (((2 * _ld2$z_real) * _ld3$z_imag) + _pd$imag);
		  _ld2$z_real = _ld4$temp;
		  _ld5$lengthsq = ((_ld2$z_real * _ld2$z_real) + (_ld3$z_imag * _ld3$z_imag));
		  _ld0$count++;
		} while( ((_ld5$lengthsq < 4.0) && (_ld0$count < _ld1$max)) );
		return _ld0$count;
	}
	public static class main extends PJProcess {
		String[] _pd$args;
		int _ld0$disp_width;
		int _ld1$disp_height;
		int[] _ld2$temp;
		int[][] _ld3$mandelbrot;
		PJOne2OneChannel<Integer>[] _ld4$channels;
		double _ld5$real_min;
		double _ld6$real_max;
		double _ld7$imag_min;
		double _ld8$imag_max;
		double _ld9$scale_real;
		double _ld10$scale_imag;
		int _ld11$y;
		int _ld12$x;
		double _ld13$c_real;
		double _ld14$c_imag;

	    public main(String[] _pd$args) {
	    	this._pd$args = _pd$args;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			_ld0$disp_width = 4000;
			_ld1$disp_height = 3000;
			_ld3$mandelbrot = new int[_ld1$disp_height][_ld0$disp_width];
			_ld4$channels = (PJOne2OneChannel<Integer>[])new Object[5];
			for(int i=0; i < 5; i++) {
				_ld4$channels[i] = new PJOne2OneChannel<Integer>();
			};
			_ld5$real_min = -0.7801785714285;
			_ld6$real_max = -0.7676785714285;
			_ld7$imag_min = -0.1279296875;
			_ld8$imag_max = -0.1181640625;
			_ld9$scale_real = ((_ld6$real_max - _ld5$real_min) / _ld0$disp_width);
			_ld10$scale_imag = ((_ld8$imag_max - _ld7$imag_min) / _ld1$disp_height);
			final PJPar parfor1 = new PJPar(-1, this);
			int cnt = 0;	
			List<PJProcess> pp = new LinkedList<PJProcess>(); 

			for(_ld11$y = 0; (_ld11$y < _ld1$disp_height); _ld11$y++){
				cnt++;
				pp.add(
					new PJProcess(){
						@Override
						public synchronized void run() {
							for(_ld12$x = 0; (_ld12$x < _ld0$disp_width); _ld12$x++){
							  _ld13$c_real = (_ld5$real_min + ((double) (_ld12$x) * _ld9$scale_real));
							  _ld14$c_imag = (_ld7$imag_min + ((double) (_ld11$y) * _ld10$scale_imag));
							  ((_ld3$mandelbrot[_ld11$y])[_ld12$x]) = (256 - mandelbrot.cal_pixel( _ld13$c_real, _ld14$c_imag ));
							};
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
			this.runLabel = 1;
			yield();
			label(1);
			images.pgm.write_P2_PGM( _ld3$mandelbrot, "mm.pgm", 256 );
			terminate();
		}
	}

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();

		PJProcess.scheduler = scheduler;
		System.out.println("Added _main process to scheduler...");

		(new mandelbrot.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
	public static boolean getTrue() {
		return true;
	}
}