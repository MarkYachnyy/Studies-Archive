#ifndef GAME_H
#define GAME_H
#include <string>

#include "Field.h"
#include "SimpleSurvivorBot.h"


class Game {
    std::vector<Player*> _players;
    int _artifactSpawnRate;

    int _invisibleStrength;
    int _slowerStrength;
    int _bombRadius;

    Artifact* createRandomArtifact();

public:
    Game(int field_w, int field_h, int artifactSpawnRatePercent,  int init_snake_c, int init_snake_l, int snake_growth_s);
    Game(int field_w, int field_h, int artifactSpawnRatePercent, int init_snake_c, int init_snake_l, int snake_growth_s, int invisibleStrength, int slowerStrength, int bombRadius);
    ~Game();
    Field _field;

    bool tick();
    std::string print();
    void addArtifact(Artifact *artifact);
    void addPlayer(Player *player);
    void addSnake(Snake *snake);

    void countCollisions();
    void killPlayers();
    void applyArtifacts();
};



#endif //GAME_H
