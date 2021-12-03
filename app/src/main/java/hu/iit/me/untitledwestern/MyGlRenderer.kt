package hu.iit.me.untitledwestern

import android.content.Context
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import hu.iit.me.untitledwestern.engine.graph.ShaderProgram
import hu.iit.me.untitledwestern.engine.util.TextUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {
    lateinit var shaderProgram: ShaderProgram
    lateinit var lineShader: ShaderProgram

    val projectionMatrix = FloatArray(16)
    var viewMatrix = FloatArray(16)

    var ratio: Float = 0.0f

    var dummygame = DummyGame(context, this, 1f)

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(TextUtil.readFile(context, "shaders/vertexShader.vert"))
        shaderProgram.createFragmentShader(TextUtil.readFile(context, "shaders/fragmentShader.frag"))
        shaderProgram.bindAttribLocation("a_TexCoordinate", 0)
        shaderProgram.link()
        shaderProgram.createUniform("viewMatrix")
        shaderProgram.createUniform("worldMatrix")
        shaderProgram.createUniform("projectionMatrix")
        shaderProgram.createUniform("vColor")
        shaderProgram.createUniform("u_Texture")

        lineShader = ShaderProgram()
        lineShader.createVertexShader(TextUtil.readFile(context, "shaders/lineVertex.vert"))
        lineShader.createFragmentShader(TextUtil.readFile(context, "shaders/lineFragment.frag"))
        lineShader.link()
        lineShader.createUniform("projectionMatrix")
        lineShader.createUniform("modelMatrix")
        lineShader.createUniform("vColor")

        dummygame.init()
    }

    override fun onDrawFrame(gl: GL10) {
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT)

        //TODO: Make a better gameloop!!
        dummygame.updatePositions()
        dummygame.updateAnimations()
        dummygame.updateCameras()

        dummygame.sceneManager.render(this)
        for(plat in dummygame.platforms){
            plat.draw(this)
        }

        for(plat in dummygame.debugBoxes){
            plat.draw(this)
        }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)
        ratio = width.toFloat() / height.toFloat()

        //Matrix.orthoM(projectionMatrix,0, 0f, width.toFloat(), height.toFloat(),0f, 1f, 10000f)

        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 10000f)
    }

    fun cleanup() {
        shaderProgram.cleanup()
        lineShader.cleanup()
    }

}