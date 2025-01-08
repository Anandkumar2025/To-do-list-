package com.example.myapplication1

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils


@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        setContentView(R.layout.activity_splash_screen)

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        findViewById<View>(R.id.app_logo).startAnimation(fadeIn)
        findViewById<View>(R.id.tagline).startAnimation(fadeIn)
        findViewById<View>(R.id.app_title).startAnimation(fadeIn)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)


    }
}