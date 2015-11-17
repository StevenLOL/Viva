package com.mica.viva.inputting;
import java.io.File;
import java.util.ArrayList;

public class VADForVectorInputOnline {
	// 4 hang so: co the phu thuoc vao micro cua thiet bi thu
		// 4 hang so nay duoc xac dinh truoc doc lap voi chuong trinh
		// co the dung matlap hoac dung 1 ctr thu am tren java
		// E0 la nang luong cua nhieu nen o moi truong it nhieu nhat
		// tinh E0 nhu sau:
		// thu am de co 1 file am thanh ( ko co tieng noi ) o moi truong it nhieu
		// nhat
		// lay ra 1 so mau de tinh E0 = lay tong binh phuong va chia trung binh
		// 4 gia tri da test: k q kha tot:
		// tieng noi lien tuc thi giu lai, doi khi bi mat 1 it
		// ko co thi bo di gia nua
		// luc co, luc ko thi loc ra tieng noi duoc kha tot
		// E0 = 10 * Math.log10(1);
		// E1 = 10 * Math.log10(Math.pow(32768, 2));
		// THRESHOLD_0 = 30;
		// THRESHOLD_1 = 20;
		static  double E0 = 6.02;//day la gia tri do duoc khi thu am trong moi truong im lang
		// E1 tuong tu nhu E0 nhung o moi truong nhieu nhieu nhat
		static  double  E1 = 85; // gia tri thu duoc trong moi truong tieng on may bay 
		// threshold_0 la nguong cat tuong ung voi moi truong it nhieu nhat
		// co the tinh bang cach tu thu am ( co ca tieng noi ) trong moi tuong do
		// roi tinh ltsd hoac dung matlab de uoc tinh gia tri do
		static final double THRESHOLD_0 = 25; // do duoc trong moi truong yen lang
		// threshold_1 tuong tu nhu threshold_0 nhung o moi truong nhieu nhieu nhat
		static final double THRESHOLD_1 = 10;// do duoc trong moi truong may bay
		// an input file .wav
		// private WavFile wavFile;
		// vec to dau vao: gia tri tat ca cac mau cua file wav
		private int[] vectorSamplesInput;// bien phu thuoc
		// arraylist de luu tru vec to dau vao cung nhung mau dem them
		// private ArrayList arrayListForPadding = new ArrayList();
		// chieu dai 1 khung( so mau cua 1 khung )
		private int numSamplesPerFrame;
		// khoang cach 2 khung lien tiep ( so mau ma 2 khung lien tiep cach nhau)
		private int numSamplesShift;
		// so luong mau khi chua dem(chinh la do dai cua vecto input khi chua dem)
		// private int numTotalSamples;// bien phu thuoc
		// tong so khung sau khi phan khung
		// private static int numFrames;
		// so diem bien doi fft cho 1 frame
		// private int nfftFor_1_Frame;
		// so khung su dung
		private int numUsedFrames;
		// so khung lien ke su dung de tinh ma tran NMK(k,l)
		private int numNearFrames_K;

		public VADForVectorInputOnline(int[] vectorSamplesInput,
				int numSamplesPerFrame, int numSamplesShift) {
			this.vectorSamplesInput = vectorSamplesInput;
			this.numSamplesPerFrame = numSamplesPerFrame;
			this.numSamplesShift = numSamplesShift;
			
		}
		// 1 so phuong thuc get
		public int getNumSamplesPerFrame() {
			return numSamplesPerFrame;
		}

		public int getNumSamplesShift() {
			return numSamplesShift;
		}

		// 1.1
		public int[] getVectorSamplesInput() {
			return vectorSamplesInput;
		}

		// 2 da xong

		// 3 da xong
		public int[][] computeFrames_Matrix(int numSamplesPerFrame,
				int numSamplesShift, int[] vectorSamplesInput) {
			int numFrames = computeNumberOfFrames(numSamplesPerFrame,
					numSamplesShift, vectorSamplesInput);
			int[] vectorInputAfterPadding = computeVectorAfterPaddingIfNeccesary(
					numSamplesPerFrame, numSamplesShift, vectorSamplesInput);

			int[][] framesMatrix = new int[numSamplesPerFrame][numFrames];
			// computeNF_PaddInputVector(numSamplesPerFrame, numSamplesShift,
			// vectorSamplesInput);
			for (int k = 0; k < numSamplesPerFrame; k++)
				for (int l = 0; l < numFrames; l++) {
					framesMatrix[k][l] = vectorInputAfterPadding[l
							* numSamplesShift + k];
				}
			// code ?
			return framesMatrix;

		}

