package graphql.schema

import spock.lang.Specification

import static graphql.schema.GraphQLDirective.newDirective
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLObjectType.newObject
import static graphql.schema.GraphQLUnionType.newUnionType

class GraphQLScalarTypeTest extends Specification {
    Coercing<String, String> coercing = new Coercing<String, String>() {
        @Override
        String serialize(Object dataFetcherResult) throws CoercingSerializeException {
            return null
        }

        @Override
        String parseValue(Object input) throws CoercingParseValueException {
            return null
        }

        @Override
        String parseLiteral(Object input) throws CoercingParseLiteralException {
            return null
        }
    }

    def "builder works as expected"() {
        given:
        def startingScalar = GraphQLScalarType.newScalar()
                .name("S1")
                .description("S1_description")
                .coercing(coercing)
                .withDirective(newDirective().name("directive1"))
                .withDirective(newDirective().name("directive2"))
                .build()
        when:
        def transformedScalar = startingScalar.transform({ builder ->
            builder.name("S2")
                    .description("S2_description")
                    .withDirective(newDirective().name("directive3"))
        })

        then:
        startingScalar.getName() == "S1"
        startingScalar.getDescription() == "S1_description"
        startingScalar.getCoercing() == coercing

        startingScalar.getDirectives().size() == 2
        startingScalar.getDirective("directive1") != null
        startingScalar.getDirective("directive2") != null

        transformedScalar.name == "S2"
        transformedScalar.description == "S2_description"
        startingScalar.getCoercing() == coercing

        transformedScalar.getDirectives().size() == 3
        transformedScalar.getDirective("directive1") != null
        transformedScalar.getDirective("directive2") != null
        transformedScalar.getDirective("directive3") != null

    }

    def "Differently wrapped types are not considered equal"() {
        given:
        def someType = GraphQLScalarType.newScalar().name("SomeType").coercing(coercing).build()
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

    def "Same-name types are considered equal"() {
        given:
        def type1 = GraphQLScalarType.newScalar().name("SomeType").coercing(coercing).build()
        def type2 = GraphQLScalarType.newScalar().name("SomeType").coercing(coercing).build()

        expect:
        type1.equals(type2) == true
    }

    def "Differently named types are not considered equal"() {
        given:
        def type1 = GraphQLScalarType.newScalar().name("SomeType1").coercing(coercing).build()
        def type2 = GraphQLScalarType.newScalar().name("SomeType2").coercing(coercing).build()

        expect:
        type1.equals(type2) == false
    }

    def "Same-name types have equal hash codes"() {
        given:
        def type1 = GraphQLScalarType.newScalar().name("SomeType").coercing(coercing).build()
        def type2 = GraphQLScalarType.newScalar().name("SomeType").coercing(coercing).build()

        expect:
        type1.hashCode() == type2.hashCode();
    }

    def "Type is equal to itself"() {
        given:
        def someType = GraphQLScalarType.newScalar().name("SomeType").coercing(coercing).build()

        expect:
        someType.equals(someType) == true
    }
}
