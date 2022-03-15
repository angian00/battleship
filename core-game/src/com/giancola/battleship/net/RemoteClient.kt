package com.giancola.battleship.net

import com.badlogic.gdx.Gdx
import com.giancola.battleship.gamelogic.*
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory


class RemoteClient(private val hostname: String, private val port: Int, var localListener: GameLogicListener) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val client = HttpClient {
        install(WebSockets)
    }

    private var outgoing: SendChannel<Frame>? = null

    companion object {
        val jsonFormatter = Json { allowStructuredMapKeys = true }
    }

    init {
        GlobalScope.launch {
            try {
                client.webSocket(method = HttpMethod.Get, host = this@RemoteClient.hostname, port = this@RemoteClient.port, path = "/play") {
                    this@RemoteClient.outgoing = outgoing
                    while (true) {
                        val frame = incoming.receive() as? Frame.Text ?: continue
                        val notification = Json.decodeFromString(RemoteNotification.serializer(), frame.readText())
                        onNotification(notification)
                    }
                }
            } catch (e: Exception) {
                localListener.onError(e.message)
            }
        }
    }

    fun dispose() {
        client.close()
    }



    fun sendLogin() {
        sendCommand(CommandLogin())
    }

    fun sendSetPlacement(playerData: PlayerData) {
        sendCommand(CommandPlaceShips(playerData))
    }


    fun sendShoot(gridX: Int, gridY: Int) {
        sendCommand(CommandShoot(gridX, gridY))
    }

    private fun sendCommand(cmd: RemoteCommand) {
        GlobalScope.launch {
            //wait for client to init
            while (outgoing == null) {
                runBlocking {
                    delay(500)
                }
            }

            logger.debug("Sending command: $cmd")
            val frameText = jsonFormatter.encodeToString(RemoteCommand.serializer(), cmd)
            logger.debug("encoded text: $frameText")

            outgoing!!.send(Frame.Text(frameText))
        }
    }

    private fun onNotification(notif: RemoteNotification) {
        logger.debug("Notification received: $notif")

        Gdx.app.postRunnable {
            when (notif) {
                is NotificationGameStarting -> localListener?.onGameStarting(notif.playerId)
                is NotificationGameStarted -> localListener?.onGameStarted()
                is NotificationCombatStarted -> localListener?.onCombatStarted(notif.playerTurn, notif.playerNames)
                is NotificationGameFinished -> localListener?.onGameFinished(notif.winner)
                is NotificationShotPerformed -> localListener?.onShot(notif.shooter, notif.gridX, notif.gridY, notif.shotResult)
                else -> { /* ignore */ }
            }
        }
    }
}
