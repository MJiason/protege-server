package com.mjiason.protegeserver.models.owl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OntologyObjectProperty {
    private IRI iri;                       // IRI for OWL identification
    private IRI domainIRI;                 // IRI of the domain class
    private IRI rangeIRI;                  // IRI of the range class or datatype
    private String label;                  // Human-readable label
    private String comment;                // Optional description
    private String type;                   // Property type (e.g., FunctionalProperty)
    private List<OWLAxiom> axioms;         // Axioms for adding annotations or properties

    // Methods for adding or managing axioms, annotations, etc.
}

