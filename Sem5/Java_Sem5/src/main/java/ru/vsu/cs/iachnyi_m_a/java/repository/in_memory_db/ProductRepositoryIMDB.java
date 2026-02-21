package ru.vsu.cs.iachnyi_m_a.java.repository.in_memory_db;

import ru.vsu.cs.iachnyi_m_a.java.entity.Product;
import ru.vsu.cs.iachnyi_m_a.java.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductRepositoryIMDB implements ProductRepository {

    public ProductRepositoryIMDB(InMemoryDatabase database) {
        this.inMemoryDatabase = database;
    }

    private InMemoryDatabase inMemoryDatabase;

    @Override
    public Optional<Product> findById(Long id) {
        return inMemoryDatabase.getAllProducts().stream().filter(product -> product.getId() == id).findFirst();
    }

    @Override
    public List<Product> findAll() {
        return inMemoryDatabase.getAllProducts();
    }

    @Override
    public Product save(Product entity) {
        return inMemoryDatabase.updateProduct(entity);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<Product> findAllBySellerId(Long sellerId) {
        return inMemoryDatabase.getAllProducts().stream().filter(product -> product.getSellerId() == sellerId).collect(Collectors.toList());
    }
}
