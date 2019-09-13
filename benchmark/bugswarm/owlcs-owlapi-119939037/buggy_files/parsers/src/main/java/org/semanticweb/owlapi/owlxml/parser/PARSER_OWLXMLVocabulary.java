/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2014, The University of Manchester
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package org.semanticweb.owlapi.owlxml.parser;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.*;
import static org.semanticweb.owlapi.vocab.OWLXMLVocabulary.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.inject.Provider;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.owlxml.parser.OWLEH.HandleChild;
import org.semanticweb.owlapi.vocab.Namespaces;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.semanticweb.owlapi.vocab.OWLXMLVocabulary;
import org.semanticweb.owlapitools.builders.*;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.0.0
 */
enum PARSER_OWLXMLVocabulary implements HasIRI {
//@formatter:off
    /** CLASS.                              */  PARSER_CLASS                               (CLASS                               , OWLClassEH::new),
    /** DATA_PROPERTY.                      */  PARSER_DATA_PROPERTY                       (DATA_PROPERTY                       , OWLDataPropertyEH::new),
    /** OBJECT_PROPERTY.                    */  PARSER_OBJECT_PROPERTY                     (OBJECT_PROPERTY                     , OWLObjectPropertyEH::new),
    /** NAMED_INDIVIDUAL.                   */  PARSER_NAMED_INDIVIDUAL                    (NAMED_INDIVIDUAL                    , OWLIndividualEH::new),
    /** ENTITY_ANNOTATION.                  */  PARSER_ENTITY_ANNOTATION                   (ENTITY_ANNOTATION                   , LegacyEntityAnnotationEH::new),
    /** ANNOTATION_PROPERTY.                */  PARSER_ANNOTATION_PROPERTY                 (ANNOTATION_PROPERTY                 , OWLAnnotationPropertyEH::new),
    /** DATATYPE.                           */  PARSER_DATATYPE                            (DATATYPE                            , OWLDatatypeEH::new),
    /** ANNOTATION.                         */  PARSER_ANNOTATION                          (ANNOTATION                          , OWLAnnotationEH::new),
    /** ANONYMOUS_INDIVIDUAL.               */  PARSER_ANONYMOUS_INDIVIDUAL                (ANONYMOUS_INDIVIDUAL                , OWLAnonymousIndividualEH::new),
    /** IMPORT.                             */  PARSER_IMPORT                              (IMPORT                              , OWLImportsHandler::new),
    /** ONTOLOGY.                           */  PARSER_ONTOLOGY                            (ONTOLOGY                            , OWLOntologyHandler::new),
    /** LITERAL.                            */  PARSER_LITERAL                             (LITERAL                             , OWLLiteralEH::new),
    /** OBJECT_INVERSE_OF.                  */  PARSER_OBJECT_INVERSE_OF                   (OBJECT_INVERSE_OF                   , OWLInverseObjectPropertyEH::new),
    /** DATA_COMPLEMENT_OF.                 */  PARSER_DATA_COMPLEMENT_OF                  (DATA_COMPLEMENT_OF                  , OWLDataComplementOfEH::new),
    /** DATA_ONE_OF.                        */  PARSER_DATA_ONE_OF                         (DATA_ONE_OF                         , ()->new OWLSetEH<>(BuilderDataOneOf::new, HandleChild.AbstractOWLDataRangeHandler)),
    /** DATATYPE_RESTRICTION.               */  PARSER_DATATYPE_RESTRICTION                (DATATYPE_RESTRICTION                , OWLDatatypeRestrictionEH::new),
    /** FACET_RESTRICTION.                  */  PARSER_FACET_RESTRICTION                   (FACET_RESTRICTION                   , OWLDatatypeFacetRestrictionEH::new),
    /** DATA_UNION_OF.                      */  PARSER_DATA_UNION_OF                       (DATA_UNION_OF                       , ()->new OWLSetEH<>(BuilderDataUnionOf::new, HandleChild.AbstractOWLDataRangeHandler)),
    /** DATA_INTERSECTION_OF.               */  PARSER_DATA_INTERSECTION_OF                (DATA_INTERSECTION_OF                , ()->new OWLSetEH<>(BuilderDataIntersectionOf::new, HandleChild.AbstractOWLDataRangeHandler)),
    /** OBJECT_INTERSECTION_OF.             */  PARSER_OBJECT_INTERSECTION_OF              (OBJECT_INTERSECTION_OF              , OWLObjectIntersectionOfEH::new),
    /** OBJECT_UNION_OF.                    */  PARSER_OBJECT_UNION_OF                     (OBJECT_UNION_OF                     , OWLObjectUnionOfEH::new),
    /** OBJECT_COMPLEMENT_OF.               */  PARSER_OBJECT_COMPLEMENT_OF                (OBJECT_COMPLEMENT_OF                , OWLObjectComplementOfEH::new),
    /** OBJECT_ONE_OF.                      */  PARSER_OBJECT_ONE_OF                       (OBJECT_ONE_OF                       , OWLObjectOneOfEH::new),
    /** OBJECT_SOME_VALUES_FROM.            */  PARSER_OBJECT_SOME_VALUES_FROM             (OBJECT_SOME_VALUES_FROM             , ()->new AbstractObjectRestrictionEH<>(BuilderObjectSomeValuesFrom::new)),
    /** OBJECT_ALL_VALUES_FROM.             */  PARSER_OBJECT_ALL_VALUES_FROM              (OBJECT_ALL_VALUES_FROM              , ()->new AbstractObjectRestrictionEH<>(BuilderObjectAllValuesFrom::new)),
    /** OBJECT_HAS_SELF.                    */  PARSER_OBJECT_HAS_SELF                     (OBJECT_HAS_SELF                     , ()->new AbstractClassExpressionEH<>(BuilderObjectHasSelf::new)),
    /** OBJECT_HAS_VALUE.                   */  PARSER_OBJECT_HAS_VALUE                    (OBJECT_HAS_VALUE                    , OWLObjectHasValueEH::new),
    /** OBJECT_MIN_CARDINALITY.             */  PARSER_OBJECT_MIN_CARDINALITY              (OBJECT_MIN_CARDINALITY              , ()->new AbstractOWLObjectCardinalityEH<>(BuilderObjectMinCardinality::new)),
    /** OBJECT_EXACT_CARDINALITY.           */  PARSER_OBJECT_EXACT_CARDINALITY            (OBJECT_EXACT_CARDINALITY            , ()->new AbstractOWLObjectCardinalityEH<>(BuilderObjectExactCardinality::new)),
    /** OBJECT_MAX_CARDINALITY.             */  PARSER_OBJECT_MAX_CARDINALITY              (OBJECT_MAX_CARDINALITY              , ()->new AbstractOWLObjectCardinalityEH<>(BuilderObjectMaxCardinality::new)),
    /** DATA_SOME_VALUES_FROM.              */  PARSER_DATA_SOME_VALUES_FROM               (DATA_SOME_VALUES_FROM               , ()->new AbstractDataRestrictionEH<>(BuilderDataSomeValuesFrom::new)),
    /** DATA_ALL_VALUES_FROM.               */  PARSER_DATA_ALL_VALUES_FROM                (DATA_ALL_VALUES_FROM                , ()->new AbstractDataRestrictionEH<>(BuilderDataAllValuesFrom::new)),
    /** DATA_HAS_VALUE.                     */  PARSER_DATA_HAS_VALUE                      (DATA_HAS_VALUE                      , OWLDataHasValueEH::new),
    /** DATA_MIN_CARDINALITY.               */  PARSER_DATA_MIN_CARDINALITY                (DATA_MIN_CARDINALITY                , ()->new AbstractDataCardinalityRestrictionEH<>(BuilderDataMinCardinality::new)),
    /** DATA_EXACT_CARDINALITY.             */  PARSER_DATA_EXACT_CARDINALITY              (DATA_EXACT_CARDINALITY              , ()->new AbstractDataCardinalityRestrictionEH<>(BuilderDataExactCardinality::new)),
    /** DATA_MAX_CARDINALITY.               */  PARSER_DATA_MAX_CARDINALITY                (DATA_MAX_CARDINALITY                , ()->new AbstractDataCardinalityRestrictionEH<>(BuilderDataMaxCardinality::new)),
    /** SUB_CLASS_OF.                       */  PARSER_SUB_CLASS_OF                        (SUB_CLASS_OF                        , OWLSubClassAxiomEH::new),
    /** EQUIVALENT_CLASSES.                 */  PARSER_EQUIVALENT_CLASSES                  (EQUIVALENT_CLASSES                  , ()->new OWLSetAxiomEH<>(BuilderEquivalentClasses::new)),
    /** DISJOINT_CLASSES.                   */  PARSER_DISJOINT_CLASSES                    (DISJOINT_CLASSES                    , ()->new OWLSetAxiomEH<>(BuilderDisjointClasses::new)),
    /** DISJOINT_UNION.                     */  PARSER_DISJOINT_UNION                      (DISJOINT_UNION                      , OWLDisjointUnionEH::new),
    /** UNION_OF.                           */  PARSER_UNION_OF                            (UNION_OF                            , OWLUnionOfEH::new),
    /** SUB_OBJECT_PROPERTY_OF.             */  PARSER_SUB_OBJECT_PROPERTY_OF              (SUB_OBJECT_PROPERTY_OF              , OWLSubObjectPropertyOfAxiomEH::new),
    /** OBJECT_PROPERTY_CHAIN.              */  PARSER_OBJECT_PROPERTY_CHAIN               (OBJECT_PROPERTY_CHAIN               , OWLSubObjectPropertyChainEH::new),
    /** EQUIVALENT_OBJECT_PROPERTIES.       */  PARSER_EQUIVALENT_OBJECT_PROPERTIES        (EQUIVALENT_OBJECT_PROPERTIES        , ()->new OWLSetAxiomEH<>(BuilderEquivalentObjectProperties::new)),
    /** DISJOINT_OBJECT_PROPERTIES.         */  PARSER_DISJOINT_OBJECT_PROPERTIES          (DISJOINT_OBJECT_PROPERTIES          , ()->new OWLSetAxiomEH<>(BuilderDisjointObjectProperties::new)),
    /** OBJECT_PROPERTY_DOMAIN.             */  PARSER_OBJECT_PROPERTY_DOMAIN              (OBJECT_PROPERTY_DOMAIN              , OWLObjectPropertyDomainEH::new),
    /** OBJECT_PROPERTY_RANGE.              */  PARSER_OBJECT_PROPERTY_RANGE               (OBJECT_PROPERTY_RANGE               , OWLObjectPropertyRangeAxiomEH::new),
    /** INVERSE_OBJECT_PROPERTIES.          */  PARSER_INVERSE_OBJECT_PROPERTIES           (INVERSE_OBJECT_PROPERTIES           , OWLInverseObjectPropertiesAxiomEH::new),
    /** FUNCTIONAL_OBJECT_PROPERTY.         */  PARSER_FUNCTIONAL_OBJECT_PROPERTY          (FUNCTIONAL_OBJECT_PROPERTY          , ()->new AbstractOWLAxiomEH<>(BuilderFunctionalObjectProperty::new)),
    /** INVERSE_FUNCTIONAL_OBJECT_PROPERTY. */  PARSER_INVERSE_FUNCTIONAL_OBJECT_PROPERTY  (INVERSE_FUNCTIONAL_OBJECT_PROPERTY  , ()->new AbstractOWLAxiomEH<>(BuilderInverseFunctionalObjectProperty::new)),
    /** SYMMETRIC_OBJECT_PROPERTY.          */  PARSER_SYMMETRIC_OBJECT_PROPERTY           (SYMMETRIC_OBJECT_PROPERTY           , ()->new AbstractOWLAxiomEH<>(BuilderSymmetricObjectProperty::new)),
    /** ASYMMETRIC_OBJECT_PROPERTY.         */  PARSER_ASYMMETRIC_OBJECT_PROPERTY          (ASYMMETRIC_OBJECT_PROPERTY          , ()->new AbstractOWLAxiomEH<>(BuilderAsymmetricObjectProperty::new)),
    /** REFLEXIVE_OBJECT_PROPERTY.          */  PARSER_REFLEXIVE_OBJECT_PROPERTY           (REFLEXIVE_OBJECT_PROPERTY           , ()->new AbstractOWLAxiomEH<>(BuilderReflexiveObjectProperty::new)),
    /** IRREFLEXIVE_OBJECT_PROPERTY.        */  PARSER_IRREFLEXIVE_OBJECT_PROPERTY         (IRREFLEXIVE_OBJECT_PROPERTY         , ()->new AbstractOWLAxiomEH<>(BuilderIrreflexiveObjectProperty::new)),
    /** TRANSITIVE_OBJECT_PROPERTY.         */  PARSER_TRANSITIVE_OBJECT_PROPERTY          (TRANSITIVE_OBJECT_PROPERTY          , ()->new AbstractOWLAxiomEH<>(BuilderTransitiveObjectProperty::new)),
    /** SUB_DATA_PROPERTY_OF.               */  PARSER_SUB_DATA_PROPERTY_OF                (SUB_DATA_PROPERTY_OF                , OWLSubDataPropertyOfAxiomEH::new),
    /** EQUIVALENT_DATA_PROPERTIES.         */  PARSER_EQUIVALENT_DATA_PROPERTIES          (EQUIVALENT_DATA_PROPERTIES          , ()->new OWLSetAxiomEH<>(BuilderEquivalentDataProperties::new)),
    /** DISJOINT_DATA_PROPERTIES.           */  PARSER_DISJOINT_DATA_PROPERTIES            (DISJOINT_DATA_PROPERTIES            , ()->new OWLSetAxiomEH<>(BuilderDisjointDataProperties::new)),
    /** DATA_PROPERTY_DOMAIN.               */  PARSER_DATA_PROPERTY_DOMAIN                (DATA_PROPERTY_DOMAIN                , OWLDataPropertyDomainAxiomEH::new),
    /** DATA_PROPERTY_RANGE.                */  PARSER_DATA_PROPERTY_RANGE                 (DATA_PROPERTY_RANGE                 , OWLDataPropertyRangeAxiomEH::new),
    /** FUNCTIONAL_DATA_PROPERTY.           */  PARSER_FUNCTIONAL_DATA_PROPERTY            (FUNCTIONAL_DATA_PROPERTY            , ()->new AbstractOWLAxiomEH<>(BuilderFunctionalDataProperty::new)),
    /** SAME_INDIVIDUAL.                    */  PARSER_SAME_INDIVIDUAL                     (SAME_INDIVIDUAL                     , ()->new OWLSetAxiomEH<>(BuilderSameIndividual::new)),
    /** DIFFERENT_INDIVIDUALS.              */  PARSER_DIFFERENT_INDIVIDUALS               (DIFFERENT_INDIVIDUALS               , ()->new OWLSetAxiomEH<>(BuilderDifferentIndividuals::new)),
    /** CLASS_ASSERTION.                    */  PARSER_CLASS_ASSERTION                     (CLASS_ASSERTION                     , OWLClassAssertionAxiomEH::new),
    /** OBJECT_PROPERTY_ASSERTION.          */  PARSER_OBJECT_PROPERTY_ASSERTION           (OBJECT_PROPERTY_ASSERTION           , OWLObjectPropertyAssertionAxiomEH::new),
    /** DATA_PROPERTY_ASSERTION.            */  PARSER_DATA_PROPERTY_ASSERTION             (DATA_PROPERTY_ASSERTION             , OWLDataPropertyAssertionAxiomEH::new),
    /** NEGATIVE_OBJECT_PROPERTY_ASSERTION. */  PARSER_NEGATIVE_OBJECT_PROPERTY_ASSERTION  (NEGATIVE_OBJECT_PROPERTY_ASSERTION  , OWLNegativeObjectPropertyAssertionAxiomEH::new),
    /** NEGATIVE_DATA_PROPERTY_ASSERTION.   */  PARSER_NEGATIVE_DATA_PROPERTY_ASSERTION    (NEGATIVE_DATA_PROPERTY_ASSERTION    , OWLNegativeDataPropertyAssertionAxiomEH::new),
    /** HAS_KEY.                            */  PARSER_HAS_KEY                             (HAS_KEY                             , OWLHasKeyEH::new),
    /** DECLARATION.                        */  PARSER_DECLARATION                         (DECLARATION                         , OWLDeclarationAxiomEH::new),
    /** ANNOTATION_ASSERTION.               */  PARSER_ANNOTATION_ASSERTION                (ANNOTATION_ASSERTION                , OWLAnnotationAssertionEH::new),
    /** ANNOTATION_PROPERTY_DOMAIN.         */  PARSER_ANNOTATION_PROPERTY_DOMAIN          (ANNOTATION_PROPERTY_DOMAIN          , OWLAnnotationPropertyDomainEH::new),
    /** ANNOTATION_PROPERTY_RANGE.          */  PARSER_ANNOTATION_PROPERTY_RANGE           (ANNOTATION_PROPERTY_RANGE           , OWLAnnotationPropertyRangeEH::new),
    /** SUB_ANNOTATION_PROPERTY_OF.         */  PARSER_SUB_ANNOTATION_PROPERTY_OF          (SUB_ANNOTATION_PROPERTY_OF          , OWLSubAnnotationPropertyOfEH::new),
    /** DATATYPE_DEFINITION.                */  PARSER_DATATYPE_DEFINITION                 (DATATYPE_DEFINITION                 , OWLDatatypeDefinitionEH::new),
    /** IRI_ELEMENT.                        */  PARSER_IRI_ELEMENT                         (IRI_ELEMENT                         , ()->new AbstractIRIEH(false)),
    /** ABBREVIATED_IRI_ELEMENT.            */  PARSER_ABBREVIATED_IRI_ELEMENT             (ABBREVIATED_IRI_ELEMENT             , ()->new AbstractIRIEH(true)),
    /** NODE_ID.                            */  PARSER_NODE_ID                             (NODE_ID                             ),
    /** ANNOTATION_URI.                     */  PARSER_ANNOTATION_URI                      (ANNOTATION_URI                      ),
    /** LABEL.                              */  PARSER_LABEL                               (LABEL                               ),
    /** COMMENT.                            */  PARSER_COMMENT                             (COMMENT                             ),
    /** DOCUMENTATION.                      */  PARSER_DOCUMENTATION                       (DOCUMENTATION                       ),
    /** DATATYPE_FACET.                     */  PARSER_DATATYPE_FACET                      (DATATYPE_FACET                      ),
    /** DATATYPE_IRI.                       */  PARSER_DATATYPE_IRI                        (DATATYPE_IRI                        ),
    /** DATA_RANGE.                         */  PARSER_DATA_RANGE                          (DATA_RANGE                          ),
    /** PREFIX.                             */  PARSER_PREFIX                              (PREFIX                              ),
    /** NAME_ATTRIBUTE.                     */  PARSER_NAME_ATTRIBUTE                      (NAME_ATTRIBUTE                      ),
    /** IRI_ATTRIBUTE.                      */  PARSER_IRI_ATTRIBUTE                       (IRI_ATTRIBUTE                       ),
    /** ABBREVIATED_IRI_ATTRIBUTE.          */  PARSER_ABBREVIATED_IRI_ATTRIBUTE           (ABBREVIATED_IRI_ATTRIBUTE           ),
    /** CARDINALITY_ATTRIBUTE.              */  PARSER_CARDINALITY_ATTRIBUTE               (CARDINALITY_ATTRIBUTE               ),
    
