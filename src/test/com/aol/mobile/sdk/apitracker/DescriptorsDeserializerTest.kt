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

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DescriptorsDeserializerTest {
    @Test
    fun testDeserialization() {
        val json = "[\n" +
                "  {\n" +
                "    \"modifiers\": [\n" +
                "      \"public\",\n" +
                "      \"final\"\n" +
                "    ],\n" +
                "    \"name\": \"com.aol.mobile.sdk.player.OneSDK\",\n" +
                "    \"fields\": [],\n" +
                "    \"methods\": [\n" +
                "      {\n" +
                "        \"modifiers\": [\n" +
                "          \"public\"\n" +
                "        ],\n" +
                "        \"name\": \"constructor\",\n" +
                "        \"returnType\": \"void\",\n" +
                "        \"params\": [\n" +
                "          {\n" +
                "            \"modifiers\": [],\n" +
                "            \"name\": \"context\",\n" +
                "            \"type\": \"android.content.Context\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"modifiers\": [],\n" +
                "            \"name\": \"sdkConfig\",\n" +
                "            \"type\": \"com.aol.mobile.sdk.player.http.model.SdkConfig\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"modifiers\": [\n" +
                "          \"public\"\n" +
                "        ],\n" +
                "        \"name\": \"addPlugin\",\n" +
                "        \"returnType\": \"void\",\n" +
                "        \"params\": [\n" +
                "          {\n" +
                "            \"modifiers\": [],\n" +
                "            \"name\": \"plugin\",\n" +
                "            \"type\": \"com.aol.mobile.sdk.player.Plugin\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"modifiers\": [\n" +
                "          \"public\"\n" +
                "        ],\n" +
                "        \"name\": \"removePlugin\",\n" +
                "        \"returnType\": \"void\",\n" +
                "        \"params\": [\n" +
                "          {\n" +
                "            \"modifiers\": [],\n" +
                "            \"name\": \"plugin\",\n" +
                "            \"type\": \"com.aol.mobile.sdk.player.Plugin\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"modifiers\": [\n" +
                "          \"public\"\n" +
                "        ],\n" +
                "        \"name\": \"getVideoProvider\",\n" +
                "        \"returnType\": \"com.aol.mobile.sdk.player.VideoProvider\",\n" +
                "        \"params\": []\n" +
                "      },\n" +
                "      {\n" +
                "        \"modifiers\": [\n" +
                "          \"public\"\n" +
                "        ],\n" +
                "        \"name\": \"createBuilder\",\n" +
                "        \"returnType\": \"com.aol.mobile.sdk.player.PlayerBuilder\",\n" +
                "        \"params\": []\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"modifiers\": [\n" +
                "      \"public\",\n" +
                "      \"final\"\n" +
                "    ],\n" +
                "    \"name\": \"com.aol.mobile.sdk.player.OneSDKBuilder\",\n" +
                "    \"fields\": [],\n" +
                "    \"methods\": [\n" +
                "      {\n" +
                "        \"modifiers\": [\n" +
                "          \"public\"\n" +
                "        ],\n" +
                "        \"name\": \"constructor\",\n" +
                "        \"returnType\": \"void\",\n" +
                "        \"params\": [\n" +
                "          {\n" +
                "            \"modifiers\": [],\n" +
                "            \"name\": \"context\",\n" +
                "            \"type\": \"android.content.Context\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"modifiers\": [\n" +
                "          \"public\"\n" +
                "        ],\n" +
                "        \"name\": \"setEnvironment\",\n" +
                "        \"returnType\": \"com.aol.mobile.sdk.player.OneSDKBuilder\",\n" +
                "        \"params\": [\n" +
                "          {\n" +
                "            \"modifiers\": [],\n" +
                "            \"name\": \"environment\",\n" +
                "            \"type\": \"com.aol.mobile.sdk.player.http.model.Environment\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"modifiers\": [\n" +
                "          \"public\"\n" +
                "        ],\n" +
                "        \"name\": \"setExtra\",\n" +
                "        \"returnType\": \"com.aol.mobile.sdk.player.OneSDKBuilder\",\n" +
                "        \"params\": [\n" +
                "          {\n" +
                "            \"modifiers\": [],\n" +
                "            \"name\": \"extra\",\n" +
                "            \"type\": \"org.json.JSONObject\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"modifiers\": [\n" +
                "          \"public\"\n" +
                "        ],\n" +
                "        \"name\": \"setAdvertisingId\",\n" +
                "        \"returnType\": \"com.aol.mobile.sdk.player.OneSDKBuilder\",\n" +
                "        \"params\": [\n" +
                "          {\n" +
                "            \"modifiers\": [],\n" +
                "            \"name\": \"advertId\",\n" +
                "            \"type\": \"java.lang.String\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"modifiers\": [\n" +
                "          \"public\"\n" +
                "        ],\n" +
                "        \"name\": \"setLimitAdTracking\",\n" +
                "        \"returnType\": \"com.aol.mobile.sdk.player.OneSDKBuilder\",\n" +
                "        \"params\": [\n" +
                "          {\n" +
                "            \"modifiers\": [],\n" +
                "            \"name\": \"limitAdTracking\",\n" +
                "            \"type\": \"boolean\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"modifiers\": [\n" +
                "          \"public\"\n" +
                "        ],\n" +
                "        \"name\": \"setConfigUrl\",\n" +
                "        \"returnType\": \"com.aol.mobile.sdk.player.OneSDKBuilder\",\n" +
                "        \"params\": [\n" +
                "          {\n" +
                "            \"modifiers\": [],\n" +
                "            \"name\": \"newConfigUrl\",\n" +
                "            \"type\": \"java.lang.String\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"modifiers\": [\n" +
                "          \"public\"\n" +
                "        ],\n" +
                "        \"name\": \"create\",\n" +
                "        \"returnType\": \"void\",\n" +
                "        \"params\": [\n" +
                "          {\n" +
                "            \"modifiers\": [\n" +
                "              \"final\"\n" +
                "            ],\n" +
                "            \"name\": \"callback\",\n" +
                "            \"type\": \"com.aol.mobile.sdk.player.OneSDKBuilder.Callback\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"modifiers\": [\n" +
                "      \"public\",\n" +
                "      \"abstract\",\n" +
                "      \"static\"\n" +
                "    ],\n" +
                "    \"name\": \"com.aol.mobile.sdk.player.OneSDKBuilder.Callback\",\n" +
                "    \"fields\": [],\n" +
                "    \"methods\": [\n" +
                "      {\n" +
                "        \"modifiers\": [\n" +
                "          \"public\",\n" +
                "          \"abstract\"\n" +
                "        ],\n" +
                "        \"name\": \"onSuccess\",\n" +
                "        \"returnType\": \"void\",\n" +
                "        \"params\": [\n" +
                "          {\n" +
                "            \"modifiers\": [],\n" +
                "            \"name\": \"oneSDK\",\n" +
                "            \"type\": \"com.aol.mobile.sdk.player.OneSDK\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"modifiers\": [\n" +
                "          \"public\",\n" +
                "          \"abstract\"\n" +
                "        ],\n" +
                "        \"name\": \"onFailure\",\n" +
                "        \"returnType\": \"void\",\n" +
                "        \"params\": [\n" +
                "          {\n" +
                "            \"modifiers\": [],\n" +
                "            \"name\": \"error\",\n" +
                "            \"type\": \"java.lang.Exception\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }]"

        val typeDescriptors: List<TypeDescriptor> = deserializeTypeDescriptors(json)
        assertThat(typeDescriptors.size).isEqualTo(3)
        assertThat(typeDescriptors[0].name).isEqualTo("com.aol.mobile.sdk.player.OneSDK")
        val firstMethodDescriptor: MethodDescriptor = typeDescriptors[0].methods.iterator().next()
        assertThat(firstMethodDescriptor.name).isEqualTo("constructor")
        assertThat(firstMethodDescriptor.params.size).isEqualTo(2)
    }
}
