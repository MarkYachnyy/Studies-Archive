#pragma once
#include "Direction.h"
#include "Point.h"
#include <ctime>
#include <cstdlib>

inline int normalizedInt(int a) {
    if (a > 0) {
        return 1;
    }
    if (a < 0) {
        return -1;
    }
    return 0;
}

inline Point getIncrementPoint(Direction direction) {
    switch (direction) {
        case Direction::Up: {
            return {0, -1};
        }
        case Direction::Right: {
            return {1, 0};
        }
        case Direction::Down: {
            return {0, 1};
        }
        case Direction::Left: {
            return {-1, 0};
        }
        default: {
            return {0, 0};
        }
    }
}

inline  Direction clockWise(Direction direction) {
    return Direction((int(direction) + 1) % 4);
}

inline  Direction counterClockWise(Direction direction) {
    return Direction((int(direction) + 3) % 4);
}

inline int randInt(int min, int max) {
    return rand() % (max - min) + min;
}
