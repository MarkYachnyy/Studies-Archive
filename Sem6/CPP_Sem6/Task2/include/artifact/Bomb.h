//
// Created by marky on 28.02.2025.
//

#ifndef BOMB_H
#define BOMB_H
#include "Artifact.h"

class Bomb: public Artifact {
    Point _point;
    int _radius;
public:
    Bomb(int x, int y, int radius);
    ~Bomb() = default;

    void use(Snake* snake) override;
    Point getPoint() override;
    int getRadius() override;
    std::string getName() override;
};
#endif //BOMB_H
