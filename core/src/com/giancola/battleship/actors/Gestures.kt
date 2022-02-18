package com.giancola.battleship.actors

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.giancola.battleship.GraphicsConstants.movementAnimDuration


interface DropTarget {
    var targetable: Boolean
}


fun Stage.getDropTargets() =
    this.getAllActors().filter {
        it is DropTarget
    }


class ShipGestureListener(private val ship: ShipActor) : ActorGestureListener() {
    private var isDragging = false
    private var grabOffsetX = 0f
    private var grabOffsetY = 0f
    private var startPositionX = 0f
    private var startPositionY = 0f

    private val shipCallbacks = ship


    interface ShipGestureCallbacks {
        fun onDragStart()
        fun onDragCanceled()
        fun onDrop()
        fun onLongPress()
    }

    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        //start dragging
        isDragging = true
        grabOffsetX = x
        grabOffsetY = y

        // store original position
        // in case actor needs to return to it later
        startPositionX = ship.x
        startPositionY = ship.y

        ship.toFront()

        // increase size; object appears larger when lifted by player
        ship.addAction(Actions.scaleTo(1.1f, 1.1f, 0.25f))

        shipCallbacks.onDragStart()
    }

    override fun pan(event: InputEvent?, x: Float, y: Float, deltaX: Float, deltaY: Float) {
        //deltaX and Y seem to be impacted by current ship rotation, so I account for that!
        val deltaPos = Vector2(deltaX, deltaY).rotate(ship.rotation)

        ship.moveBy(deltaPos.x, deltaPos.y)
    }


    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
        var dropTarget: DropTarget? = null

        if (!isDragging)
            return

        isDragging = false

        // keep track of distance to closest object
        var closestDistance = Float.MAX_VALUE

        for (targetActor in ship.stage.getDropTargets()) {
            val target = targetActor as DropTarget
            if (target.targetable && ship.overlaps(targetActor) ) {
                val currentDistance = Vector2.dst(ship.x, ship.y, targetActor.x, targetActor.y)
                if (currentDistance < closestDistance) {
                    dropTarget = target
                    closestDistance = currentDistance
                }
            }
        }

        if (dropTarget == null || (dropTarget !is PlacementBoard)) {
            ship.addAction(Actions.moveTo(startPositionX, startPositionY,2 * movementAnimDuration, Interpolation.pow3))
            shipCallbacks.onDragCanceled()

        } else {
            ship.addAction(Actions.scaleTo(1.00f, 1.00f, 0.25f))
            shipCallbacks.onDrop()
        }
    }

    override fun longPress(actor: Actor?, x: Float, y: Float): Boolean {
        ship.scaleX = 1.0f
        ship.scaleY = 1.0f
        shipCallbacks.onLongPress()

        isDragging = false

        return true
    }
}

