package com.mjiason.protegeserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OntologyObjectPropertyAPI {
    private String uniqueName;                // Unique identifier for the property
    private List<String> domain;    // Domain classes
    private List<String> range;     // Range classes or datatype
    private String label;                     // Human-readable label
    private String comment;                   // Optional description
    private List<PropertyType> propertyTypes = new ArrayList<>();        // Type of property (e.g., FunctionalProperty)

    // Check if the property is functional
    public boolean isFunctional() {
        return propertyTypes.contains(PropertyType.FunctionalProperty);
    }

    // Check if the property is transitive
    public boolean isTransitive() {
        return propertyTypes.contains(PropertyType.TransitiveProperty);
    }

    // Check if the property is inverse functional
    public boolean isInverseFunctional() {
        return propertyTypes.contains(PropertyType.InverseFunctionalProperty);
    }

    // Check if the property is symmetric
    public boolean isSymmetric() {
        return propertyTypes.contains(PropertyType.SymmetricProperty);
    }

    // Check if the property is asymmetric
    public boolean isAsymmetric() {
        return propertyTypes.contains(PropertyType.AsymmetricProperty);
    }

    // Check if the property is reflexive
    public boolean isReflexive() {
        return propertyTypes.contains(PropertyType.ReflexiveProperty);
    }

    // Check if the property is irreflexive
    public boolean isIrreflexive() {
        return propertyTypes.contains(PropertyType.IrreflexiveProperty);
    }
}