    // Rules Extensions
    /** DL_SAFE_RULE.                       */  PARSER_DL_SAFE_RULE                        (DL_SAFE_RULE                        , SWRLRuleEH::new),
    /** BODY.                               */  PARSER_BODY                                (BODY                                , SWRLAtomListEH::new),
    /** HEAD.                               */  PARSER_HEAD                                (HEAD                                , SWRLAtomListEH::new),
    /** CLASS_ATOM.                         */  PARSER_CLASS_ATOM                          (CLASS_ATOM                          , SWRLClassAtomEH::new),
    /** DATA_RANGE_ATOM.                    */  PARSER_DATA_RANGE_ATOM                     (DATA_RANGE_ATOM                     , SWRLDataRangeAtomEH::new),
    /** OBJECT_PROPERTY_ATOM.               */  PARSER_OBJECT_PROPERTY_ATOM                (OBJECT_PROPERTY_ATOM                , SWRLObjectPropertyAtomEH::new),
    /** DATA_PROPERTY_ATOM.                 */  PARSER_DATA_PROPERTY_ATOM                  (DATA_PROPERTY_ATOM                  , SWRLDataPropertyAtomEH::new),
    /** BUILT_IN_ATOM.                      */  PARSER_BUILT_IN_ATOM                       (BUILT_IN_ATOM                       , SWRLBuiltInAtomEH::new),
    /** SAME_INDIVIDUAL_ATOM.               */  PARSER_SAME_INDIVIDUAL_ATOM                (SAME_INDIVIDUAL_ATOM                , SWRLSameIndividualAtomEH::new),
    /** DIFFERENT_INDIVIDUALS_ATOM.         */  PARSER_DIFFERENT_INDIVIDUALS_ATOM          (DIFFERENT_INDIVIDUALS_ATOM          , SWRLDifferentIndividualsAtomEH::new),
    /** VARIABLE.                           */  PARSER_VARIABLE                            (VARIABLE                            , SWRLVariableEH::new),
    /** DESCRIPTION_GRAPH_RULE.             */  PARSER_DESCRIPTION_GRAPH_RULE              (DESCRIPTION_GRAPH_RULE              );
//@formatter:on

