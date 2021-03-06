package com.giancola.battleship

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.giancola.battleship.screens.LoginScreen
import ktx.app.KtxGame


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

        addScreen(LoginScreen(this))
        setScreen<LoginScreen>()
    }


    override fun dispose() {
        this.sb.dispose()
        this.stg.dispose()
        super.dispose()
    }

    fun restart() {
        stg.clear()

        for (screenEntry in screens.entries()) {
            val screenClass = screenEntry.key!!
            //val screen = screenEntry.value!!

            screens.remove(screenClass)
        }

        addScreen(LoginScreen(this))
        setScreen<LoginScreen>()
    }


}