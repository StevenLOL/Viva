package com.mica.viva.utility;

public class TextUtils {
	static String[] numbers = { "không", "một", "hai", "ba", "bốn", "năm",
			"sáu", "bảy", "tám", "chín" };

	static String[] threes = { "", "nghìn , ", "triệu , ", "tỷ , " };

	public static int getNumberOfText(String text) {
		for (int i = 0; i < numbers.length; i++) {
			if (numbers[i].equals(text))
				return i;
		}
		return -1;
	}

	public static String getTextOfNumber(int number) {
		if (number >= 0 && number < numbers.length)
			return numbers[number];
		return "";
	}

	public static String getTextOfPhoneNumber(String phone) {
		String output = "";
		try {
			if (phone != null) {
				phone = phone.replaceAll(" ", "");

				if (phone.startsWith("+") && phone.length() > 3) {
					phone = "0" + phone.substring(3);
				}

				for (int i = 0; i < phone.length(); i++) {
					int num = Integer.parseInt(Character.toString(phone
							.charAt(i)));
					output += numbers[num] + " ";
					if ((i == phone.length() - 4) || (i == phone.length() - 7)) {
						output += ", ";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}

	public static String getTextOfCurrency(String currency) {
		String output = "";
		try {
			if (currency != null) {
				currency = currency.replaceAll(" ", "");
				int blockCount = (int) (currency.length() / 3);
				for (int i = 0; i < blockCount; i++) {
					String tmp = getTextOfThreeNumber(currency
							.substring(currency.length() - 3));
					if (tmp != "")
						output = tmp + " " + threes[(i) % 4] + " " + output;

					currency = currency.substring(0, currency.length() - 3);
				}
				if (currency.length() > 0) {
					output = getTextOfThreeNumber(currency) + " "
							+ threes[blockCount % 4] + " " + output;
				}
			}
			output = output.trim().replaceAll("  ", " ");
			if (output.endsWith(","))
				output = output.substring(0, output.length() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}

	private static String getTextOfThreeNumber(String number) {
		String output = "";
		if (number.equals("000"))
			return output;

		if (number.length() >= 3) {
			int num = Integer.parseInt(Character.toString(number.charAt(number
					.length() - 3)));
			output += numbers[num] + " trăm ";
		}

		if (number.length() >= 2) {
			int num = Integer.parseInt(Character.toString(number.charAt(number
					.length() - 2)));
			if (num == 0) {
				if (!number.endsWith("00"))
					output += "lẻ ";
			} else if (num == 1) {
				output += "mười ";
			} else {
				output += numbers[num] + " mươi ";
			}
		}

		if (number.length() >= 1) {
			int num = Integer.parseInt(Character.toString(number.charAt(number
					.length() - 1)));
			if (num > 0) {
				output += numbers[num];
			}
		}

		return output.trim().replaceAll("  ", " ");

	}

	public static String getPhoneNumberOfText(String text) {
		if (text != null && text != "") {
			text = text.replace("  ", " ");
			String[] strs = text.split(" ");

			if (strs.length > 0) {
				String result = "";
				for (int i = 0; i < strs.length; i++) {
					int digit = getNumberOfText(strs[i]);
					if (digit == -1)
						return "";

					result += digit;
				}
				return result;
			} else {
				int digit = getNumberOfText(text);
				if (digit == -1)
					return "";
				return String.valueOf(digit);
			}
		}
		return "";
	}
}
