package com.giancola.battleship

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound

object Sounds {
    val hit: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/hit.wav"))
    val miss: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/miss.wav"))
    val sink: Sound = Gdx.audio.newSound(Gdx.files.internal("sounds/sink.wav"))
}