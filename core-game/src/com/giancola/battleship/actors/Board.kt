package com.giancola.battleship.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.giancola.battleship.GameConstants
import com.giancola.battleship.GameConstants.TILE_SIZE
import com.giancola.battleship.GameConstants.TILE_SIZE_SMALL
import com.giancola.battleship.LayoutConstants
import com.giancola.battleship.screens.CombatScreen
import ktx.actors.onClick


abstract class Board(stage: Stage, val boardLayout: Rectangle, val tileSize: Float, val nRows: Int, val nCols: Int): Group() {

    val tiles: Array<Array<Tile>>

    init {
        this.setPosition(boardLayout.x, boardLayout.y)
        this.setSize(tileSize * nCols, tileSize * nRows)
        stage.addActor(this)

        tiles = Array(nRows) { i -> Array(nCols) { j ->
            Tile(i, j, tileSize).also {
                //it.setPosition(tileSize * i, tileSize * j)
                it.setPosition(this@Board.x + tileSize * i, this@Board.y + tileSize * j)
                it.setSize(tileSize, tileSize)

                //this.addActor(it)
                stage.addActor(it)
            }
        } }
    }
}



class PlacementBoard(stage: Stage, nRows: Int = GameConstants.N_ROWS, nCols: Int = GameConstants.N_COLS) :
    Board(stage, LayoutConstants.standard2worldCoords(LayoutConstants.placementBoard),
        TILE_SIZE, nRows, nCols), DropTarget {
    override var targetable = true
}



class CombatPlayerBoard(stage: Stage, nRows: Int = GameConstants.N_ROWS, nCols: Int = GameConstants.N_COLS):
    Board(stage, LayoutConstants.standard2worldCoords(LayoutConstants.combatPlayerBoard),
        TILE_SIZE_SMALL, nRows, nCols) { }


class CombatEnemyBoard(screen: CombatScreen, stage: Stage, nRows: Int = GameConstants.N_ROWS, nCols: Int = GameConstants.N_COLS) :
    Board(stage, LayoutConstants.standard2worldCoords(LayoutConstants.combatEnemyBoard),
        TILE_SIZE, nRows, nCols) {

    init {
        for (tileRow in tiles) {
            for (tile in tileRow) {
                tile.onClick {
                    screen.shoot(tile.iTile, tile.jTile)
                }
            }
        }
    }
}