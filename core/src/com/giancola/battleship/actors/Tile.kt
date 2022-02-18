package com.giancola.battleship.actors

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import kotlin.math.roundToInt

class Tile(val iTile: Int, val jTile: Int, val tileSize: Float): Actor() {
    private var currTexture: Texture
    private val emptyTexture = Texture("tile_empty_${tileSize.roundToInt()}.png")
    private val hitTexture = Texture("tile_hit_${tileSize.roundToInt()}.png")
    private val missedTexture = Texture("tile_missed_${tileSize.roundToInt()}.png")

    init {
        currTexture = emptyTexture
        /*
        this.onClick {
            Gdx.app.log("Battleship", "clicked on $iTile, $jTile")
        }
         */
    }

    fun setHit() {
        currTexture = hitTexture
        this.toFront()
    }

    fun setMissed() {
        currTexture = missedTexture
        this.toFront()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        val localColor = color
        batch?.setColor(localColor.r, localColor.g, localColor.b, localColor.a * parentAlpha)

        batch?.draw(currTexture, this.x, this.y, tileSize, tileSize)

        batch?.setColor(localColor.r, localColor.g, localColor.b, 1f)
        super.draw(batch, parentAlpha)
    }
}