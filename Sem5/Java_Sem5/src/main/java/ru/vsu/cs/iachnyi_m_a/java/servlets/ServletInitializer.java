package ru.vsu.cs.iachnyi_m_a.java.servlets;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.vsu.cs.iachnyi_m_a.java.context.ApplicationContextProvider;
import ru.vsu.cs.iachnyi_m_a.java.context.MyApplicationContext;

@WebListener
public class ServletInitializer implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ApplicationContextProvider.setContext(new MyApplicationContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
