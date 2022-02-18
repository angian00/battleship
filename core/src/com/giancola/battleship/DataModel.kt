package com.giancola.battleship

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

class PlayerData(val name: String, nRows: Int, nCols: Int, shipProvision: Map<ShipType, Int>) {
    val shots: Array<Array<Boolean>>
    val shipPlacement: MutableMap<Pair<ShipType, Int>, Pair<Int, Int>?>


    init {
        this.shots = Array(nRows) { _ -> Array(nCols) { _ -> false } }
        this.shipPlacement = mutableMapOf()
        for (shipEntry in shipProvision) {
            for (shipIndex in 0 until shipEntry.value) {
                shipPlacement[Pair(shipEntry.key, shipIndex)] = null
            }
        }
    }

    fun allShipsPlaced(): Boolean =
        shipPlacement.values.all {
            it != null
        }
}

data class GameData(val player1: PlayerData, val player2: PlayerData, val turnPlayer1: Boolean = true)
