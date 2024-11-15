package com.mjiason.protegeserver.controllers;

import com.mjiason.protegeserver.models.OntologyClassAPI;
import com.mjiason.protegeserver.services.OntologyStorageService;
import com.mjiason.protegeserver.services.ValidationException;
import com.mjiason.protegeserver.services.EntityNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collection;

@RestController
@RequestMapping("/api/ontology/classes")
@Tag(name = "Ontology Class API", description = "CRUD operations for Ontology Classes")
public class OntologyClassController {

    @Autowired
    private OntologyStorageService storageService;

    @PostMapping
    @Operation(summary = "Create an Ontology Class", description = "Adds a new class to the ontology with validation.")
    public ResponseEntity<Void> createClass(@RequestBody OntologyClassAPI ontologyClass) {
        storageService.addOntologyClass(ontologyClass);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{uniqueName}")
    @Operation(summary = "Get an Ontology Class", description = "Retrieves a specific ontology class by unique name.")
    public ResponseEntity<OntologyClassAPI> getClass(@PathVariable String uniqueName) {
        OntologyClassAPI ontologyClass = storageService.getOntologyClass(uniqueName);
        return ResponseEntity.ok(ontologyClass);
    }

    @DeleteMapping("/{uniqueName}")
    @Operation(summary = "Delete an Ontology Class", description = "Removes an ontology class from storage by unique name.")
    public ResponseEntity<Void> deleteClass(@PathVariable String uniqueName) {
        storageService.removeOntologyClass(uniqueName);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Get all Ontology Classes", description = "Retrieves all classes in the ontology.")
    public ResponseEntity<Collection<OntologyClassAPI>> getAllClasses() {
        return ResponseEntity.ok(storageService.getAllOntologyClasses());
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



