package com.giancola.battleship

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.giancola.battleship.App
import com.giancola.battleship.GameConstants

object DesktopLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.width = GameConstants.WORLD_WIDTH.toInt()
        config.height = GameConstants.WORLD_HEIGHT.toInt()
        config.title = "Battleship"

        LwjglApplication(App(), config)
    }
}