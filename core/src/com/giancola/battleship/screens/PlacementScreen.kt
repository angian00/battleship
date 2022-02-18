package com.giancola.battleship.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.giancola.battleship.*
import com.giancola.battleship.GameConstants.N_COLS
import com.giancola.battleship.GameConstants.N_ROWS
import com.giancola.battleship.actors.PlayerBoard
import com.giancola.battleship.actors.ShipActor
import com.giancola.battleship.actors.overlaps
import com.giancola.battleship.ui.Styles
import ktx.actors.onClick
import ktx.app.KtxScreen


class PlacementScreen(val app: App) : KtxScreen, InputAdapter() {
    private val playerData = PlayerData("Guerino", N_ROWS, N_COLS, ShipFactory.standardSetup)
    //private val playerData = PlayerData("Guerino", N_ROWS, N_COLS, ShipFactory.debugSetup)

    private val bkg: Image
    val board: PlayerBoard
    val confirmButton: TextButton
    val ships: Map<Pair<ShipType, Int>, ShipActor>

    init {
        app.im.addProcessor(this)

        bkg = Image(Texture("placement_background.png"))
        app.stg.addActor(bkg)

        board = PlayerBoard(this)
        app.stg.addActor(board)

        confirmButton = TextButton("Confirm", Styles.buttonStyle)
        val r = LayoutConstants.standard2worldCoords(LayoutConstants.placementButton)
        confirmButton.setBounds(r.x, r.y, r.width, r.height)
        confirmButton.onClick {
            Gdx.app.exit()
        }
        //confirmButton.isDisabled = true
        confirmButton.isVisible = false
        app.stg.addActor(confirmButton)


        val mutableMap = mutableMapOf<Pair<ShipType, Int>, ShipActor>()
        for ((i, shipId) in this.playerData.shipPlacement.keys.withIndex()) {
            val shipActor = ShipActor(this, shipId)

            val shipRect = LayoutConstants.standard2worldCoords(LayoutConstants.shipPositions[i])
            shipActor.setPosition(shipRect.x, shipRect.y)

            app.stg.addActor(shipActor)

            mutableMap[shipId] = shipActor
        }
        this.ships = mutableMap.toMap()

        bkg.toBack()
    }

    override fun render(delta: Float) {
        app.stg.act()

        app.stg.draw()
    }

    override fun dispose() {
        app.im.removeProcessor(this)
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
        confirmButton.isVisible = allValid
    }
}
