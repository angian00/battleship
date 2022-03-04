package com.giancola.battleship.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.giancola.battleship.GraphicsConstants.FONT_SIZE
import com.giancola.battleship.GraphicsConstants.FONT_SIZE_SMALL


object Styles {
    val labelStyle: LabelStyle
    val labelStyleSmall: LabelStyle
    val textFieldStyle: TextField.TextFieldStyle
    val buttonStyle: TextButton.TextButtonStyle


    init {
        val font = generateFont(FONT_SIZE)
        val fontSmall = generateFont(FONT_SIZE_SMALL)
        val uniformTexture = Texture("white.png")

        labelStyle = LabelStyle(font, Color.WHITE)
        labelStyleSmall = LabelStyle(fontSmall, Color.WHITE)


        textFieldStyle = TextField.TextFieldStyle()
        textFieldStyle.font = font
        textFieldStyle.fontColor = Color.DARK_GRAY
        textFieldStyle.cursor = TextureRegionDrawable(TextureRegion(uniformTexture)).tint(Color.DARK_GRAY)

        textFieldStyle.disabledFontColor = Color.GRAY

        val tfTexture = Texture("ui_textfield.png")
        val tfNinePatch = NinePatch(tfTexture, 6, 6, 6, 6)
        val tfDrawable = NinePatchDrawable(tfNinePatch)
        textFieldStyle.background = tfDrawable

        val tfDisabledTexture = Texture("ui_textfield_disabled.png")
        val tfDisabledNinePatch = NinePatch(tfDisabledTexture, 6, 6, 6, 6)
        val tfDisabledDrawable = NinePatchDrawable(tfDisabledNinePatch)
        textFieldStyle.disabledBackground = tfDisabledDrawable


        buttonStyle = TextButton.TextButtonStyle()
        buttonStyle.font = font
        buttonStyle.fontColor = Color.BLACK

        val buttonTexture = Texture("ui_button.png")
        val buttonNinePatch = NinePatch(buttonTexture, 6, 6, 6, 6)
        val buttonDrawable = NinePatchDrawable(buttonNinePatch)
        buttonStyle.up = buttonDrawable
        buttonStyle.down = buttonDrawable

        val buttonDisabledTexture = Texture("ui_button_disabled.png")
        val buttonDisabledNinePatch = NinePatch(buttonDisabledTexture, 6, 6, 6, 6)
        val buttonDisabledDrawable = NinePatchDrawable(buttonDisabledNinePatch)
        buttonStyle.disabled = buttonDisabledDrawable
        buttonStyle.disabledFontColor = Color.GRAY
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
