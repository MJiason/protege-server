package com.mjiason.protegeserver.services;

import com.mjiason.protegeserver.models.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OntologyStorageService {

    private final OntologyAPI ontology = new OntologyAPI();
    private final Map<String, OntologyClassAPI> ontologyClasses = new HashMap<>();
    private final Map<String, OntologyObjectPropertyAPI> objectProperties = new HashMap<>();
    private final Map<String, OntologyDataPropertyAPI> dataProperties = new HashMap<>();
    private final Map<String, OntologyIndividualAPI> individuals = new HashMap<>();

    private OWLOntology owlOntology;
    private OWLDataFactory dataFactory;

    private DefaultPrefixManager prefixManager;

    private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

    public OntologyStorageService() {
        try {
            File file = new File("src/main/resources/api-ontology.owl");
            OWLOntology loadedOntology = loadOWLOntology(file);
            populateOntologyFromOWL(loadedOntology);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OntologyAPI getOntology() {
        return ontology;
    }

    public void addOntologyClass(OntologyClassAPI ontologyClass) {
        validateOntologyClass(ontologyClass);
        ontologyClasses.put(ontologyClass.getUniqueName(), ontologyClass);
    }

    public OntologyClassAPI getOntologyClass(String uniqueName) {
        if (!ontologyClasses.containsKey(uniqueName)) {
            throw new EntityNotFoundException("Ontology Class not found: " + uniqueName);
        }
        return ontologyClasses.get(uniqueName);
    }

    public void removeOntologyClass(String uniqueName) {
        if (!ontologyClasses.containsKey(uniqueName)) {
            throw new EntityNotFoundException("Ontology Class not found: " + uniqueName);
        }
        ontologyClasses.remove(uniqueName);
    }

    public Collection<OntologyClassAPI> getAllOntologyClasses() {
        return ontologyClasses.values();
    }

    public void addObjectProperty(OntologyObjectPropertyAPI objectProperty) {
        validateObjectProperty(objectProperty);
        objectProperties.put(objectProperty.getUniqueName(), objectProperty);
    }

    public OntologyObjectPropertyAPI getObjectProperty(String uniqueName) {
        if (!objectProperties.containsKey(uniqueName)) {
            throw new EntityNotFoundException("Object Property not found: " + uniqueName);
        }
        return objectProperties.get(uniqueName);
    }

    public void removeObjectProperty(String uniqueName) {
        if (!objectProperties.containsKey(uniqueName)) {
            throw new EntityNotFoundException("Object Property not found: " + uniqueName);
        }
        objectProperties.remove(uniqueName);
    }

    public Collection<OntologyObjectPropertyAPI> getAllObjectProperties() {
        return objectProperties.values();
    }

    public void addDataProperty(OntologyDataPropertyAPI dataProperty) {
        validateDataProperty(dataProperty);
        dataProperties.put(dataProperty.getUniqueName(), dataProperty);
    }

    public OntologyDataPropertyAPI getDataProperty(String uniqueName) {
        if (!dataProperties.containsKey(uniqueName)) {
            throw new EntityNotFoundException("Data Property not found: " + uniqueName);
        }
        return dataProperties.get(uniqueName);
    }

    public void removeDataProperty(String uniqueName) {
        if (!dataProperties.containsKey(uniqueName)) {
            throw new EntityNotFoundException("Data Property not found: " + uniqueName);
        }
        dataProperties.remove(uniqueName);
    }

    public Collection<OntologyDataPropertyAPI> getAllDataProperties() {
        return dataProperties.values();
    }

    public void addIndividual(OntologyIndividualAPI individual) {
        validateIndividual(individual);
        individuals.put(individual.getUniqueName(), individual);
    }

    public OntologyIndividualAPI getIndividual(String uniqueName) {
        if (!individuals.containsKey(uniqueName)) {
            throw new EntityNotFoundException("Individual not found: " + uniqueName);
        }
        return individuals.get(uniqueName);
    }

    public void removeIndividual(String uniqueName) {
        if (!individuals.containsKey(uniqueName)) {
            throw new EntityNotFoundException("Individual not found: " + uniqueName);
        }
        individuals.remove(uniqueName);
    }

    public Collection<OntologyIndividualAPI> getAllIndividuals() {
        return individuals.values();
    }

    // Validation methods
    private void validateOntologyClass(OntologyClassAPI ontologyClass) {
        // Validate that uniqueName is not null or empty
        if (ontologyClass.getUniqueName() == null || ontologyClass.getUniqueName().isEmpty()) {
            throw new ValidationException("Unique name for Ontology Class cannot be null or empty.");
        }

        // Check if the uniqueName already exists in the ontologyClasses map
        if (ontologyClasses.containsKey(ontologyClass.getUniqueName())) {
            throw new ValidationException("Unique name '" + ontologyClass.getUniqueName() + "' already exists.");
        }

        // Validate that the parent class exists in the ontologyClasses map
        String parentClass = ontologyClass.getParentClass();
        if (parentClass != null && !parentClass.isEmpty() && !ontologyClasses.containsKey(parentClass)) {
            throw new ValidationException("Parent class '" + parentClass + "' does not exist in the ontology.");
        }

        // Add any additional validations if needed.
    }

    private void validateObjectProperty(OntologyObjectPropertyAPI objectProperty) {
        if (objectProperty.getUniqueName() == null || objectProperty.getUniqueName().isEmpty()) {
            throw new ValidationException("Unique name for Object Property cannot be null or empty");
        }
        // Add other validations as needed.
    }

    private void validateDataProperty(OntologyDataPropertyAPI dataProperty) {
        if (dataProperty.getUniqueName() == null || dataProperty.getUniqueName().isEmpty()) {
            throw new ValidationException("Unique name for Data Property cannot be null or empty");
        }
        // Add other validations as needed.
    }

    private void validateIndividual(OntologyIndividualAPI individual) {
        if (individual.getUniqueName() == null || individual.getUniqueName().isEmpty()) {
            throw new ValidationException("Unique name for Individual cannot be null or empty");
        }
        // Add other validations as needed.
    }


    /**
     * Retrieves the comment (rdfs:comment) of a given OWL class.
     *
     * @param owlClass The OWL class.
     * @return The comment as a String, or an empty string if not found.
     */
    private String getClassComment(OWLClass owlClass) {
        for (OWLAnnotation annotation : EntitySearcher.getAnnotations(owlClass, owlOntology, dataFactory.getRDFSComment()).toList())
            if (annotation.getValue() instanceof OWLLiteral) {
                return ((OWLLiteral) annotation.getValue()).getLiteral();
            }

        return "";
    }

    private String getLabelComment(OWLClass owlClass) {
        for (OWLAnnotation annotation : EntitySearcher.getAnnotations(owlClass, owlOntology, dataFactory.getRDFSLabel()).toList())
            if (annotation.getValue() instanceof OWLLiteral) {
                return ((OWLLiteral) annotation.getValue()).getLiteral();
            }

        return "";
    }

    public OWLOntology loadOWLOntology(File file) {
        try {
            manager = OWLManager.createOWLOntologyManager();
            return manager.loadOntologyFromOntologyDocument(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public OWLOntology loadOWLOntology(InputStream inputStream) {
        try {
            manager = OWLManager.createOWLOntologyManager();
            return manager.loadOntologyFromOntologyDocument(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void populateOntologyFromOWL(OWLOntology loadedOWLOntology) {
        try {
            dataFactory = manager.getOWLDataFactory();
            owlOntology = loadedOWLOntology;

            String defaultPrefix = "http://www.example.com/ontologies/UnnamedOntology.owl#";
            ontology.setUniqueName("UnnamedOntology");

            OWLDocumentFormat format = manager.getOntologyFormat(owlOntology);
            if (format instanceof PrefixDocumentFormat prefixFormat) {
                if (prefixFormat.getDefaultPrefix() != null) {
                    defaultPrefix = prefixFormat.getDefaultPrefix();
                }

                prefixManager = new DefaultPrefixManager(defaultPrefix);
                prefixManager.setPrefix("owl:", "http://www.w3.org/2002/07/owl#");

                ontology.setBaseIRI(defaultPrefix);

                ontology.setUniqueName(owlOntology.getOntologyID().getOntologyIRI()
                        .map(IRI::getShortForm)
                        .orElse("UnnamedOntology"));

                System.out.println("Prefix: " + prefixFormat.getDefaultPrefix());
                System.out.println("owlOntology.getOntologyID().getOntologyIRI(): " + owlOntology.getOntologyID().getOntologyIRI().get());
            } else {
                System.out.println("The ontology format does not support prefixes.");
            }

            setOWLOntology(owlOntology);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Populate the in-memory data structures from an OWLOntology
    public void setOWLOntology(OWLOntology owlOntology) {
        ontologyClasses.clear();
        objectProperties.clear();
        dataProperties.clear();
        individuals.clear();

        owlOntology.classesInSignature().forEach(owlClass -> {
            IRI iri = owlClass.getIRI();
            String name = iri.getShortForm();
            String parentClass = getParentClassName(owlClass);

            String label = getAnnotation(owlClass, owlOntology, OWLRDFVocabulary.RDFS_LABEL);
            String comment = getAnnotation(owlClass, owlOntology, OWLRDFVocabulary.RDFS_COMMENT);
            ontologyClasses.put(name, new OntologyClassAPI(name, parentClass, label, comment));
        });

        owlOntology.objectPropertiesInSignature().forEach(owlObjectProperty -> {
            IRI iri = owlObjectProperty.getIRI();
            String name = iri.getShortForm();
            String label = getAnnotation(owlObjectProperty, owlOntology, OWLRDFVocabulary.RDFS_LABEL);
            String comment = getAnnotation(owlObjectProperty, owlOntology, OWLRDFVocabulary.RDFS_COMMENT);

            // Retrieve domain and range information as Lists of OntologyClassAPI
            List<String> domain = getDomainClasses(owlObjectProperty, owlOntology);
            List<String> range = getRangeClasses(owlObjectProperty, owlOntology);

            List<PropertyType> propertyTypes = getPropertyTypes(owlObjectProperty, owlOntology);

            // Initialize object properties with domain and range as lists
            objectProperties.put(name, new OntologyObjectPropertyAPI(name, domain, range, label, comment, propertyTypes));
        });

        // Populate Ontology Data Properties
        owlOntology.dataPropertiesInSignature().forEach(owlDataProperty -> {
            IRI iri = owlDataProperty.getIRI();
            String name = iri.getShortForm();
            String label = getAnnotation(owlDataProperty, owlOntology, OWLRDFVocabulary.RDFS_LABEL);
            String comment = getAnnotation(owlDataProperty, owlOntology, OWLRDFVocabulary.RDFS_COMMENT);

            // Retrieve domain and range for the data property
            List<String> domain = getDomainClasses(owlDataProperty, owlOntology);
            String range = getDataRange(owlDataProperty, owlOntology);

            // Initialize data properties with domain and range
            dataProperties.put(name, new OntologyDataPropertyAPI(name, domain, range, label, comment));
        });

        // Populate Ontology Individuals
        owlOntology.individualsInSignature().forEach(owlIndividual -> {
            IRI iri = owlIndividual.getIRI();
            String name = iri.getShortForm();
            String label = getAnnotation(owlIndividual, owlOntology, OWLRDFVocabulary.RDFS_LABEL);
            String comment = getAnnotation(owlIndividual, owlOntology, OWLRDFVocabulary.RDFS_COMMENT);


            OntologyClassAPI classAPI = new OntologyClassAPI();

            // Retrieve associated object properties for the individual
            List<OntologyObjectPropertyAPI> individualObjectProperties = getIndividualObjectProperties(owlIndividual, owlOntology);

            // Retrieve associated data properties for the individual
            List<OntologyDataPropertyAPI> individualDataProperties = getIndividualDataProperties(owlIndividual, owlOntology);

            // Create and add the individual to the storage
            individuals.put(name, new OntologyIndividualAPI(name, "", label, comment, individualObjectProperties, individualDataProperties));
        });
    }

    private List<PropertyType> getPropertyTypes(OWLObjectProperty property, OWLOntology ontology) {
        List<PropertyType> propertyTypes = new ArrayList<>();

        // Check if the property is functional
        if (ontology.getFunctionalObjectPropertyAxioms(property).size() > 0) {
            propertyTypes.add(PropertyType.FunctionalProperty);
        }

        // Check if the property is inverse functional
        if (ontology.getInverseFunctionalObjectPropertyAxioms(property).size() > 0) {
            propertyTypes.add(PropertyType.InverseFunctionalProperty);
        }

        // Check if the property is transitive
        if (ontology.getTransitiveObjectPropertyAxioms(property).size() > 0) {
            propertyTypes.add(PropertyType.TransitiveProperty);
        }

        // Check if the property is symmetric
        if (ontology.getSymmetricObjectPropertyAxioms(property).size() > 0) {
            propertyTypes.add(PropertyType.SymmetricProperty);
        }

        // Check if the property is asymmetric
        if (ontology.getAsymmetricObjectPropertyAxioms(property).size() > 0) {
            propertyTypes.add(PropertyType.AsymmetricProperty);
        }

        // Check if the property is reflexive
        if (ontology.getReflexiveObjectPropertyAxioms(property).size() > 0) {
            propertyTypes.add(PropertyType.ReflexiveProperty);
        }

        // Check if the property is irreflexive
        if (ontology.getIrreflexiveObjectPropertyAxioms(property).size() > 0) {
            propertyTypes.add(PropertyType.IrreflexiveProperty);
        }

        return propertyTypes;
    }

    // Convert in-memory data structures to a new OWLOntology and return it
    public OWLOntology getOWLOntology() throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.createOntology();

        ontologyClasses.values().forEach(ontologyClass -> {
            OWLClass owlClass = manager.getOWLDataFactory().getOWLClass(IRI.create(ontologyClass.getUniqueName()));
            addAnnotations(ontology, owlClass, ontologyClass.getLabel(), ontologyClass.getComment());
            manager.addAxiom(ontology, manager.getOWLDataFactory().getOWLDeclarationAxiom(owlClass));
        });

        objectProperties.values().forEach(objectProperty -> {
            OWLObjectProperty owlObjectProperty = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(objectProperty.getUniqueName()));
            addAnnotations(ontology, owlObjectProperty, objectProperty.getLabel(), objectProperty.getComment());
            // Define domain and range for the property
            objectProperty.getDomain().forEach(domainClass -> {
                OWLClass domain = manager.getOWLDataFactory().getOWLClass(IRI.create(domainClass));
                manager.addAxiom(ontology, manager.getOWLDataFactory().getOWLObjectPropertyDomainAxiom(owlObjectProperty, domain));
            });
            objectProperty.getRange().forEach(rangeClass -> {
                OWLClass range = manager.getOWLDataFactory().getOWLClass(IRI.create(rangeClass));
                manager.addAxiom(ontology, manager.getOWLDataFactory().getOWLObjectPropertyRangeAxiom(owlObjectProperty, range));
            });
            manager.addAxiom(ontology, manager.getOWLDataFactory().getOWLDeclarationAxiom(owlObjectProperty));
        });

        dataProperties.values().forEach(dataProperty -> {
            OWLDataProperty owlDataProperty = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(dataProperty.getUniqueName()));
            addAnnotations(ontology, owlDataProperty, dataProperty.getLabel(), dataProperty.getComment());
            manager.addAxiom(ontology, manager.getOWLDataFactory().getOWLDeclarationAxiom(owlDataProperty));
        });

        individuals.values().forEach(individual -> {
            OWLNamedIndividual owlIndividual = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(individual.getUniqueName()));
            addAnnotations(ontology, owlIndividual, individual.getLabel(), individual.getComment());
            manager.addAxiom(ontology, manager.getOWLDataFactory().getOWLDeclarationAxiom(owlIndividual));
        });

        return ontology;
    }

    // Helper method to get annotations from OWLEntity
    // Other service methods and fields...

    /**
     * Retrieves the name of the parent class (if any) of a given OWL class.
     *
     * @param owlClass The OWL class.
     * @return The parent class name, or an empty string if there is no parent class.
     */
    private String getParentClassName(OWLClass owlClass) {
        for (OWLSubClassOfAxiom axiom : owlOntology.subClassAxiomsForSubClass(owlClass).toList()) {
            OWLClassExpression superClass = axiom.getSuperClass();
            if (!superClass.isAnonymous()) {
                return superClass.asOWLClass().getIRI().getShortForm();
            }
        }
        return "";
    }

    /**
     * Retrieves the annotation value (label or comment) for a given OWLEntity from the provided OWLOntology.
     *
     * @param entity the OWLEntity whose annotation is to be retrieved
     * @param ontology the OWLOntology to search for the annotation
     * @param annotationType the annotation type (RDFS_LABEL or RDFS_COMMENT)
     * @return the annotation value (String), or null if the annotation is not found
     */
    private String getAnnotation(OWLEntity entity, OWLOntology ontology, OWLRDFVocabulary annotationType) {
        // Fetch annotations of the given OWLEntity
        Stream<OWLAnnotation> annotations = EntitySearcher.getAnnotations(entity, ontology, OWLManager.getOWLDataFactory().getOWLAnnotationProperty(annotationType.getIRI()));

        // Try to find the first annotation and return its literal value
        return annotations
                .findFirst()  // Get the first annotation found
                .map(annotation -> ((OWLLiteral) annotation.getValue()).getLiteral())  // Extract the literal value
                .orElse("");  // If no annotation is found, return null
    }

    private List<String> getDomainClasses(OWLObjectProperty property, OWLOntology ontology) {
        // Retrieve the domains of the given property from the ontology
        Stream<OWLClassExpression> domains = ontology.objectPropertyDomainAxioms(property)
                .map(OWLPropertyDomainAxiom::getDomain); // Extract the domain expressions

        // Flatten the expressions into OWLClasses and convert to String
        return domains
                .flatMap(HasClassesInSignature::classesInSignature) // Extract OWLClass objects
                .map(OWLClass::getIRI)                         // Get their IRIs
                .map(IRI::getShortForm)                            // Convert IRIs to String
                .collect(Collectors.toList());                 // Collect into a List
    }

    // Helper method to get domain classes as a List of OntologyClassAPI for Data Property
    private List<String> getDomainClasses(OWLDataProperty property, OWLOntology ontology) {
        // Retrieve the domain axioms of the given data property
        Stream<OWLClassExpression> domains = ontology.dataPropertyDomainAxioms(property)
                .map(OWLPropertyDomainAxiom::getDomain); // Extract the domain expressions

        // Extract OWLClasses, convert to IRIs, and return as a list of strings
        return domains
                .flatMap(HasClassesInSignature::classesInSignature) // Extract OWLClass objects
                .map(OWLClass::getIRI)                         // Get their IRIs
                .map(IRI::getShortForm)                            // Convert IRIs to String
                .collect(Collectors.toList());                 // Collect into a List
    }

    // Helper method to get range classes as a List of OntologyClassAPI
    private List<String> getRangeClasses(OWLObjectProperty property, OWLOntology ontology) {
        // Retrieve the range axioms of the given object property
        Stream<OWLClassExpression> ranges = ontology.objectPropertyRangeAxioms(property)
                .map(OWLPropertyRangeAxiom::getRange); // Extract the range expressions

        // Extract OWLClasses, convert to IRIs, and return as a list of strings
        return ranges
                .flatMap(HasClassesInSignature::classesInSignature) // Extract OWLClass objects
                .map(OWLClass::getIRI)                       // Get their IRIs
                .map(IRI::getShortForm)                          // Convert IRIs to String
                .collect(Collectors.toList());               // Collect into a List
    }

    // Helper method to get the range for a Data Property (datatype like xsd:string)
    private String getDataRange(OWLDataProperty property, OWLOntology ontology) {
        // Retrieve the range axioms for the given data property
        Optional<OWLDatatype> dataRange = ontology.dataPropertyRangeAxioms(property)
                .map(OWLPropertyRangeAxiom::getRange)  // Extract the range (OWLDataRange)
                .filter(OWLDatatype.class::isInstance) // Keep only OWLDatatype instances
                .map(OWLDatatype.class::cast)          // Cast to OWLDatatype
                .findFirst();                          // Get the first range (if any)

        // Return the IRI of the datatype as a string, or null if no range is defined
        return dataRange.map(OWLDatatype::getIRI)
                .map(IRI::getShortForm)
                .orElse("");
    }

    // Helper method to get object properties associated with an individual
    private List<OntologyObjectPropertyAPI> getIndividualObjectProperties(OWLNamedIndividual individual, OWLOntology ontology) {
        return individual.getObjectPropertiesInSignature()
                .stream()
                .map(iri -> objectProperties.get(iri.toString()))  // Map to OntologyObjectPropertyAPI
                .collect(Collectors.toList());
    }

    // Helper method to get data properties associated with an individual
    private List<OntologyDataPropertyAPI> getIndividualDataProperties(OWLNamedIndividual individual, OWLOntology ontology) {
        return individual.getDataPropertiesInSignature()
                .stream()
                .map(iri -> dataProperties.get(iri.toString()))  // Map to OntologyDataPropertyAPI
                .collect(Collectors.toList());
    }

    // Helper method to add label and comment annotations
    private void addAnnotations(OWLOntology ontology, OWLEntity entity, String label, String comment) {
        OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
        if (label != null) {
            OWLAnnotation labelAnnotation = dataFactory.getOWLAnnotation(dataFactory.getRDFSLabel(), dataFactory.getOWLLiteral(label));
            ontology.getOWLOntologyManager().addAxiom(ontology, dataFactory.getOWLAnnotationAssertionAxiom(entity.getIRI(), labelAnnotation));
        }
        if (comment != null) {
            OWLAnnotation commentAnnotation = dataFactory.getOWLAnnotation(dataFactory.getRDFSComment(), dataFactory.getOWLLiteral(comment));
            ontology.getOWLOntologyManager().addAxiom(ontology, dataFactory.getOWLAnnotationAssertionAxiom(entity.getIRI(), commentAnnotation));
        }
    }
}




