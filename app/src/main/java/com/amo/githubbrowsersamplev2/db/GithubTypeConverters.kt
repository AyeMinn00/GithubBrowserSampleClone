package com.amo.githubbrowsersamplev2.db

import androidx.room.TypeConverter
import timber.log.Timber
import java.lang.NumberFormatException

object GithubTypeConverters {

    @TypeConverter
    @JvmStatic
    fun stringToIntList(data: String?): List<Int>? {
        return data?.let {
            it.split(",").map { it2 ->
                try {
                    it2.toInt()
                } catch (e: NumberFormatException) {
                    Timber.e(e, "Cannot convert $it2 to number")
                    null
                }
            }
        }?.filterNotNull()
    }

    @TypeConverter
    @JvmStatic
    fun intListToString(ints: List<Int>?): String? {
        return ints?.joinToString(",")
    }

}