package me.sqlerrorthing.ast

data class Named(
    val obfuscated: String? = null,
    val mojang: String? = null,
    val intermediary: String? = null,
    val yarn: String? = null,
    val searge: String? = null,
) {

    val original: String get()  {
        val parts = normalName.split("/")
        when (parts.size) {
            1 -> return normalName
            else -> return parts[parts.size - 1]
        }
    }

    val normalName: String get() {
        return when {
            !yarn.isNullOrBlank() -> yarn
            !mojang.isNullOrBlank() -> mojang
            !searge.isNullOrBlank() -> searge
            else -> intermediary!!
        }
    }

    val dottedNormalName: String get() = normalName.replace("/", ".")

    val underscoredNormalName: String get() = normalName.replace("/", "_")

}
