package com.giancola.battleship.actors

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.giancola.battleship.GameConstants.TILE_SIZE
import com.giancola.battleship.gamelogic.ShipId
import com.giancola.battleship.gamelogic.ShipPlacement
import java.util.*
import kotlin.math.roundToInt


open class ShipActor(val shipId: ShipId, private val tileSize: Float = TILE_SIZE): Actor() {

    private val texture: Texture
    private val textureRegion: TextureRegion


    init {
        val filename = "ship_${shipId.shipType.name.toLowerCase(Locale.US)}_${tileSize.roundToInt()}.png"
        texture = Texture(filename)
        textureRegion = TextureRegion(texture)

        this.width = texture.width.toFloat()
        this.height = texture.height.toFloat()
        this.originX = this.width / 2
        this.originY = this.height / 2
        this.rotation = 0f
    }


    override fun draw(batch: Batch?, parentAlpha: Float) {
        val localColor = color
        batch?.setColor(localColor.r, localColor.g, localColor.b, localColor.a * parentAlpha)

        batch?.draw(textureRegion, x, y, originX, originY, width, height, scaleX, scaleY, rotation)

        batch?.setColor(localColor.r, localColor.g, localColor.b, 1f)
        super.draw(batch, parentAlpha)
    }


    fun getGeometry(): String {
        val bbox = getBoundingBox()

        return "x=${x}, y=${y}, originX=${originX}, originY=${originY}, width=${width}, height=${height}, rotation=${rotation} \n" +
                "bbox.x=${bbox.x}, bbox.y=${bbox.y}, bbox.width=${bbox.width}, bbox.height=${bbox.height}"
    }


    fun placeInGrid(shipPlacement: ShipPlacement, board: Actor) {
        val gridXStart = shipPlacement.from.x
        val gridYStart = shipPlacement.from.y
        val gridXEnd = shipPlacement.to.x
        val gridYEnd = shipPlacement.to.y

        val originAbsX = board.x + tileSize * (gridXStart + gridXEnd + 1) / 2f
        val originAbsY = board.y + tileSize * (gridYStart + gridYEnd + 1) / 2f

        x = originAbsX - width / 2
        y = originAbsY - height / 2

        rotation = if (gridXStart == gridXEnd) {
            if (gridYStart < gridYEnd)
                90f
            else
                270f

        } else {
            if (gridXStart < gridXEnd)
                0f
            else
                180f
        }
    }
}