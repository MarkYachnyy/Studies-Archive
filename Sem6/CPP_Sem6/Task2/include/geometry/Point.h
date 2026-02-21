#ifndef POINT_H
#define POINT_H

class Point {
public:
    int x, y;
    Point(int x, int y);
    bool operator==(const Point &) const = default;
};

#endif //POINT_H
