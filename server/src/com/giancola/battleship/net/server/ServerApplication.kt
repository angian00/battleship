package com.giancola.battleship.net.server

import com.giancola.battleship.gamelogic.ServerGameLogic
import com.giancola.battleship.net.CommandLogin
import com.giancola.battleship.net.RemoteCommand
import com.giancola.battleship.net.RemoteNotification
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

val jsonFormatter = Json { allowStructuredMapKeys = true }


fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    val logger = LoggerFactory.getLogger(javaClass)

    install(WebSockets)
    routing {
        val connections = Collections.synchronizedSet<ClientConnection>(LinkedHashSet())
        val gameMaster = GameMaster()

        webSocket("/play") {
            val currConn = ClientConnection(this, incoming, outgoing)
            connections += currConn

            logger.info("New connection ; connId= ${currConn.id}")

            try {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            logger.debug("text frame received: ${frame.readText()}")

                            val cmd = jsonFormatter.decodeFromString(RemoteCommand.serializer(), frame.readText())
                            gameMaster.executeCommand(currConn, cmd)
                        } else -> {
                            //ignore
                        }
                    }
                }

            } catch (e: ClosedReceiveChannelException) {
                println("onClose ${closeReason.await()}")
            } catch (e: Throwable) {
                println("onError ${closeReason.await()}")
                e.printStackTrace()

            } finally {
                connections -= currConn
                gameMaster.removeConn(currConn)
            }
        }
    }

}

class ClientConnection(val session: DefaultWebSocketSession, val incoming: ReceiveChannel<Frame>, val outgoing: SendChannel<Frame>) {
    companion object {
        var lastId = AtomicInteger(0)
    }

    val id = "user${lastId.getAndIncrement()}"
    var currGame: ServerGameLogic? = null
}

