package com.mica.viva.controller.queryinformation;

import java.util.Calendar;

import android.util.Log;

public class Compile {

	final static String[] thu = { "nhật", "hai", "ba", "tư", "năm", "sáu",
			"bẩy" };
	final static int[] songay = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30,
			31 };
	public final static String[] tenso = { " không ", " một ", " hai ", " ba ",
			" bốn ", " năm ", " sáu ", " bảy ", " tám ", " chín ", " mươi ",
			" mười một ", " mười hai ", " mười ba ", " mười bốn ",
			" mười lăm ", " mười sáu ", " mười bảy ", " mười tám ",
			" mười chín ", " hai mươi ", " hai mốt ", " hai hai ", " hai ba ",
			" hai tư ", " hai lăm ", " hai sáu ", " hai bảy ", " hai tám ",
			" hai chín ", " ba mươi ", " ba mốt ", " ba hai ", " ba ba ",
			" ba tư ", " ba lăm ", " ba sáu ", " ba bảy ", " ba tám ",
			" ba chín ", " bốn mươi ", " bốn mốt ", " bốn hai ", " bốn ba ",
			" bốn tư " };

	public static int complie_day(String st) {
		Calendar c = Calendar.getInstance();
		int ngay = c.get(Calendar.DATE);
		int k = c.get(Calendar.DAY_OF_WEEK);
		
		if (st.contains("mai"))
			ngay++;
		if (st.contains("qua"))
			ngay--;
		if (st.contains("kia"))
			ngay = ngay + 2;
		for (int i = 0; i < 7; i++)
			if (st.contains(thu[i])) {
				ngay = ngay + ((8 + i - k) % 7);
				Log.i("Datetime", "thu ngay " + st);
			}
		if (ngay > songay[c.get(Calendar.MONTH)])
			ngay -= songay[c.get(Calendar.MONTH)];
		return ngay;
	}

	public static String Giatri(int u) {
		return tenso[u];
	}

}
