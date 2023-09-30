package kr.cosine.library.kommand.page

import kr.cosine.library.extension.applyColor
import org.bukkit.configuration.file.FileConfiguration

class PageHelper(
    private val config: FileConfiguration
) {

    private val pageElementMap = mutableMapOf<PageType, PageElement>()

    fun load() {
        config.getConfigurationSection("page")?.apply {
            getKeys(false).forEach { pageTypeText ->
                getConfigurationSection(pageTypeText)?.apply {
                    val pageType = PageType.getPageType(pageTypeText) ?: return@forEach
                    val display = getString("display")?.applyColor() ?: return@forEach
                    val showText = getString("show-text")?.applyColor() ?: return@forEach
                    val pageElement = PageElement(display, showText)
                    pageElementMap[pageType] = pageElement
                }
            }
        }
    }

    fun getPageElement(pageType: PageType): PageElement? = pageElementMap[pageType]
}
