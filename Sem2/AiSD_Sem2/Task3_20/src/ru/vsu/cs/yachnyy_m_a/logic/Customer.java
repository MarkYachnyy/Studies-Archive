package ru.vsu.cs.yachnyy_m_a.logic;

import ru.vsu.cs.yachnyy_m_a.util.ArrayUtils;
import ru.vsu.cs.yachnyy_m_a.util.SwingUtils;

import java.util.Comparator;
import java.util.Objects;

public class Customer {
    private int arrival_time;
    private int choosing_time;
    private int goods_count;

    public Customer(int arrival_time, int choosing_time, int goods_count) {
        if(choosing_time < 1 || goods_count < 1 || arrival_time < 0) throw new IllegalArgumentException();
        this.arrival_time = arrival_time;
        this.choosing_time = choosing_time;
        this.goods_count = goods_count;
    }

    public int getArrivalTime() {
        return arrival_time;
    }

    public int getChoosingTime() {
        return choosing_time;
    }

    public int getGoodsCount() {
        return goods_count;
    }

    public void setGoods_count(int goods_count) {
        this.goods_count = goods_count;
    }

    public static Customer[] loadCustomerArrayFromFile(String fileName){
        int[][] matrix = ArrayUtils.readIntArray2FromFile(fileName);
        Customer[] res = new Customer[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            res[i] = new Customer(matrix[i][0], matrix[i][1], matrix[i][2]);
        }
        return res;
    }

    public static int[][] toIntMatrix(Customer[] array){
        int[][] res = new int[array.length][3];
        for (int i = 0; i < array.length; i++) {
            res[i][0] = array[i].getArrivalTime();
            res[i][1] = array[i].getChoosingTime();
            res[i][2] = array[i].getGoodsCount();
        }
        return res;
    }

    public static Customer[] readCustomerArrayFromIntMatrix(int[][] matrix){
        try{
            Customer[] res = new Customer[matrix.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = new Customer(matrix[i][0], matrix[i][1], matrix[i][2]);
            }
            return res;
        } catch (Exception e){
            SwingUtils.showErrorMessageBox(e);
        }
        return null;
    }

    public static Comparator<Customer> CashComparator(){
        return Comparator.comparingInt(o -> o.getArrivalTime() + o.getChoosingTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return arrival_time == customer.arrival_time && choosing_time == customer.choosing_time && goods_count == customer.goods_count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(arrival_time, choosing_time, goods_count);
    }

    @Override
    public String toString() {
        return String.format("arrival time: %s\nchoosing time: %s\ngoods count: %s", arrival_time, choosing_time, goods_count);
    }
}
