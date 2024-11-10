package me.sqlerrorthing.ast

enum class Access {
    DEFAULT,
    PUBLIC,
    PROTECTED,
    PRIVATE
}

enum class Flags {
    STATIC,
    FINAL
}

interface IAST