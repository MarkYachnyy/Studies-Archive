#include "Queue.cpp"
#include "Windows.h"

int main() {
    SetConsoleOutputCP(CP_UTF8);

    CPP_Task3::Queue<int> intQueue;
    intQueue.push(10);
    intQueue.push(20);
    intQueue.push(30);

    std::cout << "Очередь с целыми числами содержит: ";
    std::cout << intQueue.toString();

    std::cout << "Первый элемент (int): " << intQueue.peek() << " Размер очереди: " << intQueue.getSize() << std::endl;
    intQueue.poll();
    std::cout << "Очередь с целыми числами после извлечения элемента: ";
    std::cout << intQueue.toString();

    CPP_Task3::Queue<float> floatQueue;
    floatQueue.push(3.14f);
    floatQueue.push(2.71f);
    floatQueue.push(1.618f);

    std::cout << "\nОчередь с вещественными числами содержит: ";
    std::cout << floatQueue.toString();

    std::cout << "Первый элемент (float): " << floatQueue.peek() << " Размер очереди: " << floatQueue.getSize() <<
            std::endl;
    floatQueue.poll();
    std::cout << "Очередь с вещественными числами после извлечения элемента: ";
    std::cout << floatQueue.toString();

    CPP_Task3::Queue<std::string> stringQueue;
    stringQueue.push("Hello");
    stringQueue.push("World");
    stringQueue.push("!");

    std::cout << "\nОчередь со строками содержит: ";
    std::cout << stringQueue.toString();

    std::cout << "Первый элемент (string): " << stringQueue.peek() << " Размер очереди: " << stringQueue.getSize() <<
            std::endl;
    stringQueue.poll();
    std::cout << "Очередь со строками после извлечения элементов: ";
    std::cout << stringQueue.toString();

    return 0;
}
