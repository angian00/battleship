package com.giancola.battleship.gamelogic

enum class PlayerId {
    Player1,
    Player2;

    val otherPlayer
        get() = when (this) { Player1 -> Player2; Player2 -> Player1 }
}


abstract class GameLogic {
    protected var gameRunning: Boolean = false
    protected var playerTurn: PlayerId = PlayerId.Player1

    protected var playersData: MutableMap<PlayerId, PlayerData> = mutableMapOf()

    protected abstract fun notifyGameStarted()
    protected abstract fun notifyCombatStarted(playerTurn: PlayerId)
    protected abstract fun notifyGameFinished(winner: PlayerId)
    protected abstract fun notifyShot(playerId: PlayerId, gridX: Int, gridY: Int, shotResult: ShotResult?)

    private var nextPlayerId: PlayerId? = PlayerId.Player1

    fun nextPlayerId(): PlayerId? {
        val res = nextPlayerId

        nextPlayerId = when (nextPlayerId) {
            PlayerId.Player1 -> PlayerId.Player2
            else -> null
        }

        return res
    }

    fun setPlacement(playerId: PlayerId, playerData: PlayerData) {
        this.playersData[playerId] = playerData
        if (this.playersData.values.size == 2)
            startCombat()
    }


    fun shoot(shooter: PlayerId, gridX: Int, gridY: Int) {
        if (!gameRunning || playerTurn != shooter) {
            notifyShot(shooter, gridX, gridY, null)

        } else {
            val currPlayerData = playersData[shooter]!!
            val enemyData = playersData[shooter.otherPlayer]!!

            if (currPlayerData.shots[gridX][gridY]) {
                notifyShot(shooter, gridX, gridY, null)
            } else {
                currPlayerData.shots[gridX][gridY] = true

                playerTurn = shooter.otherPlayer

                val shotResult = enemyData.checkEnemyShot(gridX, gridY, currPlayerData.shots)
                notifyShot(shooter, gridX, gridY, shotResult)

                if (enemyData.areAllShipsSunk(currPlayerData.shots)) {
                    gameRunning = false
                    notifyGameFinished(playerTurn.otherPlayer)
                }
            }
        }


    }

    fun startGame() {
        notifyGameStarted()
    }

    fun startCombat() {
        playerTurn = PlayerId.values().random()
        gameRunning = true

        notifyCombatStarted(playerTurn)
    }
}
