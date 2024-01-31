package com.example.mobiletomobile.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mobiletomobile.BuildConfig
import com.example.mobiletomobile.R
import com.example.mobiletomobile.data.service.GlobalTouchService
import com.example.mobiletomobile.data.service.ScreenSharingForegroundService
import com.example.mobiletomobile.data.service.StartForegroundServiceCallback
import com.example.mobiletomobile.databinding.ActivityScreenSharingBinding
import com.example.mobiletomobile.ui.customViews.DrawBallView
import com.example.mobiletomobile.utils.afterDelay
import com.example.mobiletomobile.utils.beGone
import com.example.mobiletomobile.utils.beInVisible
import com.example.mobiletomobile.utils.beVisible
import com.example.mobiletomobile.utils.getLoadingDialog
import com.example.mobiletomobile.utils.getRandomString
import com.example.mobiletomobile.utils.getRandomStringForRoom
import com.google.gson.JsonParser
import com.permissionx.guolindev.PermissionX
import com.twilio.video.ConnectOptions
import com.twilio.video.EncodingParameters
import com.twilio.video.H264Codec
import com.twilio.video.LocalAudioTrack
import com.twilio.video.LocalDataTrack
import com.twilio.video.LocalParticipant
import com.twilio.video.LocalVideoTrack
import com.twilio.video.RemoteAudioTrack
import com.twilio.video.RemoteAudioTrackPublication
import com.twilio.video.RemoteDataTrack
import com.twilio.video.RemoteDataTrackPublication
import com.twilio.video.RemoteParticipant
import com.twilio.video.RemoteVideoTrack
import com.twilio.video.RemoteVideoTrackPublication
import com.twilio.video.Room
import com.twilio.video.ScreenCapturer
import com.twilio.video.TwilioException
import com.twilio.video.Video
import com.twilio.video.VideoCodec
import com.twilio.video.VideoDimensions
import com.twilio.video.VideoFormat
import org.json.JSONObject
import org.json.JSONTokener
import java.nio.ByteBuffer

