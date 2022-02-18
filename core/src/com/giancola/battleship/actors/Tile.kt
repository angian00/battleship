package com.giancola.battleship.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import ktx.actors.onClick

class Tile(val board: PlayerBoard, val iTile: Int, val jTile: Int): Actor() {
    private val sprite: Sprite = Sprite(Texture("tile_empty.png"))


    init {
        /*
        this.onClick {
            Gdx.app.log("Battleship", "clicked on $iTile, $jTile")
        }
         */
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.draw(sprite, this.x, this.y)
        super.draw(batch, parentAlpha)
    }
}