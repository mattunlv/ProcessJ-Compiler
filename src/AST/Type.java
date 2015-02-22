package AST;
import Utilities.Error;

public abstract class Type extends AST {

	public Type() {
		// must only be called from ErrorType
		super();
	}


	public Type(AST a) {
		super(a);
	}

	public Type(Token t) {
		super(t);
	}

	public abstract String typeName() ;

	/*
	 * public boolean identical(Type other) {
                // TODO this was changed 3/29/12 from typename() to signature()
                if (signature().equals(other.signature()))          
                        return true;
                else if ((this instanceof ClassType) && (other instanceof NullType))
                        return true;
                else if ((this instanceof NullType) && (other instanceof ClassType))
                        return true;
                else
                        return false;
        }
	 */

	public boolean identical(Type other) {
		System.out.println("Type.identical: " + signature() + " <-> " + other.signature());
		if (signature().equals(other.signature()))
			return true;
		return false;
	}

	public boolean assignable() {
		return (!typeName().equals("null") && !typeName().equals("void"));
	}

	public abstract String signature();


	public static boolean assignmentCompatible(Type var, Type val) {
		if (var.identical(val)) // Same type
			return true;
		else if (var.isNumericType() && val.isNumericType()) {
			// Both are numeric (primitive) types.
			PrimitiveType pvar = (PrimitiveType)var;
			PrimitiveType pval = (PrimitiveType)val;

			// double :> float :> long :> int :> short :> byte
			if (pvar.getKind() == PrimitiveType.CharKind)
				return false; // do not allow assignment of numeric values to chars
			if (pval.getKind() == PrimitiveType.CharKind)
				return (pvar.getKind() != PrimitiveType.ByteKind &&
				pvar.getKind() != PrimitiveType.ShortKind);
			return (pvar.getKind() >= pval.getKind()); // ok to assign char value to none byte/short var
		} 
		return false;
	}

	public boolean isIntegerType() {
	    return (this instanceof PrimitiveType && ((PrimitiveType)this).getKind() == PrimitiveType.IntKind);

	}

	public boolean isErrorType() {
		return (this instanceof ErrorType);
	}


	public boolean isArrayType() {
		return (this instanceof ArrayType);
	}

	public boolean isBooleanType() {
		return (this instanceof PrimitiveType && ((PrimitiveType)this).getKind() == PrimitiveType.BooleanKind);
	}

	public boolean isByteType() {
		return (this instanceof PrimitiveType && ((PrimitiveType)this).getKind() == PrimitiveType.ByteKind);
	}

	public boolean isShortType() {
		return (this instanceof PrimitiveType && ((PrimitiveType)this).getKind() == PrimitiveType.ShortKind);
	}

	public boolean isCharType() {
		return (this instanceof PrimitiveType && ((PrimitiveType)this).getKind() == PrimitiveType.CharKind);
	}

	public boolean isLongType() {
		return (this instanceof PrimitiveType && ((PrimitiveType)this).getKind() == PrimitiveType.LongKind);
	}
	public boolean isTimerType() {
		return (this instanceof PrimitiveType && ((PrimitiveType)this).getKind() == PrimitiveType.TimerKind);
	}
	public boolean isBarrierType() {
		return (this instanceof PrimitiveType && ((PrimitiveType)this).getKind() == PrimitiveType.BarrierKind);
	}
	public boolean isChannelType() {
		return (this instanceof ChannelType);
	}
	public boolean isChannelEndType() {
		return (this instanceof ChannelEndType);
	}
	public boolean isRecordType() {
		return (this instanceof RecordTypeDecl);
	}
        public boolean isProtocolType() {
                return (this instanceof ProtocolTypeDecl) ;
        }
	public boolean isVoidType() {
		return (this instanceof PrimitiveType && ((PrimitiveType)this).getKind() == PrimitiveType.VoidKind);
	}
	//public boolean isVoidType() {
	//	return (typeName().equals("void"));
	//}

	// TODO: Do we need this??
	public boolean isNullType() {
		//return (this instanceof NullType);
		return false; // TEMPOREARY RETURN VALUE
	}

	public boolean isStringType() {
	    return (this instanceof PrimitiveType && ((PrimitiveType)this).getKind() == PrimitiveType.StringKind);
	}

	public boolean isFloatType() {
		return (this instanceof PrimitiveType && ((PrimitiveType)this).getKind() == PrimitiveType.FloatKind);
	}

	public boolean isDoubleType() {
		return (this instanceof PrimitiveType && ((PrimitiveType)this).getKind() == PrimitiveType.DoubleKind);
	}

	public boolean isNumericType() {
		return (isFloatType() || isDoubleType() || isIntegralType());
	}

	public boolean isIntegralType() {
		return (isIntegerType() || isShortType() || isByteType() || isCharType() || isLongType());
	}

	public boolean isPrimitiveType() {
		return isNumericType() || isVoidType() || isNullType() || isStringType() || isBooleanType();
	}   

	public boolean isNamedType() {
		return (this instanceof NamedType);
	}
}             




