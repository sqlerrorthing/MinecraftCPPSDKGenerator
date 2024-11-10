package me.sqlerrorthing.ast

data class Named(
    val obfuscated: String? = null,
    val mojang: String? = null,
    val intermediary: String? = null,
    val yarn: String? = null,
    val searge: String? = null,
) {

    val original: String get()  {
        val parts = yarn?.split("/") ?: TODO("Fixme no yarn parts found")
        when (parts.size) {
            1 -> return yarn
            else -> return parts[parts.size - 1]
        }
    }

}
