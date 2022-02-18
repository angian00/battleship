package com.giancola.battleship.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils.random
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.giancola.battleship.BattleshipGame
import com.giancola.battleship.GameConstants.N_COLS
import com.giancola.battleship.GameConstants.N_ROWS
import com.giancola.battleship.GameConstants.TILE_SIZE_SMALL
import com.giancola.battleship.GraphicsConstants.shipDamagedColor
import com.giancola.battleship.GraphicsConstants.shipHealthyColor
import com.giancola.battleship.GraphicsConstants.shipSunkColor
import com.giancola.battleship.PlayerData
import com.giancola.battleship.ShipFactory
import com.giancola.battleship.ShipType
import com.giancola.battleship.actors.CombatEnemyBoard
import com.giancola.battleship.actors.CombatPlayerBoard
import com.giancola.battleship.actors.ShipActorNew
import ktx.app.KtxScreen


class CombatScreen(val gameApp: BattleshipGame, val playerData: PlayerData) : KtxScreen, InputAdapter() {
    val playerShipActors: Map<Pair<ShipType, Int>, ShipActorNew>

    private val bkg: Image
    val playerBoard: CombatPlayerBoard
    val enemyBoard: CombatEnemyBoard

    val enemyData = PlayerData("Enemy", N_ROWS, N_COLS, ShipFactory.standardSetup)
    init {
        gameApp.im.addProcessor(this)

        bkg = Image(Texture("combat_background.png"))
        gameApp.stg.addActor(bkg)

        playerBoard = CombatPlayerBoard(this)
        gameApp.stg.addActor(playerBoard)
        enemyBoard = CombatEnemyBoard(this)
        gameApp.stg.addActor(enemyBoard)

        playerShipActors = initShips()

        randomEnemyShots() //DEBUG

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


    private fun initShips(): Map<Pair<ShipType, Int>, ShipActorNew> {
        val mutableMap = mutableMapOf<Pair<ShipType, Int>, ShipActorNew>()

        val shipPlacements = playerData.shipPlacement
        for ((shipId, shipCoords) in shipPlacements) {
            if (shipCoords == null)
                continue

            val shipActor = ShipActorNew(shipId, TILE_SIZE_SMALL)
            shipActor.placeInGrid(shipCoords, playerBoard)

            if (shipId.first.name == "Carrier") {
                Gdx.app.log("Battleship", "placed ship: ${shipId}")
                Gdx.app.log("Battleship", "shipCoords: ${shipCoords}")
                Gdx.app.log("Battleship", shipActor.getGeometry())
            }

            shipActor.color = shipHealthyColor
            gameApp.stg.addActor(shipActor)

            mutableMap[shipId] = shipActor
        }

        return mutableMap.toMap()
    }


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

    fun enemyShot(iRow: Int, iCol: Int) {
        enemyData.shots[iRow][iCol] = true
        var missed = true

        for (shipId in playerData.shipPlacement.keys) {
            if (playerData.isShipOnTile(shipId, iRow, iCol)) {
                playerBoard.tiles[iRow][iCol].setHit()
                Gdx.app.log("Battleship","Player Ship $shipId hit!")

                if (playerData.isShipSunk(shipId, enemyData.shots)) {
                    Gdx.app.log("Battleship","Player Ship $shipId sunk!!")
                    playerShipActors[shipId]?.color = shipSunkColor
                } else {
                    playerShipActors[shipId]?.color = shipDamagedColor
                }

                missed = false
            }
        }

        if (missed) {
            Gdx.app.log("Battleship","Enemy shot missed")
            playerBoard.tiles[iRow][iCol].setMissed()
        }
    }
}
