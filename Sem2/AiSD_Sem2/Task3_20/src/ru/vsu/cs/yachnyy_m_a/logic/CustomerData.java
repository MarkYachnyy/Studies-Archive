package ru.vsu.cs.yachnyy_m_a.logic;

public class CustomerData {
    private Customer customer;
    private int leaving_time;

    public CustomerData(Customer customer, int leaving_time) {
        this.customer = customer;
        this.leaving_time = leaving_time;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getLeaving_time() {
        return leaving_time;
    }

    @Override
    public String toString() {
        return customer.toString() + '\n' + String.format("leaving time :%s", leaving_time);
    }
}
