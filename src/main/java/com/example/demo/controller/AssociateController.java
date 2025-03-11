package com.example.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Associate;
import com.example.demo.service.AssociateService;

/**
 * REST Controller for handling Associate-related requests.
 * Provides endpoints to retrieve, create, and update associates.
 */
@RestController
@RequestMapping("/api/associates")
@CrossOrigin(origins = "https://sainath105.github.io") // Allows frontend requests from Angular app
public class AssociateController {
    
    @Autowired
    private AssociateService associateService; // Injecting AssociateService

    /**
     * Retrieves all associates sorted by qualification level.
     * @return List of Associate objects.
     */
    @GetMapping
    public List<Associate> getAllAssociates() {
        return associateService.getAllAssociatesSorted(); // Use sorted data
    }
    
    /**
     * Retrieves all unique programming languages from associates.
     */
    @GetMapping("/programming-languages")
    public Set<String> getProgrammingLanguages() {
        return associateService.getProgrammingLanguages(); //  Use service method
    }

    /**
     * Retrieves all unique qualification levels from associates.
     */
    @GetMapping("/qualification-levels")
    public Set<String> getQualificationLevels() {
        return associateService.getAllAssociatesSorted()
                .stream()
                .map(Associate::getQualificationLevel)
                .collect(Collectors.toCollection(LinkedHashSet::new)); //  Maintain order (L1 → L4)
    }

    /**
     * Creates a new associate and saves it to the database.
     * @param associate The associate object to be created.
     * @return The saved Associate object.
     */
    @PostMapping
    public Associate createAssociate(@RequestBody Associate associate) {
    	 System.out.println("Received new associate: " + associate);
    	 if (associate.getId() == null) {
    	        associate.setVersion(0L);
    	    }
    	 
        return associateService.saveAssociate(associate);
    }

    /**
     * Updates an existing associate with the given ID.
     * @param id The ID of the associate to update.
     * @param associate The updated associate object.
     * @return The updated Associate object.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Associate> updateAssociate(@PathVariable Long id, @RequestBody Associate associate) {
        try {
            Associate updatedAssociate = associateService.updateAssociate(id, associate);
            return ResponseEntity.ok(updatedAssociate);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssociate(@PathVariable Long id) {
        associateService.deleteAssociate(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * Generates a downloadable Kanban Board as an HTML file.
     * @return ResponseEntity containing HTML content for download.
     */
    @GetMapping("/download-kanban")
    public ResponseEntity<String> downloadKanbanAsHtml() {
        //Fetch sorted data
        List<Associate> associates = associateService.getAllAssociatesSorted();
        Set<String> programmingLanguages = associateService.getProgrammingLanguages();

        // Group associates by qualification level & programming language
        Map<String, Map<String, String>> kanbanData = new LinkedHashMap<>();
        for (Associate associate : associates) {
            kanbanData
                .computeIfAbsent(associate.getQualificationLevel(), k -> new LinkedHashMap<>())
                .put(associate.getProgrammingLanguage(), associate.getName());
        }

        //  Generate dynamic table headers
        String headers = programmingLanguages.stream()
            .map(lang -> "<th>" + lang + "</th>")
            .collect(Collectors.joining());

        //  Generate dynamic table rows
        String tableRows = kanbanData.entrySet().stream()
            .map(entry -> {
                String qualificationLevel = entry.getKey();
                Map<String, String> skills = entry.getValue();
                
                String skillCells = programmingLanguages.stream()
                    .map(lang -> "<td>" + skills.getOrDefault(lang, "-") + "</td>")
                    .collect(Collectors.joining());

                return "<tr><td>" + qualificationLevel + "</td>" + skillCells + "</tr>";
            })
            .collect(Collectors.joining());

        //  Full HTML with dynamic content
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Kanban Board</title>
                <style>
                    body { font-family: Arial, sans-serif; text-align: center; }
                    .kanban-container { width: 80%; margin: auto; border: 1px solid black; padding: 20px; }
                    .kanban-table { width: 100%; border-collapse: collapse; }
                    .kanban-table th, .kanban-table td { border: 1px solid black; padding: 8px; }
                    .kanban-table th { background-color: #007bc0; color: white; }
                </style>
            </head>
            <body>
                <div class='kanban-container'>
                    <h2>Developers SkillBoard</h2>
                    <table class='kanban-table'>
                        <tr>
                            <th>Qualification Level</th>
                            """ + headers + """
                        </tr>
                        """ + tableRows + """
                    </table>
                </div>
            </body>
            </html>
        """;

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=kanban-board.html")
                .body(htmlContent);
    }
    @GetMapping("/stats")
    public Map<String, Object> getKanbanStats() {
        List<Associate> associates = associateService.getAllAssociates();

        // Count associates per qualification level
        Map<String, Long> levelCount = associates.stream()
                .collect(Collectors.groupingBy(Associate::getQualificationLevel, Collectors.counting()));

        // Count associates per programming language
        Map<String, Long> languageCount = associates.stream()
                .collect(Collectors.groupingBy(Associate::getProgrammingLanguage, Collectors.counting()));

        Map<String, Object> stats = new HashMap<>();
        stats.put("levelCount", levelCount);
        stats.put("languageCount", languageCount);

        return stats;
    }
    @GetMapping("/skill-distribution")
    public ResponseEntity<Map<String, Long>> getSkillDistribution() {
        Map<String, Long> skillDistribution = associateService.getSkillDistribution();
        return ResponseEntity.ok(skillDistribution);
    }
    
}

