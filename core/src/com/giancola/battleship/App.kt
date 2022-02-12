package com.giancola.battleship

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.giancola.battleship.screens.DemoScreen
import com.giancola.battleship.screens.PlacementScreen
import ktx.app.KtxGame
import com.badlogic.gdx.InputMultiplexer


class App : KtxGame<Screen>() {

    var width = 0f
    var height = 0f

    private lateinit var sb: SpriteBatch
    private lateinit var view: StretchViewport

    lateinit var cam: OrthographicCamera
    lateinit var stg: Stage

    override fun create() {
        width = Gdx.graphics.width.toFloat()
        height = Gdx.graphics.height.toFloat()

        this.sb = SpriteBatch()

        this.cam = OrthographicCamera(this.width, this.height)
        this.view = StretchViewport(GameConstants.WORLD_WIDTH, GameConstants.WORLD_HEIGHT, this.cam)
        this.stg = Stage(this.view, this.sb)

        val im = InputMultiplexer()
        im.addProcessor(this.stg)
        Gdx.input.inputProcessor = im


        //val gameScreen = DemoScreen(this)
        val gameScreen = PlacementScreen(this)

        addScreen(gameScreen)
        //setScreen<DemoScreen>()
        setScreen<PlacementScreen>()
    }

    override fun dispose() {
        this.sb.dispose()
        this.stg.dispose()
        super.dispose()
    }
}