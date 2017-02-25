package com.codelexems.code;

public class Node {
    public int val;
    public Node next;

    Node(int val){
        this.val = val;
    }

    public static Node fromArray(int []a){
        Node head= new Node(a[0]);
        Node current = head;
        for (int i = 1; i < a.length; i++) {
            Node newNode = new Node(a[i]);
            current.next = newNode;
            current = current.next;
        }
        return head;
    }
}
