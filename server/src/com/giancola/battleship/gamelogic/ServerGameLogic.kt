package com.giancola.battleship.gamelogic

import com.giancola.battleship.net.*
import com.giancola.battleship.net.server.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ServerGameLogic(): GameLogic() {
    private var playerConnections: MutableMap<PlayerId, ClientConnection> = mutableMapOf()
    //private var connToId: Map<String, PlayerId> = mapOf(conn1.id to PlayerId.Player1, conn2.id to PlayerId.Player2)
    private var connIdToPlayerId: MutableMap<String, PlayerId> = mutableMapOf()


    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    companion object {
        val jsonFormatter = Json { allowStructuredMapKeys = true }
    }


    fun registerConnection(conn: ClientConnection): PlayerId? {
        val playerId = nextPlayerId() ?: return null
        playerConnections[playerId] = conn
        connIdToPlayerId[conn.id] = playerId

        sendNotification(playerId, NotificationGameStarting(playerId))

        if (playerConnections.values.size == 2) {
            logger.info("Starting game")
            startGame()
        }

        return playerId
    }

    fun setPlacement(connId: String, playerData: PlayerData) {
        val playerId = connIdToPlayerId[connId]

        if (playerId == null) {
            logger.error("connId not found: $connId")
        } else {
            setPlacement(playerId, playerData)
        }
    }


    fun shoot(connId: String, gridX: Int, gridY: Int) {
        val playerId = connIdToPlayerId[connId] ?: return

        if (playerId == null) {
            logger.error("connId not found: $connId")
        } else {
            shoot(playerId, gridX, gridY)
        }
    }



    override fun notifyGameStarted() {
        sendNotification(NotificationGameStarted())
    }

    override fun notifyCombatStarted(playerTurn: PlayerId) {
        sendNotification(NotificationCombatStarted(playerTurn))
    }

    override fun notifyGameFinished(winner: PlayerId) {
        sendNotification(NotificationGameFinished(winner))
    }

    override fun notifyShot(shooter: PlayerId, gridX: Int, gridY: Int, shotResult: ShotResult?) {
        sendNotification(NotificationShotPerformed(shooter, gridX, gridY, shotResult))
    }


    private fun sendNotification(playerId: PlayerId, notification: RemoteNotification) {
        val conn = playerConnections[playerId] ?: return

        logger.debug("Sending notification: $notification to $playerId")
        GlobalScope.launch { conn.session.send(Frame.Text(jsonFormatter.encodeToString(RemoteNotification.serializer(), notification))) }
    }

    private fun sendNotification(notification: RemoteNotification) {
        logger.debug("Sending notification: $notification")
        for (conn in playerConnections.values) {
            val testNotification = NotificationCombatStarted(connIdToPlayerId[conn.id]!!)
            GlobalScope.launch { conn.session.send(Frame.Text(jsonFormatter.encodeToString(RemoteNotification.serializer(), notification))) }
        }
    }

}