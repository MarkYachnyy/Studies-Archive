//
// Created by marky on 28.02.2025.
//

#ifndef SLOWER_H
#define SLOWER_H
#include "Artifact.h"

class Slower: public Artifact {
    Point _point;
    int _strength;
public:
    Slower(int x, int y, int strength);
    ~Slower() = default;

    void use(Snake* snake) override;
    Point getPoint() override;
    int getRadius() override;
    std::string getName() override;
};

#endif //SLOWER_H
