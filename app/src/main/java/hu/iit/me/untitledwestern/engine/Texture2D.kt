package hu.iit.me.untitledwestern.engine

import android.graphics.Bitmap
import android.opengl.GLES30
import android.opengl.GLUtils
import android.opengl.Matrix
import hu.iit.me.untitledwestern.MyGLRenderer
import hu.iit.me.untitledwestern.engine.graph.Mesh
import hu.iit.me.untitledwestern.engine.math.Vector2D

class Texture2D {
    private lateinit var mesh: Mesh
    private var width: Int
    private var height: Int
    private val transformationMatrix: FloatArray
    private val rotationMatrix = FloatArray(16)
    var textureId: Int
    var position: Vector2D
    var scale: Float
    var rotationAngle: Float
    private val color = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)

    init{
        textureId = -1
        scale = 0.04f
        width = 0
        height = 0
        position = Vector2D(0.0f, 0.0f)
        rotationAngle = 0f
        transformationMatrix = FloatArray(16)
    }

    private fun bind(){
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
    }

    fun createTexture(bitmap: Bitmap): Boolean{
        loadTexture(bitmap)

        var positions = floatArrayOf(
            0.0f, height.toFloat(), 0.0f,
            width.toFloat(), height.toFloat(), 0.0f,
            width.toFloat(), 0.0f, 0.0f,
            width.toFloat(), 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, height.toFloat(), 0.0f,
        )

        var textCoords =  floatArrayOf(
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f
        )

        mesh = Mesh(positions, textCoords, this, 6)
        return true
    }

    private fun loadTexture(bitmap: Bitmap){
        width = bitmap.width
        height = bitmap.height

        var textures: IntArray = IntArray(1)
        GLES30.glGenTextures(1, textures, 0)
        textureId = textures[0]
        bind()

        GLES30.glPixelStorei(GLES30.GL_UNPACK_ALIGNMENT, 1)

        // Enable alpha
        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);

        // Set filtering
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_NEAREST
        )
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MAG_FILTER,
            GLES30.GL_NEAREST
        )

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)

        // Recycle the bitmap, since its data has been loaded into OpenGL.
        bitmap.recycle()
    }

    private fun getWorldMatrix(): FloatArray {
        Matrix.setIdentityM(transformationMatrix, 0)
        Matrix.translateM(transformationMatrix, 0, position.x, position.y, 0f)
        Matrix.scaleM(transformationMatrix, 0, scale, scale, scale)

        var x = 0.5f * width
        var y = 0.5f * height
        Matrix.translateM(transformationMatrix, 0, x, y, 0f)
        Matrix.setRotateM(rotationMatrix, 0, rotationAngle, 0f, 0f, -1.0f)
        Matrix.multiplyMM(transformationMatrix, 0, transformationMatrix, 0, rotationMatrix, 0)
        Matrix.translateM(transformationMatrix, 0, -x, -y, 0f)

        return transformationMatrix
    }

    private fun draw(renderer: MyGLRenderer){
        renderer.shaderProgram.bind()

        renderer.shaderProgram.setUniform("projectionMatrix", renderer.projectionMatrix)
        renderer.shaderProgram.setUniform("u_Texture", 0);
        renderer.shaderProgram.setUniform4f("vColor", color)
        renderer.shaderProgram.setUniform("worldMatrix", getWorldMatrix())
        renderer.shaderProgram.setUniform("viewMatrix", renderer.viewMatrix)

        mesh.draw(renderer.shaderProgram)

        renderer.shaderProgram.unbind()
    }

    fun draw(renderer: MyGLRenderer, position: Vector2D, scale:Float, rotationAngle:Float){
        this.position = position
        this.scale = scale
        this.rotationAngle = rotationAngle

        this.draw(renderer)
    }
}