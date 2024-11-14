package com.mjiason.protegeserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OntologyAnnotationAPI {
    private String property;      // Annotation property (e.g., rdfs:comment)
    private String value;         // Annotation value
    private String language;      // Language tag (optional)
}

