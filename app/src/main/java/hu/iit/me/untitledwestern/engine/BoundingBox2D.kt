package hu.iit.me.untitledwestern.engine

import android.opengl.GLES30
import android.opengl.Matrix
import hu.iit.me.untitledwestern.engine.graph.ShaderProgram
import hu.iit.me.untitledwestern.engine.math.Vector2D
import hu.iit.me.untitledwestern.engine.math.Vector3D
import hu.iit.me.untitledwestern.engine.util.BufferUtil

class BoundingBox2D {
    private val AABB_POINTS_2D = 4
    var minpoint: Vector2D
    var maxpoint: Vector2D

    var bbPoints: ArrayList<Vector2D>
    private var boxHalfWidth: Float
    private var boxHalfHeight: Float

    private val transformationMatrix: FloatArray
    private val rotationMatrix: FloatArray

    var mEnabled: Boolean

    constructor(newMinPoint: Vector2D, newMaxPoint: Vector2D){
        boxHalfWidth = 0f
        boxHalfHeight = 0f

        mEnabled = true

        bbPoints = ArrayList(AABB_POINTS_2D)
        for (i in 0 until bbPoints.size){
            bbPoints[i] = Vector2D(0f, 0f)
        }

        transformationMatrix = FloatArray(16)
        rotationMatrix = FloatArray(16)

        minpoint = Vector2D(newMinPoint.x, newMinPoint.y)
        maxpoint = Vector2D(newMaxPoint.x, newMaxPoint.y)

        setUpBBPoints()
        searchMinMax()

        boxHalfWidth = (maxpoint.x - minpoint.x) / 2.0f
        boxHalfHeight = (maxpoint.y - minpoint.y) / 2.0f
    }

    fun setPoints(min: Vector2D, max: Vector2D){
        minpoint.set(min.x, min.y)
        maxpoint.set(max.x, max.y)

        setUpBBPoints()
        searchMinMax()

        boxHalfWidth = (maxpoint.x - minpoint.x) / 2.0f
        boxHalfHeight = (maxpoint.y - minpoint.y) / 2.0f
    }

    fun setUpBBPoints() {
        bbPoints[0].set(minpoint.x, minpoint.y)

        bbPoints[1].set(maxpoint.x, minpoint.y)

        bbPoints[2].set(maxpoint.x, maxpoint.y)

        bbPoints[3].set(minpoint.x, maxpoint.y)
    }

    fun searchMinMax() {
        var min = Vector2D(bbPoints[0].x, bbPoints[0].y)
        var max = Vector2D(bbPoints[0].x, bbPoints[0].y)

        for (i in 0 until AABB_POINTS_2D){
            if (bbPoints[i].x < min.x) {
                min.x = bbPoints[i].x
            }
            if (bbPoints[i].y < min.y) {
                min.y = bbPoints[i].y
            }

            if (bbPoints[i].x > max.x) {
                max.x = bbPoints[i].x
            }
            if (bbPoints[i].y > max.y) {
                max.y = bbPoints[i].y
            }
        }

        minpoint.set(min.x, min.y)

        maxpoint.set(max.x, max.y)
    }

    fun setIdentityForTransformation() {
        Matrix.setIdentityM(transformationMatrix, 0)
    }

    fun transformByScale(scale: Float) {
        Matrix.scaleM(transformationMatrix, 0, scale, scale, scale)

        for (i in 0 until AABB_POINTS_2D){
            var point = floatArrayOf(bbPoints[i].x, bbPoints[i].y, 0.0f, 0.0f)
            var newpoint = FloatArray(4)
            Matrix.multiplyMV(newpoint, 0, transformationMatrix, 0, point, 0)
            bbPoints[i].set(newpoint[0], newpoint[1]);
        }

        setUpBBPoints()
        searchMinMax()
    }

    fun transformByTranslate(translateVector: Vector2D) {
        Matrix.translateM(transformationMatrix, 0, translateVector.x, translateVector.y, 0f)

        for (i in 0 until AABB_POINTS_2D){
            var point = floatArrayOf(bbPoints[i].x, bbPoints[i].y, 0.0f, 0.0f)
            var newpoint = FloatArray(4)
            Matrix.multiplyMV(newpoint, 0, transformationMatrix, 0, point, 0)
            bbPoints[i].set(newpoint[0], newpoint[1]);
        }

        setUpBBPoints()
        searchMinMax()
    }

    fun transformByRotate(rotationAngle: Float) {
        var x = boxHalfWidth
        var y = boxHalfHeight
        Matrix.translateM(transformationMatrix, 0, x, y, 0f)
        Matrix.setRotateM(rotationMatrix, 0, rotationAngle, 0f, 0f, -1.0f)
        Matrix.multiplyMM(transformationMatrix, 0, transformationMatrix, 0, rotationMatrix, 0)
        Matrix.translateM(transformationMatrix, 0, -x, -y, 0f)

        for (i in 0 until AABB_POINTS_2D){
            var point = floatArrayOf(bbPoints[i].x, bbPoints[i].y, 0.0f, 0.0f)
            var newpoint = FloatArray(4)
            Matrix.multiplyMV(newpoint, 0, transformationMatrix, 0, point, 0)
            bbPoints[i].set(newpoint[0], newpoint[1]);
        }

        setUpBBPoints()
        searchMinMax()
    }

    fun draw(shaderProgram: ShaderProgram, projectionMatrix: FloatArray) {
        // TODO: Try out the bounding box!!!
        if (!mEnabled){
            return
        }
        var vertices = floatArrayOf(
            minpoint.x, minpoint.y, 0.0f,
            minpoint.x, maxpoint.y, 0.0f,
            maxpoint.x, maxpoint.y, 0.0f,
            maxpoint.x, minpoint.y, 0.0f,
            minpoint.x, minpoint.y, 0.0f
        )

        var vaos = IntArray(1)
        GLES30.glGenVertexArrays(1, vaos, 0)
        GLES30.glBindVertexArray(vaos[0])

        var vboID = IntArray(1)
        GLES30.glGenBuffers(1, vboID, 0)
        var positionBuffer = BufferUtil.createFloatBuffer(vertices)
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboID[0])
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, positionBuffer.capacity() * 4, positionBuffer, GLES30.GL_STATIC_DRAW)

        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, 0)

        shaderProgram.bind()
        shaderProgram.setUniform("projectionMatrix", projectionMatrix)

        var modelMatrix = FloatArray(16)
        shaderProgram.setUniform("modelMatrix", modelMatrix)

        // TODO: Rewrite the shader according to my own colormapping
        shaderProgram.setUniform4f("linecolor", floatArrayOf(0.8f, 0.4f, 0.4f, 1.0f))

        // render the VAO
        GLES30.glBindVertexArray(vaos[0])
        GLES30.glEnableVertexAttribArray(0)

        GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, 5)

        GLES30.glDisableVertexAttribArray(0)

        shaderProgram.unbind()
    }

    fun checkOverlapping(box: BoundingBox2D): Boolean {
        if (maxpoint.x < box.minpoint.x || minpoint.x > box.maxpoint.x) {
            return false
        }
        if (maxpoint.y < box.minpoint.y || minpoint.y > box.maxpoint.y) {
            return false
        }
        return true
    }
}