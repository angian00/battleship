package com.giancola.battleship.gamelogic

import kotlinx.serialization.Serializable

@Serializable
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

@Serializable
data class Coords(val x: Int, val y: Int)

@Serializable
data class ShipId(val shipType: ShipType, val prog: Int)

@Serializable
data class ShipPlacement(val from: Coords, val to: Coords)

@Serializable
data class ShotResult(val hit: Boolean, val sunkShipId: ShipId?, val sunkShipPlacement: ShipPlacement?)


@Serializable
class PlayerData(val name: String, private val shipProvision: Map<ShipType, Int>, private val nRows: Int, private val nCols: Int) {
    val shots: Array<Array<Boolean>> = Array(nRows) { _ -> Array(nCols) { _ -> false } }
    val shipPlacements: MutableMap<ShipId, ShipPlacement?> = mutableMapOf()


    init {
        for (shipEntry in shipProvision) {
            for (shipIndex in 0 until shipEntry.value) {
                val key = ShipId(shipEntry.key, shipIndex)
                //shipPlacements can already be assigned if de-serializing
                if (key !in shipPlacements)
                    shipPlacements[key] = null
            }
        }
    }

    fun checkEnemyShot(gridX: Int, gridY: Int, enemyShots: Array<Array<Boolean>>): ShotResult {
        var isHit = false
        var sunkShipId: ShipId? = null
        var sunkShipPlacement: ShipPlacement? = null

        enemyShots[gridX][gridY] = true
        for (shipId in shipPlacements.keys) {
            if (isShipOnTile(shipId, gridX, gridY)) {
                isHit = true

                if (isShipSunk(shipId, enemyShots)) {
                    sunkShipId = shipId
                    sunkShipPlacement = shipPlacements[shipId]
                }

                break
            }
        }

        return ShotResult(isHit, sunkShipId, sunkShipPlacement)
    }

    fun areAllShipsSunk(enemyShots: Array<Array<Boolean>>) = shipPlacements.keys.all { shipId -> isShipSunk(shipId, enemyShots) }


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


    override fun toString(): String {
        val sb = StringBuilder()

        sb.append("player [$name]\n")
/*
        sb.append("\t shipProvision: \n")
        for ((shipType, quantity) in shipProvision) {
            sb.append("\t\t ")
                .append(shipType.toString())
                .append(": ")
                .append(quantity)
                .append("\n")
        }
        sb.append("\n")
*/
        sb.append("\t shipPlacements: \n")
        for ((shipId, shipPlacement) in shipPlacements.entries) {
            sb.append("\t\t ")
                .append(shipId.toString())
                .append(": ")

            if (shipPlacement == null) {
                sb.append("<null> ")
            } else {
                sb.append(shipPlacement.from)
                    .append(" --> ")
                    .append(shipPlacement.to)
            }
                .append("\n")
        }
        sb.append("\n")

        return sb.toString()
    }
}

data class GameData(val player1: PlayerData, val player2: PlayerData, val turnPlayer1: Boolean = true)
