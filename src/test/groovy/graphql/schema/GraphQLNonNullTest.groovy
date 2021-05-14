package graphql.schema

import graphql.AssertException
import spock.lang.Specification

import static graphql.Scalars.GraphQLInt
import static graphql.Scalars.GraphQLString
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLInterfaceType.newInterface

class GraphQLNonNullTest extends Specification {

    def "non null wrapping"() {
        when:
        GraphQLNonNull.nonNull(GraphQLString)
        then:
        noExceptionThrown()

        when:
        GraphQLNonNull.nonNull(GraphQLList.list(GraphQLString))
        then:
        noExceptionThrown()

        when:
        GraphQLNonNull.nonNull(GraphQLNonNull.nonNull(GraphQLList.list(GraphQLString)))
        then:
        thrown(AssertException)
    }

    def "Differently wrapped types are not considered equal"() {
        given:
        def someType = newInterface().name("SomeType").build()
        def someTypeL = GraphQLList.list(someType)
        def someTypeNL = GraphQLNonNull.nonNull(someTypeL)

        expect:
        someType.equals(someTypeL) == false
        someType.equals(someTypeNL) == false
    }

    def "Same-name types of same kind are considered equal"() {
        given:
        def someType1 = GraphQLNonNull.nonNull(GraphQLString)
        def someType2 = GraphQLNonNull.nonNull(GraphQLString)

        expect:
        someType1.equals(someType2) == true
    }

    def "Non-null wrappings of different types are not considered equal"() {
        given:
        def someType1 = GraphQLNonNull.nonNull(GraphQLString)
        def someType2 = GraphQLNonNull.nonNull(GraphQLInt)

        expect:
        someType1.equals(someType2) == false
    }

    def "Same-name types have equal hash codes"() {
        given:
        def someType1 = GraphQLNonNull.nonNull(GraphQLString)
        def someType2 = GraphQLNonNull.nonNull(GraphQLString)

        expect:
        someType1.hashCode() == someType2.hashCode();
    }

    def "Type is equal to itself"() {
        given:
        def someType = GraphQLNonNull.nonNull(GraphQLString)

        expect:
        someType.equals(someType) == true
    }
}