		// 3.1 da xong
		public int computeNumberOfFrames(int numSamplesPerFrame,
				int numSamplesShift, int[] vectorSamplesInput) {
			int l = vectorSamplesInput.length;
			int numberOfFrames = (int) Math.floor((l - numSamplesPerFrame)
					/ numSamplesShift + 1);
			int e = l - (numberOfFrames - 1) * numSamplesShift - numSamplesPerFrame;
			if (e > 0)
				numberOfFrames++;
			return numberOfFrames;
		}

		// 3.2 da xong
		public int[] computeVectorAfterPaddingIfNeccesary(int numSamplesPerFrame,
				int numSamplesShift, int[] vectorSamplesInput) {
			// int[] result;
			int l = vectorSamplesInput.length;
			int numberOfFrames = (int) Math.floor((l - numSamplesPerFrame)
					/ numSamplesShift + 1);
			int e = l
					- ((numberOfFrames - 1) * numSamplesShift + numSamplesPerFrame);
			if (e == 0)
				return vectorSamplesInput;

			else {
				int paddingSamples = numSamplesPerFrame - e;
				int[] result = new int[l + paddingSamples];
				for (int i = 0; i < l; i++)
					result[i] = vectorSamplesInput[i];
				for (int i = l; i < result.length; i++)
					result[i] = 0;
				return result;

			}

		}

		// 4 tinh ma tran mag

		public static double[][] computeMAG(int[][] frames_Matrix) {
			int numRows = frames_Matrix.length;
			int numColums = frames_Matrix[0].length;
			double[][] matrixMAG = new double[numRows][numColums];
			for (int colum = 0; colum < numColums; colum++) {
				double[] real = new double[numRows];

				for (int row = 0; row < numRows; row++) {
					real[row] = frames_Matrix[row][colum];
				}
				double[] resultAfterFFTForColum = fftAfterPadding(real);

				for (int row = 0; row < numRows; row++) {
					matrixMAG[row][colum] = resultAfterFFTForColum[row];
				}
			}

			// System.arraycopy(frames_Matrix[][colum], 0, matrixMAG[][colum], 0,
			// numRows);
			return matrixMAG;
		}

		// 4.1: bien doi fft voi so diem la luy thua cua 2 va su dung luon ham cua
		// so hanning
		public static double[] fftIfNumbersIsPowerOf2(double[] real) {

			int l = real.length;
			int n = l;
			int m = (int) (Math.log(n) / Math.log(2));
			// tao ham cua so: vi du: hanning
			double[] window = new double[l];
			// ham cua so la thuc nen coi nhu phan ao bang 0
			// => no ko lien quan den phan ao cua tin hieu
			for (int index = 0; index < l; index++)
				window[index] = 0.5 * (1 - Math.cos(2 * Math.PI * index / (l - 1)));

			// sau khi nhan voi ham cua so ta duoc phan thuc la x, phan ao la y

			double[] x = new double[l];
			double[] y = new double[l];

			for (int index = 0; index < l; index++) {
				x[index] = real[index] * window[index];
				y[index] = 0;
			}
			// System.arraycopy(image, 0, y, 0, l);

			// tinh fft
			int i, j, k, n1, n2, a;
			double c, s, e, t1, t2;
			double[] cos;
			double[] sin;

			// Make sure n is a power of 2
			if (n != (1 << m))
				throw new RuntimeException("FFT length must be power of 2");

			// precompute tables
			cos = new double[n / 2];
			sin = new double[n / 2];

			for (int index = 0; index < n / 2; index++) {
				cos[index] = Math.cos(-2 * Math.PI * index / n);
				sin[index] = Math.sin(-2 * Math.PI * index / n);
			}

			double[] moduleOfImpulsesAfterFFT = new double[x.length];

			// Bit-reverse
			j = 0;
			n2 = n / 2;
			for (i = 1; i < n - 1; i++) {
				n1 = n2;
				while (j >= n1) {
					j = j - n1;
					n1 = n1 / 2;
				}
				j = j + n1;

				if (i < j) {
					t1 = x[i];
					x[i] = x[j];
					x[j] = t1;
					t1 = y[i];
					y[i] = y[j];
					y[j] = t1;
				}
			}

			// 00134 // FFT
			n1 = 0;
			n2 = 1;

			for (i = 0; i < m; i++) {
				n1 = n2;
				n2 = n2 + n2;
				a = 0;

				for (j = 0; j < n1; j++) {
					c = cos[a];
					s = sin[a];
					a += 1 << (m - i - 1);

					for (k = j; k < n; k = k + n2) {
						t1 = c * x[k + n1] - s * y[k + n1];
						t2 = s * x[k + n1] + c * y[k + n1];
						x[k + n1] = x[k] - t1;
						y[k + n1] = y[k] - t2;
						x[k] = x[k] + t1;
						y[k] = y[k] + t2;
					}
				}
			}
			for (int index = 0; index < l; index++) {
				moduleOfImpulsesAfterFFT[index] = (int) ((Math.sqrt(x[index]
						* x[index] + y[index] * y[index]) * 1000)) / 1000.0000;
			}
			return moduleOfImpulsesAfterFFT;
		}

