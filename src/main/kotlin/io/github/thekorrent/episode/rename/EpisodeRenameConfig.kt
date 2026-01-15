package io.github.thekorrent.episode.rename

import io.github.thekorrent.episode.rename.model.From
import moe.shizuki.korrent.plugin.annotation.KorrentConfig
import moe.shizuki.korrent.plugin.config.PluginConfig

@KorrentConfig
class EpisodeRenameConfig(
    val rename: Rename = Rename()
): PluginConfig() {
    class Rename(
        val from: From = From.SAVEPATH,
        val categories: List<String> = listOf(),
        val tags: List<String> = listOf(),
        val regex: String = "(?<=[ \\-vV第eE]|Ep|EP|vol|Vol|【|\\[)\\d+(\\.5)?(?=[ \\].话話集完]|$)",
        val padding: Int = 2,
        val displayNamePattern: String = "{series} S{season.padding}E{episode.padding}",
        val fileNamePattern: String = "{series} S{season.padding}E{episode.padding}.{extension}"
    )
}
