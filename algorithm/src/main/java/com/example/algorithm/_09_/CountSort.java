package com.example.algorithm._09_;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class CountSort {
	public static void main(String[] args) throws Exception {
		String str = null;
		String fileName = "/Users/tomjheng/Library/CloudStorage/OneDrive-個人/_圖靈教育/【數據結構與演演算法-趙雲】/09/貪心演算法&動態規劃/200w.txt";
		InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		int data[] = new int[2100002];
		int i = 0;
		while ((str = br.readLine()) != null) {
			double a = Double.valueOf(str);
			a = a * 100;
			data[i++] = (int) a;
			// System.out.println((int) a);
		}
		System.out.println("數據讀取完畢，size為" + i);
		long start = System.currentTimeMillis();
		countSort(data, 0, data.length - 1);
		System.out.println("快排消耗的時間為:" + (System.currentTimeMillis() - start) + "ms");
	}

	public static void countSort(int data[], int min, int max) throws Exception {
		int counts[] = new int[max + 1];
		for (int i = 0; i < data.length; i++) {
			counts[data[i]]++;
		}

		File file = new File("/Users/tomjheng/Library/CloudStorage/OneDrive-個人/_圖靈教育/【數據結構與演演算法-趙雲】/09/貪心演算法&動態規劃/200w-csort.txt");
		Writer out = new FileWriter(file);

		for (int i = 0; i <= max; i++) {
			if (counts[i] > 0) {
				for (int j = 0; j < counts[i]; j++) {
//					out.write(((double) (i / 100.0)) + "\r\n");
				}
			}
		}
		out.close();
	}
}
