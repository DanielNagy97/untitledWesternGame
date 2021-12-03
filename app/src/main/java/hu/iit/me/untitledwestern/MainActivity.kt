package hu.iit.me.untitledwestern

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.View

class MainActivity : Activity() {
    private lateinit var gLView: GLSurfaceView
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gLView = MyGLSurfaceView(this)
        setContentView(gLView)


    }

    override fun onPause() {
        super.onPause()
        gLView.onPause()
    }

    override fun onResume() {
        super.onResume()
        gLView.onResume()
    }
}
