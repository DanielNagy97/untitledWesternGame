package hu.unimiskolc.iit.mobile.untitledwestern.application.westerngame.engine

import hu.unimiskolc.iit.mobile.untitledwestern.application.westerngame.Renderer

class C2DScene {
    var mLayers: ArrayList<C2DGraphicsLayer> = ArrayList()
    private var mName: String = ""
    private var mVisible: Boolean

    init {
        mName = "Sample Scene"
        mVisible = true
    }

    fun registerLayer(layer: C2DGraphicsLayer) {
        mLayers.add(layer)
    }

    fun render(renderer: Renderer) {
        if (!mVisible || mLayers.size == 0) {
            return
        }

        for (layer in mLayers) {
            layer.render(renderer)
        }
    }

    fun cleanup() {
        for (layer in mLayers) {
            layer.cleanup()
        }
        mLayers.clear()
    }

}