    private final IRI iri;
    private final String shortName;
    private final Provider<OWLEH<?, ?>> create;

    PARSER_OWLXMLVocabulary(OWLXMLVocabulary name) {
        iri = IRI.create(Namespaces.OWL.toString(), name.getShortForm());
        shortName = name.getShortForm();
        create = () -> {
            throw new OWLRuntimeException(shortName + " vocabulary element does not have a handler");
        };
    }

    PARSER_OWLXMLVocabulary(OWLXMLVocabulary name, Provider<OWLEH<?, ?>> create) {
        iri = IRI.create(Namespaces.OWL.toString(), name.getShortForm());
        shortName = name.getShortForm();
        this.create = create;
    }

    @Override
    public IRI getIRI() {
        return iri;
    }

    /**
     * @return short name
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param handler
     *        owlxml handler
     * @return element handler
     */
    OWLEH<?, ?> createHandler(OWLXMLPH handler) {
        OWLEH<?, ?> owleh = create.get();
        owleh.setHandler(handler);
        return owleh;
    }
}

@SuppressWarnings("unused")
abstract class OWLEH<O, B extends Builder<O>> {

    OWLXMLPH handler;
    OWLEH<?, ?> parentHandler;
    final StringBuilder sb = new StringBuilder();
    String elementName;
    OWLDataFactory df;
    Function<OWLDataFactory, B> provider;
    B builder;
    HandleChild child;

    void setHandler(OWLXMLPH handler) {
        this.handler = handler;
        this.df = handler.getDataFactory();
        if (provider != null) {
            this.builder = provider.apply(df);
        }
    }

    public <T> T getOWLObject() {
        return (T) builder.buildObject();
    }

    public <T> T getOWLObject(Class<T> witness) {
        return (T) getOWLObject();
    }

    IRI getIRIFromAttribute(String localName, String value) {
        if (localName.equals(IRI_ATTRIBUTE.getShortForm())) {
            return handler.getIRI(value);
        } else if (localName.equals(ABBREVIATED_IRI_ATTRIBUTE.getShortForm())) {
            return handler.getAbbreviatedIRI(value);
        } else if ("URI".equals(localName)) {
            // Legacy
            return handler.getIRI(value);
        }
        ensureAttributeNotNull(null, IRI_ATTRIBUTE.getShortForm());
        return IRI.create("");
    }

    IRI getIRIFromElement(String elementLocalName, String textContent) {
        if (elementLocalName.equals(IRI_ELEMENT.getShortForm())) {
            return handler.getIRI(textContent.trim());
        } else if (elementLocalName.equals(ABBREVIATED_IRI_ELEMENT.getShortForm())) {
            return handler.getAbbreviatedIRI(textContent.trim());
        }
        throw new OWLXMLParserException(handler, elementLocalName + " is not an IRI element");
    }

    void setParentHandler(OWLEH<?, ?> handler) {
        parentHandler = handler;
    }

    final OWLEH<?, ?> getParentHandler() {
        return verifyNotNull(parentHandler, "parentHandler cannot be null at this point");
    }

    void attribute(String localName, String value) {}

    void startElement(String name) {
        elementName = name;
    }

    final String getElementName() {
        return elementName;
    }

    final void ensureNotNull(@Nullable Object element, String message) {
        if (element == null) {
            throw new OWLXMLParserElementNotFoundException(handler, message);
        }
    }

    final void ensureAttributeNotNull(@Nullable Object element, String message) {
        if (element == null) {
            throw new OWLXMLParserAttributeNotFoundException(handler, message);
        }
    }

    final void handleChars(char[] chars, int start, int length) {
        if (isTextContentPossible()) {
            sb.append(chars, start, length);
        }
    }

    final String getText() {
        return sb.toString();
    }

    boolean isTextContentPossible() {
        return false;
    }

    void handleChild(AbstractOWLAxiomEH<? extends OWLAxiom, ?> h) {
        OWLAxiom axiom = h.getOWLObject();
        if (!axiom.isAnnotationAxiom() || handler.getConfiguration().isLoadAnnotationAxioms()) {
            handler.getOWLOntologyManager().applyChange(new AddAxiom(handler.getOntology(), axiom));
        }
    }

    void handleChild(ObjectPropertyEH h) {
        if (builder instanceof SettableProperty) {
            ((SettableProperty<OWLObjectPropertyExpression, ?>) builder).withProperty(h.getOWLObject(
                OWLObjectPropertyExpression.class));
        }
    }

    void handleChild(OWLDataPropertyEH h) {
        if (builder instanceof SettableProperty) {
            ((SettableProperty<OWLDataPropertyExpression, ?>) builder).withProperty(h.getOWLObject(
                OWLDataPropertyExpression.class));
        }
    }

    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {}

    void handleChild(DataRangeEH h) {}

    void handleChild(OWLIndividualEH h) {}

