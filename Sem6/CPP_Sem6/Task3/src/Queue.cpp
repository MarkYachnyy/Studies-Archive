#include <iostream>
#include <stdexcept>
#include <sstream>

namespace CPP_Task3 {
    template<typename T>
    struct Node {
        T data;
        Node *next;

        Node(const T &value) : data(value), next(nullptr) {}
    };

    template<typename T>
    class Queue {
        Node<T> *front;
        Node<T> *rear;
        int size;

    public:
        Queue() : front(nullptr), rear(nullptr), size(0) {}

        ~Queue() {
            clear();
        }

        void clear() {
            while (!isEmpty()) {
                poll();
            }
        }

        void push(const T &value) {
            Node<T> *newNode = new Node<T>(value);
            if (isEmpty()) {
                front = rear = newNode;
            } else {
                rear->next = newNode;
                rear = newNode;
            }
            size++;
        }

        void poll() {
            if (isEmpty()) {
                throw std::out_of_range("Queue is empty");
            }
            Node<T> *temp = front;
            front = front->next;
            delete temp;
            size--;
            if (isEmpty()) {
                rear = nullptr;
            }
        }

        T peek() const {
            if (isEmpty()) {
                throw std::out_of_range("Queue is empty");
            }
            return front->data;
        }

        bool isEmpty() const {
            return front == nullptr;
        }

        int getSize() const {
            return size;
        }

        std::string toString() const {
            std::stringstream stringstream;
            Node<T> *current = front;
            while (current != nullptr) {
                stringstream << current->data << " ";
                current = current->next;
            }
            return stringstream.str();
        }
    };
}
