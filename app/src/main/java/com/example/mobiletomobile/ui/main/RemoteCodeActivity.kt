package com.example.mobiletomobile.ui.main

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mobiletomobile.R
import com.example.mobiletomobile.data.interfaces.RoomListenerHandler
import com.example.mobiletomobile.databinding.ActivityRemoteCodeBinding
import com.example.mobiletomobile.utils.beGone
import com.example.mobiletomobile.utils.beVisible
import com.example.mobiletomobile.utils.getLoadingDialog
import com.example.mobiletomobile.utils.getRandomString
import com.example.mobiletomobile.utils.singleClick
import com.twilio.video.ConnectOptions
import com.twilio.video.LocalDataTrack
import com.twilio.video.RemoteParticipant
import com.twilio.video.Room
import com.twilio.video.TwilioException
import com.twilio.video.Video
import org.json.JSONObject
import org.json.JSONTokener
import java.util.regex.Pattern

class RemoteCodeActivity : AppCompatActivity(), View.OnClickListener {

    private val codeEditTexts = mutableListOf<EditText>()


    companion object {
        var roomListenerHandler: RoomListenerHandler? = null
        var isAudioSwitchOn = false
    }

    var binding: ActivityRemoteCodeBinding? = null

    private lateinit var loadingDialogBuilder: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRemoteCodeBinding.inflate(layoutInflater)
        setContentView(binding?.root)



        loadingDialogBuilder = getLoadingDialog()
        loadingDialogBuilder.setCancelable(false)
        ViewRemoteScreenActivity.localDataTrack = LocalDataTrack.create(this)