    void handleChild(OWLLiteralEH h) {}

    void handleChild(OWLAnnotationEH h) {}

    void handleChild(OWLSubObjectPropertyChainEH h) {}

    void handleChild(OWLDatatypeFacetRestrictionEH h) {}

    void handleChild(OWLAnnotationPropertyEH h) {
        if (builder instanceof SettableProperty) {
            ((SettableProperty<OWLAnnotationProperty, ?>) builder).withProperty(h.getOWLObject(
                OWLAnnotationProperty.class));
        }
    }

    void handleChild(OWLAnonymousIndividualEH h) {}

    void handleChild(AbstractIRIEH h) {}

    void handleChild(SWRLVariableEH h) {}

    void handleChild(SWRLAtomEH h) {}

    void handleChild(SWRLAtomListEH h) {}

    void endElement() {
        child.run(parentHandler, this);
    }

    enum HandleChild {
        AbstractOWLAxiomEH((parent, _this) -> parent.handleChild((AbstractOWLAxiomEH) _this)),
        AbstractClassExpressionEH((parent, _this) -> parent.handleChild((AbstractClassExpressionEH) _this)),
        AbstractOWLDataRangeHandler((parent, _this) -> parent.handleChild((DataRangeEH) _this)),
        ObjectPropertyEH((parent, _this) -> parent.handleChild((ObjectPropertyEH) _this)),
        OWLDataPropertyEH((parent, _this) -> parent.handleChild((OWLDataPropertyEH) _this)),
        OWLIndividualEH((parent, _this) -> parent.handleChild((OWLIndividualEH) _this)),
        OWLLiteralEH((parent, _this) -> parent.handleChild((OWLLiteralEH) _this)),
        OWLAnnotationEH((parent, _this) -> parent.handleChild((OWLAnnotationEH) _this)),
        OWLSubObjectPropertyChainEH((parent, _this) -> parent.handleChild((OWLSubObjectPropertyChainEH) _this)),
        OWLDatatypeFacetRestrictionEH((parent, _this) -> parent.handleChild((OWLDatatypeFacetRestrictionEH) _this)),
        OWLAnnotationPropertyEH((parent, _this) -> parent.handleChild((OWLAnnotationPropertyEH) _this)),
        OWLAnonymousIndividualEH((parent, _this) -> parent.handleChild((OWLAnonymousIndividualEH) _this)),
        AbstractIRIEH((parent, _this) -> parent.handleChild((AbstractIRIEH) _this)),
        SWRLVariableEH((parent, _this) -> parent.handleChild((SWRLVariableEH) _this)),
        SWRLAtomEH((parent, _this) -> parent.handleChild((SWRLAtomEH) _this)),
        SWRLAtomListEH((parent, _this) -> parent.handleChild((SWRLAtomListEH) _this));

        private BiConsumer<OWLEH<?, ?>, OWLEH<?, ?>> consumer;

        HandleChild(BiConsumer<OWLEH<?, ?>, OWLEH<?, ?>> c) {
            consumer = c;
        }

        final void run(OWLEH<?, ?> parent, OWLEH<?, ?> _this) {
            consumer.accept(parent, _this);
        };
    }
}

class AbstractClassExpressionEH<X extends OWLClassExpression, B extends Builder<X>> extends OWLEH<X, B> {

    public AbstractClassExpressionEH(Function<OWLDataFactory, B> b) {
        provider = b;
        child = HandleChild.AbstractClassExpressionEH;
    }
}

class AbstractDataCardinalityRestrictionEH<X extends OWLClassExpression, B extends Builder<X> & SettableCardinality<?> & SettableProperty<OWLDataPropertyExpression, ?> & SettableRange<OWLDataRange, ?>>
    extends AbstractDataRestrictionEH<X, B> {

    public AbstractDataCardinalityRestrictionEH(Function<OWLDataFactory, B> b) {
        super(b);
    }

    @Override
    void attribute(String localName, String value) {
        if ("cardinality".equals(localName)) {
            builder.withCardinality(Integer.parseInt(value));
        }
    }
}

class AbstractOWLAxiomEH<X extends OWLAxiom, B extends Builder<X>> extends OWLEH<X, B> {

    AbstractOWLAxiomEH(Function<OWLDataFactory, B> b) {
        provider = b;
        child = HandleChild.AbstractOWLAxiomEH;
    }

    @Override
    void handleChild(OWLAnnotationEH h) {
        ((BaseBuilder<?, ?>) builder).withAnnotation(h.getOWLObject());
    }
}

abstract class AbstractOWLDataRangeHandler<X extends OWLDataRange, B extends Builder<X>> extends OWLEH<X, B> implements
    DataRangeEH {

    public AbstractOWLDataRangeHandler(Function<OWLDataFactory, B> b) {
        provider = b;
        child = HandleChild.AbstractOWLDataRangeHandler;
    }
}

class AbstractOWLObjectCardinalityEH<X extends OWLClassExpression, B extends Builder<X> & SettableCardinality<?> & SettableProperty<OWLObjectPropertyExpression, ?> & SettableRange<OWLClassExpression, ?>>
    extends AbstractObjectRestrictionEH<X, B> {

    public AbstractOWLObjectCardinalityEH(Function<OWLDataFactory, B> b) {
        super(b);
    }

    @Override
    void attribute(String localName, String value) {
        if ("cardinality".equals(localName)) {
            builder.withCardinality(Integer.parseInt(value));
        }
    }
}

interface ObjectPropertyEH {

    <T> T getOWLObject();

    <T> T getOWLObject(Class<T> witness);
}

interface DataRangeEH {

    <T> T getOWLObject();

    <T> T getOWLObject(Class<T> witness);
}

class AbstractOWLObjectPropertyEH extends OWLEH<OWLObjectProperty, BuilderObjectProperty> implements ObjectPropertyEH {

    public AbstractOWLObjectPropertyEH() {
        provider = BuilderObjectProperty::new;
        child = HandleChild.ObjectPropertyEH;
    }
}

class AbstractDataRestrictionEH<X extends OWLClassExpression, B extends Builder<X> & SettableProperty<OWLDataPropertyExpression, ?> & SettableRange<OWLDataRange, ?>>
    extends AbstractClassExpressionEH<X, B> {

    public AbstractDataRestrictionEH(Function<OWLDataFactory, B> b) {
        super(b);
    }

    @Override
    void startElement(String name) {
        super.startElement(name);
        builder.withRange(df.getTopDatatype());
    }

    @Override
    void handleChild(DataRangeEH h) {
        builder.withRange(h.getOWLObject());
    }
}

class AbstractObjectRestrictionEH<X extends OWLClassExpression, B extends Builder<X> & SettableProperty<OWLObjectPropertyExpression, ?> & SettableRange<OWLClassExpression, ?>>
    extends AbstractClassExpressionEH<X, B> {

    public AbstractObjectRestrictionEH(Function<OWLDataFactory, B> b) {
        super(b);
    }

    @Override
    void startElement(String name) {
        super.startElement(name);
        builder.withRange(df.getOWLThing());
    }

    @Override
    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {
        builder.withRange(h.getOWLObject());
    }
}

class AbstractIRIEH extends OWLEH<IRI, Builder<IRI>> {

    Provider<IRI> p;

    public AbstractIRIEH(boolean abbreviated) {
        child = HandleChild.AbstractIRIEH;
        p = abbreviated ? this::shortIri : this::longIri;
    }

    @Override
    boolean isTextContentPossible() {
        return true;
    }

    @Override
    public IRI getOWLObject() {
        return p.get();
    }

    IRI longIri() {
        return handler.getIRI(getText().trim());
    }

    IRI shortIri() {
        return handler.getAbbreviatedIRI(getText().trim());
    }
}

class OWLUnionOfEH extends OWLEH<OWLClassExpression, Builder<OWLClassExpression>> {

    @Override
    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {
        // We simply pass on to our parent, which MUST be an OWLDisjointUnionOf
        getParentHandler().handleChild(h);
    }

    @Override
    void endElement() {
        // nothing to do here
    }

    @Override
    public <T> T getOWLObject() {
        throw new OWLRuntimeException("getOWLObject should not be called on OWLUnionOfEH");
    }
}

class LegacyEntityAnnotationEH extends AbstractOWLAxiomEH<OWLAnnotationAssertionAxiom, BuilderAnnotationAssertion> {

    LegacyEntityAnnotationEH() {
        super(BuilderAnnotationAssertion::new);
    }

    @Override
    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {
        builder.withSubject(h.getOWLObject(OWLClassExpression.class).asOWLClass());
    }

    @Override
    void handleChild(OWLDataPropertyEH h) {
        builder.withSubject(h.getOWLObject(OWLDataPropertyExpression.class).asOWLDataProperty());
    }

    @Override
    void handleChild(OWLIndividualEH h) {
        builder.withSubject(h.getOWLObject(OWLAnonymousIndividual.class));
    }

