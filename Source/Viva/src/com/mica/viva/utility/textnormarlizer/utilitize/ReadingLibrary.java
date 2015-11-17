/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mica.viva.utility.textnormarlizer.utilitize;

import com.mica.viva.utility.textnormarlizer.common.Constant;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author lelightwin
 */
public class ReadingLibrary {

	ArrayList<ThreeDigital> level;
	private HashMap<String, String> charAr = new HashMap<String, String>();
	private HashMap<String, String> loanAr = new HashMap<String, String>();
	private HashMap<String, String> unitMoneyAr = new HashMap<String, String>();
	private HashMap<String, String> greekAr = new HashMap<String, String>();

	public ReadingLibrary() {
		try {
			loadCharAr();
			loadGreekAr();
			loadLoanAr();
			loadUnitMoneyAr();
		} catch (FileNotFoundException ex) {
			Logger.getLogger(ReadingLibrary.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(ReadingLibrary.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (IOException ex) {
			Logger.getLogger(ReadingLibrary.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	/**
	 * @function load Character Array from file
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private void loadCharAr() throws FileNotFoundException,
			UnsupportedEncodingException, IOException {

		BufferedReader bfr = new BufferedReader(new InputStreamReader(
				new FileInputStream(Constant.CHARACTER_DATABASE), "utf-8"));
		String line;
		while ((line = bfr.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, "|");
			String c = st.nextToken();
			String r = st.nextToken();
			charAr.put(c, r);
		}

	}

	/**
	 * @function load Greek Symbol Array from file
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private void loadGreekAr() throws FileNotFoundException,
			UnsupportedEncodingException, IOException {

		BufferedReader bfr = new BufferedReader(new InputStreamReader(
				new FileInputStream(Constant.GREEK_DATABASE), "utf-8"));
		String line;
		while ((line = bfr.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, "|");
			String c = st.nextToken();
			String r = st.nextToken();
			greekAr.put(c, r);
		}

	}

	/**
	 * @function load loan Array from file
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private void loadLoanAr() throws UnsupportedEncodingException,
			FileNotFoundException, IOException {
		BufferedReader bfr = new BufferedReader(new InputStreamReader(
				new FileInputStream(Constant.LOAN_DATABASE), "utf-8"));
		String line;
		while ((line = bfr.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, "|");
			String a = st.nextToken();
			String r = st.nextToken();
			loanAr.put(a, r);
		}
	}

	/**
	 * @function load unit money Array from file
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private void loadUnitMoneyAr() throws FileNotFoundException,
			UnsupportedEncodingException, IOException {

		BufferedReader bfr = new BufferedReader(new InputStreamReader(
				new FileInputStream(Constant.UNIT_MONEY_DATABASE), "utf-8"));
		String line;
		while ((line = bfr.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, "|");
			String l = st.nextToken();
			String r = st.nextToken();
			unitMoneyAr.put(l, r);
		}

	}

	/**
	 * 
	 * @param nsw
	 * @return a read of nsw as a digit array
	 */
	public String readNDIG(String nsw) {
		String full = "";
		for (int i = 0; i < nsw.length(); i++) {
			String subStr = nsw.substring(i, i + 1);
			full += charAr.get(subStr) + " ";
		}
		return full;
	}

	/**
	 * 
	 * @param nsw
	 * @return a read of nsw as a fraction
	 */
	public String readNFRC(String nsw) {
		System.out.println("NFRC: " + nsw);
		String full;
		String[] sp = nsw.split("/");
		full = readNum(sp[0]) + " phần " + readNum(sp[1]);
		return full;
	}

	/**
	 * 
	 * @param nsw
	 * @return a read of nsw as a match score
	 */
	public String readNSCR(String nsw) {
		String full = "";
		return full;
	}

	/**
	 * 
	 * @param nsw
	 * @return a read of nsw as a number range
	 */
	public String readNRGN(String nsw) {
		System.out.println("NRGN: " + nsw);
		String full;
		String[] sp = nsw.split("\\p{Pd}");
		full = readNum(sp[0]) + " đến " + readNum(sp[1]);
		return full;
	}

	/**
	 * 
	 * @param nsw
	 * @return a read of nsw as an address
	 */
	public String readNADD(String nsw) {
		String full;
		String[] sp = nsw.split("/");
		full = readNum(sp[0]);
		for (int i = 1; i < sp.length; i++) {
			full = full + " trên " + sp[i];
		}
		return full;
	}

	/**
	 * 
	 * @param nsw
	 * @return
	 */
	public String readNSIG(String nsw) {
		String full;
		if (nsw.equals("2")) {
			full = "vuông";
		} else {
			full = "khối";
		}
		return full;
	}

	/**
	 * 
	 * @param nsw
	 * @return a read of nsw as a loan
	 */
	public String readLOAN(String nsw) {
		nsw = nsw.toLowerCase();
		String full;
		if (loanAr.containsKey(nsw)) {
			full = loanAr.get(nsw);
		} else {
			full = "";
		}
		return full;
	}

	/**
	 * 
	 * @param nsw
	 * @return a read of nsw as a letter sequence
	 */
	public String readSEQN(String nsw) {
		String full;
		nsw = nsw.toLowerCase();
		full = charAr.get(nsw.substring(0, 1));
		for (int i = 1; i < nsw.length(); i++) {
			full = full + " " + charAr.get(nsw.substring(i, i + 1));
		}
		return full;
	}

	/**
	 * 
	 * @param nsw
	 * @return a read of nsw as a Greek character
	 */
	public String readGREK(String nsw) {
		String full;
		full = greekAr.get(nsw);
		return full;
	}

	/**
	 * 
	 * @param nsw
	 * @return a read of nsw as a punctuation
	 */
	public String readPUNC(String nsw) {
		String full;
		if (nsw.equals("...")) {
			return "ba chấm";
		}
		if (charAr.containsKey(nsw.substring(0, 1))) {
			full = charAr.get(nsw.substring(0, 1));
		} else {
			full = "";
		}

		for (int i = 1; i < nsw.length(); i++) {
			if (charAr.containsKey(nsw.substring(0, 1))) {
				full = full + " " + charAr.get(nsw.substring(i, i + 1));
			} else {
				full = full + "";
			}

		}
		return full;
	}

	/**
	 * 
	 * @param nsw
	 * @return a read of nsw as a URL
	 */
	public String readURLE(String nsw) {
		String full;
		full = charAr.get(nsw.substring(0, 1));
		for (int i = 1; i < nsw.length(); i++) {
			full = full + " " + charAr.get(nsw.substring(i, i + 1));
		}
		return full;
	}

	/**
	 * 
	 * @param nsw
	 * @return a read of nsw as a money
	 */
	public String readMONY(String nsw) {
		String full;
		String[] ar = new String[2];
		Pattern SPLIT = Pattern.compile("(\\d\\p{L})|(\\p{L}\\d)");
		Matcher m = SPLIT.matcher(nsw);
		int lastIndex = 0;
		if (m.find()) {
			String nsw1 = nsw.substring(lastIndex, m.start() + 1);
			ar[0] = nsw1;
			String nsw2 = nsw.substring(m.end() - 1);
			ar[1] = nsw2;
		}
		if (Tools.isNumber(ar[0])) {
			full = readNNUM(ar[0]) + " " + unitMoneyAr.get(ar[1]);
		} else {
			full = readNNUM(ar[1]) + " " + unitMoneyAr.get(ar[0]);
		}
		return full;
	}

	/**
	 * 
	 * @param s
	 * @return a read of nsw as a natural number
	 */
	public String readNum(String s) {
		if (s.equals("")) {
			return "không";
		} else {
			String result = "";
			level = new ArrayList<ThreeDigital>();
			String sb = Tools.standard(s);
			for (int i = (sb.length() / 3); i > 0; i--) {
				int three = (int) sb.charAt(3 * i - 1) - 48;
				int two = (int) sb.charAt(3 * i - 2) - 48;
				int one = (int) sb.charAt(3 * i - 3) - 48;
				level.add(new ThreeDigital(one, two, three));
			}
			result += level.get(level.size() - 1).read2()
					+ Tools.group(level.size() - 1);
			for (int i = level.size() - 2; i >= 0; i--) {
				if (level.get(i).check()) {
					result += level.get(i).read1() + Tools.group(i);
				} else if (i % 3 == 0) {
					result += Tools.group(i);
				}
			}

			return result;
		}
	}

	/**
	 * 
	 * @param s
	 * @return a read of nsw as a decimal number
	 */
	public String readNNUM(String s) {
		String full;
		if (s.contains(".") && s.contains(",")) {
			String snew = s.replace(".", "");
			int i = s.indexOf(",");
			String sub1 = s.substring(0, i - 1);
			String sub2 = s.substring(i + 1);
			full = readNum(sub1) + readSEQN(sub2);
		} else {
			if (s.contains(".")) {
				String[] str = s.split("\\.");
				if (str.length >= 3) {
					String snew = s.replace(".", "");
					full = readNum(snew);
				} else {
					String sub1 = str[0];
					String sub2 = str[1];
					System.out.println(sub1 + " " + sub2);
					if (sub2.length() == 3) {
						if (sub1.length() <= 3) {
							String snew = s.replace(".", "");
							full = readNum(snew);
						} else {
							full = readNum(sub1) + " phẩy " + readSEQN(sub2);
						}
					} else {
						full = readNum(sub1) + " phẩy " + readSEQN(sub2);
					}
				}
			} else if (s.contains(",")) {
				String[] str = s.split(",");
				String sub1 = str[0];
				String sub2 = str[1];
				full = readNum(sub1) + " phẩy " + readSEQN(sub2);
			} else {
				full = readNum(s);
			}

		}
		return full;
	}

	/**
	 * 
	 * @param nsw
	 * @return a read of nsw as a time
	 */
	public String readNTIM(String nsw) {
		String res = "";
		String[] sp = nsw.split(":");

		if (sp.length == 2) {
			res = readNum(sp[0]) + " giờ " + readNum(sp[1]) + "phút ";
		} else if (sp.length == 3) {
			res = readNum(sp[0]) + " giờ " + readNum(sp[1]) + "phút "
					+ readNum(sp[2]) + " giây ";
		}
		return res;
	}

	/**
	 * 
	 * @param nsw
	 * @return a read of nsw as a date
	 */
	public String readNDAT(String nsw) {
		String res;
		String[] sp = nsw.split("[/-]");
		if (sp.length == 4) {
			String ngaytruoc;
			String ngaysau;

			if (sp[0].length() == 1) {
				ngaytruoc = "mồng " + readNum(sp[0]);

			} else {
				ngaytruoc = readNum(sp[0]);
			}
			if (sp[1].length() == 1) {

				ngaysau = "mồng " + readNum(sp[1]);
			} else {
				ngaysau = readNum(sp[1]);
			}
			res = ngaytruoc + " đến ngày" + ngaysau + " tháng "
					+ readNum(sp[2]) + " năm " + readNum(sp[3]);

		} else {
			if (sp.length == 3) {
				res = readNum(sp[0]) + " tháng " + readNum(sp[1]) + " năm "
						+ readNum(sp[2]);
				if (sp[0].length() == 1 || sp[0].charAt(0) == '0') {
					res = "mồng " + res;
				}
			} else {
				res = readNDIG(nsw);
			}

		}

		return res;
	}

	/**
	 * 
	 * @param nsw
	 * @return a read of nsw as a day
	 */
	public String readNDAY(String nsw) {
		String res;
		String[] sp = nsw.split("[/-]");
		if (sp.length == 3) {
			String ngaytruoc;
			String ngaysau;
			if (sp[0].length() == 1) {
				ngaytruoc = "mồng " + readNum(sp[0]);
			} else {
				ngaytruoc = readNum(sp[0]);
			}
			if (sp[1].length() == 1) {
				ngaysau = "mồng " + readNum(sp[1]);
			} else {
				ngaysau = readNum(sp[1]);
			}
			res = ngaytruoc + " đến ngày" + ngaysau + " tháng "
					+ readNum(sp[2]);
		} else {
			res = readNum(sp[0]) + " tháng " + readNum(sp[1]);
			if (sp[0].length() == 1) {
				res = "mồng " + res;
			}

		}

		return res;
	}

	/**
	 * 
	 * @param nsw
	 * @return a read of nsw as a month
	 */
	public String readNMON(String nsw) {
		String res;
		String[] sp = nsw.split("[/-]");
		if (sp.length == 3) {
			res = readNum(sp[0]) + " đến tháng " + readNum(sp[1]) + "năm"
					+ readNum(sp[2]);
		} else {
			res = readNum(sp[0]) + " năm " + readNum(sp[1]);
		}

		return res;
	}

	public static void main(String[] args) {
		ReadingLibrary rlib = new ReadingLibrary();
		System.out.println(rlib.readNFRC("2/10"));
	}
}
