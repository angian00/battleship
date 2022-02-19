package com.giancola.battleship

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.GridPoint2
import com.giancola.battleship.GameConstants.N_COLS
import com.giancola.battleship.GameConstants.N_ROWS

data class ShipType(val name: String, val width: Int, val height: Int)

val Carrier = ShipType("Carrier", 5, 1)
val Battleship = ShipType("Battleship", 4, 1)
val Cruiser = ShipType("Cruiser", 3, 1)
val Submarine = ShipType("Submarine", 3, 1)
val Destroyer = ShipType("Destroyer", 2, 1)

data class ShipSetup(val shipMap: Map<ShipType, Int>)

object ShipFactory {
    val standardSetup = mapOf(
        Carrier to 1,
        Battleship to 1,
        Cruiser to 1,
        Submarine to 1,
        Destroyer to 1
    )

    val debugSetup = mapOf(
        Carrier to 1
    )
}


data class ShotResult(val hit: Boolean, val sunkShip: Pair<ShipType, Int>?)


class PlayerData(val name: String, shipProvision: Map<ShipType, Int>, nRows: Int = N_ROWS, nCols: Int = N_COLS) {
    val shots: Array<Array<Boolean>>
    //val enemyShots: Array<Array<Boolean>>
    val shipPlacement: MutableMap<Pair<ShipType, Int>, Pair<GridPoint2, GridPoint2>?>


    init {
        this.shots = Array(nRows) { _ -> Array(nCols) { _ -> false } }
        //this.enemyShots = Array(nRows) { _ -> Array(nCols) { _ -> false } }
        this.shipPlacement = mutableMapOf()
        for (shipEntry in shipProvision) {
            for (shipIndex in 0 until shipEntry.value) {
                shipPlacement[Pair(shipEntry.key, shipIndex)] = null
            }
        }
    }

    fun checkEnemyShot(gridX: Int, gridY: Int, enemyShots: Array<Array<Boolean>>): ShotResult {
        var isHit = false
        var sunkShip: Pair<ShipType, Int>? = null

        enemyShots[gridX][gridY] = true
        for (shipId in shipPlacement.keys) {
            if (isShipOnTile(shipId, gridX, gridY)) {
                isHit = true

                if (isShipSunk(shipId, enemyShots))
                    sunkShip = shipId

                break
            }
        }

        return ShotResult(isHit, sunkShip)
    }


    private fun isShipOnTile(shipId: Pair<ShipType, Int>, gridX: Int, gridY: Int): Boolean {
        val shipPlace = shipPlacement[shipId] ?: return false
        val startTile = shipPlace.first
        val endTile = shipPlace.second

        return if (startTile.x == endTile.x) {
            (gridX == startTile.x) && (gridY >= startTile.y) && (gridY <= endTile.y)
        } else {
            // (startTile.y == endTile.y)
            (gridY == startTile.y) && (gridX >= startTile.x) && (gridX <= endTile.x)
        }
    }


    private fun isShipSunk(shipId: Pair<ShipType, Int>, enemyShots: Array<Array<Boolean>>): Boolean {
        val shipPlace = shipPlacement[shipId] ?: return false
        val startTile = shipPlace.first
        val endTile = shipPlace.second

        if (startTile.x == endTile.x) {
            for (y in startTile.y..endTile.y) {
                if (!enemyShots[startTile.x][y])
                    return false
            }

            return true

        } else {
            // (startTile.y == endTile.y)
            for (x in startTile.x..endTile.x) {
                if (!enemyShots[x][startTile.y])
                    return false
            }

            return true
        }

    }

}

data class GameData(val player1: PlayerData, val player2: PlayerData, val turnPlayer1: Boolean = true)
