package com.publics.news;

public class LinkedList {
   public int key;
   public int data;
   public LinkedList next;

   public LinkedList(int key, int data){
      this.key = key;
      this.data = data;
   }

   public void display(){
      System.out.print("{"+key+","+data+"}");
   }
}
