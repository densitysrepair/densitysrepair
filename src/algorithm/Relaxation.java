package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import entity.CFD;
import entity.ConflictPair;
import entity.Database;
import entity.DensityPair;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import util.Assist;
import util.ComparatorDensityPair;

public class Relaxation extends BaseDetect {

	private int K;
	private ArrayList<Integer> conflictRowIndexList;
	private ArrayList<Integer> cleanRowIndexList;
	private ArrayList<Integer> detectedRowIndexList;
	private ArrayList<Integer> attrIndexList;
	private ArrayList<ConflictPair> conflictPairList;
	private HashMap<Integer, ArrayList<Integer>> confRowListMap;
	private int[][] allKnnIndexes;
	private double[] allDensity;
	private ArrayList<CFD> cfdList;

	public Relaxation(Database db) {
		super(db);
		conflictRowIndexList = new ArrayList<>();
		cleanRowIndexList = new ArrayList<>();
		detectedRowIndexList = new ArrayList<>();
		conflictPairList = new ArrayList<>();
		confRowListMap = new HashMap<>();
	}

	public void mainRelaxation() {
		initVals();
		detectConf();
		genKNeiWithDensity();
		lpSover();
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

	private void addVar(GRBModel model, GRBVar x[]) {
		int size = rowIndexList.size();
		try {
			for (int i = 0; i < size; i++) {
				x[i] = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, "x" + "(" + i + ")");
			}
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
	}

	private void setObject(GRBModel model, GRBVar x[]) {
		int size = db.getLength();
		try {
			GRBLinExpr expr = new GRBLinExpr();
			for (int i = 0; i < size; i++) {
				double density = allDensity[i];
				expr.addTerm(density, x[i]);
			}
			model.setObjective(expr, GRB.MAXIMIZE);
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
	}

	private void addVioConstr(GRBModel model, GRBVar x[]) {
		int confNum = conflictPairList.size();
		try {
			GRBLinExpr expr = null;
			for (int ci = 0; ci < confNum; ci++) {
				ConflictPair conflictPair = conflictPairList.get(ci);
				int rowIndex1 = conflictPair.getRowIndex1();
				int rowIndex2 = conflictPair.getRowIndex2();
				expr = new GRBLinExpr();
				expr.addTerm(1.0, x[rowIndex1]);
				expr.addTerm(1.0, x[rowIndex2]);
				model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "Conflict Constraint");
			}
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
	}

	private void analyResult(GRBVar x[]) {
		int confNum = conflictRowIndexList.size();
		detectedRowIndexList.clear();
		ArrayList<DensityPair> dpList = new ArrayList<>();
		try {
			for (int cfi = 0; cfi < confNum; cfi++) {
				int rowIndex = conflictRowIndexList.get(cfi);
				double xVal = x[rowIndex].get(GRB.DoubleAttr.X);
				if (xVal < 0.5) {
					detectedRowIndexList.add(rowIndex);
				} else if (Math.abs(xVal - 0.5) < 0.0000000001) {
					detectedRowIndexList.add(rowIndex);
					DensityPair dp = new DensityPair(rowIndex, allDensity[rowIndex]);
					dpList.add(dp);
				}
			}
			Collections.sort(dpList, new ComparatorDensityPair());
			if (dpList.size() > 0) {
				int needCheckNum = dpList.size();
				ArrayList<Integer> cleanErrorRowIndexList = new ArrayList<>();
				for (int nci = 0; nci < needCheckNum; nci++) {
					DensityPair dp = dpList.get(nci);
					int rowIndex = dp.getRowIndex();
					ArrayList<Integer> confRowIndexList = confRowListMap.get(rowIndex);
					boolean isConflict = false;
					isConflict = Assist.checkConf(rowIndex, confRowIndexList, detectedRowIndexList,
							cleanErrorRowIndexList);
					if (!isConflict) {
						detectedRowIndexList.remove(detectedRowIndexList.indexOf(rowIndex));
						cleanErrorRowIndexList.add(rowIndex);
					}
				}
			}
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
	}

	private void lpSover() {
		try {
			GRBEnv env = new GRBEnv();
			GRBModel model = new GRBModel(env);
			GRBVar x[] = new GRBVar[rowIndexList.size()];
			addVar(model, x);
			model.update();
			setObject(model, x);
			addVioConstr(model, x);
			model.optimize();
			analyResult(x);
			model.dispose();
			env.dispose();
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
	}

	private void genKNeiWithDensity() {
		int size = db.getLength();
		int attrNum = db.getAttrNum();
		double[][] subDistances = new double[size][size];
		allKnnIndexes = new int[size][K];
		allDensity = new double[size];
		double[] knnDistances = new double[K];
		for (int rowIndex = 0; rowIndex < size; rowIndex++) {
			calDisWithTuplesFromList(rowIndex, subDistances[rowIndex], cleanRowIndexList);
			findCleanKnn(subDistances[rowIndex], allKnnIndexes[rowIndex], knnDistances, cleanRowIndexList);
			double density = 0;
			for (int ki = 0; ki < K; ki++) {
				density += attrNum - knnDistances[ki];
			}
			allDensity[rowIndex] = density;
		}
	}

	public int getK() {
		return K;
	}

	public void setK(int k) {
		K = k;
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

	public int[][] getAllKnnIndexes() {
		return allKnnIndexes;
	}

	public void setAllKnnIndexes(int[][] allKnnIndexes) {
		this.allKnnIndexes = allKnnIndexes;
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

	public double[] getAllDensity() {
		return allDensity;
	}

	public void setAllDensity(double[] allDensity) {
		this.allDensity = allDensity;
	}

	public ArrayList<Integer> getAttrIndexList() {
		return attrIndexList;
	}

	public void setAttrIndexList(ArrayList<Integer> attrIndexList) {
		this.attrIndexList = attrIndexList;
	}

	public ArrayList<CFD> getCfdList() {
		return cfdList;
	}

	public void setCfdList(ArrayList<CFD> cfdList) {
		this.cfdList = cfdList;
	}

}
