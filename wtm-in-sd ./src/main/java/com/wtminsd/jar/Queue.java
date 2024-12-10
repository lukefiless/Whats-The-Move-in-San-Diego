package com.wtminsd.jar;

public class Queue {
    private Node front, rear;

    public void enqueue(Event event) {
        Node newNode = new Node(event);
        if(rear == null) {
            front = rear = newNode;
            return;
        }
        rear.next = newNode;
        return;
    }

    public Event dequeue() {
        if(isEmpty()) {
            System.out.println("Queue is empty.");
            return null;
        }
        Event temp = front.event;
        front = front.next;
        if(front == null) {
            rear = null;
        }
        return temp;
    }

    public Event peek() {
        if (isEmpty()) {
            System.out.println("Queue is empty.");
            return null;
        }
        return front.event;
    }

    public boolean isEmpty() {
        return front == null;
    }
}
class Node {
    Event event;
    Node next;   

    public Node(Event event) {
        this.event = event;
        this.next = null;
    }
}
