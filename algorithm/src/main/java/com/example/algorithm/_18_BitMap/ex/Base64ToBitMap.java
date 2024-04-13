package com.example.algorithm._18_BitMap.ex;


import javax.management.ObjectName;
import java.util.HashMap;

public class Base64ToBitMap {
    public static void main(String[] args) {
        Base64String ff = new Base64String();
        String df = ff.getPDFString2();

//        System.out.println(df);
        StringBuilder sb = new StringBuilder();

        HashMap<String, String> strMapping = new HashMap<String, String>();
        strMapping.put("0", "00");
        strMapping.put("1", "01");
        strMapping.put("2", "02");
        strMapping.put("3", "03");
        strMapping.put("4", "04");
        strMapping.put("5", "05");
        strMapping.put("6", "06");
        strMapping.put("7", "07");
        strMapping.put("8", "08");
        strMapping.put("9", "09");
        strMapping.put("a", "10");
        strMapping.put("b", "11");
        strMapping.put("c", "12");
        strMapping.put("d", "13");
        strMapping.put("e", "14");
        strMapping.put("f", "15");
        strMapping.put("g", "16");
        strMapping.put("h", "17");
        strMapping.put("i", "18");
        strMapping.put("j", "19");
        strMapping.put("k", "20");
        strMapping.put("l", "21");
        strMapping.put("m", "22");
        strMapping.put("n", "23");
        strMapping.put("o", "24");
        strMapping.put("p", "25");
        strMapping.put("q", "26");
        strMapping.put("r", "27");
        strMapping.put("s", "28");
        strMapping.put("t", "29");
        strMapping.put("u", "30");
        strMapping.put("v", "31");
        strMapping.put("w", "32");
        strMapping.put("x", "33");
        strMapping.put("y", "34");
        strMapping.put("z", "35");
        strMapping.put("A", "36");
        strMapping.put("B", "37");
        strMapping.put("C", "38");
        strMapping.put("D", "39");
        strMapping.put("E", "40");
        strMapping.put("F", "41");
        strMapping.put("G", "42");
        strMapping.put("H", "43");
        strMapping.put("I", "44");
        strMapping.put("J", "45");
        strMapping.put("K", "46");
        strMapping.put("L", "47");
        strMapping.put("M", "48");
        strMapping.put("N", "49");
        strMapping.put("O", "50");
        strMapping.put("P", "51");
        strMapping.put("Q", "52");
        strMapping.put("R", "53");
        strMapping.put("S", "54");
        strMapping.put("T", "55");
        strMapping.put("U", "56");
        strMapping.put("V", "57");
        strMapping.put("W", "58");
        strMapping.put("X", "59");
        strMapping.put("Y", "60");
        strMapping.put("Z", "61");
//        for (int i = 0; i < df.length(); i++) {
//
//        }


        byte[] bits;
        bits = new byte[(df.length() >> 2) + 1];        //max/4 + 1
        // 1 char -> 1byte -> 8 bit
        // 4 char ->
        //從字串中取出4位，用上面的map轉成8bit，再將此8bit轉成1 bite 的 int
        //時間複雜O(n)/ 空間複雜O(n) 但大小會變小 可能不會變快
        for (int i = 0; i < df.length(); i = i + 4) {
//            System.out.println(df.substring(i, i + 4));

            String[] arr = df.substring(i, i + 4).split("");
            String i1 = strMapping.get(arr[0]) + strMapping.get(arr[1]) + strMapping.get(arr[2]) + strMapping.get(arr[3]);
            System.out.println(i1);
            bits[i] = Byte.parseByte(i1);
        }
        for (byte dddd : bits) {
            System.out.println(dddd);

        }
//        bitMap
    }


}