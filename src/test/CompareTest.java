package test;

import java.util.ArrayList;

import algorithm.Heuristic;
import algorithm.Relaxation;
import entity.CFD;
import entity.Database;
import util.FileHandler;

public class CompareTest {

	private Database db;

	public Database getDb() {
		return db;
	}

	public void setDb(Database db) {
		this.db = db;
	}

	public CompareTest() {

	}

	public static void main(String[] args) {
		CompareTest test = new CompareTest();
		String name = "res";
		String dataFilename = name + ".data";
		String dirtyDataFilename = name + "-dirty.data";
		FileHandler fh = new FileHandler();
		test.setDb(fh.readDirtyData(dirtyDataFilename));
		Database db = test.getDb();
		db.setCleanTpList(fh.readCleanData(dataFilename));
		ArrayList<CFD> cfdList = fh.readRule("cfd.final");
		final int K = 10;

		Heuristic heuristic = new Heuristic(db);
		heuristic.setK(K);
		heuristic.setCfdList(cfdList);
		heuristic.mainHeuristic();

		Relaxation relaxation = new Relaxation(db);
		relaxation.setK(K);
		relaxation.setCfdList(cfdList);
		relaxation.mainRelaxation();
	}

}
