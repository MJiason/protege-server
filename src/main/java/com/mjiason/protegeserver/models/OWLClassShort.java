package com.mjiason.protegeserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OWLClassShort {
    private String className;
    private String parentName;
    private String comment;
}
