package com.giancola.battleship.gamelogic

import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.MathUtils.random
import com.giancola.battleship.PlayerData
import com.giancola.battleship.ShipFactory
import com.giancola.battleship.ShipPlacement
import com.giancola.battleship.ShotResult

class AIPlayer(private val game: GameLogic): GameLogicListener {
    private val playerData = makeRandomData()
    private val playerId: PlayerId
    private val nRows = playerData.shots.size
    private val nCols = playerData.shots[0].size

    init {
        val regPlayerId = game.registerListener(this, playerData)
        require (regPlayerId != null)
        playerId = regPlayerId
    }

    companion object {
        fun makeRandomData(): PlayerData {
            val playerData = PlayerData("Computer", ShipFactory.standardSetup)

            randomizePlacement(playerData)

            return playerData
        }

        private fun randomizePlacement(playerData: PlayerData) {
            for ((gridX, shipId) in playerData.shipPlacements.keys.withIndex()) {
                val shipLength = shipId.shipType.width
                playerData.shipPlacements[shipId] = ShipPlacement(GridPoint2(gridX, 0), GridPoint2(gridX, shipLength-1))
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

        game.shoot(playerId, gridX, gridY)

    }


    //--------------- GameLogicListener methods ---------------

    override fun onGameStarted(whoseTurn: PlayerId) {
        if (whoseTurn == playerId)
            makeRandomShot()
    }

    override fun onEnemyShot(gridX: Int, gridY: Int, shotResult: ShotResult) {
        makeRandomShot()
    }

    override fun onGameFinished(winner: PlayerId) {
        //do nothing
    }
}