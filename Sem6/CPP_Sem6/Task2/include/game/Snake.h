
#ifndef SNAKE_H
#define SNAKE_H

#include <map>
#include <string>
#include <vector>
#include "Direction.h"
#include "Point.h"

class Snake {

public:
    Snake(std::vector<Point> points, Direction direction, int growthSpeed, int movesLeft);
    Snake(std::vector<Point> points, Direction direction, int growthSpeed);
    Snake(Point point, Direction direction, int length, int growthSpeed);

    bool operator==(const Snake &) const = default;

    Direction direction;

    void move();
    Point& head();
    int containsPoint(Point& point);

    int getProperty(std::string& name);
    void setProperty(std::string& name, int value);
    int decrementProperty(std::string& name);
private:
    std::map<std::string, int> properties;
    std::vector<Point> _points;

    void moveHead();
    void moveTail();
};

#endif //SNAKE_H
