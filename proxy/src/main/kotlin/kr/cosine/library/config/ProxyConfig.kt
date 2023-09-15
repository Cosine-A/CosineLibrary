package kr.cosine.library.config

import net.md_5.bungee.api.plugin.Plugin
import java.io.File

class ProxyConfig(
    plugin: Plugin
) {

    private var file = File(plugin.dataFolder, "config.yml")
    private var config = YamlConfiguration.loadConfiguration(file)

    var isSocketEnabled = false
        private set

    var port = 14915
        private set

    fun load() {
        isSocketEnabled = config.getBoolean("socket.enable")
        port = config.getInt("socket.port")
    }
}