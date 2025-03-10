package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Associate;

@Repository
public interface AssociateRepository extends JpaRepository<Associate, Long> {
    List<Associate> findByProgrammingLanguage(String programmingLanguage);
    List<Associate> findAllByOrderByQualificationLevelAsc();
}