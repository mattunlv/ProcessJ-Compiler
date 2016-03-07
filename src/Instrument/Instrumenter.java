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
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.CheckClassAdapter;

public class Instrumenter {

	static final String MARK_JUMP = "jump";
	static final String MARK_LABEL = "label";
	static final String MARK_YIELD = "yield";
	public static final int Op_goto = 167;
	public static final int Op_lookupswitch = 171;
	public static final int Op_return = 177;
	public static final int Op_iConst_0 = 3;
	public static final int Op_iConst_1 = 4;

	public String fullPath = "";
	

	public Instrumenter(String folder) {
		this.fullPath = Instrumenter.class.getResource("../"+ folder +"/").getPath();
		System.out.println("===================");
		System.out.println("== Instrumenting ==");
		System.out.println("===================");
		System.out.println("Path:" + fullPath);
	
	}

	public void execute() throws Exception {

		File directory = new File(fullPath);
		File[] directoryListing = directory.listFiles();
		File copied = null;

		if (directoryListing != null) {
			for (File file: directoryListing) {
				if (file.isFile() && isClassFile(file)){
					System.out.println("Copying...:: " + file.getName());
					
					copied = copy(file.getName(), createCopyName(file.getName()));
					
					System.out.println("Instrumenting...:: " + file.getName());
					FileInputStream is = new FileInputStream(copied);
					// make new class reader
					ClassReader cr = new ClassReader(is);
			
					// make new class visitor
					byte[] bytes = getClassBytes(cr, false);
			
					if (bytes != null) {
			
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(bytes);
						fos.close();
					}
				}
			}
		}
		
		System.out.println("Done!!");
	}
	
	public boolean isClassFile(File file) throws Exception {
		String name = file.getName();
	    return "class".equals(name.substring(name.lastIndexOf(".") + 1));
	}
	
	public String createCopyName(String filename) {
		String temp = filename.substring(0, filename.lastIndexOf("."));
		return temp+"-copy.class";
	}

	public File copy(String from, String to) throws Exception {
		File src = new File(fullPath+from);
		File dest = new File(fullPath+to);

		FileInputStream is = new FileInputStream(src);

		ClassReader cr = new ClassReader(is);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cr.accept(cw, 0);

		FileOutputStream fos = new FileOutputStream(dest);
		fos.write(cw.toByteArray());
		fos.close();

		dest.deleteOnExit();
		return dest;
	}

	public byte[] getClassBytes(ClassReader cr, boolean changeFile)
			throws Exception {

		// lets see assembler code before transformation
		//	        ASMifierClassVisitor.main(new String[]{className.replace('/', '.')});

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
		cn.accept(cw);
		classBytes = cw.toByteArray();
		
		/*
		 * Use this to see if changed bytecode will pass verifier. 
		 */
//		verifyModifiedClass(cw);
		
		return classBytes;
	}

	private void verifyModifiedClass(ClassWriter cw) {

		System.out.println("==============================");
		System.out.println("*  Verifying modified class  *");
		System.out.println("==============================");

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		CheckClassAdapter.verify(new ClassReader(cw.toByteArray()), true, pw);

		System.out.println("Result:" + sw.toString());
	}

	public boolean makeChanges(final ClassNode cn) {

		final List<AbstractInsnNode> labels = new ArrayList<AbstractInsnNode>();
		final List<AbstractInsnNode> jumps = new ArrayList<AbstractInsnNode>();
		final List<AbstractInsnNode> yields = new ArrayList<AbstractInsnNode>();
		final HashMap<Integer, LabelNode> switchMap = new HashMap<Integer, LabelNode>();

		final Map<Integer, LabelNode> labelRefs = new HashMap<Integer, LabelNode>();

		boolean hasJumps = false;

		for (Object o : cn.methods) {

			MethodNode mn = (MethodNode) o;

			extractJumpData(labels, cn, jumps, mn, yields, switchMap);

//			if (jumps.size() > 0) {
//				hasJumps = true;
//
//				insertLabelNodes(labelRefs, labels, mn, cn);
//
//				// connect to which lables!
//				makeJumpsToLabels(jumps, mn, labelRefs, cn);
//				
//				insertReturnStatsAtYields(yields, mn);
//			}

			
			if (jumps.size() > 0) {
				hasJumps = true;

				

				insertLabelNodes(labelRefs, yields, mn, cn);

				// connect to which labels!
				makeJumpsToLabels(jumps, mn, labelRefs, cn);
//				makeJumpsToYieldLabels(switchMap, mn, labelRefs, cn);
				
				insertReturnStatsAtYields(yields, mn);
				
				cleanup(mn);
				
			}
//			
			switchMap.clear(); 
			jumps.clear();
			labels.clear();
			yields.clear();
			labelRefs.clear();

		}
		return hasJumps;
	}
	
