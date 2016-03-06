package Instrument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class Instrumenter {

	static final String MARK_JUMP = "jump";
	static final String MARK_LABEL = "label";
	public static final int SUSPEND = 0;
	public static final int RESUME = 1;
	public static final int Op_goto = 167;
	public static final int Op_iConst_0 = 3;
	public static final int Op_iConst_1 = 4;

	public String path = "/Users/cabel/Documents/unlv-thesis/coding/Java/processjcompiler-java/bin/";
	public String fullPath = "";

	public Instrumenter(String folder) {
		this.fullPath = path + folder + File.separator;
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

		return classBytes;
	}

	public boolean makeChanges(final ClassNode cn) {

		final List<AbstractInsnNode> labels = new ArrayList<AbstractInsnNode>();
		final List<AbstractInsnNode> jumps = new ArrayList<AbstractInsnNode>();

		final Map<Integer, LabelNode> labelRefs = new HashMap<Integer, LabelNode>();

		boolean hasJumps = false;

		for (Object o : cn.methods) {

			MethodNode mn = (MethodNode) o;

			extractJumpData(labels, cn, jumps, mn);

			if (jumps.size() > 0) {
				hasJumps = true;

				insertLabelNodes(labelRefs, labels, mn, cn);

				// connect to which lables!
				makeJumpsToLabels(jumps, mn, labelRefs, cn);
			}

			jumps.clear();
			labels.clear();
			labelRefs.clear();

		}
		return hasJumps;
	}

	private void makeJumpsToLabels(final List<AbstractInsnNode> jumps,
			final MethodNode mn, final Map<Integer, LabelNode> myRefs,
			final ClassNode cn) {

		// join labels to jump data
		for (AbstractInsnNode node : jumps) {
			AbstractInsnNode pNode = (AbstractInsnNode) node.getPrevious();

			InsnNode insn = (InsnNode) pNode;
			int opcode = insn.getOpcode();
			int labelNumber = -1;
			switch (opcode) {
				case Op_iConst_0:
					labelNumber = SUSPEND;
					break;
				case Op_iConst_1:
					labelNumber = RESUME;
					break;
			}

			LabelNode labelNode = myRefs.get(labelNumber);

			if (labelNode != null) {
				JumpInsnNode jumpNode = new JumpInsnNode(Op_goto, labelNode);
				mn.instructions.insert(node, jumpNode);
			}
		}
	}

	private void insertLabelNodes(
			final Map<Integer, LabelNode> labelRefs,
			final List<AbstractInsnNode> labels, final MethodNode mn,
			final ClassNode cn) {

		labelRefs.clear();

		for (AbstractInsnNode node : labels) {
			AbstractInsnNode operandNode = (AbstractInsnNode) node
					.getPrevious();

			InsnNode insn = (InsnNode) operandNode;
			int opcode = insn.getOpcode();
			int labelNumber = -1;
			switch (opcode) {
				case Op_iConst_0:
					labelNumber = SUSPEND;
					break;
				case Op_iConst_1:
					labelNumber = RESUME;
					break;
			}

			AbstractInsnNode loadANode = operandNode.getPrevious(); // we need to back up one more to before the push instruction

			LabelNode labelNode = new LabelNode();
			labelRefs.put(labelNumber, labelNode);

			mn.instructions.insert(loadANode.getPrevious(), labelNode);
			// mn.instructions.insertBefore(labelNode,new JumpInsnNode(167,labelNode));
		}
	}

	private void extractJumpData(final List<AbstractInsnNode> labels,
			final ClassNode cn, final List<AbstractInsnNode> jumps,
			final MethodNode mn) {

		final String workingClassName = cn.name.replace('.', '/');

		ListIterator iterator = mn.instructions.iterator();

		while (iterator.hasNext()) {

			AbstractInsnNode WHILE_NODE = (AbstractInsnNode) iterator.next();

			if (WHILE_NODE.getType() == WHILE_NODE.METHOD_INSN) {

				MethodInsnNode min = (MethodInsnNode) WHILE_NODE;

				if (min.owner.equals(workingClassName)) {

					if (min.name.equals(MARK_JUMP)) {

						jumps.add(min);

					} else if (min.name.equals(MARK_LABEL)) {

						labels.add(min);

					}
				}
			}

		}
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
