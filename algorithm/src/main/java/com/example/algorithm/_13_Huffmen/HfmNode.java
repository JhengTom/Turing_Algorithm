package com.example.algorithm._13_Huffmen;

public class HfmNode implements Comparable<HfmNode> {        //优先队列,小的我把你优先级调高

    String chars;        //节点里面的字符
    int fre;        //表示是频率
    HfmNode left;
    HfmNode right;
    HfmNode parent;    //用来找上层的

    @Override
    public int compareTo(HfmNode o) {
        return this.fre - o.fre;

        //如果輸入'等'於原始，則返回值 0；
        //如果輸入'小'於原始，則返回一個小於 0 的值；
        //如果輸入'大'於原始，則返回一個大於 0 的值。
    }
}
