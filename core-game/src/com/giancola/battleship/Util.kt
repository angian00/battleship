package com.giancola.battleship

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.giancola.battleship.net.RemoteCommand
import com.giancola.battleship.net.RemoteNotification
import io.ktor.http.cio.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.random.Random

val rnd = Random.Default

fun randomColor(): Color = Color(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat(), 1.0f)

fun coords2str(gridX: Int, gridY: Int): String = "${(gridX + 'A'.toInt()).toChar()}${gridY+1}"


@Serializable
class JsonData(val data: Map<String, String>) {
    companion object {
        val jsonFormatter = Json { allowStructuredMapKeys = true }

        fun load(filename: String): JsonData? {
            val file = Gdx.files.local(filename)

            if (!file.exists())
                return null

            val dataStr = file.readString()
            return jsonFormatter.decodeFromString(JsonData.serializer(), dataStr)
        }
    }

    fun save(filename: String) {
        val file = Gdx.files.local(filename)

        val dataStr = jsonFormatter.encodeToString(JsonData.serializer(), this)
        file.writeString(dataStr, false)
    }
}