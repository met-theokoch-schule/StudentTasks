
package com.example.studenttask.repository;

import com.example.studenttask.model.UnitTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitTitleRepository extends JpaRepository<UnitTitle, String> {
    List<UnitTitle> findByIsActiveTrueOrderByWeightAscNameAsc();
}
