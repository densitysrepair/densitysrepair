package util;

import java.util.ArrayList;

import entity.CFD;

public class Assist {

	public static boolean detectConfBtwTwoTp(String[] data1, String[] data2, CFD cfd) {
		int[] attrXs = cfd.getAttrXs();
		int attrY = cfd.getAttrY();
		boolean[] isConstantXs = cfd.getIsConstantXs();
		boolean isConstantY = cfd.getIsConstantY();
		String[] constantXs = cfd.getConstantXs();
		String constantY = cfd.getConstantY();

		int xlen = attrXs.length;
		boolean isNei = true, isConflict = false;
		int attrX;
		for (int xi = 0; xi < xlen; xi++) {
			attrX = attrXs[xi];
			if (isConstantXs[xi]) {
				if ((!data1[attrX].equals(constantXs[xi])) || (!data2[attrX].equals(constantXs[xi]))) {
					isNei = false;
					break;
				}
			} else {
				if (!data1[attrX].equals(data2[attrX])) {
					isNei = false;
					break;
				}
			}
		}
		if (isNei) {
			if (isConstantY) {
				if ((!data1[attrY].equals(constantY)) || (!data2[attrY].equals(constantY))
						|| (!data1[attrY].equals(data2[attrY]))) {
					isConflict = true;
				}
			} else {
				if (!data1[attrY].equals(data2[attrY])) {
					isConflict = true;
				}
			}
		}
		return isConflict;
	}

	public static boolean checkConf(int rowIndex, ArrayList<Integer> confRowIndexList,
			ArrayList<Integer> errorRowIndexList, ArrayList<Integer> cleanErrorRowIndexList) {
		int confNum = confRowIndexList.size();
		for (int nri = 0; nri < confNum; nri++) {
			int neiRowIndex = confRowIndexList.get(nri);
			if (cleanErrorRowIndexList.contains(neiRowIndex)) {
				return true;
			}
			if (!errorRowIndexList.contains(neiRowIndex)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNumber(String str) {
		String reg = "-?[0-9]+(\\.[0-9]+)?";
		return str.matches(reg);
	}

	public static double norNumDis(double a, double b) {
		double interval = Math.abs(a - b);
		double norNumDis = 1 - 1 / Math.pow(Math.E, interval);
		return norNumDis;
	}

	public static double normStrDis(String word1, String word2) {
		int len1 = word1.length(), len2 = word2.length();
		int[][] dp = new int[len1 + 1][len2 + 1];
		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}
		for (int i = 0; i <= len2; i++) {
			dp[0][i] = i;
		}
		for (int i = 1; i <= len1; i++) {
			for (int j = 1; j <= len2; j++) {
				if (word1.charAt(i - 1) == word2.charAt(j - 1))
					dp[i][j] = dp[i - 1][j - 1];
				else
					dp[i][j] = 1 + Math.min(dp[i][j - 1], Math.min(dp[i - 1][j], dp[i - 1][j - 1]));
			}
		}
		double gelED = (double) dp[len1][len2];
		double norED = (2 * gelED) / (len1 + len2 + gelED);
		return norED;
	}

}