@Suppress("DEPRECATION")
class ScreenSharingActivity : AppCompatActivity(), StartForegroundServiceCallback,
    View.OnClickListener {

    private var resultCode: Int? = null
    private var data: Intent? = null

    companion object {
        const val TAG = "MobileToMobileMirroring"
        const val REQUEST_MEDIA_PROJECTION = 100


        @SuppressLint("StaticFieldLeak")
        var room: Room? = null
    }

    var isSharing = false

    private var screenCaptured: ScreenCapturer? = null
    private val videoCodec: VideoCodec = H264Codec()
    private var localParticipant: LocalParticipant? = null
    private var localVideoTrack: LocalVideoTrack? = null
    private var localAudioTrack: LocalAudioTrack? = null

    private var mediaProjectionManager: MediaProjectionManager? = null


    private val codeEditTextsAfter = mutableListOf<EditText>()
    private lateinit var loadingDialogBuilder: Dialog

    private var roomCode = ""

    private var drawBallView: DrawBallView? = null

    private var localDataTrack: LocalDataTrack? = null

    // Dedicated thread and handler for messages received from a RemoteDataTrack
    private val dataTrackMessageThread =
        HandlerThread("DATA_TRACK_MESSAGE_THREAD")
    private var dataTrackMessageThreadHandler: Handler? = null

    // Map used to map remote data tracks to remote participants
    private val dataTrackRemoteParticipantMap: HashMap<RemoteDataTrack, RemoteParticipant> =
        HashMap()


    lateinit var binding: ActivityScreenSharingBinding

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (Settings.canDrawOverlays(this@ScreenSharingActivity)) {
                initSharing()
            } else {
                Toast.makeText(
                    this@ScreenSharingActivity,
                    "Overlay Permission Not Granted!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()


            }

            Log.d("overlayPermission", "result: " + result.resultCode)

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenSharingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()

        drawBallView = DrawBallView(this)
        ScreenSharingForegroundService.startServiceCallback = this
        initViews()



        if (ScreenSharingForegroundService.isRunning) {
            setIsSharing(true)
            setCode(ScreenSharingForegroundService.currentCode!!)
            room = ScreenSharingForegroundService.currentRoom!!


//            if (ScreenSharingForegroundService.currentRoom?.remoteParticipants?.size!! >= 2) {
//                Toast.makeText(this@ScreenSharingActivity, "two participants", Toast.LENGTH_SHORT)
//                    .show()
//            }
        } else {
            if (!Settings.canDrawOverlays(this@ScreenSharingActivity)) {
                showOverlayPermissionDialogue()
            }

        }

        // Create the local data track
        localDataTrack = LocalDataTrack.create(this)


        // Start the thread where data messages are received
        dataTrackMessageThread.start()
        dataTrackMessageThreadHandler = Handler(dataTrackMessageThread.looper)


    }


    private fun showOverlayPermissionDialogue() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_overlay_permission, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(false)
        val btnClose = dialogView.findViewById<ImageView>(R.id.imgBtnCloseOverlayPermission)



        dialogView.findViewById<View>(R.id.continueScreenShare).setOnClickListener {
            alertDialog.dismiss()
            initSharing()
        }
        btnClose.setOnClickListener {
            alertDialog.dismiss()
            initSharing()
        }

        val grantPermission = dialogView.findViewById<CardView>(R.id.grantOverlayPermission)
        grantPermission.setOnClickListener {
            alertDialog.dismiss()
            checkDrawOverlayPermission()

        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun checkDrawOverlayPermission() {
        if (Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            )
            resultLauncher.launch(intent)
        }
    }


    private fun initViews() {
        loadingDialogBuilder = this.getLoadingDialog()

        binding.apply {
            codeEditTextsAfter.add(etChar1)
            codeEditTextsAfter.add(etChar2)
            codeEditTextsAfter.add(etChar3)
            codeEditTextsAfter.add(etChar4)
            audioSwitch.setOnCheckedChangeListener { _, b ->
                if (b) {
                    shareAudio()
                } else {
                    stopSharingAudio()
                }
            }
            copyConstraint.setOnClickListener(this@ScreenSharingActivity)
            nextBtn.setOnClickListener(this@ScreenSharingActivity)
            stopBtn.setOnClickListener(this@ScreenSharingActivity)
        }

    }

    private fun shareAudio() {

        PermissionX.init(this).permissions(Manifest.permission.RECORD_AUDIO)
            .request { allGranted, _, _ ->
                if (allGranted) {
                    localAudioTrack = LocalAudioTrack.create(this@ScreenSharingActivity, true)
                    val hasPublished = localParticipant?.publishTrack(localAudioTrack!!)
                    Log.e("HAS PUBLISHED", "$hasPublished")
                    setIsSharingAudio(true)
                } else {
                    stopSharingAudio()
                    binding.audioSwitch.isChecked = false
                }

            }

    }

    private fun stopSharingAudio() {
        setIsSharingAudio(false)
        if (localAudioTrack != null) {
            localParticipant?.unpublishTrack(localAudioTrack!!)
        }

    }

    private fun askScreenCapturePermissions() {


        Log.d(
            TAG,
            "Requesting permission to capture screen"
        )

        mediaProjectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjectionManager?.createScreenCaptureIntent()?.let {
            startActivityForResult(
                it,
                REQUEST_MEDIA_PROJECTION
            )
        }


    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != RESULT_OK) {
                Log.d(TAG, "onActivityResult: permission not granted!")
                finish()
                return
            }



            val foregroundServiceIntent =
                Intent(this@ScreenSharingActivity, ScreenSharingForegroundService::class.java)

            startService(foregroundServiceIntent)



            this.resultCode = resultCode
            this.data = data
//                }

            //after service has been started, the onServiceStart function in this class is called.

            Log.d(TAG, "onActivityResult: $mediaProjectionManager")


        }


    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            title.text = getString(R.string.share_screen)
            back.setOnClickListener(this@ScreenSharingActivity)

        }
    }

    private val screenCapturedListener: ScreenCapturer.Listener = object : ScreenCapturer.Listener {
        override fun onScreenCaptureError(errorDescription: String) {
            Log.e(
                TAG,
                "Screen capture error: $errorDescription"
            )
        }

        override fun onFirstFrameAvailable() {
            Log.d(TAG, "First frame from screen capture available")

        }
    }

    private fun stopCapture() {
        val foregroundServiceIntent =
            Intent(this, ScreenSharingForegroundService::class.java)
        stopService(foregroundServiceIntent)
        screenCaptured?.stopCapture()
        localVideoTrack?.release()
        localAudioTrack?.release()
        localDataTrack?.release()
        localAudioTrack = null

    }


