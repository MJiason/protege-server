package com.mjiason.protegeserver.controllers;

import com.mjiason.protegeserver.services.OntologyStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/ontology/file")
@Tag(name = "Ontology File API", description = "Operations for loading and saving ontology files")
public class OntologyFileController {

    @Autowired
    private OntologyStorageService storageService;

    @PostMapping("/upload")
    @Operation(summary = "Upload an ontology file", description = "Loads an ontology from an OWL file.")
    public ResponseEntity<Void> uploadOntologyFile(@RequestParam("file") MultipartFile file) {
        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file.getInputStream());
            storageService.setOntology(ontology);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/download")
    @Operation(summary = "Download the ontology file", description = "Saves the current in-memory ontology to an OWL file.")
    public ResponseEntity<byte[]> downloadOntologyFile() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            manager.saveOntology(storageService.getOntology(), outputStream);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ontology.owl")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(outputStream.toByteArray());
        } catch (OWLOntologyStorageException | OWLOntologyCreationException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}


