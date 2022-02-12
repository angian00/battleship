package com.giancola.battleship

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.giancola.battleship.GameConstants.TILE_SIZE
import com.giancola.battleship.screens.PlacementScreen


class PlayerBoard(val screen: PlacementScreen, val nRows: Int = GameConstants.N_ROWS, val nCols: Int = GameConstants.N_COLS): Group() {
    val tiles = Array(nRows) { i -> Array(nCols) { j ->
        Tile(this, i, j).also {
            it.setPosition(TILE_SIZE * i, TILE_SIZE * j)
            it.setSize(TILE_SIZE, TILE_SIZE)
            this.addActor(it)
        }
    } }

    init {
        val boardLayout = LayoutConstants.standard2worldCoords(LayoutConstants.playerBoard)
        this.setPosition(boardLayout.x, boardLayout.y)
    }
}