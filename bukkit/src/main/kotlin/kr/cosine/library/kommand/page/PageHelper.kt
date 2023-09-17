package kr.cosine.library.kommand.page

import kr.cosine.library.config.YamlConfiguration
import kr.cosine.library.extension.applyColor

class PageHelper(
    private val config: YamlConfiguration
) {

    private val pageElementMap = mutableMapOf<PageType, PageElement>()

    fun load(root: String) {
        config.getSection("command.$root.help")?.apply {
            getKeys().forEach { pageTypeText ->
                getSection(pageTypeText)?.apply {
                    val pageType = PageType.getPageType(pageTypeText.removeSuffix("-page")) ?: return@forEach
                    val display = getString("display").applyColor()
                    val showText = getString("show-text").applyColor()
                    val pageElement = PageElement(display, showText)
                    pageElementMap[pageType] = pageElement
                }
            }
        }
    }

    fun getPageElement(pageType: PageType): PageElement? = pageElementMap[pageType]
}
