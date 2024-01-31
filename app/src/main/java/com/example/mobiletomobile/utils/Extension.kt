package com.example.mobiletomobile.utils


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.StatFs
import android.os.SystemClock
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.airbnb.lottie.BuildConfig
import com.airbnb.lottie.LottieAnimationView
import com.example.mobiletomobile.BuildConfig.APPLICATION_ID
import com.example.mobiletomobile.R
import com.example.mobiletomobile.ui.base.SplashActivity
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Random





var mLastClickTime = 0L
var isProcessingClick = false

fun singleClick(listener: () -> Unit) {
    if (isProcessingClick) {
        return
    }

    isProcessingClick = true
    val currentTime = SystemClock.elapsedRealtime()

    if (currentTime - mLastClickTime >= 500) { // 1000 = 1 second
        mLastClickTime = currentTime
        listener.invoke()
    }

    // Reset the flag after a short delay to enable subsequent clicks
    afterDelay(500) {
        isProcessingClick = false
    }
}

fun Context.getLoadingDialog(): Dialog {
    val dialog = Dialog(this)
    dialog.setContentView(R.layout.dialog_loading)
    dialog.setCancelable(false)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    return dialog
}

fun saveIsProInSharedPreferences(isPro: Boolean, activity: Activity) {
    val sharedPreferences = activity.getSharedPreferences(
        "${APPLICATION_ID}SharedPreferences",
        Context.MODE_PRIVATE
    )
    sharedPreferences.edit().putBoolean("isPro", isPro).apply()
}


fun getRandomString(length: Int, includeLowerCase: Boolean = false): String {
    val allowedChars = (('A'..'Z') + ('0'..'9')).toMutableList()
    if (includeLowerCase) allowedChars += ('a'..'z')

    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

fun getRandomStringForRoom(length: Int): String {
    val allowedChars = ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

fun getCurrentDateAndTime(): String {
    val calendar: Calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    val date: String = dateFormat.format(calendar.time)
    val time: String = timeFormat.format(calendar.time)

    return "$date $time"
}

fun Activity.showSnackBar(
    message: String,
    actionText: String,
    actionListener: () -> Unit
) {
    val snackBar = Snackbar.make(
        this.findViewById(android.R.id.content),  // Replace with the root view of your activity
        message,
        Snackbar.LENGTH_SHORT
    )

    // Optional: Set an action to perform when the user clicks the action button on the SnackBar
    snackBar.setAction(actionText) {
        actionListener.invoke()
        snackBar.dismiss()
    }

    // Optional: Customize the appearance or behavior of the SnackBar, e.g., changing the text color
    // snackBar.setActionTextColor(resources.getColor(R.color.colorAccent))

    snackBar.show()
}


fun Context.isNetworkAvailable(): Boolean {

    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }

                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }

                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
    } else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}


@Suppress("DEPRECATION")
fun Context.verifyInstallerId(): Boolean {

    return if (BuildConfig.DEBUG) {
        true
    } else {
        val validInstallers: List<String> =
            ArrayList(listOf("com.android.vending", "com.google.android.feedback"))
        val installer = this.packageManager.getInstallerPackageName(this.packageName)
        return installer != null && validInstallers.contains(installer)
    }
}

fun Activity.checkIfUserIsPro(): Boolean {
    val sharedPreferences = this.getSharedPreferences(
        "${BuildConfig.APPLICATION_ID}SharedPreferences",
        Context.MODE_PRIVATE
    )
    return sharedPreferences.getBoolean("isPro", false)

}

fun afterDelay(delayInTime: Long, listener: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        listener.invoke()
    }, delayInTime)
}

fun Context.openLink(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}

fun View.beVisible() {
    visibility = View.VISIBLE
}

fun View.beInVisible() {
    visibility = View.INVISIBLE
}

fun View.beGone() {
    visibility = View.GONE
}

var toast: Toast? = null
fun Context.showToast(msg: String) {
    toast?.cancel()
    toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
    toast?.show()
}



fun Fragment.setBackPress(listener: () -> Unit) {
    requireView().isFocusableInTouchMode = true
    requireView().requestFocus()
    requireView().setOnKeyListener { _, keyCode, event ->
        if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
            singleClick {
                listener.invoke()
            }
            Log.d("BackPressed", "handleOnBackPressed: backPressed")
            true
        } else false
    }
}



fun TextView.makeTextLink(
    str: String,
    underlined: Boolean,
    color: Int?,
    isItalic: Boolean = false,
    action: (() -> Unit)? = null

) {
    val spannableString = SpannableString(this.text)
    val textColor = color ?: this.currentTextColor
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            action?.invoke()
        }

        override fun updateDrawState(drawState: TextPaint) {
            super.updateDrawState(drawState)
            drawState.isUnderlineText = underlined
            drawState.color = textColor
        }
    }
    var index = spannableString.indexOf(str)
    if (index == -1) {
        index = 0
    }
    spannableString.setSpan(
        clickableSpan,
        index,
        index + str.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    this.text = spannableString
    this.movementMethod = LinkMovementMethod.getInstance()
    this.highlightColor = Color.TRANSPARENT
    if (isItalic)
        this.setTypeface(null, Typeface.ITALIC)

}
