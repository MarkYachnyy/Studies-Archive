#ifndef INVISIBLE_H
#define INVISIBLE_H
#include "Artifact.h"
#include "Point.h"


class Invisible: public Artifact {
    Point _point;
    int _strength;
public:
    Invisible(int x, int y, int strength);
    ~Invisible() = default;

    void use(Snake* snake) override;
    Point getPoint() override;
    int getRadius() override;
    std::string getName() override;
};
#endif
