package com.giancola.battleship.net

import com.giancola.battleship.gamelogic.PlayerData
import com.giancola.battleship.gamelogic.PlayerId
import com.giancola.battleship.gamelogic.ShotResult
import kotlinx.serialization.Serializable

@Serializable
sealed class RemoteCommand

@Serializable
class CommandLogin: RemoteCommand()

@Serializable
data class CommandPlaceShips(val playerData: PlayerData): RemoteCommand()

@Serializable
data class CommandShoot(val gridX: Int, val gridY: Int): RemoteCommand()


@Serializable
sealed class RemoteNotification

@Serializable
data class NotificationGameStarting(val playerId: PlayerId): RemoteNotification()

@Serializable
class NotificationGameStarted: RemoteNotification()

@Serializable
data class NotificationCombatStarted(val playerTurn: PlayerId, val playerNames: Map<PlayerId, String>): RemoteNotification()

@Serializable
data class NotificationShotPerformed(val shooter: PlayerId, val gridX: Int, val gridY: Int, val shotResult: ShotResult?): RemoteNotification()

@Serializable
data class NotificationGameFinished(val winner: PlayerId): RemoteNotification()




