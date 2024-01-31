package com.example.mobiletomobile.ui.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.mobiletomobile.R
import com.example.mobiletomobile.databinding.FeedbackDialogBinding

class FeedBackDialog(val activity: Activity): AlertDialog(activity) {

    lateinit var binding: FeedbackDialogBinding

    var feedbackEmail = "write feedback email"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FeedbackDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnFeedback.setOnClickListener {
                dismiss()
                try {

                    val feedbackText = editTextFeedback.text.toString()
                    if (feedbackText.isEmpty()) {
                        Toast.makeText(activity, "Feedback is required", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    Log.d("FEEDBACK", feedbackText)
                    val mailTo = "mailto:$feedbackEmail?&subject=" + Uri.encode(
                        "Feedback | ${
                            activity.resources.getString(R.string.app_name)
                        }"
                    ) + "&body=" + Uri.encode(
                        feedbackText
                    )
                    val emailIntent = Intent(Intent.ACTION_VIEW)
                    emailIntent.data = Uri.parse(mailTo)
                    activity.startActivity(emailIntent)


                } catch (e: Exception) {
                    println(e.message)
                }
            }
        }
    }
}