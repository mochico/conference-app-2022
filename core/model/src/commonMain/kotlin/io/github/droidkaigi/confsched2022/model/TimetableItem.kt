@file:UseSerializers(
    PersistentListSerializer::class,
)

package io.github.droidkaigi.confsched2022.model

import io.github.droidkaigi.confsched2022.model.TimetableItem.Session
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
public sealed class TimetableItem {
    public abstract val id: TimetableItemId
    public abstract val title: MultiLangText
    public abstract val startsAt: Instant
    public abstract val endsAt: Instant
    public abstract val category: TimetableCategory
    public abstract val room: TimetableRoom
    public abstract val targetAudience: String
    public abstract val language: TimetableLanguage
    public abstract val asset: TimetableAsset
    public abstract val levels: PersistentList<String>
    public val day: DroidKaigi2022Day? get() = DroidKaigi2022Day.ofOrNull(startsAt)

    @Serializable
    public data class Session(
        override val id: TimetableItemId,
        override val title: MultiLangText,
        override val startsAt: Instant,
        override val endsAt: Instant,
        override val category: TimetableCategory,
        override val room: TimetableRoom,
        override val targetAudience: String,
        override val language: TimetableLanguage,
        override val asset: TimetableAsset,
        override val levels: PersistentList<String>,
        val description: String,
        val speakers: PersistentList<TimetableSpeaker>,
        val message: MultiLangText?,
    ) : TimetableItem() {
        public companion object
    }

    @Serializable
    public data class Special(
        override val id: TimetableItemId,
        override val title: MultiLangText,
        override val startsAt: Instant,
        override val endsAt: Instant,
        override val category: TimetableCategory,
        override val room: TimetableRoom,
        override val targetAudience: String,
        override val language: TimetableLanguage,
        override val asset: TimetableAsset,
        override val levels: PersistentList<String>,
        val speakers: PersistentList<TimetableSpeaker> = persistentListOf(),
    ) : TimetableItem()

    public val startsTimeString: String by lazy {
        val localDate = startsAt.toLocalDateTime(TimeZone.currentSystemDefault())
        "${localDate.hour}".padStart(2, '0') + ":" + "${localDate.minute}".padStart(2, '0')
    }

    public val minutesString: String by lazy {
        val minutes = (endsAt - startsAt)
            .toComponents { minutes, _, _ -> minutes }
        "${minutes}min"
    }
}

public fun TimetableItem.Session.Companion.fake(): Session {
    return Session(
        id = TimetableItemId("2"),
        title = MultiLangText("DroidKaigiのアプリのアーキテクチャ", "DroidKaigi App Architecture"),
        startsAt = LocalDateTime.parse("2021-10-20T10:30:00")
            .toInstant(TimeZone.of("UTC+9")),
        endsAt = LocalDateTime.parse("2021-10-20T10:50:00")
            .toInstant(TimeZone.of("UTC+9")),
        category = TimetableCategory(
            id = 28654,
            title = MultiLangText(
                "Android FrameworkとJetpack",
                "Android Framework and Jetpack",
            ),
        ),
        room = TimetableRoom(
            id = 2,
            name = MultiLangText("Room1", "Room2"),
            sort = 1
        ),
        targetAudience = "For App developer アプリ開発者向け",
        language = TimetableLanguage(
            langOfSpeaker = "JAPANESE",
            isInterpretationTarget = true,
        ),
        asset = TimetableAsset(
            videoUrl = "https://www.youtube.com/watch?v=hFdKCyJ-Z9A",
            slideUrl = "https://droidkaigi.jp/2021/",
        ),
        speakers = listOf(
            TimetableSpeaker(
                id = "1",
                name = "taka",
                iconUrl = "https://github.com/takahirom.png",
                bio = "Likes Android",
                tagLine = "Android Engineer"
            ),
            TimetableSpeaker(
                id = "2",
                name = "ry",
                iconUrl = "https://github.com/ry-itto.png",
                bio = "Likes iOS",
                tagLine = "iOS Engineer",
            ),
        ).toPersistentList(),
        description = "これはディスクリプションです。\nこれはディスクリプションです。\nこれはディスクリプションです。\nこれはディスクリプションです。",
        message = MultiLangText(
            jaTitle = "このセッションは事情により中止となりました",
            enTitle = "This session has been cancelled due to circumstances."
        ),
        levels = listOf(
            "INTERMEDIATE",
        ).toPersistentList(),
    )
}
