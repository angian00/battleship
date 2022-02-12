package com.giancola.battleship

import com.badlogic.gdx.math.Rectangle
import com.giancola.battleship.GameConstants.WORLD_HEIGHT

object GameConstants {
    const val WORLD_WIDTH = 360.0f
    const val WORLD_HEIGHT = 640.0f

    const val N_ROWS = 9
    const val N_COLS = 6

    const val TILE_SIZE = 40.0f
}


object LayoutConstants {
    val playerBoard = Rectangle(0.0f, 94.0f, 360.0f, 240.0f)

    fun standard2worldCoords(layoutRect: Rectangle): Rectangle {
        val flippedY = WORLD_HEIGHT - layoutRect.y -layoutRect.height;
        return Rectangle(layoutRect.x, flippedY, layoutRect.width, layoutRect.height)
    }

}