    @Override
    void handleChild(ObjectPropertyEH h) {
        builder.withSubject(h.getOWLObject(OWLObjectPropertyExpression.class).asOWLObjectProperty());
    }

    @Override
    void handleChild(OWLAnnotationEH h) {
        if (builder.getSubject() == null) {
            super.handleChild(h);
        } else {
            OWLAnnotation o = h.getOWLObject();
            builder.withProperty(o.getProperty()).withValue(o.getValue());
        }
    }
}

class OWLAnnotationAssertionEH extends AbstractOWLAxiomEH<OWLAnnotationAssertionAxiom, BuilderAnnotationAssertion> {

    OWLAnnotationAssertionEH() {
        super(BuilderAnnotationAssertion::new);
    }

    @Override
    void handleChild(AbstractIRIEH h) {
        internalSet(h.getOWLObject());
    }

    public <T extends OWLAnnotationSubject & OWLAnnotationValue> void internalSet(T h) {
        if (builder.getSubject() == null) {
            builder.withSubject(h);
        } else {
            builder.withValue(h);
        }
    }

    @Override
    void handleChild(OWLAnonymousIndividualEH h) {
        internalSet(h.getOWLObject());
    }

    @Override
    void handleChild(OWLLiteralEH h) {
        builder.withValue(h.getOWLObject());
    }
}

class OWLAnnotationEH extends OWLEH<OWLAnnotation, BuilderAnnotation> {

    public OWLAnnotationEH() {
        provider = x -> new BuilderAnnotation(x);
        child = HandleChild.OWLAnnotationEH;
    }

    @Override
    void attribute(String localName, String value) {
        super.attribute(localName, value);
        // Legacy Handling
        if (localName.equals(ANNOTATION_URI.getShortForm())) {
            builder.withProperty(handler.getIRI(value));
        }
    }

    @Override
    void handleChild(OWLAnnotationEH h) {
        builder.withAnnotation(h.getOWLObject());
    }

    @Override
    void handleChild(OWLAnonymousIndividualEH h) {
        builder.withValue(h.getOWLObject());
    }

    @Override
    void handleChild(OWLLiteralEH h) {
        builder.withValue(h.getOWLObject());
    }

    @Override
    void handleChild(AbstractIRIEH h) {
        builder.withValue(h.getOWLObject());
    }
}

class OWLAnnotationPropertyDomainEH extends
    AbstractOWLAxiomEH<OWLAnnotationPropertyDomainAxiom, BuilderAnnotationPropertyDomain> {

    OWLAnnotationPropertyDomainEH() {
        super(BuilderAnnotationPropertyDomain::new);
    }

    @Override
    void handleChild(AbstractIRIEH h) {
        builder.withDomain(h.getOWLObject());
    }
}

class OWLAnnotationPropertyEH extends OWLEH<OWLAnnotationProperty, BuilderAnnotationProperty> {

    public OWLAnnotationPropertyEH() {
        provider = x -> new BuilderAnnotationProperty(x);
        child = HandleChild.OWLAnnotationPropertyEH;
    }

    @Override
    void attribute(String localName, String value) {
        builder.withIRI(getIRIFromAttribute(localName, value));
    }
}

class OWLAnnotationPropertyRangeEH extends
    AbstractOWLAxiomEH<OWLAnnotationPropertyRangeAxiom, BuilderAnnotationPropertyRange> {

    OWLAnnotationPropertyRangeEH() {
        super(BuilderAnnotationPropertyRange::new);
    }

    @Override
    void handleChild(AbstractIRIEH h) {
        builder.withRange(h.getOWLObject());
    }
}

class OWLAnonymousIndividualEH extends OWLEH<OWLAnonymousIndividual, BuilderAnonymousIndividual> {

    public OWLAnonymousIndividualEH() {
        this(BuilderAnonymousIndividual::new);
    }

    public OWLAnonymousIndividualEH(Function<OWLDataFactory, BuilderAnonymousIndividual> b) {
        provider = b;
        child = HandleChild.OWLAnonymousIndividualEH;
    }

    @Override
    void attribute(String localName, String value) {
        if (localName.equals(NODE_ID.getShortForm())) {
            builder.withId(value.trim());
        } else {
            super.attribute(localName, value);
        }
    }
}

class OWLClassAssertionAxiomEH extends AbstractOWLAxiomEH<OWLClassAssertionAxiom, BuilderClassAssertion> {

    OWLClassAssertionAxiomEH() {
        super(BuilderClassAssertion::new);
    }

    @Override
    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {
        builder.withClass(h.getOWLObject());
    }

    @Override
    void handleChild(OWLIndividualEH h) {
        builder.withIndividual(h.getOWLObject());
    }

    @Override
    void handleChild(OWLAnonymousIndividualEH h) {
        builder.withIndividual(h.getOWLObject());
    }
}

class OWLClassEH extends AbstractClassExpressionEH<OWLClass, BuilderClass> {

    public OWLClassEH() {
        super(BuilderClass::new);
    }

    @Override
    void attribute(String localName, String value) {
        builder.withIRI(getIRIFromAttribute(localName, value));
    }
}

class OWLDataComplementOfEH extends AbstractOWLDataRangeHandler<OWLDataComplementOf, BuilderDataComplementOf> {

    public OWLDataComplementOfEH() {
        super(BuilderDataComplementOf::new);
    }

    @Override
    void handleChild(DataRangeEH h) {
        builder.withRange(h.getOWLObject(OWLDataRange.class));
    }
}

class OWLDataHasValueEH extends AbstractClassExpressionEH<OWLDataHasValue, BuilderDataHasValue> {

    public OWLDataHasValueEH() {
        super(BuilderDataHasValue::new);
    }

    @Override
    void handleChild(OWLLiteralEH h) {
        builder.withLiteral(h.getOWLObject());
    }
}

class OWLSetEH<X extends OWLObject, Y extends OWLObject, B extends Builder<X> & SettableItem<Y, B>> extends OWLEH<X, B>
    implements DataRangeEH {

    OWLSetEH(Function<OWLDataFactory, B> b, HandleChild c) {
        provider = b;
        child = c;
    }

    @Override
    void handleChild(DataRangeEH h) {
        builder.withItem(h.getOWLObject());
    }

    @Override
    void handleChild(OWLLiteralEH h) {
        builder.withItem(h.getOWLObject());
    }
}

class OWLDataPropertyAssertionAxiomEH extends
    AbstractOWLAxiomEH<OWLDataPropertyAssertionAxiom, BuilderDataPropertyAssertion> {

    OWLDataPropertyAssertionAxiomEH() {
        super(BuilderDataPropertyAssertion::new);
    }

    @Override
    void handleChild(OWLAnonymousIndividualEH h) {
        builder.withSubject(h.getOWLObject());
    }

    @Override
    void handleChild(OWLIndividualEH h) {
        builder.withSubject(h.getOWLObject());
    }

    @Override
    void handleChild(OWLLiteralEH h) {
        builder.withValue(h.getOWLObject());
    }
}

class OWLDataPropertyDomainAxiomEH extends AbstractOWLAxiomEH<OWLDataPropertyDomainAxiom, BuilderDataPropertyDomain> {

    OWLDataPropertyDomainAxiomEH() {
        super(BuilderDataPropertyDomain::new);
    }

    @Override
    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {
        builder.withDomain(h.getOWLObject());
    }
}

class OWLDataPropertyEH extends OWLEH<OWLDataProperty, BuilderDataProperty> {

    public OWLDataPropertyEH() {
        provider = x -> new BuilderDataProperty(x);
        child = HandleChild.OWLDataPropertyEH;
    }

    @Override
    void attribute(String localName, String value) {
        builder.withIRI(getIRIFromAttribute(localName, value));
    }
}

class OWLDataPropertyRangeAxiomEH extends AbstractOWLAxiomEH<OWLDataPropertyRangeAxiom, BuilderDataPropertyRange> {

    OWLDataPropertyRangeAxiomEH() {
        super(BuilderDataPropertyRange::new);
    }

    @Override
    void handleChild(DataRangeEH h) {
        builder.withRange(h.getOWLObject(OWLDataRange.class));
    }
}

class OWLDataRestrictionEH extends AbstractOWLDataRangeHandler<OWLDatatypeRestriction, BuilderDatatypeRestriction> {

    BuilderFacetRestriction oneRestriction;

    public OWLDataRestrictionEH() {
        super(BuilderDatatypeRestriction::new);
    }

    @Override
    void setHandler(OWLXMLPH handler) {
        super.setHandler(handler);
        oneRestriction = new BuilderFacetRestriction(df);
    }

    @Override
    void handleChild(DataRangeEH h) {
        builder.withDatatype(h.getOWLObject(OWLDataRange.class).asOWLDatatype());
    }

    @Override
    void handleChild(OWLLiteralEH h) {
        oneRestriction.withLiteral(h.getOWLObject());
    }

