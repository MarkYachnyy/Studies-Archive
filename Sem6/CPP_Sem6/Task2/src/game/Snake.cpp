#include "Point.h"
#include "Snake.h"

#include <iostream>
#include "Utils.h"
#include "Direction.h"
#include "SnakeStateNames.h"

Snake::Snake(std::vector<Point> points, Direction direction, int growthSpeed) : Snake(
    points, direction, growthSpeed, growthSpeed) {
}

Snake::Snake(std::vector<Point> points, Direction direction, int growthSpeed, int movesLeft) {
    this->_points = points;
    this->direction = direction;
    this->setProperty(GROWTH_SPEED, growthSpeed);
    this->setProperty(STEPS_LEFT_TO_GROWTH, movesLeft);
}


Snake::Snake(Point point, Direction direction, int length, int growthSpeed) {
    this->_points = std::vector<Point>();
    this->direction = direction;
    this->setProperty(GROWTH_SPEED, growthSpeed);
    this->setProperty(STEPS_LEFT_TO_GROWTH, growthSpeed);

    Point tail(point.x, point.y);
    Point inc = getIncrementPoint(direction);
    tail.x -= inc.x * (length - 1);
    tail.y -= inc.y * (length - 1);
    _points.push_back(point);
    _points.push_back(tail);
}

int Snake::getProperty(std::string& name) {
    return properties.contains(name) ? properties[name] : 0;
}

void Snake::setProperty(std::string &name, int value) {
    properties[name] = value;
}

void Snake::move() {
    int dx = _points.at(0).x - _points.at(1).x;
    int dy = _points.at(0).y - _points.at(1).y;


    if (!(dx > 0 && direction == Direction::Right ||
          dx < 0 && direction == Direction::Left ||
          dy > 0 && direction == Direction::Down ||
          dy < 0 && direction == Direction::Up)) {
        _points.insert(_points.cbegin(), Point(head().x, head().y));
    }
    moveHead();
    if (getProperty(STEPS_LEFT_TO_GROWTH) > 0) {
        moveTail();
        decrementProperty(STEPS_LEFT_TO_GROWTH);
    } else {
        setProperty(STEPS_LEFT_TO_GROWTH, getProperty(GROWTH_SPEED));
    }
    if (getProperty(INVISIBLE)) decrementProperty(INVISIBLE);
}

int Snake::decrementProperty(std::string &name) {
    setProperty(name, getProperty(name) - 1);
    return getProperty(name);
}


void Snake::moveHead() {
    Point inc = getIncrementPoint(direction);
    this->head().x += inc.x;
    this->head().y += inc.y;
}

void Snake::moveTail() {
    Point &tail = _points.at(_points.size() - 1);
    Point &tail1 = _points.at(_points.size() - 2);
    int dx = normalizedInt(tail1.x - tail.x);
    int dy = normalizedInt(tail1.y - tail.y);
    tail.x += dx;
    tail.y += dy;
    if (tail.x == tail1.x && tail.y == tail1.y) {
        _points.pop_back();
    }
}

int Snake::containsPoint(Point &p) {
    int x = p.x;
    int y = p.y;
    int res = 0;
    for (int i = 0; i < _points.size() - 1; ++i) {
        Point p1 = _points.at(i);
        Point p2 = _points.at(i + 1);
        if (x >= std::min(p1.x, p2.x) && x <= std::max(p1.x, p2.x) && y == p1.y && y == p2.y ||
            y >= std::min(p1.y, p2.y) && y <= std::max(p1.y, p2.y) && x == p1.x && x == p2.x) {
            res++;
        }
    }
    return res;
}


Point &Snake::head() {
    return _points.at(0);
}
