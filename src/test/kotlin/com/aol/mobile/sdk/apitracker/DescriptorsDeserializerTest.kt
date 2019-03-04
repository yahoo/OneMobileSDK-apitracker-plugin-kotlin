/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.apitracker

import com.verizonmedia.mobile.publicapi.apitracker.dto.MethodDescriptor
import com.verizonmedia.mobile.publicapi.apitracker.dto.TypeDescriptor
import com.verizonmedia.mobile.publicapi.apitracker.dto.asTypeDescriptorList
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DescriptorsDeserializerTest {
    @Test
    fun testTypeDescriptorsDeserializationFromJson() {
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

        val typeDescriptors: List<TypeDescriptor> = json.asTypeDescriptorList()
        assertThat(typeDescriptors.size).isEqualTo(3)
        assertThat(typeDescriptors[0].name).isEqualTo("com.aol.mobile.publicapi.player.OneSDK")
        val firstMethodDescriptor: MethodDescriptor = typeDescriptors[0].methods.iterator().next()
        assertThat(firstMethodDescriptor.name).isEqualTo("constructor")
        assertThat(firstMethodDescriptor.params.size).isEqualTo(2)
    }
}
