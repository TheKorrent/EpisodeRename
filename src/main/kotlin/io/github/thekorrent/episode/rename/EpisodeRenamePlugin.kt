package io.github.thekorrent.episode.rename

import moe.shizuki.korrent.plugin.KorrentPlugin
import moe.shizuki.korrent.plugin.config.PluginConfigManager
import org.pf4j.PluginWrapper

class EpisodeRenamePlugin(wrapper: PluginWrapper) : KorrentPlugin(wrapper) {
    companion object {
        val config get() = _config

        private var _pluginConfigManager = PluginConfigManager("null")
        private var _config = EpisodeRenameConfig()
    }

    init {
        _pluginConfigManager = this.pluginConfigManager
        _config = _pluginConfigManager.load()
    }
}
