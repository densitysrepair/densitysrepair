package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import entity.CFD;
import entity.ConflictPair;
import entity.Database;
import entity.EdgePair;
import util.Assist;
import util.ComparatorEdgePair;

public class Heuristic extends BaseDetect {

	private int K;
	private int[][] allKnnIndexes;
	private double[][] allKnnDensities;
	private double[][] allKnnDensities1;
	private double[][] allKnnDensities2;
	private double[] allTopKDensity;
	private ArrayList<Integer> attrIndexList;
	private ArrayList<Integer> conflictRowIndexList;
	private ArrayList<Integer> cleanRowIndexList;
	private ArrayList<Integer> detectedRowIndexList;
	private ArrayList<EdgePair> epList;
	private ArrayList<ConflictPair> conflictPairList;
	private HashMap<Integer, ArrayList<Integer>> confRowListMap;
	private ArrayList<CFD> cfdList;

	public Heuristic(Database db) {
		super(db);
		// TODO Auto-generated constructor stub
		conflictRowIndexList = new ArrayList<>();
		cleanRowIndexList = new ArrayList<>();
		detectedRowIndexList = new ArrayList<>();
		epList = new ArrayList<>();
		conflictPairList = new ArrayList<>();
		confRowListMap = new HashMap<>();
	}

	public void mainHeuristic() {
		initVals();
		detectConf();
		genKNeiWithDensity();
		genEdgePairList();
		detectErrorForEachEdge();
	}

	private void detectConf() {
		int ruleNum = cfdList.size();
		int size = db.getLength();
		for (int rowIndex1 = 0; rowIndex1 < size; rowIndex1++) {
			String[] data1 = dbVals[rowIndex1];
			ArrayList<Integer> confRowIndexList1;
			if (confRowListMap.containsKey(rowIndex1)) {
				confRowIndexList1 = confRowListMap.get(rowIndex1);
			} else {
				confRowIndexList1 = new ArrayList<>();
			}
			boolean isClean = true;
			for (int rowIndex2 = rowIndex1 + 1; rowIndex2 < size; rowIndex2++) {
				String[] data2 = dbVals[rowIndex2];
				ArrayList<Integer> confRowIndexList2;
				if (confRowListMap.containsKey(rowIndex2)) {
					confRowIndexList2 = confRowListMap.get(rowIndex2);
				} else {
					confRowIndexList2 = new ArrayList<>();
				}
				for (int ri = 0; ri < ruleNum; ri++) {
					CFD cfd = cfdList.get(ri);
					boolean isConflict = Assist.detectConfBtwTwoTp(data1, data2, cfd);
					if (isConflict) {
						confRowIndexList1.add(rowIndex2);
						confRowIndexList2.add(rowIndex1);
						isClean = false;
						ConflictPair conflictPair = new ConflictPair(rowIndex1, rowIndex2);
						conflictPairList.add(conflictPair);
						if (!conflictRowIndexList.contains(rowIndex1)) {
							conflictRowIndexList.add(rowIndex1);
						}
						if (!conflictRowIndexList.contains(rowIndex2)) {
							conflictRowIndexList.add(rowIndex2);
						}
						break;
					}
				}
				confRowListMap.put(rowIndex2, confRowIndexList2);
			}
			if (isClean && !conflictRowIndexList.contains(rowIndex1)) {
				cleanRowIndexList.add(rowIndex1);
			}
			confRowListMap.put(rowIndex1, confRowIndexList1);
		}
	}
	
	private void genKNeiWithDensity() {
		int size = db.getLength();
		int attrNum = db.getAttrNum();
		double[][] subDistances = new double[size][size];
		allKnnIndexes = new int[size][K];
		allTopKDensity = new double[size];
		allKnnDensities = new double[size][K];
		allKnnDensities1 = new double[size][K];
		allKnnDensities2 = new double[size][K];
		double[] knnDistances = new double[K];
		for (int rowIndex = 0; rowIndex < size; rowIndex++) {
			calDisWithTuplesFromList(rowIndex, subDistances[rowIndex], cleanRowIndexList);
			findCleanKnn(subDistances[rowIndex], allKnnIndexes[rowIndex], knnDistances, cleanRowIndexList);
			double totalDensity = 0, tmpDensity;
			for (int ki = 0; ki < K; ki++) {
				tmpDensity = attrNum - knnDistances[ki];
				allKnnDensities[rowIndex][ki] = tmpDensity;
				allKnnDensities1[rowIndex][ki] = tmpDensity;
				allKnnDensities2[rowIndex][ki] = tmpDensity;
				totalDensity += tmpDensity;
			}
			allTopKDensity[rowIndex] = totalDensity;
		}
	}
	
