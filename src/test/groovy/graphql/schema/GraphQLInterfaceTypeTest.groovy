package graphql.schema

import graphql.AssertException
import spock.lang.Specification

import static graphql.Scalars.GraphQLBoolean
import static graphql.Scalars.GraphQLInt
import static graphql.Scalars.GraphQLString
import static graphql.schema.GraphQLEnumType.newEnum
import static graphql.schema.GraphQLEnumType.newEnum
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLObjectType.newObject

class GraphQLInterfaceTypeTest extends Specification {

    def "duplicate field definition fails"() {
        when:
        // preserve old constructor behavior test
        new GraphQLInterfaceType("TestInputObjectType", "description",
                [
                        newFieldDefinition().name("NAME").type(GraphQLString).build(),
                        newFieldDefinition().name("NAME").type(GraphQLString).build()
                ], new TypeResolverProxy())
        then:
        thrown(AssertException)
    }

    def "builder can change existing object into a new one"() {
        given:
        def startingInterface = newInterface().name("StartingType")
                .description("StartingDescription")
                .field(newFieldDefinition().name("Str").type(GraphQLString))
                .field(newFieldDefinition().name("Int").type(GraphQLInt))
                .typeResolver(new TypeResolverProxy())
                .build()

        when:
        def objectType2 = startingInterface.transform({ builder ->
            builder
                    .name("NewName")
                    .description("NewDescription")
                    .field(newFieldDefinition().name("AddedInt").type(GraphQLInt)) // add more
                    .field(newFieldDefinition().name("Int").type(GraphQLInt)) // override and change
                    .field(newFieldDefinition().name("Str").type(GraphQLBoolean)) // override and change
        })
        then:

        startingInterface.getName() == "StartingType"
        startingInterface.getDescription() == "StartingDescription"
        startingInterface.getFieldDefinitions().size() == 2
        startingInterface.getFieldDefinition("Int").getType() == GraphQLInt
        startingInterface.getFieldDefinition("Str").getType() == GraphQLString

        objectType2.getName() == "NewName"
        objectType2.getDescription() == "NewDescription"
        objectType2.getFieldDefinitions().size() == 3
        objectType2.getFieldDefinition("AddedInt").getType() == GraphQLInt
        objectType2.getFieldDefinition("Int").getType() == GraphQLInt
        objectType2.getFieldDefinition("Str").getType() == GraphQLBoolean
    }

    def "Differently wrapped types are not considered equal"() {
        given:
        def someType = newInterface().name("SomeType").build()
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
        def someType1 = newInterface().name("SomeType").build()
        def someType2 = newInterface().name("SomeType").build()

        expect:
        someType1.equals(someType2) == true
    }

    def "Different-name types of same kind are not considered equal"() {
        given:
        def someType1 = newInterface().name("SomeType1").build()
        def someType2 = newInterface().name("SomeType2").build()

        expect:
        someType1.equals(someType2) == false
    }

    def "Same-name types have equal hash codes"() {
        given:
        def someType1 = newInterface().name("SomeType").build()
        def someType2 = newInterface().name("SomeType").build()

        expect:
        someType1.hashCode() == someType2.hashCode();
    }

    def "Type is equal to itself"() {
        given:
        def someType = newInterface().name("SomeType").build()

        expect:
        someType.equals(someType) == true
    }
}
