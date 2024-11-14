package com.mjiason.protegeserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OntologyClassAPI {
    private String uniqueName;                // Unique identifier for the class
    private String label;                     // Human-readable label
    private String comment;                   // Description or comment
    private List<OntologyAnnotationAPI> annotations;  // List of annotations
}

