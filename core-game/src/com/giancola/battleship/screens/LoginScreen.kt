package com.giancola.battleship.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.giancola.battleship.*
import com.giancola.battleship.gamelogic.*
import com.giancola.battleship.net.RemoteClient
import com.giancola.battleship.ui.OverlayMessage
import com.giancola.battleship.ui.Styles
import ktx.actors.onClick
import ktx.app.KtxScreen


const val SERVER_PLACEHOLDER = "<hostname:port>"
const val NAME_PLACEHOLDER = "<your name>"

const val LOGIN_DATA_FILE = "loginData.json"

class LoginScreen(private val gameApp: BattleshipGame) : KtxScreen, InputAdapter(), GameLogicListener {
    private var playerId: PlayerId? = null
    private val bkg: Image
    private val serverTextField: TextField
    private val loginTextField: TextField
    private val loginButton: TextButton
    private var client: RemoteClient? = null


    init {
        gameApp.im.addProcessor(this)

        bkg = Image(Texture("login_background.png"))
        gameApp.stg.addActor(bkg)

        serverTextField = TextField(SERVER_PLACEHOLDER, Styles.textFieldStyle)
        var r = LayoutConstants.standard2worldCoords(LayoutConstants.serverTextField)
        serverTextField.setBounds(r.x, r.y, r.width, r.height)
        gameApp.stg.addActor(serverTextField)

        loginTextField = TextField(NAME_PLACEHOLDER, Styles.textFieldStyle)
        r = LayoutConstants.standard2worldCoords(LayoutConstants.loginTextField)
        loginTextField.setBounds(r.x, r.y, r.width, r.height)
        gameApp.stg.addActor(loginTextField)

        val loginData = JsonData.load(LOGIN_DATA_FILE)
        if (loginData != null) {
            serverTextField.text = loginData.data["serverName"]
            loginTextField.text = loginData.data["loginName"]
        }

        loginButton = TextButton("Login", Styles.buttonStyle)
        r = LayoutConstants.standard2worldCoords(LayoutConstants.loginButton)
        loginButton.setBounds(r.x, r.y, r.width, r.height)

        loginButton.onClick {
            val tokens = serverTextField.text.split(":")
            try {
                val hostname = tokens[0]
                val port = tokens[1].toInt()
                client = RemoteClient(hostname, port, this)

            } catch (e: Exception) {
                serverTextField.text = SERVER_PLACEHOLDER

                return@onClick
            }

            val loginData = mapOf("serverName" to serverTextField.text, "loginName" to loginTextField.text)
            JsonData(loginData).save(LOGIN_DATA_FILE)

            Gdx.app.log("Battleship", "sending login command")
            client?.sendLogin()

            serverTextField.isDisabled = true
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
        //val playerData = PlayerData(loginTextField.text, ShipFactory.debugSetup, GameConstants.N_ROWS, GameConstants.N_COLS)
        val placementScreen = PlacementScreen(gameApp, client!!, playerData, playerId!!)
        client!!.localListener = placementScreen

        gameApp.removeScreen<LoginScreen>()

        gameApp.addScreen(placementScreen)
        gameApp.setScreen<PlacementScreen>()

        dispose()
    }

    override fun onGameDisconnected() {
        Gdx.app.log("Battleship", "Game disconnected")

        val msg = OverlayMessage(gameApp.stg)
        msg.setText(GraphicsConstants.disconnectionMessageText)

        msg.addAction(Actions.sequence(
            Actions.show(),
            Actions.delay(GraphicsConstants.disconnectionMessageDuration),
            Actions.run {
                client?.dispose()
                this@LoginScreen.dispose()
                gameApp.restart()
            }
        ))
    }

    override fun onCombatStarted(whoseTurn: PlayerId, playerNames: Map<PlayerId, String>) {}
    override fun onShot(shooter: PlayerId, gridX: Int, gridY: Int, shotResult: ShotResult?) {}
    override fun onGameFinished(winner: PlayerId) {}

    override fun onError(error: String?) {
        Gdx.app.log("Battleship", "Error: $error")

        serverTextField.text = SERVER_PLACEHOLDER
        loginTextField.text = NAME_PLACEHOLDER
        loginButton.setText("Login")

        serverTextField.isDisabled = false
        loginTextField.isDisabled = false
        loginButton.isDisabled = false
    }
}
