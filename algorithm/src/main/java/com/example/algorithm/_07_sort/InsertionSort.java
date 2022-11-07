package com.example.algorithm._07_sort;

public class InsertionSort {

    /**
     * 1.將陣列分成已排序段和未排序段。初始化時已排序端只有一個元素
     * 2.到未排序段取元素插入到已排序段，並保證插入後仍然有序
     * 3.重複執行上述操作，直到未排序段元素全部加完。
     *
     * @param args
     */
    public static void main(String[] args) {
        int a[] = {9, 8, 7, 0, 1, 3, 2};
        int n = a.length;
        //這裡面會有幾層迴圈 2
        //時間複雜度：n^2
        //最好的情況：什麼情況下：O(n); O(1)
        //for(){      //分段
        for (int i = 1; i < n; i++) {       //為什麼i要從1開始？ 第一個不用排序，我們就把陣列從i分開，0~I的認為已經排好序
            int data = a[i];
            int j = i - 1;
            for (; j >= 0; j--) {//從尾到頭 1+2+3+4+5+...+n=>
                if (a[j] > data) {
                    a[j + 1] = a[j];    // 資料往後移動
                } else { //因為前面已經是排好序的 那麼找到一個比他小的就不用找了，因為前面的肯定更小
                    break; //O(1)     如果這個break執行的越多 那麼我是不是效率就越高?
                }
            }
            a[j + 1] = data;
            System.out.print("第" + i + "次的排序結果為：");
            for (j = 0; j < n; j++) {
                System.out.print(a[j] + " ");
            }
            System.out.println();
        }
        // }
    }
}

