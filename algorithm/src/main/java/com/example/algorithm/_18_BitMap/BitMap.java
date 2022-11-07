package com.example.algorithm._18_BitMap;

public class BitMap {

    byte[] bits;        //如果是byte那就一个只能存8个数
    int max;            //表示最大的那个数

    public BitMap(int max) {
        this.max = max;
        bits = new byte[(max >> 3) + 1];        //max/8 + 1
    }

    public void add(int n) {        //往bitmap里面添加数字
        if (n > max) {
            return;
        }
        int bitsIndex = n >> 3;        // 除以8 就可以知道在那个byte
        int loc = n % 8;        ///这里其实还可以用&运算
        //接下来就是要把bit数组里面的 bisIndex这个下标的byte里面的 第loc 个bit位置为1
        bits[bitsIndex] |= 1 << loc; //先做或運算後往左邊移動loc位
        //
    }

    public boolean find(int n) {
        int bitsIndex = n >> 3;        // 除以8 就可以知道在那个byte
        int loc = n % 8;        ///这里其实还可以用&运算

        int flag = bits[bitsIndex] & (1 << loc);    //如果原来的那个位置是0 那肯定就是0 只有那个位置是1 才行
        if (flag == 0) return false;
        return true;
    }

    /***
     * 6 % 8 = 6
     *
     * 8 : 1000
     * 7 : 0111
     * 6 : 0110
     *
     * 6 & 7 = 0110 => 6 //只有在二進治的條件下才能這樣!!而且計的減一
     * 6 % 8 = 6 & 8
     *
     */

    public boolean remove(int n) {
        int bitsIndex = n >> 3;        // 除以8 就可以知道在那个byte
        int loc = n & 7;        ///这里其实还可以用&运算
        System.out.println(n + "刪除之前的資料=>" + Integer.toBinaryString(bits[bitsIndex]));

        if (!find(n)) {
            // 不存在 直接返回false
            return false;
        }
        // 当前位上存在，实行删除
        bits[bitsIndex] ^= 1 << loc;
        System.out.println("loc=>" + loc);
        System.out.println(n + "刪除之後的資料=>" + Integer.toBinaryString(bits[bitsIndex]));

        return true;
    }

    public static void main(String[] args) {
        BitMap bitMap = new BitMap(200000001);    //10亿
        bitMap.add(2);
        bitMap.add(3);
        bitMap.add(64);
        bitMap.add(66);

        System.out.println(bitMap.find(3));
        System.out.println(bitMap.find(64));
        System.out.println(bitMap.find(65));

        bitMap.remove(64);
        System.out.println(bitMap.find(64));
    }

}

/**
 * |=運算解說  只要有1就是1
 * <p>
 * int a = 5;
 * int b = 3;
 * System.out.println(a|=b);
 * 輸出7
 * 5 的二進位 是 0101
 * 3 的二進位 是 0011
 * 按位或 運算
 * 有一個為1 就是1
 * 結果 ： 0111 （2的二次方+加2的一次方+1） 7
 * <p>
 * ^= XOR 運算解說 不同就是1，相同就是0
 * <p>
 * int a = 5;
 * int b = 3;
 * System.out.println(a^=b);
 * 輸出6
 * 5 的二進位 是 0101
 * 3 的二進位 是 0011
 * 結果 ： 0110
 */


/**
 * ^= XOR 運算解說 不同就是1，相同就是0
 *
 * int a = 5;
 * int b = 3;
 * System.out.println(a^=b);
 * 輸出6
 * 5 的二進位 是 0101
 * 3 的二進位 是 0011
 * 結果 ： 0110
 */
