//
// Created by marky on 28.02.2025.
//
#pragma once
#include <string>
#ifndef ARTIFACT_H
#define ARTIFACT_H

#include "Snake.h"

class Artifact {
public:
    Artifact() = default;
    virtual ~Artifact() = default;

    virtual void use(Snake* snake){};
    virtual Point getPoint(){return {0,0};}
    virtual int getRadius(){return 0;}
    virtual std::string getName(){return "";}
};

#endif //ARTIFACT_H
