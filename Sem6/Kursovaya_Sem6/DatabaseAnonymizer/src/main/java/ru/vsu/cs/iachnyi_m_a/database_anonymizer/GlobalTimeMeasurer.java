package ru.vsu.cs.iachnyi_m_a.database_anonymizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class GlobalTimeMeasurer {
    private static int c = 0;
    private static long initTime;
    public static boolean finished = false;
    private static Runnable runnable;

    public static void setInitTime() {
        finished = false;
        GlobalTimeMeasurer.initTime = System.currentTimeMillis();
    }

    public static void setOnFinishedLambda(Runnable r) {
        runnable = r;
    }

    public static void tick() {
        if (c < 5) {
            c++;
        } else {
            runnable.run();
            try {
                Files.write(Paths.get("./output.txt"), (String.valueOf(System.currentTimeMillis() - initTime) + '\n').getBytes(),
                        StandardOpenOption.WRITE,
                        StandardOpenOption.APPEND,
                        StandardOpenOption.CREATE);
            } catch (IOException e) {

            }
            c = 0;
            finished = true;
        }
    }
}