	private void cleanup(MethodNode mn) {
		mn.instructions.resetLabels();
		ListIterator it = mn.instructions.iterator();
		List<AbstractInsnNode> remove = new ArrayList<AbstractInsnNode>();
		while(it.hasNext()) {
			AbstractInsnNode n = (AbstractInsnNode)it.next();
			if (n.getOpcode() == Opcodes.ATHROW)
				System.out.println("athrow found");
			else if (n.getOpcode() == Opcodes.NOP)
				System.out.println("nop found");
		}
		
//		for(AbstractInsnNode n1: remove)
//			mn.instructions.remove(n1);
	}
	
	private void makeJumpsToYieldLabels(final Map<Integer, LabelNode> switchMap,
			final MethodNode mn, final Map<Integer, LabelNode> myRefs,
			final ClassNode cn) {

		System.out.println("makeJumpsToYieldLabels....");
		// join labels to jump data
//		for (AbstractInsnNode node : jumps) {
//			AbstractInsnNode pNode = (AbstractInsnNode) node.getPrevious();
//
//			int labelNumber = getOperand(cn.name, pNode);
//			LabelNode labelNode = myRefs.get(labelNumber);
//
//			if (labelNode != null) {
//				JumpInsnNode jumpNode = new JumpInsnNode(Op_goto, labelNode);
//				mn.instructions.insert(node, jumpNode);
//			}
//		}
		
		for (Integer index : switchMap.keySet()) {
			LabelNode switchLabelNode = switchMap.get(index);

//			AbstractInsnNode pNode = (AbstractInsnNode) node.getPrevious();
//			int labelNumber = getOperand(cn.name, pNode);
			LabelNode labelNode = myRefs.get(index);

			if (labelNode != null) {
				JumpInsnNode jumpNode = new JumpInsnNode(Op_goto, labelNode);

				mn.instructions.insert(switchLabelNode.getNext(),jumpNode);
//				AbstractInsnNode temp = switchLabelNode.getNext().getNext().getNext();
//
//				System.out.println(index + "::" + mn.instructions.indexOf(switchLabelNode));
//
//				if (temp instanceof JumpInsnNode) {
//					mn.instructions.insert(temp,jumpNode);
//				} else {
//					mn.instructions.insert(temp.getPrevious(),jumpNode);
//				}
			}
		}
		
	}
	
	private void makeJumpsToLabels(final List<AbstractInsnNode> jumps,
			final MethodNode mn, final Map<Integer, LabelNode> myRefs,
			final ClassNode cn) {

		// join labels to jump data
		for (AbstractInsnNode node : jumps) {
			AbstractInsnNode pNode = (AbstractInsnNode) node.getPrevious();

			int labelNumber = getOperand(cn.name, pNode);
			LabelNode labelNode = myRefs.get(labelNumber);

			if (labelNode != null) {
				System.out.println("labelNumber=" + labelNumber);
				mn.instructions.insert(node, new JumpInsnNode(Op_goto, labelNode));
			}
		}
	}
	
	private void insertReturnStatsAtYields(final List<AbstractInsnNode> yields, final MethodNode mn) {
		InsnNode ret = new InsnNode(Op_return);
		LabelNode dummy = new LabelNode();
//		System.out.println("last opcode=" + mn.instructions.getLast().);
		mn.instructions.insertBefore(mn.instructions.getLast(), dummy);

		for (AbstractInsnNode node : yields) {
			mn.instructions.insert(node, new JumpInsnNode(Op_goto, dummy));
		}
	}
	
	
	
	private void insertLabelNodes(
			final Map<Integer, LabelNode> labelRefs,
			final List<AbstractInsnNode> labels, final MethodNode mn,
			final ClassNode cn) {

		labelRefs.clear();

		for (AbstractInsnNode node : labels) {
			AbstractInsnNode operandNode = (AbstractInsnNode) node.getPrevious();

			int labelNumber = getOperand(cn.name, operandNode);

			AbstractInsnNode loadANode = operandNode.getPrevious(); // we need to back up one more to before the push instruction

			LabelNode labelNode = new LabelNode();
			labelRefs.put(labelNumber, labelNode);

            System.out.println("NODE BEFORE LABEL INSERT: " + loadANode + "," + loadANode.getType() + "," + loadANode.getOpcode());

//			mn.instructions.insert(loadANode.getPrevious(), labelNode);
			mn.instructions.insert(node, labelNode);
			// mn.instructions.insertBefore(labelNode,new JumpInsnNode(167,labelNode));
		}
	}
	
