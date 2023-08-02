package entity;

public class ConflictPair {
	
	private int rowIndex1;
	private int rowIndex2;

	public ConflictPair(int rowIndex1, int rowIndex2) {
		setRowIndex1(rowIndex1);
		setRowIndex2(rowIndex2);
	}

	public int getRowIndex1() {
		return rowIndex1;
	}

	public void setRowIndex1(int rowIndex1) {
		this.rowIndex1 = rowIndex1;
	}

	public int getRowIndex2() {
		return rowIndex2;
	}

	public void setRowIndex2(int rowIndex2) {
		this.rowIndex2 = rowIndex2;
	}

}
