#include <algorithm>
#include "Game.h"
#include "SimpleSurvivorBot.h"
#include "Utils.h"
#include <string>

#include "SnakeStateNames.h"
#include "Invisible.h"
#include "Bomb.h"
#include "Slower.h"
#include "symbols.h"

Game::Game(int field_w, int field_h, int artifactSpawnRatePercent, int init_snake_c, int init_snake_l,
           int snake_growth_s): Game(
    field_w, field_h, artifactSpawnRatePercent, init_snake_c, init_snake_l, snake_growth_s, 20, 10, 5) {
}

Game::Game(int field_w, int field_h, int artifactSpawnRatePercent, int init_snake_c, int init_snake_l,
           int snake_growth_s,
           int invisibleStrength, int slowerStrength, int bombRadius) : _field(
    field_w, field_h) {
    _artifactSpawnRate = artifactSpawnRatePercent;
    _players = std::vector<Player *>();

    int h = (field_h - init_snake_l) / 2;
    for (int i = 0; i < init_snake_c; ++i) {
        Snake *s = new Snake({i * field_w / init_snake_c, h}, Direction::Up, init_snake_l, snake_growth_s);
        addSnake(s);
    }
    for (Snake *snake: _field.snakes) {
        Player *p = new SimpleSurvivorBot(snake, &_field, 3);
        addPlayer(p);
    }

    _bombRadius = bombRadius;
    _invisibleStrength = invisibleStrength;
    _slowerStrength = slowerStrength;
}

void Game::addArtifact(Artifact *artifact) {
    _field.artifacts.emplace_back(artifact);
}

void Game::addPlayer(Player *player) {
    _players.emplace_back(player);
}

void Game::addSnake(Snake *snake) {
    _field.snakes.emplace_back(snake);
}

Game::~Game() {
    for (Player *p: _players) {
        delete p;
    }
}


bool Game::tick() {
    for (Player *player: _players) {
        player->turn();
    }
    for (Snake* snake: _field.snakes) {
        snake->move();
    }
    if (randInt(0, 100) < _artifactSpawnRate) {
        addArtifact(createRandomArtifact());
    }
    applyArtifacts();
    countCollisions();
    killPlayers();
    return true;
}

void Game::killPlayers() {
    std::vector<Snake *> newSnakes;
    std::vector<Player *> newPlayers;
    for (Player *p: _players) {
        Snake *s = p->getSnake();
        if (s->getProperty(DEAD)) {
            delete s;
            delete p;
        } else {
            newSnakes.emplace_back(s);
            newPlayers.emplace_back(p);
        }
    }
    _players = newPlayers;
    _field.snakes = newSnakes;
}


void Game::countCollisions() {
    for (Snake *playersSnake: _field.snakes) {
        if (playersSnake->head().x < 0 || playersSnake->head().x >= _field.getWidth() ||
            playersSnake->head().y < 0 || playersSnake->head().y >= _field.getHeight()) {
            playersSnake->setProperty(DEAD, 1);
            continue;
        }
        if (playersSnake->containsPoint(playersSnake->head()) > 1) {
            if (playersSnake->getProperty(INVISIBLE) <= 0) {
                playersSnake->setProperty(DEAD, 1);
                continue;
            }
        }
        for (Snake *s: _field.snakes) {
            if (s != playersSnake) {
                if (s->containsPoint(playersSnake->head())) {
                    playersSnake->setProperty(DEAD, 1);
                    break;
                }
            }
        }
    }
}

void Game::applyArtifacts() {
    std::vector<Artifact *> to_erase;
    for (Artifact *a: _field.artifacts) {
        bool used = false;
        for (Snake *s: _field.snakes) {
            if (a->getPoint().x == s->head().x && a->getPoint().y == s->head().y) {
                used = true;
                for (int i = a->getPoint().y - a->getRadius(); i < a->getPoint().y + a->getRadius() + 1; ++i) {
                    for (int j = a->getPoint().x - a->getRadius(); j < a->getPoint().x + a->getRadius() + 1; ++j) {
                        Point p(j, i);
                        for (Snake *s1: _field.snakes) {
                            if (s1->containsPoint(p)) {
                                a->use(s1);
                            }
                        }
                    }
                }
            }
        }

        if (used) {
            to_erase.emplace_back(a);
        }
    }
    for (Artifact *a: to_erase) {
        delete a;
        _field.artifacts.erase(std::find(_field.artifacts.begin(), _field.artifacts.end(), a));
    }
}

Artifact *Game::createRandomArtifact() {
    Point p(0, 0);
    do {
        p = Point(randInt(0, _field.getWidth()), randInt(0, _field.getHeight()));
    } while (_field.isOccupied(p));
    Artifact *artifact;
    switch (randInt(0, 3)) {
        case 0: {
            artifact = new Invisible(p.x, p.y, _invisibleStrength);
            break;
        }
        case 1: {
            artifact = new Bomb(p.x, p.y, _bombRadius);
            break;
        }
        case 2: {
            artifact = new Slower(p.x, p.y, _slowerStrength);
            break;
        }
        default: {
            break;
        }
    }
    return artifact;
}

std::string Game::print() {
    std::string res;
    for (int i = 0; i < _field.getHeight(); ++i) {
        for (int j = 0; j < _field.getWidth(); ++j) {
            std::string c = BG_BLACK;
            for (Snake *snake: _field.snakes) {
                Point p(j, i);
                if (snake->head().x == p.x && snake->head().y == p.y) {
                    c = BG_RED;
                } else if (snake->containsPoint(p)) {
                    if (snake->getProperty(INVISIBLE)) {
                        c = BG_CYAN;
                    } else {
                        c = BG_YELLOW;
                    }
                    break;
                }
            }
            bool artifact = false;
            for (Artifact *a: _field.artifacts) {
                if (a->getPoint().x == j && a->getPoint().y == i) {
                    artifact = true;
                    if (a->getName() == "invisible") {
                        c += CYAN;
                    } else if (a->getName() == "bomb") {
                        c += RED;
                    } else {
                        c += PURPLE;
                    }
                    break;
                }
            }
            c += artifact ? DIAMOND : WHITESPACE;
            c += RESET;
            c += ' ';
            res += c;
        }
        res += "\n";
    }

    return res;
}
