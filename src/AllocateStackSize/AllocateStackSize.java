package AllocateStackSize;

import AST.*;
import Utilities.*;
import Utilities.Error;
import java.io.*;
import java.util.*;
import NameCollector.*;
import CodeGeneratorC.*;
//========================================================================================
/**
 * This is the compiler pass to allocate the correct stack sizes for the compiler. This
 * class computes the correct stack size for the main function as well as all ParBlock
 * functions that are passed through the CCSP interface. Given an empty
 * Hashtable<String,int> it will populate it with mappings of function names to size in
 * bytes needed by that function This includes the size for all functions that this function
 * may call. This way when the CodeGeneratorC is ran again that compiler pass will look at
 * the hashtable and input the correct values. TODO: Will die horribly with recursion.
 * Notice all sizes are measured in bytes but the CCSP API wants the final results in words.
 * This is handled by the code generator.
 */
//========================================================================================
public class AllocateStackSize extends Visitor<Void>{
  Hashtable<String, Integer> sizePerFunction;
  Hashtable<String, Integer> suTable;
  String currentFunction = null;
  /**
   * This Hash Table is used to create temporary functions with unique names. Everytime
   * we hit a ProcDecl we know we have a new function and are looking at a new scope,
   * hence we might have a ParBlock{...} so we create a new entry in this function and
   * and set the amount to 0 from here for every statement that needs to be wrapped in
   * a function we give it a name of the form <NameOfFunction>_ParBlockStmt<counterValue>.
   * Exactly how it's done in code generation.
   */
  private Hashtable<String, Integer> parBlockStmtCounts;
  /**
   * Same thing for Par ForStats.
   */
  private Hashtable<String, Integer> parForStmtCounts;
  /**
   * This table holds a list of nodes per function. The nodes have a value and a tag.
   * The value contains the size for that piece and the tag contains where it came from.
   * This way per function a nice list of where all the memory came from.
   */
  private TableList nodesPerFunction;
  /**
   * We must know the name of the last function of the program as this is
   * the function that will be called by the CCSP runtime to start running
   * the program, this is the last function on the file.
   */
  private String lastFunction = null;

  /** Size for various function and miscellanous things. */
  private final int ccspKernelCallSize = 32;
  private final int printfSize = 64; //ExternalCallN = 32 + 32 kernell call.
  private final int wsSize = 4;
  private final int wordSize = 4;

//========================================================================================
  /**
   * Constructor for our visitor, given a HashTable it will populate it with mappings of
   * function names to size in bytes needed by that function
   * @param sizePerFunction: empty Java Hashtable.
   * @param suTable read from file mapping functions to sizes.
   */
  public AllocateStackSize(Hashtable<String, Integer> sizePerFunction,
                           Hashtable<String, Integer> suTable){
    Log.log("=================================================");
    Log.log("* S T A C K  S I Z E  A L L O C A T O R  ( C )  *");
    Log.log("=================================================");

    if(sizePerFunction == null || suTable == null)
      Error.error("Null hashtable passed to Stack Size Allocate Visitor!");

    this.sizePerFunction = sizePerFunction;
    this.suTable = suTable;
    this.parBlockStmtCounts = new Hashtable();
    this.parForStmtCounts = new Hashtable();
    this.nodesPerFunction = new TableList();

    //Certain functions are built in and we define their size here: TODO: In the future
    //this should be done in a separate file and the values should be read in??
    nodesPerFunction.addNode(CodeGeneratorC.printFunctionName,
			     new ValueAndTag(64, "Size of println.", true));
    suTable.put("ccsp_kernel_call", ccspKernelCallSize);

    return;
  }
  //========================================================================================
  /**
   * We start here since we know a compilation is always passed to us.
   */
  public Void visitCompilation(Compilation c){
    Log.log("Visiting a Compilation...");

    Sequence<Type> typeDecls = c.typeDecls();
    for(Type current : typeDecls)
      if(current instanceof ProcTypeDecl){
        String name = ((ProcTypeDecl)current).name().getname();
        this.lastFunction = name;
      }

    //Visit entire tree and populate nodes per function table.
    c.typeDecls().visit(this);
    Log.log(nodesPerFunction.toString());

    //Now we sum up all the nodes for each one of our functions and save this values to
    //our sizePerFunction table to be passed back to main.
    Set<String> keys = nodesPerFunction.getKeys();

    for(String functionName : keys){
      int sum = nodesPerFunction.sumEntriesMaxInvocationOnly(functionName);
      sizePerFunction.put(functionName, sum);
    }

    return null;
  }
  //========================================================================================
  /**
   * This is what we are interested in. The stack size required per proc declaration.
   * So for all our ProcTypeDecl we figure out the size required by setting this as the
   * current function, then we recurse on all of it's children and add nodes for their
   * size to the hashtable entry for this function. By the time we are done we will have
   *the total size. Notice that this function may call other functions. This case is handled
   * in @visitInvocation() below.
   */
  public Void visitProcTypeDecl(ProcTypeDecl pd){
    String name = CodeGeneratorC.makeFunctionName(pd);
    Log.log(pd.line + ": Visiting a Proc Type Decl: " + name);
    int value; String tag;

    //Save our current function and set it to this guy before recursing.
    String prevCurrentFunction = currentFunction;
    currentFunction = name;
    //First we recurse on our children to get to the bottom of the call chain. Then we
    //work back out.
    pd.body().visit(this);

    //Count our locals and parameters.
    tag = "Variables and locals size.";
    MutableInt variablesSize = new MutableInt();
    pd.visit(new LocalsAndParamsSize(variablesSize));
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(variablesSize.val(), tag));

