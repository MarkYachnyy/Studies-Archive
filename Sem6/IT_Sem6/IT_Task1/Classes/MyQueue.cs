using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace IT_Task1.Classes;

public class MyQueue<T>
{
    private int size;
    private QueueNode<T>? head;

    public MyQueue()
    {
        this.size = 0;
        this.head = null;
    }

    public int Size
    {
        get { return size; }
    }

    public bool IsEmpty
    {
        get { return this.Size == 0; }
    }

    public T Current
    {
        get
        {
            if (head != null)
            {
                return head.Item;
            }
            else
                throw new EmptyQueueException();
        }
    }

    public T Dequeue()
    {
        if (head != null)
        {
            T res = head.Item;
            head = head.Next;
            size--;
            return res;
        }
        else
        {
            throw new EmptyQueueException();
        }
    }

    public void Inqueue(T item)
    {
        QueueNode<T> node = new(item);
        if (head == null)
        {
            head = node;
        }
        else
        {
            QueueNode<T> tail = head;
            while (tail.Next != null)
            {
                tail = tail.Next;
            }

            tail.Next = node;
        }

        size++;
    }
}

class QueueNode<T>
{
    public QueueNode(T item)
    {
        this.Item = item;
        this.Next = null;
    }

    public T Item { get; }

    public QueueNode<T>? Next { get; set; }
}