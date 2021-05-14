package graphql.schema

import graphql.AssertException
import spock.lang.Specification

import static graphql.Scalars.GraphQLBoolean
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLUnionType.newUnionType

class GraphQLUnionTypeTest extends Specification {

    def "no possible types in union fails"() {
        when:
        newUnionType()
                .name("TestUnionType")
                .typeResolver(new TypeResolverProxy())
                .build()
        then:
        thrown(AssertException)
    }

    def objType1 = newObject().name("T1")
            .field(newFieldDefinition().name("f1").type(GraphQLBoolean))
            .build()
    def objType2 = newObject().name("T2")
            .field(newFieldDefinition().name("f1").type(GraphQLBoolean))
            .build()

    def objType3 = newObject().name("T3")
            .field(newFieldDefinition().name("f2").type(GraphQLBoolean))
            .build()

    def "object transformation works as expected"() {

        given:
        def startingUnion = newUnionType().name("StartingType")
                .description("StartingDescription")
                .possibleType(objType1)
                .possibleType(objType2)
                .typeResolver(new TypeResolverProxy())
                .build()

        when:
        def transformedUnion = startingUnion.transform({ builder ->
            builder
                    .name("NewName")
                    .description("NewDescription")
                    .clearPossibleTypes()
                    .possibleType(objType3)
        })
        then:

        startingUnion.getName() == "StartingType"
        startingUnion.getDescription() == "StartingDescription"
        startingUnion.getTypes().size() == 2

        startingUnion.isPossibleType(objType2)
        !startingUnion.isPossibleType(objType3)

        transformedUnion.getName() == "NewName"
        transformedUnion.getDescription() == "NewDescription"
        transformedUnion.getTypes().size() == 1
        !transformedUnion.isPossibleType(objType2)
        transformedUnion.isPossibleType(objType3)
    }

    def "Differently wrapped types are not considered equal"() {
        given:
        def someType = newUnionType().name("SomeType")
                .possibleType(objType1)
                .possibleType(objType2)
                .build()
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
        def someType1 = newUnionType().name("SomeType")
                .possibleType(objType1)
                .possibleType(objType2)
                .build()
        def someType2 = newUnionType().name("SomeType")
                .possibleType(objType1)
                .possibleType(objType2)
                .build()

        expect:
        someType1.equals(someType2) == true
    }

    def "Differently named types of same kind are not considered equal"() {
        given:
        def someType1 = newUnionType().name("SomeType1")
                .possibleType(objType1)
                .possibleType(objType2)
                .build()
        def someType2 = newUnionType().name("SomeType2")
                .possibleType(objType1)
                .possibleType(objType2)
                .build()

        expect:
        someType1.equals(someType2) == false
    }

    def "Same-name types have equal hash codes"() {
        given:
        def someType1 = newUnionType().name("SomeType")
                .possibleType(objType1)
                .possibleType(objType2)
                .build()
        def someType2 = newUnionType().name("SomeType")
                .possibleType(objType1)
                .possibleType(objType2)
                .build()

        expect:
        someType1.hashCode() == someType2.hashCode();
    }

    def "Type is equal to itself"() {
        given:
        def someType = newUnionType().name("SomeType")
                .possibleType(objType1)
                .possibleType(objType2)
                .build()

        expect:
        someType.equals(someType) == true
    }
}
