package com.example.com.mica.viva.diacritic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;

public class SendReceiveSocketTask implements Runnable {

	private Socket socket = null;
	private String server;
	private int port;
	private Handler receiveHandler;
	private Object input;

	public void run() {
		try {
			socket = new Socket(server, port);

			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter pw = new PrintWriter(socket.getOutputStream());

			pw.println(input);
			pw.flush();

			String result = br.readLine();

			// send message to handler
			Message msg = Message.obtain();
			msg.obj = result;
			receiveHandler.sendMessage(msg);
			// close socket
			br.close();
			pw.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SendReceiveSocketTask(String _server, int _port,
			Handler _receiveHandler, Object _input) {
		server = _server;
		port = _port;
		receiveHandler = _receiveHandler;
		input = _input;
	}

}