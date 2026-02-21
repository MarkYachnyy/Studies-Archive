#include <iostream>
#include <thread>
#include <cstdlib>
#include <Windows.h>

#include "Utils.h"
#include "Game.h"


int main() {
    SetConsoleOutputCP(CP_UTF8);
    srand(time(NULL));
    Game game(20,  20, 3, 4, 4, 100, 30,5,4);
    for (int i = 0; i < 1000;i++) {
        game.tick();
        std::cout << "\033[H\033[2J";
        std::cout << game.print() << std::endl;
        std::flush(std::cout);
        std::this_thread::sleep_for(std::chrono::milliseconds(150));
    }

}

