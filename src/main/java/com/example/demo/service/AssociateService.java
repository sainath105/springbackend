package com.example.demo.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Associate;
import com.example.demo.repository.AssociateRepository;

@Service
public class AssociateService {
    @Autowired
    private AssociateRepository associateRepository;
   


    //  Fetch all associates from DB
    public List<Associate> getAllAssociates() {
        return associateRepository.findAll();
    }

    //  Save a new associate
    public Associate saveAssociate(Associate associate) {
    	System.out.println("Before save: " + associate.getDateAdded());

    	 associate.setDateAdded(LocalDate.now());  
    	if (associate.getId() == null) {
            associate.setVersion(0L);
        }
    	System.out.println("Before save: " + associate.getDateAdded());

        return associateRepository.save(associate);
        

    }

    //  Update an associate
    public Associate updateAssociate(Long id, Associate associate) {
        Optional<Associate> existingAssociate = associateRepository.findById(id);
        
        if (existingAssociate.isPresent()) {
            Associate updatedAssociate = existingAssociate.get();
            updatedAssociate.setName(associate.getName()); // Keep name unchanged
            updatedAssociate.setProgrammingLanguage(associate.getProgrammingLanguage()); // Update skill/swim lane
            updatedAssociate.setQualificationLevel(associate.getQualificationLevel()); // Update level
            return associateRepository.save(updatedAssociate); // Save changes to DB
        } else {
            throw new RuntimeException("Associate not found with id: " + id);
        }
    }

    //  Fetch all associates & sort by qualification level (L1 → L4)
    public List<Associate> getAllAssociatesSorted() {
    	return associateRepository.findAllByOrderByQualificationLevelAsc();
    }

    //  Get unique programming languages dynamically from DB
    public Set<String> getProgrammingLanguages() {
        return associateRepository.findAll()
                .stream()
                .map(Associate::getProgrammingLanguage)
                .collect(Collectors.toSet());
    }

    //  Group associates by qualification level & programming language
    public Map<String, Map<String, String>> getSkillBoardData() {
        List<Associate> associates = getAllAssociatesSorted();

        Map<String, Map<String, String>> skillMap = new LinkedHashMap<>();
        for (Associate associate : associates) {
            skillMap
                .computeIfAbsent(associate.getQualificationLevel(), k -> new HashMap<>())
                .put(associate.getProgrammingLanguage(), associate.getName());
        }
        return skillMap;
    }
    public Map<String, Long> getSkillDistribution() {
        return associateRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        Associate::getProgrammingLanguage,
                        Collectors.counting()
                ));
    }
    public void deleteAssociate(Long id) {
        associateRepository.deleteById(id);
    }
   

}
