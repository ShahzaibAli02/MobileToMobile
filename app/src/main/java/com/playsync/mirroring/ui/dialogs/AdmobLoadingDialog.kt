package com.playsync.mirroring.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.playsync.mirroring.databinding.DialogAdLoadingBinding

class AdmobLoadingDialog(context: Context) : Dialog(context) {

    var binding: DialogAdLoadingBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAdLoadingBinding.inflate(layoutInflater)
        binding?.apply {
            setContentView(root)
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }


    }
}