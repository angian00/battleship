package com.giancola.battleship.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable


object Styles {
    val labelStyle: LabelStyle
    val labelStyleSmall: LabelStyle
    val buttonStyle: TextButton.TextButtonStyle

    init {
        val font = generateFont(24)
        val fontSmall = generateFont(18)

        labelStyle = LabelStyle(font, Color.WHITE)
        labelStyleSmall = LabelStyle(fontSmall, Color.WHITE)

        buttonStyle = TextButton.TextButtonStyle()
        buttonStyle.font = font
        buttonStyle.fontColor = Color.BLACK

        val buttonTexture = Texture("ui_button.png")
        val ninePatch = NinePatch(buttonTexture, 6, 6, 6, 6)
        val buttonDrawable = NinePatchDrawable(ninePatch)
        buttonStyle.up = buttonDrawable
        buttonStyle.down = buttonDrawable
    }


    private fun generateFont(fontSize: Int): BitmapFont {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/Charter.ttc"))
        val fontParam = FreeTypeFontGenerator.FreeTypeFontParameter()
        fontParam.size = fontSize
        fontParam.minFilter = Texture.TextureFilter.Linear
        fontParam.magFilter = Texture.TextureFilter.Linear

        val font = generator.generateFont(fontParam)
        generator.dispose()

        return font
    }
}
