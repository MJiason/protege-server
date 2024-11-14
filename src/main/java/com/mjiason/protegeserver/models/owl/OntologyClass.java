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
public class OntologyClass {
    private IRI iri;                   // IRI for OWL identification
    private String label;              // Human-readable label
    private String comment;            // Optional description
    private List<OWLAxiom> axioms;     // List of OWL axioms associated with this class

    // Methods to add axioms, annotations, etc.
}
