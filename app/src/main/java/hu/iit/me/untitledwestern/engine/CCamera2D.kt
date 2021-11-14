package hu.iit.me.untitledwestern.engine

import android.opengl.Matrix
import hu.iit.me.untitledwestern.engine.math.Vector2D

class CCamera2D {
    var mPosition: Vector2D
    var mId: Int
    val transformationMatrix: FloatArray

    constructor(x: Float, y: Float, id: Int){
        mPosition = Vector2D(x, y)
        mId = id
        transformationMatrix = FloatArray(16)
    }

    fun moveLeft(value: Float){
        mPosition.x += value
    }

    fun moveRight(value: Float){
        mPosition.x -= value
    }

    fun moveUp(value: Float){
        mPosition.y += value
    }

    fun moveDown(value: Float){
        mPosition.x -= value
    }

    fun moveRelative(valueX: Float, valueY: Float){
        mPosition.x += valueX
        mPosition.y += valueY
    }

    fun setViewMatrix(viewMatrix: FloatArray) {
        Matrix.setLookAtM(viewMatrix, 0, 0.0f, 0.0f, 82f, 0.0f, 0.0f, 0.0f, 0.0f, 1f, 0.0f)
        //Matrix.setIdentityM(transformationMatrix, 0)
        Matrix.translateM(viewMatrix, 0, -mPosition.x, mPosition.y, 0f)

        //return transformationMatrix
    }

    fun setPosition(x: Float, y: Float){
        mPosition.set(x, y)
    }
}