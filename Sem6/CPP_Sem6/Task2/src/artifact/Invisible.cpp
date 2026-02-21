#include "Invisible.h"

#include "SnakeStateNames.h"

Invisible::Invisible(int x, int y, int strength): _point(x, y) {
    this->_strength = strength;
}

void Invisible::use(Snake *snake) {
    snake->setProperty(INVISIBLE, _strength);
}

Point Invisible::getPoint() {
    return _point;
}

int Invisible::getRadius() {
    return 0;
}

std::string Invisible::getName() {
    return "invisible";
}