		// 4.2 fft voi so diem dau vao co so diem bat ki , co the ko la luy thua cua
		// 2
		// neu n = chieu dai cua mang real ko la luy thua cua 2 thi
		// khi co phai dem them cac gia tri 0
		// va tang n len gia tri gan nhat la luy thua cua 2 de ap dung 4.1

		public static double[] fftAfterPadding(double[] real) {
			// int leng = real.length;
			int length = real.length;
			int m = (int) (Math.log(length) / Math.log(2));
			if (length != (1 << m))
				m++;// neu length ko la luy thua bac m cua 2 thi tang m len 1
			int n = (int) Math.pow(2, m);
			double[] realAfterPadding = new double[n];
			int i = 0;
			for (i = 0; i < length; i++) {
				realAfterPadding[i] = real[i];
			}
			// dem 0 vao nhung cho thieu
			for (i = length; i < n; i++) {
				realAfterPadding[i] = 0;
			}
			double[] result = fftIfNumbersIsPowerOf2(realAfterPadding);
			// lay ra cac gia tri tuong ung voi mang dau vao ban dau
			double[] resultEnd = new double[length];
			for (i = 0; i < length; i++) {
				resultEnd[i] = result[i];
			}
			return resultEnd;
		}

		// 5
		public double[][] computeMatrixLTSE(double[][] matrix_MAG, int numUsedFrames) {
			// da test :ok
			int numRows = matrix_MAG.length;
			int numColums = matrix_MAG[0].length;
			double[][] matrixLTSE = new double[numRows][numColums];

			for (int row = 0; row < numRows; row++)
				for (int colum = 0; colum < numColums; colum++) {
					double maxOfRow = matrix_MAG[row][colum];
					for (int j = -numUsedFrames; j <= numUsedFrames; j++) {
						if ((colum + j >= 0) && (colum + j < numColums)) {
							if (maxOfRow < matrix_MAG[row][colum + j])
								maxOfRow = matrix_MAG[row][colum + j];
						}
					}
					matrixLTSE[row][colum] = maxOfRow;
				}

			return matrixLTSE;

		}

