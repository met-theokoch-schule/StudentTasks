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
        return unitTitleRepository.findByIsActiveTrueOrderByWeightAscNameAsc();
    }

    public UnitTitle findById(String id) {
        return unitTitleRepository.findById(id).orElse(null);
    }

    public UnitTitle save(UnitTitle unitTitle) {
        return unitTitleRepository.save(unitTitle);
    }

    public UnitTitle createOrUpdate(String id, String name, String description, int weight) {
        UnitTitle existing = unitTitleRepository.findById(id).orElse(null);
        if (existing == null) {
            existing = new UnitTitle(id, name, description, weight);
        } else {
            // Update existing properties
            existing.setName(name);
            existing.setDescription(description);
            existing.setWeight(weight);
        }
        return unitTitleRepository.save(existing);
    }
}