	LookupSwitchInsnNode lsn = null;

	private void extractJumpData(final List<AbstractInsnNode> labels,
			final ClassNode cn, final List<AbstractInsnNode> jumps,
			final MethodNode mn, final List<AbstractInsnNode> yields,
			final HashMap<Integer, LabelNode> switchMap) {

		final String workingClassName = cn.name.replace('.', '/');

		ListIterator iterator = mn.instructions.iterator();

		while (iterator.hasNext()) {

			AbstractInsnNode INSN_NODE = (AbstractInsnNode) iterator.next();

//			if (INSN_NODE.getType() == INSN_NODE.LOOKUPSWITCH_INSN) {
//				
//				lsn = (LookupSwitchInsnNode) INSN_NODE;
//
//				LabelNode ln = lsn.dflt;
//				List keys = lsn.keys;
//				List lbls = lsn.labels;
//				
//				Label l = ln.getLabel();
////				System.out.println("label=" + l.toString());
//				
//				
////				System.out.println("Printing keys...");
////				for(int i = 0; i<keys.size(); i++) {
////					System.out.println(keys.get(i));
////				}
////				System.out.println("Printing labels size=" + lbls.size());
////				for(int i = 0; i<lbls.size(); i++) {
////					System.out.println(lbls.get(i));
////				}
//				
//				for (int i =0; i<keys.size(); i++) {
////					switchMap.put((Integer)keys.get(i), (Label)lbls.get(i));
//					if (lbls.get(i) instanceof JumpInsnNode)
//						System.out.println("jump instruction found");
//				}
//			}
			
			if (INSN_NODE.getType() == INSN_NODE.TABLESWITCH_INSN) {
				
				TableSwitchInsnNode lsn = (TableSwitchInsnNode) INSN_NODE;

				LabelNode ln = lsn.dflt;
				List<LabelNode> lbls = lsn.labels;
				
				for (int i =0; i<lbls.size(); i++) {
					switchMap.put(new Integer(i), lbls.get(i));
				}
			}

			if (INSN_NODE.getType() == INSN_NODE.METHOD_INSN) {

				MethodInsnNode min = (MethodInsnNode) INSN_NODE;

				if (min.owner.equals(workingClassName)) {

					if (min.name.equals(MARK_JUMP)) {

						jumps.add(min);
// 							AbstractInsnNode pNode = (AbstractInsnNode) min.getPrevious();
//	                        int labelNumber = getOperand(cn.name, pNode);
//	                        if (debug) {
//	                            System.out.println("******* FOUND JUMP : " + labelNumber);
//	                        }

					} else if (min.name.equals(MARK_LABEL)) {

						labels.add(min);

// 							AbstractInsnNode pNode = (AbstractInsnNode) min.getPrevious();
//	                        int labelNumber = getOperand(cn.name, pNode);
//	                        if (debug) {
//	                            System.out.println("******* FOUND JUMP : " + labelNumber);
//	                        }
					} else if (min.name.equals(MARK_YIELD)) {
						yields.add(min);
					}
				}
			}

		}
	}
	
	public int getOperand(String clazz, final AbstractInsnNode node) {
        
        int labelNumber = -1;
        
//        System.out.println(" > Node to be evaluated in switch is: " + node);
        
        if( node.getType()==node.VAR_INSN){
            VarInsnNode vis = (VarInsnNode) node;
            int opvis = vis.getOpcode();
            
            System.out.println(" > vis opcode=" + opvis);
        }
        
        if(node.getType()==node.INT_INSN){
            
            IntInsnNode iin = (IntInsnNode) node;
            labelNumber = iin.operand;
            
        }else if( node.getType()==node.INSN){
            
            
            InsnNode insn = (InsnNode) node;
            // System.out.println("INSN NODE: " + insn);
            // System.out.print("\t");
            
            int opcode = insn.getOpcode();
            
//            System.out.println(" > Opcode for jump is: " + opcode);
            switch(opcode){
//                case 2:
//                    labelNumber = -1;
//                    break;
                case 3:
                    labelNumber = 0;
                    break;
                    
                case 4:
                    labelNumber = 1;
                    break;
                    
                case 5:
                    labelNumber = 2;
                    break;
                    
                case 6:
                    labelNumber = 3;
                    break;
                case 7:
                    labelNumber = 4;
                    break;
                case 8:
                    labelNumber = 5;
                    break;
            }
        }
        return labelNumber;
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
