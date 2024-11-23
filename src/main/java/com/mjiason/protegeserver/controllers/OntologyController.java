package com.mjiason.protegeserver.controllers;

import com.mjiason.protegeserver.models.OntologyAPI;
import com.mjiason.protegeserver.services.OntologyStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ontology")
@Tag(name = "Ontology Management", description = "Operations for loading and saving ontology files")
public class OntologyController {
    private final OntologyStorageService ontologyStorageService;

    public OntologyController(OntologyStorageService ontologyStorageService) {
        this.ontologyStorageService = ontologyStorageService;
    }

    @GetMapping("/")
    @Operation(summary = "Get ontology", description = "Retrieves the current ontology.")
    public ResponseEntity<OntologyAPI> getOntology() {
        OntologyAPI ontology = ontologyStorageService.getOntology();
        if (ontology == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(ontology);
    }
}
