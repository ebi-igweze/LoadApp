package com.udacity

enum class DownloadOption(val url: String) {
    GLIDE( "https://github.com/bumptech/glide"),
    ADVANCED_ANDROID("https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"),
    RETROFIT("https://github.com/square/retrofit");

    val simpleName: String get() {
        return when (this) {
            GLIDE -> "Glide"
            RETROFIT -> "Retrofit"
            ADVANCED_ANDROID -> "Advanced Android"
        }
    }
}

fun Int.asDownloadOption(): DownloadOption = when(this) {
    DownloadOption.GLIDE.ordinal -> DownloadOption.GLIDE
    DownloadOption.RETROFIT.ordinal -> DownloadOption.RETROFIT
    DownloadOption.ADVANCED_ANDROID.ordinal -> DownloadOption.ADVANCED_ANDROID
    else -> throw IllegalArgumentException("Provided Id is not a valid option")
}