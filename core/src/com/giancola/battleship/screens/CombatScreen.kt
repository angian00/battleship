package com.giancola.battleship.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.giancola.battleship.*
import com.giancola.battleship.GameConstants.N_COLS
import com.giancola.battleship.GameConstants.N_ROWS
import com.giancola.battleship.GameConstants.TILE_SIZE_SMALL
import com.giancola.battleship.GraphicsConstants.shipHealthyColor
import com.giancola.battleship.GraphicsConstants.shipSunkColor
import com.giancola.battleship.actors.CombatEnemyBoard
import com.giancola.battleship.actors.CombatPlayerBoard
import com.giancola.battleship.actors.ShipActor
import com.giancola.battleship.gamelogic.GameLogic
import com.giancola.battleship.gamelogic.GameLogicListener
import com.giancola.battleship.gamelogic.LocalGameLogic
import com.giancola.battleship.gamelogic.PlayerId
import ktx.app.KtxScreen


class CombatScreen(val gameApp: BattleshipGame, val playerData: PlayerData) : KtxScreen, InputAdapter(), GameLogicListener {
    private val playerShipActors: Map<Pair<ShipType, Int>, ShipActor>

    private val bkg: Image
    private val playerBoard: CombatPlayerBoard
    private val enemyBoard: CombatEnemyBoard

    val game: GameLogic = LocalGameLogic()
    val playerId: PlayerId

    init {
        gameApp.im.addProcessor(this)

        bkg = Image(Texture("combat_background.png"))
        gameApp.stg.addActor(bkg)

        playerBoard = CombatPlayerBoard(this.gameApp.stg)
        enemyBoard = CombatEnemyBoard(this, this.gameApp.stg)

        playerShipActors = initShips()

        val regPlayerId = game.registerListener(this, playerData)
        require (regPlayerId != null)
        playerId = regPlayerId

        //randomEnemyShots() //DEBUG

        bkg.toBack()
    }

    override fun render(delta: Float) {
        gameApp.stg.act()
        gameApp.stg.draw()
    }

    override fun dispose() {
        gameApp.im.removeProcessor(this)
        super.dispose()
    }


    private fun initShips(): Map<Pair<ShipType, Int>, ShipActor> {
        val mutableMap = mutableMapOf<Pair<ShipType, Int>, ShipActor>()

        val shipPlacements = playerData.shipPlacement
        for ((shipId, shipCoords) in shipPlacements) {
            if (shipCoords == null)
                continue

            val shipActor = ShipActor(shipId, TILE_SIZE_SMALL)
            shipActor.placeInGrid(shipCoords, playerBoard)

            shipActor.color = shipHealthyColor
            gameApp.stg.addActor(shipActor)

            mutableMap[shipId] = shipActor
        }

        return mutableMap.toMap()
    }

/*
    private fun randomEnemyShots() {
        val nShots = 20

        var shotCount = 0
        while (shotCount < nShots) {
            val iRow = random(N_ROWS-1)
            val iCol = random(N_COLS-1)

            if (!enemyData.shots[iRow][iCol]) {
                enemyShot(iRow, iCol)
                shotCount ++
            }
        }
    }

    fun enemyShot(gridX: Int, gridY: Int) {
        enemyData.shots[gridX][gridY] = true

        val shotResult = playerData.checkEnemyShot(gridX, gridY)
        if (shotResult.hit) {
            playerBoard.tiles[gridX][gridY].setHit()
            Gdx.app.log("Battleship", "Player ship hit: $gridX, $gridY !")
        } else {
            Gdx.app.log("Battleship", "Enemy shot missed")
            playerBoard.tiles[gridX][gridY].setMissed()
        }

        if (shotResult.sunkShip != null) {
            Gdx.app.log("Battleship","Player ship ${shotResult.sunkShip} sunk!!")
            //playerShipActors[shotResult.sunkShip]?.color = shipSunkColor
        }
    }
*/


    fun shoot(gridX: Int, gridY: Int) {
        val shotResult = game.shoot(playerId, gridX, gridY) ?: return

        if (shotResult.hit) {
            Gdx.app.log("Battleship", "Enemy ship hit: $gridX, $gridY !")
            enemyBoard.tiles[gridX][gridY].setHit()
        } else {
            Gdx.app.log("Battleship", "Player shot missed")
            enemyBoard.tiles[gridX][gridY].setMissed()
        }

        if (shotResult.sunkShip != null) {
            Gdx.app.log("Battleship","Enemy ship ${shotResult.sunkShip} sunk!!")
        }
    }


    //--------------- GameLogicListener methods ---------------
    override fun onEnemyShot(gridX: Int, gridY: Int, shotResult: ShotResult) {
        if (shotResult.hit) {
            Gdx.app.log("Battleship", "Player ship hit: $gridX, $gridY !")
            playerBoard.tiles[gridX][gridY].setHit()
        } else {
            Gdx.app.log("Battleship", "Enemy shot missed")
            playerBoard.tiles[gridX][gridY].setMissed()
        }

        if (shotResult.sunkShip != null) {
            Gdx.app.log("Battleship","Player ship ${shotResult.sunkShip} sunk!!")
            //playerShipActors[shotResult.sunkShip]?.color = shipSunkColor
        }
    }

    override fun onGameStarted(whoseTurn: PlayerId) {
        Gdx.app.log("Battleship", "Game started!")
    }

    override fun onGameFinished(winner: PlayerId) {

    }

    //--------------- end GameLogicListener methods ---------------

}
