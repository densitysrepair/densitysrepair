package entity;

public class CFD {

	private int[] attrXs;
	private int attrY;
	private boolean[] isConstantXs;
	private boolean isConstantY;
	private String[] constantXs;
	private String constantY;

	public CFD() {

	}

	public CFD(int[] attrXs, int attrY, boolean[] isConstantXs,boolean isConstantY, String[] constantXs,
			String constantY) {
		setAttrXs(attrXs);
		setAttrY(attrY);
		setIsConstantXs(isConstantXs);
		setIsConstantY(isConstantY);
		setConstantXs(constantXs);
		setConstantY(constantY);
	}

	public boolean[] getIsConstantXs() {
		return isConstantXs;
	}

	public void setIsConstantXs(boolean[] isConstantXs) {
		this.isConstantXs = isConstantXs;
	}

	public boolean getIsConstantY() {
		return isConstantY;
	}

	public void setIsConstantY(boolean isConstantY) {
		this.isConstantY = isConstantY;
	}

	public String[] getConstantXs() {
		return constantXs;
	}

	public void setConstantXs(String[] constantXs) {
		this.constantXs = constantXs;
	}

	public String getConstantY() {
		return constantY;
	}

	public void setConstantY(String constantY) {
		this.constantY = constantY;
	}

	public int[] getAttrXs() {
		return attrXs;
	}

	public void setAttrXs(int[] attrXs) {
		this.attrXs = attrXs;
	}

	public int getAttrY() {
		return attrY;
	}

	public void setAttrY(int attrY) {
		this.attrY = attrY;
	}

	public String toString() {
		StringBuffer extname = new StringBuffer();
		for (int xindex = 0; xindex < attrXs.length; ++xindex) {
			extname.append(attrXs[xindex] + ",");
		}
		extname.replace(extname.length() - 1, extname.length(), ":");
		extname.append(attrY);
		String cfdstr = extname.toString();
		return cfdstr;
	}


}
