package CodeGeneratorC;

import AST.*;
import Utilities.*;
import Utilities.Error;
import org.stringtemplate.v4.*;
import java.io.*;
import java.util.*;

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
  /** Name of the parameter of type Workspace that all function needs*/
  private final String globalWorkspace = "WP";
  /** To figure out in more detail and perhaps chose best value at runtime?*/
  private final int stackSize = 256;
  /** Object containing a group of  string templates. */
  private STGroup group;
  /** We must know the name of the last function of the program as this is
   * the function that will be called by the CCSP runtime to start running
   * the program, this is the last function on the file. */
  private String lastFunction = "";
  /**We need to create a: barrier, word, and Worspace variable for each invocation.
   * We do this by appending the S := "barrier" | "word" | "ws" to the function name
   * S ++ functionName, this works except when the same function is called multiple
   * times in the same scope. Therefore this hashtable keeps track of the proper number
   * to append to the end of the functionName to avoid this as an issue. */
  private Hashtable<String, Integer> functionCounts;

  //====================================================================================
  public CodeGeneratorC() {
    Log.log("======================================");
    Log.log("* C O D E   G E N E R A T O R  ( C )  *");
    Log.log("======================================");

    //Load our string templates from specified directory.
    group = new STGroupFile(grammarStFile);
    //Create hashtable!
    functionCounts = new Hashtable<String, Integer>();
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

    return (T) template.render();
  }
  //====================================================================================
  // BinaryExpr
  public T visitBinaryExpr(BinaryExpr be) {
    ST template = group.getInstanceOf("BinaryExpr");

    String left = (String) be.left().visit(this);
    String right = (String) be.right().visit(this);
    String op = (String) be.opString();

    //TODO: Add suport for string concatanation here.

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
    ST template = group.getInstanceOf("Compilation");
    LinkedList<String> prototypes = new LinkedList<String>();

    //We add our function prototypes here as the C program will need them.
    Sequence<Type> typeDecls = c.typeDecls();

    //Iterate over the sequence only collecting the procType arguments.
    for (int i = 0; i < typeDecls.size(); i++) //TODO: Null pointer exception if program is empty?
      if (typeDecls.child(i) instanceof ProcTypeDecl){
        ProcTypeDecl current = (ProcTypeDecl)typeDecls.child(i);
        String prototypeName = getPrototypeString(current);

        prototypes.add(prototypeName);
        //Update name!
        lastFunction = current.name().getname();
      }

    //TODO add the pragmas, packageName and imports later!
    //Recurse to all children getting strings needed for this Class' Template.
    String[] typeDeclsStr = (String[]) c.typeDecls().visit(this);

    //This is where functions are created as they are procedure type.
    template.add("prototypes", prototypes);
    template.add("typeDecls", typeDeclsStr);
    //ProcessJ is set so the last function in the file is called as the main,
    //we do that here by specifiying which function to call.
    template.add("functionToCall", lastFunction);

    //Finally write the output to a file
    String finalOutput = template.render();
    writeToFile(finalOutput);
    System.out.println("Output written to file codeGenerated.c");

    return (T) finalOutput;
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
  /**
   * Method invocations have to be treated special by the compiler, when generating the
   * C equivalent it is not enough to simply call the function. The way that CCSP
   * requires the function call is as follows:


   * //Running a proc requires a barrier:
   * LightProcBarrier barrier;

   * //Initialize barrier by giving it the number of processes that will run with it,
   * //in this case just 1 as well as the barrier's address.
   * LightProcBarrierInit (wordPointer, &barrier, 1);

   * //Process for source function.
   * word workspace1 = WORKSPACE_SIZE(0, STACKSIZE); //Zero is the number of arguments.
   * Workspace ws1 = MAlloc (wordPointer, sizeof (word) * workspace1);
   * //Initialize process by giving it the number of parameters it will take in as well
   * //as it's workspace.
   * ws1 = LightProcInit(wordPointer, ws1, 0, STACKSIZE);
   * //Argument calls are made here if any in the form of:
   *  ProcParam(wordPointer, ws1, 0, &a); //Where 0 refers to the ith parameter you want
   * set and &a refers to the pointer to the parameter that should be passed in.

   * //Run process!
   * LightProcStart(wordPointer, &barrier, ws1, source);


   * Notice that there is no error checking done by the C code but that's okay as we
   * have done the type checking through the ProcessJ compiler.
   */
  public T visitInvocation(Invocation in) {
    String functionName = in.procedureName().getname();

    //We hit an invocation add to our hashtable and get the proper number to append
    //as our counter, see functionCounts for explanataion.
    int fCount = incrementEntry(functionCounts, functionName);
    System.out.println(String.format("Function: %s has a cound of %d", functionName, fCount));
    //Print statements are treated differently. TODO: In the future this will change.
    if(functionName.equals("println"))
      return (T) createPrintFunction(in);

    ST template = group.getInstanceOf("Invocation");
    //Create proper names for variables to avoid name variable already declared.
    String postFix = functionName + fCount;
    String barrierName = "barrier" + postFix;
    String wordName = "word" + postFix;
    String workspaceName = "ws" + postFix;
    Sequence<Expression> params = in.params();

    //Add all parameters to our template!
    template.add("barrierName", barrierName);
    template.add("wordName", wordName);
    template.add("workspaceName", workspaceName);
    template.add("paramNumber", params.nchildren);
    template.add("functionName", functionName);
    template.add("stackSize", stackSize);
    template.add("paramWorkspaceName", globalWorkspace);

    return (T) template.render();
  }
  //====================================================================================
  // LocalDecl
  public T visitLocalDecl(LocalDecl ld) {
    //TODO: isConstant ??
    ST template = group.getInstanceOf("LocalDecl");
    String type = (String) ld.type().typeName();
    String var = (String) ld.var().visit(this);

    //If string type should be char* since C...
    if(type.equals("string") == true)
      type = "char*";

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
  public T visitProcTypeDecl(ProcTypeDecl pd){
    ST template = group.getInstanceOf("ProcTypeDecl");

    Sequence<Modifier> modifiers = pd.modifiers();
    //TODO rest.
    String returnType = pd.returnType().typeName();
    String name = (String) pd.name().visit(this);
    String[] block = (String[]) pd.body().visit(this);
    //Remember that CCSP expects no arguments in the actual prototype, they are passed
    //by function calls from their API.
    //String[] formals = (String[]) pd.formalParams().visit(this);
    String[] formals = {"Workspace " + globalWorkspace};

    template.add("returnType", returnType);
    template.add("name", name);
    template.add("formals", formals);
    template.add("body", block);

    //The CCSP runtime requires a Worspace shutdown after the main function to have run
    //has finished : Shutdown(<paramWorkspaceName>);
    //Therefore we need to make sure we append Shutdowns to returns. TODO
    //The easiest and probably best way to get this acomplished is to change all
    //these "return;" statements into goto LABEL: where label then properly
    //shutdowns the workspace: TODO

    //This is the last function of the program make sure to shutdown Worspace!
    if(name.equals(lastFunction))
      template.add("paramWorkspaceName", globalWorkspace);


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
  /**
   * Auxillary function, given a protocol it will create the appropriate protoype needed
   * by the equivalent c program. This is used to create all the function protoypes
   * that need to be declared at the top.
   */
  String getPrototypeString(ProcTypeDecl procedure){
    ST template = group.getInstanceOf("prototype");
    String name = procedure.name().getname();
    template.add("name", name);
    template.add("workspaceName", globalWorkspace);

    return template.render();
  }
  //====================================================================================
  /**
   * We treat the printing function a bit different since we will be calling printf from
   * it. So we need to create the appropriate final string to pass to it.
   * This function is only called from visitInvocation().
   */
  String createPrintFunction(Invocation in){
    ST template;
    //For some reason we need to flush we want input to actually print. TODO?
    String fflush = ";\nfflush(NULL)";
    //println() is a function of only one argument.
    Expression expr = in.params().child(0);
    //The overall type of  expr is a string but may have other types that are
    //concatenated.
    LinkedList<String> names = new LinkedList<String>();
    String printfStr = makePrintfStr(expr, names);

    //Use different string template depending on whether we have any arguments for
    //our printf besides the string literal.
    if(names.size() == 0)
      template = group.getInstanceOf("printfNoArgs");
    else{
      template = group.getInstanceOf("printf");
      template.add("argumentList", names);
    }
    //Escape character the escape character ;)
    template.add("string",printfStr + "\\n");

    return template.render() + fflush;
  }
  //====================================================================================
  /**
   * Recursively visits the expression of the println invocation creating an equivalent
   * printf string which will be returned. The passed list will be populated with the
   * equivalent argument for every %d, %f, and %s.
   */
  String makePrintfStr(Expression expr, LinkedList lst){
    //If Name Expression we return %s and append name to list.
    if(expr instanceof NameExpr){
      NameExpr ne = (NameExpr)expr;
      Type myType = ne.type;
      String name = ne.toString();
      lst.add(name);

      //Go through the possible types picking the format symbol.
      if(myType.isStringType() == true)
        return "%s";
      if(myType.isFloatType() == true || myType.isDoubleType() == true)
        return "%f";
      if(myType.isIntegralType() == true)
        return "%d";

      Error.error("Type for this name expression in println() not implemented yet...");
      return null;
    }
    //Return our primitive literal as text!
    if(expr instanceof PrimitiveLiteral){
      PrimitiveLiteral pl = (PrimitiveLiteral)expr;
      String content = pl.getText();

      //If it's a string then we get rid of the " " on the literal.
      if(pl.getKind() == PrimitiveLiteral.StringKind)
        return content.substring(1, content.length() - 1);

      //Otherwise we just return it as is :)
      return content;
    }

    //Else it's a binary expression recurse down.
    if(expr instanceof BinaryExpr){
      BinaryExpr be = (BinaryExpr)expr;
      return makePrintfStr(be.left(), lst) + makePrintfStr(be.right(), lst);
    }

    //Else error for now...
    Error.error("Expression type %s println() function not implemented yet...");
    return null;
  }
  //====================================================================================
  /**
   * Given a string it will write to the file as the final output of the compiler.
   * TODO: Should probably figure out a way to let user specify name of output file.
   * as of now it always writes to "codeGenerated.c"
   */
  void writeToFile(String finalOutput){
    Writer writer = null;

    try{
      FileOutputStream fos = new FileOutputStream("codeGenerated.c");
      writer = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
      writer.write(finalOutput);
    } catch (IOException ex) {
      println("IOException: Could not write to file for some reason :/");
    } finally {
      try {writer.close();} catch(Exception ex) {println("Could not close file handle!");}
    }

    return;
  }
  //====================================================================================
  /**
   * Given a hashtable of and the function name it will increment the counter that is the
   * value of that hashtable at for that function name and returns the value of the entry.
   * if the entry was empty it will add it to our table with a value of 1. */
  int incrementEntry(Hashtable<String, Integer> table, String functionName){
    int returnInt;
    //If we have the key increment the value at that spot.
    if(table.containsKey(functionName) == true){
      Integer integer = table.get(functionName) + 1;
      table.put(functionName, integer);
      returnInt = integer;
    }
    else{
      table.put(functionName, 1);
      returnInt = 1;
    }

    return returnInt;
  }
}
