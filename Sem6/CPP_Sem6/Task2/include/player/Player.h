#ifndef PLAYER_H
#define PLAYER_H

#pragma once
#include "Field.h"
#include "Snake.h"

class Player {
public:
    Player() = default;
    virtual ~Player() = default;
    virtual Field* getField(){return nullptr;}
    virtual Snake* getSnake(){return nullptr;}
    virtual void turn(){}
};

#endif //PLAYER_H
