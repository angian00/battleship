package com.giancola.battleship.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.giancola.battleship.BattleshipGame
import com.giancola.battleship.GameConstants
import com.giancola.battleship.LayoutConstants
import com.giancola.battleship.gamelogic.*
import com.giancola.battleship.net.RemoteClient
import com.giancola.battleship.ui.Styles
import ktx.actors.onClick
import ktx.app.KtxScreen


class LoginScreen(private val gameApp: BattleshipGame) : KtxScreen, InputAdapter(), GameLogicListener {
    private var playerId: PlayerId? = null
    private val bkg: Image
    private val loginTextField: TextField
    private val loginButton: TextButton
    private val client: RemoteClient

    init {
        gameApp.im.addProcessor(this)

        client = RemoteClient("localhost", 8080) //TODO: get from configuration
        client.localListener = this


        bkg = Image(Texture("login_background.png"))
        gameApp.stg.addActor(bkg)

        loginTextField = TextField("Your name", Styles.textFieldStyle)
        var r = LayoutConstants.standard2worldCoords(LayoutConstants.loginTextField)
        loginTextField.setBounds(r.x, r.y, r.width, r.height)
        gameApp.stg.addActor(loginTextField)

        loginButton = TextButton("Login", Styles.buttonStyle)
        r = LayoutConstants.standard2worldCoords(LayoutConstants.loginButton)
        loginButton.setBounds(r.x, r.y, r.width, r.height)

        loginButton.onClick {
            client.sendLogin()
            Gdx.app.log("Battleship", "sending login command")

            loginTextField.isDisabled = true
            loginButton.isDisabled = true
            loginButton.setText("Waiting for match...")
        }
        gameApp.stg.addActor(loginButton)


        bkg.toBack()
    }

    override fun render(delta: Float) {
        gameApp.stg.act()
        gameApp.stg.draw()
    }

    override fun dispose() {
        gameApp.im.removeProcessor(this)
        super.dispose()
    }



    override fun onGameStarting(playerId: PlayerId) {
        Gdx.app.log("Battleship", "Game starting, I am $playerId")
        this.playerId = playerId
    }

    override fun onGameStarted() {
        Gdx.app.log("Battleship", "Switching to placement screen")

        gameApp.stg.clear()

        val playerData = PlayerData(loginTextField.text, ShipFactory.standardSetup, GameConstants.N_ROWS, GameConstants.N_COLS)
        val placementScreen = PlacementScreen(gameApp, client, playerData, playerId!!)
        client.localListener = placementScreen

        gameApp.addScreen(placementScreen)
        gameApp.setScreen<PlacementScreen>()

        dispose()
    }
    override fun onCombatStarted(whoseTurn: PlayerId) {}
    override fun onShot(shooter: PlayerId, gridX: Int, gridY: Int, shotResult: ShotResult?) {}
    override fun onGameFinished(winner: PlayerId) {}
}
