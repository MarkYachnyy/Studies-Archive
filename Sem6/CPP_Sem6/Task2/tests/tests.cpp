#include "Snake.h"
#include "Point.h"
#include "Utils.h"
#include <cstdlib>
#include <gtest/gtest.h>

#include "Invisible.h"
#include "Slower.h"
#include "Game.h"
#include "SnakeStateNames.h"

TEST(SnakeTest, CreationTest1) {
    int x = randInt(-10, 10);
    int y = randInt(-10, 10);
    int l = randInt(2, 10);

    Snake snake1(std::vector<Point>{{x, y}, {x, y + l - 1}}, Direction::Up, 2);
    Snake snake2(Point(x, y), Direction::Up, l, 2);
    EXPECT_EQ(snake1, snake2);
}

TEST(SnakeTest, CreationTest2) {
    int x = randInt(-10, 10);
    int y = randInt(-10, 10);
    int l = randInt(2, 10);

    Snake snake1(std::vector<Point>{{x, y}, {x + l - 1, y}}, Direction::Left, 2);
    Snake snake2(Point(x, y), Direction::Left, l, 2);
    EXPECT_EQ(snake1, snake2);
}

TEST(SnakeTest, CreationTest3) {
    int x = randInt(-10, 10);
    int y = randInt(-10, 10);
    int l = randInt(2, 10);

    Snake snake1(std::vector<Point>{{x, y}, {x, y - l + 1}}, Direction::Down, 2);
    Snake snake2(Point(x, y), Direction::Down, l, 2);
    EXPECT_EQ(snake1, snake2);
}

TEST(SnakeTest, CreationTest4) {
    int x = randInt(-10, 10);
    int y = randInt(-10, 10);
    int l = randInt(2, 10);

    Snake snake1(std::vector<Point>{{x, y}, {x - l + 1, y}}, Direction::Right, 2);
    Snake snake2(Point(x, y), Direction::Right, l, 2);
    EXPECT_EQ(snake1, snake2);
}

TEST(SnakeTest, MoveTestSimple1) {
    int x = randInt(-10, 10);
    int y = randInt(-10, 10);
    int l = randInt(2, 10);

    Snake snake1(std::vector<Point>{{x, y}, {x - l + 1, y}}, Direction::Right, 2);
    snake1.move();
    Snake snake2(std::vector<Point>{{x + 1, y}, {x - l + 2, y}}, Direction::Right, 2, 1);
    EXPECT_EQ(snake1, snake2);
}


TEST(SnakeTest, MoveTestTailDisappear1) {
    int x = randInt(-10, 10);
    int y = randInt(-10, 10);
    int l = randInt(2, 10);

    Snake snake1(std::vector<Point>{{x, y}, {x, y + l - 1}, {x + 1, y + l - 1}}, Direction::Up, 5);
    Snake snake2(std::vector<Point>{{x, y - 1}, {x, y + l - 1}}, Direction::Up, 5, 4);
    snake1.move();
    EXPECT_EQ(snake1, snake2);
}

TEST(SnakeTest, MoveTestTailDisappear2) {
    int x = randInt(-10, 10);
    int y = randInt(-10, 10);
    int l = randInt(2, 10);

    Snake snake1(std::vector<Point>{{x, y}, {x + l - 1, y}, {x + l - 1, y + l - 1}, {x + l, y + l - 1}},
                 Direction::Left, 5);
    Snake snake2(std::vector<Point>{{x - 1, y}, {x + l - 1, y}, {x + l - 1, y + l - 1}}, Direction::Left, 5, 4);
    snake1.move();
    EXPECT_EQ(snake1, snake2);
}

TEST(SnakeTest, MoveTestTailDoesNotDisappear) {
    int x = randInt(-10, 10);
    int y = randInt(-10, 10);
    int l = randInt(2, 10);

    Snake snake1(std::vector<Point>{{x, y}, {x + l - 1, y}, {x + l - 1, y + l - 1}, {x + l + 1, y + l - 1}},
                 Direction::Left, 5);
    Snake snake2(std::vector<Point>{{x - 1, y}, {x + l - 1, y}, {x + l - 1, y + l - 1}, {x + l, y + l - 1}}, Direction::Left, 5, 4);
    snake1.move();
    EXPECT_EQ(snake1, snake2);
}

