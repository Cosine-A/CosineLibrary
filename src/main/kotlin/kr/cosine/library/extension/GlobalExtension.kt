package kr.cosine.library.extension

inline fun <reified T> T.toArray(): Array<T> {
    return arrayOf(this)
}