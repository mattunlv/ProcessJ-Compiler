package Instrument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.CheckClassAdapter;

/**
 * This class does the necessary bytecode
 * re-writing so that the yields in ProcessJ
 * work correctly.
 *  
 */
public class Instrumenter {

  static final String MARK_YIELD = "yield";
  static final String MARK_RESUME = "resume";
  static final String MARK_LABEL = "label";

  public String fullPath = "";
  
  public Instrumenter(String folder) {
    this.fullPath = Instrumenter.class.getResource("../"+ folder +"/").getPath();
    
    System.out.println("================================");
    System.out.println("*  Instrumenting classes in:   *");
    System.out.println("================================");
    System.out.println("Path:" + fullPath);
    System.out.println("--------------------------------");
  
  }

  public void execute() throws Exception {

    File directory = new File(fullPath);
    File[] directoryListing = directory.listFiles();

    if (directoryListing != null) {
      for (File file: directoryListing) {
        if (file.isFile() && isClassFile(file)){
          
          System.out.println("Instrumenting => " + file.getName());
          FileInputStream is = new FileInputStream(file);
          ClassReader cr = new ClassReader(is);
      
          byte[] bytes = getClassBytes(cr, false);
      
          if (bytes != null) {
            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(bytes);
            fos.close();
          }
          is.close();
        }
      }
    }
    
    System.out.println("Done!!");
  }
  
  