//    override fun onDestroy() {
//        super.onDestroy()
//        binding = null
//
//        /*
//         * Always disconnect from the room before leaving the Activity to
//         * ensure any memory allocated to the Room resource is freed.
//         */if (room != null && room!!.state != Room.State.DISCONNECTED) {
//            room!!.disconnect()
//
//        }
//
//        // Quit the data track message thread
//        dataTrackMessageThread.quit()
//
//
//        /*
//         * Release the local audio and video tracks ensuring any memory allocated to audio
//         * or video is freed.
//         */if (localDataTrack != null) {
//            localDataTrack!!.release()
//            localDataTrack = null
//        }
//    }

    private fun addRemoteDataTrack(
        remoteParticipant: RemoteParticipant, remoteDataTrack: RemoteDataTrack
    ) {
        dataTrackRemoteParticipantMap[remoteDataTrack] = remoteParticipant
        Log.d(TAG, "addRemoteDataTrack: $dataTrackRemoteParticipantMap")
        remoteDataTrack.setListener(remoteDataTrackListener())
    }

    private fun remoteDataTrackListener(): RemoteDataTrack.Listener {
        return object : RemoteDataTrack.Listener {
            override fun onMessage(remoteDataTrack: RemoteDataTrack, byteBuffer: ByteBuffer) {}
            override fun onMessage(remoteDataTrack: RemoteDataTrack, message: String) {


                val parser = JsonParser()
                val json = parser.parse(message)



                Log.d(TAG, "onMessage:  ${json.asJsonObject["action"]}")
                Log.d(TAG, "onMessage:  ${json.asJsonObject["screenHeight"]}")

                if (!GlobalTouchService.isRunning) {
                    startService(
                        Intent(
                            this@ScreenSharingActivity,
                            GlobalTouchService::class.java
                        )
                    )
                }
                setBallPointerOnScreen(
                    json.asJsonObject["xCoordinate"].asFloat,
                    json.asJsonObject["yCoordinate"].asFloat,
                    json.asJsonObject["action"].asInt,
                    json.asJsonObject["color"].asInt,
                    json.asJsonObject["screenHeight"].asInt
                )
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setBallPointerOnScreen(x: Float, y: Float, action: Int, color: Int, height: Int) {

        drawBallView?.apply {
            this.minimumWidth = 500
            this.minimumHeight = 800
            this.currX = x
            this.currY = y
            this.ballColor = color
        }


        val intent = Intent(this, GlobalTouchService::class.java)
        intent.putExtra("xCoordinate", x)
        intent.putExtra("yCoordinate", y)
        intent.putExtra("action", action)
        intent.putExtra("color", color)
        intent.putExtra("screenHeight", height)
        startService(intent)

    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (ScreenSharingForegroundService.isRunning) {
            showStopSharingDialog()
        } else {
            finish()
        }
    }


    override fun onClick(v: View?) {
        binding?.apply {
            when (v) {

                copyConstraint -> {
                    val clipboardManager =
                        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboardManager.setPrimaryClip(ClipData.newPlainText("Code", roomCode))
                    Toast.makeText(
                        this@ScreenSharingActivity,
                        "Code copied to clipboard: $roomCode",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                toolbar.back -> {
                    onBackPressed()
                }

                stopBtn -> {
                    showStopSharingDialog()
                }

                nextBtn -> {
                    initSharing()
                }
            }
        }

    }

    private fun initSharing()
    {
        if (!ScreenSharingForegroundService.isRunning)
        {
            generateScreenSharingCode()
            askScreenCapturePermissions()
        } else
        {
            Toast.makeText(
                this@ScreenSharingActivity, "Already Sharing Screen.", Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun showStopSharingDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.stop_sharing_dialog, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        val yesBtn = dialogView.findViewById<TextView>(R.id.btnYes)

        yesBtn.setOnClickListener {
            alertDialog.dismiss()
            stopCapture()
            room?.disconnect()
            finish()
        }

        val noBtn = dialogView.findViewById<TextView>(R.id.btnNo)

        noBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }


    override fun onServiceStarted() {
        Handler(Looper.getMainLooper()).postDelayed({
            //Do something after 100ms
            screenCaptured =
                ScreenCapturer(this, resultCode!!, data!!, screenCapturedListener)
            val videoFormat = VideoFormat(VideoDimensions.HD_720P_VIDEO_DIMENSIONS, 24)
            localVideoTrack =
                LocalVideoTrack.create(this, true, screenCaptured!!, videoFormat)

            localParticipant?.publishTrack(localVideoTrack!!)
            getAccessToken()
            ScreenSharingForegroundService.currentCode = roomCode
        }, 1000)
    }

    private fun getAccessToken() {
        setIsLoading(true)
        val queue = Volley.newRequestQueue(this)
        val identity = getRandomString(8)
        val url = "https://prune-antelope-6809.twil.io/video-token?identity=$identity"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Log.i(TAG, String.format(response.toString()))
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

                Toast.makeText(this@ScreenSharingActivity, "Failed to connect.", Toast.LENGTH_SHORT)
                    .show()
                stopCapture()
            })
        queue.add(stringRequest)
    }

    private fun generateScreenSharingCode() {
        afterDelay(1000) {
            binding.pleaseWaitConstraint.beGone()
        }

        val code = getRandomStringForRoom(4)
        roomCode = code

    }

    private fun setCode(code: String) {
        roomCode = code
        code.forEachIndexed { index, element ->
            codeEditTextsAfter[index].setText(element.toString())
        }
    }


    private fun connectToRoom(roomName: String, accessToken: String) {
        val optionsBuilder: ConnectOptions.Builder =
            ConnectOptions.Builder(accessToken)
        optionsBuilder.preferVideoCodecs(listOf(videoCodec))


        optionsBuilder.roomName(roomName)
        optionsBuilder.encodingParameters(
            EncodingParameters(16, 0)
        )

        if (localVideoTrack != null) {
            optionsBuilder.videoTracks(listOf(localVideoTrack))
        }
        if (localAudioTrack != null) {
            optionsBuilder.audioTracks(listOf(localAudioTrack))
        }


        Video.connect(
            this@ScreenSharingActivity,
            optionsBuilder.build(),
            roomListener
        )
    }

    private fun setIsSharingAudio(isSharing: Boolean) {

        ScreenSharingForegroundService.isAudioSharing = isSharing
    }

    private val roomListener = object : Room.Listener {
        @SuppressLint("SetTextI18n")
        override fun onConnected(r: Room) {
            setIsLoading(false)
            setIsSharing(true)
            localParticipant = r.localParticipant
            room = r
            ScreenSharingForegroundService.currentRoom = room

            binding.apply {

                tagLine.text = "Share this generated code to other user"
                roomCode.forEachIndexed { index, element ->
                    codeEditTextsAfter[index].setText(element.toString())

                }
                copyConstraint.beVisible()
                nextBtn.beInVisible()
                stopBtn.beVisible()
            }


        }

        override fun onConnectFailure(room: Room, twilioException: TwilioException) {
            setIsLoading(false)

            Toast.makeText(this@ScreenSharingActivity, "Failed to connect.", Toast.LENGTH_SHORT)
                .show()
            Log.d(
                "TWILIO",
                "explanation: ${twilioException.explanation}, message: ${twilioException.message}"
            )
            stopCapture()


        }

        override fun onReconnecting(room: Room, twilioException: TwilioException) {

        }

        override fun onReconnected(room: Room) {

        }

        override fun onDisconnected(room: Room, twilioException: TwilioException?) {


            Log.d(TAG, "onDisconnected")
            Log.d(TAG, "${twilioException?.message}, ${twilioException?.explanation}")

            if (twilioException?.message != null) {
                Toast.makeText(
                    this@ScreenSharingActivity,
                    "Lost connection", Toast.LENGTH_SHORT
                )
            }

            setIsSharing(false)
            stopCapture()
            room.disconnect()
            generateScreenSharingCode()

        }

        override fun onParticipantConnected(room: Room, remoteParticipant: RemoteParticipant) {
            showParticipantJoinedDialog()
            remoteParticipant.setListener(remoteParticipantListener())
            isSharing = true
//            SharedPrefs(this@ScreenSharingActivity).setParticipantConnected(true)


            binding?.apply {
                nextBtn.beGone()
                stopBtn.beVisible()
            }
        }

        override fun onParticipantDisconnected(room: Room, remoteParticipant: RemoteParticipant) {

//            SharedPrefs(this@ScreenSharingActivity).setParticipantConnected(false)
            showParticipantDisconnectedDialog()
            setIsSharing(false)
            setIsSharingAudio(false)

            isSharing = false

        }

        override fun onRecordingStarted(room: Room) {

        }

        override fun onRecordingStopped(room: Room) {

        }
    }

    private fun showParticipantDisconnectedDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.participant_disconnected_dialog, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        val btnYes = dialogView.findViewById<TextView>(R.id.btnYes)

        btnYes.setOnClickListener {
            alertDialog.dismiss()
            binding.stopBtn.beGone()
            binding.nextBtn.beVisible()
            generateScreenSharingCode()
            askScreenCapturePermissions()
        }

        val btnNo = dialogView.findViewById<TextView>(R.id.btnNo)

        btnNo.setOnClickListener {
            alertDialog.dismiss()
            stopCapture()
            room?.disconnect()
            finish()
        }

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun showParticipantJoinedDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.participant_joined_dialog, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        val btnOK = dialogView.findViewById<TextView>(R.id.btnOK)

        btnOK.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun remoteParticipantListener(): RemoteParticipant.Listener {
        return object : RemoteParticipant.Listener {
            override fun onAudioTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {

                Log.d(TAG, "onLocalAudioTrackPublished")
            }

            override fun onAudioTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
            }

            override fun onAudioTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                remoteAudioTrack: RemoteAudioTrack
            ) {
                Log.d(TAG, "onLocalAudioTrackSubscribed")
                remoteAudioTrack.enablePlayback(true)
                val audioManager = getSystemService(Service.AUDIO_SERVICE) as AudioManager
                audioManager.mode = AudioManager.MODE_NORMAL
                audioManager.isSpeakerphoneOn = true


            }

            override fun onAudioTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                twilioException: TwilioException
            ) {
            }

            override fun onAudioTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                remoteAudioTrack: RemoteAudioTrack
            ) {
            }

            override fun onVideoTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {


            }

            override fun onVideoTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
                Log.d("UNPUBLISHED", "TRACK WAS UNPUBLISHED")
                Log.d("UNPUBLISHED", remoteVideoTrackPublication.trackName)

            }

            override fun onVideoTrackSubscribed(
                participant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                remoteVideoTrack: RemoteVideoTrack
            ) {


            }

            override fun onVideoTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                twilioException: TwilioException
            ) {

            }

            override fun onVideoTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                remoteVideoTrack: RemoteVideoTrack
            ) {

            }

            override fun onDataTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication
            ) {
            }

            override fun onDataTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication
            ) {
            }

            override fun onDataTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                remoteDataTrack: RemoteDataTrack
            ) {
                /*
                 * Data track messages are received on the thread that calls setListener. Post the
                 * invocation of setting the listener onto our dedicated data track message thread.
                 */dataTrackMessageThreadHandler!!.post {
                    addRemoteDataTrack(
                        remoteParticipant,
                        remoteDataTrack
                    )
                }
            }

            override fun onDataTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                twilioException: TwilioException
            ) {
            }

            override fun onDataTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                remoteDataTrack: RemoteDataTrack
            ) {


            }

            override fun onAudioTrackEnabled(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
            }

            override fun onAudioTrackDisabled(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
            }

            override fun onVideoTrackEnabled(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
            }

            override fun onVideoTrackDisabled(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
            }
        }
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

    private fun setIsSharing(isSharing: Boolean) {
        this@ScreenSharingActivity.isSharing = isSharing
        if (isSharing) {
            Log.e(TAG, "setIsSharing: is now sharing screen")
        } else {
            binding.apply {
                stopBtn.beGone()
                nextBtn.beVisible()
            }
        }

    }

}