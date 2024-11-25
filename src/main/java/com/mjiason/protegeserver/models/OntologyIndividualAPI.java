package com.mjiason.protegeserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OntologyIndividualAPI {
    private String uniqueName;                   // Unique identifier for the individual
    private String className;           // The class this individual belongs to
    private String label;                        // Human-readable label
    private String comment;                      // Optional description
    // private List<OntologyObjectPropertyAPI> objectProperties; // Associated object properties
    // private List<OntologyDataPropertyAPI> dataProperties;     // Associated data properties

    // Object property relations: property name -> list of related individual names
    private Map<String, List<String>> objectPropertyRelations;

    // Data properties: property name -> property value(s)
    private Map<String, List<String>> filledDataProperties;
}

