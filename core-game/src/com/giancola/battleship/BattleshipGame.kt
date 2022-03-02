package com.giancola.battleship

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.giancola.battleship.screens.PlacementScreen
import ktx.app.KtxGame
import com.badlogic.gdx.InputMultiplexer
import com.giancola.battleship.screens.CombatScreen


class BattleshipGame: KtxGame<Screen>() {

    var width = 0f
    var height = 0f

    private lateinit var sb: SpriteBatch
    private lateinit var view: StretchViewport

    lateinit var cam: OrthographicCamera
    lateinit var stg: Stage
    lateinit var im: InputMultiplexer


    override fun create() {
        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height.toFloat()

        this.sb = SpriteBatch()

        this.cam = OrthographicCamera(this.width, this.height)
        this.view = StretchViewport(GameConstants.WORLD_WIDTH, GameConstants.WORLD_HEIGHT, this.cam)
        this.stg = Stage(this.view, this.sb)

        this.im = InputMultiplexer()
        im.addProcessor(this.stg)
        Gdx.input.inputProcessor = im

        addScreen(PlacementScreen(this))
        setScreen<PlacementScreen>()
    }


    override fun dispose() {
        this.sb.dispose()
        this.stg.dispose()
        super.dispose()
    }
}