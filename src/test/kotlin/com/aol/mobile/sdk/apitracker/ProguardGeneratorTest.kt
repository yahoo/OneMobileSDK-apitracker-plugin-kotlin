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

import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProguardGeneratorTest {
    private lateinit var expectedProguard: String
    private lateinit var actualProguard: String

    private lateinit var typeDescriptors: List<TypeDescriptor>

    @Before
    fun before() {
        expectedProguard = """
            -keep public class com.aol.mobile.sdk.player.OneSDK {
                public protected *;
            }

            -keep public class com.aol.mobile.sdk.player.OneSDKBuilder {
                public protected *;
            }

            -keep public class com.aol.mobile.sdk.player.OneSDKBuilder.Callback {
                public protected *;
            }

            """.trimIndent()

        val json = """
            [
              {
                "modifiers": [
                  "public",
                  "final"
                ],
                "name": "com.aol.mobile.sdk.player.OneSDK",
                "fields": [],
                "methods": [
                  {
                    "modifiers": [
                      "public"
                    ],
                    "name": "constructor",
                    "returnType": "void",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "context",
                        "type": "android.content.Context"
                      },
                      {
                        "modifiers": [],
                        "name": "sdkConfig",
                        "type": "com.aol.mobile.sdk.player.http.model.SdkConfig"
                      }
                    ]
                  },
                  {
                    "modifiers": [
                      "public"
                    ],
                    "name": "addPlugin",
                    "returnType": "void",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "plugin",
                        "type": "com.aol.mobile.sdk.player.Plugin"
                      }
                    ]
                  },
                  {
                    "modifiers": [
                      "public"
                    ],
                    "name": "removePlugin",
                    "returnType": "void",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "plugin",
                        "type": "com.aol.mobile.sdk.player.Plugin"
                      }
                    ]
                  },
                  {
                    "modifiers": [
                      "public"
                    ],
                    "name": "getVideoProvider",
                    "returnType": "com.aol.mobile.sdk.player.VideoProvider",
                    "params": []
                  },
                  {
                    "modifiers": [
                      "public"
                    ],
                    "name": "createBuilder",
                    "returnType": "com.aol.mobile.sdk.player.PlayerBuilder",
                    "params": []
                  }
                ]
              },
              {
                "modifiers": [
                  "public",
                  "final"
                ],
                "name": "com.aol.mobile.sdk.player.OneSDKBuilder",
                "fields": [],
                "methods": [
                  {
                    "modifiers": [
                      "public"
                    ],
                    "name": "constructor",
                    "returnType": "void",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "context",
                        "type": "android.content.Context"
                      }
                    ]
                  },
                  {
                    "modifiers": [
                      "public"
                    ],
                    "name": "setEnvironment",
                    "returnType": "com.aol.mobile.sdk.player.OneSDKBuilder",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "environment",
                        "type": "com.aol.mobile.sdk.player.http.model.Environment"
                      }
                    ]
                  },
                  {
                    "modifiers": [
                      "public"
                    ],
                    "name": "setExtra",
                    "returnType": "com.aol.mobile.sdk.player.OneSDKBuilder",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "extra",
                        "type": "org.json.JSONObject"
                      }
                    ]
                  },
                  {
                    "modifiers": [
                      "public"
                    ],
                    "name": "setAdvertisingId",
                    "returnType": "com.aol.mobile.sdk.player.OneSDKBuilder",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "advertId",
                        "type": "java.lang.String"
                      }
                    ]
                  },
                  {
                    "modifiers": [
                      "public"
                    ],
                    "name": "setLimitAdTracking",
                    "returnType": "com.aol.mobile.sdk.player.OneSDKBuilder",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "limitAdTracking",
                        "type": "boolean"
                      }
                    ]
                  },
                  {
                    "modifiers": [
                      "public"
                    ],
                    "name": "setConfigUrl",
                    "returnType": "com.aol.mobile.sdk.player.OneSDKBuilder",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "newConfigUrl",
                        "type": "java.lang.String"
                      }
                    ]
                  },
                  {
                    "modifiers": [
                      "public"
                    ],
                    "name": "create",
                    "returnType": "void",
                    "params": [
                      {
                        "modifiers": [
                          "final"
                        ],
                        "name": "callback",
                        "type": "com.aol.mobile.sdk.player.OneSDKBuilder.Callback"
                      }
                    ]
                  }
                ]
              },
              {
                "modifiers": [
                  "public",
                  "abstract",
                  "static"
                ],
                "name": "com.aol.mobile.sdk.player.OneSDKBuilder.Callback",
                "fields": [],
                "methods": [
                  {
                    "modifiers": [
                      "public",
                      "abstract"
                    ],
                    "name": "onSuccess",
                    "returnType": "void",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "oneSDK",
                        "type": "com.aol.mobile.sdk.player.OneSDK"
                      }
                    ]
                  },
                  {
                    "modifiers": [
                      "public",
                      "abstract"
                    ],
                    "name": "onFailure",
                    "returnType": "void",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "error",
                        "type": "java.lang.Exception"
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
    fun testProguardGenerationOfTypes() {
        actualProguard = generateProguardContent(typeDescriptors)

        assertEquals(expectedProguard, actualProguard)
    }
}
