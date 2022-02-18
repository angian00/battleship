package com.giancola.battleship.actors

import com.badlogic.gdx.scenes.scene2d.Group
import com.giancola.battleship.GameConstants
import com.giancola.battleship.GameConstants.TILE_SIZE_SMALL
import com.giancola.battleship.LayoutConstants
import com.giancola.battleship.screens.CombatScreen


class CombatPlayerBoard(val screen: CombatScreen, val nRows: Int = GameConstants.N_ROWS, val nCols: Int = GameConstants.N_COLS): Group() {

    val tiles: Array<Array<Tile>>

    init {
        val boardLayout = LayoutConstants.standard2worldCoords(LayoutConstants.combatPlayerBoard)
        this.setPosition(boardLayout.x, boardLayout.y)
        this.setSize(TILE_SIZE_SMALL * nCols, TILE_SIZE_SMALL * nRows)

        tiles = Array(nRows) { i -> Array(nCols) { j ->
            Tile(i, j, TILE_SIZE_SMALL).also {
                it.setPosition(this@CombatPlayerBoard.x + TILE_SIZE_SMALL * i, this@CombatPlayerBoard.y + TILE_SIZE_SMALL * j)
                it.setSize(TILE_SIZE_SMALL, TILE_SIZE_SMALL)

                //this.addActor(it)
                screen.gameApp.stg.addActor(it)
            }
        } }
    }
}