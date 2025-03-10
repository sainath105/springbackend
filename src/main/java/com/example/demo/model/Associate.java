package com.example.demo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity
public class Associate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String programmingLanguage; 
    private String qualificationLevel;
    @Version
    private Long version;
    @Column(name = "date_added")
    private LocalDate dateAdded;
    // Default constructor
    public Associate() {
        this.version = 0L; // Initialize version field
    }

    // Parameterized constructor
    public Associate(String name, String programmingLanguage, String qualificationLevel, boolean isEditing) {
        this.name = name;
        this.programmingLanguage = programmingLanguage;
        this.qualificationLevel = qualificationLevel;
        
        this.version = 0L; // Initialize version field
        this.dateAdded = LocalDate.now();
    }
    
    

	// Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public String getQualificationLevel() {
        return qualificationLevel;
    }

    public void setQualificationLevel(String qualificationLevel) {
        this.qualificationLevel = qualificationLevel;
    }
    public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public LocalDate getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(LocalDate dateAdded) {
		this.dateAdded = dateAdded;
	}
	
	
	
}