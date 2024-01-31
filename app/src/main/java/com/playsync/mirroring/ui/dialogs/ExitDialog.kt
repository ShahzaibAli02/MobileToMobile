package com.playsync.mirroring.ui.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.playsync.mirroring.databinding.DialogExitBinding
import com.playsync.mirroring.databinding.DialogStopMirroringBinding

class ExitDialog(val activity: Activity, val listener : () -> Unit) : AlertDialog(activity) {
    private lateinit var binding: DialogExitBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogExitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        with(binding){



            btnYes.setOnClickListener {
                listener.invoke()
                dismiss()
            }

            btnNo.setOnClickListener {
                dismiss()
            }
        }
    }

}