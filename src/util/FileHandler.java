package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import entity.CFD;
import entity.Database;
import entity.Tuple;

public class FileHandler {

	public static String PATH = "data/";

	public ArrayList<CFD> readRule(String input) {
		ArrayList<CFD> cfdList = new ArrayList<CFD>();
		File dcFile = new File(PATH + input);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(dcFile));
			String templine = null;
			while ((templine = br.readLine()) != null) {
				if (templine.indexOf('#') == 0 || templine.trim().equals(""))
					continue;
				String[] vals = templine.split(";");
				String[] xs = vals[0].split(":");
				int xlen = xs.length;
				int[] attrXs = new int[xlen];
				boolean[] isConstantXs = new boolean[xlen];
				String[] constantXs = new String[xlen];
				for (int xi = 0; xi < xlen; xi++) {
					String[] attrXSS = xs[xi].split(",");
					attrXs[xi] = Integer.parseInt(attrXSS[0]);
					String tmpConstantX = attrXSS[1];
					if (tmpConstantX.equals("null")) {
						isConstantXs[xi] = false;
					} else {
						isConstantXs[xi] = true;
						constantXs[xi] = tmpConstantX;
					}
				}
				String[] ys = vals[1].split(",");
				int attrA = Integer.parseInt(ys[0]);
				boolean isConstantY;
				String constantY = ys[1];
				if (constantY.equals("null")) {
					isConstantY = false;
				} else {
					isConstantY = true;
				}
				CFD tempCFD = new CFD(attrXs, attrA, isConstantXs, isConstantY, constantXs, constantY);
				cfdList.add(tempCFD);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cfdList;
	}

	public ArrayList<Tuple> readCleanData(String input) {
		ArrayList<Tuple> cleanTpList = new ArrayList<>();
		try {
			FileReader fr = new FileReader(PATH + input);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			String[] vals = null;
			String title = br.readLine();
			int attrNum = title.split(",").length;
			int tid = 0;
			while ((line = br.readLine()) != null) {
				vals = line.split(",");
				tid++;
				String[] data = new String[attrNum];
				for (int i = 0; i < attrNum; ++i) {
					data[i] = vals[i].trim();
				}
				Tuple tp = new Tuple(attrNum);
				tp.buildTuple(tid, data);
				cleanTpList.add(tp);
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cleanTpList;
	}

	public Database readDirtyData(String input) {
		Database db = new Database();
		try {
			FileReader fr = new FileReader(PATH + input);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			String[] vals = null;
			ArrayList<Tuple> dirtyTpList = new ArrayList<>();
			String title = br.readLine();
			db.setHeader(title);
			int attrNum = title.split(",").length;
			db.setAttrNum(attrNum);
			int tid = 0;
			while ((line = br.readLine()) != null) {
				vals = line.split(",");
				tid++;
				String[] data = new String[attrNum];
				for (int i = 0; i < attrNum; ++i) {
					data[i] = vals[i].trim();
				}
				Tuple tp = new Tuple(attrNum);
				tp.buildTuple(tid, data);
				dirtyTpList.add(tp);
			}
			db.setDirtyTpList(dirtyTpList);
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return db;
	}

}