    //Su table entry info.
    value = suTable.get(name);
    tag = "Su Table size for this function.";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(value, tag));

    value = wsSize;
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(value, "Size of Workspace."));

    //This is the function to be called by our program. Some extra variables to add.
    if(lastFunction.equals(currentFunction)){
      //Shutdow()
      value = suTable.get("Shutdown");
      tag = "Size of ShutDown function.";
      nodesPerFunction.addNode(currentFunction, new ValueAndTag(value, tag, true));
      //Shutdown() calls the CCSP Kernel.
      value = suTable.get("ccsp_kernel_call");
      tag = "Shutdown() calls ccsp_kernel_call.";
      nodesPerFunction.addNode(currentFunction, new ValueAndTag(value, tag, true));
    }

    currentFunction = prevCurrentFunction;
    return null;
  }
  //========================================================================================
  /**
   * Our ProcType has an invocation to another function as of now there are two cases:
   * 1) This is another function in this file.
   * 2) This is a function in another file: Like println.
   * Case 1: We check our hashtable and see if we have calculated this value, if we haven't
   * we recurse on the Proc of this invocation. Fetch the ProcType belonging to this invocation
   * and recurse on it to allocate it's size.
   * Case 2: All functions that do not have an explicit tree because they're built in (like
   * println) are added to the table above with their correct size and therefore are guaranteed
   * to exist. TODO: In the future we might change this.
   */
  public Void visitInvocation(Invocation in){
    if(in.targetProc == null)
      Error.error("Our Invocation had a null target Proc!");
    
    String functionName = CodeGeneratorC.makeFunctionName(in.targetProc);
    Log.log(in.line + ": Visiting a Invocation: " + functionName);

    //Check our hash table to see if this proc has had it's size set, if not do it now.
    if(nodesPerFunction.hasEntry(functionName) == false)
      in.targetProc.visit(this);

    //Now visit our children in case we have function calls as our arguments.
    in.params().visit(this);
    
    int size = nodesPerFunction.sumEntries(functionName);
    String tag =
      String.format("Invocation call to function %s at line: %d.", functionName, in.line);

    nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag, true));

    return null;
  }
  //========================================================================================
  /**
   * If this is a parallel ForStat we have to do a lot of extra work and allocate extra
   * memory as we expect this to run in paralle through the CCSP API. It's not too bad
   * though, it's almost exactly like the ProcPar.
   */
  public Void visitForStat(ForStat fs){
    Log.log(fs.line + ": Visiting a ForStat.");

    /* This is just a regular for loop, recurse on children and move on! */
    if(fs.isPar() == false){
      fs.visitChildren(this);
      return null;
    }
    
    Statement stat = fs.stats();
    final int pointerSize = 4;
    String tag;
    
    //We will be calculating the size of a function that is not in the AST hence we have
    //to save the current function and prentend we are on a new function.
    String callerFunction = currentFunction;

    //Figure out the size for the function we created. By convention, this will be the
    //name of this function. First we add one to the entry in our hash table and get the
    //name.
    incrementEntry(parForStmtCounts, callerFunction, 1);
    int nameNumber = parForStmtCounts.get(callerFunction);
    //This must be set to the class variable current function as the .visit(this) will
    //expect it to be! So don't change :b
    currentFunction = callerFunction + "_ParForStmt" + nameNumber;

    //Count the variables used as these will be passed as in as pointers.
    LinkedList<NameExpr> myNames = new LinkedList();
    stat.visit(new NameCollector(myNames));
    int argumentCount = myNames.size();

    //Every arg will be passed through a we account for that size here.
    int argumentSize = argumentCount * pointerSize;
    tag = "Size of arguments for function. Count: " + argumentCount;
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(argumentSize, tag));

    tag = "Workspace size as extra parameter to function.";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(wsSize, tag));

    int size = suTable.get(currentFunction);
    tag = "Su table size for this function.";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag));

    //Recurse on all our children and add their values.
    stat.visit(this);

    //Add value to the function which contain this ParBlock.
    int parStatmentSize = nodesPerFunction.sumEntries(currentFunction);
    tag = String.format("Function %s call in parallel.", currentFunction);
    nodesPerFunction.addNode(callerFunction, new ValueAndTag(parStatmentSize, tag));

    //This line is super important! We are back to allocating for our caller function.
    currentFunction = callerFunction;

    //LightProcBarrierInit.
    size = suTable.get("LightProcBarrierInit");
    tag = "Procar calls LightProcBarrierInit: LightProcBarrierInit size.";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag));

    //LightProcStart.
    size = suTable.get("LightProcStart");
    tag = "ProcPar calls LightProcStart: LightProcStart size.";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag));
    
    //LightProcBarrierWait.
    size = suTable.get("LightProcBarrierWait");
    tag = "ProcPar calls LightProcBarrierWait: LightProcBarrierWait size.";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag));


    return null;
  }
  //========================================================================================
  /**
   * ChannelReadExpr require some additional size for the temporary variable it calls as well
   * as the function it calls. TODO: As of now it calls channelInInt but this needs to be
   * generalized for all types using a different function.
   */
  public Void visitChannelReadExpr(ChannelReadExpr cr){
    Log.log(cr.line + ": Visiting a ChannelReadExpr.");
    /*TODO: This needs to be changed to allocate as much memory as we need for this
      specific type.*/
    int typeSize = 4;

    String tag = "Channel ReadExpr: temp variable to hold results at: " + cr.line + ".";
    ValueAndTag vt = new ValueAndTag(typeSize, tag);
    nodesPerFunction.addNode(currentFunction, vt);

    int value = suTable.get("ChanInInt");
    tag = "Channel ReadExpr (ChanInInt) at: " + cr.line + ".";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(value, tag));

    //Call chain specifies we must call ChanIn.
    value = suTable.get("ChanIn");
    tag = "Channel ReadExpr (ChanIn) at: " + cr.line + ".";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(value, tag));

    //ChannelIn calls the ccsp kernel.
    value = suTable.get("ccsp_kernel_call");
    tag = "Channel ReadExpr (ccsp_kerner_call) at: " + cr.line + ".";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(value, tag));

    //Call chain specifies we must call ChanInWord.
    value = suTable.get("ChanInWord");
    tag = "Channel ReadExpr (ChanInWord) at: " + cr.line + ".";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(value, tag));

    return null;
  }
  //========================================================================================
  /**
   * TODO: As of now it calls ChanOutInt but this needs to be generalized for all types
   * using a different function.
   */
  public Void visitChannelWriteStat(ChannelWriteStat cw){
    Log.log(cw.line + ": Visiting a ChannelWriteStat.");
    /*TODO: This needs to be changed to allocate as much memory as we need for this
      specific type.*/
    int typeSize = 4;

    int value = suTable.get("ChanOutInt");
    String tag = "ChannelWriteStat (ChanOutInt) at: " + cw.line + ".";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(value, tag));

    //Call chain specifies we must call ChanIn.
    value = suTable.get("ChanOutWord");
    tag = "ChannelWriteStat (ChanOutWord) at: " + cw.line + ".";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(value, tag));

    //ChannelIn calls the ccsp kernel.
    value = suTable.get("ccsp_kernel_call");
    tag = "ChannelWriteStat (ccsp_kerner_call) at: " + cw.line + ".";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(value, tag));

    return null;
  }
  //========================================================================================
  /**
   * ParBlocks are another structure that requires stack space. We convert ParBlocks into
   * functions during the code generation phase. That is, for every statement in the
   * ParBlock that statement is wrapped (like a taco) in a function call where all
   * variables are passed to the function as pointers. This newly created functions is then
   * passed to the ProPar function to be ran in parallel. Hence we need to allocate the
   * proper size of each statement in the parBlock.
   */
  public Void visitParBlock(ParBlock pb){
    /*Whoever called us will need space to fit all our ParBlock related statements and
      we also need to set the size for all the functions we created. */
    Log.log(pb.line + ": Visiting ParBlock");

    final int pointerSize = 4;
    //Add nodes for the each statement as well as for function we are in.
    Sequence<Statement> stats = pb.stats();
    //We will be calculating the size of a function that is not in the AST hence we have
    //to save the current function and prentend we are on a new function.
    String callerFunction = currentFunction;

    //Iterate through statements and calculate their size:
    for(Statement myStat : stats){
      //By convention, this will be the name of this function. First we add one to the
      //entry in our hash table and get the name.
      incrementEntry(parBlockStmtCounts, callerFunction, 1);
      int nameNumber = parBlockStmtCounts.get(callerFunction);
      //This must be set to the class variable current function as the .visit(this) will
      //expect it to be! So don't change :b
      currentFunction = callerFunction + "_ParBlockStmt" + nameNumber;
      String tag;

      //Count the variables used as these will be passed as in as pointers.
      LinkedList<NameExpr> myNames = new LinkedList();
      myStat.visit(new NameCollector(myNames));
      int argumentCount = myNames.size();

      //Every arg will be passed through a ProcPar() call, we account for that size here.
      //ProcGetParam is not needed as it's actually a macro.
      int argumentSize = argumentCount * pointerSize;
      tag = "Size of arguments for function. Count: " + argumentCount;
      nodesPerFunction.addNode(currentFunction, new ValueAndTag(argumentSize, tag));

      tag = "Workspace size as extra parameter to function.";
      nodesPerFunction.addNode(currentFunction, new ValueAndTag(wsSize, tag));

      int size = suTable.get(currentFunction);
      tag = "Su table size for this function.";
      nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag));

      //Recurse on all our children and add their values.
      myStat.visit(this);

      /*Workspace allocated for this function. based from the WORKSPACE_SIZE:
        #define WORKSPACE_SIZE(args, stack)                             \
	((args) + (stack) + CIF_PROCESS_WORDS + 2 + CIF_STACK_LINKAGE + (CIF_STACK_ALIGN - 1))

        After the  preprocessor:
        ((args) + (stack) + 8 + 2 + 1 + (4 - 1))

        The 14 and the arguments are not taken into account, as these are static values
        which can be computed before AllocateStackSize is called, hence they are counted
        in the su table entry for the function which contains this Par block. Only thing
        we have to worry about is the stack argument which is exactly what this class
        attempts to allocate.
      */

      //Add value to the function which contain this ParBlock.
      int parStatmentSize = nodesPerFunction.sumEntries(currentFunction);
      tag = String.format("Function %s call in parallel.", currentFunction);
      nodesPerFunction.addNode(callerFunction, new ValueAndTag(parStatmentSize, tag));
    }

    //This line is super important! We are back to allocating for our caller function.
    currentFunction = callerFunction;

    int size; String tag;

    size = suTable.get("ProcPar");
    tag = "SuTable entry for ProcPar.";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag));

    //Note we created a Workspace parWs[n]; yet this is determinded statically based on
    //the number of statments in our parBlock so it is accounted for in the size of the
    //su table since it's a stack allocated array.

    //Light Proc init size.
    size = suTable.get("LightProcInit");
    tag = "Parblocks call: LightProcInit; LightProcInit size.";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag, true));

    //ProcPar calls the following:
    //LightProcBarrierInit.
    size = suTable.get("LightProcBarrierInit");
    tag = "Procar calls LightProcBarrierInit: LightProcBarrierInit size.";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag));
    //LightProcStart.
    size = suTable.get("LightProcStart");
    tag = "ProcPar calls LightProcStart: LightProcStart size.";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag));
    //LightProcBarrierWait.
    size = suTable.get("LightProcBarrierWait");
    tag = "ProcPar calls LightProcBarrierWait: LightProcBarrierWait size.";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag));

    return null;
  }
  //========================================================================================
  /**
   * Timeouts call the TimerDelay function! Make sure to allocate memory for that here.
   */
  public Void visitTimeoutStat(TimeoutStat ts){
    Log.log(ts.line + ": Visiting an TimeoutStat!");
    String tag;
    int size;
        
    //Calls made by CCSP for timing out:
    size = suTable.get("TimerDelay");
    tag = "TimeoutStat call: TimerDelay().";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag, true));

    size = suTable.get("TimerRead");
    tag = "TimeoutStat call: TimerRead().";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag, true));

    size = suTable.get("TimerWait");
    tag = "TimeoutStat call: TimerWait().";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag, true));

    size = suTable.get("TimerWait");
    tag = "TimeoutStat call: TimerWait().";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag, true));

    return null;
  }
  //========================================================================================
    public Void visitSyncStat(SyncStat st) {
    Log.log(st.line + ": Visiting a SyncStat");
    String tag;
    int size;
        
    //Call made by CCSP to wait on a barrier.
    size = suTable.get("LightProcBarrierWait");
    tag = "SyncStat call: LightProcBarrierWait().";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag, true));
    
    return null;
  }
  //========================================================================================
  /**
   * The AltStat create several extra variables nedeed for CCSP. We need to accout for their
   * memory here!
   */
  public Void visitAltStat(AltStat as) {
    Log.log(as.line + ": Visiting an AltStat!");
    Sequence<AltCase> altCaseList = as.body();
    int count = altCaseList.size();
    boolean hasTimeout = false;

    int size = 0;
    String tag = null;
    
    //If the alt uses timeout we must use a different function to invoke the alt.
    for(int i = 0; i < count; i++){
      AltCase altCase = altCaseList.getElementN(i);
      if(CodeGeneratorC.caseIsTimeout(altCase)){
	 hasTimeout = true;
	 break;
	}
    }

    //Calls to CCSP functions!
    if(hasTimeout == true){
      size = suTable.get("TimerAlt");
      tag = "AltStat call: TimerAlt()";
      nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag, true));

      //We have an alt timeout case so we know it must be intialized.
      size = suTable.get("AltEnableTimer");
      tag = "AltStat call: AltEnableTimer";
      nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag, true));

      //If did an AltEnableTimer we must also do an AltDisableTimer...
      size = suTable.get("AltDisableTimer");
      tag = "AltStat call: AltDisableTimer";
      nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag, true));

      //A timer read automatically happens:
      size = suTable.get("TimerRead");
      tag = "AltStat call: TimerRead";
      nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag, true));
      
    }else{
      size = suTable.get("Alt");
      tag = "AltStat call: Alt";
      nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag, true));
    }

    //The entire switch statement works based of AltEnd()
    size = suTable.get("AltEnd");
    tag = "AltStat call: AltEnd";
    nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag, true));

    boolean hasChannelCase = false;
    
    //If we have any Channel alt cases we make this:
    for(AltCase altCase : altCaseList)
      if(CodeGeneratorC.caseIsChannel(altCase) == true)
	hasChannelCase = true;

    //Now add this memory if needed...
    if(hasChannelCase == true){
      size = suTable.get("AltEnableChannel");
      tag = "AltStat call: AltEnableChannel";
      nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag, true));
      
      size = suTable.get("AltDisableChannel");
      tag = "AltStat call: AltDisableChannel";
      nodesPerFunction.addNode(currentFunction, new ValueAndTag(size, tag, true));
    }

    //Now visit our children:
    altCaseList.visit(this);
    
    //TODO: finish implementing!!!
    return null;
    
  }
  //========================================================================================
  /**
   * Given a hashtable of and the function name it will increment the counter that is the
   * value of that hashtable at for that function name by the value given.
   * If the entry was empty it will add it to our table.
   * @param table: Hashtable of function names and size.
   * @param functionName: function to increment value of.
   * @param amount: amount to increase entry by.
   * @return void.
   */
  private void incrementEntry(Hashtable<String, Integer> table, String functionName,
                              int amount){
    //If we have the key increment the value at that spot.
    if(table.containsKey(functionName) == true){
      Integer integer = table.get(functionName) + amount;
      table.put(functionName, integer);
    }
    else
      table.put(functionName, amount);

    return;
  }
  //========================================================================================
}


