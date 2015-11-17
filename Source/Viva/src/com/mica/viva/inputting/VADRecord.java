package com.mica.viva.inputting;

import java.io.BufferedReader;

import com.mica.viva.ApplicationConfigs;
import com.mica.viva.Constant;
import com.mica.viva.controller.UIController;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

//import com.example.vad_online_1_android.VAD_For_VectorInput_Online;

import android.R;
import android.R.integer;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import com.mica.viva.controller.*;

import android.widget.Toast;

public class VADRecord extends Activity {
	// binhpro
	public static final int STATUS_NETWORK_ERROR = -1;

	// int _inputType;
	long timesWaitToSend_Recognize_Receive_FromServer = 0;

	int countTimesWhenBufferIsFull = 0;

	private AudioRecord recorder = null;
	private boolean isStopRecord = false;
	private boolean StopActivity = false;

	private int capacityMaxOfBuffer = 8000;//
	private BlockingQueue<Integer> buffer = new ArrayBlockingQueue<Integer>(
			capacityMaxOfBuffer);

	long batdau = 0;
	long ganKetThuc = 0;
	long ketthuc = 0;

	private long begin = 0;
	private long finishToPause = 0;

	Thread thread1 = null;
	Thread thread2 = null;
	Thread thread3 = null;

	private MsgHandler msghdr;

	Queue sameplesQueue = new LinkedList();
	String result = "he";
	Socket socket = new Socket();

	//private SharedPreferences preference;
	//private OnSharedPreferenceChangeListener preferencesListener;
	private String IPServer = "172.16.78.31";
	private Integer Server_Port = 8021;
	protected ProgressDialog myPd_ring;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (ApplicationConfigs.getInstance().getConnectionMode().equals("0")) {
			IPServer = "172.16.78.31";
			Server_Port = 8021;
		} else {
			IPServer = "mica.edu.vn";
			Server_Port = 1569;
		}

