package com.mica.viva.diacritic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.security.auth.login.AppConfigurationEntry;

import com.mica.viva.ApplicationConfigs;
import com.mica.viva.ApplicationContext;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

public class VietnameseDiacritic {
	private Socket socket = null;
	private String server = "mica.edu.vn";
	private int port = 1571;

	public String AddDiacritic(String inputSentence) {
		try {
			inputSentence = standardiseInputSentence(inputSentence);

			// open socket
			socket = new Socket();
			socket.connect(new InetSocketAddress(server, port), 1500);

			BufferedReader br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			PrintWriter pw = new PrintWriter(socket.getOutputStream());

			pw.println(inputSentence);
			pw.flush();

			String result = br.readLine();

			// close socket
			br.close();
			pw.close();
			socket.close();

			result = normaliseOutputSentence(result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inputSentence;
	}

	public VietnameseDiacritic(String _server, int _port,
			Handler _receiveHandler, Object _input) {
		server = _server;
		port = _port;
	}

	public VietnameseDiacritic() {		
		if (ApplicationConfigs.getInstance().getConnectionMode().equals("0")) {
			server = "172.16.78.31";
			port = 2345;
		} else {
			server = "mica.edu.vn";
			port = 1571;
		}
	}

	private String standardiseInputSentence(String sentence) {
		sentence = sentence.toLowerCase();

		return sentence;
	}

	private String normaliseOutputSentence(String sentence) {
		String result = sentence.replaceAll("\\|[\\d\\s]+\\-[\\d\\s]+\\|", "");
		result = result.replace("  ", " ");
		return result;
	}
}