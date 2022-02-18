package com.giancola.battleship.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable


object Styles {
    val buttonStyle: TextButton.TextButtonStyle

    init {
        //val skin = Skin()

        buttonStyle = TextButton.TextButtonStyle()
        buttonStyle.font = generateFont()
        buttonStyle.fontColor = Color.BLACK

        val buttonTexture = Texture("ui_button.png")
        val ninePatch = NinePatch(buttonTexture, 6, 6, 6, 6)
        val buttonDrawable = NinePatchDrawable(ninePatch)
        buttonStyle.up = buttonDrawable
        buttonStyle.down = buttonDrawable
    }


    private fun generateFont(): BitmapFont {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/Charter.ttc"))
        val fontParam = FreeTypeFontGenerator.FreeTypeFontParameter()
        fontParam.size = 24
        fontParam.minFilter = Texture.TextureFilter.Linear
        fontParam.magFilter = Texture.TextureFilter.Linear

        val font = generator.generateFont(fontParam)
        generator.dispose()

        return font
    }
}
