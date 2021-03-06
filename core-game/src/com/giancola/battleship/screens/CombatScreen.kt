package com.giancola.battleship.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.giancola.battleship.*
import com.giancola.battleship.GameConstants.TILE_SIZE
import com.giancola.battleship.GameConstants.TILE_SIZE_SMALL
import com.giancola.battleship.GraphicsConstants.enemyShipColor
import com.giancola.battleship.GraphicsConstants.feedbackBadColor
import com.giancola.battleship.GraphicsConstants.feedbackGoodColor
import com.giancola.battleship.GraphicsConstants.feedbackNeutralColor
import com.giancola.battleship.GraphicsConstants.playerShipColor
import com.giancola.battleship.actors.CombatEnemyBoard
import com.giancola.battleship.actors.CombatPlayerBoard
import com.giancola.battleship.actors.ShipActor
import com.giancola.battleship.gamelogic.*
import com.giancola.battleship.net.RemoteClient
import com.giancola.battleship.ui.*
import ktx.actors.onClick
import ktx.app.KtxScreen


class CombatScreen(private val gameApp: BattleshipGame, private val client: RemoteClient,
                   private val playerData: PlayerData, private val playerId: PlayerId,
                   private val playerNames: Map<PlayerId, String>, startTurn: PlayerId) :
            KtxScreen, InputAdapter(), GameLogicListener {

    private val playerShipActors: Map<ShipId, ShipActor>
    private val enemyShipActors: MutableMap<ShipId, ShipActor> = mutableMapOf()

    var moveTime: Float

    private val bkg: Image
    private val playerBoard: CombatPlayerBoard
    private val enemyBoard: CombatEnemyBoard

    private val combatNamesLabel: Label
    private val feedbackLabel: Label
    private val turnLabel: Label
    private val timeLabel: Label

    private val restartButton: Button


    init {
        val stage = gameApp.stg

        gameApp.im.addProcessor(this)

        bkg = Image(Texture("combat_background.png"))
        stage.addActor(bkg)

        playerBoard = CombatPlayerBoard(stage)
        enemyBoard = CombatEnemyBoard(this, stage)

        combatNamesLabel = CombatNamesLabel(stage)
        feedbackLabel = CombatFeedbackLabel(stage)
        turnLabel = CombatTurnLabel(stage)
        timeLabel = CombatTimeLabel(stage)

        initLabels(startTurn)

        restartButton = TextButton("New Game", Styles.buttonStyle)
        val r = LayoutConstants.standard2worldCoords(LayoutConstants.combatTurnLabel)
        restartButton.setBounds(r.x, r.y, r.width, r.height)
        restartButton.onClick {
            client.dispose()
            dispose()
            gameApp.restart()
        }

        restartButton.isVisible = false
        gameApp.stg.addActor(restartButton)

        playerShipActors = initShips()

        moveTime = 0f

        bkg.toBack()
    }

    override fun render(delta: Float) {
        gameApp.stg.act()
        updateTime(delta)
        gameApp.stg.draw()
    }

    override fun dispose() {
        gameApp.im.removeProcessor(this)
        super.dispose()
    }


    private fun initShips(): Map<ShipId, ShipActor> {
        val mutableMap = mutableMapOf<ShipId, ShipActor>()

        val shipPlacements = playerData.shipPlacements
        for ((shipId, shipCoords) in shipPlacements) {
            if (shipCoords == null)
                continue

            val shipActor = ShipActor(shipId, TILE_SIZE_SMALL)
            shipActor.placeInGrid(shipCoords, playerBoard)

            shipActor.color = playerShipColor
            gameApp.stg.addActor(shipActor)

            mutableMap[shipId] = shipActor
        }

        return mutableMap.toMap()
    }

    private fun addEnemyShip(shipId: ShipId, placement: ShipPlacement) {
        val shipActor = ShipActor(shipId, TILE_SIZE)
        shipActor.placeInGrid(placement, enemyBoard)

        shipActor.color = enemyShipColor
        gameApp.stg.addActor(shipActor)
        shipActor.zIndex = bkg.zIndex + 1
        enemyShipActors[shipId] = shipActor
    }


    fun shoot(gridX: Int, gridY: Int) {
        //gameLogic.shoot(playerId, gridX, gridY)
        client.sendShoot(gridX, gridY)
    }

    private fun playerShot(gridX: Int, gridY: Int, shotResult: ShotResult?) {
        if (shotResult == null) {
            return
        }

        if (shotResult.hit) {
            Gdx.app.log("Battleship", "[${coords2str(gridX, gridY)}] Enemy ship hit !")
            feedbackLabel.color = feedbackGoodColor
            feedbackLabel.setText("[${coords2str(gridX, gridY)}] Enemy ship hit !")
            enemyBoard.tiles[gridX][gridY].setHit()
            Sounds.hit.play()

        } else {
            Gdx.app.log("Battleship", "[${coords2str(gridX, gridY)}] Player shot missed")
            feedbackLabel.color = feedbackNeutralColor
            feedbackLabel.setText("[${coords2str(gridX, gridY)}] Shot missed")
            enemyBoard.tiles[gridX][gridY].setMissed()
            Sounds.miss.play()
        }

        if (shotResult.sunkShipId != null) {
            val sunkShipId = shotResult.sunkShipId!!
            Gdx.app.log("Battleship","[${coords2str(gridX, gridY)}] Enemy [${sunkShipId.shipType}] sunk !!")
            feedbackLabel.color = feedbackGoodColor
            feedbackLabel.setText("[${coords2str(gridX, gridY)}] Enemy [${sunkShipId.shipType.name}] sunk !!")
            Sounds.sink.play()

            addEnemyShip(sunkShipId, shotResult.sunkShipPlacement!!)
            /*
            for (tileRow in enemyBoard.tiles) {
                for (tile in tileRow)
                    tile.toFront()
            }
            */
        }

        moveTime = 0f
        turnLabel.setText("Enemy's turn")
    }


    private fun enemyShot(gridX: Int, gridY: Int, shotResult: ShotResult?) {
        if (shotResult == null)
            return

        if (shotResult.hit) {
            Gdx.app.log("Battleship", "[${coords2str(gridX, gridY)}] Player ship hit !")
            feedbackLabel.color = feedbackBadColor
            feedbackLabel.setText("[${coords2str(gridX, gridY)}] Player ship hit !")
            playerBoard.tiles[gridX][gridY].setHit()
            Sounds.hit.play()

        } else {
            Gdx.app.log("Battleship", "[${coords2str(gridX, gridY)}] Enemy shot missed")
            feedbackLabel.color = feedbackNeutralColor
            feedbackLabel.setText("[${coords2str(gridX, gridY)}] Enemy shot missed")
            playerBoard.tiles[gridX][gridY].setMissed()
            Sounds.miss.play()
        }

        if (shotResult.sunkShipId != null) {
            val sunkShipId = shotResult.sunkShipId!!
            Gdx.app.log("Battleship","[${coords2str(gridX, gridY)}] Player ship ${sunkShipId.shipType} sunk!!")
            feedbackLabel.color = feedbackBadColor
            feedbackLabel.setText("[${coords2str(gridX, gridY)}] Player ship sunk [${sunkShipId.shipType.name}] !!")
            Sounds.sink.play()
        }

        moveTime = 0f
        turnLabel.setText("Your turn")
    }


    private fun initLabels(whoseTurn: PlayerId) {
        combatNamesLabel.setText("${playerNames[PlayerId.Player1]} vs ${playerNames[PlayerId.Player2]}")

        val playerStr = when (whoseTurn) {
            playerId -> "Your"; else -> "Enemy's"
        }
        Gdx.app.log("Battleship", "Combat started - $playerStr turn")
        feedbackLabel.color = feedbackNeutralColor
        feedbackLabel.setText("Combat started")

        moveTime = 0f
        turnLabel.setText("$playerStr turn")
    }

    //--------------- GameLogicListener methods ---------------
    override fun onGameStarting(playerId: PlayerId) {}
    override fun onGameStarted() {}
    override fun onCombatStarted(whoseTurn: PlayerId, playerNames: Map<PlayerId, String>) {}

    override fun onGameFinished(winner: PlayerId) {
        val playerStr = when (winner) { playerId -> "You"; else -> "Enemy" }
        Gdx.app.log("Battleship", "Game finished - $playerStr won !")
        feedbackLabel.color = if (winner == playerId) feedbackGoodColor else feedbackBadColor
        feedbackLabel.setText("Game finished - $playerStr won !")

        moveTime = 0f
        turnLabel.setText("")
        turnLabel.isVisible = false

        restartButton.isVisible = true
    }

    override fun onGameDisconnected() {
        Gdx.app.log("Battleship", "Game disconnected")

        val msg = OverlayMessage(gameApp.stg)
        msg.setText(GraphicsConstants.disconnectionMessageText)

        msg.addAction(
            Actions.sequence(
                Actions.show(),
                Actions.delay(GraphicsConstants.disconnectionMessageDuration),
                Actions.run {
                    client.dispose()
                    this@CombatScreen.dispose()
                    gameApp.restart()
                }
            )
        )
    }

    override fun onShot(shooter: PlayerId, gridX: Int, gridY: Int, shotResult: ShotResult?) {
        if (shooter == playerId)
            playerShot(gridX, gridY, shotResult)
        else
            enemyShot(gridX, gridY, shotResult)
    }

    override fun onError(error: String?) {
        Gdx.app.log("Battleship", "Error: $error")
    }

    //--------------- end GameLogicListener methods ---------------


    private fun updateTime(delta: Float) {
        moveTime += delta

        val seconds = moveTime.toInt() % 60
        val minutes = moveTime.toInt() / 60

        val secStr = ( if (seconds < 10 ) { "0" } else "" ) + seconds
        val minStr = ( if (minutes < 10 ) { "0" } else "" ) + minutes

        timeLabel.setText("move time: ${minStr}:${secStr}")
    }
}
