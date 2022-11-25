package ru.netology.dto

import android.widget.ImageView

data class Post(
    val id: Int,
    val author: String,
    val published: String,
    val content: String,
    val likesCount: Int,
    val sharesCount: Int,
    val viewsCount: Int,
    val likedByMe: Boolean
) {
    fun setCount(count: Int): String {
        var stringCount = count.toString()
        var countArray = stringCount.toCharArray()
        when {
            count in 0..999 -> return "$count"
            count in 1000..9999 -> {
                return if (countArray[1].equals('0')) "${countArray[0]}K" else "${countArray[0]}.${countArray[1]}K"
            }
            count in 10000..99_999 -> return "${countArray[0]}${countArray[1]}K"
            count in 100_000..999_999 -> return "${countArray[0]}${countArray[1]}${countArray[2]}K"
            count in 1_000_000..9_000_000 -> {
                return if (countArray[1].equals('0')) "${countArray[0]}M" else "${countArray[0]}.${countArray[1]}M"
            }
            count in 10_000_000..99_000_000 -> {
                return if (countArray[2].equals('0')) "${countArray[0]}${countArray[1]}M" else "${countArray[0]}${countArray[1]}.${countArray[2]}M"
            }
            else -> return "${countArray[0]}${countArray[1]}${countArray[2]}.${countArray[3]}M"
        }
        return "error"
    }
}