package com.mjiason.protegeserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OntologyIndividualAPI {
    private String uniqueName;                   // Unique identifier for the individual
    private OntologyClassAPI classAPI;           // The class this individual belongs to
    private String label;                        // Human-readable label
    private String comment;                      // Optional description
    private List<OntologyObjectPropertyAPI> objectProperties; // Associated object properties
    private List<OntologyDataPropertyAPI> dataProperties;     // Associated data properties
}

