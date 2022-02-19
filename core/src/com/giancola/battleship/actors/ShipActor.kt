package com.giancola.battleship.actors

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.giancola.battleship.GameConstants.TILE_SIZE
import com.giancola.battleship.ShipType
import java.util.*
import kotlin.math.roundToInt


open class ShipActor(val shipId: Pair<ShipType, Int>, private val tileSize: Float = TILE_SIZE): Actor() {

    private val texture: Texture
    private val textureRegion: TextureRegion


    init {
        val shipType = shipId.first
        val filename = "ship_${shipType.name.toLowerCase(Locale.US)}_${tileSize.roundToInt()}.png"
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


    fun placeInGrid(coords: Pair<GridPoint2, GridPoint2>, board: Actor) {
        val gridXStart = coords.first.x
        val gridYStart = coords.first.y
        val gridXEnd = coords.second.x
        val gridYEnd = coords.second.y

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