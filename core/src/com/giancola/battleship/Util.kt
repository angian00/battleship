package com.giancola.battleship

import com.badlogic.gdx.graphics.Color
import kotlin.random.Random

val rnd = Random.Default

fun randomColor(): Color = Color(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat(), 1.0f)