  public byte[] getClassBytes(ClassReader cr, boolean changeFile)
      throws Exception {

    // lets see assembler code before transformation
//     ASMifierClassVisitor.main(new String[]{className.replace('/', '.')});

    ClassNode cn = new ClassNode();
    cr.accept(cn, 0);

    boolean changed = makeChanges(cn);

    if (changed) {
      return getNewClassBytes(cn);
    } else {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private byte[] getNewClassBytes(final ClassNode cn) {
    byte[] classBytes;
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//    ClassVisitor cv = new CheckClassAdapter(cw);
    cn.accept(cw);
    classBytes = cw.toByteArray();
    
    /*
     * Use this to see if changed bytecode will pass verifier. 
     */
//    verifyModifiedClass(cw);
    
    return classBytes;
  }

  public boolean makeChanges(final ClassNode cn) {

    final List<AbstractInsnNode> yields = new ArrayList<AbstractInsnNode>();
    final List<AbstractInsnNode> resumes = new ArrayList<AbstractInsnNode>();
    final List<AbstractInsnNode> labels = new ArrayList<AbstractInsnNode>();

    final Map<Integer, LabelNode> labelRefs = new HashMap<Integer, LabelNode>();

    boolean hasJumps = false;

    for (Object o : cn.methods) {

      MethodNode mn = (MethodNode) o;

      extractYieldData(cn, mn, yields, resumes, labels);

      if (yields.size() > 0) {

        hasJumps = true;
        
        insertLabelNodes(cn, mn, labels, labelRefs);

        // connect to which label!
        makeResumesToLabels(cn, mn, resumes, labelRefs);

        insertReturnOnYields(mn, yields);
        
//        cleanup(mn);
        
      }

      resumes.clear();
      yields.clear();
      labelRefs.clear();

    }
    return hasJumps;
  }
  
  private void extractYieldData(final ClassNode cn, final MethodNode mn, 
      final List<AbstractInsnNode> yields, final List<AbstractInsnNode> resumes
      , final List<AbstractInsnNode> labels) {

    final String workingClassName = cn.name.replace('.', '/');
    ListIterator<AbstractInsnNode> iterator = mn.instructions.iterator();

    while (iterator.hasNext()) {

      AbstractInsnNode insnNode = (AbstractInsnNode) iterator.next();

      if (insnNode.getType() == AbstractInsnNode.METHOD_INSN) {

        MethodInsnNode min = (MethodInsnNode) insnNode;

        if (min.owner.equals(workingClassName)) {

          if (min.name.equals(MARK_YIELD)) {
//        	  System.out.println("found yield!!");
            yields.add(min);

          } else if (min.name.equals(MARK_RESUME)) {

            resumes.add(min);

          } else if (min.name.equals(MARK_LABEL)) {
            
            labels.add(min);
          }
        }
      }

    }
  }
  
  private void insertLabelNodes( final ClassNode cn, final MethodNode mn,
      final List<AbstractInsnNode> labels, 
      final Map<Integer, LabelNode> labelRefs) {

    labelRefs.clear();

    for (AbstractInsnNode node : labels) {
      AbstractInsnNode operandNode = (AbstractInsnNode) node.getPrevious();

      int labelNumber = getOperand(cn.name, operandNode);
      
      AbstractInsnNode loadANode = operandNode.getPrevious(); // we need to back up one more to before the push instruction

      LabelNode labelNode = new LabelNode();
      labelRefs.put(labelNumber, labelNode);

      mn.instructions.insertBefore(loadANode, labelNode);
    }
  }
  
  
  private void makeResumesToLabels(final ClassNode cn, final MethodNode mn, 
      final List<AbstractInsnNode> resumes, final Map<Integer, LabelNode> myRefs) {

    // join resumes to labels 
    for (AbstractInsnNode node : resumes) {
      AbstractInsnNode operandNode = (AbstractInsnNode) node.getPrevious();

      int labelNumber = getOperand(cn.name, operandNode);
      LabelNode labelNode = myRefs.get(labelNumber);

      if (labelNode != null) {
        mn.instructions.insert(node, new JumpInsnNode(Opcodes.GOTO, labelNode));
      }
    }
  }
  
  private void insertReturnOnYields(final MethodNode mn, final List<AbstractInsnNode> yields) {
    //the label we want to insert just before the method return.
    LabelNode jumpDest = new LabelNode();

    /*
     * 03/07/2016
     * 
     * Insert a jump destination before the last instruction 
     * of the method. The last instruction is always a return 
     * opcode (statement). But it can be either one of these
     * based on what type method returns: return, ireturn, 
     * lreturn, freturn, dreturn and areturn. These need to be 
     * handled differently as they can have related preceding
     * operations.
     * 
     *  Example: 
     *  1. ireturn that returns constant value can have 
     *  iconst_<x> or bipush or sipush or ldc etc.
     *  2. ireturn that returns a variable value will have
     *  aload_0 and getfield instructions before it.
     *       
     *  6:00pm EDIT
     *  Since ProcessJ processes do not return any value, but just
     *  void, we might not need anything other than RETURN. But 
     *  for multi-core scheduler, we are thinking of returning a 
     *  List<Process>. Then ARETURN might be needed. Else they aren't.
     *  
     *  Will leave them in anything. Won't hurt.
     */
    AbstractInsnNode retNode = mn.instructions.getLast().getPrevious();

    int ret_opcode = retNode.getOpcode();

    switch(ret_opcode) {
      case Opcodes.RETURN: //doesn't have any preceding operation
        mn.instructions.insertBefore(retNode, jumpDest);
        break;
      case Opcodes.IRETURN:
      case Opcodes.DRETURN:
      case Opcodes.FRETURN:
      case Opcodes.LRETURN:
      case Opcodes.ARETURN:

        int ret_prev_opcode = retNode.getPrevious().getOpcode();

        if (ret_prev_opcode == Opcodes.GETFIELD) {
          //if previous opcode is a getfield, then there must be aload_0 before it.
          mn.instructions.insertBefore(retNode.getPrevious().getPrevious(), jumpDest);
        } else {
          //if not, there is only one preceding operation like bipush, ldc, etc.
          mn.instructions.insertBefore(retNode.getPrevious(), jumpDest);
        }
        break;
      default:
        jumpDest = null;
    }
    
    if (jumpDest != null) {
      for (AbstractInsnNode node : yields) {
        //insert goto jumpDest after each yield as we want the method to return.
        mn.instructions.insert(node, new JumpInsnNode(Opcodes.GOTO, jumpDest));
      }
    }
  }
  
  private void verifyModifiedClass(ClassWriter cw) {

    System.out.println("==============================");
    System.out.println("*  Verifying modified class  *");
    System.out.println("==============================");

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    /*
     * It won't be exactly the same verification as JVM does, but it runs 
     * data flow analysis for the code of each method and checks that 
     * expectations are met for each method instruction.
     */
    CheckClassAdapter.verify(new ClassReader(cw.toByteArray()), true, pw);

    //if result is empty, verification passed.
    System.out.println("Result:" + sw.toString());
  }
  
  private boolean isClassFile(File file) throws Exception {
    String name = file.getName();
      return "class".equals(name.substring(name.lastIndexOf(".") + 1));
  }
  
  private int getOperand(String clazz, final AbstractInsnNode node) {
        
        int labelNumber = -1;
        
        switch(node.getType()) {
          case AbstractInsnNode.VAR_INSN: 
            /*
             * Limitation: variables cannot be used to
             * yield/resume. e.g. yield(x)
             * 
             * There is no way to get the actual value
             * of x. So, this code does nothing for now. 
             * If need be, this can be looked into.
             */
            VarInsnNode vis = (VarInsnNode) node;
            int opvis = vis.getOpcode();
                int var = vis.var;
                System.out.println(" > vis opcode=" + opvis + " value=" + var);
            break;
          case AbstractInsnNode.INT_INSN: //handles yields on values >5
            IntInsnNode iin = (IntInsnNode) node;
                labelNumber = iin.operand;
            break;
          case AbstractInsnNode.INSN: //handles yields on -1 to 5
            InsnNode insn = (InsnNode) node;
                int opcode = insn.getOpcode();
                
                switch(opcode){
                    case Opcodes.ICONST_M1: //value=2 (probably don't need)
                        labelNumber = -1;
                        break;
                    case Opcodes.ICONST_0: //value=3
                        labelNumber = 0;
                        break;
                    case Opcodes.ICONST_1: //value=4
                        labelNumber = 1;
                        break;
                    case Opcodes.ICONST_2: //value=5
                        labelNumber = 2;
                        break;
                    case Opcodes.ICONST_3: //value=6
                        labelNumber = 3;
                        break;
                    case Opcodes.ICONST_4: //value=7
                        labelNumber = 4;
                        break;
                    case Opcodes.ICONST_5: //value=8
                        labelNumber = 5;
                        break;
                }
            break;
        }

        return labelNumber;
    }
  
  private void cleanup(MethodNode mn) {
    mn.instructions.resetLabels();
    ListIterator it = mn.instructions.iterator();
    List<AbstractInsnNode> remove = new ArrayList<AbstractInsnNode>();
    while(it.hasNext()) {
      AbstractInsnNode n = (AbstractInsnNode)it.next();
      if (n.getOpcode() == Opcodes.ATHROW)
        System.out.println("athrow found");//we want to remove this
      else if (n.getOpcode() == Opcodes.NOP)
        System.out.println("nop found");//we want to remove this
    }
    
    //for(AbstractInsnNode n1: remove)
      //mn.instructions.remove(n1);
  }
  
  public static void main(String[] args) {
    try {

      if (args.length == 1 ) {
        Instrumenter obj = new Instrumenter(args[0]);
        obj.execute();
      } else {
        System.out.println("Print Usage!!!");
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
