package entity;

public class DensityPair {
	private int rowIndex;
	private double density;

	public DensityPair(int rowIndex, double density) {
		setRowIndex(rowIndex);
		setDensity(density);
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public double getDensity() {
		return density;
	}

	public void setDensity(double density) {
		this.density = density;
	}

}