TEST(ArtifactTest, InvisibleTest) {
    int x = randInt(-10, 10);
    int y = randInt(-10, 10);
    int s = randInt(2, 10);

    Snake snake1(std::vector<Point>{{x, y}}, Direction::Left, 5, 5);
    Snake snake2(std::vector<Point>{{x, y}}, Direction::Left, 5,5);
    snake2.setProperty(INVISIBLE, s);
    Invisible i(1,1, s);
    i.use(&snake1);

    EXPECT_EQ(snake1,snake2);
}

TEST(ArtifactTest, SlowerTest) {
    int x = randInt(-10, 10);
    int y = randInt(-10, 10);
    int s = randInt(2, 10);

    Snake snake1(std::vector<Point>{{x, y}}, Direction::Left, 5, 2);
    Snake snake2(std::vector<Point>{{x, y}}, Direction::Left, 5,2+s);
    Slower i(1,1, s);
    i.use(&snake1);

    EXPECT_EQ(snake1,snake2);
}

TEST(GameTest, CollisionTest) {
    int w = randInt(20,30);
    int h = randInt(20,30);
    int l = randInt(3, 6);

    Game game1(w, h, 0, 0,0, 2);
    Game game2(w, h, 0, 0,0, 2);

    game1.addSnake(new Snake(Point(w/2, h/2), Direction::Left, l, 10));
    game2.addSnake(new Snake(Point(w/2, h/2), Direction::Left, l, 10));

    game1.addSnake(new Snake(Point(w/2, h/2), Direction::Right, l, 10));
    game2.addSnake(new Snake(Point(w/2, h/2), Direction::Right, l, 10));

    game1.addSnake(new Snake(Point(w/2+l-1, h/2), Direction::Up, l, 10));
    game2.addSnake(new Snake(Point(w/2+l-1, h/2), Direction::Up, l, 10));

     for (Snake* s: game1._field.snakes) {
         s->setProperty(DEAD, 1);
     }

    game2.countCollisions();

    EXPECT_EQ(game1._field, game2._field);
}

TEST(GameTest, OutOfBoundsTest) {
    int w = randInt(20,30);
    int h = randInt(20,30);
    int l = randInt(3, 6);

    Game game1(w, h, 0, 0,0, 2);
    Game game2(w, h, 0, 0,0, 2);

    game1.addSnake(new Snake(Point(w/2,-1), Direction::Up, l, 10));
    game2.addSnake(new Snake(Point(w/2,-1), Direction::Up, l, 10));

    game1.addSnake(new Snake(Point(-1, h/2), Direction::Left, l, 10));
    game2.addSnake(new Snake(Point(-1, h/2), Direction::Left, l, 10));

    game1.addSnake(new Snake(Point(w, w/2), Direction::Right, l, 10));
    game2.addSnake(new Snake(Point(w, w/2), Direction::Right, l, 10));

    game1.addSnake(new Snake(Point(w/2, h), Direction::Down, l, 10));
    game2.addSnake(new Snake(Point(w/2, h), Direction::Down, l, 10));

    for (Snake* s: game1._field.snakes) {
        s->setProperty(DEAD, true);
    }

    game2.countCollisions();

    EXPECT_EQ(game1._field, game2._field);
}

TEST(PlayerTest, BordersDodgeTest) {
    int w = 21;
    int h = 21;
    int l = randInt(3, 6);
    Game game1(w, h, 0, 0,0, 2);

    bool res = true;

    Direction dirs[] = {Direction::Up, Direction::Right, Direction::Down, Direction::Left};
    for (Direction dir: dirs) {
        Snake *snake = new Snake(Point(w/2 + w/2*getIncrementPoint(dir).x, h/2 + h/2*getIncrementPoint(dir).y), dir, l, 1);
        Player* p = new SimpleSurvivorBot(snake, &game1._field, 3);
        game1.addPlayer(p);
        game1.addSnake(snake);
        p->turn();
        if (!(snake->direction == clockWise(dir) || snake->direction == counterClockWise(dir))) {
            res = false;
            break;
        }
    }

    EXPECT_EQ(res, true);
}


int main(int argc, char **argv) {
    srand(time(NULL));
    testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}
