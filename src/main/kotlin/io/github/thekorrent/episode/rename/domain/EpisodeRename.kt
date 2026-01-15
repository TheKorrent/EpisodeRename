package io.github.thekorrent.episode.rename.domain

import com.google.common.eventbus.Subscribe
import io.github.thekorrent.episode.rename.EpisodeRenamePlugin.Companion.config
import io.github.thekorrent.episode.rename.model.From.CATEGORY
import io.github.thekorrent.episode.rename.model.From.SAVEPATH
import moe.shizuki.korrent.bittorrent.event.QBittorrentTorrentDownloadedEvent
import moe.shizuki.korrent.plugin.annotation.KorrentEvent
import java.io.File

@KorrentEvent
class EpisodeRename {
    @Subscribe
    fun rename(event: QBittorrentTorrentDownloadedEvent) {
        val client = event.client
        val hash = event.torrent

        val torrent = client.getTorrents(hashes = hash).execute().body()?.first() ?: return
        val category = torrent.category ?: return
        val tags = torrent.tags?.split(",")?.map { it.trim() } ?: return
        val savePath = torrent.savePath ?: return

        val files = client.getTorrentContents(hash).execute().body() ?: return
        val file = files.firstOrNull() ?: return

        if (files.size != 1) return

        if (category.isNotEmpty() && config.rename.categories.none { category.startsWith(it) }) return

        if (config.rename.tags.isNotEmpty() && config.rename.tags.intersect(tags.toSet()).isEmpty()) return

        val extension = file.name.split(".").last()

        val series = when (config.rename.from) {
            CATEGORY -> parseSeriesFromCategory(category.split("/"))
            SAVEPATH -> parseSeriesFromSavePath(savePath)
        } ?: return

        val season = when (config.rename.from) {
            CATEGORY -> parseSeasonFromCategory(category.split("/"))
            SAVEPATH -> parseSeasonFromSavePath(savePath)
        } ?: return

        val episode = parseEpisode(file.name) ?: return

        val episodePart = episode.split(".")

        if (episodePart.size > 2) return

        val paddedSeason = season.toString().padStart(config.rename.padding, '0')

        val paddedEpisode = if (episodePart.size > 1) {
            "${episodePart.first().padStart(config.rename.padding, '0')}.${episodePart.last()}"
        } else {
            episodePart.first().padStart(config.rename.padding, '0')
        }

        val renamedDisplayName = config.rename.displayNamePattern
            .replace("{series}", series)
            .replace("{season}", season.toString())
            .replace("{episode}", episode)
            .replace("{season.padding}", paddedSeason)
            .replace("{episode.padding}", paddedEpisode)

        val renamedFileName = config.rename.fileNamePattern
            .replace("{series}", series)
            .replace("{season}", season.toString())
            .replace("{episode}", episode)
            .replace("{season.padding}", paddedSeason)
            .replace("{episode.padding}", paddedEpisode)
            .replace("{extension}", extension)

        client.renameTorrent(hash, renamedDisplayName).execute()
        client.renameTorrentFile(hash, file.name, renamedFileName).execute()
    }

    private fun parseEpisode(name: String): String? {
        val regex = config.rename.regex.toRegex()

        return regex.find(name)?.value
    }

    private fun parseSeriesFromCategory(category: List<String>): String? {
        if (category.size <= 1) return null

        return category[category.size - 2]
    }

    private fun parseSeriesFromSavePath(path: String): String {
        val file = File(path)

        val series = file.parentFile

        return series.name
    }

    private fun parseSeason(season: String): Int? {
        val regex = "\\d+".toRegex()
        val season = regex.findAll(season).map { it.value }.toList()

        if (season.size != 1) return null

        return season.first().toInt()
    }

    private fun parseSeasonFromCategory(category: List<String>): Int? {
        if (category.size <= 1) return null

        return parseSeason(category[category.size - 1])
    }

    private fun parseSeasonFromSavePath(path: String): Int? {
        val file = File(path)

        return parseSeason(file.name)
    }
}