    @Override
    void handleChild(OWLDatatypeFacetRestrictionEH h) {
        builder.withItem(h.getOWLObject());
    }

    @Override
    void attribute(String localName, String value) {
        super.attribute(localName, value);
        if ("facet".equals(localName)) {
            oneRestriction.withFacet(OWLFacet.getFacet(handler.getIRI(value)));
        }
    }
}

class OWLDatatypeDefinitionEH extends AbstractOWLAxiomEH<OWLDatatypeDefinitionAxiom, BuilderDatatypeDefinition> {

    OWLDatatypeDefinitionEH() {
        super(BuilderDatatypeDefinition::new);
    }

    @Override
    void handleChild(DataRangeEH h) {
        OWLDataRange handledDataRange = h.getOWLObject();
        if (handledDataRange.isOWLDatatype() && builder.getType() == null) {
            builder.with(handledDataRange.asOWLDatatype());
        } else {
            builder.withType(handledDataRange);
        }
    }
}

class OWLDatatypeEH extends AbstractOWLDataRangeHandler<OWLDatatype, BuilderDatatype> {

    public OWLDatatypeEH() {
        super(BuilderDatatype::new);
    }

    @Override
    void attribute(String localName, String value) {
        builder.withIRI(getIRIFromAttribute(localName, value));
    }
}

class OWLDatatypeFacetRestrictionEH extends OWLEH<OWLFacetRestriction, BuilderFacetRestriction> {

    public OWLDatatypeFacetRestrictionEH() {
        provider = BuilderFacetRestriction::new;
        child = HandleChild.OWLDatatypeFacetRestrictionEH;
    }

    @Override
    void handleChild(OWLLiteralEH h) {
        builder.withLiteral(h.getOWLObject());
    }

    @Override
    void attribute(String localName, String value) {
        if ("facet".equals(localName)) {
            builder.withFacet(OWLFacet.getFacet(IRI.create(value)));
        }
    }
}

class OWLDatatypeRestrictionEH extends AbstractOWLDataRangeHandler<OWLDatatypeRestriction, BuilderDatatypeRestriction> {

    public OWLDatatypeRestrictionEH() {
        super(BuilderDatatypeRestriction::new);
    }

    @Override
    void handleChild(DataRangeEH h) {
        OWLDataRange dr = h.getOWLObject();
        if (dr.isOWLDatatype()) {
            builder.withDatatype(dr.asOWLDatatype());
        }
    }

    @Override
    void handleChild(OWLDatatypeFacetRestrictionEH h) {
        builder.withItem(h.getOWLObject());
    }
}

class OWLDeclarationAxiomEH extends AbstractOWLAxiomEH<OWLDeclarationAxiom, BuilderDeclaration> {

    OWLDeclarationAxiomEH() {
        super(BuilderDeclaration::new);
    }

    @Override
    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {
        builder.withEntity(h.getOWLObject(OWLClassExpression.class).asOWLClass());
    }

    @Override
    void handleChild(ObjectPropertyEH h) {
        builder.withEntity(h.getOWLObject(OWLObjectPropertyExpression.class).asOWLObjectProperty());
    }

    @Override
    void handleChild(OWLDataPropertyEH h) {
        builder.withEntity(h.getOWLObject(OWLDataPropertyExpression.class).asOWLDataProperty());
    }

    @Override
    void handleChild(DataRangeEH h) {
        builder.withEntity(h.getOWLObject(OWLDataRange.class).asOWLDatatype());
    }

    @Override
    void handleChild(OWLAnnotationPropertyEH h) {
        builder.withEntity(h.getOWLObject(OWLAnnotationProperty.class).asOWLAnnotationProperty());
    }

    @Override
    void handleChild(OWLIndividualEH h) {
        builder.withEntity(h.getOWLObject(OWLIndividual.class).asOWLNamedIndividual());
    }

    @Override
    void handleChild(OWLAnnotationEH h) {
        if (builder.getEntity() == null) {
            super.handleChild(h);
        } else {
            builder.withAnnotation(h.getOWLObject());
        }
    }
}

class OWLSetAxiomEH<X extends OWLAxiom, Y extends OWLObject, B extends Builder<X> & SettableItem<Y, B>> extends
    AbstractOWLAxiomEH<X, B> {

    OWLSetAxiomEH(Function<OWLDataFactory, B> b) {
        super(b);
    }

    @Override
    void handleChild(OWLIndividualEH h) {
        builder.withItem(h.getOWLObject());
    }

    @Override
    void handleChild(OWLAnonymousIndividualEH h) {
        builder.withItem(h.getOWLObject());
    }

    @Override
    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {
        builder.withItem(h.getOWLObject());
    }

    @Override
    void handleChild(OWLDataPropertyEH h) {
        builder.withItem(h.getOWLObject());
    }

    @Override
    void handleChild(ObjectPropertyEH h) {
        builder.withItem(h.getOWLObject());
    }
}

class OWLDisjointUnionEH extends AbstractOWLAxiomEH<OWLDisjointUnionAxiom, BuilderDisjointUnion> {

    OWLDisjointUnionEH() {
        super(BuilderDisjointUnion::new);
    }

    @Override
    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {
        if (builder.getClassExpression() == null) {
            builder.withClass(h.getOWLObject(OWLClassExpression.class).asOWLClass());
        } else {
            builder.withItem(h.getOWLObject());
        }
    }
}

class OWLHasKeyEH extends AbstractOWLAxiomEH<OWLHasKeyAxiom, BuilderHasKey> {

    OWLHasKeyEH() {
        super(BuilderHasKey::new);
    }

    @Override
    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {
        builder.withClass(h.getOWLObject());
    }

    @Override
    void handleChild(OWLDataPropertyEH h) {
        builder.withItem(h.getOWLObject());
    }

    @Override
    void handleChild(ObjectPropertyEH h) {
        builder.withItem(h.getOWLObject());
    }
}

class OWLIndividualEH extends OWLEH<OWLNamedIndividual, BuilderNamedIndividual> {

    public OWLIndividualEH() {
        provider = BuilderNamedIndividual::new;
        child = HandleChild.OWLIndividualEH;
    }

    @Override
    void attribute(String localName, String value) {
        builder.withIRI(getIRIFromAttribute(localName, value));
    }
}

class OWLInverseObjectPropertiesAxiomEH extends
    AbstractOWLAxiomEH<OWLInverseObjectPropertiesAxiom, BuilderInverseObjectProperties> {

    OWLInverseObjectPropertiesAxiomEH() {
        super(BuilderInverseObjectProperties::new);
    }

    @Override
    void handleChild(ObjectPropertyEH h) {
        if (builder.getProperty() == null) {
            builder.withProperty(h.getOWLObject());
        } else {
            builder.withInverseProperty(h.getOWLObject());
        }
    }
}

class OWLInverseObjectPropertyEH extends OWLEH<OWLObjectInverseOf, BuilderObjectInverseOf> implements ObjectPropertyEH {

    public OWLInverseObjectPropertyEH() {
        provider = BuilderObjectInverseOf::new;
        child = HandleChild.ObjectPropertyEH;
    }
}

class OWLLiteralEH extends OWLEH<OWLLiteral, BuilderLiteral> {

    public OWLLiteralEH() {
        provider = BuilderLiteral::new;
        child = HandleChild.OWLLiteralEH;
    }

    @Override
    void attribute(String localName, String value) {
        if (localName.equals(DATATYPE_IRI.getShortForm())) {
            IRI iri = handler.getIRI(value);
            OWLDatatype type = df.getOWLDatatype(iri);
            // do not set the type for string types - it overrides the language
            // tag if one exists
            if (!OWL2Datatype.RDF_LANG_STRING.matches(type) && !OWL2Datatype.RDF_PLAIN_LITERAL.matches(type)
                && !OWL2Datatype.XSD_STRING.matches(type)) {
                builder.withDatatype(iri);
            }
        } else if ("lang".equals(localName)) {
            builder.withLanguage(value);
        }
    }

    @Override
    void endElement() {
        builder.withLiteralForm(getText());
        getParentHandler().handleChild(this);
    }

    @Override
    boolean isTextContentPossible() {
        return true;
    }
}

class OWLNegativeDataPropertyAssertionAxiomEH extends
    AbstractOWLAxiomEH<OWLNegativeDataPropertyAssertionAxiom, BuilderNegativeDataPropertyAssertion> {

    OWLNegativeDataPropertyAssertionAxiomEH() {
        super(BuilderNegativeDataPropertyAssertion::new);
    }

    @Override
    void handleChild(OWLAnonymousIndividualEH h) {
        builder.withSubject(h.getOWLObject());
    }

    @Override
    void handleChild(OWLIndividualEH h) {
        builder.withSubject(h.getOWLObject());
    }

    @Override
    void handleChild(OWLLiteralEH h) {
        builder.withValue(h.getOWLObject());
    }
}

