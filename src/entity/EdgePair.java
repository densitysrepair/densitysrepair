package entity;

public class EdgePair {
	private double edgeDensity;
	private int rowIndex1;
	private int rowIndex2;

	public EdgePair(int rowIndex1, int rowIndex2, double edgeDensity) {
		setRowIndex1(rowIndex1);
		setRowIndex2(rowIndex2);
		setEdgeDensity(edgeDensity);
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

	public double getEdgeDensity() {
		return edgeDensity;
	}

	public void setEdgeDensity(double edgeDensity) {
		this.edgeDensity = edgeDensity;
	}

}
