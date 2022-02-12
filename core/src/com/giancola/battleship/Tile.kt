package com.giancola.battleship

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import ktx.actors.onClick

class Tile(val board: PlayerBoard, val iTile: Int, val jTile: Int): Actor() {
    companion object {
        val sr = ShapeRenderer()
    }


    init {
        this.color = randomColor()

        this.onClick {
            println("clicked on ${iTile}, ${jTile}")
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.end()

        sr.projectionMatrix = batch!!.projectionMatrix
        sr.transformMatrix = batch.transformMatrix
        sr.translate(x, y, 0f)

        //sr.begin(ShapeRenderer.ShapeType.Line)
        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.color = this.color
        sr.rect(0f, 0f, this.width, this.height)
        sr.end()

        batch?.begin()
        super.draw(batch, parentAlpha)
    }
}