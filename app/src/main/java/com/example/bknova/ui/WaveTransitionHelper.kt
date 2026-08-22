package com.example.bknova.ui

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator

object WaveTransitionHelper {

    fun startTransition(activity: Activity, targetIntent: Intent, onFinishCurrent: (() -> Unit)? = null) {
        val root = activity.findViewById<ViewGroup>(android.R.id.content)
        val waveView = WaveTransitionView(activity)
        root.addView(waveView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                waveView.progress = it.animatedValue as Float
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    targetIntent.putExtra("FROM_WAVE_TRANSITION", true)
                    activity.startActivity(targetIntent)
                    activity.overridePendingTransition(0, 0)
                    onFinishCurrent?.invoke() ?: activity.finish()
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            start()
        }
    }

    fun finishTransition(activity: Activity) {
        val root = activity.findViewById<ViewGroup>(android.R.id.content)
        val waveView = WaveTransitionView(activity)
        root.addView(waveView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        waveView.progress = 1f

        ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                waveView.progress = it.animatedValue as Float
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    root.removeView(waveView)
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            // Delay slightly to ensure activity is fully visible
            startDelay = 100
            start()
        }
    }
}
