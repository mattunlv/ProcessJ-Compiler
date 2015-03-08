package CodeGeneratorC;

import AST.*;
import Utilities.*;
import Utilities.Error;
import org.stringtemplate.v4.*;
import java.io.*;

/**
 * Code Geneartor turns processJ code to equivalent C code using using
 * parse tree by visiting each node. It then recusively builds the program
 * through recursion returning strings rendered by the string template.
 * Notice sometimes we return String[] if there is multiple statements.
 * General rules of thumb:
 * 1) Anything that is wrapped in a sequence will be passed up as an String[].
 *    it will then be passed to ST as an String[] where the equivalent ST rule
 *    will have a <X;separator = " S"> where S is a string. Therefore the sequence
 *    be delimited by say newlines or commas depending on the proper context.
 * 2) Optional parameters are implemented using <if(X)> <endif> blocks in the ST.
 */
public class CodeGeneratorC <T extends Object> extends Visitor<T> {

    public static SymbolTable symtab = new SymbolTable();

    /** Relative location of our group string template file.*/
    private final String grammarStFile = "src/StringTemplates/grammarTemplates.stg";

    /** Object containing a group of  string templates. */
    private STGroup group;
    //====================================================================================
    public CodeGeneratorC() {
    	Log.log("======================================");
    	Log.log("* C O D E   G E N E R A T O R  ( C )  *");
    	Log.log("======================================");

        //Load our string templates from specified directory.
        group = new STGroupFile(grammarStFile);
    }
    //====================================================================================
    // AltCase
    //====================================================================================
    // AltStat
    //====================================================================================
    // ArrayAccessExpr
    //====================================================================================
    // ArrayLiteral
    //====================================================================================
    // ArrayType
    //====================================================================================
    // Assignment
    public T visitAssignment(Assignment as) {
      ST template = group.getInstanceOf("Assignment");

      String left = (String) as.left().visit(this);
      String right = (String) as.right().visit(this);
      String op = (String) as.opString();

      template.add("left",left);
      template.add("right",right);
      template.add("op",op);

      println(template.render());
      return (T) template.render();
    }
    //====================================================================================
    // BinaryExpr
    public T visitBinaryExpr(BinaryExpr be) {
      ST template = group.getInstanceOf("BinaryExpr");

      String left = (String) be.left().visit(this);
      String right = (String) be.right().visit(this);
      String op = (String) be.opString();

      template.add("left",left);
      template.add("right",right);
      template.add("op",op);

      return (T) template.render();
    }
    //====================================================================================
    // Block
    public T visitBlock(Block bl) {
      String[] statements = (String[]) bl.stats().visit(this);

      return (T) statements;
    }
    //====================================================================================
    // BreakStat //TODO: Add identifier option.
    public T visitBreakStat(BreakStat bs) {
      ST template = group.getInstanceOf("BreakStat");
      //Can be null.
      Name name = bs.target();

      if(name != null){
        String nameStr = (String) name.visit(this);
        //Add name option here.
      }

      return (T) template.render();
    }
    //====================================================================================
    // CastExpr
    public T visitCastExpr(CastExpr ce) {
      ST template = group.getInstanceOf("CastExpr");
      //No node for type get actual string.
      String ct = ce.type().typeName();
      String expr = (String) ce.expr().visit(this);

      template.add("ct", ct);
      template.add("expr", expr);


      return (T) template.render();
    }
    //====================================================================================
    // ChannelType
    //====================================================================================
    // ChannelEndExpr
    //====================================================================================
    // ChannelEndType
    //====================================================================================
    // ChannelReadExpr
    //====================================================================================
    // ChannelWriteStat
    //====================================================================================
    // ClaimStat
    //====================================================================================
    // Compilation
    public T visitCompilation(Compilation c) {
      //TODO This can return null, should we add runtime checks for if null? It makes
      //the code look ugly and should not really happen? But better to be on the safe
      //side? Idk.. Only happens if someone goes in the stg file and messes stuff
      //up >:p
      ST template = group.getInstanceOf("Compilation");

      //TODO add the pragmas, packageName and imports later!
      //Recurse to all children getting strings needed for this Class' Template.
      String[] typeDecls = (String[]) c.typeDecls().visit(this);

      template.add("typeDecls", typeDecls);

      //Final complete program!
      System.out.println(template.render());
      return (T) template.render();
    }
    //====================================================================================
    // ConstantDecl
    //====================================================================================
    // ContinueStat //TODO: add identifier option.
    public T visitContinueStat(ContinueStat cs) {
      ST template = group.getInstanceOf("ContinueStat");
      //Can be null.
      Name name = cs.target();

      if(name != null){
        String nameStr = (String) name.visit(this);
        //Add name option here.
      }

      return (T) template.render();
    }
    //====================================================================================
    // DoStat
    public T visitDoStat(DoStat ds){
      ST template = group.getInstanceOf("DoStat");

      String[] stats = (String[]) ds.stat().visit(this);
      String expr = (String) ds.expr().visit(this);

      template.add("stat", stats);
      template.add("expr", expr);

      return (T) template.render();
    }
    //====================================================================================
    // ExprStat
    public T visitExprStat(ExprStat es) {
      return (T) es.expr().visit(this);
    }
    //====================================================================================
    // ExternType
    //====================================================================================
    // ForStat
    public T visitForStat(ForStat fs) {
      ST template = group.getInstanceOf("ForStat");

      String expr = (String) fs.expr().visit(this);
      Sequence<Statement> init = fs.init();
      Sequence<ExprStat> incr = fs.incr();
      String[] initStr = null;
      String[] incrStr = null;
      //TODO: Barriers

      //Depending whether there is curly brackets it may return an array, or maybe just
      //a single object. So we must check what it actually is!
      Object stats = fs.stats().visit(this);

      if(stats instanceof String[])
        template.add("stats", (String[]) stats);
      else
        template.add("stats", (String) stats);

      //Check for null >:(
      if(init != null)
        initStr = (String[]) init.visit(this);
      if(incr != null)
        incrStr = (String[]) incr.visit(this);

      template.add("init", initStr);
      template.add("incr", incrStr);
      template.add("expr", expr);

      return (T) template.render();
    }
    //====================================================================================
    // Guard
    //====================================================================================
    // IfStat
    public T visitIfStat(IfStat is) {
      ST template = group.getInstanceOf("IfStat");

      String expr = (String) is.expr().visit(this);
      Statement elsePart = is.elsepart();
      Object elsePartStr = null;
      Object thenPart = is.thenpart().visit(this);

      template.add("expr", expr);

      //May be one statement or multiple statements.
      if(thenPart instanceof String[])
        template.add("thenPart", (String[]) thenPart);
      else
        template.add("thenPart", (String) thenPart);

      //May or may not be here!
      if(elsePart != null){
        elsePartStr = elsePart.visit(this);

        if(thenPart instanceof String[])
          template.add("elsePart", (String[]) elsePartStr);
        else
          template.add("elsePart", (String) elsePartStr);
      }

      return (T) template.render();
    }
    //====================================================================================
    // Import
    //====================================================================================
    // Invocation
    //====================================================================================
    // LocalDecl
    public T visitLocalDecl(LocalDecl ld) {
      //TODO: isConstant ??
      ST template = group.getInstanceOf("LocalDecl");
      String type = (String) ld.type().typeName();
      String var = (String) ld.var().visit(this);

      template.add("type", type);
      template.add("var", var);
      println(template.render());
      return (T) template.render();
    }
    //====================================================================================
    // Modifier
    //====================================================================================
    // Name
    public T visitName(Name na) {
      return (T) na.getname(); //TODO: Fix lower case 'n';
    }
    //====================================================================================
    // NamedType
    //====================================================================================
    // NameExpr
    public T visitNameExpr(NameExpr ne) {
      return (T) ne.toString(); //TODO: Fix lower case 'n'
    }
    //====================================================================================
    // NewArray
    //====================================================================================
    // NewMobile
    //====================================================================================
    // ParamDecl
    public T visitParamDecl(ParamDecl pd) {
      ST template = group.getInstanceOf("ParamDecl");
      String type = pd.type().typeName();
      String name = (String) pd.paramName().visit(this);

      template.add("type", type);
      template.add("name", name);

      if(pd.isConstant() == true)
        template.add("constant", "");

      return (T) template.render();
    }
    //====================================================================================
    // ParBlock
    //====================================================================================
    // Pragma
    //====================================================================================
    // PrimitiveLiteral
    //====================================================================================
    public T visitPrimitiveLiteral(PrimitiveLiteral li) {
      return (T) li.getText();
    }
    //====================================================================================
    // PrimitiveType
    //====================================================================================
    // ProcTypeDecl //TODO: change parameters as they need to be passed differently through
    // the ccsp api not through the actual parameter slots.
    public T visitProcTypeDecl(ProcTypeDecl pd) {
      ST template = group.getInstanceOf("ProcTypeDecl");

      Sequence<Modifier> modifiers = pd.modifiers();
      //TODO rest.
      String returnType = pd.returnType().typeName();
      String name = (String) pd.name().visit(this);
      String[] block = (String[]) pd.body().visit(this);
      String[] formals = (String[]) pd.formalParams().visit(this);

      template.add("returnType", returnType);
      template.add("name", name);
      template.add("formals", formals);
      template.add("body", block);

      return (T) template.render();
    }
    //====================================================================================
    // ProtocolLiteral
    //====================================================================================
    // ProtocolCase
    //====================================================================================
    // ProtocolTypeDecl
    //====================================================================================
    // RecordAccess
    //====================================================================================
    // RecordLiteral
    //====================================================================================
    // RecordMembero
    //====================================================================================
    // RecordTypeDecl
    //====================================================================================
    // ReturnStat
    public T visitReturnStat(ReturnStat rs) {
      ST template = group.getInstanceOf("ReturnStat");
      Expression expr = rs.expr();
      String exprStr = "";

      //Can return null so we must check for this!
      if(expr != null){
        exprStr = (String) expr.visit(this);
        template.add("expr", exprStr);
      }

      return (T) template.render();
    }
    //====================================================================================
    // Sequence
    public T visitSequence(Sequence se) {
      String[] returnArray = new String[se.size()];

      //Iterate through all children placing results in array.
      for (int i = 0; i<se.size(); i++)
        if (se.child(i) != null)
          returnArray[i] = (String) se.child(i).visit(this);
        else
          returnArray[i] = null;

      return (T) returnArray;
    }
    //====================================================================================
    // SkipStat
    //====================================================================================
    // StopStat
    //====================================================================================
    // SuspendStat
    //====================================================================================
    // SwitchGroup
    public T visitSwitchGroup(SwitchGroup sg) {
      ST template = group.getInstanceOf("SwitchGroup");
      String[] labels = (String[]) sg.labels().visit(this);
      String[] stmts = (String[]) sg.statements().visit(this);

      template.add("labels", labels);
      template.add("stmts", stmts);

      return (T) template.render();
    }
    //====================================================================================
    // SwitchLabel
    public T visitSwitchLabel(SwitchLabel sl) {
      ST template = group.getInstanceOf("SwitchLabel");
      boolean isDefault = sl.isDefault();

      if(isDefault == false){
        String constExpr = (String) sl.expr().visit(this);
        template.add("constExpr", constExpr);
      }
      else
        template.add("constExpr", "default");

      return (T) template.render();
    }
    //====================================================================================
    // SwitchStat
    public T visitSwitchStat(SwitchStat st) {
      ST template = group.getInstanceOf("SwitchStat");
      String expr = (String) st.expr().visit(this);
      String[] switchGroups = (String[]) st.switchBlocks().visit(this);

      template.add("expr", expr);
      template.add("switchGroups", switchGroups);

      return (T) template.render();
    }
    //====================================================================================
    // SyncStat
    //====================================================================================
    // Ternary
    public T visitTernary(Ternary te) {
      ST template = group.getInstanceOf("Ternary");
      String expr = (String) te.expr().visit(this);
      String trueBranch = (String) te.trueBranch().visit(this);
      String falseBranch = (String) te.falseBranch().visit(this);

      template.add("expr", expr);
      template.add("trueBranch", trueBranch);
      template.add("falseBranch", falseBranch);

      return (T) template.render();
    }
    //====================================================================================
    // TimeoutStat
    //====================================================================================
    // UnaryPostExpr
    public T visitUnaryPostExpr(UnaryPostExpr up) {
      ST template = group.getInstanceOf("UnaryPostExpr");
      String expr = (String) up.expr().visit(this);
      String op = up.opString();

      template.add("expr", expr);
      template.add("op", op);
      return (T) template.render();
    }
    //====================================================================================
    // UnaryPreExpr
    public T visitUnaryPreExpr(UnaryPreExpr up) {
      ST template = group.getInstanceOf("UnaryPreExpr");
      String expr = (String) up.expr().visit(this);
      String op = up.opString();

      template.add("expr", expr);
      template.add("op", op);

      return (T) template.render();
    }
    //====================================================================================
    // Var
    public T visitVar(Var va) {
      ST template = group.getInstanceOf("Var");
      String name = (String) va.name().visit(this);
      String exprStr = "";
      Expression expr = va.init();

      template.add("name", name);

      //Expr may be null if the variable is not intialized to anything!
      if(expr != null){
        exprStr = (String) expr.visit(this);
        template.add("init", exprStr);
      }

      return (T) template.render();
    }
    //====================================================================================
    // WhileStat
     public T visitWhileStat(WhileStat ws) {
      ST template = group.getInstanceOf("WhileStat");
      String expr = (String) ws.expr().visit(this);
      Object stats = ws.stat().visit(this);

      template.add("expr", expr);

      //May be one element or multiple.
      if(stats instanceof String[])
        template.add("stat", (String[]) stats);
      else
        template.add("stat", (String) stats);

      return (T) template.render();
    }
    //====================================================================================
}