		// 6
		public double[] computeRowVectorLTSD(double[][] matrix_LTSE,
				int num_InitialFrames_NonDetected, double alpha) {
			// numInitialframesnondectected: so frames ban dau ma chuong trinh chua
			// ap dung thuat toan
			// mac dinh nhung frames do la tieng noi
			// nhung frames do la co so de tinh E(l),N(k,l)
			// tu tinh lay nfft = so hang cua matrix_LTSE
			int nfft = matrix_LTSE.length;
			int numRows = nfft;
			int numColums = matrix_LTSE[0].length;

			double[] vectorRowLTSD = new double[numColums];
			double[] vectorRowThreshold = new double[numColums];
			double[] vector_E_PowerOfBaseNoise_EachFrame = new double[numColums];
			double[][] matrixNoiseMean = new double[numRows][numColums];

			int[] vectorSamplesInput = getVectorSamplesInput();
			double[][] matrixMAG = computeMAG(computeFrames_Matrix(numRows,
					getNumSamplesShift(), vectorSamplesInput));
			// su dung 6 frames lien ke de tinh cho NMK
			double[][] matrixNMK = computeMatrix_NMK(numColums, matrixMAG);

			// vi du neu 5 frames ban dau ko duoc ap dung thuat toan phat hien
			// ( num_InitialFrames_NonDetected = 5 )
			// ta se lay 1000( 1s thu am ban dau) mau cua vecto input de tinh E(0)
			// den E(5) va N(k,0) den N(k,5)
			// ko xet den ltsd(0) den ltsd(4) coi nhu mac dinh = 0
			// ko xet den threshold(0) den (4) coi nhu mac dinh = 0
			// ko xet den E(0) den E(4) coi nhu mac dinh = 0
			// ko xet den N(k,0) den N(k,4) coi nhu mac dinh = 0
			// bat dau xet tu 5 tro di tuc la tu chi so
			// num_InitialFrames_NonDetected tro di

			// tinh cac truong gia tri tai chi so num_InitialFrames_NonDetected
			// tinh tb nang luong nhieu
			double numInitialSamplesForCompute_Initial_E_And_Initial_NoiseMean = 500.0;
			double tempForInitial_E = 0;
			for (int i = 0; i < numInitialSamplesForCompute_Initial_E_And_Initial_NoiseMean; i++) {
				tempForInitial_E += (Math.pow(vectorSamplesInput[i], 2))
						/ numInitialSamplesForCompute_Initial_E_And_Initial_NoiseMean;
			}
			vector_E_PowerOfBaseNoise_EachFrame[num_InitialFrames_NonDetected] = 10 * Math
					.log10(tempForInitial_E);
			// tinh tb pho cua khung
			for (int i = 0; i < numInitialSamplesForCompute_Initial_E_And_Initial_NoiseMean; i++) {
				matrixNoiseMean[0][num_InitialFrames_NonDetected] += (Math
						.abs(vectorSamplesInput[i]) / numInitialSamplesForCompute_Initial_E_And_Initial_NoiseMean);
			}
			for (int row = 1; row < numRows; row++) {
				matrixNoiseMean[row][num_InitialFrames_NonDetected] = matrixNoiseMean[0][num_InitialFrames_NonDetected];
			}

			// tinh 2 hang so trong cong thuc tinh threshold
			double a = (THRESHOLD_0 - THRESHOLD_1) / (E0 - E1);
			double b = THRESHOLD_0 - E0 * a;
			// => threshold(l) = a * E(l) + b

			// tinh threshold tai chi so num_InitialFrames_NonDetected
			vectorRowThreshold[num_InitialFrames_NonDetected] = a
					* vector_E_PowerOfBaseNoise_EachFrame[num_InitialFrames_NonDetected]
					+ b;

			// tinh ltsd tai chi so num_InitialFrames_NonDetected
			// nfft = numRows: so samples tren 1 frame
			double temp = 0;
			for (int k = 0; k < numRows; k++) {
				temp += Math.pow(matrix_LTSE[k][num_InitialFrames_NonDetected], 2)
						/ (Math.pow(
								matrixNoiseMean[k][num_InitialFrames_NonDetected],
								2) * nfft);
			}
			vectorRowLTSD[num_InitialFrames_NonDetected] = 10 * Math.log10(temp);

			// ke tu chi so num_InitialFrames_NonDetected tro di
			// phan tu sau tinh theo phan tu truoc
			for (int colum = num_InitialFrames_NonDetected + 1; colum < numColums; colum++) {
				if (vectorRowLTSD[colum - 1] < vectorRowThreshold[colum - 1]) {

					// ko co tieng noi thi
					// cap nhat E(l): gia tri moi lay bang tb nang luong cua frame
					// lien truoc no
					// double[] tempColumVector = new double[numColums];
					double tempForE = 0;
					for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
						tempForE += Math.pow(matrixMAG[rowIndex][colum - 1], 2)
								/ numRows;
					}
					vector_E_PowerOfBaseNoise_EachFrame[colum] = 10 * Math
							.log10(tempForE);

					// tinh NoiseMean(k,l)
					for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
						matrixNoiseMean[rowIndex][colum] = matrixNoiseMean[rowIndex][colum - 1];

					}

				} else {// khi co tieng noi o frame lien truoc
					// thi E(l) lay bang E(l-1): nang luong tb cua nhieu o frame
					// lien truoc
					// tinh E
					vector_E_PowerOfBaseNoise_EachFrame[colum] = vector_E_PowerOfBaseNoise_EachFrame[colum - 1];

					// tinh NoiseMean(k,l): cap nhat nhieu moi
					for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
						matrixNoiseMean[rowIndex][colum] = alpha
								* matrixNoiseMean[rowIndex][colum - 1]
								+ (1 - alpha) * matrixNMK[rowIndex][colum];

					}

				}
				// tinh threshold[l]
				vectorRowThreshold[colum] = a
						* vector_E_PowerOfBaseNoise_EachFrame[colum] + b;

				// tinh ltsd(k,l)
				double tempForLTSD = 0;
				for (int k = 0; k < numRows; k++) {
					tempForLTSD += Math.pow(matrix_LTSE[k][colum], 2)
							/ (Math.pow(matrixNoiseMean[k][colum], 2) * nfft);
				}
				vectorRowLTSD[colum] = 10 * Math.log10(tempForLTSD);
			}

			return vectorRowLTSD;
		}

		// 6. 1
		// tinh thu threshold :lay tu method tinh ltsd va sua di doan return
		public double[] computeRowVectorThreshold(double[][] matrix_LTSE,
				int num_InitialFrames_NonDetected, double alpha) {
			// numInitialframesnondectected: so frames ban dau ma chuong trinh chua
			// ap dung thuat toan
			// mac dinh nhung frames do la tieng noi
			// nhung frames do la co so de tinh E(l),N(k,l)
			// tu tinh lay nfft = so hang cua matrix_LTSE
			int nfft = matrix_LTSE.length;
			int numRows = nfft;
			int numColums = matrix_LTSE[0].length;

			double[] vectorRowLTSD = new double[numColums];
			double[] vectorRowThreshold = new double[numColums];
			double[] vector_E_PowerOfBaseNoise_EachFrame = new double[numColums];
			double[][] matrixNoiseMean = new double[numRows][numColums];

			int[] vectorSamplesInput = getVectorSamplesInput();
			double[][] matrixMAG = computeMAG(computeFrames_Matrix(numRows,
					getNumSamplesShift(), vectorSamplesInput));
			// su dung 6 frames lien ke de tinh cho NMK
			double[][] matrixNMK = computeMatrix_NMK(numColums, matrixMAG);

			// vi du neu 5 frames ban dau ko duoc ap dung thuat toan phat hien
			// ( num_InitialFrames_NonDetected = 5 )
			// ta se lay 16000( 1s thu am ban dau) mau cua vecto input de tinh E(0)
			// den E(5) va N(k,0) den N(k,5)
			// ko xet den ltsd(0) den ltsd(4) coi nhu mac dinh = 0
			// ko xet den threshold(0) den (4) coi nhu mac dinh = 0
			// ko xet den E(0) den E(4) coi nhu mac dinh = 0
			// ko xet den N(k,0) den N(k,4) coi nhu mac dinh = 0
			// bat dau xet tu 5 tro di tuc la tu chi so
			// num_InitialFrames_NonDetected tro di

			// tinh cac truong gia tri tai chi so num_InitialFrames_NonDetected
			// tinh tb nang luong nhieu
			double numInitialSamplesForCompute_Initial_E_And_Initial_NoiseMean = 500.0;
			double tempForInitial_E = 0;
			for (int i = 0; i < numInitialSamplesForCompute_Initial_E_And_Initial_NoiseMean; i++) {
				tempForInitial_E += (Math.pow(vectorSamplesInput[i], 2))
						/ numInitialSamplesForCompute_Initial_E_And_Initial_NoiseMean;
			}
			vector_E_PowerOfBaseNoise_EachFrame[num_InitialFrames_NonDetected] = 10 * Math
					.log10(tempForInitial_E);
			// tinh tb pho cua khung
			for (int i = 0; i < numInitialSamplesForCompute_Initial_E_And_Initial_NoiseMean; i++) {
				matrixNoiseMean[0][num_InitialFrames_NonDetected] += (Math
						.abs(vectorSamplesInput[i]) / numInitialSamplesForCompute_Initial_E_And_Initial_NoiseMean);
			}
			for (int row = 1; row < numRows; row++) {
				matrixNoiseMean[row][num_InitialFrames_NonDetected] = matrixNoiseMean[0][num_InitialFrames_NonDetected];
			}

			// tinh 2 hang so trong cong thuc tinh threshold
			double a = (THRESHOLD_0 - THRESHOLD_1) / (E0 - E1);
			double b = THRESHOLD_0 - E0 * a;
			// => threshold(l) = a * E(l) + b

			// tinh threshold tai chi so num_InitialFrames_NonDetected
			vectorRowThreshold[num_InitialFrames_NonDetected] = a
					* vector_E_PowerOfBaseNoise_EachFrame[num_InitialFrames_NonDetected]
					+ b;

			// tinh ltsd tai chi so num_InitialFrames_NonDetected
			double temp = 0;
			for (int k = 0; k < numRows; k++) {
				temp += Math.pow(matrix_LTSE[k][num_InitialFrames_NonDetected], 2)
						/ (Math.pow(
								matrixNoiseMean[k][num_InitialFrames_NonDetected],
								2) * nfft);
			}
			vectorRowLTSD[num_InitialFrames_NonDetected] = 10 * Math.log10(temp);

			// ke tu chi so num_InitialFrames_NonDetected tro di
			// phan tu sau tinh theo phan tu truoc
			for (int colum = num_InitialFrames_NonDetected + 1; colum < numColums; colum++) {
				if (vectorRowLTSD[colum - 1] < vectorRowThreshold[colum - 1]) {

					// ko co tieng noi thi
					// cap nhat E(l): gia tri moi lay bang tb nang luong cua frame
					// lien truoc no
					// double[] tempColumVector = new double[numColums];
					double tempForE = 0;
					for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
						tempForE += Math.pow(matrixMAG[rowIndex][colum - 1], 2)
								/ numRows;
					}
					vector_E_PowerOfBaseNoise_EachFrame[colum] = 10 * Math
							.log10(tempForE);

					// tinh NoiseMean(k,l)
					for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
						matrixNoiseMean[rowIndex][colum] = matrixNoiseMean[rowIndex][colum - 1];

					}

				} else {// khi co tieng noi o frame lien truoc
					// thi E(l) lay bang E(l-1): nang luong tb cua nhieu o frame
					// lien truoc
					// tinh E
					vector_E_PowerOfBaseNoise_EachFrame[colum] = vector_E_PowerOfBaseNoise_EachFrame[colum - 1];

					// tinh NoiseMean(k,l): cap nhat nhieu moi
					for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
						matrixNoiseMean[rowIndex][colum] = alpha
								* matrixNoiseMean[rowIndex][colum - 1]
								+ (1 - alpha) * matrixNMK[rowIndex][colum];

					}

				}
				// tinh threshold[l]
				vectorRowThreshold[colum] = a
						* vector_E_PowerOfBaseNoise_EachFrame[colum] + b;

				// tinh ltsd(k,l)
				double tempForLTSD = 0;
				for (int k = 0; k < numRows; k++) {
					tempForLTSD += Math.pow(matrix_LTSE[k][colum], 2)
							/ (Math.pow(matrixNoiseMean[k][colum], 2) * nfft);
				}
				vectorRowLTSD[colum] = 10 * Math.log10(tempForLTSD);
			}

			return vectorRowThreshold;
		}

		// 6.1
		// tinh thu vectormarker: 1 la co tieng noi, 0 la ko co
		public double[] computeRowVectorMarker(double[] vectorRowLTSD,
				double[] vectorRowThreshold, int num_InitialFrames_NonDetected) {
			int numColums = vectorRowLTSD.length;
			double[] vectorRowMarker = new double[numColums];

			// tinh vectorMarker
			for (int i = 0; i < num_InitialFrames_NonDetected; i++) {
				vectorRowMarker[i] = 0;
				// khi chua ap dung thuat toan ta cho marker = 1 nhung  de tien cho viec
				// viet ctr online cho nhom viva sau nay ( vad online )thi nen de la 0
			}

			for (int i = num_InitialFrames_NonDetected; i < numColums; i++) {
				if (vectorRowLTSD[i] >= vectorRowThreshold[i])
					// co tieng noi
					vectorRowMarker[i] = 1;
				else
					// ko co tieng noi
					vectorRowMarker[i] = 0;
			}

			return vectorRowMarker;
		}

		// 6.1.1
		// hieu chinh vector marker de phu hop voi thuc te
		// neu sau 1s ma ko co tieng noi thi moi xac nhan do la ko tieng noi
		public double[] computeRowVectorMarkerAfterAdjustment(
				double[] vectorRowMarker, int numSamplesPerFrame,
				int numSamplesShift) {
			int numColums = vectorRowMarker.length;
			double[] vectorRowMarkerAfterAdjustment = new double[numColums];

			double sampleRate = 16000.0; // mac dinh la 16000
			double duration_MIN_nondetected = 0.1; // in seconds
			double numSamples = sampleRate * duration_MIN_nondetected;

			// tinh so frames tu 3 gia tri tren
			int numberOfFrames = (int) Math.floor((numSamples - numSamplesPerFrame)
					/ numSamplesShift + 1);

			// o marker chua hieu chinh neu ta phat hien ra 1 phan tu 0 ( co the ko
			// co tieng noi) thi tu phan tu do tro di
			// phai co 1 so luong phan tu 0 lon hon gia tri numberofframes o tren
			// thi ta moi xac nhan do la khoan ko tieng noi
			// con neu trai dieu nay, t van xac nhan la tieng noi va dat lai cac
			// phan tu marker do = 1

			// hieu chinh nhu sau
			// for(int i = 0; i < numColums; i++){
			// if(vectorRowMarker[i] == 0){
			//
			// }
			// }
			int i = 0;
			while (i < numColums) {
				if (vectorRowMarker[i] == 1) {
					vectorRowMarkerAfterAdjustment[i] = vectorRowMarker[i];
					i++;
				} else {
					int j = 0;
					for (j = 0; j < numColums - i; j++) {
						if (vectorRowMarker[i + j] == 1) {
							if (j > numberOfFrames) {
								for (int index = 1; index < j; index++) {
									vectorRowMarkerAfterAdjustment[i + index] = 0;
								}
							} else {
								for (int index = 0; index < j; index++) {
									vectorRowMarkerAfterAdjustment[i + index] = 1;
								}
							}

							break;
						}

					}
					i = i + j;
				}
			}

			// xu li doan cuoi cua moi marker cua moi frame:
			// neu truong hop doan cuoi do co nhieu 0 lien tiep thi co the la tieng
			// noi hoac ko tieng noi
			// doan cuoi rat quan trong de sau nay cho ctr nhom viva

			if (vectorRowMarkerAfterAdjustment[numColums - 1 - numberOfFrames] == 1) {
				// tuc la coi tu do den cuoi la tieng noi
				for (int i1 = numColums - numberOfFrames; i1 < numColums; i1++) {
					vectorRowMarkerAfterAdjustment[i1] = 1;
				}
			} else {
				boolean isSpeech = false;
				for (int i1 = numColums - numberOfFrames; i1 < numColums - 1; i1++) {
					if (vectorRowMarkerAfterAdjustment[i1] == 1) {
						// co tieng noi thi set lai bien isspeech va thoat luon vong
						// for
						isSpeech = true;
						break;
					}
					// neu co 1 phan tu o doan cuoi = 1 thi thoat luon vong for va
					// coi ca doan do la tieng noi
				}
				if (isSpeech == true) {
					for (int i1 = numColums - 1 - numberOfFrames; i1 < numberOfFrames; i1++) {
						vectorRowMarkerAfterAdjustment[i1] = 1;
					}
				} else {
					for (int i1 = numColums - numberOfFrames; i1 < numberOfFrames; i1++) {
						vectorRowMarkerAfterAdjustment[i1] = 0;
					}
				}

				// cho cac gia tri
			}

			return vectorRowMarkerAfterAdjustment;
		}

		// 6.1.1
		public double[][] computeMatrix_NMK(int numNearFrames_K,
				double[][] matrix_MAG) {
			int numRows = matrix_MAG.length;
			int numColums = matrix_MAG[0].length;
			double[][] matrix_NMK = new double[numRows][numColums];
			// compute
			for (int row = 0; row < numRows; row++)
				for (int colum = 0; colum < numColums; colum++) {
					double temp = 0;
					for (int j = -numNearFrames_K; j <= numNearFrames_K; j++) {
						if ((colum + j >= 0) && (colum + j < numColums))
							temp += matrix_MAG[row][colum + j];
					}
					matrix_NMK[row][colum] = (int) ((1 / (2.0 * numNearFrames_K + 1) * temp) * 10) / 10.0;
					// nhan len 1000 roi ep kieu int va lai chia cho 1000
					// de han che so chu so thap phan sau dau phay cua ket qua ko
					// qua 4 chu so
				}
			return matrix_NMK;

		}

		// 7 tinh vectosamples sau khi da co vectomarker da hieu chinh
		public int[] computeVectorSamplesInputAfterVAD(
				double[] vectorRowMarkerAfterAdjustment, int numSamplesShift) {
			// chu y do dai 2 vecto nay la khac nhau
			// vectorsamplesinput la vec to ban dau, chua dem
			// vectormarker la vecto danh dau co hay ko co tieng noi
			// do dai cua no la so frames sau khi phan khung
			ArrayList listForStore_SpeechSamples = new ArrayList();
			// vi ko biet ro so luong mau con lai la bn nen tao 1 arraylist voi do
			// dai bat ki
			// de luu nhung mau duoc giu lai

			int[] vectorSamplesInput = getVectorSamplesInput();
			int numSamplesInput = vectorSamplesInput.length;
			int numFrames = vectorRowMarkerAfterAdjustment.length;
			for (int i = 0; i < numFrames; i++) {
				if (vectorRowMarkerAfterAdjustment[i] == 1) {
					for (int j = 0; j < numSamplesShift; j++) {
						if ((i * numSamplesShift + j) < numSamplesInput) {
							listForStore_SpeechSamples.add(vectorSamplesInput[i
									* numSamplesShift + j]);
						}
					}
				}
			}
			// chuyen so mau o arraylist sang 1 mang de tra ve kq dau ra
			int[] vectorSamplesInputAfterVAD = new int[listForStore_SpeechSamples
					.size()];
			for (int i = 0; i < vectorSamplesInputAfterVAD.length; i++) {
				vectorSamplesInputAfterVAD[i] = (Integer) listForStore_SpeechSamples
						.get(i);
			}

			return vectorSamplesInputAfterVAD;
		}

		public boolean markerForStopRecord(double[] vectorRowMarkerAfterAdjustment,
				int numSamplesPerFrame, int numSamplesShift) {

			double sampleRate = 16000.0; // mac dinh la 16000
			double duration_MIN_ForStopRecord = 0.9; // in seconds, voi 1,5s cho kq kha tot ( chi co nhuoc diem la o lan im lang dau tien cua vad online4 thi hoi lau (mat 4s )
			// lay 1.8s de cho viec ngat thu am sau qua 2s tro nen hop li
			double numSamples = sampleRate * duration_MIN_ForStopRecord;

			// tinh so frames tu 3 gia tri tren
			int numberOfFramesMinForStopRecord = (int) Math
					.floor((numSamples - numSamplesPerFrame) / numSamplesShift + 1);

			int numColums = vectorRowMarkerAfterAdjustment.length;

			boolean stopRecord = false;
			int i = 0;
			while (i < numColums - numberOfFramesMinForStopRecord + 1) {
				if (vectorRowMarkerAfterAdjustment[i] == 1)
					i++;
				else {
					double sum = 0.0;
					for (int j = 1; j < numberOfFramesMinForStopRecord; j++) {
						sum += vectorRowMarkerAfterAdjustment[i + j];
					}
					if (sum == 0.0) {
						stopRecord = true;
						i = numColums;
					} else
						i++;
				}
			}

			// System.out.println("Stop: " + stopRecord);
			return stopRecord;
		}
	

}
