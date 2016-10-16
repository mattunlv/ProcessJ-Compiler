import java.util.*;
import ProcessJ.runtime.*;

public class simpleRecord {
	public static class Record_Transaction {
		public int j;
		public Record_Transaction(int j) {
	    	this.j = j;
		}
	}
	public static class Record_Client {
		public double[][] temp1;
		public Record_Client(double[][] temp1) {
	    	this.temp1 = temp1;
		}
	}
	public static class Record_P {
		public int x;
		public int y;
		public int z;
		public Record_P(int x, int y, int z) {
	    	this.x = x;
	    	this.y = y;
	    	this.z = z;
		}
	}
	public static class Record_K {
		public int z;
		public Record_K(int z) {
	    	this.z = z;
		}
	}
	public static void foo() {
		Record_P _ld0$myRecord = new Record_P(1, 2, 3);
		int[] _ld1$temp;
		Record_Client[] _ld2$client;
		String[][][] _ld3$str;
	}
	public static boolean getTrue() {
		return true;
	}
}