package com.example.algorithm._09_;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;

public class MegrSort {

	private static double temp [];
	public static void main(String[] args) throws Exception {

		String str = null;
		String fileName = "/Users/tomjheng/Library/CloudStorage/OneDrive-個人/_圖靈教育/【數據結構與演演算法-趙雲】/09/貪心演算法&動態規劃/200w.txt";
		InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		double data[] = new double[2100002];
		int i = 0;
		while ((str = br.readLine()) != null) {
			data[i++] = Double.valueOf(str);
		}
		System.out.println("數據讀取完畢，size為" + i);
		long start = System.currentTimeMillis();
		// Arrays.sort(data);
		temp = new double[data.length];
		megerSort(data, 0, data.length - 1);
		System.out.println("歸併消耗的時間為:" + (System.currentTimeMillis() - start) + "ms");
		File file = new File("E:\\workspace\\sort\\200w-msort.txt");
		Writer out = new FileWriter(file);
		for (i = 0; i < data.length; i++) {
			out.write(String.valueOf(data[i]) + "\r\n");
		}
		out.close();

		// System.out.println(Arrays.toString(data));
		// JDK裡面的排序源碼

	}

	public static void megerSort(double data[], int left, int right) { // 陣列的兩端
		if (left < right) {
			//System.out.println(left + ":" + right);
			int mid = (left + right) / 2;
			megerSort(data, left, mid);
			megerSort(data, mid + 1, right);
			meger(data, left, mid, right);
		}
	}

	public static void meger(double data[], int left, int mid, int right) {
		//System.out.println(left + ":" + right);
		//double temp[] = new double[data.length]; // 借助一個臨時陣列用來保存合併的資料

		int point1 = left; // 表示的是左邊的第一個數的位置
		int point2 = mid + 1; // 表示的是右邊的第一個數的位置

		int loc = left; // 表示的是我們當前已經到了哪個位置了
		while (point1 <= mid && point2 <= right) {
			if (data[point1] < data[point2]) {
				temp[loc++] = data[point1++];
			} else {
				temp[loc++] = data[point2++];
			}
		}
		while (point1 <= mid) {
			temp[loc++] = data[point1++];
		}
		while (point2 <= right) {
			temp[loc++] = data[point2++];
		}
		for (int i = left; i <= right; i++) {
			data[i] = temp[i];
		}
	}
}
