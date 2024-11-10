package me.sqlerrorthing.utils

object AccessFlags {
    private const val ACC_PUBLIC = 0x0001 // public
    private const val ACC_PRIVATE = 0x0002 // private
    private const val ACC_PROTECTED = 0x0004 // protected
    private const val ACC_STATIC = 0x0008 // static
    private const val ACC_FINAL = 0x0010 // final

    fun isPublic(flag: Int): Boolean = (flag and ACC_PUBLIC) != 0

    fun isPrivate(flag: Int): Boolean = (flag and ACC_PRIVATE) != 0

    fun isProtected(flag: Int): Boolean = (flag and ACC_PROTECTED) != 0

    fun isPackagePrivate(flag: Int): Boolean = (flag and (ACC_PUBLIC or ACC_PRIVATE or ACC_PROTECTED)) == 0

    fun isStatic(flag: Int): Boolean = (flag and ACC_STATIC) != 0

    fun isFinal(flag: Int): Boolean = (flag and ACC_FINAL) != 0
}
