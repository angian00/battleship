package com.giancola.battleship.actors

import com.badlogic.gdx.scenes.scene2d.Group
import com.giancola.battleship.GameConstants
import com.giancola.battleship.GameConstants.TILE_SIZE
import com.giancola.battleship.LayoutConstants
import com.giancola.battleship.screens.CombatScreen


class CombatEnemyBoard(val screen: CombatScreen, val nRows: Int = GameConstants.N_ROWS, val nCols: Int = GameConstants.N_COLS): Group() {

    val tiles = Array(nRows) { i -> Array(nCols) { j ->
        Tile(i, j, TILE_SIZE).also {
            it.setPosition(TILE_SIZE * i, TILE_SIZE * j)
            it.setSize(TILE_SIZE, TILE_SIZE)

            this.addActor(it)
        }
    } }

    init {
        val boardLayout = LayoutConstants.standard2worldCoords(LayoutConstants.combatEnemyBoard)
        this.setPosition(boardLayout.x, boardLayout.y)
        this.setSize(TILE_SIZE * nCols, TILE_SIZE * nRows)
    }
}