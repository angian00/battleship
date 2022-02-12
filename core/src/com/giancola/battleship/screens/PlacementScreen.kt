package com.giancola.battleship.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.giancola.battleship.App
import com.giancola.battleship.PlayerBoard
import com.giancola.battleship.actors.Background
import ktx.app.KtxScreen
import ktx.graphics.*


class PlacementScreen(val app: App) : KtxScreen {
    private val bkg = Background("placement_background.png")

    init {
        app.stg.addActor(bkg)
        app.stg.addActor(PlayerBoard(this))

        bkg.toBack()
    }

    override fun render(delta: Float) {
        app.stg.act()
        app.stg.draw()
    }

    override fun dispose() {
        super.dispose()
    }
}
