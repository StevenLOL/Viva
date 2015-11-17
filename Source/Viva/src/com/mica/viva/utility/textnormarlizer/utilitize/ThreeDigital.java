package com.mica.viva.utility.textnormarlizer.utilitize;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author lelightwin
 */
public class ThreeDigital {

    private int one;
    private int two;
    private int three;

    public ThreeDigital(int one, int two, int three) {

        System.out.println("package TextNor.Read; class ThreeDigital");

        this.one = one;
        this.two = two;
        this.three = three;
    }

    public int one() {
        return one;
    }

    public int two() {
        return two;
    }

    public int three() {
        return three;
    }

    public String readDigital(int i, boolean b) {
        switch (i) {
            case 0:
                if (b) {
                    return "không";
                } else {
                    return "";
                }
            case 1:
                if (b) {
                    return "một";
                } else {
                    return "mốt";
                }
            case 2:
                return "hai";
            case 3:
                return "ba";
            case 4:
                if (b) {
                    return "bốn";
                } else {
                    return "tư";
                }
            case 5:
                if (b) {
                    return "năm";
                } else {
                    return "lăm";
                }
            case 6:
                return "sáu";
            case 7:
                return "bảy";
            case 8:
                return "tám";
            case 9:
                return "chín";
            default:
                return "không";
        }
    }

    public String read1() {
        String result;
        String s1;
        String s2;
        String s3;
        String str;

        if (two == 0) {
            s2 = "";
            if (three == 0) {
                str = "";
                s3 = "";
            } else {
                str = " linh ";
                if ((three == 4) || (three == 5)) {
                    s3 = readDigital(three, false);
                } else {
                    s3 = readDigital(three, true);
                }
            }
        } else if (two == 1) {
            s2 = "";
            str = " mười ";
            if ((three == 5) || (three == 0)) {
                s3 = readDigital(three, false);
            } else {
                s3 = readDigital(three, true);
            }
        } else {
            s2 = readDigital(two, true);
            str = " mươi ";
            if ((three == 1) || (three == 4) || (three == 5) || (three == 0)) {
                s3 = readDigital(three, false);
            } else {
                s3 = readDigital(three, true);
            }
        }

        s1 = readDigital(one, true);
        result = s1 + " trăm " + s2 + str + s3;
        return result;
    }

    public String read2() {
        String result;
        String s1;
        String s2;
        String s3;
        String str1;
        String str2;

        if (one != 0) {
            s1 = readDigital(one, true);
            str2 = " trăm ";
            if (two == 0) {
                s2 = "";
                if (three == 0) {
                    str1 = "";
                    s3 = "";
                } else {
                    str1 = " linh ";
                    if ((three == 4) || (three == 5)) {
                        s3 = readDigital(three, false);
                    } else {
                        s3 = readDigital(three, true);
                    }
                }
            } else if (two == 1) {
                s2 = "";
                str1 = " mười ";
                if ((three == 5) || (three == 0)) {
                    s3 = readDigital(three, false);
                } else {
                    s3 = readDigital(three, true);
                }
            } else {
                s2 = readDigital(two, true);
                str1 = " mươi ";
                if ((three == 1) || (three == 4) || (three == 5) || (three == 0)) {
                    s3 = readDigital(three, false);
                } else {
                    s3 = readDigital(three, true);
                }
            }
        } else {
            s1 = "";
            str2 = "";
            if (two == 0) {
                s2 = "";
                str1 = "";
                s3 = readDigital(three, true);
            } else if (two == 1) {
                s2 = "";
                str1 = "mười ";
                if ((three == 5) || (three == 0)) {
                    s3 = readDigital(three, false);
                } else {
                    s3 = readDigital(three, true);
                }
            } else {
                s2 = readDigital(two, true);
                str1 = " mươi ";
                if ((three == 1) || (three == 4) || (three == 5) || (three == 0)) {
                    s3 = readDigital(three, false);
                } else {
                    s3 = readDigital(three, true);
                }
            }
        }

        result = s1 + str2 + s2 + str1 + s3;

        return result;
    }

    public boolean check() {
        if ((one == 0) && (two == 0) && (three == 0)) {
            return false;
        } else {
            return true;
        }
    }
//    public static void main(String[] args) {
//        String s = new ThreeDigital(0, 0, 0).read2();
//        System.out.println(s);
//    }
}
