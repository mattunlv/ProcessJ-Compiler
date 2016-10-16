package Generated.randomtest;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class randomtest { 
	public static void foo() {
		std.Random.initRandom( 45 );
		while(true) {
		long _ld0$l = std.Random.longRandom();
		std.io.println( _ld0$l );
		}
	}
	public static void main(String[] _pd$args) {
		randomtest.foo();
	}
	public static boolean getTrue() {
		return true;
	}
}