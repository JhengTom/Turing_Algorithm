package com.example.algorithm._09_;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Metting implements Comparable<Metting> {
//	https://blog.csdn.net/weixin_45670060/article/details/119656251
//	https://blog.csdn.net/qq877728715/article/details/102738886
	int meNum; // 編號
	int startTime; // 開始時間
	int endTime; // 結束時間

	public Metting(int meNum, int startTime, int endTime) {
		super();
		this.meNum = meNum;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public int compareTo(Metting o) {
		if (this.endTime > o.endTime)
			return 1;
		return -1;
	}

	@Override
	public String toString() {
		return "Metting [meNum=" + meNum + ", startTime=" + startTime
				+ ", endTime=" + endTime + "]";
	}

}

public class MettingTest {
	public static void main(String[] args) {
		Scanner cin = new Scanner(System.in);
		List<Metting> mettings = new ArrayList<Metting>();
		int n = cin.nextInt();	//n個會議
		for (int i = 0 ;i < n; i++){
			int start = cin.nextInt();
			int end = cin.nextInt();
			Metting metting = new Metting(i+1, start, end);
			mettings.add(metting);
		}
		System.out.println(mettings.toString());
		mettings.sort(null);
		System.out.println(mettings.toString());
		int curTime = 0;		//當前的時間,從一天的0點開始,如果領導要求從8點開始 那curTime=8
		for(int i = 0 ; i < n; i ++){
			Metting metting = mettings.get(i);
			if(metting.startTime >= curTime){		//會議的開始時間比我們當前的要大 表示可以開
				System.out.println(metting.toString());
				curTime = metting.endTime;
			}
		}
	}
}
//		4
//		1 4
//		4 6
//		2 6
//		1 2
//		[Metting [meNum=1, startTime=1, endTime=4], Metting [meNum=2, startTime=4, endTime=6],
//			Metting [meNum=3, startTime=2, endTime=6], Metting [meNum=4, startTime=1, endTime=2]]
//		[Metting [meNum=4, startTime=1, endTime=2], Metting [meNum=1, startTime=1, endTime=4],
//			Metting [meNum=3, startTime=2, endTime=6], Metting [meNum=2, startTime=4, endTime=6]]
//		Metting [meNum=4, startTime=1, endTime=2]
//		Metting [meNum=3, startTime=2, endTime=6]
//
//
//		[Metting [meNum=1, startTime=1, endTime=4], Metting [meNum=2, startTime=4, endTime=6],
//			Metting [meNum=3, startTime=2, endTime=6], Metting [meNum=4, startTime=1, endTime=2]]
//		[Metting [meNum=1, startTime=1, endTime=4], Metting [meNum=2, startTime=4, endTime=6],
//			Metting [meNum=3, startTime=2, endTime=6], Metting [meNum=4, startTime=1, endTime=2]]
//		Metting [meNum=1, startTime=1, endTime=4]
//		Metting [meNum=2, startTime=4, endTime=6]
