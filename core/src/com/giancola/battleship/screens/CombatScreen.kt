package com.giancola.battleship.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.giancola.battleship.BattleshipGame
import com.giancola.battleship.GameConstants.TILE_SIZE_SMALL
import com.giancola.battleship.GraphicsConstants.feedbackBadColor
import com.giancola.battleship.GraphicsConstants.feedbackGoodColor
import com.giancola.battleship.GraphicsConstants.feedbackNeutralColor
import com.giancola.battleship.GraphicsConstants.shipHealthyColor
import com.giancola.battleship.PlayerData
import com.giancola.battleship.ShipId
import com.giancola.battleship.ShotResult
import com.giancola.battleship.actors.CombatEnemyBoard
import com.giancola.battleship.actors.CombatPlayerBoard
import com.giancola.battleship.actors.ShipActor
import com.giancola.battleship.gamelogic.GameLogic
import com.giancola.battleship.gamelogic.GameLogicListener
import com.giancola.battleship.gamelogic.LocalGameLogic
import com.giancola.battleship.gamelogic.PlayerId
import com.giancola.battleship.ui.CombatFeedbackLabel
import com.giancola.battleship.ui.CombatTimeLabel
import com.giancola.battleship.ui.CombatTurnLabel
import ktx.app.KtxScreen


class CombatScreen(val gameApp: BattleshipGame, val playerData: PlayerData) : KtxScreen, InputAdapter(), GameLogicListener {
    private val playerShipActors: Map<ShipId, ShipActor>

    private val bkg: Image
    private val playerBoard: CombatPlayerBoard
    private val enemyBoard: CombatEnemyBoard

    private val feedbackLabel: Label
    private val turnLabel: Label
    private val timeLabel: Label

    val game: GameLogic = LocalGameLogic()
    val playerId: PlayerId
    var moveTime: Float

    init {
        val stage = gameApp.stg

        gameApp.im.addProcessor(this)

        bkg = Image(Texture("combat_background.png"))
        stage.addActor(bkg)

        playerBoard = CombatPlayerBoard(stage)
        enemyBoard = CombatEnemyBoard(this, stage)

        feedbackLabel = CombatFeedbackLabel(stage)
        feedbackLabel.color = feedbackNeutralColor
        feedbackLabel.setText("Waiting for game to start")
        turnLabel = CombatTurnLabel(stage)
        turnLabel.setText("...")
        timeLabel = CombatTimeLabel(stage)
        timeLabel.setText("")


        playerShipActors = initShips()

        val regPlayerId = game.registerListener(this, playerData)
        require (regPlayerId != null)
        playerId = regPlayerId

        moveTime = 0f

        //randomEnemyShots() //DEBUG

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

            shipActor.color = shipHealthyColor
            gameApp.stg.addActor(shipActor)

            mutableMap[shipId] = shipActor
        }

        return mutableMap.toMap()
    }


    fun shoot(gridX: Int, gridY: Int) {
        val shotResult = game.shoot(playerId, gridX, gridY) ?: return

        if (shotResult.hit) {
            Gdx.app.log("Battleship", "Enemy ship hit: $gridX, $gridY !")
            feedbackLabel.color = feedbackGoodColor
            feedbackLabel.setText("Enemy ship hit !")
            enemyBoard.tiles[gridX][gridY].setHit()
        } else {
            Gdx.app.log("Battleship", "Player shot missed")
            feedbackLabel.color = feedbackNeutralColor
            feedbackLabel.setText("Shot missed")
            enemyBoard.tiles[gridX][gridY].setMissed()
        }

        if (shotResult.sunkShip != null) {
            Gdx.app.log("Battleship","Enemy [${shotResult.sunkShip.shipType}] sunk !!")
            feedbackLabel.color = feedbackGoodColor
            feedbackLabel.setText("Enemy [${shotResult.sunkShip.shipType.name}] sunk !!")
        }

        moveTime = 0f
        turnLabel.setText("Enemy's turn")
    }


    //--------------- GameLogicListener methods ---------------
    override fun onEnemyShot(gridX: Int, gridY: Int, shotResult: ShotResult) {
        if (shotResult.hit) {
            Gdx.app.log("Battleship", "Player ship hit: $gridX, $gridY !")
            feedbackLabel.color = feedbackBadColor
            feedbackLabel.setText("Player ship hit: $gridX, $gridY !")
            playerBoard.tiles[gridX][gridY].setHit()
        } else {
            Gdx.app.log("Battleship", "Enemy shot missed")
            feedbackLabel.color = feedbackNeutralColor
            feedbackLabel.setText("Enemy shot missed")
            playerBoard.tiles[gridX][gridY].setMissed()
        }

        if (shotResult.sunkShip != null) {
            Gdx.app.log("Battleship","Player ship ${shotResult.sunkShip.shipType} sunk!!")
            feedbackLabel.color = feedbackBadColor
            feedbackLabel.setText("Player ship sunk [${shotResult.sunkShip.shipType.name}] !!")
        }

        moveTime = 0f
        turnLabel.setText("Your turn")
    }

    override fun onGameStarted(whoseTurn: PlayerId) {
        val playerStr = when (whoseTurn) { playerId -> "Your"; else -> "Enemy's" }
        Gdx.app.log("Battleship", "Game started - $playerStr turn")
        feedbackLabel.color = feedbackNeutralColor
        feedbackLabel.setText("Game started")

        moveTime = 0f
        turnLabel.setText("$playerStr turn")
    }

    override fun onGameFinished(winner: PlayerId) {
        val playerStr = when (winner) { playerId -> "You"; else -> "Enemy" }
        Gdx.app.log("Battleship", "Game finished - $playerStr won !")
        feedbackLabel.color = if (winner == playerId) feedbackGoodColor else feedbackBadColor
        feedbackLabel.setText("Game finished - $playerStr won !")

        moveTime = 0f
        turnLabel.setText("")
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
