#include "Slower.h"

#include "SnakeStateNames.h"

Slower::Slower(int x, int y, int strength): _point(x, y) {
    this->_strength = strength;
}

void Slower::use(Snake *snake) {
    snake->setProperty(STEPS_LEFT_TO_GROWTH, snake->getProperty(STEPS_LEFT_TO_GROWTH) + _strength);
}

Point Slower::getPoint() {
    return _point;
}

int Slower::getRadius() {
    return 0;
}

std::string Slower::getName() {
    return "slower";
}
