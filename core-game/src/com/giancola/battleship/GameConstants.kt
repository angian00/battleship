package com.giancola.battleship

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.giancola.battleship.GameConstants.WORLD_HEIGHT

object GameConstants {
    const val WORLD_WIDTH = 360f
    const val WORLD_HEIGHT = 640f

    const val N_ROWS = 10
    const val N_COLS = 10

    const val TILE_SIZE = 36f
    const val TILE_SIZE_SMALL = 18f
}


object LayoutConstants {
    val serverTextField = Rectangle(60f, 463f, 240f, 40f)
    val loginTextField = Rectangle(60f, 520f, 240f, 40f)
    val loginButton = Rectangle(60f, 580f, 240f, 50f)


    val placementBoard = Rectangle(0f, 85f, 360f, 360f)
    //val placementBoard = Rectangle(0f, 280f, 360f, 360f)  //DEBUG

    val placementShipPositions = listOf(
        Rectangle(16f, 462f, 180f, 36f),
        Rectangle(6f, 512f, 144f, 36f),
        Rectangle(230f, 462f, 108f, 36f),
        Rectangle(162f, 512f, 144f, 36f),
        Rectangle(282f, 512f, 72f, 36f)
    )

    val placementButton = Rectangle(60f, 584f, 240f, 50f)


    val combatPlayerBoard = Rectangle(0f, 85f, 180f, 180f)
    val combatEnemyBoard  = Rectangle(0f, 280f, 360f, 360f)

    val combatNamesLabel = Rectangle(185f, 85f, 170f, 60f)
    val combatFeedbackLabel = Rectangle(185f, 145f, 170f, 60f)
    val combatTurnLabel = Rectangle(185f, 205f, 170f, 40f)
    val combatTimeLabel = Rectangle(185f, 235f, 170f, 40f)



    fun standard2worldCoords(layoutRect: Rectangle): Rectangle {
        val flippedY = WORLD_HEIGHT - layoutRect.y -layoutRect.height
        return Rectangle(layoutRect.x, flippedY, layoutRect.width, layoutRect.height)
    }

}

object GraphicsConstants {
    const val movementAnimDuration = 0.25f

    val playerShipColor: Color = Color.GREEN
    val enemyShipColor: Color = Color.BLUE

    const val FONT_SIZE = 24
    const val FONT_SIZE_SMALL = 18

    val feedbackGoodColor: Color = Color.GREEN
    val feedbackNeutralColor: Color = Color.DARK_GRAY
    val feedbackBadColor: Color = Color.RED

    val disconnectionMessageDuration = 3.0f
    val disconnectionMessageText = "GAME DISCONNECTED"

    val messageFadeOutDuration = 2.0f
}