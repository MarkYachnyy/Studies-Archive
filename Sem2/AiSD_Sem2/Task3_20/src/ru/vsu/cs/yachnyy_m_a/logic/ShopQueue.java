package ru.vsu.cs.yachnyy_m_a.logic;

import ru.vsu.cs.yachnyy_m_a.logic.factories.PriorityQueueFactory;
import ru.vsu.cs.yachnyy_m_a.logic.factories.QueueFactory;

import java.util.*;

public class ShopQueue {

    private PriorityQueue<Customer> customers_left;
    private Queue<Customer> customers_at_cash;

    public ShopQueue(List<Customer> customers, QueueFactory<Customer> nFactory, PriorityQueueFactory<Customer> pFactory){
        ArrayList<Customer> customers_copy = new ArrayList<>(customers);
        Collections.shuffle(customers_copy);
        customers_copy.sort(Comparator.comparingInt(c -> c.getArrivalTime() + c.getChoosingTime()));
        customers_at_cash = nFactory.create();
        customers_left = pFactory.create(Customer.CashComparator());
        customers_left.addAll(customers_copy);
    }

    public ShopQueue(Customer[] customers, QueueFactory<Customer> nFactory, PriorityQueueFactory<Customer> pFactory){
        this(Arrays.asList(customers), nFactory, pFactory);
    }

    public List<CustomerData> getExitTimeList(){
        int time = 0;
        ArrayList<CustomerData> list = new ArrayList<>();
        while(!customers_left.isEmpty()){

            Customer next = customers_left.poll();
            customers_at_cash.add(next);
            time = next.getArrivalTime() + next.getChoosingTime();

            while (!customers_at_cash.isEmpty()){
                Customer served = customers_at_cash.poll();
                time += served.getGoodsCount();
                list.add(new CustomerData(served, time));

                Customer arrived = customers_left.isEmpty() ? null : customers_left.peek();
                while(arrived != null && arrived.getChoosingTime() + arrived.getArrivalTime() <= time){
                    customers_at_cash.add(customers_left.poll());
                    arrived = customers_left.isEmpty() ? null : customers_left.peek();
                }
            }
        }
        return list;
    }

}
