package com.example.algorithm._11_tree;

public class BinarySeachTree {

   private int color = 0;    //0表示黑，1表示紅
   int data;
   BinarySeachTree left;
   BinarySeachTree right;

   BinarySeachTree parent;

   public BinarySeachTree(int data) {
      this.data = data;
      this.left = null;
      this.parent = null;
      this.color = 1;
      this.right = null;
      //parent.parent    ;爺爺
      //parent.parent.left 左邊的叔叔
      //parent.left 兄弟姐妹
   }
   //插入的時候每次都是和根結點比較。一直要找到它應該插入的位置。
   //肯定會插在葉子結點。那麼其實大家可以看到 插入其實就是查找。 默認root不會為空

   public void insert(BinarySeachTree root,int data) {
      //if(root == null) {}
      if(root.data < data) { //根節點小 我們要放到右邊
         if(root.right == null) {
            root.right = new BinarySeachTree(data);
         }else {
            insert(root.right, data);
         }
      }else {
         if(root.left == null) {
            root.left = new BinarySeachTree(data);
         }else {
            insert(root.left, data);
         }
      }
   }

   public void find(BinarySeachTree root,int data) {
      if(root != null) {
         if(root.data < data) {
            find(root.right, data);
         }else if(root.data > data) {
            find(root.left, data);
         }else {
            System.out.println("找到了");
            System.out.println(root.data);
            return ;
         }
      }
   }
   public void pre() {

   }
   public void post() {

   }
   public void in(BinarySeachTree root) {    //中序遍歷
      if(root != null) {
         in(root.left);
         System.out.print(root.data + " ");
         in(root.right);
      }
   }

   public static void main(String[] args) {
      //快速排序，歸併排序，二叉樹排序
      int data[] = {0,5,9,1,2,3,10};
      BinarySeachTree root = new BinarySeachTree(data[0]);   //第一個點作為跟結點
      for(int i = 1 ; i < data.length ; i ++) {
         root.insert(root, data[i]);
      }
      System.out.println("中序遍歷:");
      root.in(root);
   }
}

