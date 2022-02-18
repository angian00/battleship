package com.giancola.battleship.screens

import com.badlogic.gdx.graphics.Color.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.giancola.battleship.BattleshipGame
import ktx.app.KtxScreen
import ktx.graphics.*


class DemoScreen(val battleshipGame: BattleshipGame) : KtxScreen {
    private val sr = ShapeRenderer()
    private var rotation = 0.0f

    init {

    }

    override fun render(delta: Float) {
        rotation += 1f

        with(sr) {
            use(Line) {
                translate(battleshipGame.width / 2, battleshipGame.height / 2, 0f)
                rotate(0f, 0f, 1f, rotation)
                color = WHITE
                rect(0f - 75, 0f - 75, 150f, 150f)
            }

            use(Filled) {
                identity()
                color = RED
                circle(0f, 0f, 200f, 25)

                color = PINK
                circle(0f, battleshipGame.height, 200f, 25)

                color = YELLOW
                circle(battleshipGame.width, battleshipGame.height, 200f, 25)

                color = BLUE
                circle(battleshipGame.width, 0f, 200f, 25)
            }
        }
    }

    override fun dispose() {
        sr.dispose()
        super.dispose()
    }
}
