package com.mjiason.protegeserver.controllers;

import com.mjiason.protegeserver.models.OntologyObjectPropertyAPI;
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
@RequestMapping("/api/ontology/object-properties")
@Tag(name = "Ontology Object Property API", description = "CRUD operations for Ontology Object Properties")
public class OntologyObjectPropertyController {

    @Autowired
    private OntologyStorageService storageService;

    @PostMapping
    @Operation(summary = "Create an Object Property", description = "Adds a new object property to the ontology with validation.")
    public ResponseEntity<Void> createObjectProperty(@RequestBody OntologyObjectPropertyAPI objectProperty) {
        storageService.addObjectProperty(objectProperty);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{uniqueName}")
    @Operation(summary = "Get an Object Property", description = "Retrieves a specific object property by unique name.")
    public ResponseEntity<OntologyObjectPropertyAPI> getObjectProperty(@PathVariable String uniqueName) {
        OntologyObjectPropertyAPI property = storageService.getObjectProperty(uniqueName);
        return ResponseEntity.ok(property);
    }

    @DeleteMapping("/{uniqueName}")
    @Operation(summary = "Delete an Object Property", description = "Removes an object property by unique name.")
    public ResponseEntity<Void> deleteObjectProperty(@PathVariable String uniqueName) {
        storageService.removeObjectProperty(uniqueName);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Get all Object Properties", description = "Retrieves all object properties in the ontology.")
    public ResponseEntity<Collection<OntologyObjectPropertyAPI>> getAllObjectProperties() {
        return ResponseEntity.ok(storageService.getAllObjectProperties());
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


