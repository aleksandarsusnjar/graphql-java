package graphql.schema

import graphql.AssertException
import graphql.language.EnumValue
import graphql.language.StringValue
import spock.lang.Specification

import static graphql.schema.GraphQLEnumType.newEnum
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLInterfaceType.newInterface
import static graphql.schema.GraphQLInterfaceType.newInterface

class GraphQLEnumTypeTest extends Specification {

    GraphQLEnumType enumType

    def setup() {
        enumType = newEnum().name("TestEnum")
                .value("NAME", 42)
                .build()
    }

    def "parse throws exception for unknown value"() {
        when:
        enumType.parseValue("UNKNOWN")

        then:
        thrown(CoercingParseValueException)
    }


    def "parse value return value for the name"() {
        expect:
        enumType.parseValue("NAME") == 42
    }

    def "serialize returns name for value"() {
        expect:
        enumType.serialize(42) == "NAME"
    }

    def "serialize throws exception for unknown value"() {
        when:
        enumType.serialize(12)
        then:
        thrown(CoercingSerializeException)
    }


    def "parseLiteral return null for invalid input"() {
        when:
        enumType.parseLiteral(StringValue.newStringValue("foo").build())
        then:
        thrown(CoercingParseLiteralException)
    }

    def "parseLiteral return null for invalid enum name"() {
        when:
        enumType.parseLiteral(EnumValue.newEnumValue("NOT_NAME").build())
        then:
        thrown(CoercingParseLiteralException)
    }

    def "parseLiteral returns value for 'NAME'"() {
        expect:
        enumType.parseLiteral(EnumValue.newEnumValue("NAME").build()) == 42
    }


    def "null values are not allowed"() {
        when:
        newEnum().name("AnotherTestEnum")
                .value("NAME", null)
        then:
        thrown(AssertException)
    }


    def "duplicate value definition overwrites"() {
        when:
        def enumType = newEnum().name("AnotherTestEnum")
                .value("NAME", 42)
                .value("NAME", 43)
                .build()
        then:
        enumType.getValue("NAME").getValue() == 43
    }

    enum Episode {
        NEWHOPE, EMPIRE
    }

    def "serialize Java enum objects with String definition values"() {

        given:
        enumType = newEnum().name("Episode")
                .value("NEWHOPE", "NEWHOPE")
                .value("EMPIRE", "EMPIRE")
                .build()

        when:
        def serialized = enumType.serialize(Episode.EMPIRE)

        then:
        serialized == "EMPIRE"
    }

    def "serialize Java enum objects with Java enum definition values"() {

        given:
        enumType = newEnum().name("Episode")
                .value("NEWHOPE", Episode.NEWHOPE)
                .value("EMPIRE", Episode.EMPIRE)
                .build()

        when:
        def serialized = enumType.serialize(Episode.NEWHOPE)

        then:
        serialized == "NEWHOPE"
    }

    def "serialize String objects with Java enum definition values"() {

        given:
        enumType = newEnum().name("Episode")
                .value("NEWHOPE", Episode.NEWHOPE)
                .value("EMPIRE", Episode.EMPIRE)
                .build()

        String stringInput = Episode.NEWHOPE.toString()

        when:
        def serialized = enumType.serialize(stringInput)

        then:
        serialized == "NEWHOPE"
    }

    def "object can be transformed"() {
        given:
        def startEnum = newEnum().name("E1")
                .description("E1_description")
                .value("A")
                .value("B")
                .value("C")
                .value("D")
                .build()
        when:
        def transformedEnum = startEnum.transform({
            it
                    .name("E2")
                    .clearValues()
                    .value("X", 1)
                    .value("Y", 2)
                    .value("Z", 3)

        })

        then:
        startEnum.name == "E1"
        startEnum.description == "E1_description"
        startEnum.getValues().size() == 4
        startEnum.getValue("A").value == "A"
        startEnum.getValue("B").value == "B"
        startEnum.getValue("C").value == "C"
        startEnum.getValue("D").value == "D"

        transformedEnum.name == "E2"
        transformedEnum.description == "E1_description" // left alone
        transformedEnum.getValues().size() == 3
        transformedEnum.getValue("X").value == 1
        transformedEnum.getValue("Y").value == 2
        transformedEnum.getValue("Z").value == 3

    }

    def "Differently wrapped types are not considered equal"() {
        given:
        def someType = newEnum().name("SomeType").value("A").build()
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
        def someType1 = newEnum().name("SomeType").value("A").build()
        def someType2 = newEnum().name("SomeType").value("A").build()

        expect:
        someType1.equals(someType2) == true
    }

    def "Different-name types of same kind are not considered equal"() {
        given:
        def someType1 = newEnum().name("SomeType1").value("A").build()
        def someType2 = newEnum().name("SomeType2").value("A").build()

        expect:
        someType1.equals(someType2) == false
    }

    def "Same-name types have equal hash codes"() {
        given:
        def someType1 = newEnum().name("SomeType").value("A").build()
        def someType2 = newEnum().name("SomeType").value("A").build()

        expect:
        someType1.hashCode() == someType2.hashCode();
    }

    def "Type is equal to itself"() {
        given:
        def someType = newEnum().name("SomeType").value("A").build()

        expect:
        someType.equals(someType) == true
    }
}
