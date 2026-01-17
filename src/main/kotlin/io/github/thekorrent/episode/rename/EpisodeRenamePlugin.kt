package io.github.thekorrent.episode.rename

import moe.shizuki.korrent.defaultNullPluginConfigManager
import moe.shizuki.korrent.defaultNullPluginDataManager
import moe.shizuki.korrent.objectMapper
import moe.shizuki.korrent.plugin.KorrentPlugin
import org.pf4j.PluginWrapper
import java.io.File

class EpisodeRenamePlugin(wrapper: PluginWrapper) : KorrentPlugin(wrapper) {
    companion object {
        val pluginDataManager get() = _pluginDataManager
        val config: EpisodeRenameConfig by lazy { _pluginConfigManager.load() }

        private var _pluginConfigManager = defaultNullPluginConfigManager
        private var _pluginDataManager = defaultNullPluginDataManager
    }

    init {
        _pluginConfigManager = this.pluginConfigManager
        _pluginDataManager = this.pluginDataManager
    }

    override fun start() {
        val offsets = File(pluginDataManager.pluginDataFolder, "offsets.json")

        if(!offsets.exists()) {
            objectMapper.writeValue(offsets, mapOf("category" to 0))
        }

        super.start()
    }
}
