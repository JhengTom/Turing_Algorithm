package com.example.algorithm._3_arrayList;

import javax.xml.transform.Source;

/**
 * @Description
 * @Name homeWork
 * @Author xd_to
 * @Project Turing
 * @Package com.example.algorithm._3_arrayList
 * @Date 2022/9/17 - 下午 03:36
 * @Version 1.0
 */
public class HomeWorkLinked {
    private MyNode head;

    public void add(Integer value) {
        System.out.println("add => " +value);
        MyNode newNode = new MyNode(value);
        newNode.next = head;
        head = newNode;
        circle();
    }

    public void circle() {
        MyNode cur = head;

        while (cur.next != null) {
                cur = cur.next;
            }
        System.out.println(cur.next);
        cur.next=head;
        System.out.println(cur.next);
    }

    public void de() {

    }

    public void print() {
        MyNode cur = head;

        while (cur != null) {
            System.out.print(cur.value + ",");
            cur = cur.next;
        }
        System.out.println();

    }

    public static void main(String[] args) {


        HomeWorkLinked myLinked = new HomeWorkLinked();
        Integer i = 1;
        Integer n = 6;
        while (i < n + 1) {
//            System.out.println(i);
            myLinked.add(i);
            i++;
        }
        myLinked.print();

    }

}

class MyNode {
    Integer value;
    MyNode next;

    MyNode(Integer value) {
//        System.out.println(value);
        this.value = value;
        this.next = null;
    }
}