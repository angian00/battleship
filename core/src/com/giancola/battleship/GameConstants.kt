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
    val combatEnemyBoard = Rectangle(0f, 280f, 360f, 360f)

    fun standard2worldCoords(layoutRect: Rectangle): Rectangle {
        val flippedY = WORLD_HEIGHT - layoutRect.y -layoutRect.height
        return Rectangle(layoutRect.x, flippedY, layoutRect.width, layoutRect.height)
    }

}

object GraphicsConstants {
    const val movementAnimDuration = 0.25f

    val shipHealthyColor: Color = Color.GREEN
    val shipDamagedColor: Color = Color.ORANGE
    val shipSunkColor: Color = Color.RED
}