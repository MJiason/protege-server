package com.mjiason.protegeserver.controllers;

import com.mjiason.protegeserver.models.OWLClassShort;
import com.mjiason.protegeserver.services.OntologyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.semanticweb.owlapi.model.OWLClass;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/ontology")
@Tag(name = "String API", description = "API to manage strings")
public class OntologyManagerController {

    private final OntologyService ontologyService;

    public OntologyManagerController(OntologyService ontologyService) {
        this.ontologyService = ontologyService;
    }
    @GetMapping
    @CrossOrigin(origins = "http://localhost:4200")
    public List<OWLClassShort> getClasses() {
        ontologyService.getOntology();
        return ontologyService.getOntologyClasses();
    }

}
