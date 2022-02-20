package com.giancola.battleship

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

data class ShipId(val shipType: ShipType, val prog: Int)
data class ShipPlacement(val from: GridPoint2, val to: GridPoint2)

data class ShotResult(val hit: Boolean, val sunkShip: ShipId?)


class PlayerData(val name: String, shipProvision: Map<ShipType, Int>, nRows: Int = N_ROWS, nCols: Int = N_COLS) {
    val shots: Array<Array<Boolean>>
    //val enemyShots: Array<Array<Boolean>>
    val shipPlacements: MutableMap<ShipId, ShipPlacement?>


    init {
        this.shots = Array(nRows) { _ -> Array(nCols) { _ -> false } }
        //this.enemyShots = Array(nRows) { _ -> Array(nCols) { _ -> false } }
        this.shipPlacements = mutableMapOf()
        for (shipEntry in shipProvision) {
            for (shipIndex in 0 until shipEntry.value) {
                shipPlacements[ShipId(shipEntry.key, shipIndex)] = null
            }
        }
    }

    fun checkEnemyShot(gridX: Int, gridY: Int, enemyShots: Array<Array<Boolean>>): ShotResult {
        var isHit = false
        var sunkShip: ShipId? = null

        enemyShots[gridX][gridY] = true
        for (shipId in shipPlacements.keys) {
            if (isShipOnTile(shipId, gridX, gridY)) {
                isHit = true

                if (isShipSunk(shipId, enemyShots))
                    sunkShip = shipId

                break
            }
        }

        return ShotResult(isHit, sunkShip)
    }


    private fun isShipOnTile(shipId: ShipId, gridX: Int, gridY: Int): Boolean {
        val shipPlacement = shipPlacements[shipId] ?: return false
        val startTile = shipPlacement.from
        val endTile = shipPlacement.to

        return if (startTile.x == endTile.x) {
            (gridX == startTile.x) && (gridY >= startTile.y) && (gridY <= endTile.y)
        } else {
            // (startTile.y == endTile.y)
            (gridY == startTile.y) && (gridX >= startTile.x) && (gridX <= endTile.x)
        }
    }


    private fun isShipSunk(shipId: ShipId, enemyShots: Array<Array<Boolean>>): Boolean {
        val shipPlacement = shipPlacements[shipId] ?: return false
        val startTile = shipPlacement.from
        val endTile = shipPlacement.to

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
