package com.example.algorithm._06_rec;

public class Fibonacci {

    private static int data[]; // 初始換全部是0

    // 1 1 2 3 5 8 13
    // f(n)=f(n-1)+f(n-2)
    public static int fab(int n) { // 分析一段代碼好壞，有兩個指標，時間複雜度和空間複雜度 都是：O(2^n)
        if (n <= 2)
            return 1; // 遞迴的終止條件
        return fab(n - 1) + fab(n - 2); // 繼續遞迴的過程
    }

    public static int fac(int n) { // 求N的階乘 用普通遞迴怎麼寫 5=5*4*3*2*1=> f(n) =
        if (n <= 1)
            return 1;
        return n * fac(n - 1);
    }

    public static int noFab(int n) { // 不用遞迴 O(n)
        // 迴圈
        if (n <= 2)
            return 1;
        int a = 1;
        int b = 1;
        int c = 0;
        for (int i = 3; i <= n; i++) {
            /**
             * A B C
             * 1 1 2
             * 1 2 3
             * 2 3 5
             */
            c = a + b;
            a = b;
            b = c;
        }
        return c;
    }

    public static int fab2(int n) { // 用陣列來做緩存 將為了O(n)，空間也降至為O(n)
        if (n <= 2)
            return 1; // 遞迴的終止條件
        if (data[n] > 0) {
            return data[n];
        }
        int res = fab2(n - 1) + fab2(n - 2); // 繼續遞迴的過程
        data[n] = res;
        return res;
    }

    public static int tailfab(int pre, int res, int n) { // 分析一段代碼好壞，有兩個指標，時間複雜度和空間複雜度 都是： O(n)
        System.out.println("pre:"+pre+" , res:"+res+" , n:"+n);
        if (n <= 2)
            return res; // 遞迴的終止條件
        return tailfab(res, pre + res, n - 1);    //JDK源碼
        //參數：
        /**
         * n 是肯定有的
         * res 上一次運算出來結果
         * pre 上上一次運算出來的結果
         */
    }

    public static int tailFac(int n, int res) { // 尾遞迴 傳結果，res就是我們每次保存的結果
        System.out.println(n + ":" + res); // 這個地方列印出來的值是怎麼樣的？
        if (n <= 1)
            return res;
        return tailFac(n - 1, n * res);    //倒著算
    }


    public static void main(String[] args) {
        System.out.println(tailfab(1, 1, 5));
//        for (int i = 1; i <= 45; i++) {
//            long start = System.currentTimeMillis();
//            System.out.println(i + ":" + tailfab(1, 1, i) + " 所花費時間為"
//                    + (System.currentTimeMillis() - start) + "ms");
//        }
//    tailFac(5, 1);

        /*
         * for (int i = 1; i <= 45; i++) { long start =
         * System.currentTimeMillis(); System.out.println(i + ":" + fab(i) +
         * " 所花費的時間為" + (System.currentTimeMillis() - start) + "ms"); }
         */

      /*for (int i = 1; i <= 45; i++) {
         long start = System.currentTimeMillis();
         System.out.println(i + ":" + noFab(i) + " 所花費的時間為"
               + (System.currentTimeMillis() - start) + "ms");
      }
      System.out.println("==================================");
      System.out.println("加了緩存的情況");
      for (int i = 1; i <= 45; i++) {
         data = new int[46];
         long start = System.currentTimeMillis();
         System.out.println(i + ":" + fab2(i) + " 所花費的時間為"
               + (System.currentTimeMillis() - start) + "ms");
      }*/
    }

}

