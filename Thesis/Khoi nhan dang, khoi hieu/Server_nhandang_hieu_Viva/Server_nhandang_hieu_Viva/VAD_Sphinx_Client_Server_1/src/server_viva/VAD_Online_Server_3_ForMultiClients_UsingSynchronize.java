package server_viva;

//import edu.cmu.sphinx.frontend.util.Microphone;
import java.io.DataOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import understanding.Function;
import understanding.Parameter;
import understanding.Understanding;
import edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
//da test thu cung voi class client 2 ngay 1/3/2013
//chay tot, nhan du
//chay mai mai do vong while o main method de luon lang nghe client

public class VAD_Online_Server_3_ForMultiClients_UsingSynchronize {

	int clientNumber = 0;
	// day la vd voi 3 recognizer
	// thuc te co the tinh so recognizer ( goi so do la n ) nhu sau
	// voi truong hop co 100 client cung ket noi voi server trong 1s va ta mong
	// muon
	// sau 2s thi 100 yc gui den duoc nhan dang
	// vi tg trg binh 1 lan nhan dang la 0,5
	// => (phan nguyen cua ( 100 / n ) + 1 ) * 0,5 = 2 => n = 33
	// voi n = 3 va cac thong so va yeu cau nhu tren thi phuc vu douc 10 client
	// ket noi voi server trong vong 1s

	Recognizer recognizer_0 = null;
	Recognizer recognizer_1 = null;
	Recognizer recognizer_2 = null;

	private boolean[] isFree = { true, true, true };
	private long time[] = { 0, 1, 2 };

	URL audioFileURL_0;
	URL audioFileURL_1;
	URL audioFileURL_2;

	URL configURL;

	ConfigurationManager cm_0;
	ConfigurationManager cm_1;
	ConfigurationManager cm_2;

	AudioFileDataSource dataSource_0;
	AudioFileDataSource dataSource_1;
	AudioFileDataSource dataSource_2;

	CommonClassForMultiThreads commonObj = new CommonClassForMultiThreads();
	CommonClassForMultiThreads commonObj_0 = new CommonClassForMultiThreads();
	CommonClassForMultiThreads commonObj_1 = new CommonClassForMultiThreads();
	CommonClassForMultiThreads commonObj_2 = new CommonClassForMultiThreads();

	public static void main(String[] args) {

		System.out.println("server online 3 da chay");

		new VAD_Online_Server_3_ForMultiClients_UsingSynchronize();

	}

