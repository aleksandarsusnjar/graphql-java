package graphql.schema


import spock.lang.Specification

import static graphql.Scalars.GraphQLInt
import static graphql.Scalars.GraphQLString

class GraphQLListTest extends Specification {

    def "list wrapping"() {
        when:
        GraphQLList.list(GraphQLString)
        then:
        noExceptionThrown()

        when:
        GraphQLList.list(GraphQLNonNull.nonNull(GraphQLString))
        then:
        noExceptionThrown()
    }

    def "Differently wrapped types are not equal"() {
        given:
        def someType = GraphQLList.list(GraphQLString)
        def someTypeNL = GraphQLNonNull.nonNull(someType)
        def someTypeLN = GraphQLList.list(GraphQLNonNull.nonNull(GraphQLString))
        def someTypeNLN = GraphQLNonNull.nonNull(GraphQLList.list(GraphQLNonNull.nonNull(GraphQLString)))

        expect:
        someType.equals(someTypeNL) == false
        someType.equals(someTypeLN) == false
        someType.equals(someTypeNLN) == false
    }

    def "Same-name types of same kind are considered equal"() {
        given:
        def someType1 = GraphQLList.list(GraphQLString)
        def someType2 = GraphQLList.list(GraphQLString)

        expect:
        someType1.equals(someType2) == true
    }

    def "Lists of different types are not considered equal"() {
        given:
        def someType1 = GraphQLList.list(GraphQLString)
        def someType2 = GraphQLList.list(GraphQLInt)

        expect:
        someType1.equals(someType2) == false
    }

    def "Same-name types have equal hash codes"() {
        given:
        def someType1 = GraphQLList.list(GraphQLString)
        def someType2 = GraphQLList.list(GraphQLString)

        expect:
        someType1.hashCode() == someType2.hashCode()
    }

    def "Type is equal to itself"() {
        given:
        def someType = GraphQLList.list(GraphQLString)

        expect:
        someType.equals(someType) == true
    }
}