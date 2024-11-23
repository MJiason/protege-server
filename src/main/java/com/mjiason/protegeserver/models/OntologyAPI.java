package com.mjiason.protegeserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OntologyAPI {
    private String uniqueName; // Unique identifier for the ontology, e.g., "pizza"
    private String baseIRI;    // Base IRI for the ontology
}
