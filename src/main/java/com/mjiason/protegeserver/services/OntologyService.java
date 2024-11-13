package com.mjiason.protegeserver.services;

import com.mjiason.protegeserver.models.OWLClassShort;
import lombok.Getter;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



@Getter
@Service
public class OntologyService {

    private OWLOntology ontology;
    private OWLDataFactory dataFactory;

    public OntologyService() {
        loadOntology();
    }

    private void loadOntology() {
        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File file = new File("src/main/resources/api-ontology.owl");
            ontology = manager.loadOntologyFromOntologyDocument(file);
            dataFactory = manager.getOWLDataFactory();
            System.out.println("Ontology loaded: " + ontology.getOntologyID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<OWLClassShort> getOntologyClasses() {
        List<OWLClassShort> classInfoList = new ArrayList<>();

        for (OWLClass owlClass : ontology.classesInSignature().toList()) {
            String className = owlClass.getIRI().getShortForm();
            String comment = getClassComment(owlClass);
            String parentClassName = getParentClassName(owlClass);

            classInfoList.add(new OWLClassShort(className, parentClassName, comment));
        }

        return classInfoList;
    }

    /**
     * Retrieves the comment (rdfs:comment) of a given OWL class.
     *
     * @param owlClass The OWL class.
     * @return The comment as a String, or an empty string if not found.
     */
    private String getClassComment(OWLClass owlClass) {
        for (OWLAnnotation annotation : EntitySearcher.getAnnotations(owlClass, ontology, dataFactory.getRDFSComment()).toList())
            if (annotation.getValue() instanceof OWLLiteral) {
                return ((OWLLiteral) annotation.getValue()).getLiteral();
            }
        return "";
    }

    /**
     * Retrieves the name of the parent class (if any) of a given OWL class.
     *
     * @param owlClass The OWL class.
     * @return The parent class name, or an empty string if there is no parent class.
     */
    private String getParentClassName(OWLClass owlClass) {
        for (OWLSubClassOfAxiom axiom : ontology.subClassAxiomsForSubClass(owlClass).toList()) {
            OWLClassExpression superClass = axiom.getSuperClass();
            if (!superClass.isAnonymous()) {
                return superClass.asOWLClass().getIRI().getShortForm();
            }
        }
        return "";
    }
}

