package com.mjiason.protegeserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OntologyObjectPropertyAPI {
    private String uniqueName;                // Unique identifier for the property
    private List<OntologyClassAPI> domain;    // Domain classes
    private List<OntologyClassAPI> range;     // Range classes or datatype
    private String label;                     // Human-readable label
    private String comment;                   // Optional description
    private PropertyType propertyType;        // Type of property (e.g., FunctionalProperty)
}

