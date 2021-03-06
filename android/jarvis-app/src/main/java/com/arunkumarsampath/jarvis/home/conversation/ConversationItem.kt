/*
 * Copyright 2018 Arunkumar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.arunkumarsampath.jarvis.home.conversation

import android.support.v7.util.DiffUtil
import com.arunkumarsampath.jarvis.util.common.firebase.KeyProvider
import java.util.*


data class ConversationItem(
        var key: String = "",
        var who: String = "",
        var content: String = "",
        var timestamp: Long = 0L
) : KeyProvider {

    override fun key() = key

    companion object {
        const val JARVIS = "jarvis"
        const val USER = "user"

        fun generateMockItems(howMuch: Int = 100): List<ConversationItem> {
            val chatItems = ArrayList<ConversationItem>()
            val random = Random()
            for (i in 1..howMuch) {
                chatItems.add(ConversationItem(
                        random.nextInt().toString(),
                        if (random.nextBoolean()) JARVIS else USER,
                        random.nextInt().toString(),
                        random.nextLong()
                ))
            }
            return chatItems
        }

        class ConversationDiffCallback : DiffUtil.ItemCallback<ConversationItem>() {

            override fun areItemsTheSame(
                    oldItem: ConversationItem?,
                    newItem: ConversationItem?
            ): Boolean = oldItem?.key == newItem?.key

            override fun areContentsTheSame(
                    oldItem: ConversationItem?,
                    newItem: ConversationItem?
            ): Boolean = oldItem?.equals(newItem)!!
        }
    }
}