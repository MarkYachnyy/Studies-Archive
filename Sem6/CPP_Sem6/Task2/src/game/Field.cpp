#include "Game.h"

Field::Field(const int width, const int height) {
    this->_width = width;
    this->_height = height;
    this->snakes = std::vector<Snake*>();
    this->artifacts = std::vector<Artifact*>();
}

Field::~Field() {
    for (Snake* s: snakes) {
        delete s;
    }
    for (Artifact* a: artifacts) {
        delete a;
    }
}

bool Field::operator==(const Field &other) const {
    if (this->_width != other._width || this->_height != other._height) {
        return false;
    }
    if (this->snakes.size() != other.snakes.size()) {
        return false;
    }
    if (this->artifacts.size() != other.artifacts.size()) {
        return false;
    }
    for (int i = 0; i < this->snakes.size(); ++i) {
        Snake* s1 = this->snakes[i];
        Snake* s2 = other.snakes[i];
        if (*s1 != *s2) {
            return false;
        }
    }
    return true;
}

int Field::getHeight() const {
    return _height;
}

int Field::getWidth() const {
    return _width;
}

bool Field::isOccupied(Point &p) {
    for (Snake* s: snakes) {
        if (s->containsPoint(p)) {
            return true;
        }
    }
    for (Artifact* a: artifacts) {
        if (a->getPoint().x == p.x && a->getPoint().y == p.y) {
            return true;
        }
    }
    return false;
}





