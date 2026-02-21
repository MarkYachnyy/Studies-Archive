package ru.vsu.cs.iachnyi_m_a.java.service;

import lombok.Getter;
import ru.vsu.cs.iachnyi_m_a.java.entity.Seller;
import ru.vsu.cs.iachnyi_m_a.java.repository.SellerRepository;

public class SellerService {

    private SellerRepository repository;

    public SellerService(SellerRepository repository) {
        this.repository = repository;
    }

    public Seller getSellerById(Long id){
        return repository.findById(id).orElse(null);
    }
}
