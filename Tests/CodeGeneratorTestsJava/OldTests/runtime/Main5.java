public class Main5 {
	final RunQueue rq = new RunQueue();

	private abstract class _p1 extends Process {
		int g(Channel<Integer> c, int a, int b) {
			// make new activation
			return g();
		}

		// g(Channel<Integer>, int, int)
		int g() {
			// Define paramters 
			Channel<Integer> c; // to avoid name clashes it should be named param$c
			int a;
			int b;
			// Get activation
			Activation _activation = getActivation();
			// Get run label 
			runLabel = _activation.getRunLabel();

			// Re-establish parameters
			c = (Channel<Integer>) _activation.getLocal(0);
			a = (Integer) _activation.getLocal(1);
			b = (Integer) _activation.getLocal(2);

			if (runLabel != 0) {
				// Re-establish locals
			}

			switch (runLabel) {
			case 0:
			case 1:
				//System.out.println("Hello all the way from g() - I think I will Yield!");
				//	yield(new Activation("yield in g", new Object[] { c, new Integer(a), new Integer(b) }, 3), 2);
				//return 0;
			case 2:
				if (c.isReadyToRead(this)) {
					b = c.read(this);
					//yield(new Activation("yield in g", new Object[] { c, new Integer(a), new Integer(b) }, 3), 3);
					//return 0;
				} else {
					setNotReady();
					yield(new Activation(new Object[] { c, new Integer(a),
							new Integer(b) }, 3), 2);
					return 0;
				}
			case 3:
				//System.out.println("Hello all the way from g() again - after the read - lets terminate");
				terminate();
				return a + b;
			}
			return a + b;
		}

		void bar(int a) {
			//   System.out.println("start of bar()");
			for (int i = 0; i < 10; i++)
				;//	System.out.print(i + " " + a + " ");
					//System.out.println();
					//System.out.println("end of bar()");
		}

		/* ******************** */
		/* ** Constructor    ** */
		/* ******************** */

		public _p1(Channel<Integer> c) {
			Activation _activation = new Activation(new Object[] { c }, 2); // c & a
			addActivationFirst(_activation);
		}

		// Original name: p1
		// with the correct parameter interface: Channel<Integer> c
		void p1() {
			// Define parameters
			Channel<Integer> c; // should be param$c
			// Get activation
			Activation _activation = getActivation();
			// Get runlabel
			runLabel = _activation.getRunLabel();

			// Re-establish parameters
			c = (Channel<Integer>) _activation.getLocal(0);

			int a;
			// Re-establish local (this is done after any declaration!)
			a = (_activation.getLocal(1) == null ? 72 : (Integer) _activation
					.getLocal(1));

			switch (runLabel) {
			case 0:
				//System.out.println("Hello from P1 first time");
				//	yield(new Activation("p1", new Object[] { c, new Integer(a) }, 2), 1);
				//return;
			case 1:
				//System.out.println("Hello from P1 second time");
				bar(a);
				a = 38;
				// Make activation for g(Channel<Integer>, int, int )
				// 
				addActivationFirst(new Activation(new Object[] { c,
						new Integer(a), new Integer(38) }, 3));
			case 2:
				// if we arrive at 2 from 1 we will have a new activation for g at the head of the queue
				// if we arrive at 2 from the switch the activation will aleady be at the head of the queue
				//System.out.println("Activation Stack before call to g: " + getActivationString());
				int temp_3 = g();
				//System.out.println("did we yield? " + yielded);
				if (yielded) {
					// if we yielded make an activation and add it to the head of the queue
					//addActivationFirst(new Activation("p1 (on yield)", new Object[] { c, new Integer(38) }, 2, 2));	       
					yield(new Activation(new Object[] { c, new Integer(38) }, 2),
							2);
					return;
				} else
					a = a + temp_3;
				terminate();
			}
			terminate();
			return;
		}
	}

	private abstract class _p2 extends Process {
		public _p2(Channel<Integer> c) {
			addActivationLast(new Activation(new Object[] { c }, 1));
		}

		void p2() {
			Activation _activation = getActivation();
			runLabel = _activation.getRunLabel();

			Channel<Integer> c = (Channel<Integer>) _activation.getLocal(0);

			switch (runLabel) {
			case 0:
				//System.out.println("Hello from P2");

			case 1:
				if (c.isReadyToWrite()) {
					c.write(this, 42);
					yield(new Activation(new Object[] { c }, 1), 2);
					return;
				} else {
					setNotReady();
					yield(new Activation(new Object[] { c }, 1), 1);
					return;
				}
			case 2:
				terminate();
				return;
			}
		}
	}

	/*
	  proc void main() {
	    chan<int> c;
	par {
	  p1(c.write);
	  p2(c.read);
	    }
	 */

	private class _main extends Process {

		public _main(int count) {
			addActivationLast(new Activation(new Object[] { count }, 2));
		}

		public void run() {
			main();
		}

		public void main() {

			Activation _activation = getActivation();
			runLabel = _activation.getRunLabel();

			int param$count;
			param$count = (_activation.getLocal(0) == null ? 0
					: (Integer) _activation.getLocal(0));

			Channel<Integer> c;
			// restore locals
			c = (_activation.getLocal(1) == null ? null
					: (Channel<Integer>) _activation.getLocal(1)); // no need for null checking - only for primitives

			switch (runLabel) {
			case 0:
				//c = new Channel<Integer>();
			case 1:
				//System.out.println("Main right before the par block " + (this == null));

				for (int i = 0; i < param$count; i++) {
					System.out.println(i);
					c = new Channel<Integer>();
					final Par _par1 = new Par(2, this);

					// this assure that this process will not get scheduled until it
					// is marked ready by the par block!
					setNotReady();

					// parameters need to be copied to final variables here!
					//final Channel<Integer> temp_1 = c;

					// make the new processes and add them to the runqueue	       
					Process p1 = new _p1(c) {
						public void run() {
							p1(); // activation is alrady at the head of the activation stack
						}

						public void finalize() {
							_par1.decrement();
						}
					};
					rq.insert(p1);
					Process p2 = new _p2(c) {
						public void run() {
							p2();
						}

						public void finalize() {
							_par1.decrement();
						}
					};
					rq.insert(p2);
					// then suspend yourself to run the processes in the par!		
				}
				yield(new Activation(new Object[] { new Integer(count), c }, 2),
						2);
				return;
			case 2:
				//System.out.println("Main has finished! Ready to terminate!");
				terminate();
				return;
			}
			terminate();
			return;
		}
	}

	// hack for testing 
	static int count;

	public static void main(String args[]) {
		count = Integer.parseInt(args[0]);
		new Main5().run();
	}

	public void run() {
		rq.insert(new _main(count));

		// run the scheduler here and start main!
		int notReadyCounter = 0;
		while (rq.size() > 0) {
			//       System.out.println("--------------------------------------------------");
			System.out.println("Run Queue size: [" + rq.size() + "]");
			// grab the next process in the run queue
			Process p = rq.getNext();
			// is it ready to run?
			if (p.isReady()) {
				// yes, so run it
				//System.out.println("Ready to run " + p);
				//System.out.println("Activation record for " + p + " = <" + p.getActivationString() + ">");
				p.yielded = false;
				p.run();
				// and reset the notReadyCounter
				notReadyCounter = 0;
				// result < 0 => process terminated
				if (!p.terminated()) {
					// did not terminate, so insert in run queue
					// Note, it is the process' own job to
					// set the 'ready' flag.
					rq.insert(p);
					//System.out.println("Ran " + p.getId() + " - Next run label is " + p.getNextRunLabel());
				} else {
					// did terminate so do nothing
					p.finalize();
					//System.out.println(p.getId() + " terminated.");
				}
			} else {
				// no, not ready, put it back in the run queue
				// and count it as not ready
				//System.out.println(p + ": not ready to run. Back in the queue!");
				rq.insert(p);
				notReadyCounter++;
			}
			// if we have seen all the processes
			// an none were ready we have a deadlock
			if (notReadyCounter == rq.size() && rq.size() > 0) {
				System.out
						.println("No processes ready to run. System is deadlocked");
				System.out.println(rq);
				System.exit(1);
			}
		}

	}

}