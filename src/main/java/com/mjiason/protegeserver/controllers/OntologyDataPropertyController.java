package com.mjiason.protegeserver.controllers;

import com.mjiason.protegeserver.models.OntologyDataPropertyAPI;
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
@RequestMapping("/api/ontology/data-properties")
@Tag(name = "Ontology Data Property API", description = "CRUD operations for Ontology Data Properties")
public class OntologyDataPropertyController {

    @Autowired
    private OntologyStorageService storageService;

    @PostMapping
    @Operation(summary = "Create a Data Property", description = "Adds a new data property to the ontology with validation.")
    public ResponseEntity<OntologyDataPropertyAPI> createDataProperty(@RequestBody OntologyDataPropertyAPI dataProperty) {
        storageService.addDataProperty(dataProperty);
        return ResponseEntity.ok(dataProperty);
    }

    @GetMapping("/{uniqueName}")
    @Operation(summary = "Get a Data Property", description = "Retrieves a specific data property by unique name.")
    public ResponseEntity<OntologyDataPropertyAPI> getDataProperty(@PathVariable String uniqueName) {
        OntologyDataPropertyAPI property = storageService.getDataProperty(uniqueName);
        return ResponseEntity.ok(property);
    }

    @DeleteMapping("/{uniqueName}")
    @Operation(summary = "Delete a Data Property", description = "Removes a data property by unique name.")
    public ResponseEntity<Void> deleteDataProperty(@PathVariable String uniqueName) {
        storageService.removeDataProperty(uniqueName);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Get all Data Properties", description = "Retrieves all data properties in the ontology.")
    public ResponseEntity<Collection<OntologyDataPropertyAPI>> getAllDataProperties() {
        return ResponseEntity.ok(storageService.getAllDataProperties());
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


