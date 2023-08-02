package util;

import java.util.Comparator;

import entity.EdgePair;

public class ComparatorEdgePair implements Comparator<EdgePair> {

	@Override
	public int compare(EdgePair ep1, EdgePair ep2) {
		// TODO Auto-generated method stub
		double edgeDensity1 = ep1.getEdgeDensity();
		double edgeDensity2 = ep2.getEdgeDensity();
		if (edgeDensity1 > edgeDensity2) {
			return -1;
		} else if (edgeDensity1 < edgeDensity2) {
			return 1;
		}
		return 0;
	}

}
