package io.github.thekorrent.episode.rename

import moe.shizuki.korrent.defaultNullPluginConfigManager
import moe.shizuki.korrent.plugin.KorrentPlugin
import org.pf4j.PluginWrapper

class EpisodeRenamePlugin(wrapper: PluginWrapper) : KorrentPlugin(wrapper) {
    companion object {
        val config: EpisodeRenameConfig by lazy { _pluginConfigManager.load() }

        private var _pluginConfigManager = defaultNullPluginConfigManager
    }

    init {
        _pluginConfigManager = this.pluginConfigManager
    }
}
