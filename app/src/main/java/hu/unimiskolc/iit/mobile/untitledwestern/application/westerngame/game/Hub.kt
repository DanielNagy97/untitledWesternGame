package hu.unimiskolc.iit.mobile.untitledwestern.application.westerngame.game

import hu.unimiskolc.iit.mobile.untitledwestern.application.westerngame.engine.C2DGraphicsLayer
import hu.unimiskolc.iit.mobile.untitledwestern.application.westerngame.engine.GameObject
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

class Hub(
    var hubLayer: C2DGraphicsLayer,
    var scoreNumbers: ArrayList<GameObject>,
    var hearts: ArrayList<GameObject>,
    var gameOverText: GameObject,
    var startGameText: GameObject,
    var scale: Float
) {
    private var blinkTime: Float = 0f

    init {
        calculateLayout()
    }

    fun calculateLayout() {
        // x position
        scoreNumbers.last().position.x = hubLayer.mCamera!!.viewPort.minpoint.x + 2 * scale
        for (i in scoreNumbers.size - 1 downTo 1) {
            scoreNumbers[i - 1].position.x = scoreNumbers[i].getBoundingBox().maxpoint.x + 2 * scale
        }
        // y position
        for (num in scoreNumbers) {
            num.position.y =
                hubLayer.mCamera!!.viewPort.maxpoint.y - (num.getBoundingBox().maxpoint.y - num.getBoundingBox().minpoint.y) - 2 * scale
        }

        // x position
        hearts[0].position.x =
            hubLayer.mCamera!!.viewPort.maxpoint.x - (hearts[0].getBoundingBox().maxpoint.x - hearts[0].getBoundingBox().minpoint.x) - 2 * scale
        for (i in 1 until hearts.size) {
            hearts[i].position.x =
                hearts[i - 1].getBoundingBox().minpoint.x - (hearts[i].getBoundingBox().maxpoint.x - hearts[i].getBoundingBox().minpoint.x) - 2 * scale
        }
        // y position
        for (heart in hearts) {
            heart.position.y =
                hubLayer.mCamera!!.viewPort.maxpoint.y - (heart.getBoundingBox().maxpoint.y - heart.getBoundingBox().minpoint.y) - 2 * scale
        }

        positionTextCenter(gameOverText)
        gameOverText.visible = false

        positionTextCenter(startGameText)
    }

    private fun positionTextCenter(textObject: GameObject) {
        textObject.position.x =
            0f - (textObject.getBoundingBox().maxpoint.x - textObject.getBoundingBox().minpoint.x) / 2
        textObject.position.y =
            0f - (textObject.getBoundingBox().maxpoint.y - textObject.getBoundingBox().minpoint.y) / 2
    }

    fun blinkStartText(dt: Float) {
        blinkTime += dt
        val isVisible = floor(blinkTime) % 2 != 0f
        startGameText.visible = isVisible
    }

    fun updateScoreBoard(score: Int) {
        var nDigits: Int = (floor(log10(score.toDouble())) + 1).toInt()
        if (nDigits < 0) {
            nDigits = 0
        }
        for (i in 1..nDigits) {
            val nthNumber: Int = floor(score / 10.0.pow((i - 1).toDouble()) % 10).toInt()
            hubLayer.mObjectList[i - 1].currSprite = nthNumber
        }
    }

    fun updateHearts(lives: Int) {
        for (i in 0 until hearts.size) {
            hearts[i].visible = i < lives
        }
    }
}