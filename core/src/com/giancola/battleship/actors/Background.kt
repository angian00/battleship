package com.giancola.battleship.actors

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor

class Background(private val filename: String): Actor() {
    private val sprite: Sprite = Sprite(Texture(filename))

    override fun draw(batch: Batch?, parentAlpha: Float) {
        sprite.draw(batch, parentAlpha)
    }
}