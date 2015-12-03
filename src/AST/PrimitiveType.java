package AST;
import Utilities.Visitor;

public class PrimitiveType extends Type {

  public final static int BooleanKind = PrimitiveLiteral.BooleanKind;
  public final static int CharKind = PrimitiveLiteral.CharKind;
  public final static int ByteKind = PrimitiveLiteral.ByteKind;
  public final static int ShortKind = PrimitiveLiteral.ShortKind;
  public final static int IntKind = PrimitiveLiteral.IntKind;
  public final static int LongKind = PrimitiveLiteral.LongKind;
  public final static int FloatKind = PrimitiveLiteral.FloatKind;
  public final static int DoubleKind = PrimitiveLiteral.DoubleKind;
  public final static int StringKind = PrimitiveLiteral.StringKind;
  public final static int VoidKind = PrimitiveLiteral.NullKind;
  public final static int BarrierKind = PrimitiveLiteral.BarrierKind;
  public final static int TimerKind = PrimitiveLiteral.TimerKind;


  private static String[] names = { "boolean", "byte", "short", "char", "int", "long", "float", "double",  "string", "void", "barrier", "timer" };
  private int kind;

  public PrimitiveType(Token p_t, int kind) {
    super(p_t);
    this.kind = kind;
  }

  public PrimitiveType(int kind) {
    super((AST) null);
    this.kind = kind;
  }

  /** Return the size of this type in bytes in C. */
  public int byteSizeC(){
    switch (kind) {
    case BooleanKind:
      return 1;
    case ByteKind:
      return 1;
    case ShortKind:
      return 2;
    case CharKind:
      return 1;
    case IntKind:
      return 4;
    case LongKind:
      return 8;
    case FloatKind:
      return 4;
    case DoubleKind:
      return 8;
    case StringKind:
      return 4;
    case BarrierKind:
      //TODO
      return 4;
    case TimerKind:
      //TODO
      return 4;
    default:
      return -1;
    }


  }

  public static int ceiling(PrimitiveType p1, PrimitiveType p2) {
    if (p1.kind < p2.kind)
      return p2.kind;
    return p1.kind;
  }

  public static PrimitiveType ceilingType(PrimitiveType p1, PrimitiveType p2) {
    if (p1.kind < IntKind && p2.kind < IntKind)
      return new PrimitiveType(IntKind);

    if (p1.kind < p2.kind)
      return p2;
    return p1;
  }

  public String toString() {
    return typeName();
  }

  public String typeName() {
    return names[kind];
  }

  public int getKind() {
    return kind;
  }

  public String signature() {
    switch (kind) {
    case BooleanKind:
      return "Z";
    case ByteKind:
      return "B";
    case ShortKind:
      return "S";
    case CharKind:
      return "C";
    case IntKind:
      return "I";
    case LongKind:
      return "J";
    case FloatKind:
      return "F";
    case DoubleKind:
      return "D";
    case StringKind:
      return "T";
    case VoidKind:
      return "V";
    case BarrierKind:
      return "R";
    case TimerKind:
      return "M";
    default:
      return "UNKNOWN TYPE";
    }
  }

  public <S extends Object> S visit(Visitor<S> v) {
    return v.visitPrimitiveType(this);
  }
}
