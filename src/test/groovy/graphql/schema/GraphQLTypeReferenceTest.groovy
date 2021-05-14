package graphql.schema

import graphql.Scalars
import spock.lang.Specification

import static graphql.Scalars.GraphQLInt
import static graphql.Scalars.GraphQLString
import static graphql.Scalars.GraphQLString
import static graphql.Scalars.GraphQLString
import static graphql.Scalars.GraphQLString
import static graphql.Scalars.GraphQLString
import static graphql.Scalars.GraphQLString
import static graphql.schema.GraphQLArgument.newArgument
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import static graphql.schema.GraphQLInputObjectField.newInputObjectField
import static graphql.schema.GraphQLInputObjectType.newInputObject
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLSchema.newSchema

class GraphQLTypeReferenceTest extends Specification {

    def "the same reference can be used multiple times without throwing exception"() {
        when:
        GraphQLTypeReference ref = new GraphQLTypeReference("String")
        def inputObject = newInputObject()
                .name("ObjInput")
                .field(newInputObjectField()
                .name("value")
                .type(ref)) //Will get replaced, as expected
                .field(newInputObjectField()
                .name("value2")
                .type(ref)) //Will get replaced, as expected
                .build()

        GraphQLSchema schema = newSchema()
                .query(
                newObject()
                        .name("Query")
                        .field(newFieldDefinition()
                        .name("test")
                        .type(Scalars.GraphQLString)
                        .argument(newArgument()
                        .name("in")
                        .type(inputObject))
                )).build()

        then:
        // issue 1216 - reuse of type reference caused problems
        schema != null
        GraphQLInputObjectType objInput = ((GraphQLInputObjectType) schema.getType("ObjInput"))
        objInput.getField("value").getType() != ref
        objInput.getField("value").getType() instanceof GraphQLScalarType
        objInput.getField("value2").getType() != ref
        objInput.getField("value2").getType() instanceof GraphQLScalarType
    }

    def "Differently wrapped types are not considered equal"() {
        given:
        def someType = new GraphQLTypeReference("SomeType")
        def someTypeN = GraphQLNonNull.nonNull(someType)
        def someTypeL = GraphQLList.list(someType)
        def someTypeLN = GraphQLList.list(someTypeN)
        def someTypeNL = GraphQLNonNull.nonNull(someTypeL)
        def someTypeNLN = GraphQLNonNull.nonNull(someTypeLN)

        expect:
        someType.equals(someTypeN) == false
        someType.equals(someTypeL) == false
        someType.equals(someTypeLN) == false
        someType.equals(someTypeNL) == false
        someType.equals(someTypeNLN) == false
    }

    def "Same-name types of same kind are considered equal"() {
        given:
        def someType1 = new GraphQLTypeReference("SomeType")
        def someType2 = new GraphQLTypeReference("SomeType")

        expect:
        someType1.equals(someType2) == true
    }

    def "Non-null wrappings of different types are not considered equal"() {
        given:
        def someType1 = new GraphQLTypeReference("SomeType1")
        def someType2 = new GraphQLTypeReference("SomeType2")

        expect:
        someType1.equals(someType2) == false
    }

    def "Same-name types have equal hash codes"() {
        given:
        def someType1 = new GraphQLTypeReference("SomeType")
        def someType2 = new GraphQLTypeReference("SomeType")

        expect:
        someType1.hashCode() == someType2.hashCode();
    }

    def "Type is equal to itself"() {
        given:
        def someType = new GraphQLTypeReference("SomeType")

        expect:
        someType.equals(someType) == true
    }
}
