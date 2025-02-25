/*

   Copyright 2018-2021 Charles Korn.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

@file:OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)

package com.charleskorn.kaml

import ch.tutteli.atrium.api.fluent.en_GB.cause
import ch.tutteli.atrium.api.fluent.en_GB.message
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import com.charleskorn.kaml.testobjects.NestedObjects
import com.charleskorn.kaml.testobjects.PolymorphicWrapper
import com.charleskorn.kaml.testobjects.SealedWrapper
import com.charleskorn.kaml.testobjects.SimpleStructure
import com.charleskorn.kaml.testobjects.Team
import com.charleskorn.kaml.testobjects.TestEnum
import com.charleskorn.kaml.testobjects.TestSealedStructure
import com.charleskorn.kaml.testobjects.UnsealedClass
import com.charleskorn.kaml.testobjects.UnsealedString
import com.charleskorn.kaml.testobjects.UnwrappedInt
import com.charleskorn.kaml.testobjects.UnwrappedInterface
import com.charleskorn.kaml.testobjects.UnwrappedString
import com.charleskorn.kaml.testobjects.polymorphicModule
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.serializersModuleOf
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.jvm.JvmInline

object YamlReadingTest : Spek({
    describe("a YAML parser") {
        describe("parsing scalars") {
            context("given the input 'hello'") {
                val input = "hello"

                context("parsing that input as a string") {
                    val result = Yaml.default.decodeFromString(String.serializer(), input)

                    it("deserializes it to the expected string value") {
                        expect(result).toEqual("hello")
                    }
                }

                context("parsing that input as a nullable string") {
                    val result = Yaml.default.decodeFromString(String.serializer().nullable, input)

                    it("deserializes it to the expected string value") {
                        expect(result).toEqual("hello")
                    }
                }

                context("parsing that input with a serializer that uses YAML location information when throwing exceptions") {
                    it("throws an exception with the correct location information") {
                        expect({ Yaml.default.decodeFromString(LocationThrowingSerializer, input) }).toThrow<LocationInformationException> {
                            message { toEqual("Serializer called with location (1, 1) and path: <root>") }
                        }
                    }
                }

                context("parsing that input as a value type") {
                    val result = Yaml.default.decodeFromString(StringValue.serializer(), input)

                    it("deserializes it to the expected object") {
                        expect(result).toEqual(StringValue("hello"))
                    }
                }
            }

            context("given the input '123'") {
                val input = "123"

                context("parsing that input as an integer") {
                    val result = Yaml.default.decodeFromString(Int.serializer(), input)

                    it("deserializes it to the expected integer") {
                        expect(result).toEqual(123)
                    }
                }

                context("parsing that input as a long") {
                    val result = Yaml.default.decodeFromString(Long.serializer(), input)

                    it("deserializes it to the expected long") {
                        expect(result).toEqual(123)
                    }
                }

                context("parsing that input as a short") {
                    val result = Yaml.default.decodeFromString(Short.serializer(), input)

                    it("deserializes it to the expected short") {
                        expect(result).toEqual(123)
                    }
                }

                context("parsing that input as a byte") {
                    val result = Yaml.default.decodeFromString(Byte.serializer(), input)

                    it("deserializes it to the expected byte") {
                        expect(result).toEqual(123)
                    }
                }

                context("parsing that input as a double") {
                    val result = Yaml.default.decodeFromString(Double.serializer(), input)

                    it("deserializes it to the expected double") {
                        expect(result).toEqual(123.0)
                    }
                }

                context("parsing that input as a float") {
                    val result = Yaml.default.decodeFromString(Float.serializer(), input)

                    it("deserializes it to the expected float") {
                        expect(result).toEqual(123.0f)
                    }
                }

                context("parsing that input as a nullable integer") {
                    val result = Yaml.default.decodeFromString(Int.serializer().nullable, input)

                    it("deserializes it to the expected integer") {
                        expect(result).toEqual(123)
                    }
                }

                context("parsing that input as a nullable long") {
                    val result = Yaml.default.decodeFromString(Long.serializer().nullable, input)

                    it("deserializes it to the expected long") {
                        expect(result).toEqual(123)
                    }
                }

                context("parsing that input as a nullable short") {
                    val result = Yaml.default.decodeFromString(Short.serializer().nullable, input)

                    it("deserializes it to the expected short") {
                        expect(result).toEqual(123)
                    }
                }

                context("parsing that input as a nullable byte") {
                    val result = Yaml.default.decodeFromString(Byte.serializer().nullable, input)

                    it("deserializes it to the expected byte") {
                        expect(result).toEqual(123)
                    }
                }

                context("parsing that input as a nullable double") {
                    val result = Yaml.default.decodeFromString(Double.serializer().nullable, input)

                    it("deserializes it to the expected double") {
                        expect(result).toEqual(123.0)
                    }
                }

                context("parsing that input as a nullable float") {
                    val result = Yaml.default.decodeFromString(Float.serializer().nullable, input)

                    it("deserializes it to the expected float") {
                        expect(result).toEqual(123.0f)
                    }
                }
            }

            context("given the input 'true'") {
                val input = "true"

                context("parsing that input as a boolean") {
                    val result = Yaml.default.decodeFromString(Boolean.serializer(), input)

                    it("deserializes it to the expected boolean value") {
                        expect(result).toEqual(true)
                    }
                }

                context("parsing that input as a nullable boolean") {
                    val result = Yaml.default.decodeFromString(Boolean.serializer().nullable, input)

                    it("deserializes it to the expected boolean value") {
                        expect(result).toEqual(true)
                    }
                }
            }

            context("given the input 'c'") {
                val input = "c"

                context("parsing that input as a character") {
                    val result = Yaml.default.decodeFromString(Char.serializer(), input)

                    it("deserializes it to the expected character value") {
                        expect(result).toEqual('c')
                    }
                }

                context("parsing that input as a nullable character") {
                    val result = Yaml.default.decodeFromString(Char.serializer().nullable, input)

                    it("deserializes it to the expected character value") {
                        expect(result).toEqual('c')
                    }
                }
            }

            mapOf(
                "Value1" to TestEnum.Value1,
                "Value2" to TestEnum.Value2
            ).forEach { (input, expectedValue) ->
                context("given the input '$input'") {
                    context("parsing that input as an enumeration value") {
                        val result = Yaml.default.decodeFromString(TestEnum.serializer(), input)

                        it("deserializes it to the expected enumeration value") {
                            expect(result).toEqual(expectedValue)
                        }
                    }
                }
            }

            context("parsing an invalid enumeration value") {
                it("throws an appropriate exception") {
                    expect({ Yaml.default.decodeFromString(TestEnum.serializer(), "nonsense") }).toThrow<YamlScalarFormatException> {
                        message { toEqual("Value 'nonsense' is not a valid option, permitted choices are: Value1, Value2") }
                        line { toEqual(1) }
                        column { toEqual(1) }
                        path { toEqual(YamlPath.root) }
                    }
                }
            }
        }

        describe("parsing null values") {
            val input = "null"

            context("parsing a null value as a nullable string") {
                val result = Yaml.default.decodeFromString(String.serializer().nullable, input)

                it("returns a null value") {
                    expect(result).toEqual(null)
                }
            }

            context("parsing a null value as a non-nullable string") {
                it("throws an appropriate exception") {
                    expect({ Yaml.default.decodeFromString(String.serializer(), input) }).toThrow<UnexpectedNullValueException> {
                        message { toEqual("Unexpected null or empty value for non-null field.") }
                        line { toEqual(1) }
                        column { toEqual(1) }
                        path { toEqual(YamlPath.root) }
                    }
                }
            }

            context("parsing a null value as a nullable integer") {
                val result = Yaml.default.decodeFromString(Int.serializer().nullable, input)

                it("returns a null value") {
                    expect(result).toEqual(null)
                }
            }

            context("parsing a null value as a non-nullable integer") {
                it("throws an appropriate exception") {
                    expect({ Yaml.default.decodeFromString(Int.serializer(), input) }).toThrow<UnexpectedNullValueException> {
                        message { toEqual("Unexpected null or empty value for non-null field.") }
                        line { toEqual(1) }
                        column { toEqual(1) }
                        path { toEqual(YamlPath.root) }
                    }
                }
            }

            context("parsing a null value as a nullable long") {
                val result = Yaml.default.decodeFromString(Long.serializer().nullable, input)

                it("returns a null value") {
                    expect(result).toEqual(null)
                }
            }

            context("parsing a null value as a non-nullable long") {
                it("throws an appropriate exception") {
                    expect({ Yaml.default.decodeFromString(Long.serializer(), input) }).toThrow<UnexpectedNullValueException> {
                        message { toEqual("Unexpected null or empty value for non-null field.") }
                        line { toEqual(1) }
                        column { toEqual(1) }
                        path { toEqual(YamlPath.root) }
                    }
                }
            }

            context("parsing a null value as a nullable short") {
                val result = Yaml.default.decodeFromString(Short.serializer().nullable, input)

                it("returns a null value") {
                    expect(result).toEqual(null)
                }
            }

            context("parsing a null value as a non-nullable short") {
                it("throws an appropriate exception") {
                    expect({ Yaml.default.decodeFromString(Short.serializer(), input) }).toThrow<UnexpectedNullValueException> {
                        message { toEqual("Unexpected null or empty value for non-null field.") }
                        line { toEqual(1) }
                        column { toEqual(1) }
                        path { toEqual(YamlPath.root) }
                    }
                }
            }

            context("parsing a null value as a nullable byte") {
                val result = Yaml.default.decodeFromString(Byte.serializer().nullable, input)

                it("returns a null value") {
                    expect(result).toEqual(null)
                }
            }

            context("parsing a null value as a non-nullable byte") {
                it("throws an appropriate exception") {
                    expect({ Yaml.default.decodeFromString(Byte.serializer(), input) }).toThrow<UnexpectedNullValueException> {
                        message { toEqual("Unexpected null or empty value for non-null field.") }
                        line { toEqual(1) }
                        column { toEqual(1) }
                        path { toEqual(YamlPath.root) }
                    }
                }
            }

            context("parsing a null value as a nullable double") {
                val result = Yaml.default.decodeFromString(Double.serializer().nullable, input)

                it("returns a null value") {
                    expect(result).toEqual(null)
                }
            }

            context("parsing a null value as a non-nullable double") {
                it("throws an appropriate exception") {
                    expect({ Yaml.default.decodeFromString(Double.serializer(), input) }).toThrow<UnexpectedNullValueException> {
                        message { toEqual("Unexpected null or empty value for non-null field.") }
                        line { toEqual(1) }
                        column { toEqual(1) }
                        path { toEqual(YamlPath.root) }
                    }
                }
            }

            context("parsing a null value as a nullable float") {
                val result = Yaml.default.decodeFromString(Float.serializer().nullable, input)

                it("returns a null value") {
                    expect(result).toEqual(null)
                }
            }

            context("parsing a null value as a non-nullable float") {
                it("throws an appropriate exception") {
                    expect({ Yaml.default.decodeFromString(Float.serializer(), input) }).toThrow<UnexpectedNullValueException> {
                        message { toEqual("Unexpected null or empty value for non-null field.") }
                        line { toEqual(1) }
                        column { toEqual(1) }
                        path { toEqual(YamlPath.root) }
                    }
                }
            }

            context("parsing a null value as a nullable boolean") {
                val result = Yaml.default.decodeFromString(Boolean.serializer().nullable, input)

                it("returns a null value") {
                    expect(result).toEqual(null)
                }
            }

            context("parsing a null value as a non-nullable boolean") {
                it("throws an appropriate exception") {
                    expect({ Yaml.default.decodeFromString(Boolean.serializer(), input) }).toThrow<UnexpectedNullValueException> {
                        message { toEqual("Unexpected null or empty value for non-null field.") }
                        line { toEqual(1) }
                        column { toEqual(1) }
                        path { toEqual(YamlPath.root) }
                    }
                }
            }

            context("parsing a null value as a nullable character") {
                val result = Yaml.default.decodeFromString(Char.serializer().nullable, input)

                it("returns a null value") {
                    expect(result).toEqual(null)
                }
            }

            context("parsing a null value as a non-nullable character") {
                it("throws an appropriate exception") {
                    expect({ Yaml.default.decodeFromString(Char.serializer(), input) }).toThrow<UnexpectedNullValueException> {
                        message { toEqual("Unexpected null or empty value for non-null field.") }
                        line { toEqual(1) }
                        column { toEqual(1) }
                        path { toEqual(YamlPath.root) }
                    }
                }
            }

            context("parsing a null value as a nullable enum") {
                val result = Yaml.default.decodeFromString(TestEnum.serializer().nullable, input)

                it("returns a null value") {
                    expect(result).toEqual(null)
                }
            }

            context("parsing a null value as a non-nullable enum") {
                it("throws an appropriate exception") {
                    expect({ Yaml.default.decodeFromString(TestEnum.serializer(), input) }).toThrow<UnexpectedNullValueException> {
                        message { toEqual("Unexpected null or empty value for non-null field.") }
                        line { toEqual(1) }
                        column { toEqual(1) }
                        path { toEqual(YamlPath.root) }
                    }
                }
            }

            context("parsing a null value as a nullable list") {
                val result = Yaml.default.decodeFromString(ListSerializer(String.serializer()).nullable, input)

                it("returns a null value") {
                    expect(result).toEqual(null)
                }
            }

            context("parsing a null value as a non-nullable list") {
                it("throws an appropriate exception") {
                    expect({ Yaml.default.decodeFromString(ListSerializer(String.serializer()), input) }).toThrow<UnexpectedNullValueException> {
                        message { toEqual("Unexpected null or empty value for non-null field.") }
                        line { toEqual(1) }
                        column { toEqual(1) }
                        path { toEqual(YamlPath.root) }
                    }
                }
            }

            context("parsing a null value as a nullable object") {
                val result = Yaml.default.decodeFromString(ComplexStructure.serializer().nullable, input)

                it("returns a null value") {
                    expect(result).toEqual(null)
                }
            }

            context("parsing a null value as a non-nullable object") {
                it("throws an appropriate exception") {
                    expect({ Yaml.default.decodeFromString(ComplexStructure.serializer(), input) }).toThrow<UnexpectedNullValueException> {
                        message { toEqual("Unexpected null or empty value for non-null field.") }
                        line { toEqual(1) }
                        column { toEqual(1) }
                        path { toEqual(YamlPath.root) }
                    }
                }
            }

            context("parsing a null value with a serializer that uses YAML location information when throwing exceptions") {
                it("throws an exception with the correct location information") {
                    expect({ Yaml.default.decodeFromString(LocationThrowingSerializer, input) }).toThrow<LocationInformationException> {
                        message { toEqual("Serializer called with location (1, 1) and path: <root>") }
                    }
                }
            }
        }

        describe("parsing lists") {
            context("given a list of strings") {
                val input = """
                    - thing1
                    - thing2
                    - thing3
                """.trimIndent()

                context("parsing that input as a list") {
                    val result = Yaml.default.decodeFromString(ListSerializer(String.serializer()), input)

                    it("deserializes it to the expected value") {
                        expect(result).toEqual(listOf("thing1", "thing2", "thing3"))
                    }
                }

                context("parsing that input as a nullable list") {
                    val result = Yaml.default.decodeFromString(ListSerializer(String.serializer()).nullable, input)

                    it("deserializes it to the expected value") {
                        expect(result).toEqual(listOf("thing1", "thing2", "thing3"))
                    }
                }

                context("parsing that input with a serializer that uses YAML location information when throwing exceptions") {
                    it("throws an exception with the correct location information") {
                        expect({ Yaml.default.decodeFromString(ListSerializer(LocationThrowingSerializer), input) }).toThrow<LocationInformationException> {
                            message { toEqual("Serializer called with location (1, 3) and path: [0]") }
                        }
                    }
                }
            }

            context("given a list of numbers") {
                val input = """
                    - 123
                    - 45
                    - 6
                """.trimIndent()

                context("parsing that input as a list of integers") {
                    val result = Yaml.default.decodeFromString(ListSerializer(Int.serializer()), input)

                    it("deserializes it to the expected value") {
                        expect(result).toEqual(listOf(123, 45, 6))
                    }
                }

                context("parsing that input as a list of longs") {
                    val result = Yaml.default.decodeFromString(ListSerializer(Long.serializer()), input)

                    it("deserializes it to the expected value") {
                        expect(result).toEqual(listOf(123L, 45, 6))
                    }
                }

                context("parsing that input as a list of shorts") {
                    val result = Yaml.default.decodeFromString(ListSerializer(Short.serializer()), input)

                    it("deserializes it to the expected value") {
                        expect(result).toEqual(listOf(123.toShort(), 45, 6))
                    }
                }

                context("parsing that input as a list of bytes") {
                    val result = Yaml.default.decodeFromString(ListSerializer(Byte.serializer()), input)

                    it("deserializes it to the expected value") {
                        expect(result).toEqual(listOf(123.toByte(), 45, 6))
                    }
                }

                context("parsing that input as a list of doubles") {
                    val result = Yaml.default.decodeFromString(ListSerializer(Double.serializer()), input)

                    it("deserializes it to the expected value") {
                        expect(result).toEqual(listOf(123.0, 45.0, 6.0))
                    }
                }

                context("parsing that input as a list of floats") {
                    val result = Yaml.default.decodeFromString(ListSerializer(Float.serializer()), input)

                    it("deserializes it to the expected value") {
                        expect(result).toEqual(listOf(123.0f, 45.0f, 6.0f))
                    }
                }
            }

            context("given a list of booleans") {
                val input = """
                    - true
                    - false
                """.trimIndent()

                context("parsing that input as a list") {
                    val result = Yaml.default.decodeFromString(ListSerializer(Boolean.serializer()), input)

                    it("deserializes it to the expected value") {
                        expect(result).toEqual(listOf(true, false))
                    }
                }
            }

            context("given a list of enum values") {
                val input = """
                    - Value1
                    - Value2
                """.trimIndent()

                context("parsing that input as a list") {
                    val result = Yaml.default.decodeFromString(ListSerializer(TestEnum.serializer()), input)

                    it("deserializes it to the expected value") {
                        expect(result).toEqual(listOf(TestEnum.Value1, TestEnum.Value2))
                    }
                }
            }

            context("given a list of characters") {
                val input = """
                    - a
                    - b
                """.trimIndent()

                context("parsing that input as a list") {
                    val result = Yaml.default.decodeFromString(ListSerializer(Char.serializer()), input)

                    it("deserializes it to the expected value") {
                        expect(result).toEqual(listOf('a', 'b'))
                    }
                }
            }

            context("given a list of nullable strings") {
                val input = """
                    - thing1
                    - null
                """.trimIndent()

                context("parsing that input as a list") {
                    val result = Yaml.default.decodeFromString(ListSerializer(String.serializer().nullable), input)

                    it("deserializes it to the expected value") {
                        expect(result).toEqual(listOf("thing1", null))
                    }
                }
            }

            context("given a list of lists") {
                val input = """
                    - [thing1, thing2]
                    - [thing3]
                """.trimIndent()

                context("parsing that input as a list") {
                    val result = Yaml.default.decodeFromString(ListSerializer(ListSerializer(String.serializer())), input)

                    it("deserializes it to the expected value") {
                        expect(result).toEqual(
                            listOf(
                                listOf("thing1", "thing2"),
                                listOf("thing3")
                            )
                        )
                    }
                }
            }

            context("given a list of objects") {
                val input = """
                    - name: thing1
                    - name: thing2
                """.trimIndent()

                context("parsing that input as a list") {
                    val result = Yaml.default.decodeFromString(ListSerializer(SimpleStructure.serializer()), input)

                    it("deserializes it to the expected value") {
                        expect(result).toEqual(
                            listOf(
                                SimpleStructure("thing1"),
                                SimpleStructure("thing2")
                            )
                        )
                    }
                }
            }
        }

        describe("parsing objects") {
            context("given some input representing an object with an optional value specified") {
                val input = """
                    string: Alex
                    byte: 12
                    short: 1234
                    int: 123456
                    long: 1234567
                    float: 1.2
                    double: 2.4
                    enum: Value1
                    boolean: true
                    char: A
                    nullable: present
                """.trimIndent()

                context("parsing that input") {
                    val result = Yaml.default.decodeFromString(ComplexStructure.serializer(), input)

                    it("deserializes it to a Kotlin object") {
                        expect(result).toEqual(
                            ComplexStructure(
                                "Alex",
                                12,
                                1234,
                                123456,
                                1234567,
                                1.2f,
                                2.4,
                                TestEnum.Value1,
                                true,
                                'A',
                                "present"
                            )
                        )
                    }
                }
            }

            context("given some input representing an object with an optional value specified as null") {
                val input = """
                    string: Alex
                    byte: 12
                    short: 1234
                    int: 123456
                    long: 1234567
                    float: 1.2
                    double: 2.4
                    enum: Value1
                    boolean: true
                    char: A
                    nullable: null
                """.trimIndent()

                context("parsing that input") {
                    val result = Yaml.default.decodeFromString(ComplexStructure.serializer(), input)

                    it("deserializes it to a Kotlin object") {
                        expect(result).toEqual(
                            ComplexStructure(
                                "Alex",
                                12,
                                1234,
                                123456,
                                1234567,
                                1.2f,
                                2.4,
                                TestEnum.Value1,
                                true,
                                'A',
                                null
                            )
                        )
                    }
                }
            }

            context("given some input representing an object with an optional value not specified") {
                val input = """
                    string: Alex
                    byte: 12
                    short: 1234
                    int: 123456
                    long: 1234567
                    float: 1.2
                    double: 2.4
                    enum: Value1
                    boolean: true
                    char: A
                """.trimIndent()

                context("parsing that input") {
                    val result = Yaml.default.decodeFromString(ComplexStructure.serializer(), input)

                    it("deserializes it to a Kotlin object") {
                        expect(result).toEqual(
                            ComplexStructure(
                                "Alex",
                                12,
                                1234,
                                123456,
                                1234567,
                                1.2f,
                                2.4,
                                TestEnum.Value1,
                                true,
                                'A',
                                null
                            )
                        )
                    }
                }
            }

            context("given some input representing an object with an embedded list") {
                val input = """
                    members:
                        - Alex
                        - Jamie
                """.trimIndent()

                context("parsing that input") {
                    val result = Yaml.default.decodeFromString(Team.serializer(), input)

                    it("deserializes it to a Kotlin object") {
                        expect(result).toEqual(Team(listOf("Alex", "Jamie")))
                    }
                }
            }

            context("given some input representing an object with an embedded object") {
                val input = """
                    firstPerson:
                        name: Alex
                    secondPerson:
                        name: Jamie
                """.trimIndent()

                context("parsing that input") {
                    val result = Yaml.default.decodeFromString(NestedObjects.serializer(), input)

                    it("deserializes it to a Kotlin object") {
                        expect(result).toEqual(NestedObjects(SimpleStructure("Alex"), SimpleStructure("Jamie")))
                    }
                }
            }

            context("given some input representing an object where the keys are in a different order to the object definition") {
                val input = """
                    secondPerson:
                        name: Jamie
                    firstPerson:
                        name: Alex
                """.trimIndent()

                context("parsing that input") {
                    val result = Yaml.default.decodeFromString(NestedObjects.serializer(), input)

                    it("deserializes it to a Kotlin object") {
                        expect(result).toEqual(NestedObjects(SimpleStructure("Alex"), SimpleStructure("Jamie")))
                    }
                }
            }

            context("given some tagged input representing an arbitrary list") {
                val input = """
                    !!list
                        - 5
                        - 3
                """.trimIndent()

                context("parsing that input as list") {
                    val result = Yaml.default.decodeFromString(ListSerializer(Int.serializer()), input)
                    it("deserializes it to a list ignoring the tag") {
                        expect(result).toEqual(listOf(5, 3))
                    }
                }

                context("parsing that input with a serializer that uses YAML location information when throwing exceptions") {
                    it("throws an exception with the correct location information") {
                        expect({ Yaml.default.decodeFromString(LocationThrowingSerializer, input) }).toThrow<LocationInformationException> {
                            message { toEqual("Serializer called with location (1, 1) and path: <root>") }
                        }
                    }
                }
            }

            context("given some tagged input representing an arbitrary map") {
                val input = """
                    !!map
                    foo: bar
                """.trimIndent()

                context("parsing that input as map") {
                    val result = Yaml.default.decodeFromString(
                        MapSerializer(String.serializer(), String.serializer()),
                        input
                    )
                    it("deserializes it to a Map ignoring the tag") {
                        expect(result).toEqual(mapOf("foo" to "bar"))
                    }
                }

                context("parsing that input with a serializer that uses YAML location information when throwing exceptions") {
                    it("throws an exception with the correct location information") {
                        expect({ Yaml.default.decodeFromString(LocationThrowingMapSerializer, input) }).toThrow<LocationInformationException> {
                            message { toEqual("Serializer called with location (1, 1) and path: <root>") }
                        }
                    }
                }
            }

            context("given some input representing an object with a missing key") {
                val input = """
                    byte: 12
                    short: 1234
                    int: 123456
                    long: 1234567
                    float: 1.2
                    double: 2.4
                    enum: Value1
                    boolean: true
                    char: A
                """.trimIndent()

                context("parsing that input") {
                    it("throws an appropriate exception") {
                        expect({ Yaml.default.decodeFromString(ComplexStructure.serializer(), input) }).toThrow<MissingRequiredPropertyException> {
                            message { toEqual("Property 'string' is required but it is missing.") }
                            line { toEqual(1) }
                            column { toEqual(1) }
                            propertyName { toEqual("string") }
                            path { toEqual(YamlPath.root) }
                        }
                    }
                }
            }

            context("given some input representing an object with an unknown key") {
                val input = """
                    abc123: something
                """.trimIndent()

                context("parsing that input") {
                    it("throws an appropriate exception") {
                        expect({ Yaml.default.decodeFromString(ComplexStructure.serializer(), input) }).toThrow<UnknownPropertyException> {
                            message { toEqual("Unknown property 'abc123'. Known properties are: boolean, byte, char, double, enum, float, int, long, nullable, short, string") }
                            line { toEqual(1) }
                            column { toEqual(1) }
                            propertyName { toEqual("abc123") }
                            validPropertyNames { toEqual(setOf("boolean", "byte", "char", "double", "enum", "float", "int", "long", "nullable", "short", "string")) }
                            path { toEqual(YamlPath.root.withMapElementKey("abc123", Location(1, 1))) }
                        }
                    }
                }
            }

            context("given some input representing an object with an invalid value for a field") {
                mapOf(
                    "byte" to "Value 'xxx' is not a valid byte value.",
                    "short" to "Value 'xxx' is not a valid short value.",
                    "int" to "Value 'xxx' is not a valid integer value.",
                    "long" to "Value 'xxx' is not a valid long value.",
                    "float" to "Value 'xxx' is not a valid floating point value.",
                    "double" to "Value 'xxx' is not a valid floating point value.",
                    "enum" to "Value 'xxx' is not a valid option, permitted choices are: Value1, Value2",
                    "boolean" to "Value 'xxx' is not a valid boolean, permitted choices are: true or false",
                    "char" to "Value 'xxx' is not a valid character value."
                ).forEach { (fieldName, errorMessage) ->
                    context("given the invalid field represents a $fieldName") {
                        val input = "$fieldName: xxx"

                        context("parsing that input") {
                            it("throws an appropriate exception") {
                                expect({ Yaml.default.decodeFromString(ComplexStructure.serializer(), input) }).toThrow<InvalidPropertyValueException> {
                                    message { toEqual("Value for '$fieldName' is invalid: $errorMessage") }
                                    line { toEqual(1) }
                                    column { toEqual(fieldName.length + 3) }
                                    propertyName { toEqual(fieldName) }
                                    reason { toEqual(errorMessage) }
                                    path { toEqual(YamlPath.root.withMapElementKey(fieldName, Location(1, 1)).withMapElementValue(Location(1, fieldName.length + 3))) }
                                }
                            }
                        }
                    }
                }
            }

            context("given some input representing an object with a null value for a non-nullable scalar field") {
                val input = "name: null"

                context("parsing that input") {
                    it("throws an appropriate exception") {
                        expect({ Yaml.default.decodeFromString(SimpleStructure.serializer(), input) }).toThrow<InvalidPropertyValueException> {
                            message { toEqual("Value for 'name' is invalid: Unexpected null or empty value for non-null field.") }
                            line { toEqual(1) }
                            column { toEqual(7) }
                            propertyName { toEqual("name") }
                            reason { toEqual("Unexpected null or empty value for non-null field.") }
                            path { toEqual(YamlPath.root.withMapElementKey("name", Location(1, 1)).withMapElementValue(Location(1, 7))) }
                        }
                    }
                }
            }

            context("given some input representing an object with a null value for a non-nullable nested object field") {
                val input = "firstPerson: null"

                context("parsing that input") {
                    it("throws an appropriate exception") {
                        expect({ Yaml.default.decodeFromString(NestedObjects.serializer(), input) }).toThrow<InvalidPropertyValueException> {
                            message { toEqual("Value for 'firstPerson' is invalid: Unexpected null or empty value for non-null field.") }
                            line { toEqual(1) }
                            column { toEqual(14) }
                            propertyName { toEqual("firstPerson") }
                            reason { toEqual("Unexpected null or empty value for non-null field.") }
                            path { toEqual(YamlPath.root.withMapElementKey("firstPerson", Location(1, 1)).withMapElementValue(Location(1, 14))) }
                        }
                    }
                }
            }

            context("given some input representing an object with a null value for a nullable nested object field") {

                val input = "firstPerson: null"

                context("parsing that input") {
                    val result = Yaml.default.decodeFromString(NullableNestedObject.serializer(), input)

                    it("deserializes it to a Kotlin object") {
                        expect(result).toEqual(NullableNestedObject(null))
                    }
                }
            }

            context("given some input representing an object with a null value for a non-nullable nested list field") {
                val input = "members: null"

                context("parsing that input") {
                    it("throws an appropriate exception") {
                        expect({ Yaml.default.decodeFromString(Team.serializer(), input) }).toThrow<InvalidPropertyValueException> {
                            message { toEqual("Value for 'members' is invalid: Unexpected null or empty value for non-null field.") }
                            line { toEqual(1) }
                            column { toEqual(10) }
                            propertyName { toEqual("members") }
                            reason { toEqual("Unexpected null or empty value for non-null field.") }
                            path { toEqual(YamlPath.root.withMapElementKey("members", Location(1, 1)).withMapElementValue(Location(1, 10))) }
                        }
                    }
                }
            }

            context("given some input representing an object with a null value for a nullable nested list field") {
                val input = "members: null"

                context("parsing that input") {
                    val result = Yaml.default.decodeFromString(NullableNestedList.serializer(), input)

                    it("deserializes it to a Kotlin object") {
                        expect(result).toEqual(NullableNestedList(null))
                    }
                }
            }

            context("given some input representing an object with a custom serializer for one of its values") {
                val input = "value: something"

                context("parsing that input with a serializer that uses YAML location information when throwing exceptions") {
                    it("throws an exception with the correct location information") {
                        expect({ Yaml.default.decodeFromString(StructureWithLocationThrowingSerializer.serializer(), input) }).toThrow<LocationInformationException> {
                            message { toEqual("Serializer called with location (1, 8) and path: value") }
                        }
                    }
                }
            }

            context("given some input representing a generic map") {
                val input = """
                    SOME_ENV_VAR: somevalue
                    SOME_OTHER_ENV_VAR: someothervalue
                """.trimIndent()

                context("parsing that input") {
                    val result = Yaml.default.decodeFromString(MapSerializer(String.serializer(), String.serializer()), input)

                    it("deserializes it to a Kotlin map") {
                        expect(result).toEqual(
                            mapOf(
                                "SOME_ENV_VAR" to "somevalue",
                                "SOME_OTHER_ENV_VAR" to "someothervalue"
                            )
                        )
                    }
                }

                context("parsing that input with a serializer for the key that uses YAML location information when throwing exceptions") {
                    it("throws an exception with the correct location information") {
                        expect({ Yaml.default.decodeFromString(MapSerializer(LocationThrowingSerializer, String.serializer()), input) }).toThrow<LocationInformationException> {
                            message { toEqual("Serializer called with location (1, 1) and path: SOME_ENV_VAR") }
                        }
                    }
                }

                context("parsing that input with a serializer for the value that uses YAML location information when throwing exceptions") {
                    it("throws an exception with the correct location information") {
                        expect({ Yaml.default.decodeFromString(MapSerializer(String.serializer(), LocationThrowingSerializer), input) }).toThrow<LocationInformationException> {
                            message { toEqual("Serializer called with location (1, 15) and path: SOME_ENV_VAR") }
                        }
                    }
                }
            }

            context("given some input with some extensions") {
                val input = """
                    .some-extension: &name Jamie

                    name: *name
                """.trimIndent()

                context("parsing that input") {
                    val configuration = YamlConfiguration(extensionDefinitionPrefix = ".")
                    val yaml = Yaml(configuration = configuration)
                    val result = yaml.decodeFromString(SimpleStructure.serializer(), input)

                    it("deserializes it to a Kotlin object, replacing the reference to the extension with the extension") {
                        expect(result).toEqual(SimpleStructure("Jamie"))
                    }
                }
            }

            context("given some input with an additional unknown field") {
                val input = """
                    name: Blah Blahson
                    extra-field: Hello
                """.trimIndent()

                context("given strict mode is enabled") {
                    val configuration = YamlConfiguration(strictMode = true)
                    val yaml = Yaml(configuration = configuration)

                    context("parsing that input") {
                        it("throws an appropriate exception") {
                            expect({ yaml.decodeFromString(SimpleStructure.serializer(), input) }).toThrow<UnknownPropertyException> {
                                message { toEqual("Unknown property 'extra-field'. Known properties are: name") }
                                line { toEqual(2) }
                                column { toEqual(1) }
                                path { toEqual(YamlPath.root.withMapElementKey("extra-field", Location(2, 1))) }
                            }
                        }
                    }
                }

                context("given strict mode is disabled") {
                    val configuration = YamlConfiguration(strictMode = false)
                    val yaml = Yaml(configuration = configuration)

                    context("parsing that input") {
                        it("ignores the extra field and returns a deserialised object") {
                            expect(yaml.decodeFromString(SimpleStructure.serializer(), input)).toEqual(SimpleStructure("Blah Blahson"))
                        }
                    }
                }
            }

            context("given a nullable object") {
                val input = """
                    host: "db.test.com"
                """.trimIndent()

                val result = Yaml.default.decodeFromString(Database.serializer().nullable, input)

                it("deserializes it to the expected object") {
                    expect(result).toEqual(Database("db.test.com"))
                }
            }
        }

        describe("parsing polymorphic values") {
            describe("given tags are used to store the type information") {
                val polymorphicYaml = Yaml(serializersModule = polymorphicModule, configuration = YamlConfiguration(polymorphismStyle = PolymorphismStyle.Tag))

                context("given some input where the value should be a sealed class") {
                    val input = """
                        !<sealedString>
                        value: "asdfg"
                    """.trimIndent()

                    context("parsing that input") {
                        val result = polymorphicYaml.decodeFromString(TestSealedStructure.serializer(), input)

                        it("deserializes it to a Kotlin object") {
                            expect(result).toEqual(TestSealedStructure.SimpleSealedString("asdfg"))
                        }
                    }

                    context("parsing that input as map") {
                        val result = polymorphicYaml.decodeFromString(MapSerializer(String.serializer(), String.serializer()), input)

                        it("deserializes it to a map ignoring the tag") {
                            expect(result).toEqual(mapOf("value" to "asdfg"))
                        }
                    }
                }

                // See https://github.com/charleskorn/kaml/issues/179.
                context("given some input where a tag is provided but no value is provided") {
                    val input = """
                        !<sealedString>
                    """.trimIndent()

                    context("parsing that input") {
                        it("throws an appropriate exception") {
                            expect({ polymorphicYaml.decodeFromString(TestSealedStructure.serializer(), input) }).toThrow<MissingRequiredPropertyException> {
                                message { toEqual("Property 'value' is required but it is missing.") }
                                line { toEqual(1) }
                                column { toEqual(1) }
                                propertyName { toEqual("value") }
                                path { toEqual(YamlPath.root) }
                            }
                        }
                    }
                }

                context("given some input where the value is a literal") {
                    val input = """
                        !<simpleString> "asdfg"
                    """.trimIndent()

                    context("parsing that input") {
                        val result = polymorphicYaml.decodeFromString(PolymorphicSerializer(UnwrappedInterface::class), input)

                        it("deserializes it to a Kotlin object") {
                            expect(result).toEqual(UnwrappedString("asdfg"))
                        }
                    }

                    context("parsing that input as a string") {
                        val result = polymorphicYaml.decodeFromString(String.serializer(), input)

                        it("deserializes it to a string ignoring the tag") {
                            expect(result).toEqual("asdfg")
                        }
                    }
                }

                context("given some input where the value should be an unsealed class") {
                    val input = """
                        !<unsealedString>
                        value: "asdfg"
                    """.trimIndent()

                    context("parsing that input") {
                        val result = polymorphicYaml.decodeFromString(PolymorphicSerializer(UnsealedClass::class), input)

                        it("deserializes it to a Kotlin object") {
                            expect(result).toEqual(UnsealedString("asdfg"))
                        }
                    }

                    context("parsing that input as map") {
                        val result = polymorphicYaml.decodeFromString(MapSerializer(String.serializer(), String.serializer()), input)

                        it("deserializes it to a map ignoring the tag") {
                            expect(result).toEqual(mapOf("value" to "asdfg"))
                        }
                    }
                }

                context("given some input for an object where the property value should be a sealed class") {
                    val input = """
                        element: !<sealedString>
                            value: "asdfg"
                    """.trimIndent()

                    context("parsing that input") {
                        val result = polymorphicYaml.decodeFromString(SealedWrapper.serializer(), input)

                        it("deserializes it to a Kotlin object") {
                            expect(result).toEqual(SealedWrapper(TestSealedStructure.SimpleSealedString("asdfg")))
                        }
                    }

                    context("parsing that input as map") {
                        val result = polymorphicYaml.decodeFromString(MapSerializer(String.serializer(), MapSerializer(String.serializer(), String.serializer())), input)

                        it("deserializes it to a map ignoring the tag") {
                            expect(result).toEqual(mapOf("element" to mapOf("value" to "asdfg")))
                        }
                    }
                }

                context("given some input for an object where the property value is a literal") {
                    val input = """
                        test: !<simpleInt> 42
                    """.trimIndent()

                    context("parsing that input") {
                        val result = polymorphicYaml.decodeFromString(PolymorphicWrapper.serializer(), input)

                        it("deserializes it to a Kotlin object") {
                            expect(result).toEqual(PolymorphicWrapper(UnwrappedInt(42)))
                        }
                    }
                }

                context("given some tagged input representing a list of polymorphic objects") {
                    val input = """
                        - !<sealedString>
                          value: null
                        - !<sealedInt>
                          value: -987
                        - !<sealedInt>
                          value: 654
                        - !<sealedString>
                          value: "tests"
                    """.trimIndent()

                    context("parsing that input") {
                        val result = polymorphicYaml.decodeFromString(ListSerializer(TestSealedStructure.serializer()), input)

                        it("deserializes it to a Kotlin object") {
                            expect(result).toEqual(
                                listOf(
                                    TestSealedStructure.SimpleSealedString(null),
                                    TestSealedStructure.SimpleSealedInt(-987),
                                    TestSealedStructure.SimpleSealedInt(654),
                                    TestSealedStructure.SimpleSealedString("tests")
                                )
                            )
                        }
                    }
                }

                context("given some untagged input for a polymorphic class") {
                    val input = """
                        element:
                            value: "asdfg"
                    """.trimIndent()

                    context("parsing that input") {
                        it("throws an exception with the correct location information") {
                            expect({ polymorphicYaml.decodeFromString(SealedWrapper.serializer(), input) }).toThrow<InvalidPropertyValueException> {
                                message { toEqual("Value for 'element' is invalid: Value is missing a type tag (eg. !<type>)") }
                                line { toEqual(2) }
                                column { toEqual(5) }
                                cause<MissingTypeTagException>()
                                path { toEqual(YamlPath.root.withMapElementKey("element", Location(1, 1)).withMapElementValue(Location(2, 5))) }
                            }
                        }
                    }
                }

                context("given some untagged input for a polymorphic value") {
                    val input = """
                        test: "asdfg"
                    """.trimIndent()

                    context("parsing that input") {
                        it("throws an exception with the correct location information") {
                            expect({ polymorphicYaml.decodeFromString(PolymorphicWrapper.serializer(), input) }).toThrow<InvalidPropertyValueException> {
                                message { toEqual("Value for 'test' is invalid: Value is missing a type tag (eg. !<type>)") }
                                line { toEqual(1) }
                                column { toEqual(7) }
                                cause<MissingTypeTagException>()
                                path { toEqual(YamlPath.root.withMapElementKey("test", Location(1, 1)).withMapElementValue(Location(1, 7))) }
                            }
                        }
                    }
                }

                context("given some untagged null input for a polymorphic value") {
                    val input = """
                        test: null
                    """.trimIndent()

                    context("parsing that input") {
                        it("throws an exception with the correct location information") {
                            expect({ polymorphicYaml.decodeFromString(PolymorphicWrapper.serializer(), input) }).toThrow<InvalidPropertyValueException> {
                                message { toEqual("Value for 'test' is invalid: Value is missing a type tag (eg. !<type>)") }
                                line { toEqual(1) }
                                column { toEqual(7) }
                                cause<MissingTypeTagException>()
                                path { toEqual(YamlPath.root.withMapElementKey("test", Location(1, 1)).withMapElementValue(Location(1, 7))) }
                            }
                        }
                    }
                }

                context("given a polymorphic value for a property from an unsealed type with an unknown type tag") {
                    val input = """
                        !<someOtherType> 42
                    """.trimIndent()

                    context("parsing that input") {
                        it("throws an exception with the correct location information") {
                            expect({ polymorphicYaml.decodeFromString(PolymorphicSerializer(UnsealedClass::class), input) }).toThrow<UnknownPolymorphicTypeException> {
                                message { toEqual("Unknown type 'someOtherType'. Known types are: unsealedBoolean, unsealedString") }
                                line { toEqual(1) }
                                column { toEqual(1) }
                                typeName { toEqual("someOtherType") }
                                validTypeNames { toEqual(setOf("unsealedBoolean", "unsealedString")) }
                                path { toEqual(YamlPath.root) }
                            }
                        }
                    }
                }

                context("given a polymorphic value for a property from a sealed type with an unknown type tag") {
                    val input = """
                        !<someOtherType> 42
                    """.trimIndent()

                    context("parsing that input") {
                        it("throws an exception with the correct location information") {
                            expect({ polymorphicYaml.decodeFromString(TestSealedStructure.serializer(), input) }).toThrow<UnknownPolymorphicTypeException> {
                                message { toEqual("Unknown type 'someOtherType'. Known types are: sealedInt, sealedString") }
                                line { toEqual(1) }
                                column { toEqual(1) }
                                typeName { toEqual("someOtherType") }
                                validTypeNames { toEqual(setOf("sealedInt", "sealedString")) }
                                path { toEqual(YamlPath.root) }
                            }
                        }
                    }
                }

                context("given a polymorphic value from a literal with an unknown type tag") {
                    val input = """
                        !<someOtherType> 42
                    """.trimIndent()

                    context("parsing that input") {
                        it("throws an exception with the correct location information") {
                            expect({ polymorphicYaml.decodeFromString(TestSealedStructure.serializer(), input) }).toThrow<UnknownPolymorphicTypeException> {
                                message { toEqual("Unknown type 'someOtherType'. Known types are: sealedInt, sealedString") }
                                line { toEqual(1) }
                                column { toEqual(1) }
                                typeName { toEqual("someOtherType") }
                                validTypeNames { toEqual(setOf("sealedInt", "sealedString")) }
                                path { toEqual(YamlPath.root) }
                            }
                        }
                    }
                }
            }

            describe("given a property is used to store the type information") {
                val polymorphicYaml = Yaml(serializersModule = polymorphicModule, configuration = YamlConfiguration(polymorphismStyle = PolymorphismStyle.Property))

                context("given some input where the value should be a sealed class") {
                    val input = """
                        type: sealedString
                        value: "asdfg"
                    """.trimIndent()

                    context("parsing that input") {
                        val result = polymorphicYaml.decodeFromString(TestSealedStructure.serializer(), input)

                        it("deserializes it to a Kotlin object") {
                            expect(result).toEqual(TestSealedStructure.SimpleSealedString("asdfg"))
                        }
                    }

                    context("parsing that input as map") {
                        val result = polymorphicYaml.decodeFromString(MapSerializer(String.serializer(), String.serializer()), input)

                        it("deserializes it to a map including the type") {
                            expect(result).toEqual(mapOf("type" to "sealedString", "value" to "asdfg"))
                        }
                    }
                }

                context("given some input where the value should be an unsealed class") {
                    val input = """
                        type: unsealedString
                        value: "asdfg"
                    """.trimIndent()

                    context("parsing that input") {
                        val result = polymorphicYaml.decodeFromString(PolymorphicSerializer(UnsealedClass::class), input)

                        it("deserializes it to a Kotlin object") {
                            expect(result).toEqual(UnsealedString("asdfg"))
                        }
                    }

                    context("parsing that input as map") {
                        val result = polymorphicYaml.decodeFromString(MapSerializer(String.serializer(), String.serializer()), input)

                        it("deserializes it to a map ignoring the tag") {
                            expect(result).toEqual(mapOf("type" to "unsealedString", "value" to "asdfg"))
                        }
                    }
                }

                context("given some input for an object where the property value should be a sealed class") {
                    val input = """
                        element:
                            type: sealedString
                            value: "asdfg"
                    """.trimIndent()

                    context("parsing that input") {
                        val result = polymorphicYaml.decodeFromString(SealedWrapper.serializer(), input)

                        it("deserializes it to a Kotlin object") {
                            expect(result).toEqual(SealedWrapper(TestSealedStructure.SimpleSealedString("asdfg")))
                        }
                    }

                    context("parsing that input as map") {
                        val result = polymorphicYaml.decodeFromString(MapSerializer(String.serializer(), MapSerializer(String.serializer(), String.serializer())), input)

                        it("deserializes it to a map ignoring the tag") {
                            expect(result).toEqual(mapOf("element" to mapOf("type" to "sealedString", "value" to "asdfg")))
                        }
                    }
                }

                context("given some input missing a type property") {
                    val input = """
                        value: "asdfg"
                    """.trimIndent()

                    context("parsing that input") {
                        it("throws an exception with the correct location information") {
                            expect({ polymorphicYaml.decodeFromString(TestSealedStructure.serializer(), input) }).toThrow<MissingRequiredPropertyException> {
                                message { toEqual("Property 'type' is required but it is missing.") }
                                line { toEqual(1) }
                                column { toEqual(1) }
                                propertyName { toEqual("type") }
                                path { toEqual(YamlPath.root) }
                            }
                        }
                    }
                }

                mapOf(
                    "a list" to "[]",
                    "a map" to "{}",
                    "a null value" to "null",
                    "a tagged value" to "!<tag> sealedString"
                ).forEach { description, value ->
                    context("given some input with a type property that is $description") {
                        val input = """
                            type: $value
                            value: "asdfg"
                        """.trimIndent()

                        context("parsing that input") {
                            it("throws an exception with the correct location information") {
                                expect({ polymorphicYaml.decodeFromString(TestSealedStructure.serializer(), input) }).toThrow<InvalidPropertyValueException> {
                                    message { toEqual("Value for 'type' is invalid: expected a string, but got $description") }
                                    line { toEqual(1) }
                                    column { toEqual(7) }
                                    propertyName { toEqual("type") }
                                    reason { toEqual("expected a string, but got $description") }
                                    path { toEqual(YamlPath.root.withMapElementKey("type", Location(1, 1)).withMapElementValue(Location(1, 7))) }
                                }
                            }
                        }
                    }
                }

                context("given some tagged input representing a list of polymorphic objects") {
                    val input = """
                        - type: sealedString
                          value: null
                        - type: sealedInt
                          value: -987
                        - type: sealedInt
                          value: 654
                        - type: sealedString
                          value: "tests"
                    """.trimIndent()

                    context("parsing that input") {
                        val result = polymorphicYaml.decodeFromString(ListSerializer(TestSealedStructure.serializer()), input)

                        it("deserializes it to a Kotlin object") {
                            expect(result).toEqual(
                                listOf(
                                    TestSealedStructure.SimpleSealedString(null),
                                    TestSealedStructure.SimpleSealedInt(-987),
                                    TestSealedStructure.SimpleSealedInt(654),
                                    TestSealedStructure.SimpleSealedString("tests")
                                )
                            )
                        }
                    }
                }

                context("given a polymorphic value for a property from an unsealed type with an unknown type tag") {
                    val input = """
                        type: someOtherType
                        value: 123
                    """.trimIndent()

                    context("parsing that input") {
                        it("throws an exception with the correct location information") {
                            expect({ polymorphicYaml.decodeFromString(PolymorphicSerializer(UnsealedClass::class), input) }).toThrow<UnknownPolymorphicTypeException> {
                                message { toEqual("Unknown type 'someOtherType'. Known types are: unsealedBoolean, unsealedString") }
                                line { toEqual(1) }
                                column { toEqual(7) }
                                typeName { toEqual("someOtherType") }
                                validTypeNames { toEqual(setOf("unsealedBoolean", "unsealedString")) }
                                path { toEqual(YamlPath.root.withMapElementKey("type", Location(1, 1)).withMapElementValue(Location(1, 7))) }
                            }
                        }
                    }
                }

                context("given a polymorphic value for a property from a sealed type with an unknown type tag") {
                    val input = """
                        type: someOtherType
                        value: 123
                    """.trimIndent()

                    context("parsing that input") {
                        it("throws an exception with the correct location information") {
                            expect({ polymorphicYaml.decodeFromString(TestSealedStructure.serializer(), input) }).toThrow<UnknownPolymorphicTypeException> {
                                message { toEqual("Unknown type 'someOtherType'. Known types are: sealedInt, sealedString") }
                                line { toEqual(1) }
                                column { toEqual(7) }
                                typeName { toEqual("someOtherType") }
                                validTypeNames { toEqual(setOf("sealedInt", "sealedString")) }
                                path { toEqual(YamlPath.root.withMapElementKey("type", Location(1, 1)).withMapElementValue(Location(1, 7))) }
                            }
                        }
                    }
                }

                context("given some input with a tag and a type property") {
                    val input = """
                        !<sealedInt>
                        type: sealedString
                        value: "asdfg"
                    """.trimIndent()

                    context("parsing that input") {
                        val result = polymorphicYaml.decodeFromString(TestSealedStructure.serializer(), input)

                        it("uses the type from the property and ignores the tag") {
                            expect(result).toEqual(TestSealedStructure.SimpleSealedString("asdfg"))
                        }
                    }
                }
            }

            describe("given a custom property name is used to store the type information") {
                val polymorphicYaml = Yaml(serializersModule = polymorphicModule, configuration = YamlConfiguration(polymorphismStyle = PolymorphismStyle.Property, polymorphismPropertyName = "kind"))

                context("given some input where the value should be a sealed class") {
                    val input = """
                        kind: sealedString
                        value: "asdfg"
                    """.trimIndent()

                    context("parsing that input") {
                        val result = polymorphicYaml.decodeFromString(TestSealedStructure.serializer(), input)

                        it("deserializes it to a Kotlin object") {
                            expect(result).toEqual(TestSealedStructure.SimpleSealedString("asdfg"))
                        }
                    }

                    context("parsing that input as map") {
                        val result = polymorphicYaml.decodeFromString(MapSerializer(String.serializer(), String.serializer()), input)

                        it("deserializes it to a map including the type") {
                            expect(result).toEqual(mapOf("kind" to "sealedString", "value" to "asdfg"))
                        }
                    }
                }

                context("given some input where the value should be an unsealed class") {
                    val input = """
                        kind: unsealedString
                        value: "asdfg"
                    """.trimIndent()

                    context("parsing that input") {
                        val result = polymorphicYaml.decodeFromString(PolymorphicSerializer(UnsealedClass::class), input)

                        it("deserializes it to a Kotlin object") {
                            expect(result).toEqual(UnsealedString("asdfg"))
                        }
                    }

                    context("parsing that input as map") {
                        val result = polymorphicYaml.decodeFromString(MapSerializer(String.serializer(), String.serializer()), input)

                        it("deserializes it to a map ignoring the tag") {
                            expect(result).toEqual(mapOf("kind" to "unsealedString", "value" to "asdfg"))
                        }
                    }
                }

                context("given some input for an object where the property value should be a sealed class") {
                    val input = """
                        element:
                            kind: sealedString
                            value: "asdfg"
                    """.trimIndent()

                    context("parsing that input") {
                        val result = polymorphicYaml.decodeFromString(SealedWrapper.serializer(), input)

                        it("deserializes it to a Kotlin object") {
                            expect(result).toEqual(SealedWrapper(TestSealedStructure.SimpleSealedString("asdfg")))
                        }
                    }

                    context("parsing that input as map") {
                        val result = polymorphicYaml.decodeFromString(MapSerializer(String.serializer(), MapSerializer(String.serializer(), String.serializer())), input)

                        it("deserializes it to a map ignoring the tag") {
                            expect(result).toEqual(mapOf("element" to mapOf("kind" to "sealedString", "value" to "asdfg")))
                        }
                    }
                }

                context("given some input missing a type property") {
                    val input = """
                        value: "asdfg"
                    """.trimIndent()

                    context("parsing that input") {
                        it("throws an exception with the correct location information") {
                            expect({ polymorphicYaml.decodeFromString(TestSealedStructure.serializer(), input) }).toThrow<MissingRequiredPropertyException> {
                                message { toEqual("Property 'kind' is required but it is missing.") }
                                line { toEqual(1) }
                                column { toEqual(1) }
                                propertyName { toEqual("kind") }
                                path { toEqual(YamlPath.root) }
                            }
                        }
                    }
                }

                mapOf(
                    "a list" to "[]",
                    "a map" to "{}",
                    "a null value" to "null",
                    "a tagged value" to "!<tag> sealedString"
                ).forEach { description, value ->
                    context("given some input with a type property that is $description") {
                        val input = """
                            kind: $value
                            value: "asdfg"
                        """.trimIndent()

                        context("parsing that input") {
                            it("throws an exception with the correct location information") {
                                expect({ polymorphicYaml.decodeFromString(TestSealedStructure.serializer(), input) }).toThrow<InvalidPropertyValueException> {
                                    message { toEqual("Value for 'kind' is invalid: expected a string, but got $description") }
                                    line { toEqual(1) }
                                    column { toEqual(7) }
                                    propertyName { toEqual("kind") }
                                    reason { toEqual("expected a string, but got $description") }
                                    path { toEqual(YamlPath.root.withMapElementKey("kind", Location(1, 1)).withMapElementValue(Location(1, 7))) }
                                }
                            }
                        }
                    }
                }

                context("given some tagged input representing a list of polymorphic objects") {
                    val input = """
                        - kind: sealedString
                          value: null
                        - kind: sealedInt
                          value: -987
                        - kind: sealedInt
                          value: 654
                        - kind: sealedString
                          value: "tests"
                    """.trimIndent()

                    context("parsing that input") {
                        val result = polymorphicYaml.decodeFromString(ListSerializer(TestSealedStructure.serializer()), input)

                        it("deserializes it to a Kotlin object") {
                            expect(result).toEqual(
                                listOf(
                                    TestSealedStructure.SimpleSealedString(null),
                                    TestSealedStructure.SimpleSealedInt(-987),
                                    TestSealedStructure.SimpleSealedInt(654),
                                    TestSealedStructure.SimpleSealedString("tests")
                                )
                            )
                        }
                    }
                }

                context("given a polymorphic value for a property from an unsealed type with an unknown type tag") {
                    val input = """
                        kind: someOtherType
                        value: 123
                    """.trimIndent()

                    context("parsing that input") {
                        it("throws an exception with the correct location information") {
                            expect({ polymorphicYaml.decodeFromString(PolymorphicSerializer(UnsealedClass::class), input) }).toThrow<UnknownPolymorphicTypeException> {
                                message { toEqual("Unknown type 'someOtherType'. Known types are: unsealedBoolean, unsealedString") }
                                line { toEqual(1) }
                                column { toEqual(7) }
                                typeName { toEqual("someOtherType") }
                                validTypeNames { toEqual(setOf("unsealedBoolean", "unsealedString")) }
                                path { toEqual(YamlPath.root.withMapElementKey("kind", Location(1, 1)).withMapElementValue(Location(1, 7))) }
                            }
                        }
                    }
                }

                context("given a polymorphic value for a property from a sealed type with an unknown type tag") {
                    val input = """
                        kind: someOtherType
                        value: 123
                    """.trimIndent()

                    context("parsing that input") {
                        it("throws an exception with the correct location information") {
                            expect({ polymorphicYaml.decodeFromString(TestSealedStructure.serializer(), input) }).toThrow<UnknownPolymorphicTypeException> {
                                message { toEqual("Unknown type 'someOtherType'. Known types are: sealedInt, sealedString") }
                                line { toEqual(1) }
                                column { toEqual(7) }
                                typeName { toEqual("someOtherType") }
                                validTypeNames { toEqual(setOf("sealedInt", "sealedString")) }
                                path { toEqual(YamlPath.root.withMapElementKey("kind", Location(1, 1)).withMapElementValue(Location(1, 7))) }
                            }
                        }
                    }
                }

                context("given some input with a tag and a type property") {
                    val input = """
                        !<sealedInt>
                        kind: sealedString
                        value: "asdfg"
                    """.trimIndent()

                    context("parsing that input") {
                        val result = polymorphicYaml.decodeFromString(TestSealedStructure.serializer(), input)

                        it("uses the type from the property and ignores the tag") {
                            expect(result).toEqual(TestSealedStructure.SimpleSealedString("asdfg"))
                        }
                    }
                }
            }
        }

        describe("parsing values with a dynamically installed serializer") {
            describe("parsing a literal with a contextual serializer") {
                val contextSerializer = object : KSerializer<Inner> {
                    override val descriptor: SerialDescriptor
                        get() = String.serializer().descriptor

                    override fun deserialize(decoder: Decoder): Inner = Inner("from context serializer")
                    override fun serialize(encoder: Encoder, value: Inner) = throw UnsupportedOperationException()
                }

                val module = serializersModuleOf(Inner::class, contextSerializer)
                val parser = Yaml(serializersModule = module)

                val input = """
                    inner: this is the input
                """.trimIndent()

                val result = parser.decodeFromString(Container.serializer(), input)

                it("deserializes it using the dynamically installed serializer") {
                    expect(result).toEqual(Container(Inner("from context serializer")))
                }
            }

            describe("parsing a class with a contextual serializer") {
                val contextSerializer = object : KSerializer<Inner> {
                    override val descriptor = buildClassSerialDescriptor("Inner") {
                        element("thing", String.serializer().descriptor)
                    }

                    override fun deserialize(decoder: Decoder): Inner {
                        val objectDecoder = decoder.beginStructure(descriptor)
                        val index = objectDecoder.decodeElementIndex(descriptor)
                        val name = objectDecoder.decodeStringElement(descriptor, index)
                        objectDecoder.endStructure(descriptor)

                        return Inner("$name, from context serializer")
                    }

                    override fun serialize(encoder: Encoder, value: Inner) = throw UnsupportedOperationException()
                }

                val module = serializersModuleOf(Inner::class, contextSerializer)
                val parser = Yaml(serializersModule = module)

                val input = """
                    inner:
                        thing: this is the input
                """.trimIndent()

                val result = parser.decodeFromString(Container.serializer(), input)

                it("deserializes it using the dynamically installed serializer") {
                    expect(result).toEqual(Container(Inner("this is the input, from context serializer")))
                }
            }

            describe("parsing a map with a contextual serializer") {
                val contextSerializer = object : KSerializer<Inner> {
                    override val descriptor = buildSerialDescriptor("Inner", StructureKind.MAP) {
                        element("key", String.serializer().descriptor)
                        element("value", String.serializer().descriptor)
                    }

                    override fun deserialize(decoder: Decoder): Inner {
                        val objectDecoder = decoder.beginStructure(descriptor)
                        val keyIndex = objectDecoder.decodeElementIndex(descriptor)
                        val key = objectDecoder.decodeStringElement(descriptor, keyIndex)
                        val valueIndex = objectDecoder.decodeElementIndex(descriptor)
                        val value = objectDecoder.decodeStringElement(descriptor, valueIndex)

                        objectDecoder.endStructure(descriptor)

                        return Inner("$key: $value, from context serializer")
                    }

                    override fun serialize(encoder: Encoder, value: Inner) = throw UnsupportedOperationException()
                }

                val module = serializersModuleOf(Inner::class, contextSerializer)
                val parser = Yaml(serializersModule = module)

                val input = """
                    inner:
                        thing: this is the input
                """.trimIndent()

                val result = parser.decodeFromString(Container.serializer(), input)

                it("deserializes it using the dynamically installed serializer") {
                    expect(result).toEqual(Container(Inner("thing: this is the input, from context serializer")))
                }
            }
        }

        describe("parsing values with mismatched types") {
            context("given a list") {
                listOf(
                    "a string" to String.serializer(),
                    "an integer" to Int.serializer(),
                    "a long" to Long.serializer(),
                    "a short" to Short.serializer(),
                    "a byte" to Byte.serializer(),
                    "a double" to Double.serializer(),
                    "a float" to Float.serializer(),
                    "a boolean" to Boolean.serializer(),
                    "a character" to Char.serializer(),
                    "an enumeration value" to TestEnum.serializer(),
                    "a map" to MapSerializer(String.serializer(), String.serializer()),
                    "an object" to ComplexStructure.serializer(),
                    "a string" to String.serializer().nullable
                ).forEach { (description, serializer) ->
                    val input = "- thing"

                    context("parsing that input as $description") {
                        it("throws an exception with the correct location information") {
                            expect({ Yaml.default.decodeFromString(serializer, input) }).toThrow<IncorrectTypeException> {
                                message { toEqual("Expected $description, but got a list") }
                                line { toEqual(1) }
                                column { toEqual(1) }
                                path { toEqual(YamlPath.root) }
                            }
                        }
                    }
                }

                context("parsing that input as the value in a map") {
                    val input = """
                        key:
                            - some_value
                    """.trimIndent()

                    it("throws an exception with the correct location information") {
                        expect({ Yaml.default.decodeFromString(MapSerializer(String.serializer(), String.serializer()), input) }).toThrow<InvalidPropertyValueException> {
                            message { toEqual("Value for 'key' is invalid: Expected a string, but got a list") }
                            line { toEqual(2) }
                            column { toEqual(5) }
                            path { toEqual(YamlPath.root.withMapElementKey("key", Location(1, 1)).withMapElementValue(Location(2, 5))) }
                        }
                    }
                }

                context("parsing that input as the value in an object") {
                    val input = """
                        string:
                            - some_value
                    """.trimIndent()

                    it("throws an exception with the correct location information") {
                        expect({ Yaml.default.decodeFromString(ComplexStructure.serializer(), input) }).toThrow<InvalidPropertyValueException> {
                            message { toEqual("Value for 'string' is invalid: Expected a string, but got a list") }
                            line { toEqual(2) }
                            column { toEqual(5) }
                            path { toEqual(YamlPath.root.withMapElementKey("string", Location(1, 1)).withMapElementValue(Location(2, 5))) }
                        }
                    }
                }

                context("parsing that input as the value in a list") {
                    val input = """
                        - [ some_value ]
                    """.trimIndent()

                    it("throws an exception with the correct location information") {
                        expect({ Yaml.default.decodeFromString(ListSerializer(String.serializer()), input) }).toThrow<IncorrectTypeException> {
                            message { toEqual("Expected a string, but got a list") }
                            line { toEqual(1) }
                            column { toEqual(3) }
                            path { toEqual(YamlPath.root.withListEntry(0, Location(1, 3))) }
                        }
                    }
                }
            }

            context("given a map") {
                listOf(
                    "a string" to String.serializer(),
                    "an integer" to Int.serializer(),
                    "a long" to Long.serializer(),
                    "a short" to Short.serializer(),
                    "a byte" to Byte.serializer(),
                    "a double" to Double.serializer(),
                    "a float" to Float.serializer(),
                    "a boolean" to Boolean.serializer(),
                    "a character" to Char.serializer(),
                    "an enumeration value" to TestEnum.serializer(),
                    "a list" to ListSerializer(String.serializer()),
                    "a string" to String.serializer().nullable
                ).forEach { (description, serializer) ->
                    val input = "key: value"

                    context("parsing that input as $description") {
                        it("throws an exception with the correct location information") {
                            expect({ Yaml.default.decodeFromString(serializer, input) }).toThrow<IncorrectTypeException> {
                                message { toEqual("Expected $description, but got a map") }
                                line { toEqual(1) }
                                column { toEqual(1) }
                                path { toEqual(YamlPath.root) }
                            }
                        }
                    }
                }

                context("parsing that input as the value in a map") {
                    val input = """
                        key:
                            some_key: some_value
                    """.trimIndent()

                    it("throws an exception with the correct location information") {
                        expect({ Yaml.default.decodeFromString(MapSerializer(String.serializer(), String.serializer()), input) }).toThrow<InvalidPropertyValueException> {
                            message { toEqual("Value for 'key' is invalid: Expected a string, but got a map") }
                            line { toEqual(2) }
                            column { toEqual(5) }
                            path { toEqual(YamlPath.root.withMapElementKey("key", Location(1, 1)).withMapElementValue(Location(2, 5))) }
                        }
                    }
                }

                context("parsing that input as the value in an object") {
                    val input = """
                        string:
                            some_key: some_value
                    """.trimIndent()

                    it("throws an exception with the correct location information") {
                        expect({ Yaml.default.decodeFromString(ComplexStructure.serializer(), input) }).toThrow<InvalidPropertyValueException> {
                            message { toEqual("Value for 'string' is invalid: Expected a string, but got a map") }
                            line { toEqual(2) }
                            column { toEqual(5) }
                            path { toEqual(YamlPath.root.withMapElementKey("string", Location(1, 1)).withMapElementValue(Location(2, 5))) }
                        }
                    }
                }

                context("parsing that input as the value in a list") {
                    val input = """
                        - some_key: some_value
                    """.trimIndent()

                    it("throws an exception with the correct location information") {
                        expect({ Yaml.default.decodeFromString(ListSerializer(String.serializer()), input) }).toThrow<IncorrectTypeException> {
                            message { toEqual("Expected a string, but got a map") }
                            line { toEqual(1) }
                            column { toEqual(3) }
                            path { toEqual(YamlPath.root.withListEntry(0, Location(1, 3))) }
                        }
                    }
                }
            }

            context("given a scalar value") {
                mapOf(
                    "a list" to ListSerializer(String.serializer()),
                    "a map" to MapSerializer(String.serializer(), String.serializer()),
                    "an object" to ComplexStructure.serializer()
                ).forEach { (description, serializer) ->
                    val input = "blah"

                    context("parsing that input as $description") {
                        it("throws an exception with the correct location information") {
                            expect({ Yaml.default.decodeFromString(serializer, input) }).toThrow<IncorrectTypeException> {
                                message { toEqual("Expected $description, but got a scalar value") }
                                line { toEqual(1) }
                                column { toEqual(1) }
                                path { toEqual(YamlPath.root) }
                            }
                        }
                    }
                }

                context("parsing that input as the value in a map") {
                    val input = """
                        key: some_value
                    """.trimIndent()

                    it("throws an exception with the correct location information") {
                        expect({ Yaml.default.decodeFromString(MapSerializer(String.serializer(), ListSerializer(String.serializer())), input) }).toThrow<InvalidPropertyValueException> {
                            message { toEqual("Value for 'key' is invalid: Expected a list, but got a scalar value") }
                            line { toEqual(1) }
                            column { toEqual(6) }
                            path { toEqual(YamlPath.root.withMapElementKey("key", Location(1, 1)).withMapElementValue(Location(1, 6))) }
                        }
                    }
                }

                context("parsing that input as the value in an object") {
                    val input = """
                        members: some_value
                    """.trimIndent()

                    it("throws an exception with the correct location information") {
                        expect({ Yaml.default.decodeFromString(Team.serializer(), input) }).toThrow<InvalidPropertyValueException> {
                            message { toEqual("Value for 'members' is invalid: Expected a list, but got a scalar value") }
                            line { toEqual(1) }
                            column { toEqual(10) }
                            path { toEqual(YamlPath.root.withMapElementKey("members", Location(1, 1)).withMapElementValue(Location(1, 10))) }
                        }
                    }
                }

                context("parsing that input as the value in a list") {
                    val input = """
                        - some_value
                    """.trimIndent()

                    it("throws an exception with the correct location information") {
                        expect({ Yaml.default.decodeFromString(ListSerializer((ListSerializer(String.serializer()))), input) }).toThrow<IncorrectTypeException> {
                            message { toEqual("Expected a list, but got a scalar value") }
                            line { toEqual(1) }
                            column { toEqual(3) }
                            path { toEqual(YamlPath.root.withListEntry(0, Location(1, 3))) }
                        }
                    }
                }
            }
        }

        describe("parsing values with a contextual serializer") {
            mapOf(
                "scalar" to "2",
                "list" to "[ thing ]",
                "map" to "{ key: value }"
            ).forEach { (description, input) ->
                context("given some input representing a $description") {
                    context("parsing that input using a contextual serializer at the top level") {
                        val result = Yaml.default.decodeFromString(ContextualSerializer, input)

                        it("the serializer receives the top-level object") {
                            expect(result).toEqual(description)
                        }
                    }

                    context("parsing that input using a contextual serializer nested within an object") {
                        val result = Yaml.default.decodeFromString(ObjectWithNestedContextualSerializer.serializer(), "thing: $input")

                        it("the serializer receives the correct object") {
                            expect(result).toEqual(ObjectWithNestedContextualSerializer(description))
                        }
                    }
                }
            }

            describe("given the contextual serializer attempts to begin a structure that does not match the input") {
                context("given the input is a map") {
                    val input = "a: b"

                    mapOf(
                        PrimitiveKind.STRING to "a string",
                        StructureKind.LIST to "a list"
                    ).forEach { (kind, description) ->
                        context("attempting to begin $description") {
                            it("throws an exception with the correct location information") {
                                expect({ Yaml.default.decodeFromString(ContextualSerializerThatAttemptsToDeserializeIncorrectType(kind), input) }).toThrow<IncorrectTypeException> {
                                    message { toEqual("Expected $description, but got a map") }
                                    line { toEqual(1) }
                                    column { toEqual(1) }
                                    path { toEqual(YamlPath.root) }
                                }
                            }
                        }
                    }
                }

                context("given the input is a list") {
                    val input = "- a"

                    mapOf(
                        StructureKind.OBJECT to "an object",
                        StructureKind.CLASS to "an object",
                        StructureKind.MAP to "a map",
                        PrimitiveKind.STRING to "a string"
                    ).forEach { (kind, description) ->
                        context("attempting to begin $description") {
                            it("throws an exception with the correct location information") {
                                expect({ Yaml.default.decodeFromString(ContextualSerializerThatAttemptsToDeserializeIncorrectType(kind), input) }).toThrow<IncorrectTypeException> {
                                    message { toEqual("Expected $description, but got a list") }
                                    line { toEqual(1) }
                                    column { toEqual(1) }
                                    path { toEqual(YamlPath.root) }
                                }
                            }
                        }
                    }
                }

                context("given the input is a scalar") {
                    val input = "2"

                    mapOf(
                        StructureKind.OBJECT to "an object",
                        StructureKind.CLASS to "an object",
                        StructureKind.MAP to "a map",
                        StructureKind.LIST to "a list"
                    ).forEach { (kind, description) ->
                        context("attempting to begin $description") {
                            it("throws an exception with the correct location information") {
                                expect({ Yaml.default.decodeFromString(ContextualSerializerThatAttemptsToDeserializeIncorrectType(kind), input) }).toThrow<IncorrectTypeException> {
                                    message { toEqual("Expected $description, but got a scalar value") }
                                    line { toEqual(1) }
                                    column { toEqual(1) }
                                    path { toEqual(YamlPath.root) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
})

@Serializable
private data class ComplexStructure(
    val string: String,
    val byte: Byte,
    val short: Short,
    val int: Int,
    val long: Long,
    val float: Float,
    val double: Double,
    val enum: TestEnum,
    val boolean: Boolean,
    val char: Char,
    val nullable: String? = null
)

@Serializable
private data class StructureWithLocationThrowingSerializer(
    @Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
    @Serializable(with = LocationThrowingSerializer::class)
    val value: CustomSerializedValue
)

private data class CustomSerializedValue(val thing: String)

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
private object LocationThrowingSerializer : KSerializer<Any> {
    override val descriptor = buildSerialDescriptor(LocationThrowingSerializer::class.qualifiedName!!, SerialKind.CONTEXTUAL)

    override fun deserialize(decoder: Decoder): Any {
        val location = (decoder as YamlInput).getCurrentLocation()
        val path = decoder.getCurrentPath()

        throw LocationInformationException("Serializer called with location (${location.line}, ${location.column}) and path: ${path.toHumanReadableString()}")
    }

    override fun serialize(encoder: Encoder, value: Any) = throw UnsupportedOperationException()
}

private object LocationThrowingMapSerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = MapSerializer(String.serializer(), String.serializer()).descriptor

    override fun deserialize(decoder: Decoder): Any {
        val location = (decoder as YamlInput).getCurrentLocation()
        val path = decoder.getCurrentPath()

        throw LocationInformationException("Serializer called with location (${location.line}, ${location.column}) and path: ${path.toHumanReadableString()}")
    }

    override fun serialize(encoder: Encoder, value: Any) = throw UnsupportedOperationException()
}

private class LocationInformationException(message: String) : RuntimeException(message)

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
object ContextualSerializer : KSerializer<String> {
    override val descriptor = buildSerialDescriptor("ContextualSerializer", SerialKind.CONTEXTUAL) {
        element("string", PrimitiveSerialDescriptor("value", PrimitiveKind.STRING))
        element("object", buildSerialDescriptor("thing", StructureKind.OBJECT))
    }

    override fun deserialize(decoder: Decoder): String {
        val input = decoder.beginStructure(descriptor) as YamlInput
        val type = input.node::class.simpleName!!
        input.endStructure(descriptor)

        return type.removePrefix("Yaml").lowercase()
    }

    override fun serialize(encoder: Encoder, value: String): Unit = throw UnsupportedOperationException()
}

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
class ContextualSerializerThatAttemptsToDeserializeIncorrectType(private val kind: SerialKind) : KSerializer<String> {
    private val innerDescriptor = if (kind == StructureKind.CLASS) buildClassSerialDescriptor("thing") else buildSerialDescriptor("thing", kind)

    override val descriptor = buildSerialDescriptor("ContextualSerializer", SerialKind.CONTEXTUAL) {
        element("string", PrimitiveSerialDescriptor("value", PrimitiveKind.STRING))
        element("object", innerDescriptor)
    }

    override fun deserialize(decoder: Decoder): String {
        val input = decoder.beginStructure(descriptor) as YamlInput

        input.beginStructure(innerDescriptor)

        return "Should never get to this point"
    }

    override fun serialize(encoder: Encoder, value: String): Unit = throw UnsupportedOperationException()
}

// FIXME: ideally these would just be inline in the test cases that need them, but due to
// https://github.com/Kotlin/kotlinx.serialization/issues/1427, this is no longer possible with
// kotlinx.serialization 1.2 and above.
// See also https://github.com/Kotlin/kotlinx.serialization/issues/1468.

@Serializable
private data class NullableNestedObject(val firstPerson: SimpleStructure?)

@Serializable
data class NullableNestedList(val members: List<String>?)

@Serializable
private data class Database(val host: String)

private data class Inner(val name: String)

@Serializable
private data class Container(@Contextual val inner: Inner)

@Serializable
private data class ObjectWithNestedContextualSerializer(@Serializable(with = ContextualSerializer::class) val thing: String)

@Serializable
@JvmInline
value class StringValue(val value: String)