class OWLNegativeObjectPropertyAssertionAxiomEH extends
    AbstractOWLAxiomEH<OWLNegativeObjectPropertyAssertionAxiom, BuilderNegativeObjectPropertyAssertion> {

    OWLNegativeObjectPropertyAssertionAxiomEH() {
        super(BuilderNegativeObjectPropertyAssertion::new);
    }

    @Override
    void handleChild(OWLAnonymousIndividualEH h) {
        internalSet(h.getOWLObject());
    }

    public void internalSet(OWLIndividual h) {
        if (builder.getSubject() == null) {
            builder.withSubject(h);
        } else if (builder.getValue() == null) {
            builder.withValue(h);
        }
    }

    @Override
    void handleChild(OWLIndividualEH h) {
        internalSet(h.getOWLObject());
    }
}

class OWLObjectComplementOfEH extends AbstractClassExpressionEH<OWLObjectComplementOf, BuilderComplementOf> {

    public OWLObjectComplementOfEH() {
        super(BuilderComplementOf::new);
    }

    @Override
    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {
        builder.withClass(h.getOWLObject());
    }
}

class OWLObjectHasValueEH extends AbstractClassExpressionEH<OWLObjectHasValue, BuilderObjectHasValue> {

    public OWLObjectHasValueEH() {
        super(BuilderObjectHasValue::new);
    }

    @Override
    void handleChild(OWLIndividualEH h) {
        builder.withValue(h.getOWLObject());
    }

    @Override
    void handleChild(OWLAnonymousIndividualEH h) {
        builder.withValue(h.getOWLObject());
    }
}

class OWLObjectIntersectionOfEH extends
    AbstractClassExpressionEH<OWLObjectIntersectionOf, BuilderObjectIntersectionOf> {

    public OWLObjectIntersectionOfEH() {
        super(BuilderObjectIntersectionOf::new);
    }

    @Override
    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {
        builder.withItem(h.getOWLObject());
    }
}

class OWLObjectOneOfEH extends AbstractClassExpressionEH<OWLObjectOneOf, BuilderOneOf> {

    public OWLObjectOneOfEH() {
        super(BuilderOneOf::new);
    }

    @Override
    void handleChild(OWLIndividualEH h) {
        builder.withItem(h.getOWLObject());
    }
}

class OWLObjectPropertyAssertionAxiomEH extends
    AbstractOWLAxiomEH<OWLObjectPropertyAssertionAxiom, BuilderObjectPropertyAssertion> {

    OWLObjectPropertyAssertionAxiomEH() {
        super(BuilderObjectPropertyAssertion::new);
    }

    @Override
    void handleChild(OWLAnonymousIndividualEH h) {
        internalSet(h.getOWLObject());
    }

    public void internalSet(OWLIndividual h) {
        if (builder.getSubject() == null) {
            builder.withSubject(h);
        } else if (builder.getValue() == null) {
            builder.withValue(h);
        }
    }

    @Override
    void handleChild(OWLIndividualEH h) {
        internalSet(h.getOWLObject());
    }
}

class OWLObjectPropertyDomainEH extends AbstractOWLAxiomEH<OWLObjectPropertyDomainAxiom, BuilderObjectPropertyDomain> {

    OWLObjectPropertyDomainEH() {
        super(BuilderObjectPropertyDomain::new);
    }

    @Override
    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {
        builder.withDomain(h.getOWLObject());
    }
}

class OWLObjectPropertyEH extends AbstractOWLObjectPropertyEH {

    @Override
    void attribute(String localName, String value) {
        builder.withIRI(getIRIFromAttribute(localName, value));
    }
}

class OWLObjectPropertyRangeAxiomEH extends
    AbstractOWLAxiomEH<OWLObjectPropertyRangeAxiom, BuilderObjectPropertyRange> {

    OWLObjectPropertyRangeAxiomEH() {
        super(BuilderObjectPropertyRange::new);
    }

    @Override
    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {
        builder.withRange(h.getOWLObject());
    }
}

class OWLObjectUnionOfEH extends AbstractClassExpressionEH<OWLObjectUnionOf, BuilderUnionOf> {

    public OWLObjectUnionOfEH() {
        super(BuilderUnionOf::new);
    }

    @Override
    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {
        builder.withItem(h.getOWLObject());
    }
}

class OWLSubAnnotationPropertyOfEH extends
    AbstractOWLAxiomEH<OWLSubAnnotationPropertyOfAxiom, BuilderSubAnnotationPropertyOf> {

    OWLSubAnnotationPropertyOfEH() {
        super(BuilderSubAnnotationPropertyOf::new);
    }

    @Override
    void handleChild(OWLAnnotationPropertyEH h) {
        if (builder.getSub() == null) {
            builder.withSub(h.getOWLObject());
        } else if (builder.getSup() == null) {
            builder.withSup(h.getOWLObject());
        } else {
            ensureNotNull(null, "two annotation properties elements");
        }
    }
}

class OWLSubClassAxiomEH extends AbstractOWLAxiomEH<OWLSubClassOfAxiom, BuilderSubClass> {

    OWLSubClassAxiomEH() {
        super(BuilderSubClass::new);
    }

    @Override
    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {
        if (builder.getSub() == null) {
            builder.withSub(h.getOWLObject());
        } else if (builder.getSup() == null) {
            builder.withSup(h.getOWLObject());
        }
    }
}

class OWLSubDataPropertyOfAxiomEH extends AbstractOWLAxiomEH<OWLSubDataPropertyOfAxiom, BuilderSubDataProperty> {

    OWLSubDataPropertyOfAxiomEH() {
        super(BuilderSubDataProperty::new);
    }

    @Override
    void handleChild(OWLDataPropertyEH h) {
        if (builder.getSub() == null) {
            builder.withSub(h.getOWLObject());
        } else if (builder.getSup() == null) {
            builder.withSup(h.getOWLObject());
        }
    }
}

class OWLSubObjectPropertyChainEH extends
    OWLEH<List<OWLObjectPropertyExpression>, Builder<List<OWLObjectPropertyExpression>>> {

    final List<OWLObjectPropertyExpression> propertyList = new ArrayList<>();

    public OWLSubObjectPropertyChainEH() {
        child = HandleChild.OWLSubObjectPropertyChainEH;
    }

    @Override
    public List<OWLObjectPropertyExpression> getOWLObject() {
        return propertyList;
    }

    @Override
    void handleChild(ObjectPropertyEH h) {
        propertyList.add(h.getOWLObject());
    }
}

class OWLSubObjectPropertyOfAxiomEH extends AbstractOWLAxiomEH<OWLSubObjectPropertyOfAxiom, BuilderSubObjectProperty> {

    BuilderPropertyChain chain;

    OWLSubObjectPropertyOfAxiomEH() {
        super(BuilderSubObjectProperty::new);
    }

    @Override
    void setHandler(OWLXMLPH handler) {
        super.setHandler(handler);
        chain = new BuilderPropertyChain(df);
    }

    @Override
    void handleChild(ObjectPropertyEH h) {
        OWLObjectPropertyExpression prop = h.getOWLObject();
        if (builder.getSub() == null && chain.chainSize() == 0) {
            builder.withSub(prop);
        } else if (builder.getSup() == null) {
            builder.withSup(prop);
            chain.withProperty(prop);
        } else {
            ensureNotNull(null, "Expected two object property expression elements");
        }
    }

    @Override
    void handleChild(OWLAnnotationEH h) {
        super.handleChild(h);
        chain.withAnnotation(h.getOWLObject());
    }

    @Override
    void handleChild(OWLSubObjectPropertyChainEH h) {
        chain.withPropertiesInChain(h.getOWLObject());
    }

    @Override
    public <T> T getOWLObject() {
        if (chain.chainSize() > 0) {
            return (T) chain.buildObject();
        }
        return (T) builder.buildObject();
    }
}

abstract class SWRLAtomEH extends OWLEH<SWRLAtom, Builder<SWRLAtom>> {

    SWRLAtom atom;

    public SWRLAtomEH() {
        child = HandleChild.SWRLAtomEH;
    }

    void setAtom(SWRLAtom atom) {
        this.atom = atom;
    }

    @Override
    public <T> T getOWLObject() {
        return (T) verifyNotNull(atom);
    }
}

class SWRLAtomListEH extends OWLEH<List<SWRLAtom>, Builder<List<SWRLAtom>>> {

    final List<SWRLAtom> atoms = new ArrayList<>();

    public SWRLAtomListEH() {
        child = HandleChild.SWRLAtomListEH;
    }

    @Override
    void handleChild(SWRLAtomEH h) {
        atoms.add(h.getOWLObject());
    }

    @Override
    public List<SWRLAtom> getOWLObject() {
        return atoms;
    }
}

class SWRLBuiltInAtomEH extends SWRLAtomEH {

    IRI iri;
    final List<SWRLDArgument> args = new ArrayList<>();

