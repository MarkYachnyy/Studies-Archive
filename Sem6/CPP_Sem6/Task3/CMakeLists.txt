cmake_minimum_required(VERSION 3.30)
project(CPP_Task3)

set(CMAKE_CXX_STANDARD 20)

add_subdirectory(googletest)

add_executable(CPP_Task3
        src/main.cpp
        src/Queue.cpp)

add_executable(tests
        test/tests.cpp
        src/Queue.cpp)

target_link_libraries(tests gtest)
