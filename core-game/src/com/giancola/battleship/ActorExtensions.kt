package com.giancola.battleship.actors

import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import kotlin.math.round


fun Rectangle.roundCoords(): Rectangle = Rectangle(
        round(this.x),
        round(this.y),
        round(this.width),
        round(this.height)
)

fun Actor.getBoundingBox(): Rectangle {
    val vertices = floatArrayOf(0f, 0f, this.width, 0f, this.width, this.height, 0f, this.height)
    val boundaryPolygon = Polygon(vertices)
    boundaryPolygon.setPosition(this.x, this.y)
    boundaryPolygon.setOrigin(this.originX, this.originY)
    boundaryPolygon.rotation = this.rotation

    return boundaryPolygon.boundingRectangle.roundCoords()
}

fun Actor.overlaps(other: Actor): Boolean  = this.getBoundingBox().overlaps(other.getBoundingBox())


fun Actor.getDescendants(): List<Actor> {
    val res: MutableList<Actor> = mutableListOf()
    if (this is Group) {
        for (a in this.children) {
            res += a
            res += a.getDescendants()
        }
    }

    return res
}



fun Stage.getAllActors(): List<Actor> {
    val res: MutableList<Actor> = mutableListOf()

    for  (a in this.actors) {
        res += a
        res += a.getDescendants()
    }

    return res
}
