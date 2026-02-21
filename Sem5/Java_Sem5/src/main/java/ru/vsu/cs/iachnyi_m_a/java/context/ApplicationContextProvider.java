package ru.vsu.cs.iachnyi_m_a.java.context;

public class ApplicationContextProvider {

    private static ApplicationContext applicationContext;

    public static void setContext(ApplicationContext context) {
        applicationContext = context;
    }

    public static ApplicationContext getContext() {
        return applicationContext;
    }
}
