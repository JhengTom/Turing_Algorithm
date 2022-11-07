package com.example.algorithm._19_BloomFilter;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public class BloomFilterTest {
    public static void main(String[] args) {
        int datasize = 100000000;    //我們要插入的資料也就是n
        double fpp = 0.001;        //0.1%	誤判率

        BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), datasize, fpp);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            bloomFilter.put(i);
        }
        System.out.println((System.currentTimeMillis() - start) + ":ms");


        //怎麼測試這個誤判率
        int t = 0;
        for (int i = 20000000; i < 30000000; i++) {
            if (bloomFilter.mightContain(i)) { //表示存在
                t++;
            }
        }

        System.out.println("誤判的個數:" + t);

    }
}
