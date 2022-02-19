package com.giancola.battleship.gamelogic

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils.random
import com.giancola.battleship.PlayerData
import com.giancola.battleship.ShotResult

enum class PlayerId {
    Player1,
    Player2;

    val otherPlayer
        get() = when (this) { Player1 -> Player2; Player2 -> Player1 }
}

interface GameLogic {
    var gameStarted: Boolean
    var playerTurn: PlayerId


    fun registerListener(listener: GameLogicListener, playerData: PlayerData): PlayerId?
    fun shoot(playerId: PlayerId, gridX: Int, gridY: Int): ShotResult?
}


interface GameLogicListener {
    fun onGameStarted(whoseTurn: PlayerId)
    fun onEnemyShot(gridX: Int, gridY: Int, shotResult: ShotResult)
    fun onGameFinished(winner: PlayerId)
}


class LocalGameLogic: GameLogic {
    override var gameStarted: Boolean = false
    override var playerTurn: PlayerId = PlayerId.Player1

    private var playerListeners: MutableMap<PlayerId, GameLogicListener> = mutableMapOf()
    private var playersData: MutableMap<PlayerId, PlayerData> = mutableMapOf()

    init {
        AIPlayer(this)
    }



    override fun registerListener(listener: GameLogicListener, playerData: PlayerData): PlayerId? {
        for (playerId in PlayerId.values()) {
            if (playerId !in playerListeners) {
                playerListeners[playerId] = listener
                this.playersData[playerId] = playerData

                if (PlayerId.Player1 in playerListeners && PlayerId.Player2 in playerListeners) {
                    startGame()
                }

                return playerId
            }
        }

        return null
    }


    override fun shoot(playerId: PlayerId, gridX: Int, gridY: Int): ShotResult? {
        if (!gameStarted)
            return null

        if (playerTurn != playerId)
            return null

        val currPlayerData = playersData[playerId]!!
        val enemyData  = playersData[playerId.otherPlayer]!!

        if (currPlayerData.shots[gridX][gridY])
            return null

        currPlayerData.shots[gridX][gridY] = true

        playerTurn = playerId.otherPlayer


        val shotResult = enemyData.checkEnemyShot(gridX, gridY, currPlayerData.shots)
        sendEnemyShot(playerTurn, gridX, gridY, shotResult)

        return shotResult
    }

    private fun startGame() {
        playerTurn = if (random(2) == 1) PlayerId.Player1 else PlayerId.Player2
        gameStarted = true

        sendStartGame()
    }

    //------------ async functions -----------

    private fun sendStartGame() {
        Thread {
            randomSleep()
            Gdx.app.postRunnable {
                for (listener in playerListeners.values)
                    listener.onGameStarted(playerTurn)
            }

        }.start()
    }

    private fun sendEnemyShot(playerId: PlayerId, gridX: Int, gridY: Int, shotResult: ShotResult) {
        Thread {
            randomSleep()
            Gdx.app.postRunnable {
                playerListeners[playerId]!!.onEnemyShot(gridX, gridY, shotResult)
            }

        }.start()
    }

    private fun randomSleep() {
        val sleepInterval = random(1000, 3000).toLong()
        Thread.sleep(sleepInterval)
    }
}