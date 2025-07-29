package com.livechatinc.chatsdk.src.core.managers

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat

internal class WindowInsetManager(private val rootView: View) {
    private var insetsAnimationRunning = false

    fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            handleStaticInsets(v, insets)
            insets
        }

        setupKeyboardAnimationCallback()
    }

    private fun handleStaticInsets(v: View, insets: WindowInsetsCompat) {
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

        if (ime.bottom == 0 && !insetsAnimationRunning) {
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        }
    }

    private fun setupKeyboardAnimationCallback() {
        ViewCompat.setWindowInsetsAnimationCallback(
            rootView,
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
                override fun onPrepare(animation: WindowInsetsAnimationCompat) {
                    insetsAnimationRunning = true

                    super.onPrepare(animation)
                }

                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: List<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    applyAnimatedInsets(insets)
                    return insets
                }

                private fun applyAnimatedInsets(insets: WindowInsetsCompat) {
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

                    rootView.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        maxOf(ime.bottom, systemBars.bottom)
                    )
                }

                override fun onEnd(animation: WindowInsetsAnimationCompat) {
                    insetsAnimationRunning = false

                    super.onEnd(animation)
                }
            }
        )
    }
}
