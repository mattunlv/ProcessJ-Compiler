package SampleTests;

import ProcessJ.runtime.PJProtocolCase;

public class simpleProtocolCDS {

	public static class Protocol_A {
		public enum Tag {
			a1, a2
		};

		public static class Protocol_A_a1 extends PJProtocolCase {
			public int x;
			int y;

			public Protocol_A_a1(int x, int y) {
				this.x = x;
				this.y = y;
			}
		}

		public static class Protocol_A_a2 extends PJProtocolCase {
			public double d;
			float f;

			public Protocol_A_a2(double d, float f) {
				this.d = d;
				this.f = f;
			}
		}

		PJProtocolCase data;
		Tag tag;

		public Protocol_A(PJProtocolCase data, Tag tag) {
			this.data = data;
			this.tag = tag;
		}
	}

	public static class Protocol_B {
		public enum Tag {
			b1, b2
		};

		public static class Protocol_B_b1 extends PJProtocolCase {
			public int x;
			int y;
			int z;

			public Protocol_B_b1(int x, int y, int z) {
				this.x = x;
				this.y = y;
				this.z = z;
			}
		}

		public static class Protocol_B_b2 extends PJProtocolCase {
			public double d;
			float f;
			long l;

			public Protocol_B_b2(double d, float f, long l) {
				this.d = d;
				this.f = f;
				this.l = l;
			}
		}

		PJProtocolCase data;
		Tag tag;

		public Protocol_B(PJProtocolCase data, Tag tag) {
			this.data = data;
			this.tag = tag;
		}
	}

	public static class Protocol_C {
		public enum Tag {
			c, a1, a2, b1, b2
		};

		public static class Protocol_C_c extends PJProtocolCase {
			public int cc;

			public Protocol_C_c(int cc) {
				this.cc = cc;
			}
		}

		PJProtocolCase data;
		Tag tag;

		public Protocol_C(PJProtocolCase data, Tag tag) {
			this.data = data;
			this.tag = tag;
		}
	}

	public static void main(String args[]) {
		Protocol_C val1 = null;
		for (int i = 0; i < 5; i++) {
			switch (i) {
				case 0:
					val1 = new Protocol_C(new Protocol_A.Protocol_A_a1(12, 34), Protocol_C.Tag.a1);
					break;
				case 1:
					val1 = new Protocol_C(new Protocol_B.Protocol_B_b2(3.14, 3.14F, 3L), Protocol_C.Tag.b2);
					break;
				case 2:
					val1 = new Protocol_C(new Protocol_C.Protocol_C_c(34), Protocol_C.Tag.c);
					break;
				case 3:
					val1 = new Protocol_C(new Protocol_A.Protocol_A_a2(2.56, 3.45F), Protocol_C.Tag.a2);
					break;
				case 4:
					val1 = new Protocol_C(new Protocol_B.Protocol_B_b1(1, 2, 3), Protocol_C.Tag.b1);
					break;
			}
			switch (val1.tag) {
				case a1:
					System.out.print("a1: ");
					System.out.print("x = " + ((Protocol_A.Protocol_A_a1) val1.data).x + ", ");
					System.out.println("y = " + ((Protocol_A.Protocol_A_a1) val1.data).y);
					break;
				case a2:
					System.out.print("a2: ");
					System.out.print("d = " + ((Protocol_A.Protocol_A_a2) val1.data).d + ", ");
					System.out.println("f = " + ((Protocol_A.Protocol_A_a2) val1.data).f);
					break;
				case b1:
					System.out.print("b1: ");
					System.out.print("x = " + ((Protocol_B.Protocol_B_b1) val1.data).x + ", ");
					System.out.println("y = " + ((Protocol_B.Protocol_B_b1) val1.data).y);
					System.out.println("z = " + ((Protocol_B.Protocol_B_b1) val1.data).z);
					break;
				case b2:
					System.out.print("b2: ");
					System.out.print("d = " + ((Protocol_B.Protocol_B_b2) val1.data).d + ", ");
					System.out.print("f = " + ((Protocol_B.Protocol_B_b2) val1.data).f + ", ");
					System.out.println("l = " + ((Protocol_B.Protocol_B_b2) val1.data).l);
					break;
				case c:
					System.out.print("c: ");
					System.out.println("cc = " + ((Protocol_C.Protocol_C_c) val1.data).cc);
					break;
			}
			//this is what should be generated for the test class. above code is just for testing.
			Protocol_C val = new Protocol_C(new Protocol_A.Protocol_A_a1(12, 34), Protocol_C.Tag.a1);
			Protocol_B b = new Protocol_B(new Protocol_B.Protocol_B_b1(1, 2, 3), Protocol_B.Tag.b1);
			switch(val.tag) {
				case c: ((Protocol_C.Protocol_C_c) val.data).cc = 100; break;
				case a1: ((Protocol_A.Protocol_A_a1) val.data).x = 200;
				switch( b.tag ){
			          case b1:
			            ((Protocol_B.Protocol_B_b1) b.data).x = 10;
			            break;
			          case b2:
			            ((Protocol_B.Protocol_B_b2) b.data).l = 10;
			            break;
			      };
			      break;
				case b2: ((Protocol_B.Protocol_B_b2) val.data).f = 300; break;
					
			}
		}
	}

}
