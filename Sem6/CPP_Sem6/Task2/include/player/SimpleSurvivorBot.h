//
// Created by marky on 03.03.2025.
//

#ifndef SIMPLESURVIVORBOT_H
#define SIMPLESURVIVORBOT_H
#include "Player.h"
#include "Snake.h"
#include "Field.h"


class SimpleSurvivorBot: public Player{
    int _visionRange;
    Field* _field;
    Snake* _snake;

    int freeSpace(Direction direction);
    bool isAnObstacle(Point& p);
public:

    void turn() override;
    Snake* getSnake() override;
    Field* getField() override;

    SimpleSurvivorBot(Snake* snake, Field* field, int visionRange);
    ~SimpleSurvivorBot();
};


#endif //SIMPLESURVIVORBOT_H
