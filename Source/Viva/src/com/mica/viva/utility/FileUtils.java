package com.mica.viva.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.mica.viva.ApplicationContext;

import android.util.Log;

public class FileUtils {

	public static void copyDirFromAssest(String dir) throws IOException {
		File f = new File(getWritablePath() + dir);		
		f.mkdirs();

		String[] files = ApplicationContext.getApplicationContext().getAssets()
				.list(dir);

		for (String file : files) {
			copyFromAssest(dir + "/" + file);
		}
	}

	public static void copyFromAssest(String fileName) {
		String mPath = getWritablePath();
		Log.d("Viva FileUtils", "Copying file: " + fileName);
		File f = new File(mPath + fileName);
		if (f.exists()) {
			Log.d("Viva FileUtils", "File existed");
			return;
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			in = ApplicationContext.getApplicationContext().getAssets()
					.open(fileName);
			out = new FileOutputStream(mPath + fileName);
			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			Log.d("Viva FileUtils", "Copied file " + fileName + " to " + mPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void copyFile(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[65536];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	public static String getWritablePath() {
		return ("/data/data/"
				+ ApplicationContext.getApplicationContext().getPackageName() + "/");
	}

	public static boolean CheckFileExist(String filePath) {
		File f = new File(filePath);
		if (f.exists()) {
			return true;
		} else
			return false;
	}

}
