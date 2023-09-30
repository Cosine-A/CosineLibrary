package kr.cosine.library.kommand.page

enum class PageType {
    BEFORE,
    CURRENT,
    NEXT;

    companion object {
        fun getPageType(pageTypeText: String): PageType? {
            return PageType.values().find { it.name == pageTypeText.uppercase() }
        }
    }
}