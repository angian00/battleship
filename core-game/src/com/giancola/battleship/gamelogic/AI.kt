package com.giancola.battleship.gamelogic

import com.badlogic.gdx.math.MathUtils.random
import com.giancola.battleship.GameConstants.N_COLS
import com.giancola.battleship.GameConstants.N_ROWS

class AIPlayer(private val game: LocalGameLogic): GameLogicListener {
    private val playerData = makeRandomData()
    private val playerId: PlayerId
    private val nRows = playerData.shots.size
    private val nCols = playerData.shots[0].size

    init {
        val regPlayerId = game.registerListener(this)
        require (regPlayerId != null)
        playerId = regPlayerId
    }

    companion object {
        fun makeRandomData(): PlayerData {
            val playerData = PlayerData("Computer", ShipFactory.standardSetup, N_ROWS, N_COLS)

            randomizePlacement(playerData)

            return playerData
        }

        private fun randomizePlacement(playerData: PlayerData) {
            for ((gridX, shipId) in playerData.shipPlacements.keys.withIndex()) {
                val shipLength = shipId.shipType.width
                playerData.shipPlacements[shipId] = ShipPlacement(Coords(gridX, 0), Coords(gridX, shipLength-1))
            }
        }
    }


    private fun makeRandomShot() {
        var gridX: Int
        var gridY: Int

        do {
            gridX = random(nRows-1)
            gridY = random(nCols-1)
        } while (playerData.shots[gridX][gridY])

        Thread {
            randomThink()
            game.shoot(playerId, gridX, gridY)
        }.start()
    }


    //--------------- GameLogicListener methods ---------------

    override fun onGameStarting(playerId: PlayerId) {
    }

    override fun onGameStarted() {
        game.setPlacement(playerId, playerData)
    }

    override fun onCombatStarted(whoseTurn: PlayerId, playerNames: Map<PlayerId, String>) {
        if (whoseTurn == playerId)
            makeRandomShot()
    }

    override fun onShot(shooter: PlayerId, gridX: Int, gridY: Int, shotResult: ShotResult?) {
        if (shooter != playerId)
            makeRandomShot()
    }

    override fun onGameFinished(winner: PlayerId) {
        //do nothing
    }

    override fun onGameDisconnected() {
        //do nothing
    }

    override fun onError(error: String?) {
        //do nothing
    }


    private fun randomThink() {
        val sleepInterval = random(1000, 4000).toLong()
        Thread.sleep(sleepInterval)
    }
}