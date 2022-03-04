package com.giancola.battleship.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.giancola.battleship.BattleshipGame
import com.giancola.battleship.GameConstants.TILE_SIZE
import com.giancola.battleship.LayoutConstants
import com.giancola.battleship.actors.InteractiveShipActor
import com.giancola.battleship.actors.PlacementBoard
import com.giancola.battleship.actors.getBoundingBox
import com.giancola.battleship.actors.overlaps
import com.giancola.battleship.gamelogic.*
import com.giancola.battleship.net.RemoteClient
import com.giancola.battleship.ui.Styles
import ktx.actors.onClick
import ktx.app.KtxScreen
import kotlin.math.roundToInt


class PlacementScreen(private val gameApp: BattleshipGame, private val client: RemoteClient,
                      private val playerData: PlayerData, private val playerId: PlayerId) : KtxScreen, InputAdapter(), GameLogicListener {

    private val bkg: Image
    val board: PlacementBoard
    val ships: Map<ShipId, InteractiveShipActor>
    private val confirmButton: TextButton

    init {
        gameApp.im.addProcessor(this)

        bkg = Image(Texture("placement_background.png"))
        gameApp.stg.addActor(bkg)

        board = PlacementBoard(gameApp.stg)
        gameApp.stg.addActor(board)

        confirmButton = TextButton("Confirm", Styles.buttonStyle)
        val r = LayoutConstants.standard2worldCoords(LayoutConstants.placementButton)
        confirmButton.setBounds(r.x, r.y, r.width, r.height)

        confirmButton.onClick {
            updatePlayerData()
            client.sendSetPlacement(playerData)
            Gdx.app.log("Battleship", "sending setPlacement command")

            confirmButton.isDisabled = true
            confirmButton.setText("Waiting for enemy...")
        }
        confirmButton.isDisabled = true
        gameApp.stg.addActor(confirmButton)


        val mutableMap = mutableMapOf<ShipId, InteractiveShipActor>()
        for ((i, shipId) in this.playerData.shipPlacements.keys.withIndex()) {
            val shipActor = InteractiveShipActor(this, shipId)

            val shipRect = LayoutConstants.standard2worldCoords(LayoutConstants.placementShipPositions[i])
            shipActor.setPosition(shipRect.x, shipRect.y)

            gameApp.stg.addActor(shipActor)

            mutableMap[shipId] = shipActor
        }
        this.ships = mutableMap.toMap()

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

    fun validatePlacements() {
        var allValid = true

        for (ship in ships.values) {
            if (!ship.overlaps(board)) {
                ship.validPlacement = null
                allValid = false
            } else {
                ship.validatePlacement()
                if (!ship.validPlacement!!)
                    allValid = false
            }
        }

        //enable confirm button if everything ship is placed
        confirmButton.isDisabled = !allValid
    }

    private fun updatePlayerData() {
        for (ship in ships.values) {
            val bbox = ship.getBoundingBox()
            val iGridStart = ((bbox.x - board.x) / TILE_SIZE).roundToInt()
            val jGridStart = ((bbox.y - board.y) / TILE_SIZE).roundToInt()
            val iGridEnd = ((bbox.x + bbox.width - board.x) / TILE_SIZE).roundToInt() - 1
            val jGridEnd = ((bbox.y + bbox.height - board.y) / TILE_SIZE).roundToInt() - 1

            playerData.shipPlacements[ship.shipId] = ShipPlacement(
                Coords(iGridStart, jGridStart),
                Coords(iGridEnd, jGridEnd)
            )
        }
    }



    override fun onGameStarting(playerId: PlayerId) {}
    override fun onGameStarted() {}

    override fun onCombatStarted(whoseTurn: PlayerId) {
        Gdx.app.log("Battleship", "Switching to combat screen")

        gameApp.stg.clear()

        val combatScreen = CombatScreen(gameApp, client, playerData, playerId, whoseTurn)
        client.localListener = combatScreen

        gameApp.addScreen(combatScreen)
        gameApp.setScreen<CombatScreen>()

        dispose()
    }

    override fun onShot(shooter: PlayerId, gridX: Int, gridY: Int, shotResult: ShotResult?) {}
    override fun onGameFinished(winner: PlayerId) {}
}
