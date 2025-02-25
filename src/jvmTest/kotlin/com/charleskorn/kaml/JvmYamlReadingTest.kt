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

package com.charleskorn.kaml

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import kotlinx.serialization.builtins.serializer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.ByteArrayInputStream

object JvmYamlReadingTest : Spek({
    describe("JVM-specific extensions for YAML reading") {
        describe("parsing from a stream") {
            val input = "123"
            val result = Yaml.default.decodeFromStream(Int.serializer(), ByteArrayInputStream(input.toByteArray(Charsets.UTF_8)))

            it("successfully deserializes values from a stream") {
                expect(result).toEqual(123)
            }
        }
    }
})
