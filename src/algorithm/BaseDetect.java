package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import entity.Database;
import entity.KnnPair;
import entity.Tuple;
import util.Assist;
import util.ComparatorKnnPair;

public class BaseDetect {

	protected Database db;
	protected ArrayList<Tuple> tpList;
	protected String[][] dbVals;
	protected ArrayList<Integer> rowIndexList;

	public BaseDetect(Database db) {
		setDb(db);
		tpList = db.getDirtyTpList();
	}

	protected void initVals() {
		int size = db.getLength();
		int attrNum = db.getAttrNum();
		dbVals = new String[size][attrNum];
		Tuple tp = null;
		rowIndexList = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			tp = tpList.get(i);
			String[] datas = tp.getAllData();
			for (int j = 0; j < attrNum; j++) {
				dbVals[i][j] = datas[j];
			}
			rowIndexList.add(i);
		}
	}

	protected void calDisWithTuplesFromList(int rowIndex, double[] distances, ArrayList<Integer> cleanRowIndexList) {
		int size = cleanRowIndexList.size();
		int neiRowIndex;
		double dis;
		for (int ci = 0; ci < size; ci++) {
			neiRowIndex = cleanRowIndexList.get(ci);
			if (neiRowIndex == rowIndex) {
				distances[ci] = Double.MAX_VALUE;
			} else {
				dis = calDisBtwTwoTp(rowIndex, neiRowIndex);
				distances[ci] = dis;
			}
		}
	}

	protected void findCleanKnn(double[] distances, int[] knnIndexes, double[] knnDistances,
			ArrayList<Integer> cleanRowIndexList) {
		int length = knnIndexes.length;
		if (length >= cleanRowIndexList.size()) {
			for (int i = 0; i < cleanRowIndexList.size(); i++) {
				int cleanRowIndex = cleanRowIndexList.get(i);
				knnIndexes[i] = cleanRowIndex;
				knnDistances[i] = distances[i];
			}
		} else {
			for (int i = 0; i < length; i++) {
				int cleanRowIndex = cleanRowIndexList.get(i);
				knnIndexes[i] = cleanRowIndex;
				knnDistances[i] = distances[i];
			}
			int maxIndex = getMaxIndexfromK(knnDistances);
			double maxVal = knnDistances[maxIndex];
			double dis;
			for (int i = length; i < cleanRowIndexList.size(); i++) {
				int cleanRowIndex = cleanRowIndexList.get(i);
				dis = distances[i];
				if (dis < maxVal) {
					knnIndexes[maxIndex] = cleanRowIndex;
					knnDistances[maxIndex] = dis;

					maxIndex = getMaxIndexfromK(knnDistances);
					maxVal = knnDistances[maxIndex];
				}
			}
		}
		ArrayList<KnnPair> kpList = new ArrayList<>();
		KnnPair kp = null;
		for (int i = 0; i < length; i++) {
			kp = new KnnPair(knnDistances[i], knnIndexes[i]);
			kpList.add(kp);
		}
		Collections.sort(kpList, new ComparatorKnnPair());
		for (int i = 0; i < length; i++) {
			kp = kpList.get(i);
			knnIndexes[i] = kp.getIndex();
			knnDistances[i] = kp.getDistance();
		}
	}

	protected int getMaxIndexfromK(double[] vals) {
		int index = -1;
		double max = -100;
		for (int i = 0; i < vals.length; i++) {
			if (vals[i] > max) {
				max = vals[i];
				index = i;
			}
		}
		return index;
	}

	protected double calDisBtwTwoTp(int rowIndex1, int rowIndex2) {
		String[] vals1 = dbVals[rowIndex1];
		String[] vals2 = dbVals[rowIndex2];
		int attrNum = db.getAttrNum();
		double dis, sum = 0;
		double numVal1, numVal2;
		String val1, val2;
		for (int attri = 0; attri < attrNum; attri++) {
			val1 = vals1[attri];
			val2 = vals2[attri];
			if (val1.equals("") || val2.equals("")) {
				if (val1.equals("") && val2.equals("")) {
					dis = 0;
				} else {
					dis = 1;
				}
			} else {
				if (Assist.isNumber(val1) && Assist.isNumber(val2)) {
					numVal1 = Double.parseDouble(val1);
					numVal2 = Double.parseDouble(val2);
					dis = Assist.norNumDis(numVal1, numVal2);
				} else {
					dis = Assist.normStrDis(val1, val2);
				}
			}
			sum += dis;
		}
		return sum;
	}

	public Database getDb() {
		return db;
	}

	public void setDb(Database db) {
		this.db = db;
	}

	public ArrayList<Tuple> getTpList() {
		return tpList;
	}

	public void setTpList(ArrayList<Tuple> tpList) {
		this.tpList = tpList;
	}

	public String[][] getDbVals() {
		return dbVals;
	}

	public void setDbVals(String[][] dbVals) {
		this.dbVals = dbVals;
	}

	public ArrayList<Integer> getRowIndexList() {
		return rowIndexList;
	}

	public void setRowIndexList(ArrayList<Integer> rowIndexList) {
		this.rowIndexList = rowIndexList;
	}
}
