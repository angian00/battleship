package com.giancola.battleship.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.giancola.battleship.GraphicsConstants
import com.giancola.battleship.LayoutConstants
import com.giancola.battleship.ui.Styles.labelStyle
import com.giancola.battleship.ui.Styles.labelStyleSmall


open class MessageLabel(stage: Stage, layoutRect: Rectangle, style: LabelStyle): Label("", style) {
    init {
        this.setPosition(layoutRect.x, layoutRect.y)
        this.setSize(layoutRect.width, layoutRect.height)
        this.setAlignment(Align.center)
        this.setWrap(true)
        this.color = Color.BLACK

        stage.addActor(this)
    }
}

class CombatFeedbackLabel(stage: Stage): MessageLabel(stage, LayoutConstants.standard2worldCoords(LayoutConstants.combatFeedbackLabel), labelStyleSmall)

class CombatTurnLabel(stage: Stage): MessageLabel(stage, LayoutConstants.standard2worldCoords(LayoutConstants.combatTurnLabel), labelStyle)

class CombatTimeLabel(stage: Stage): MessageLabel(stage, LayoutConstants.standard2worldCoords(LayoutConstants.combatTimeLabel), labelStyleSmall)

/*
class Message: Label {
    constructor(stage: Stage): super("", Styles.labelStyle) {
        this.width = stage.width
        this.height = stage.height
        this.setAlignment(Align.center)

        this.isVisible = false
        stage.addActor(this)
    }

    fun show(message: String) {
        this.setText(message)

        this.alpha = 1.0f
        this.isVisible = true
    }

    fun showAndFade(message: String) {
        show(message)
        this.addAction(Actions.fadeOut(messageFadeOutDuration)
            .then(Actions.hide()))
    }
}
*/
