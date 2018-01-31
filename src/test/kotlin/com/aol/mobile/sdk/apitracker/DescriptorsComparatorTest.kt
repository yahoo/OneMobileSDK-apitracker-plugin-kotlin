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

import com.aol.mobile.sdk.apitracker.UD.Companion.ChangeType.ADDITION
import com.aol.mobile.sdk.apitracker.UD.Companion.ChangeType.REMOVAL
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DescriptorsComparatorTest {

    @Test
    fun additionOfTypeShouldResultAddition() {
        val oldJson = """
            [
              {
                "modifiers": [
                  "public",
                  "final"
                ],
                "name": "com.aol.mobile.sdk.player.OneSDK",
                "fields": [],
                "methods": []
              }
            ]
            """
        val oldList = oldJson.asTypeDescriptorList().map { it.toUdList() }.flatten()

        val newJson = """
            [
              {
                "modifiers": [
                  "public",
                  "final"
                ],
                "name": "com.aol.mobile.sdk.player.OneSDK",
                "fields": [],
                "methods": []
              },
              {
                "modifiers": [
                  "public",
                  "final"
                ],
                "name": "com.aol.mobile.sdk.player.NEW_ONE_SDK",
                "fields": [],
                "methods": []
              }
            ]
            """
        val newList = newJson.asTypeDescriptorList().map { it.toUdList() }.flatten()
        val changes = UD.getChanges(oldList, newList)

        assertThat(changes.size).isEqualTo(1)
        assertThat(changes[newList[1]]).isEqualTo(UD.Companion.ChangeType.ADDITION)
    }

    @Test
    fun removalOfTypeShouldResultRemoval() {
        val oldJson = """
            [
              {
                "modifiers": [
                  "public",
                  "final"
                ],
                "name": "com.aol.mobile.sdk.player.OneSDK",
                "fields": [],
                "methods": []
              },
              {
                "modifiers": [
                  "public",
                  "final"
                ],
                "name": "com.aol.mobile.sdk.player.ONE_SDK_TO_BE_REMOVED",
                "fields": [],
                "methods": []
              }
            ]
            """
        val oldList = oldJson.asTypeDescriptorList().map { it.toUdList() }.flatten()

        val newJson = """
            [
              {
                "modifiers": [
                  "public",
                  "final"
                ],
                "name": "com.aol.mobile.sdk.player.OneSDK",
                "fields": [],
                "methods": []
              }
            ]
            """
        val newList = newJson.asTypeDescriptorList().map { it.toUdList() }.flatten()
        val changes = UD.getChanges(oldList, newList)

        assertThat(changes.size).isEqualTo(1)
        assertThat(changes[oldList[1]]).isEqualTo(REMOVAL)
    }

    @Test
    fun modificationOfTypeShouldResultAdditionAndRemoval() {
        val oldJson = """
            [
              {
                "modifiers": [
                  "public",
                  "final"
                ],
                "name": "com.aol.mobile.sdk.player.OneSDK",
                "fields": [],
                "methods": []
              }
            ]
            """
        val oldList = oldJson.asTypeDescriptorList().map { it.toUdList() }.flatten()

        val newJson = """
            [
              {
                "modifiers": [
                  "NEW_MODIFIER",
                  "public",
                  "final"
                ],
                "name": "com.aol.mobile.sdk.player.OneSDK",
                "fields": [],
                "methods": []
              }
            ]
            """
        val newList = newJson.asTypeDescriptorList().map { it.toUdList() }.flatten()
        val changes = UD.getChanges(oldList, newList)

        assertThat(changes.size).isEqualTo(2)
        assertThat(changes[oldList[0]]).isEqualTo(REMOVAL)
        assertThat(changes[newList[0]]).isEqualTo(ADDITION)
    }

    @Test
    fun additionOfFieldShouldResultAddition() {
        val oldJson = """
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
                "methods": []
              }
            ]
            """
        val oldList = oldJson.asTypeDescriptorList().map { it.toUdList() }.flatten()

        val newJson = """
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
                  },
                  {
                    "modifiers": [
                      "public",
                      "final"
                    ],
                    "name": "NEW_FIELD",
                    "type": "int"
                  }
                ],
                "methods": []
              }
            ]
            """
        val newList = newJson.asTypeDescriptorList().map { it.toUdList() }.flatten()
        val changes = UD.getChanges(oldList, newList)

        assertThat(changes.size).isEqualTo(1)
        assertThat(changes[newList[2]]).isEqualTo(ADDITION)
    }

    @Test
    fun removalOfFieldShouldResultRemoval() {
        val oldJson = """
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
                  },
                  {
                    "modifiers": [
                      "public",
                      "final"
                    ],
                    "name": "FIELD_TO_BE_REMOVED",
                    "type": "int"
                  }
                ],
                "methods": []
              }
            ]
            """
        val oldList = oldJson.asTypeDescriptorList().map { it.toUdList() }.flatten()

        val newJson = """
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
                "methods": []
              }
            ]
            """
        val newList = newJson.asTypeDescriptorList().map { it.toUdList() }.flatten()
        val changes = UD.getChanges(oldList, newList)

        assertThat(changes.size).isEqualTo(1)
        assertThat(changes[oldList[2]]).isEqualTo(REMOVAL)
    }

    @Test
    fun modificationOfFieldShouldResultAdditionAndRemoval() {
        val oldJson = """
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
                "methods": []
              }
            ]
            """
        val oldList = oldJson.asTypeDescriptorList().map { it.toUdList() }.flatten()

        val newJson = """
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
                      "NEW_MODIFIER",
                      "public",
                      "final"
                    ],
                    "name": "field1",
                    "type": "int"
                  }
                ],
                "methods": []
              }
            ]
            """
        val newList = newJson.asTypeDescriptorList().map { it.toUdList() }.flatten()
        val changes = UD.getChanges(oldList, newList)

        assertThat(changes.size).isEqualTo(2)
        assertThat(changes[oldList[1]]).isEqualTo(REMOVAL)
        assertThat(changes[newList[1]]).isEqualTo(ADDITION)
    }

    @Test
    fun additionOfMethodShouldResultAddition() {
        val oldJson = """
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
                      "public",
                      "abstract"
                    ],
                    "name": "provideObservers",
                    "returnType": "com.aol.mobile.sdk.player.PlayerStateObserver[]",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "videoProviderResponse"
                      }
                    ]
                  }
                ]
              }
            ]
            """
        val oldList = oldJson.asTypeDescriptorList().map { it.toUdList() }.flatten()

        val newJson = """
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
                      "public",
                      "abstract"
                    ],
                    "name": "provideObservers",
                    "returnType": "com.aol.mobile.sdk.player.PlayerStateObserver[]",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "videoProviderResponse"
                      }
                    ]
                  },
                  {
                    "modifiers": [
                      "public",
                      "abstract"
                    ],
                    "name": "NEW_METHOD",
                    "returnType": "com.aol.mobile.sdk.player.PlayerStateObserver[]",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "videoProviderResponse"
                      }
                    ]
                  }
                ]
              }
            ]
            """
        val newList = newJson.asTypeDescriptorList().map { it.toUdList() }.flatten()
        val changes = UD.getChanges(oldList, newList)

        assertThat(changes.size).isEqualTo(1)
        assertThat(changes[newList[2]]).isEqualTo(ADDITION)
    }

    @Test
    fun removalOfMethodShouldResultRemoval() {
        val oldJson = """
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
                      "public",
                      "abstract"
                    ],
                    "name": "provideObservers",
                    "returnType": "com.aol.mobile.sdk.player.PlayerStateObserver[]",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "videoProviderResponse"
                      }
                    ]
                  },
                  {
                    "modifiers": [
                      "public",
                      "abstract"
                    ],
                    "name": "METHOD_TO_BE_REMOVED",
                    "returnType": "com.aol.mobile.sdk.player.PlayerStateObserver[]",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "videoProviderResponse"
                      }
                    ]
                  }
                ]
              }
            ]
            """
        val oldList = oldJson.asTypeDescriptorList().map { it.toUdList() }.flatten()

        val newJson = """
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
                      "public",
                      "abstract"
                    ],
                    "name": "provideObservers",
                    "returnType": "com.aol.mobile.sdk.player.PlayerStateObserver[]",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "videoProviderResponse"
                      }
                    ]
                  }
                ]
              }
            ]
            """
        val newList = newJson.asTypeDescriptorList().map { it.toUdList() }.flatten()
        val changes = UD.getChanges(oldList, newList)

        assertThat(changes.size).isEqualTo(1)
        assertThat(changes[oldList[2]]).isEqualTo(REMOVAL)
    }

    @Test
    fun modificationOfMethodShouldResultAdditionAndRemoval() {
        val oldJson = """
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
                      "public",
                      "abstract"
                    ],
                    "name": "provideObservers",
                    "returnType": "com.aol.mobile.sdk.player.PlayerStateObserver[]",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "videoProviderResponse"
                      }
                    ]
                  }
                ]
              }
            ]
            """
        val oldList = oldJson.asTypeDescriptorList().map { it.toUdList() }.flatten()

        val newJson = """
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
                      "NEW_MODIFIER",
                      "public",
                      "abstract"
                    ],
                    "name": "provideObservers",
                    "returnType": "com.aol.mobile.sdk.player.PlayerStateObserver[]",
                    "params": [
                      {
                        "modifiers": [],
                        "name": "videoProviderResponse"
                      }
                    ]
                  }
                ]
              }
            ]
            """
        val newList = newJson.asTypeDescriptorList().map { it.toUdList() }.flatten()
        val changes = UD.getChanges(oldList, newList)

        assertThat(changes.size).isEqualTo(2)
        assertThat(changes[oldList[1]]).isEqualTo(REMOVAL)
        assertThat(changes[newList[1]]).isEqualTo(ADDITION)
    }
}
