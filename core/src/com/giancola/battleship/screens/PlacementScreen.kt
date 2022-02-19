package com.giancola.battleship.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.giancola.battleship.*
import com.giancola.battleship.GameConstants.TILE_SIZE
import com.giancola.battleship.actors.InteractiveShipActor
import com.giancola.battleship.actors.PlacementBoard
import com.giancola.battleship.actors.getBoundingBox
import com.giancola.battleship.actors.overlaps
import com.giancola.battleship.ui.Styles
import ktx.actors.onClick
import ktx.app.KtxScreen
import kotlin.math.roundToInt


class PlacementScreen(private val gameApp: BattleshipGame) : KtxScreen, InputAdapter() {
    private val playerData = PlayerData("Guerino", ShipFactory.standardSetup)

    private val bkg: Image
    val board: PlacementBoard
    val ships: Map<Pair<ShipType, Int>, InteractiveShipActor>
    private val confirmButton: TextButton

    init {
        gameApp.im.addProcessor(this)

        bkg = Image(Texture("placement_background.png"))
        gameApp.stg.addActor(bkg)

        board = PlacementBoard(gameApp.stg)
        gameApp.stg.addActor(board)

        confirmButton = TextButton("Confirm", Styles.buttonStyle)
        val r = LayoutConstants.standard2worldCoords(LayoutConstants.placementButton)
        confirmButton.setBounds(r.x, r.y, r.width, r.height)
        confirmButton.onClick {
            Gdx.app.log("Battleship", "Switching to combat screen")

            updatePlayerData()

            gameApp.stg.clear()

            val combatScreen = CombatScreen(gameApp, playerData)
            gameApp.addScreen(combatScreen)
            gameApp.setScreen<CombatScreen>()

            dispose()
        }
        //confirmButton.isDisabled = true  //DEBUG
        //confirmButton.isVisible = false
        gameApp.stg.addActor(confirmButton)


        val mutableMap = mutableMapOf<Pair<ShipType, Int>, InteractiveShipActor>()
        for ((i, shipId) in this.playerData.shipPlacement.keys.withIndex()) {
            val shipActor = InteractiveShipActor(this, shipId)

            val shipRect = LayoutConstants.standard2worldCoords(LayoutConstants.placementShipPositions[i])
            shipActor.setPosition(shipRect.x, shipRect.y)

            gameApp.stg.addActor(shipActor)

            mutableMap[shipId] = shipActor
        }
        this.ships = mutableMap.toMap()

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

    fun validatePlacements() {
        var allValid = true

        for (ship in ships.values) {
            if (!ship.overlaps(board)) {
                ship.validPlacement = null
                allValid = false
            } else {
                ship.validatePlacement()
                if (!ship.validPlacement!!)
                    allValid = false
            }
        }

        //enable confirm button if everything ship is placed
        //screen.confirmButton.isDisabled = !allValid
        //confirmButton.isVisible = allValid
    }

    private fun updatePlayerData() {
        for (ship in ships.values) {
            val bbox = ship.getBoundingBox()
            val iGridStart = ((bbox.x - board.x) / TILE_SIZE).roundToInt()
            val jGridStart = ((bbox.y - board.y) / TILE_SIZE).roundToInt()
            val iGridEnd = ((bbox.x + bbox.width - board.x) / TILE_SIZE).roundToInt() - 1
            val jGridEnd = ((bbox.y + bbox.height - board.y) / TILE_SIZE).roundToInt() - 1

            playerData.shipPlacement[ship.shipId] = Pair(
                GridPoint2(iGridStart, jGridStart),
                GridPoint2(iGridEnd, jGridEnd)
            )
        }
    }
}
