//
// Created by marky on 28.02.2025.
//

#ifndef FIELD_H
#define FIELD_H
#include <vector>

#include "Snake.h"
#include "Artifact.h"

class Field {
    int _width, _height;
public:
    Field(int width, int height);
    ~Field();
    bool operator==(const Field &other) const;

    int getWidth() const;
    int getHeight() const;
    bool isOccupied(Point& p);

    std::vector<Artifact*> artifacts;
    std::vector<Snake*> snakes;

};

#endif //FIELD_H
