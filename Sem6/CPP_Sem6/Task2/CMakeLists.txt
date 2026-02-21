cmake_minimum_required(VERSION 3.30)
project(Task2)

set(CMAKE_CXX_STANDARD 20)
set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -static-libstdc++ -static-libgcc")

add_subdirectory(googletest)

add_executable(Task2 src/main.cpp
        include/game/Snake.h
        include/geometry/Point.h
        include/game/Field.h
        include/artifact/Artifact.h
        include/artifact/Bomb.h
        include/artifact/Slower.h
        include/artifact/Invisible.h
        src/game/Field.cpp
        src/game/Snake.cpp
        include/util/Utils.h
        include/geometry/Direction.h
        src/geometry/Point.cpp
        src/game/Game.cpp
        include/game/Game.h
        src/player/SimpleSurvivorBot.cpp
        include/player/SimpleSurvivorBot.h
        include/player/Player.h
        src/artifact/Invisible.cpp
        include/util/symbols.h
        src/artifact/Bomb.cpp
        src/artifact/Slower.cpp
        include/game/SnakeStateNames.h)

include_directories(include/game)
include_directories(include/geometry)
include_directories(include/player)
include_directories(include/util)
include_directories(include/artifact)

add_executable(tests tests/tests.cpp
        include/game/Snake.h
        include/geometry/Point.h
        include/game/Field.h
        include/artifact/Artifact.h
        include/artifact/Bomb.h
        include/artifact/Slower.h
        include/artifact/Invisible.h
        src/game/Field.cpp
        src/game/Snake.cpp
        include/util/Utils.h
        include/geometry/Direction.h
        src/geometry/Point.cpp
        src/game/Game.cpp
        include/game/Game.h
        src/player/SimpleSurvivorBot.cpp
        include/player/SimpleSurvivorBot.h
        include/player/Player.h
        src/artifact/Invisible.cpp
        include/util/symbols.h
        src/artifact/Bomb.cpp
        src/artifact/Slower.cpp
        include/game/SnakeStateNames.h
)

target_link_libraries(tests gtest)