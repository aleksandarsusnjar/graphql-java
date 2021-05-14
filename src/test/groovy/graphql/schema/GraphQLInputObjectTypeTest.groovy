package graphql.schema

import graphql.AssertException
import spock.lang.Specification

import static graphql.Scalars.GraphQLBoolean
import static graphql.Scalars.GraphQLInt
import static graphql.Scalars.GraphQLString
import static graphql.schema.GraphQLInputObjectField.newInputObjectField
import static graphql.schema.GraphQLInputObjectType.newInputObject
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLObjectType.newObject

class GraphQLInputObjectTypeTest extends Specification {

    def "duplicate field definition fails"() {
        when:
        // preserve old constructor behavior test
        new GraphQLInputObjectType("TestInputObjectType", "description",
                [
                        newInputObjectField().name("NAME").type(GraphQLString).build(),
                        newInputObjectField().name("NAME").type(GraphQLString).build()
                ])
        then:
        thrown(AssertException)
    }


    def "duplicate field definition overwrites"() {
        when:
        def inputObjectType = newInputObject().name("TestInputObjectType")
                .field(newInputObjectField().name("NAME").type(GraphQLString))
                .field(newInputObjectField().name("NAME").type(GraphQLInt))
                .build()
        then:
        inputObjectType.getName() == "TestInputObjectType"
        inputObjectType.getFieldDefinition("NAME").getType() == GraphQLInt
    }

    def "builder can change existing object into a new one"() {
        given:
        def inputObjectType = newInputObject().name("StartType")
                .description("StartingDescription")
                .field(newInputObjectField().name("Str").type(GraphQLString))
                .field(newInputObjectField().name("Int").type(GraphQLInt))
                .build()

        when:
        def transformedInputType = inputObjectType.transform({ builder ->
            builder
                    .name("NewObjectName")
                    .description("NewDescription")
                    .field(newInputObjectField().name("AddedInt").type(GraphQLInt)) // add more
                    .field(newInputObjectField().name("Int").type(GraphQLInt)) // override and change
                    .field(newInputObjectField().name("Str").type(GraphQLBoolean)) // override and change
        })
        then:

        inputObjectType.getName() == "StartType"
        inputObjectType.getDescription() == "StartingDescription"
        inputObjectType.getFieldDefinitions().size() == 2
        inputObjectType.getFieldDefinition("Int").getType() == GraphQLInt
        inputObjectType.getFieldDefinition("Str").getType() == GraphQLString

        transformedInputType.getName() == "NewObjectName"
        transformedInputType.getDescription() == "NewDescription"
        transformedInputType.getFieldDefinitions().size() == 3
        transformedInputType.getFieldDefinition("AddedInt").getType() == GraphQLInt
        transformedInputType.getFieldDefinition("Int").getType() == GraphQLInt
        transformedInputType.getFieldDefinition("Str").getType() == GraphQLBoolean
    }

    def "Differently wrapped types are not considered equal"() {
        given:
        def someType = newInputObject().name("SomeType").build()
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
        def someType1 = newInputObject().name("SomeType").build()
        def someType2 = newInputObject().name("SomeType").build()

        expect:
        someType1.equals(someType2) == true
    }

    def "Differently named types of same kind are not considered equal"() {
        given:
        def someType1 = newInputObject().name("SomeType1").build()
        def someType2 = newInputObject().name("SomeType2").build()

        expect:
        someType1.equals(someType2) == false
    }

    def "Same-name types have equal hash codes"() {
        given:
        def someType1 = newInputObject().name("SomeType").build()
        def someType2 = newInputObject().name("SomeType").build()

        expect:
        someType1.hashCode() == someType2.hashCode();
    }

    def "Type is equal to itself"() {
        given:
        def someType = newInputObject().name("SomeType").build()

        expect:
        someType.equals(someType) == true
    }

}
