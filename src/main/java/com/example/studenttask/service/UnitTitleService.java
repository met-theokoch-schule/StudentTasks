package com.example.studenttask.service;

import com.example.studenttask.model.UnitTitle;
import com.example.studenttask.repository.UnitTitleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitTitleService {

    @Autowired
    private UnitTitleRepository unitTitleRepository;

    public List<UnitTitle> findAllActive() {
        return unitTitleRepository.findByIsActiveTrueOrderByName();
    }

    public UnitTitle findById(String id) {
        return unitTitleRepository.findById(id).orElse(null);
    }

    public UnitTitle save(UnitTitle unitTitle) {
        return unitTitleRepository.save(unitTitle);
    }

    public UnitTitle createIfNotExists(String id, String name, String description) {
        UnitTitle existing = unitTitleRepository.findById(id).orElse(null);
        if (existing == null) {
            existing = new UnitTitle(id, name, description);
            return unitTitleRepository.save(existing);
        }
        return existing;
    }
}