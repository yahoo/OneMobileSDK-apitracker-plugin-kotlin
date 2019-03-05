/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.apitracker

import com.aol.mobile.sdk.apitracker.dto.TypeDescriptor
import com.aol.mobile.sdk.apitracker.dto.asTypeDescriptorList
import com.aol.mobile.sdk.apitracker.utils.Proguard
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class ProguardGeneratorTest {
    private lateinit var expectedProguard: String
    private lateinit var actualProguard: String

    private lateinit var typeDescriptors: List<TypeDescriptor>

    @Before
    fun before() {
        expectedProguard = """
            -keep public class com.aol.mobile.publicapi.player.OneSDK {
                public protected *;
            }

            -keep public class com.aol.mobile.publicapi.player.OneSDKBuilder {
                public protected *;
            }

            -keep public class com.aol.mobile.publicapi.player.OneSDKBuilder.Callback {
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
                "name": "com.aol.mobile.publicapi.player.OneSDK",
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
                        "type": "com.aol.mobile.publicapi.player.http.model.SdkConfig"
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
                        "type": "com.aol.mobile.publicapi.player.Plugin"
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
                        "type": "com.aol.mobile.publicapi.player.Plugin"
                      }
                    ]
                  },
                  {
                    "modifiers": [
                      "public"
                    ],
                    "name": "getVideoProvider",
                    "returnType": "com.aol.mobile.publicapi.player.VideoProvider",
                    "params": []
                  },
                  {
                    "modifiers": [
                      "public"
                    ],
                    "name": "createBuilder",
                    "returnType": "com.aol.mobile.publicapi.player.PlayerBuilder",
                    "params": []
                  }
                ]
              },
              {
                "modifiers": [
                  "public",
                  "final"
                ],
                "name": "com.aol.mobile.publicapi.player.OneSDKBuilder",
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
                    "returnType": "com.aol.mobile.publicapi.player.OneSDKBuilder",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "environment",
                        "type": "com.aol.mobile.publicapi.player.http.model.Environment"
                      }
                    ]
                  },
                  {
                    "modifiers": [
                      "public"
                    ],
                    "name": "setExtra",
                    "returnType": "com.aol.mobile.publicapi.player.OneSDKBuilder",
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
                    "returnType": "com.aol.mobile.publicapi.player.OneSDKBuilder",
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
                    "returnType": "com.aol.mobile.publicapi.player.OneSDKBuilder",
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
                    "returnType": "com.aol.mobile.publicapi.player.OneSDKBuilder",
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
                        "type": "com.aol.mobile.publicapi.player.OneSDKBuilder.Callback"
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
                "name": "com.aol.mobile.publicapi.player.OneSDKBuilder.Callback",
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
                        "type": "com.aol.mobile.publicapi.player.OneSDK"
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
        actualProguard = Proguard.generateRules(typeDescriptors)

        assertThat(expectedProguard).isEqualTo(actualProguard)
    }
}
