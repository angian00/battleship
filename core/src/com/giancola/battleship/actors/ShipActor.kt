package com.giancola.battleship.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.giancola.battleship.GameConstants.TILE_SIZE
import com.giancola.battleship.GraphicsConstants.movementAnimDuration
import com.giancola.battleship.ShipType
import com.giancola.battleship.screens.PlacementScreen
import ktx.actors.alpha
import java.util.*
import kotlin.math.roundToInt


class ShipActor(private val screen: PlacementScreen, private val shipId: Pair<ShipType, Int>): Actor(),
    ShipGestureListener.ShipGestureCallbacks {

    private val texture: Texture
    private val textureRegion: TextureRegion

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
        val shipType = shipId.first
        val filename = "ship_${shipType.name.toLowerCase(Locale.US)}.png"
        texture = Texture(filename)
        textureRegion = TextureRegion(texture)

        this.width = texture.width.toFloat()
        this.height = texture.height.toFloat()
        this.originX = this.width / 2
        this.originY = this.height / 2
        this.rotation = 0f

        this.color = Color.ORANGE

        addListener(ShipGestureListener(this))
    }


    override fun onLongPress() {
        Gdx.app.log("Battleship", "(${shipId.first}).onLongPress")
        rotateBy(90f)
        snapToGrid()
    }

    override fun onDragStart() {
        Gdx.app.log("Battleship", "(${shipId.first}).onDragStart")
        alpha = 0.5f
    }

    override fun onDrop() {
        Gdx.app.log("Battleship", "(${shipId.first}).onDrop")
        alpha = 1.0f

        snapToGrid()
    }

    override fun onDragCanceled() {
        Gdx.app.log("Battleship", "onDragCanceled")
        alpha = 1.0f
    }


    override fun draw(batch: Batch?, parentAlpha: Float) {
        val localColor = color
        batch?.setColor(localColor.r, localColor.g, localColor.b, localColor.a * parentAlpha)

        batch?.draw(textureRegion, x, y, originX, originY, width, height, scaleX, scaleY, rotation)

        batch?.setColor(localColor.r, localColor.g, localColor.b, 1f)
        super.draw(batch, parentAlpha)
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
        val bbox = getBounds()

        //Gdx.app.log("Battleship", "before snapToGrid:")
        //Gdx.app.log("Battleship", getGeometry())

        var snapX = ((bbox.x - board.x) / TILE_SIZE).roundToInt() * TILE_SIZE + board.x
        var snapY = ((bbox.y - board.y) / TILE_SIZE).roundToInt() * TILE_SIZE + board.y

        snapX = snapX.coerceIn(board.x, board.x + board.width - bbox.width)
        snapY = snapY.coerceIn(board.y, board.y + board.height - bbox.height)

        //moveBy(snapX-bbox.x, snapY-bbox.y)
        //Gdx.app.log("Battleship", "after snapToGrid:")
        //Gdx.app.log("Battleship", getGeometry())

        addAction(Actions.sequence(
            Actions.moveBy(snapX-bbox.x, snapY-bbox.y, movementAnimDuration, Interpolation.pow3),
            Actions.run {
                screen.validatePlacements()
            }
        ))
    }

    private fun getGeometry(): String {
        val bbox = getBounds()

        return "x=${x}, y=${y}, originX=${originX}, originY=${originY}, rotation=${rotation} \n" +
                "bbox.x=${bbox.x}, bbox.y=${bbox.y}, bbox.width=${bbox.width}, bbox.height=${bbox.height}"
    }

}