	private void genEdgePairList() {
		epList.clear();
		int confNum = conflictPairList.size();
		int rowIndex1, rowIndex2;
		double topKDensity1, topKDensity2;
		for (int cfi = 0; cfi < confNum; cfi++) {
			ConflictPair conflictPair = conflictPairList.get(cfi);
			rowIndex1 = conflictPair.getRowIndex1();
			rowIndex2 = conflictPair.getRowIndex2();
			topKDensity1 = allTopKDensity[rowIndex1];
			topKDensity2 = allTopKDensity[rowIndex2];
			EdgePair ep = new EdgePair(rowIndex1, rowIndex2, topKDensity1 + topKDensity2);
			epList.add(ep);
		}
		Collections.sort(epList, new ComparatorEdgePair());
	}

	

	private void detectErrorForEachEdge() {
		int epNum = epList.size();
		int cleanRowNum = cleanRowIndexList.size();
		int attrNum = db.getAttrNum();
		double theta1, theta2;
		EdgePair ep;
		for (int epi = 0; epi < epNum; epi++) {
			ep = epList.get(epi);
			int rowIndex1 = ep.getRowIndex1();
			int rowIndex2 = ep.getRowIndex2();
			if (detectedRowIndexList.contains(rowIndex1) || detectedRowIndexList.contains(rowIndex2)) {
				continue;
			}
			theta1 = allTopKDensity[rowIndex1];
			theta2 = allTopKDensity[rowIndex2];
			for (int ci = 0; ci < cleanRowNum; ci++) {
				int cleanRowIndex = cleanRowIndexList.get(ci);
				double kDensity = allKnnDensities[cleanRowIndex][K - 1];
				double tmpDensity1 = attrNum - calDisBtwTwoTp(rowIndex1, cleanRowIndex);
				double tmpDensity2 = attrNum - calDisBtwTwoTp(rowIndex2, cleanRowIndex);
				if (tmpDensity1 > kDensity) {
					theta1 += tmpDensity1 - kDensity;
					int kIndex = 0;
					for (int ki = 0; ki < K; ki++) {
						if (tmpDensity1 > allKnnDensities[cleanRowIndex][ki]) {
							kIndex = ki;
							break;
						}
					}
					for (int ki = K - 1; ki > kIndex; ki--) {
						allKnnDensities1[cleanRowIndex][ki] = allKnnDensities1[cleanRowIndex][ki - 1];
					}
					allKnnDensities1[cleanRowIndex][kIndex] = tmpDensity1;
				}
				if (tmpDensity2 > kDensity) {
					theta2 += tmpDensity2 - kDensity;
					int kIndex = 0;
					for (int ki = 0; ki < K; ki++) {
						if (tmpDensity2 > allKnnDensities[cleanRowIndex][ki]) {
							kIndex = ki;
							break;
						}
					}
					for (int ki = K - 1; ki > kIndex; ki--) {
						allKnnDensities2[cleanRowIndex][ki] = allKnnDensities2[cleanRowIndex][ki - 1];
					}
					allKnnDensities2[cleanRowIndex][kIndex] = tmpDensity2;
				}
			}
			if (theta1 < theta2) {
				allKnnDensities = allKnnDensities1;
				detectedRowIndexList.add(rowIndex1);
			} else {
				allKnnDensities = allKnnDensities2;
				detectedRowIndexList.add(rowIndex2);
			}
		}
		checkMaximal();
	}

