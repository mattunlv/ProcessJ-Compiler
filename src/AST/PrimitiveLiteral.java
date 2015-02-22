package AST;
import Utilities.Error;
import Utilities.Visitor;

import java.math.*;

public class PrimitiveLiteral extends Literal {    

	public final static int BooleanKind = 0; // either true or false
	public final static int ByteKind    = 1; // 8 bit signed:                  -128 -> 127
	public final static int ShortKind   = 2; // 16 bit signed:               -32768 -> 32767
	public final static int CharKind    = 3; // 16 bit unicode
	public final static int IntKind     = 4; // 32 bit signed:          -2147483648 -> 2147483647
	public final static int LongKind    = 5; // 64 bit signed: -9223372036854775808 -> 9223372036854775807

	public final static int FloatKind   = 6; // 32 bit IEEE 754-1985. min: 1.4e-45f max: 3.4028235e+38f
	public final static int DoubleKind  = 7; // 64 bit IEEE 754-1985. min: 5e-324   max: 1.7976931348623157e+308    

	public final static int StringKind  = 8;
	public final static int NullKind    = 9;

	public final static int BarrierKind = 10; // No literal exists of this type;
	public final static int TimerKind   = 11; // No literal exists of this type;

	private static String [] names = { "boolean", "byte", "short", "char", "int", "long", "float", "double", "string", "null", "barrier", "timer" };

	private int kind;
	private String text;

	public PrimitiveLiteral(Token p_t, int kind) {
		super(p_t);
		this.kind = kind;
		this.text = p_t.lexeme;
		nchildren = 0;

		if (kind == CharKind)
			text = new Integer(parseChar(text)).toString();
		else if (kind == IntKind || kind == ShortKind || kind == ByteKind) {
			try {
				text = (Integer.decode(text)).toString();
			} catch (NumberFormatException e) {
				Error.error(this, "integer number " + text + " too large", true);
			}
		}
		else if (kind == LongKind) {
			if (text.charAt(text.length()-1) == 'l' || text.charAt(text.length()-1) == 'L') {
				text = text.substring(0,text.length()-1);
				try {
					text = (Long.decode(text)).toString();
				} catch (NumberFormatException e) {
					Error.error(this, "long number " + text + " too large", true);
				}
			}
		}
		else if (kind == DoubleKind) {
			if (text.charAt(text.length()-1) == 'd' || text.charAt(text.length()-1) == 'D')
				text = text.substring(0,text.length()-1);
			text = ""+Double.parseDouble(text);
		}
		else if (kind == FloatKind) {
			if (text.charAt(text.length()-1) == 'f' || text.charAt(text.length()-1) == 'F')
				text = text.substring(0,text.length()-1);
			text = ""+Float.parseFloat(text);
		} 
		// kind 10 will just fall through
	}

	public int getKind() {
		return kind;
	}

	public String getText() {
		return text;
	}

	public boolean isConstant() {
		return true;
	}

	public Object constantValue() {
		if (kind == StringKind || kind == NullKind)
			return text;
		else if (kind == BooleanKind) 
			return new BigDecimal(text.equals("false")?"0":"1");
		else 
			return new BigDecimal(text);
	}


	public String toString() {
		return names[kind] + "-type = " + text;
	}

	public float floatValue() {
		if (kind != FloatKind) 
			Error.error("Can't convert non float literal to float.");
		return Float.parseFloat(text);
	}

	public double doubleValue() {
		if (kind != DoubleKind)
			Error.error("Can't convert non double literal to double.");
		return Double.parseDouble(text);
	}

	public int intValue() {
		if (kind != IntKind && kind != CharKind)
			Error.error("Can't convert non int value to int.");	
		return Integer.decode(text).intValue();
	}

	public int charValue() {
		if (kind != CharKind) 
			Error.error("Can't convert non char value to char.");
		return Integer.decode(text).intValue();
	}

	public long longValue() {
		if (kind != LongKind)
			Error.error("Can't convert non long value to long.");
		return Long.decode(text).longValue();
	}

	public static boolean isByteValue(long val) {
		return (-128 <= val && val <= 127);
	}

	public boolean isByteValue() {
		return isByteValue(Long.decode(text).longValue());
	}

	public static boolean isShortValue(long val) {
		return (-32768 <= val && val <= 32767);
	}

	public boolean isShortValue() {
		return isShortValue(Long.decode(text).longValue());
	}

	public static boolean isIntValue(long val) {
		return (-2147483648 <= val && val <= 2147483647);
	}

	public boolean isIntValue() {
		return isIntValue(Long.decode(text).longValue());

	}

	public static boolean isCharValue(long val) {
		return (0 <= val && val <= 65535);
	}

	public boolean isCharValue() {
		return isCharValue(Long.decode(text).longValue());
	}

	public static boolean isFloatValue(double val) {
		return (-3.4028235e+38f <= val && val <= 3.4028235e+38f);
	}

	public boolean isFloatValue() {
		return isFloatValue(Double.parseDouble(text));
	}

	public int hexDigit2Int(char h) {
		if ('0' <= h && h <= '9')
			return h - '0';
		return h - 'a' + 10;
	}

	public int parseChar(String s) {
		int i=0;
		char ch = s.charAt(1);
		if (ch == '\\') {
			// Escape char.
			ch = s.charAt(2);
			if (ch == 'u')
				// unicode. Ex: '\uABCD'
				i = hexDigit2Int(s.charAt(3)) * 4096 + 
				hexDigit2Int(s.charAt(4)) * 256 + 
				hexDigit2Int(s.charAt(5)) * 16 + 
				hexDigit2Int(s.charAt(6));
			else
				switch (ch) {
				case 'b': i = 8; break;  
				case 't': i = 9; break;  
				case 'n': i = 10; break;  
				case 'f': i = 12; break;      
				case 'r': i = 13; break;  
				case '\"': i = 34; break;  
				case '\'': i = 39; break;  
				case '\\': i = 92; break;     
				default: {// octal value; Ex '\45'
					int l = s.length()-3;
					switch (l) {
					case 1: i = (s.charAt(2) - '0'); break;
					case 2: i = (s.charAt(2) - '0') * 8  + (s.charAt(3) - '0'); break;
					case 3: i = (s.charAt(2) - '0') * 64 + (s.charAt(3) - '0') * 8 + (s.charAt(4) - '0'); break;
					}
				}
				}
		} else {
			i = ch;
		}
		return i;
	}

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitPrimitiveLiteral(this);
	}
}