//========================================================================================
/**
 * This Class is only used by the AllocateStackSize class to count the number of local
 * inside a function frame and get their size in bytes . We must know this to make sure we
 * always allocate enough space on the stack for the function frame. We recurse on all
 * children of this function frame. When we hit a Invocation (which has it's own function
 * frame) we stop. This should be called only on a ProcTypeDecl;
 * TODO: this class will create garbage for other types of statements. Should we somehow
 * enforce to stop this?
 */
//========================================================================================
class LocalsAndParamsSize extends Visitor<Void>{
  /** Total size in bytes of all local within this function frame. This is global for
   * simplicity of adding to it... */
  private MutableInt myInt = null;

  //========================================================================================
  /**
   * Given a Integer object it will populate it with the total size in bytes needed for
   * local of this function frame.
   */
  public LocalsAndParamsSize(MutableInt myInt){
    Log.log("  Counting Parameters and local variables for this stack frame...");
    if(myInt == null)
      Error.error("You passed a null Integer object!");

    this.myInt = myInt;
    return;
  }
  //========================================================================================
  /**
   * When we hit a LocalDecl we add it's size to our global count. TODO: Add all primitive
   * types and further we must eventually create a method to break down more complicated
   * objects (like records) and count it's individual components.
   */
  public Void visitLocalDecl(LocalDecl ld){
    String message = "  %s: Visiting Local Decl (%s %s)";
    Log.log(String.format(message, ld.line, ld.type().typeName(), ld.var().name().getname()));

    Type type = ld.type();
    int byteSize = 0;

    if(type instanceof PrimitiveType)
      byteSize = ((PrimitiveType) type).byteSizeC();
    else if(type instanceof ChannelType)
      byteSize = ((ChannelType) type).byteSizeC();
    else if(type instanceof ArrayType)
      byteSize = ((ArrayType) type).byteSizeC();
    else
      Error.error(ld, "Could not figure out byte size of Local Variable.");

    Log.log("    Size: " + byteSize);
    myInt.add(byteSize);

    return null;
  }
  //========================================================================================
  /**We hit a parameter count! */
    public Void visitParamDecl(ParamDecl pd){
    String message = "  %s: Visiting ParamDecl (%s %s)";
    Log.log( String.format(message, pd.line, pd.name(), pd.type().typeName()) );

    Type type = pd.type();
    int byteSize = 0;

    if(type instanceof PrimitiveType)
      byteSize = ((PrimitiveType) type).byteSizeC();
    else if(type instanceof ChannelType)
      byteSize = ((ChannelType) type).byteSizeC();
    else if(type instanceof ChannelEndType)
      byteSize = ((ChannelEndType) type).byteSizeC();
    else if (type instanceof ArrayType)
      byteSize = ((ArrayType) type).byteSizeC();
    else
      Error.error(pd, "Could not figure out byte size of Local Variable.");

    Log.log("    Size: " + byteSize);
    myInt.add(byteSize);

    return null;
  }
  //========================================================================================
  /**
   * We only want locals in this scope so if we hit a function invocation we stop.
   */
  public Void visitInvocation(Invocation in){
    return null;
  }
  //========================================================================================
}

//========================================================================================
/**
 * We need a simple int object to pass in.
 */
class MutableInt{
  private int x;

  public MutableInt(){ x = 0; return; }

  public void add(int y){ x += y; return; }

  public int val(){ return x; }
}
//========================================================================================