        setupToolbar()
        initView()
        addEditTextListeners()


    }


    private fun setupToolbar() {
        binding?.apply {
            toolbar.back.setOnClickListener(this@RemoteCodeActivity)

            toolbar.title.text = resources.getString(R.string.remote_screen)

            nextBtn.isEnabled = false
            nextBtn.alpha = 0.5f

            nextBtn.setOnClickListener(this@RemoteCodeActivity)
        }

    }

    private fun initView() {
        binding?.apply {
            codeEditTexts.add(etChar1)
            codeEditTexts.add(etChar2)
            codeEditTexts.add(etChar3)
            codeEditTexts.add(etChar4)
            pasteConstraint.setOnClickListener(this@RemoteCodeActivity)
            clearConstraint.setOnClickListener(this@RemoteCodeActivity)
            audioSwitch.setOnCheckedChangeListener { _, isChecked ->
                isAudioSwitchOn = isChecked
            }
        }


    }

    private fun addEditTextListeners() {


        codeEditTexts[0].apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {


                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (text.toString().length == 1) {
                        clearFocus()
                        codeEditTexts[1].requestFocus()
                        codeEditTexts[1].selectAll()
                    }

                    checkShareButton()
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })


        }
        codeEditTexts[1].apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (text.toString().length == 1) {
                        clearFocus()
                        codeEditTexts[2].requestFocus()
                        codeEditTexts[2].selectAll()
                    }

                    checkShareButton()
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            setOnKeyListener(View.OnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_DEL && text.isEmpty()) {
                    clearFocus()
                    codeEditTexts[0].requestFocus()
                    codeEditTexts[0].selectAll()
                    return@OnKeyListener true
                }
                false
            })
        }


        codeEditTexts[2].apply {
            addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                        p0: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {


                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        if (text.toString().length == 1) {
                            clearFocus()
                            codeEditTexts[3].requestFocus()
                            codeEditTexts[3].selectAll()
                        }

                        checkShareButton()

                    }

                    override fun afterTextChanged(p0: Editable?) {
                    }
                })

            setOnKeyListener(View.OnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_DEL && text.isEmpty()) {
                    clearFocus()
                    codeEditTexts[1].requestFocus()
                    codeEditTexts[1].selectAll()
                    return@OnKeyListener true
                }
                false
            })
        }
        codeEditTexts[3].apply {

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {


                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (text.toString().length == 1) {
                        clearFocus()
                        val inputMethodManager =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)

                        binding?.pasteConstraint?.beGone()
                        binding?.clearConstraint?.beVisible()

                    }

                    checkShareButton()

                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            setOnKeyListener(View.OnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_DEL && text.isEmpty()) {
                    clearFocus()
                    codeEditTexts[2].requestFocus()
                    codeEditTexts[2].selectAll()
                    return@OnKeyListener true
                }
                false
            })
        }


    }

    private fun checkShareButton() {
        binding?.apply {
            if (etChar1.text.toString().isNotEmpty() &&
                etChar2.text.toString().isNotEmpty() &&
                etChar3.text.toString().isNotEmpty() &&
                etChar4.text.toString().isNotEmpty()
            ) {
                pasteConstraint.beGone()
                clearConstraint.beVisible()

                nextBtn.alpha = 1.0f
                nextBtn.isEnabled = true

            } else {
                pasteConstraint.beVisible()
                clearConstraint.beGone()

                nextBtn.alpha = 0.5f
                nextBtn.isEnabled = false

            }
        }
    }

    private fun getAccessToken(
        roomCode: String
    ) {

        //binding?.btnConnect?.isEnabled = false
        val queue = Volley.newRequestQueue(this)
        val identity = getRandomString(8)

        setIsLoading(true)
        val url = "https://prune-antelope-6809.twil.io/video-token?identity=$identity"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val jsonObject =
                    JSONTokener(String.format(response.toString())).nextValue() as JSONObject

                val accessToken = jsonObject.getString("token")
                Log.d("ACCESS TOKEN", accessToken)
                connectToRoom(roomName = roomCode, accessToken = accessToken)
            },
            {
                Log.d(TAG, "Error: ${it?.message}")
                Log.d(
                    TAG,
                    String.format(it.networkResponse?.data?.decodeToString() ?: "Error")
                )
                setIsLoading(false)
                //binding?.btnConnect?.isEnabled = true
            })
        queue.add(stringRequest)
    }

    private fun connectToRoom(roomName: String, accessToken: String) {
        val optionsBuilder: ConnectOptions.Builder =
            ConnectOptions.Builder(accessToken)
        optionsBuilder.roomName(roomName)
        optionsBuilder.dataTracks(listOf(ViewRemoteScreenActivity.localDataTrack))
        Video.connect(this, optionsBuilder.build(), roomListener)
    }


    private fun setIsLoading(isLoading: Boolean) {


        if (isLoading) {
            if (!loadingDialogBuilder.isShowing) {
                loadingDialogBuilder.show()
            }
        } else {
            if (loadingDialogBuilder.isShowing) {
                loadingDialogBuilder.dismiss()
            }

        }
    }


    private val roomListener = object : Room.Listener {
        override fun onConnected(r: Room) {
            setIsLoading(false)
            //binding?.btnConnect?.isEnabled = true
            val remoteParticipants = r.remoteParticipants
            if (remoteParticipants.isEmpty()) {
                Toast.makeText(this@RemoteCodeActivity, "Room not found.", Toast.LENGTH_SHORT)
                    .show()

                r.disconnect()

            } else {
                val intent = Intent(this@RemoteCodeActivity, ViewRemoteScreenActivity::class.java)

                startActivity(intent)
                Handler(Looper.getMainLooper()).postDelayed({
                    //this delay is important because we have to wait for the listener to be initialised
                    roomListenerHandler?.onConnected(r)
                }, 500)
            }


        }


        override fun onConnectFailure(room: Room, twilioException: TwilioException) {
            setIsLoading(false)
            // binding?.btnConnect?.isEnabled = true
            Log.d(
                TAG,
                "Failed to connect ${twilioException.explanation} ${twilioException.message}"
            )
            Toast.makeText(this@RemoteCodeActivity, "Failed to connect", Toast.LENGTH_SHORT).show()
        }

        override fun onReconnecting(room: Room, twilioException: TwilioException) {
            Log.d(TAG, "RECONNECTING")

        }

        override fun onReconnected(room: Room) {
            roomListenerHandler?.onReconnected(room)
        }

        override fun onDisconnected(room: Room, twilioException: TwilioException?) {

            if (roomListenerHandler == null) {

                Toast.makeText(this@RemoteCodeActivity, "Disconnected", Toast.LENGTH_SHORT).show()


            } else {
                roomListenerHandler?.onDisconnected(room, twilioException)
            }

            codeEditTexts.forEach {
                it.text.clear()
            }

        }

        override fun onParticipantConnected(room: Room, remoteParticipant: RemoteParticipant) {

        }

        override fun onParticipantDisconnected(r: Room, remoteParticipant: RemoteParticipant) {
            roomListenerHandler?.onParticipantDisconnected(r, remoteParticipant)
        }

        override fun onRecordingStarted(room: Room) {

        }

        override fun onRecordingStopped(room: Room) {

        }
    }


    override fun onClick(v: View?) {
        binding?.apply {
            when (v) {

                toolbar.back -> {
                    singleClick {
                        onBackPressed()
                    }
                }

                nextBtn -> {
                    singleClick {

                        var roomCode = ""
                        codeEditTexts.forEach {
                            roomCode += it.text.toString()
                        }
                        if (roomCode.length < 4) {
                            Toast.makeText(
                                this@RemoteCodeActivity,
                                "Code is Required!",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            val p: Pattern = Pattern.compile("[^a-zA-Z0-9]")
                            val hasSpecialChar: Boolean = p.matcher(roomCode).find()
                            if (hasSpecialChar) {
                                Toast.makeText(
                                    this@RemoteCodeActivity,
                                    "Language not supported!",
                                    Toast.LENGTH_SHORT
                                ).show()


                            } else {


                                getAccessToken(roomCode)

                            }
                        }
                    }

                }

                pasteConstraint -> {
                    val clipboardManager =
                        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager


                    if (clipboardManager.primaryClip != null) {

                        clipboardManager.apply {
                            if (hasPrimaryClip() && primaryClipDescription!!.hasMimeType(
                                    ClipDescription.MIMETYPE_TEXT_PLAIN
                                )
                            ) {

                                try {
                                    val code = primaryClip!!.getItemAt(0).text
                                    Log.d("CODE", code.toString())
                                    if (code.length == 4) {
                                        code.forEachIndexed { index, char ->
                                            codeEditTexts[index].setText(char.toString())

                                            nextBtn.alpha = 1.0f
                                            nextBtn.isEnabled = true
                                        }
                                    } else {
                                        Toast.makeText(
                                            this@RemoteCodeActivity,
                                            "Invalid Code!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                } catch (e: Exception) {
                                    print(e.message)
                                }


                            }
                        }
                    } else {
                        Toast.makeText(this@RemoteCodeActivity, "Please copy your partner's code & paste here!", Toast.LENGTH_SHORT).show()


                    }


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        clipboardManager.clearPrimaryClip()
                    }
                }

                clearConstraint -> {
                    codeEditTexts.forEach {
                        it.text.clear()
                    }
                }
            }

        }
    }


}