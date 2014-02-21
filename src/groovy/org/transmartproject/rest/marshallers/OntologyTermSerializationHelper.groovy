package org.transmartproject.rest.marshallers

import grails.rest.Link
import org.springframework.beans.factory.annotation.Autowired
import org.transmartproject.core.exceptions.InvalidArgumentsException
import org.transmartproject.core.ontology.OntologyTerm
import org.transmartproject.core.ontology.StudiesResource
import org.transmartproject.rest.StudyLoadingService

import javax.annotation.Resource

import static grails.rest.render.util.AbstractLinkingRenderer.RELATIONSHIP_SELF

class OntologyTermSerializationHelper implements HalOrJsonSerializationHelper<OntologyTerm> {

    @Resource
    StudyLoadingService studyLoadingServiceProxy

    @Autowired
    StudiesResource studiesResourceService

    final Class targetType = OntologyTerm

    final String collectionName = 'ontology_terms'

    @Override
    Collection<Link> getLinks(OntologyTerm term) {
        /* this gets tricky. We may be rendering this as part of the /studies response */
        OntologyTerm studyTerm
        String studyName
        try {
            studyTerm = studyLoadingServiceProxy.study.ontologyTerm
            studyName = studyLoadingServiceProxy.study.name
        } catch (InvalidArgumentsException iae) {
            studyTerm = term
            studyName = term.name.toUpperCase(Locale.ENGLISH)
        }
        studyName = studyName.toLowerCase(Locale.ENGLISH).encodeAsURL()

        def pathPart = term.key - studyTerm.key ?: 'ROOT'
        pathPart = pathPart.encodeAsURL()

        // TODO add other relationships (children, parent, ...)
        [new Link(RELATIONSHIP_SELF, "/studies/$studyName/concepts/$pathPart")]
    }

    @Override
    Map<String, Object> convertToMap(OntologyTerm term) {
        [
                name:     term.name,
                key:      term.key,
                fullName: term.fullName,
        ]
    }

    @Override
    Set<String> getEmbeddedEntities(OntologyTerm object) {
        [] as Set
    }
}