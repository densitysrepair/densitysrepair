package entity;

import java.util.ArrayList;

public class Database {

	private int attrNum;
	private ArrayList<Tuple> cleanTpList;
	private ArrayList<Tuple> dirtyTpList;
	private ArrayList<Tuple> repairedTpList;
	private String header;
	private double[] minVals;
	private double[] maxVals;
	private ArrayList<String> allKeyList;

	public Database() {

	}

	public int getAttrNum() {
		return attrNum;
	}

	public void setAttrNum(int attrNum) {
		this.attrNum = attrNum;
	}

	public ArrayList<Tuple> getCleanTpList() {
		return cleanTpList;
	}

	public void setCleanTpList(ArrayList<Tuple> cleanTpList) {
		this.cleanTpList = cleanTpList;
	}

	public ArrayList<Tuple> getDirtyTpList() {
		return dirtyTpList;
	}

	public void setDirtyTpList(ArrayList<Tuple> dirtyTpList) {
		this.dirtyTpList = dirtyTpList;
	}

	public int getLength() {
		return dirtyTpList.size();
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public void addTuple(Tuple tp) {
		this.dirtyTpList.add(tp);
	}

	public ArrayList<Tuple> getRepairedTpList() {
		return repairedTpList;
	}

	public void setRepairedTpList(ArrayList<Tuple> repairedTpList) {
		this.repairedTpList = repairedTpList;
	}

	public double[] getMinVals() {
		return minVals;
	}

	public void setMinVals(double[] minVals) {
		this.minVals = minVals;
	}

	public double[] getMaxVals() {
		return maxVals;
	}

	public void setMaxVals(double[] maxVals) {
		this.maxVals = maxVals;
	}

	public ArrayList<String> getAllKeyList() {
		return allKeyList;
	}

	public void setAllKeyList(ArrayList<String> allKeyList) {
		this.allKeyList = allKeyList;
	}

}
