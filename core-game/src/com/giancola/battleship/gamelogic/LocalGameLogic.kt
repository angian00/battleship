package com.giancola.battleship.gamelogic

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils.random


class LocalGameLogic: GameLogic() {
    var playerListeners: MutableMap<PlayerId, GameLogicListener> = mutableMapOf()

    init {
        AIPlayer(this)
    }

    fun registerListener(listener: GameLogicListener): PlayerId? {
        val playerId = nextPlayerId() ?: return null
        playerListeners[playerId] = listener

        if (playerListeners.values.size == 2) {
            startGame()
        }

        return playerId
    }

    //------------ notify functions -----------

    override fun notifyGameStarted() {
        Thread {
            randomSleep()
            Gdx.app.postRunnable {
                for (listener in playerListeners.values)
                    listener.onGameStarted()
            }

        }.start()
    }

    override fun notifyCombatStarted(playerTurn: PlayerId, playerNames: Map<PlayerId, String>) {
        Thread {
            randomSleep()
            Gdx.app.postRunnable {
                for (listener in playerListeners.values)
                    listener.onCombatStarted(playerTurn, playerNames)
            }

        }.start()
    }

    override fun notifyGameFinished(winner: PlayerId) {
        Thread {
            //double sleep to prevent race condition with sendEnemyShot
            randomSleep()
            randomSleep()

            Gdx.app.postRunnable {
                for (listener in playerListeners.values)
                    listener.onGameFinished(winner)
            }

        }.start()
    }

    override fun notifyShot(shooter: PlayerId, gridX: Int, gridY: Int, shotResult: ShotResult?) {
        Thread {
            randomSleep()
            Gdx.app.postRunnable {
                for (listener in playerListeners.values)
                    listener.onShot(shooter, gridX, gridY, shotResult)
            }

        }.start()
    }

    private fun randomSleep() {
        val sleepInterval = random(200, 500).toLong()
        Thread.sleep(sleepInterval)
    }
}

interface GameLogicListener {
    fun onGameStarting(playerId: PlayerId)
    fun onGameStarted()
    fun onCombatStarted(whoseTurn: PlayerId, playerNames: Map<PlayerId, String>)
    fun onShot(shooter: PlayerId, gridX: Int, gridY: Int, shotResult: ShotResult?)
    fun onGameFinished(winner: PlayerId)

    fun onError(error: String?)
}