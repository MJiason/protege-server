package com.mjiason.protegeserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OntologyDataPropertyAPI {
    private String uniqueName;                // Unique identifier for the data property
    private List<String> domain;              // Domain classes
    private String range;                     // Range datatype (e.g., xsd:string)
    private String label;                     // Human-readable label
    private String comment;                   // Optional description
}