    @Override
    void attribute(String localName, String value) {
        iri = getIRIFromAttribute(localName, value);
    }

    @Override
    void handleChild(SWRLVariableEH h) {
        args.add(h.getOWLObject());
    }

    @Override
    void handleChild(OWLLiteralEH h) {
        args.add(df.getSWRLLiteralArgument(h.getOWLObject()));
    }

    @Override
    void endElement() {
        setAtom(df.getSWRLBuiltInAtom(verifyNotNull(iri), args));
        getParentHandler().handleChild(this);
    }
}

class SWRLClassAtomEH extends SWRLAtomEH {

    OWLClassExpression ce;
    SWRLIArgument arg;

    @Override
    void handleChild(SWRLVariableEH h) {
        arg = h.getOWLObject();
    }

    @Override
    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {
        ce = h.getOWLObject();
    }

    @Override
    void handleChild(OWLIndividualEH h) {
        arg = df.getSWRLIndividualArgument(h.getOWLObject());
    }

    @Override
    void endElement() {
        setAtom(df.getSWRLClassAtom(verifyNotNull(ce), verifyNotNull(arg)));
        getParentHandler().handleChild(this);
    }
}

class SWRLDataPropertyAtomEH extends SWRLAtomEH {

    OWLDataPropertyExpression prop;
    SWRLIArgument arg0;
    SWRLDArgument arg1;

    @Override
    void handleChild(OWLDataPropertyEH h) {
        prop = h.getOWLObject();
    }

    @Override
    void handleChild(SWRLVariableEH h) {
        if (arg0 == null) {
            arg0 = h.getOWLObject();
        } else if (arg1 == null) {
            arg1 = h.getOWLObject();
        }
    }

    @Override
    void handleChild(OWLLiteralEH h) {
        arg1 = df.getSWRLLiteralArgument(h.getOWLObject());
    }

    @Override
    void handleChild(OWLIndividualEH h) {
        arg0 = df.getSWRLIndividualArgument(h.getOWLObject());
    }

    @Override
    void handleChild(OWLAnonymousIndividualEH h) {
        arg0 = df.getSWRLIndividualArgument(h.getOWLObject());
    }

    @Override
    void endElement() {
        setAtom(df.getSWRLDataPropertyAtom(verifyNotNull(prop), verifyNotNull(arg0), verifyNotNull(arg1)));
        getParentHandler().handleChild(this);
    }
}

class SWRLDataRangeAtomEH extends SWRLAtomEH {

    OWLDataRange prop;
    SWRLDArgument arg1;

    @Override
    void handleChild(DataRangeEH h) {
        prop = h.getOWLObject();
    }

    @Override
    void handleChild(SWRLVariableEH h) {
        arg1 = h.getOWLObject();
    }

    @Override
    void handleChild(OWLLiteralEH h) {
        arg1 = df.getSWRLLiteralArgument(h.getOWLObject());
    }

    @Override
    void endElement() {
        setAtom(df.getSWRLDataRangeAtom(verifyNotNull(prop), verifyNotNull(arg1)));
        getParentHandler().handleChild(this);
    }
}

class SWRLDifferentIndividualsAtomEH extends SWRLAtomEH {

    SWRLIArgument arg0;
    SWRLIArgument arg1;

    @Override
    void handleChild(SWRLVariableEH h) {
        if (arg0 == null) {
            arg0 = h.getOWLObject();
        } else if (arg1 == null) {
            arg1 = h.getOWLObject();
        }
    }

    @Override
    void handleChild(OWLIndividualEH h) {
        if (arg0 == null) {
            arg0 = df.getSWRLIndividualArgument(h.getOWLObject());
        } else if (arg1 == null) {
            arg1 = df.getSWRLIndividualArgument(h.getOWLObject());
        }
    }

    @Override
    void endElement() {
        setAtom(df.getSWRLDifferentIndividualsAtom(verifyNotNull(arg0), verifyNotNull(arg1)));
        getParentHandler().handleChild(this);
    }
}

class SWRLObjectPropertyAtomEH extends SWRLAtomEH {

    OWLObjectPropertyExpression prop;
    SWRLIArgument arg0;
    SWRLIArgument arg1;

    @Override
    void handleChild(ObjectPropertyEH h) {
        prop = h.getOWLObject();
    }

    @Override
    void handleChild(SWRLVariableEH h) {
        if (arg0 == null) {
            arg0 = h.getOWLObject();
        } else if (arg1 == null) {
            arg1 = h.getOWLObject();
        }
    }

    @Override
    void handleChild(OWLIndividualEH h) {
        if (arg0 == null) {
            arg0 = df.getSWRLIndividualArgument(h.getOWLObject());
        } else if (arg1 == null) {
            arg1 = df.getSWRLIndividualArgument(h.getOWLObject());
        }
    }

    @Override
    void endElement() {
        setAtom(df.getSWRLObjectPropertyAtom(verifyNotNull(prop), verifyNotNull(arg0), verifyNotNull(arg1)));
        getParentHandler().handleChild(this);
    }
}

class SWRLRuleEH extends AbstractOWLAxiomEH<SWRLRule, BuilderSWRLRule> {

    SWRLRuleEH() {
        super(BuilderSWRLRule::new);
    }

    @Override
    void handleChild(SWRLAtomListEH h) {
        if (builder.bodySize() == 0) {
            builder.withBody(h.getOWLObject());
        } else if (builder.headSize() == 0) {
            builder.withHead(h.getOWLObject());
        }
    }
}

class SWRLSameIndividualAtomEH extends SWRLAtomEH {

    SWRLIArgument arg0;
    SWRLIArgument arg1;

    @Override
    void handleChild(SWRLVariableEH h) {
        if (arg0 == null) {
            arg0 = h.getOWLObject();
        } else if (arg1 == null) {
            arg1 = h.getOWLObject();
        }
    }

    @Override
    void handleChild(OWLIndividualEH h) {
        if (arg0 == null) {
            arg0 = df.getSWRLIndividualArgument(h.getOWLObject());
        } else if (arg1 == null) {
            arg1 = df.getSWRLIndividualArgument(h.getOWLObject());
        }
    }

    @Override
    void endElement() {
        setAtom(df.getSWRLSameIndividualAtom(verifyNotNull(arg0), verifyNotNull(arg1)));
        getParentHandler().handleChild(this);
    }
}

class SWRLVariableEH extends OWLEH<SWRLVariable, BuilderSWRLVariable> {

    public SWRLVariableEH() {
        child = HandleChild.SWRLVariableEH;
        provider = BuilderSWRLVariable::new;
    }

    @Override
    void attribute(String localName, String value) {
        builder.with(getIRIFromAttribute(localName, value));
    }
}

class OWLOntologyHandler extends OWLEH<OWLOntology, Builder<OWLOntology>> {

    @Override
    void startElement(String name) {
        // nothing to do here
    }

    @Override
    void attribute(String localName, String value) {
        if ("ontologyIRI".equals(localName)) {
            OWLOntologyID newID = new OWLOntologyID(optional(IRI.create(value)), handler.getOntology().getOntologyID()
                .getVersionIRI());
            handler.getOWLOntologyManager().applyChange(new SetOntologyID(handler.getOntology(), newID));
        }
        if ("versionIRI".equals(localName)) {
            OWLOntologyID newID = new OWLOntologyID(handler.getOntology().getOntologyID().getOntologyIRI(), optional(IRI
                .create(value)));
            handler.getOWLOntologyManager().applyChange(new SetOntologyID(handler.getOntology(), newID));
        }
    }

    @Override
    void handleChild(DataRangeEH h) {
        // nothing to do here
    }

    @Override
    void handleChild(AbstractClassExpressionEH<? extends OWLClassExpression, ?> h) {
        // nothing to do here
    }

    @Override
    void handleChild(OWLAnnotationEH h) {
        handler.getOWLOntologyManager().applyChange(new AddOntologyAnnotation(handler.getOntology(), h.getOWLObject()));
    }

    @Override
    void endElement() {
        // nothing to do here
    }

    @Override
    public OWLOntology getOWLObject() {
        return handler.getOntology();
    }

    @Override
    void setParentHandler(OWLEH<?, ?> handler) {
        // nothing to do here
    }
}

class OWLImportsHandler extends OWLEH<OWLOntology, Builder<OWLOntology>> {

    @Override
    void endElement() {
        IRI ontIRI = handler.getIRI(getText().trim());
        OWLImportsDeclaration decl = df.getOWLImportsDeclaration(ontIRI);
        handler.getOWLOntologyManager().applyChange(new AddImport(handler.getOntology(), decl));
        handler.getOWLOntologyManager().makeLoadImportRequest(decl, handler.getConfiguration());
    }

    @Override
    public OWLOntology getOWLObject() {
        throw new OWLRuntimeException("There is no OWLObject for imports handlers");
    }

    @Override
    boolean isTextContentPossible() {
        return true;
    }
}