	public VAD_Online_Server_3_ForMultiClients_UsingSynchronize() {
		try {

			audioFileURL_0 = new File("hienka_0.wav").toURI().toURL();
			audioFileURL_1 = new File("hienka_1.wav").toURI().toURL();
			audioFileURL_2 = new File("hienka_2.wav").toURI().toURL();

			configURL = VAD_Online_Server_3_ForMultiClients_UsingSynchronize.class
					.getResource("Viva4.config.xml");

			cm_0 = new ConfigurationManager(configURL);
			cm_1 = new ConfigurationManager(configURL);
			cm_2 = new ConfigurationManager(configURL);

			recognizer_0 = (Recognizer) cm_0.lookup("recognizer");
			recognizer_0.allocate();

			recognizer_1 = (Recognizer) cm_1.lookup("recognizer");
			recognizer_1.allocate();

			recognizer_2 = (Recognizer) cm_2.lookup("recognizer");
			recognizer_2.allocate();

			ServerSocket serverSocket = new ServerSocket(8021);
			clientNumber = 0;

			while (true) {
				Socket clientSocket = serverSocket.accept();

				Thread threadForClient = new Thread(new HandleAClient(
						clientSocket));
				threadForClient.start();
				threadForClient.setPriority(Thread.MAX_PRIORITY);

				System.out.println("\nsố lần đã kết nối để truyền nhận: "
						+ (clientNumber + 1));
				// text2.setText((clientNumber + 1)+"");

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private class CommonClassForMultiThreads {

		private Lock lock = new ReentrantLock(true);

		private Lock lock_0 = new ReentrantLock(true);
		private Lock lock_1 = new ReentrantLock(true);
		private Lock lock_2 = new ReentrantLock(true);

		private int chooseRecognizer() {
			lock.lock();
			int index = 0;
			int temp = findIndexOfMinElement(time);
			if (isFree[temp] == true) {
				index = temp;
			} else {
				index = Integer.MAX_VALUE;
			}
			lock.unlock();
			return index;

		}

		private int findIndexOfMinElement(long[] input) {
			long min = Long.MAX_VALUE;
			int indexOfMin = 0;

			for (int i = 0; i < input.length; i++) {
				if (min >= input[i]) {
					min = input[i];
					indexOfMin = i;
				}
			}
			return indexOfMin;
		}

		private Result writeFileAndRecognize_0(int[] vectorOutputAfter) {
			lock_0.lock();
			// isFree[0] = false;
			Result result = null;

			try {
				int sampleRate = 16000;
				long frameCounter = 0;
				double duration = vectorOutputAfter.length / 16000.0; // Seconds
				long numFrames1 = (long) (duration * sampleRate);
				int l = vectorOutputAfter.length;

				String resultText = "";

				WavFile wavFile_0 = WavFile
						.newWavFile(new File("hienka_0.wav"), 1, numFrames1,
								16, sampleRate);

				// Loop until all frames written
				while (frameCounter < numFrames1) {
					long remaining = wavFile_0.getFramesRemaining();
					int toWrite = (remaining > l) ? l : (int) remaining;

					wavFile_0.writeFrames(vectorOutputAfter, toWrite);

					frameCounter++;
				}
				wavFile_0.close();
				System.out
						.println("vadonline_server_3: da ghi xong so mau moi vao 1 file hienka_0 ");

				//
				dataSource_0 = (AudioFileDataSource) cm_0
						.lookup("audioFileDataSource");
				dataSource_0.setAudioFile(audioFileURL_0, null);
				result = recognizer_0.recognize();

			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				lock_0.unlock();
				// time[0] = System.currentTimeMillis();
				// isFree[0] = true;
				return result;
			}

		}

		private Result writeFileAndRecognize_1(int[] vectorOutputAfter) {
			lock_1.lock();
			// isFree[1] = false;
			Result result = null;

			try {
				int sampleRate = 16000;
				long frameCounter = 0;
				double duration = vectorOutputAfter.length / 16000.0; // Seconds
				long numFrames1 = (long) (duration * sampleRate);
				int l = vectorOutputAfter.length;

				String resultText = "";

				WavFile wavFile_1 = WavFile
						.newWavFile(new File("hienka_1.wav"), 1, numFrames1,
								16, sampleRate);

				// Loop until all frames written
				while (frameCounter < numFrames1) {
					long remaining = wavFile_1.getFramesRemaining();
					int toWrite = (remaining > l) ? l : (int) remaining;

					wavFile_1.writeFrames(vectorOutputAfter, toWrite);

					frameCounter++;
				}
				wavFile_1.close();
				System.out
						.println("vadonline_server_3: da ghi xong so mau moi vao 1 file hienka_1 ");

				//
				dataSource_1 = (AudioFileDataSource) cm_1
						.lookup("audioFileDataSource");
				dataSource_1.setAudioFile(audioFileURL_1, null);
				result = recognizer_1.recognize();

			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				lock_1.unlock();
				// isFree[1] = true;
				// time[1] = System.currentTimeMillis();
				return result;
			}

		}

		private Result writeFileAndRecognize_2(int[] vectorOutputAfter) {
			lock_2.lock();
			// isFree[2] = false;
			Result result = null;

			try {
				int sampleRate = 16000;
				long frameCounter = 0;
				double duration = vectorOutputAfter.length / 16000.0; // Seconds
				long numFrames1 = (long) (duration * sampleRate);
				int l = vectorOutputAfter.length;

				String resultText = "";

				WavFile wavFile_2 = WavFile
						.newWavFile(new File("hienka_2.wav"), 1, numFrames1,
								16, sampleRate);

				// Loop until all frames written
				while (frameCounter < numFrames1) {
					long remaining = wavFile_2.getFramesRemaining();
					int toWrite = (remaining > l) ? l : (int) remaining;

					wavFile_2.writeFrames(vectorOutputAfter, toWrite);

					frameCounter++;
				}
				wavFile_2.close();
				System.out
						.println("vadonline_server_3: da ghi xong so mau moi vao 1 file hienka_2 ");

				//
				dataSource_2 = (AudioFileDataSource) cm_2
						.lookup("audioFileDataSource");
				dataSource_2.setAudioFile(audioFileURL_2, null);
				result = recognizer_2.recognize();

			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				lock_2.unlock();
				// time[2] = System.currentTimeMillis();
				// isFree[2] = true;
				return result;

			}

		}
	}

	class HandleAClient implements Runnable {

		private Socket clientSocket = null;
		// private long batdau = 0;
		// private long ketthuc = 0;
		private long[] beginRecognize = new long[3];
		private long[] finishRecognize = new long[3];
		// private long beginWriteFile = 0;
		// private long finishWriteFile = 0;
		// private long beginAlocate = 0;
		// private long finishAlocate = 0;

		private int indexOfClient = 0;

		public HandleAClient(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		public void run() {
			boolean waitRecognizer = true;
			while (waitRecognizer) {
				indexOfClient = commonObj.chooseRecognizer();
				if (indexOfClient != Integer.MAX_VALUE) {
					waitRecognizer = false;
				}
			}
			// indexOfClient = clientNumber;
			clientNumber++;
			String resultText = "";
			Result result = null;
			try {

				ObjectInputStream ois = new ObjectInputStream(
						clientSocket.getInputStream());
				DataOutputStream dosServer = new DataOutputStream(
						clientSocket.getOutputStream());
				short[] vectorOutputAfterInShort = (short[]) ois.readObject();
				int[] vectorOutputAfter = new int[vectorOutputAfterInShort.length];
				for (int i = 0; i < vectorOutputAfter.length; i++) {
					vectorOutputAfter[i] = vectorOutputAfterInShort[i];
				}
				if ((indexOfClient) % 3 == 0) {
					// synchronized (commonObj_0) {
					// bao rang recognizer_0 dang ban
					isFree[0] = false;
					beginRecognize[0] = System.currentTimeMillis();
					// cho time[0] = maxvalue de tien cho viec thuc hien choose
					// methode
					time[0] = Long.MAX_VALUE;
					result = commonObj_0
							.writeFileAndRecognize_0(vectorOutputAfter);
					isFree[0] = true;
					time[0] = System.currentTimeMillis();

					// }

				} else if (indexOfClient % 3 == 1) {
					// synchronized (commonObj_1) {
					isFree[1] = false;
					beginRecognize[1] = System.currentTimeMillis();
					time[1] = Long.MAX_VALUE;
					result = commonObj_1
							.writeFileAndRecognize_1(vectorOutputAfter);
					isFree[1] = true;
					time[1] = System.currentTimeMillis();

					// }
				} else {
					// synchronized (commonObj_2) {
					isFree[2] = false;
					beginRecognize[2] = System.currentTimeMillis();
					time[2] = Long.MAX_VALUE;
					result = commonObj_2
							.writeFileAndRecognize_2(vectorOutputAfter);
					isFree[2] = true;
					time[2] = System.currentTimeMillis();

					// }
				}

				if (result != null) {
					resultText = result.getBestFinalResultNoFiller();
					if (resultText.equals("")) {
						resultText = "no thing";
					}
				} else {
					System.out.println("I can't hear what you said.");
				}
				resultText = resultText.replaceAll("_", " ");
				// resultText += "";

				// dua vao khoi hieu
				Understanding understanding = new Understanding();
				Function function = understanding
						.understandCommand(adjustmentString(resultText));

				String s = resultText + ";";// adjustmentString(resultText)+"|";
				s += function.getFuntionCode() + ";" + function.getModuleCode()
						+ ";";
				Parameter[] paras = function.getParameters();

				for (int i = 0; i < function.getNumberofParameter(); i++) {
					s += paras[i].getParameter() + ";" + paras[i].getValue()
							+ ";";
				}

				// s += "\n";
				String s1 = adjustmentString1(s) + "\n";

				dosServer.writeBytes(s1);
				dosServer.flush();

				String stringAfterRecognizeAndAdjustment = adjustmentString(resultText);

				System.out.println("Client da noi: "
						+ stringAfterRecognizeAndAdjustment);
				// System.out.println("hà nội");
				System.out.println("ket qua sau nhan dang: \n" + resultText);
				System.out.println("ket qua sau khi chinh "
						+ "sua thanh tieng viet: \n"
						+ stringAfterRecognizeAndAdjustment);
				System.out
						.println("Loi noi ban dau(chua duoc chuyen thanh tieng viet chuan )"
								+ "\n+ ket qua sau khi qua khoi hieu: \n" + s);
				System.out
						.println("Dong text tren duoc chinh sua "
								+ "de truyen xuong client: \n"
								+ s1
								+ "\nO duoi client se tu chuyen ket qua nay thanh dang tieng viet chuan"
								+ "\nVa van dam bao giu dung ket qua cua khoi hieu");

			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		private String adjustmentString(String stringInput) {
			String outputString = "";
			// asc
			outputString = stringInput.replaceAll("aw", "ă");
			outputString = outputString.replaceAll("AW", "Ă");
			outputString = outputString.replaceAll("aa", "â");
			outputString = outputString.replaceAll("AA", "Â");
			outputString = outputString.replaceAll("ee", "ê");
			outputString = outputString.replaceAll("EE", "Ê");
			outputString = outputString.replaceAll("oz", "ô");
			outputString = outputString.replaceAll("OZ", "Ô");
			outputString = outputString.replaceAll("ow", "ơ");
			outputString = outputString.replaceAll("OW", "Ơ");
			outputString = outputString.replaceAll("uw", "ư");
			outputString = outputString.replaceAll("UW", "Ư");
			outputString = outputString.replaceAll("dd", "đ");
			outputString = outputString.replaceAll("DD", "Đ");

			// tone
			outputString = outputString.replaceAll("a2", "à");
			outputString = outputString.replaceAll("a3", "ã");
			outputString = outputString.replaceAll("a4", "ả");
			outputString = outputString.replaceAll("a5", "á");
			outputString = outputString.replaceAll("a6", "ạ");

			outputString = outputString.replaceAll("ă2", "ằ");
			outputString = outputString.replaceAll("ă3", "ẵ");
			outputString = outputString.replaceAll("ă4", "ẳ");
			outputString = outputString.replaceAll("ă5", "ắ");
			outputString = outputString.replaceAll("ă6", "ặ");

			outputString = outputString.replaceAll("â2", "ầ");
			outputString = outputString.replaceAll("â3", "ẫ");
			outputString = outputString.replaceAll("â4", "ẩ");
			outputString = outputString.replaceAll("â5", "ấ");
			outputString = outputString.replaceAll("â6", "ậ");

			outputString = outputString.replaceAll("e2", "è");
			outputString = outputString.replaceAll("e3", "ẽ");
			outputString = outputString.replaceAll("e4", "ẻ");
			outputString = outputString.replaceAll("e5", "é");
			outputString = outputString.replaceAll("e6", "ẹ");

			outputString = outputString.replaceAll("ê2", "ề");
			outputString = outputString.replaceAll("ê3", "ễ");
			outputString = outputString.replaceAll("ê4", "ể");
			outputString = outputString.replaceAll("ê5", "ế");
			outputString = outputString.replaceAll("ê6", "ệ");

			outputString = outputString.replaceAll("i2", "ì");
			outputString = outputString.replaceAll("i3", "ĩ");
			outputString = outputString.replaceAll("i4", "ỉ");
			outputString = outputString.replaceAll("i5", "í");
			outputString = outputString.replaceAll("i6", "ị");

			outputString = outputString.replaceAll("o2", "ò");
			outputString = outputString.replaceAll("o3", "õ");
			outputString = outputString.replaceAll("o4", "ỏ");
			outputString = outputString.replaceAll("o5", "ó");
			outputString = outputString.replaceAll("o6", "ọ");

			outputString = outputString.replaceAll("ô2", "ồ");
			outputString = outputString.replaceAll("ô3", "ỗ");
			outputString = outputString.replaceAll("ô4", "ổ");
			outputString = outputString.replaceAll("ô5", "ố");
			outputString = outputString.replaceAll("ô6", "ộ");

			outputString = outputString.replaceAll("ơ2", "ờ");
			outputString = outputString.replaceAll("ơ3", "ỡ");
			outputString = outputString.replaceAll("ơ4", "ở");
			outputString = outputString.replaceAll("ơ5", "ớ");
			outputString = outputString.replaceAll("ơ6", "ợ");

			outputString = outputString.replaceAll("u2", "ù");
			outputString = outputString.replaceAll("u3", "ũ");
			outputString = outputString.replaceAll("u4", "ủ");
			outputString = outputString.replaceAll("u5", "ú");
			outputString = outputString.replaceAll("u6", "ụ");

			outputString = outputString.replaceAll("ư2", "ừ");
			outputString = outputString.replaceAll("ư3", "ữ");
			outputString = outputString.replaceAll("ư4", "ử");
			outputString = outputString.replaceAll("ư5", "ứ");
			outputString = outputString.replaceAll("ư6", "ự");

			outputString = outputString.replaceAll("y2", "ỳ");
			outputString = outputString.replaceAll("y3", "ỹ");
			outputString = outputString.replaceAll("y4", "ỷ");
			outputString = outputString.replaceAll("y5", "ý");
			outputString = outputString.replaceAll("y6", "ỵ");

			// bat dau sua chu in hoa

			outputString = outputString.replaceAll("A2", "À");
			outputString = outputString.replaceAll("A3", "Ã");
			outputString = outputString.replaceAll("A4", "Ả");
			outputString = outputString.replaceAll("A5", "Á");
			outputString = outputString.replaceAll("A6", "Ạ");

			outputString = outputString.replaceAll("Ă2", "Ằ");
			outputString = outputString.replaceAll("Ă3", "Ẵ");
			outputString = outputString.replaceAll("Ă4", "Ẳ");
			outputString = outputString.replaceAll("Ă5", "Ắ");
			outputString = outputString.replaceAll("Ă6", "Ặ");

			outputString = outputString.replaceAll("Â2", "Ầ");
			outputString = outputString.replaceAll("Â3", "Ẫ");
			outputString = outputString.replaceAll("Â4", "Ẩ");
			outputString = outputString.replaceAll("Â5", "Ấ");
			outputString = outputString.replaceAll("Â6", "Ậ");

			outputString = outputString.replaceAll("E2", "È");
			outputString = outputString.replaceAll("E3", "Ẽ");
			outputString = outputString.replaceAll("E4", "Ẻ");
			outputString = outputString.replaceAll("E5", "É");
			outputString = outputString.replaceAll("E6", "Ẹ");

			outputString = outputString.replaceAll("Ê2", "Ề");
			outputString = outputString.replaceAll("Ê3", "Ễ");
			outputString = outputString.replaceAll("Ê4", "Ể");
			outputString = outputString.replaceAll("Ê5", "Ế");
			outputString = outputString.replaceAll("Ê6", "Ệ");

			outputString = outputString.replaceAll("I2", "Ì");
			outputString = outputString.replaceAll("I3", "Ĩ");
			outputString = outputString.replaceAll("I4", "Ỉ");
			outputString = outputString.replaceAll("I5", "Í");
			outputString = outputString.replaceAll("I6", "Ị");

			outputString = outputString.replaceAll("O2", "Ò");
			outputString = outputString.replaceAll("O3", "Õ");
			outputString = outputString.replaceAll("O4", "Ỏ");
			outputString = outputString.replaceAll("O5", "Ó");
			outputString = outputString.replaceAll("O6", "Ọ");

			outputString = outputString.replaceAll("Ô2", "Ồ");
			outputString = outputString.replaceAll("Ô3", "Ỗ");
			outputString = outputString.replaceAll("Ô4", "Ổ");
			outputString = outputString.replaceAll("Ô5", "Ố");
			outputString = outputString.replaceAll("Ô6", "Ộ");

			outputString = outputString.replaceAll("Ơ2", "Ờ");
			outputString = outputString.replaceAll("Ơ3", "Ỡ");
			outputString = outputString.replaceAll("Ơ4", "Ở");
			outputString = outputString.replaceAll("Ơ5", "Ớ");
			outputString = outputString.replaceAll("Ơ6", "Ợ");

			outputString = outputString.replaceAll("U2", "Ù");
			outputString = outputString.replaceAll("U3", "Ũ");
			outputString = outputString.replaceAll("U4", "Ủ");
			outputString = outputString.replaceAll("U5", "Ú");
			outputString = outputString.replaceAll("U6", "Ụ");

			outputString = outputString.replaceAll("Ư2", "Ừ");
			outputString = outputString.replaceAll("Ư3", "Ữ");
			outputString = outputString.replaceAll("Ư4", "Ử");
			outputString = outputString.replaceAll("Ư5", "Ứ");
			outputString = outputString.replaceAll("Ư6", "Ự");

			outputString = outputString.replaceAll("Y2", "Ỳ");
			outputString = outputString.replaceAll("Y3", "Ỹ");
			outputString = outputString.replaceAll("Y4", "Ỷ");
			outputString = outputString.replaceAll("Y5", "Ý");
			outputString = outputString.replaceAll("Y6", "Ỵ");

			// outputString = outputString.replaceAll("_", " ");

			// con thieu phan chu in hoa cua phan tone

			return outputString;
		}

		// nguoc lai so voi method tren:
		private String adjustmentString1(String stringInput) {
			String outputString = "";

			// tone
			outputString = stringInput.replaceAll("à", "a2");

			outputString = outputString.replaceAll("ã", "a3");
			outputString = outputString.replaceAll("ả", "a4");
			outputString = outputString.replaceAll("á", "a5");
			outputString = outputString.replaceAll("ạ", "a6");

			outputString = outputString.replaceAll("ằ", "ă2");
			outputString = outputString.replaceAll("ẵ", "ă3");
			outputString = outputString.replaceAll("ẳ", "ă4");
			outputString = outputString.replaceAll("ắ", "ă5");
			outputString = outputString.replaceAll("ặ", "ă6");

			outputString = outputString.replaceAll("ầ", "â2");
			outputString = outputString.replaceAll("ẫ", "â3");
			outputString = outputString.replaceAll("ẩ", "â4");
			outputString = outputString.replaceAll("ấ", "â5");
			outputString = outputString.replaceAll("ậ", "â6");

			outputString = outputString.replaceAll("è", "e2");
			outputString = outputString.replaceAll("ẽ", "e3");
			outputString = outputString.replaceAll("ẻ", "e4");
			outputString = outputString.replaceAll("é", "e5");
			outputString = outputString.replaceAll("ẹ", "e6");

			outputString = outputString.replaceAll("ề", "ê2");
			outputString = outputString.replaceAll("ễ", "ê3");
			outputString = outputString.replaceAll("ể", "ê4");
			outputString = outputString.replaceAll("ế", "ê5");
			outputString = outputString.replaceAll("ệ", "ê6");

			outputString = outputString.replaceAll("ì", "i2");
			outputString = outputString.replaceAll("ĩ", "i3");
			outputString = outputString.replaceAll("ỉ", "i4");
			outputString = outputString.replaceAll("í", "i5");
			outputString = outputString.replaceAll("ị", "i6");

			outputString = outputString.replaceAll("ò", "o2");
			outputString = outputString.replaceAll("õ", "o3");
			outputString = outputString.replaceAll("ỏ", "o4");
			outputString = outputString.replaceAll("ó", "o5");
			outputString = outputString.replaceAll("ọ", "o6");

			outputString = outputString.replaceAll("ồ", "ô2");
			outputString = outputString.replaceAll("ỗ", "ô3");
			outputString = outputString.replaceAll("ổ", "ô4");
			outputString = outputString.replaceAll("ố", "ô5");
			outputString = outputString.replaceAll("ộ", "ô6");

			outputString = outputString.replaceAll("ờ", "ơ2");
			outputString = outputString.replaceAll("ỡ", "ơ3");
			outputString = outputString.replaceAll("ở", "ơ4");
			outputString = outputString.replaceAll("ớ", "ơ5");
			outputString = outputString.replaceAll("ợ", "ơ6");

			outputString = outputString.replaceAll("ù", "u2");
			outputString = outputString.replaceAll("ũ", "u3");
			outputString = outputString.replaceAll("ủ", "u4");
			outputString = outputString.replaceAll("ú", "u5");
			outputString = outputString.replaceAll("ụ", "u6");

			outputString = outputString.replaceAll("ừ", "ư2");
			outputString = outputString.replaceAll("ữ", "ư3");
			outputString = outputString.replaceAll("ử", "ư4");
			outputString = outputString.replaceAll("ứ", "ư5");
			outputString = outputString.replaceAll("ự", "ư6");

			outputString = outputString.replaceAll("ỳ", "y2");
			outputString = outputString.replaceAll("ỹ", "y3");
			outputString = outputString.replaceAll("ỷ", "y4");
			outputString = outputString.replaceAll("ý", "y5");
			outputString = outputString.replaceAll("ỵ", "y6");

			// bat dau sua chu in hoa

			outputString = outputString.replaceAll("À", "A2");
			outputString = outputString.replaceAll("Ã", "A3");
			outputString = outputString.replaceAll("Ả", "A4");
			outputString = outputString.replaceAll("Á", "A5");
			outputString = outputString.replaceAll("Ạ", "A6");

			outputString = outputString.replaceAll("Ằ", "Ă2");
			outputString = outputString.replaceAll("Ẵ", "Ă3");
			outputString = outputString.replaceAll("Ẳ", "Ă4");
			outputString = outputString.replaceAll("Ắ", "Ă5");
			outputString = outputString.replaceAll("Ặ", "Ă6");

			outputString = outputString.replaceAll("Ầ", "Â2");
			outputString = outputString.replaceAll("Ẫ", "Â3");
			outputString = outputString.replaceAll("Ẩ", "Â4");
			outputString = outputString.replaceAll("Ấ", "Â5");
			outputString = outputString.replaceAll("Ậ", "Â6");

			outputString = outputString.replaceAll("È", "E2");
			outputString = outputString.replaceAll("Ẽ", "E3");
			outputString = outputString.replaceAll("Ẻ", "E4");
			outputString = outputString.replaceAll("É", "E5");
			outputString = outputString.replaceAll("Ẹ", "E6");

			outputString = outputString.replaceAll("Ề", "Ê2");
			outputString = outputString.replaceAll("Ễ", "Ê3");
			outputString = outputString.replaceAll("Ể", "Ê4");
			outputString = outputString.replaceAll("Ế", "Ê5");
			outputString = outputString.replaceAll("Ệ", "Ê6");

			outputString = outputString.replaceAll("Ì", "I2");
			outputString = outputString.replaceAll("Ĩ", "I3");
			outputString = outputString.replaceAll("Ỉ", "I4");
			outputString = outputString.replaceAll("Í", "I5");
			outputString = outputString.replaceAll("Ị", "I6");

			outputString = outputString.replaceAll("Ò", "O2");
			outputString = outputString.replaceAll("Õ", "O3");
			outputString = outputString.replaceAll("Ỏ", "O4");
			outputString = outputString.replaceAll("Ó", "O5");
			outputString = outputString.replaceAll("Ọ", "O6");

			outputString = outputString.replaceAll("Ồ", "Ô2");
			outputString = outputString.replaceAll("Ỗ", "Ô3");
			outputString = outputString.replaceAll("Ổ", "Ô4");
			outputString = outputString.replaceAll("Ố", "Ô5");
			outputString = outputString.replaceAll("Ộ", "Ô6");

			outputString = outputString.replaceAll("Ờ", "Ơ2");
			outputString = outputString.replaceAll("Ỡ", "Ơ3");
			outputString = outputString.replaceAll("Ở", "Ơ4");
			outputString = outputString.replaceAll("Ớ", "Ơ5");
			outputString = outputString.replaceAll("Ợ", "Ơ6");

			outputString = outputString.replaceAll("Ù", "U2");
			outputString = outputString.replaceAll("Ũ", "U3");
			outputString = outputString.replaceAll("Ủ", "U4");
			outputString = outputString.replaceAll("Ú", "U5");
			outputString = outputString.replaceAll("Ụ", "U6");

			outputString = outputString.replaceAll("Ừ", "Ư2");
			outputString = outputString.replaceAll("Ữ", "Ư3");
			outputString = outputString.replaceAll("Ử", "Ư4");
			outputString = outputString.replaceAll("Ứ", "Ư5");
			outputString = outputString.replaceAll("Ự", "Ư6");

			outputString = outputString.replaceAll("Ỳ", "Y2");
			outputString = outputString.replaceAll("Ỹ", "Y3");
			outputString = outputString.replaceAll("Ỷ", "Y4");
			outputString = outputString.replaceAll("Ý", "Y5");
			outputString = outputString.replaceAll("Ỵ", "Y6");

			// asc
			outputString = outputString.replaceAll("ă", "aw");
			outputString = outputString.replaceAll("Ă", "AW");
			outputString = outputString.replaceAll("â", "aa");
			outputString = outputString.replaceAll("Â", "AA");
			outputString = outputString.replaceAll("ê", "ee");
			outputString = outputString.replaceAll("Ê", "EE");
			outputString = outputString.replaceAll("ô", "oz");
			outputString = outputString.replaceAll("Ô", "OZ");
			outputString = outputString.replaceAll("ơ", "ow");
			outputString = outputString.replaceAll("Ơ", "OW");
			outputString = outputString.replaceAll("ư", "uw");
			outputString = outputString.replaceAll("Ư", "UW");
			outputString = outputString.replaceAll("đ", "dd");
			outputString = outputString.replaceAll("Đ", "DD");

			// outputString = outputString.replaceAll("_", " ");

			// con thieu phan chu in hoa cua phan tone

			return outputString;
		}

	}

}
