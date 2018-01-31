/*
 * Copyright (c) 2018. Oath.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.aol.mobile.sdk.apitracker

import com.aol.mobile.sdk.apitracker.UD.Kind.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class DescriptorsTransformerTest {
    private lateinit var typeDescriptors: List<TypeDescriptor>

    @Before
    fun before() {
        val json = """
            [
              {
                "modifiers": [
                  "public",
                  "final"
                ],
                "name": "com.aol.mobile.sdk.player.OneSDK",
                "fields": [
                  {
                    "modifiers": [
                      "public",
                      "final"
                    ],
                    "name": "field1",
                    "type": "int"
                  }
                ],
                "methods": [
                  {
                    "modifiers": [
                      "public"
                    ],
                    "name": "method1",
                    "returnType": "void",
                    "params": [
                      {
                        "modifiers": [],
                        "type": "android.content.Context"
                      }
                    ]
                  }
                ]
              },
              {
                "modifiers": [
                  "public",
                  "final"
                ],
                "name": "com.aol.mobile.sdk.player.TwoSDK",
                "fields": [
                  {
                    "modifiers": [
                      "public",
                      "final"
                    ],
                    "name": "field2",
                    "type": "int"
                  }
                ],
                "methods": [
                  {
                    "modifiers": [
                      "public"
                    ],
                    "name": "method2",
                    "returnType": "void",
                    "params": [
                      {
                        "modifiers": [],
                        "type": "android.content.Context"
                      }
                    ]
                  }
                ]
              }
            ]
            """

        typeDescriptors = json.asTypeDescriptorList()
    }

    @Test
    fun testTransformationOfTypeDescriptorsToUniversalDescriptors() {
        val UDS: List<UD> = typeDescriptors.map { it.toUdList() }.flatten()

        assertThat(UDS.size).isEqualTo(6)

        assertThat(UDS[0].fullName).isEqualTo("com.aol.mobile.sdk.player.OneSDK")
        assertThat(UDS[0].kind).isEqualTo(TYPE)

        assertThat(UDS[1].fullName).isEqualTo("com.aol.mobile.sdk.player.OneSDK.field1")
        assertThat(UDS[1].kind).isEqualTo(FIELD)
        assertThat(UDS[1].returnType).isEqualTo("int")

        assertThat(UDS[5].fullName).isEqualTo("com.aol.mobile.sdk.player.TwoSDK.method2")
        assertThat(UDS[5].kind).isEqualTo(METHOD)
        assertThat(UDS[5].returnType).isEqualTo("void")
    }
}
