package com.example.okey101

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import kotlin.random.Random

class PunishmentWheelDialog(private val context: Context, private val loserName: String) {

    fun show() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_dialog_wheel, null)
        val wheelView = dialogView.findViewById<PunishmentWheelView>(R.id.wheelView)
        val btnSpin = dialogView.findViewById<Button>(R.id.btnSpin)
        val tvResult = dialogView.findViewById<TextView>(R.id.tvResult)
        val tvDescription = dialogView.findViewById<TextView>(R.id.tvDescription)
        
        tvDescription.text = "Kaybeden: $loserName"

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()
        
        // Transparent background for rounded corners
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnSpin.setOnClickListener {
            btnSpin.isEnabled = false
            tvResult.visibility = android.view.View.INVISIBLE
            
            // Random angle: Min 5 full spins (5 * 360) + random offset
            val currentRotation = wheelView.rotation
            val randomAngle = Random.nextFloat() * 360f
            val targetRotation = currentRotation + (360f * 5) + randomAngle

            val animator = ObjectAnimator.ofFloat(wheelView, "rotation", currentRotation, targetRotation)
            animator.duration = 4000 // 4 seconds
            animator.interpolator = DecelerateInterpolator()
            
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    val resultItem = wheelView.getItemAtAngle(targetRotation)
                    tvResult.text = "Kaybeden $loserName,\n$resultItem Ismarlıyor! :)"
                    tvResult.visibility = android.view.View.VISIBLE
                    btnSpin.isEnabled = true
                    btnSpin.text = "TEKRAR ÇEVİR"
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            
            animator.start()
        }

        dialog.show()
    }
}
