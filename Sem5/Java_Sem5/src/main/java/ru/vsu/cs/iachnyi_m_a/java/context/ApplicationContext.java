package ru.vsu.cs.iachnyi_m_a.java.context;

public interface ApplicationContext {
    <T> T getBean(Class<T> clazz);
}