	private void checkMaximal() {
		int checkNum = detectedRowIndexList.size();
		ArrayList<Integer> cleanErrorRowIndexList = new ArrayList<>();
		for (int ci = 0; ci < checkNum; ci++) {
			int checkRowIndex = detectedRowIndexList.get(ci);
			ArrayList<Integer> tmpConfRowList = confRowListMap.get(checkRowIndex);
			int tmpConfNum = tmpConfRowList.size();
			boolean isError = false;
			for (int tmpi = 0; tmpi < tmpConfNum; tmpi++) {
				int tmpConfRowIndex = tmpConfRowList.get(tmpi);
				if (detectedRowIndexList.contains(tmpConfRowIndex)) {
					continue;
				} else {
					isError = true;
				}
			}
			if (!isError) {
				cleanErrorRowIndexList.add(checkRowIndex);
			}
		}
		int cleanErrorNum = cleanErrorRowIndexList.size();
		for (int cei = 0; cei < cleanErrorNum; cei++) {
			int cleanErrorRowIndex = cleanErrorRowIndexList.get(cei);
			detectedRowIndexList.remove(detectedRowIndexList.indexOf(cleanErrorRowIndex));
		}
	}

	

	public int getK() {
		return K;
	}

	public void setK(int k) {
		K = k;
	}

	public int[][] getAllKnnIndexes() {
		return allKnnIndexes;
	}

	public void setAllKnnIndexes(int[][] allKnnIndexes) {
		this.allKnnIndexes = allKnnIndexes;
	}

	public double[][] getAllKnnDensities1() {
		return allKnnDensities1;
	}

	public void setAllKnnDensities1(double[][] allKnnDensities1) {
		this.allKnnDensities1 = allKnnDensities1;
	}

	public double[][] getAllKnnDensities2() {
		return allKnnDensities2;
	}

	public void setAllKnnDensities2(double[][] allKnnDensities2) {
		this.allKnnDensities2 = allKnnDensities2;
	}

	public ArrayList<EdgePair> getEpList() {
		return epList;
	}

	public void setEpList(ArrayList<EdgePair> epList) {
		this.epList = epList;
	}

	public double[] getAllTopKDensity() {
		return allTopKDensity;
	}

	public void setAllTopKDensity(double[] allTopKDensity) {
		this.allTopKDensity = allTopKDensity;
	}

	public ArrayList<Integer> getConflictRowIndexList() {
		return conflictRowIndexList;
	}

	public void setConflictRowIndexList(ArrayList<Integer> conflictRowIndexList) {
		this.conflictRowIndexList = conflictRowIndexList;
	}

	public ArrayList<Integer> getCleanRowIndexList() {
		return cleanRowIndexList;
	}

	public void setCleanRowIndexList(ArrayList<Integer> cleanRowIndexList) {
		this.cleanRowIndexList = cleanRowIndexList;
	}

	public ArrayList<Integer> getDetectedRowIndexList() {
		return detectedRowIndexList;
	}

	public void setDetectedRowIndexList(ArrayList<Integer> detectedRowIndexList) {
		this.detectedRowIndexList = detectedRowIndexList;
	}

	public ArrayList<ConflictPair> getConflictPairList() {
		return conflictPairList;
	}

	public void setConflictPairList(ArrayList<ConflictPair> conflictPairList) {
		this.conflictPairList = conflictPairList;
	}

	public HashMap<Integer, ArrayList<Integer>> getConfRowListMap() {
		return confRowListMap;
	}

	public void setConfRowListMap(HashMap<Integer, ArrayList<Integer>> confRowListMap) {
		this.confRowListMap = confRowListMap;
	}


	public double[][] getAllKnnDensities() {
		return allKnnDensities;
	}

	public void setAllKnnDensities(double[][] allKnnDensities) {
		this.allKnnDensities = allKnnDensities;
	}

	public ArrayList<CFD> getCfdList() {
		return cfdList;
	}

	public void setCfdList(ArrayList<CFD> cfdList) {
		this.cfdList = cfdList;
	}

	public ArrayList<Integer> getAttrIndexList() {
		return attrIndexList;
	}

	public void setAttrIndexList(ArrayList<Integer> attrIndexList) {
		this.attrIndexList = attrIndexList;
	}

}
