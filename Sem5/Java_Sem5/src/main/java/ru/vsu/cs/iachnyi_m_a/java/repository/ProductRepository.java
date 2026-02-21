package ru.vsu.cs.iachnyi_m_a.java.repository;

import ru.vsu.cs.iachnyi_m_a.java.entity.Product;

import java.util.List;

public interface ProductRepository extends DataRepository<Product, Long> {
    public List<Product> findAllBySellerId(Long sellerId);
}
