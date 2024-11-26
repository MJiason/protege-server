package com.mjiason.protegeserver.controllers;

import com.mjiason.protegeserver.models.OntologyIndividualAPI;
import com.mjiason.protegeserver.services.OntologyStorageService;
import com.mjiason.protegeserver.services.ValidationException;
import com.mjiason.protegeserver.services.EntityNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/ontology/individuals")
@Tag(name = "Ontology Individual API", description = "CRUD operations for Ontology Individuals")
public class OntologyIndividualController {

    @Autowired
    private OntologyStorageService storageService;

    @PostMapping
    @Operation(summary = "Create an Individual", description = "Adds a new individual to the ontology with validation.")
    public ResponseEntity<OntologyIndividualAPI> createIndividual(@RequestBody OntologyIndividualAPI individual) {
        storageService.addIndividual(individual);
        return ResponseEntity.ok(individual);
    }

    @GetMapping("/{uniqueName}")
    @Operation(summary = "Get an Individual", description = "Retrieves a specific individual by unique name.")
    public ResponseEntity<OntologyIndividualAPI> getIndividual(@PathVariable String uniqueName) {
        OntologyIndividualAPI individual = storageService.getIndividual(uniqueName);
        return ResponseEntity.ok(individual);
    }

    @DeleteMapping("/{uniqueName}")
    @Operation(summary = "Delete an Individual", description = "Removes an individual by unique name.")
    public ResponseEntity<Void> deleteIndividual(@PathVariable String uniqueName) {
        storageService.removeIndividual(uniqueName);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Get all Individuals", description = "Retrieves all individuals in the ontology.")
    public ResponseEntity<Collection<OntologyIndividualAPI>> getAllIndividuals() {
        return ResponseEntity.ok(storageService.getAllIndividuals());
    }

    // Exception handlers
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}