		UIController.SetButtonRecording();
		msghdr = new MsgHandler();
		myPd_ring = new ProgressDialog(VADRecord.this);
		myPd_ring.setTitle("Đợi trong giây lát!");
		myPd_ring.setMessage("Đang ghi âm...");
		myPd_ring.setButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent returnIntent = new Intent();
				returnIntent.putExtra("Result", Constant.FUNCTION_IGNORE);
				isStopRecord = false;
				StopActivity = true;
				setResult(RESULT_OK, returnIntent);
				finish();
			}
		});
		// myPd_ring.setProgress(0); // set percentage completed to 0%
		myPd_ring.show();

		// UIController.displayRequestText("Recording");
		isStopRecord = false;

		begin = System.currentTimeMillis();
		batdau = System.currentTimeMillis();

		

		// CustomPreference();

		// setParam();
		// Bundle extras = getIntent().getExtras();
		// if (extras != null) {
		// _inputType = extras.getInt("input_type");
		// }

	}

	@Override
	protected void onStart() {		
		super.onStart();
		thread1 = new Thread(new Record_Task());
		thread1.start();
		// thread1.setPriority(Thread.MAX_PRIORITY);

		thread2 = new Thread(new Read_DelteleFromBuffer_Task2());
		thread2.start();
	}
	
	public class MsgHandler extends Handler {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 124: {
				finishToPause = System.currentTimeMillis();
				UIController.SetButtonOffRecording();
				myPd_ring.setMessage("Đang gửi dữ liệu...");
				thread3 = new Thread(
						new SendSamplesToAndReceiveFromServer_Task4());
				thread3.start();
				thread3.setPriority(Thread.MAX_PRIORITY);
				break;
			}
			case 125: {
				isStopRecord = false;
				StopActivity = true;

				Intent returnIntent = new Intent();
				returnIntent.putExtra("Result", result);
				setResult(RESULT_OK, returnIntent);
				finish();
				break;
			}
			case STATUS_NETWORK_ERROR:
				Intent returnIntent = new Intent();
				returnIntent.putExtra("Result", Constant.INPUT_VOICE_ERROR);
				setResult(RESULT_OK, returnIntent);
				finish();
				break;
			default: {
				break;
			}
			}

		}
	}

	class Record_Task implements Runnable {

		private static final int RECORDER_BPP = 16;
		private static final int RECORDER_SAMPLERATE = 16000;
		private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_CONFIGURATION_MONO;
		private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

		private int bufferSize = 0;

		byte[] data = new byte[0];
		int numBytesRead = 0;

		public int[] getSampleFromByteArray(byte[] sampleBytes) {
			int[] samples = new int[sampleBytes.length / 2];
			for (int i = 0; i < samples.length; i++) {
				// samples[i] = sampleBytes[i*2] * 256 + sampleBytes[i*2 + 1];
				samples[i] = sampleBytes[i * 2] + sampleBytes[i * 2 + 1] * 256;
				// vi luu theo kieu little indian
			}

			return samples;
		}

		public void run() {

			try {

				bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
						RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
				recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
						RECORDER_SAMPLERATE, RECORDER_CHANNELS,
						RECORDER_AUDIO_ENCODING, bufferSize);

				recorder.startRecording();
				data = new byte[bufferSize];

				while (isStopRecord == false) {

					numBytesRead = recorder.read(data, 0, bufferSize);

					int samples[] = new int[data.length / 2];
					if (numBytesRead >= data.length) {
						samples = getSampleFromByteArray(data);

						for (int i = 0; i < samples.length; i++) {
							buffer.put(samples[i]);// de lay ra va vad

							sameplesQueue.offer(samples[i]);
							// dung hang doi nay de giu mau ban dau
						}

						if ((buffer.size() >= capacityMaxOfBuffer)
								&& countTimesWhenBufferIsFull != 0) {

							thread1.yield();
							thread1.sleep(50);

						}

					}

				}
			} catch (Exception e) {
				// System.out.println("IO Exception");
				System.exit(0);
			}
			// msghdr.sendEmptyMessage(125);

		}
	}

	class Read_DelteleFromBuffer_Task2 implements Runnable {
		int numsSamplesPerFrame = 400;
		int numsSamplesShift = 384;
		double e_PowerOfNoise_OfLastFrameOfEachTime_WhenBuffferIsFull = 0;
		double[] noiseMean_VectorOfLastFrameOfEachTime_WhenBufferIsFull = new double[numsSamplesPerFrame];
		double threshold_OfLastFrameOfEachTime_WhenBuffferIsFull = 0;
		double ltsd_OfLastFrameOfEachTime_WhenBuffferIsFull = 0;
		// static double[] nmk_VectorOfLastFrameOfEachTime_WhenBufferIsFull =
		// new double[numsSamplesPerFrame];
		double[] mag_VectorOfLastFrameOfEachTime_WhenBufferIsFull = new double[numsSamplesPerFrame];
		Queue queueForMarker = new LinkedList();

		// Queue sampleQueue = new LinkedList();

		public void run() {
			try {
				// int[] vectorInputForVAD = new int[24000];
				while (isStopRecord == false) {

					// dung hang doi de chua 2 vector marker lien tiep de tinh
					// toan va ngat thu am
					// giua 2 lan 32000 mau lien tiep
					// Queue queueForMarker = new LinkedList();

					if (buffer.size() >= capacityMaxOfBuffer) {
						int[] vectorInputForVAD = new int[capacityMaxOfBuffer];
						for (int i = 0; i < vectorInputForVAD.length; i++) {
							vectorInputForVAD[i] = buffer.take();
							if (i == vectorInputForVAD.length - 1) {
								// thread2.setPriority(Thread.MAX_PRIORITY);
							}
						}
						numsSamplesPerFrame = 400;
						numsSamplesShift = 384;
						VADForVectorInputOnline vadForVector = new VADForVectorInputOnline(
								vectorInputForVAD, 400, 384);
						int[][] framesMatrix = vadForVector
								.computeFrames_Matrix(400, 384,
										vectorInputForVAD);
						double[][] matrixMAG = vadForVector
								.computeMAG(framesMatrix);
						double[][] matrixLTSE = vadForVector.computeMatrixLTSE(
								matrixMAG, 6);

						int numRows = matrixLTSE.length;// cung chinh la
														// numsSamplesPerFrame
														// hay la 400
						int nfft = numRows;
						int numColums = matrixLTSE[0].length; // numberOfFrames
																// sau khi phan
																// khung moi khi
																// bo dem day
						double[] vectorRowLTSD = new double[numColums];
						double[] vectorRowThreshold = new double[numColums];
						double[] vector_E_PowerOfBaseNoise_EachFrame = new double[numColums];
						double[][] matrixNoiseMean = new double[numRows][numColums];
						// dang o trong if block
						double[][] matrixNMK = vadForVector.computeMatrix_NMK(
								numColums, matrixMAG);
						double alpha = 0.97;
						// tinh 2 hang so trong cong thuc tinh threshold
						double a = (VADForVectorInputOnline.THRESHOLD_0 - VADForVectorInputOnline.THRESHOLD_1)
								/ (VADForVectorInputOnline.E0 - VADForVectorInputOnline.E1);
						double b = VADForVectorInputOnline.THRESHOLD_0
								- VADForVectorInputOnline.E0 * a;
						// => threshold(l) = a * E(l) + b

						// viec tinh NMK da duoc tinh ngam trong method tinh
						// ltsd va
						// threshold
						// voi so frames lien ke su dung la numRows = 400

						if (countTimesWhenBufferIsFull == 0) {
							int num_InitialFrames_NonDetected = 2;
							double numInitialSamplesForCompute_Initial_E_And_Initial_NoiseMean = 500.0;
							double tempForInitial_E = 0;
							for (int i = 0; i < numInitialSamplesForCompute_Initial_E_And_Initial_NoiseMean; i++) {
								tempForInitial_E += (Math.pow(
										vectorInputForVAD[i], 2))
										/ numInitialSamplesForCompute_Initial_E_And_Initial_NoiseMean;
							}
							vector_E_PowerOfBaseNoise_EachFrame[num_InitialFrames_NonDetected] = 10 * Math
									.log10(tempForInitial_E);
							// tinh tb pho cua khung
							for (int i = 0; i < numInitialSamplesForCompute_Initial_E_And_Initial_NoiseMean; i++) {
								matrixNoiseMean[0][num_InitialFrames_NonDetected] += (Math
										.abs(vectorInputForVAD[i]) / numInitialSamplesForCompute_Initial_E_And_Initial_NoiseMean);
							}
							// dang o trong if block buffer is full
							for (int row = 1; row < numRows; row++) {
								matrixNoiseMean[row][num_InitialFrames_NonDetected] = matrixNoiseMean[0][num_InitialFrames_NonDetected];
							}

							// tinh threshold tai chi so
							// num_InitialFrames_NonDetected
							vectorRowThreshold[num_InitialFrames_NonDetected] = a
									* vector_E_PowerOfBaseNoise_EachFrame[num_InitialFrames_NonDetected]
									+ b;

							// tinh ltsd tai chi so
							// num_InitialFrames_NonDetected
							// nfft = numRows: so samples tren 1 frame
							double temp = 0;
							for (int k = 0; k < numRows; k++) {
								temp += Math
										.pow(matrixLTSE[k][num_InitialFrames_NonDetected],
												2)
										/ (Math.pow(
												matrixNoiseMean[k][num_InitialFrames_NonDetected],
												2) * nfft);
							}
							vectorRowLTSD[num_InitialFrames_NonDetected] = 10 * Math
									.log10(temp);

							// ke tu chi so num_InitialFrames_NonDetected tro di
							// phan tu sau tinh theo phan tu truoc
							// dang o trong if block buffer is full
							for (int colum = num_InitialFrames_NonDetected + 1; colum < numColums; colum++) {
								if (vectorRowLTSD[colum - 1] < vectorRowThreshold[colum - 1]) {

									// ko co tieng noi thi
									// cap nhat E(l): gia tri moi lay bang tb
									// nang luong cua frame
									// lien truoc no
									// double[] tempColumVector = new
									// double[numColums];
									double tempForE = 0;
									for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
										tempForE += Math.pow(
												matrixMAG[rowIndex][colum - 1],
												2)
												/ numRows;
									}
									vector_E_PowerOfBaseNoise_EachFrame[colum] = 10 * Math
											.log10(tempForE);

									// tinh NoiseMean(k,l)
									for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
										matrixNoiseMean[rowIndex][colum] = matrixNoiseMean[rowIndex][colum - 1];

									}
									// dang o trong if block buffer is full

								} else {// khi co tieng noi o frame lien truoc
									// thi E(l) lay bang E(l-1): nang luong tb
									// cua nhieu o frame
									// lien truoc
									// tinh E
									vector_E_PowerOfBaseNoise_EachFrame[colum] = vector_E_PowerOfBaseNoise_EachFrame[colum - 1];

									// tinh NoiseMean(k,l): cap nhat nhieu moi
									for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
										matrixNoiseMean[rowIndex][colum] = alpha
												* matrixNoiseMean[rowIndex][colum - 1]
												+ (1 - alpha)
												* matrixNMK[rowIndex][colum];

									}

								}
								// tinh threshold[l]
								vectorRowThreshold[colum] = a
										* vector_E_PowerOfBaseNoise_EachFrame[colum]
										+ b;

								// tinh ltsd(k,l)
								double tempForLTSD = 0;
								for (int k = 0; k < numRows; k++) {
									tempForLTSD += Math.pow(
											matrixLTSE[k][colum], 2)
											/ (Math.pow(
													matrixNoiseMean[k][colum],
													2) * nfft);
								}
								// dang o trong if block buffer is full
								vectorRowLTSD[colum] = 10 * Math
										.log10(tempForLTSD);
							}

						} else {
							// tinh cho frame dau tien: frame co chi so 0 cua
							// moi lan 32000 mau, ke tu lan thu 2 tro di
							if (ltsd_OfLastFrameOfEachTime_WhenBuffferIsFull < threshold_OfLastFrameOfEachTime_WhenBuffferIsFull) {

								// ko co tieng noi thi
								// cap nhat E(l): gia tri moi lay bang tb nang
								// luong cua frame
								// lien truoc no
								// double[] tempColumVector = new
								// double[numColums];
								double tempForE = 0;
								for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
									tempForE += Math
											.pow(mag_VectorOfLastFrameOfEachTime_WhenBufferIsFull[rowIndex],
													2)
											/ numRows;
								}
								vector_E_PowerOfBaseNoise_EachFrame[0] = 10 * Math
										.log10(tempForE);

								// tinh NoiseMean(k,l)
								for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
									matrixNoiseMean[rowIndex][0] = noiseMean_VectorOfLastFrameOfEachTime_WhenBufferIsFull[rowIndex];

								}
								// dang o trong if block buffer is full

							} else {// khi co tieng noi o frame lien truoc
								// thi E(l) lay bang E(l-1): nang luong tb cua
								// nhieu o frame
								// lien truoc
								// tinh E
								vector_E_PowerOfBaseNoise_EachFrame[0] = e_PowerOfNoise_OfLastFrameOfEachTime_WhenBuffferIsFull;

								// tinh NoiseMean(k,l): cap nhat nhieu moi
								for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
									matrixNoiseMean[rowIndex][0] = alpha
											* noiseMean_VectorOfLastFrameOfEachTime_WhenBufferIsFull[rowIndex]
											+ (1 - alpha)
											* matrixNMK[rowIndex][0];

								}

							}
							// tinh threshold[l]
							vectorRowThreshold[0] = a
									* vector_E_PowerOfBaseNoise_EachFrame[0]
									+ b;

							// tinh ltsd(k,0)
							double tempForLTSD = 0;
							for (int k = 0; k < numRows; k++) {
								tempForLTSD += Math.pow(matrixLTSE[k][0], 2)
										/ (Math.pow(matrixNoiseMean[k][0], 2) * nfft);
							}
							vectorRowLTSD[0] = 10 * Math.log10(tempForLTSD);
							// dang o trong if block buffer is full

							// tinh cho cac frame tiep theo tro di ( tu chi so 1
							// tro di )

							// ke tu chi so 1 tro di
							// phan tu sau tinh theo phan tu truoc
							for (int colum = 1; colum < numColums; colum++) {
								if (vectorRowLTSD[colum - 1] < vectorRowThreshold[colum - 1]) {

									// ko co tieng noi thi
									// cap nhat E(l): gia tri moi lay bang tb
									// nang luong cua frame
									// lien truoc no
									// double[] tempColumVector = new
									// double[numColums];
									double tempForE = 0;
									for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
										tempForE += Math.pow(
												matrixMAG[rowIndex][colum - 1],
												2)
												/ numRows;
									}
									vector_E_PowerOfBaseNoise_EachFrame[colum] = 10 * Math
											.log10(tempForE);

									// tinh NoiseMean(k,l)
									for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
										matrixNoiseMean[rowIndex][colum] = matrixNoiseMean[rowIndex][colum - 1];

									}
									// dang o trong if block buffer is full

								} else {// khi co tieng noi o frame lien truoc
									// thi E(l) lay bang E(l-1): nang luong tb
									// cua nhieu o frame
									// lien truoc
									// tinh E
									vector_E_PowerOfBaseNoise_EachFrame[colum] = vector_E_PowerOfBaseNoise_EachFrame[colum - 1];

									// tinh NoiseMean(k,l): cap nhat nhieu moi
									for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
										matrixNoiseMean[rowIndex][colum] = alpha
												* matrixNoiseMean[rowIndex][colum - 1]
												+ (1 - alpha)
												* matrixNMK[rowIndex][colum];

									}

								}
								// tinh threshold[l]
								vectorRowThreshold[colum] = a
										* vector_E_PowerOfBaseNoise_EachFrame[colum]
										+ b + 3;// cong them de cat tot hon

								// tinh ltsd(k,l)
								double tempForLTSD_1 = 0;
								for (int k = 0; k < numRows; k++) {
									tempForLTSD_1 += Math.pow(
											matrixLTSE[k][colum], 2)
											/ (Math.pow(
													matrixNoiseMean[k][colum],
													2) * nfft);
								}
								// dang o trong if block buffer is full
								vectorRowLTSD[colum] = 10 * Math
										.log10(tempForLTSD_1);
							}

							// Luu cac truong gia tri ( 6 truong ) o frame cuoi
							// de lam co so cho frame dau tien o lan 32000 mau
							// tiep theo

							e_PowerOfNoise_OfLastFrameOfEachTime_WhenBuffferIsFull = vector_E_PowerOfBaseNoise_EachFrame[numColums - 1];

							for (int row = 0; row < numRows; row++) {
								noiseMean_VectorOfLastFrameOfEachTime_WhenBufferIsFull[row] = matrixNoiseMean[row][numColums - 1];
							}

							threshold_OfLastFrameOfEachTime_WhenBuffferIsFull = vectorRowThreshold[numColums - 1];

							ltsd_OfLastFrameOfEachTime_WhenBuffferIsFull = vectorRowLTSD[numColums - 1];

							for (int row = 0; row < numRows; row++) {
								mag_VectorOfLastFrameOfEachTime_WhenBufferIsFull[row] = matrixMAG[row][numColums - 1];
							}

							// for (int row = 0; row < numRows; row++) {
							// nmk_VectorOfLastFrameOfEachTime_WhenBufferIsFull[row]
							// = matrixNMK[row][numColums - 1];
							// }
							// dang o trong if block buffer is full

						}

						double[] vectorRowThreshold1 = new double[vectorRowThreshold.length];
						for (int i = 0; i < vectorRowThreshold.length; i++) {
							// cong them vao threshold de nang cao len de cat
							// cho hop li
							vectorRowThreshold1[i] = vectorRowThreshold[i] + 4;// 6
																				// cho
																				// ket
																				// qua
																				// kha
																				// tot,
																				// 8
																				// tot
																				// hon
						}
						// ko can hieu chinh vector marker

						double[] vectorRowMarkerAfter = vadForVector
								.computeRowVectorMarker(vectorRowLTSD,
										vectorRowThreshold1, 2);

						// double[] vectorRowMarkerAfter = vadForVector
						// .computeRowVectorMarkerAfterAdjustment(
						// vectorRowMarker, 400, 384);
						// // tinh vecto ra sau khi vadonline
						// int[] vectorSamplesAfterVADOnline = vadForVector
						// .computeVectorSamplesInputAfterVAD(
						// vectorRowMarkerAfter, numsSamplesShift);
						// // dua vao hang doi
						// for (int i = 0; i <
						// vectorSamplesAfterVADOnline.length; i++) {
						// sampleQueue.offer(vectorSamplesAfterVADOnline[i]);
						// }

						for (int i = 0; i < vectorRowMarkerAfter.length; i++) {
							queueForMarker.offer(vectorRowMarkerAfter[i]);
						}

						boolean isStopRecord_2 = false;

						if (queueForMarker.size() >= vectorRowMarkerAfter.length * 2) {
							double[] vectorMarkerFor_2_ConsescutiveTimes = new double[vectorRowMarkerAfter.length * 2];
							// Lam thu chuyen tu hang doi sang mang de dua vao
							// method maker de phat hien de ngat thu am

							for (int i1 = 0; i1 < vectorMarkerFor_2_ConsescutiveTimes.length; i1++) {
								vectorMarkerFor_2_ConsescutiveTimes[i1] = (Double) queueForMarker
										.poll();
							}
							// dang o trong if block buffer is full
							// sau do lai dua tra lai vectomarkerafter hien tai
							// vao hang doi
							// de tiep tuc doi them vectormarkerafter cho lan
							// sau de tinh toan
							for (int i1 = 0; i1 < vectorRowMarkerAfter.length; i1++) {
								queueForMarker.offer(vectorRowMarkerAfter[i1]);
							}

							isStopRecord_2 = vadForVector.markerForStopRecord(
									vectorMarkerFor_2_ConsescutiveTimes, 400,
									384);
						}

						// boolean isStopRecord_1 = vadForVector
						// .markerForStopRecord(vectorRowMarkerAfter, 400,
						// 384);
						// boolean isStopRecord_2 =
						// vadForVector.markerForStopRecord(vectorMarkerFor_2_ConsescutiveTimes,
						// 400, 384);
						ganKetThuc = System.currentTimeMillis();
						// dung bien nay de han che trong truong hop ng noi chua
						// kip noi trong vong 1.5s thi da bi tat
						long timesRecordBeforePause = (ganKetThuc - batdau); // in
																				// seconds
						//
						if ((((isStopRecord_2 == true)) && (timesRecordBeforePause >= 850))
								|| (timesRecordBeforePause >= 10000)) {
							isStopRecord = true;
						}

						// System.out.println("OK");
						countTimesWhenBufferIsFull++;

						if (isStopRecord == true) {
							buffer.clear();
							queueForMarker.clear();
							//
							recorder.stop();
							recorder.release();

							recorder = null;
							msghdr.sendEmptyMessage(124);
							//
							ketthuc = System.currentTimeMillis();

							// System.out.println("Thoi gian thu am: " +
							// (ketthuc - batdau)+ " ms");
							thread1 = null;
							thread2 = null;

							buffer.clear();
							queueForMarker.clear();

							//

						}
						// dang o trong if block buffer is full
					}

				}

			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	class SendSamplesToAndReceiveFromServer_Task4 implements Runnable {

		// private Socket socket = null;
		long beginSend = 0;
		long finishReceive = 0;

		public void run() {
			try {
				socket = new Socket(IPServer, Server_Port);

				// open socket with time out
				// socket = new Socket();
				// socket.connect(new InetSocketAddress(IPServer, Server_Port),
				// 5000);

				beginSend = System.currentTimeMillis();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));

				ObjectOutputStream oos = new ObjectOutputStream(
						socket.getOutputStream());
				// int[] intVector = new int[sameplesQueue.size() - (int)(1 *
				// 16000)];
				short[] shortVector = new short[sameplesQueue.size()];
				// bo bot di 1s cuoi cung la khoang lang de tiet kiem th gian
				// truyen
				// chi nen bot di 1s hoac it hon (vi da thu voi nhieu cau noi
				// khac nhau )
				int temp = 0;
				for (int i = 0; i < shortVector.length; i++) {
					temp = (Integer) sameplesQueue.poll();
					shortVector[i] = (short) temp;
				}
				sameplesQueue.clear();
				// gui sang server
				oos.writeObject(shortVector);
				oos.flush();

				// nhan ket qua nhan dang

				String result1 = br.readLine().toString();
				result = adjustmentString(result1);

				if (result.equals("")) {
					result = "no thing";
				}

				finishReceive = System.currentTimeMillis();
				timesWaitToSend_Recognize_Receive_FromServer = finishReceive
						- beginSend;

				msghdr.sendEmptyMessage(125);
				// oos.close();
				// br.close();
				socket.close();

			} catch (Exception e) {
				msghdr.sendEmptyMessage(STATUS_NETWORK_ERROR);
				e.printStackTrace();
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

			// con thieu phan chu in hoa cua phan tone

			return outputString;
		}
	}

//	public void CustomPreference() {
//		// preferences
//		preference = PreferenceManager.getDefaultSharedPreferences(this);
//		preferencesListener = new OnSharedPreferenceChangeListener() {
//			// @Override
//			public void onSharedPreferenceChanged(
//					SharedPreferences sharedPreferences, String key) {
//				Log.i("Refereces", "changed");
//				setParam();
//			}
//		};
//		preference
//				.registerOnSharedPreferenceChangeListener(preferencesListener);
//
//		setParam();
//	}
//
//	public void setParam() {

//		String u = preference.getString("Client_address", "0");
//		// String PortServer = preference.getString("Server_Port", "1569");
//		// Server_Port =Integer.parseInt(PortServer);
//		if (u.contains("0")) {
//			IPServer = "172.16.78.31";
//			Server_Port = 8021;
//		} else {
//			IPServer = "mica.edu.vn";
//			Server_Port = 1569;
//		}
//		// Log.i ("chosename",u);
//		// Log.i ("port: ",String.valueOf(Server_Port));
//		// Log.i("Server : ", IPServer);
//	}

}
