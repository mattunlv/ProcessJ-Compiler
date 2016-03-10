package ProcessJ.runtime;

public class Activation {
	private Object[] data;
	private int runLabel;

	public Activation(int noLocals) {
		data = new Object[noLocals];
		runLabel = 0;
	}

	public Activation(Object[] params, int noLocals) {
		data = new Object[noLocals];
		for (int i = 0; i < params.length; i++)
			data[i] = params[i];
		runLabel = 0;
	}

	public Activation(int noLocals, int runLabel) {
		data = new Object[noLocals];
		this.runLabel = runLabel;
	}

	public Activation(Object[] params, int noLocals, int runLabel) {
		data = new Object[noLocals];
		for (int i = 0; i < params.length; i++)
			data[i] = params[i];
		this.runLabel = runLabel;
	}

	public int getRunLabel() {
		return runLabel;
	}

	public int setRunLabel(int runLabel) {
		this.runLabel = runLabel;
		return runLabel;
	}

	public Object getLocal(int address) {
		return data[address];
	}

	public void setLocal(int address, Object value) {
		data[address] = value;
	}
}
