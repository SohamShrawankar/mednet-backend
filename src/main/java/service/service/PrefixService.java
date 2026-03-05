package com.mednet.service;

import com.mednet.entity.Prefix;
import com.mednet.repository.PrefixRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrefixService {

    private final PrefixRepository prefixRepository;

    public PrefixService(PrefixRepository prefixRepository) {
        this.prefixRepository = prefixRepository;
    }

    public Prefix save(Prefix prefix) {
        return prefixRepository.save(prefix);
    }

    public List<Prefix> findAll() {
        return prefixRepository.findAll();
    }

    public void delete(Long id) {
        prefixRepository.deleteById(id);
    }
}
