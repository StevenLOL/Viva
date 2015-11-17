/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mica.viva.utility.textnormarlizer.utilitize;

/**
 *
 * @author lelightwin
 */
public class Tools {

    /**
     *
     * @param nsw
     * @return check whether nsw is a number
     */
    public static boolean isNumber(String nsw) {
        for (int i = 0; i < nsw.length(); i++) {
            if (Character.isDigit(nsw.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param s
     * @return
     */
    public static String standard(String s) {

        String in = s.replace(" ", "");
        StringBuilder sa = new StringBuilder(in);
        int i = 0;
        while ((sa.charAt(i) == '0')
                && (i < sa.length() - 1)) {
            i++;
        }

        sa.replace(0, i, "");

        if (sa.length() % 3 == 1) {
            sa.insert(0, "00");
        } else if (sa.length() % 3 == 2) {
            sa.insert(0, "0");
        }

        return sa.toString();
    }

    /**
     *
     * @param i
     * @return a group which i belongs to
     */
    public static String group(int i) {
        String re;
        if (i % 3 == 1) {
            re = " nghìn ";
        } else if (i % 3 == 2) {
            re = " triệu ";
        } else {
            if (i >= 3) {
                re = " tỉ ";
            } else {
                re = "";
            }
        }
        return re;
    }
}
