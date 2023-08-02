package entity;

public class Tuple {

	private int tid;
	private int attrNum;
	private String[] args;
	private String classLabel;

	public Tuple(int attrNum) {
		setAttrNum(attrNum);
		args = new String[attrNum];
	}

	public void setClassLabel(String classLabel) {
		this.classLabel = classLabel;
	}

	public String getClassLabel() {
		return classLabel;
	}

	public void buildTuple(int tid, String[] vals) {
		setTid(tid);

		if (vals.length != attrNum) {
			System.out.println("Inconsistent attrNum !");
		}

		for (int i = 0; i < attrNum; ++i) {
			args[i] = vals[i];
		}
	}

	public void setAttrNum(int attrNum) {
		this.attrNum = attrNum;
	}

	public int getAttrNum() {
		return attrNum;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public int getTid() {
		return tid;
	}

	public String[] getAllData() {
		return args;
	}

	public String getDataByIndex(int attrIndex) {
		return args[attrIndex];
	}

}
