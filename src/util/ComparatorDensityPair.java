package util;

import java.util.Comparator;

import entity.DensityPair;

public class ComparatorDensityPair implements Comparator<DensityPair> {

	@Override
	public int compare(DensityPair dp1, DensityPair dp2) {
		// TODO Auto-generated method stub
		double density1 = dp1.getDensity();
		double density2 = dp2.getDensity();
		if (density1 > density2) {
			return -1;
		} else if (density1 < density2) {
			return 1;
		}
		return 0;
	}

}
