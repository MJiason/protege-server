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
        if (ontologyClass.getUniqueName() == null || ontologyClass.getUniqueName().isEmpty()) {
            throw new ValidationException("Unique name for Ontology Class cannot be null or empty");
        }
        // Add other validations as needed.
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
                        .map(IRI::getIRIString)
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
            String name = prefixManager.getShortForm(iri);
            // String label = getLabelComment(owlClass);
            // String comment = getClassComment(owlClass);

            String label = getAnnotation(owlClass, owlOntology, OWLRDFVocabulary.RDFS_LABEL);
            String comment = getAnnotation(owlClass, owlOntology, OWLRDFVocabulary.RDFS_COMMENT);
            ontologyClasses.put(name, new OntologyClassAPI(name, label, comment));
        });

        owlOntology.objectPropertiesInSignature().forEach(owlObjectProperty -> {
            IRI iri = owlObjectProperty.getIRI();
            String name = prefixManager.getShortForm(iri);
            String label = getAnnotation(owlObjectProperty, owlOntology, OWLRDFVocabulary.RDFS_LABEL);
            String comment = getAnnotation(owlObjectProperty, owlOntology, OWLRDFVocabulary.RDFS_COMMENT);

            // Retrieve domain and range information as Lists of OntologyClassAPI
            List<OntologyClassAPI> domain = getDomainClasses(owlObjectProperty, owlOntology);
            List<OntologyClassAPI> range = getRangeClasses(owlObjectProperty, owlOntology);

            // Initialize object properties with domain and range as lists
            objectProperties.put(name, new OntologyObjectPropertyAPI(name, domain, range, label, comment, PropertyType.FunctionalProperty));
        });

        // Populate Ontology Data Properties
        owlOntology.dataPropertiesInSignature().forEach(owlDataProperty -> {
            IRI iri = owlDataProperty.getIRI();
            String name = prefixManager.getShortForm(iri);
            String label = getAnnotation(owlDataProperty, owlOntology, OWLRDFVocabulary.RDFS_LABEL);
            String comment = getAnnotation(owlDataProperty, owlOntology, OWLRDFVocabulary.RDFS_COMMENT);

            // Retrieve domain and range for the data property
            List<OntologyClassAPI> domain = getDomainClasses(owlDataProperty, owlOntology);
            String range = getDataRange(owlDataProperty, owlOntology);

            // Initialize data properties with domain and range
            dataProperties.put(name, new OntologyDataPropertyAPI(name, domain, range, label, comment));
        });

        // Populate Ontology Individuals
        owlOntology.individualsInSignature().forEach(owlIndividual -> {
            IRI iri = owlIndividual.getIRI();
            String name = prefixManager.getShortForm(iri);
            String label = getAnnotation(owlIndividual, owlOntology, OWLRDFVocabulary.RDFS_LABEL);
            String comment = getAnnotation(owlIndividual, owlOntology, OWLRDFVocabulary.RDFS_COMMENT);


            OntologyClassAPI classAPI = new OntologyClassAPI();

            // Retrieve associated object properties for the individual
            List<OntologyObjectPropertyAPI> individualObjectProperties = getIndividualObjectProperties(owlIndividual, owlOntology);

            // Retrieve associated data properties for the individual
            List<OntologyDataPropertyAPI> individualDataProperties = getIndividualDataProperties(owlIndividual, owlOntology);

            // Create and add the individual to the storage
            individuals.put(name, new OntologyIndividualAPI(name, classAPI, label, comment, individualObjectProperties, individualDataProperties));
        });
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
                OWLClass domain = manager.getOWLDataFactory().getOWLClass(IRI.create(domainClass.getUniqueName()));
                manager.addAxiom(ontology, manager.getOWLDataFactory().getOWLObjectPropertyDomainAxiom(owlObjectProperty, domain));
            });
            objectProperty.getRange().forEach(rangeClass -> {
                OWLClass range = manager.getOWLDataFactory().getOWLClass(IRI.create(rangeClass.getUniqueName()));
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

    private List<OntologyClassAPI> getDomainClasses(OWLObjectProperty property, OWLOntology ontology) {
        return new ArrayList<>();/*property.getDomains(ontology)
                .stream()
                .map(domainIRI -> ontologyClasses.get(domainIRI.toString()))  // Map to OntologyClassAPI
                .collect(Collectors.toList());*/
    }

    // Helper method to get domain classes as a List of OntologyClassAPI for Data Property
    private List<OntologyClassAPI> getDomainClasses(OWLDataProperty property, OWLOntology ontology) {
        return new ArrayList<>();/*property.getDomains(ontology)
                .stream()
                .map(domainIRI -> ontologyClasses.get(domainIRI.toString()))  // Map to OntologyClassAPI
                .collect(Collectors.toList());*/
    }

    // Helper method to get range classes as a List of OntologyClassAPI
    private List<OntologyClassAPI> getRangeClasses(OWLObjectProperty property, OWLOntology ontology) {
        return new ArrayList<>();/*property.getRanges(ontology)
                .stream()
                .map(rangeIRI -> ontologyClasses.get(rangeIRI.toString()))  // Map to OntologyClassAPI
                .collect(Collectors.toList());*/
    }

    // Helper method to get the range for a Data Property (datatype like xsd:string)
    private String getDataRange(OWLDataProperty property, OWLOntology ontology) {
        return ""; /*property.getRanges(ontology)
                .stream()
                .map(rangeIRI -> rangeIRI.toString())  // Get the range as a String (datatype)
                .findFirst()
                .orElse(null);  // Return null if no range is found*/
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




