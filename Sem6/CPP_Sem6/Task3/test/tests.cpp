#include <gtest/gtest.h>
#include "../src/Queue.cpp"

TEST(QueueTest, PushTest) {
    CPP_Task3::Queue<int> q1 = CPP_Task3::Queue<int>();
    CPP_Task3::Queue<std::string> q2 = CPP_Task3::Queue<std::string>();
    CPP_Task3::Queue<std::vector<int> > q3 = CPP_Task3::Queue<std::vector<int> >();

    for (int i = 0; i < 10; ++i) {
        EXPECT_EQ(q1.getSize(), i);
        EXPECT_EQ(q2.getSize(), i);
        EXPECT_EQ(q3.getSize(), i);
        q1.push(11);
        q2.push("nfgsf");
        q3.push(std::vector<int>());
    }
}

TEST(QueueTest, PollTest) {
    CPP_Task3::Queue<int> q1 = CPP_Task3::Queue<int>();
    CPP_Task3::Queue<std::string> q2 = CPP_Task3::Queue<std::string>();
    CPP_Task3::Queue<std::vector<int> > q3 = CPP_Task3::Queue<std::vector<int> >();

    for (int i = 0; i < 10; ++i) {
        q1.push(11);
        q2.push("ndasd");
        q3.push(std::vector<int>());
    }

    for (int i = 9; i >= 0; --i) {
        q1.poll();
        q2.poll();
        q3.poll();
        EXPECT_EQ(q1.getSize(), i);
        EXPECT_EQ(q2.getSize(), i);
        EXPECT_EQ(q3.getSize(), i);
    }
}

TEST(QueueTest, PeekTest) {
    CPP_Task3::Queue<int> q1 = CPP_Task3::Queue<int>();
    CPP_Task3::Queue<std::string> q2 = CPP_Task3::Queue<std::string>();
    CPP_Task3::Queue<std::vector<int> > q3 = CPP_Task3::Queue<std::vector<int> >();

    for (int i = 0; i < 2; ++i) {
        q1.push(11);
        q2.push("nfsd");
        q3.push(std::vector<int>());
    }


    q1.peek();
    q2.peek();
    q3.peek();
    EXPECT_EQ(q1.getSize(), 2);
    EXPECT_EQ(q2.getSize(), 2);
    EXPECT_EQ(q3.getSize(), 2);
}


TEST(QueueTest, PollTestException) {
    CPP_Task3::Queue<int> q1 = CPP_Task3::Queue<int>();
    CPP_Task3::Queue<std::string> q2 = CPP_Task3::Queue<std::string>();
    CPP_Task3::Queue<std::vector<int> > q3 = CPP_Task3::Queue<std::vector<int> >();

    bool e1 = false;
    bool e2 = false;
    bool e3 = false;
    try {
        q1.poll();
    } catch (std::out_of_range&) {
        e1 = true;
    }
    try {
        q2.poll();
    } catch (std::out_of_range&) {
        e2 = true;
    }
    try {
        q3.poll();
    } catch (std::out_of_range&) {
        e3 = true;
    }
    EXPECT_EQ(e1 && e2 && e3, true);
}

TEST(QueueTest, PeekTestException) {
    CPP_Task3::Queue<int> q1 = CPP_Task3::Queue<int>();
    CPP_Task3::Queue<std::string> q2 = CPP_Task3::Queue<std::string>();
    CPP_Task3::Queue<std::vector<int> > q3 = CPP_Task3::Queue<std::vector<int> >();

    bool e1 = false;
    bool e2 = false;
    bool e3 = false;
    try {
        q1.peek();
    } catch (std::out_of_range&) {
        e1 = true;
    }
    try {
        q2.peek();
    } catch (std::out_of_range&) {
        e2 = true;
    }
    try {
        q3.peek();
    } catch (std::out_of_range&) {
        e3 = true;
    }
    EXPECT_EQ(e1 && e2 && e3, true);
}

TEST(QueueTest, PrintTest) {
    CPP_Task3::Queue<int> q1 = CPP_Task3::Queue<int>();

    for (int i = 0; i < 10; ++i) {
        q1.push(i);
    }

    std::cout << q1.toString();
    EXPECT_EQ(q1.toString(), "0 1 2 3 4 5 6 7 8 9 ");
}

int main(int argc, char **argv) {
    srand(time(NULL));
    testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}
