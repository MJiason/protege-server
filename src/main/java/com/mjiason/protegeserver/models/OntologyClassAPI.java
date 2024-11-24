package com.mjiason.protegeserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OntologyClassAPI {
    private String uniqueName;                // Unique identifier for the class
    private String parentClass;                // Parent class for the class
    private String label;                     // Human-readable label
    private String comment;                   // Description or comment
}

