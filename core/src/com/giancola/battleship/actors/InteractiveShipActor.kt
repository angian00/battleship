package com.giancola.battleship.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.giancola.battleship.GameConstants.TILE_SIZE
import com.giancola.battleship.GraphicsConstants
import com.giancola.battleship.ShipId
import com.giancola.battleship.screens.PlacementScreen
import ktx.actors.alpha
import kotlin.math.roundToInt


class InteractiveShipActor(private val screen: PlacementScreen, shipId: ShipId, tileSize: Float = TILE_SIZE): ShipActor(shipId, tileSize), ShipGestureListener.ShipGestureCallbacks {
    var validPlacement: Boolean? = null
        set(value) {
            field = value
            color = when (value) {
                true -> Color.GREEN
                false -> Color.RED
                null -> Color.ORANGE
            }
        }

    init {
        this.color = Color.ORANGE

        addListener(ShipGestureListener(this))
    }


    override fun onLongPress() {
        //Gdx.app.log("Battleship", "(${shipId.first}).onLongPress")
        rotateBy(90f)
        snapToGrid()
    }

    override fun onDragStart() {
        //Gdx.app.log("Battleship", "(${shipId.first}).onDragStart")
        alpha = 0.5f
    }

    override fun onDrop() {
        //Gdx.app.log("Battleship", "(${shipId.first}).onDrop")
        alpha = 1.0f

        snapToGrid()
    }

    override fun onDragCanceled() {
        //Gdx.app.log("Battleship", "onDragCanceled")
        alpha = 1.0f
    }



    fun validatePlacement(): Boolean {
        this.validPlacement = true

        for (shipActor in screen.ships.values) {
            if (shipActor === this)
                continue

            if (this.overlaps(shipActor)) {
                this.validPlacement = false
                shipActor.validPlacement = false
            }
        }

        return this.validPlacement!!
    }


    private fun snapToGrid() {
        val board = screen.board
        val bbox = getBoundingBox()

        //Gdx.app.log("Battleship", "before snapToGrid:")
        //Gdx.app.log("Battleship", getGeometry())

        var snapX = ((bbox.x - board.x) / TILE_SIZE).roundToInt() * TILE_SIZE + board.x
        var snapY = ((bbox.y - board.y) / TILE_SIZE).roundToInt() * TILE_SIZE + board.y

        snapX = snapX.coerceIn(board.x, board.x + board.width - bbox.width)
        snapY = snapY.coerceIn(board.y, board.y + board.height - bbox.height)

        //moveBy(snapX-bbox.x, snapY-bbox.y)
        //Gdx.app.log("Battleship", "after snapToGrid:")
        //Gdx.app.log("Battleship", getGeometry())

        addAction(
            Actions.sequence(
            Actions.moveBy(snapX-bbox.x, snapY-bbox.y, GraphicsConstants.movementAnimDuration, Interpolation.pow3),
            Actions.run {
                screen.validatePlacements()
            }
        ))
    }

}