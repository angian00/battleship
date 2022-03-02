package com.giancola.battleship.net.server

import com.giancola.battleship.gamelogic.GameLogic
import com.giancola.battleship.gamelogic.ServerGameLogic
import com.giancola.battleship.net.*
import org.slf4j.LoggerFactory
import java.util.*


class GameMaster {
    private val playerConns: MutableMap<String, ClientConnection> = Collections.synchronizedMap<String, ClientConnection>(mutableMapOf())
    private val activeGames: MutableSet<GameLogic> = Collections.synchronizedSet<GameLogic>(HashSet())

    private val logger = LoggerFactory.getLogger(javaClass)

    fun executeCommand(userConn: ClientConnection, cmd: RemoteCommand) {
        logger.info("Command received from ${userConn.id}: $cmd")

        when (cmd) {
            is CommandLogin -> login(userConn)

            is CommandPlaceShips -> {
                val game = playerConns[userConn.id]?.currGame
                game?.setPlacement(userConn.id, cmd.playerData)
            }

            is CommandShoot -> {
                val game = playerConns[userConn.id]?.currGame
                game?.shoot(userConn.id, cmd.gridX, cmd.gridY)
            }
        }
    }

    fun removeConn(playerConn: ClientConnection) {
        activeGames.remove(playerConn.currGame)
        playerConn.currGame = null
        playerConns.remove(playerConn.id)
        //FIXME: set to null activeGame for other player
    }

    private fun login(playerConn: ClientConnection) {
        var otherPlayerConn: ClientConnection?

        playerConns[playerConn.id] = playerConn
        if (playerConns.size > 1) {
            //choose an opponent
            do {
                otherPlayerConn = playerConns.values.random()
            } while (playerConn == otherPlayerConn || otherPlayerConn == null || otherPlayerConn.currGame != null)

            //val newGame = NetworkGameLogic(playerConn, otherPlayerConn)
            val newGame = ServerGameLogic()
            playerConn.currGame = newGame
            otherPlayerConn.currGame = newGame
            activeGames += newGame

            newGame.registerConnection(playerConn)
            newGame.registerConnection(otherPlayerConn)
        }
    }

}

