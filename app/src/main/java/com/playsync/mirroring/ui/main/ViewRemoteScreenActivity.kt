package com.playsync.mirroring.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Display
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.playsync.mirroring.BuildConfig.APPLICATION_ID
import com.playsync.mirroring.R
import com.playsync.mirroring.data.interfaces.RoomListenerHandler
import com.playsync.mirroring.data.models.MotionMessage
import com.playsync.mirroring.databinding.ActivityViewRemoteScreenBinding
import com.playsync.mirroring.ui.customViews.DrawBallView
import com.playsync.mirroring.ui.dialogs.ColorPickerDialog
import com.playsync.mirroring.ui.dialogs.StopMirroringDialog
import com.playsync.mirroring.ui.main.RemoteCodeActivity.Companion.isAudioSwitchOn
import com.playsync.mirroring.utils.SharedPrefs
import com.playsync.mirroring.utils.beInVisible
import com.playsync.mirroring.utils.visible
import com.playsync.mirroring.utils.getLoadingDialog
import com.playsync.mirroring.utils.singleClick
import com.google.gson.Gson
import com.permissionx.guolindev.PermissionX
import com.twilio.video.LocalAudioTrack
import com.twilio.video.LocalDataTrack
import com.twilio.video.LocalParticipant
import com.twilio.video.RemoteAudioTrack
import com.twilio.video.RemoteAudioTrackPublication
import com.twilio.video.RemoteDataTrack
import com.twilio.video.RemoteDataTrackPublication
import com.twilio.video.RemoteParticipant
import com.twilio.video.RemoteVideoTrack
import com.twilio.video.RemoteVideoTrackPublication
import com.twilio.video.Room
import com.twilio.video.TwilioException


@Suppress("DEPRECATION")
class ViewRemoteScreenActivity : AppCompatActivity(), RoomListenerHandler
{

    private var sharedPrefs: SharedPrefs? = null

    private var binding: ActivityViewRemoteScreenBinding? = null
    private lateinit var loadingDialogBuilder: Dialog

    private var room: Room? = null
    private var localParticipant: LocalParticipant? = null
    private var activityRunning = true
    private var localAudioTrack: LocalAudioTrack? = null
    private var isSharingAudio: Boolean = false

    companion object
    {
        const val TAG = "ViewRemote"
        var localDataTrack: LocalDataTrack? = null
    }

    private var drawBallView: DrawBallView? = null


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityViewRemoteScreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)



        sharedPrefs = SharedPrefs(this)
        setToolBar()

        loadingDialogBuilder = getLoadingDialog()


        RemoteCodeActivity.roomListenerHandler = this

        drawBallView = DrawBallView(this)
        setBallPointerOnScreen()

        if (isAudioSwitchOn)
        {
            shareAudio()
            binding?.apply {
                toolbar.audioSwitch.isChecked = isAudioSwitchOn

            }
        }

    }

    @Deprecated("Deprecated in Java") override fun onBackPressed()
    {
        StopMirroringDialog(this@ViewRemoteScreenActivity) {
            room?.disconnect()
            finish()
        }.show()

    }

    @SuppressLint(
            "ClickableViewAccessibility",
            "LongLogTag"
    ) private fun setBallPointerOnScreen()
    {


        val rootLayout = binding?.mainContent

        if (sharedPrefs?.getDrawBallViewColor() == 0)
        {
            drawBallView?.ballColor = Color.BLUE
        } else
        {
            Log.d(
                    TAG,
                    "setBallPointerOnScreen: " + sharedPrefs!!.getDrawBallViewColor()
            ) //            drawBallView?.ballColor = sharedPrefs?.getDrawBallViewColor()!!.toInt()
        }

        if (sharedPrefs?.getDrawBallWidth() != "" && sharedPrefs?.getDrawBallHeight() != "" && sharedPrefs?.getDrawBallRadius() != "")
        {
            drawBallView?.width = sharedPrefs?.getDrawBallWidth()!!.toFloat()
            drawBallView?.height = sharedPrefs?.getDrawBallHeight()!!.toFloat()
            drawBallView?.radius = sharedPrefs?.getDrawBallRadius()!!.toFloat()
        }


        rootLayout?.setOnTouchListener { _, motionEvent ->

            drawBallView?.let {
                if (motionEvent.action == MotionEvent.ACTION_UP)
                {
                    it.beInVisible()
                }

                if (motionEvent.action == MotionEvent.ACTION_DOWN)
                {
                    it.visible()
                }
                it.currX = motionEvent!!.x
                it.currY = motionEvent.y
            }


            val x = motionEvent.x.toInt()
            val y = motionEvent.y.toInt() // Get the screen dimensions

            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display: Display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            val screenWidth: Int = size.x
            val screenHeight: Int = size.y

            // Calculate the normalized touch coordinates

            // Calculate the normalized touch coordinates
            val normalizedX = x.toFloat() / screenWidth.toFloat()
            val normalizedY = y.toFloat() / screenHeight.toFloat()


            Log.e(
                    TAG,
                    "setBallPointerOnScreen: before x = ${motionEvent.x}"
            )
            Log.e(
                    TAG,
                    "setBallPointerOnScreen: before y = ${motionEvent.y}"
            )

            // Normalize the touch coordinates


            Log.e(
                    TAG,
                    "setBallPointerOnScreen: after x = $normalizedX"
            )
            Log.e(
                    TAG,
                    "setBallPointerOnScreen: after y = $normalizedY"
            )

            val modelMessage = MotionMessage()



            modelMessage.xCoordinate = normalizedX
            modelMessage.yCoordinate = normalizedY
            modelMessage.action = motionEvent.action
            if (sharedPrefs?.getDrawBallViewColor() != 0)
            {
                modelMessage.color = sharedPrefs?.getDrawBallViewColor()!!.toInt()
            } else
            {
                modelMessage.color = Color.BLUE
            }

            modelMessage.screenHeight = screenHeight
            if (sharedPrefs?.getDrawBallWidth() != "" && sharedPrefs?.getDrawBallHeight() != "" && sharedPrefs?.getDrawBallRadius() != "")
            {
                modelMessage.height = SharedPrefs(this).getDrawBallHeight().toFloat()
                modelMessage.width = SharedPrefs(this).getDrawBallWidth().toFloat()
                modelMessage.radius = SharedPrefs(this).getDrawBallRadius().toFloat()
            }


            Log.d(
                    TAG,
                    "setBallPointerOnScreen: " + modelMessage.screenHeight
            )

            val gson = Gson()

            val coordinates = gson.toJson(modelMessage)

            localDataTrack?.send(coordinates) ?: Log.e(
                    TAG,
                    "Ignoring touch event because data track is released"
            )

            drawBallView!!.invalidate()

            true
        }

        //rootLayout!!.addView(drawBallView)
    }

    private fun showPermissionDialog()
    {
        val permissionDialog = AlertDialog.Builder(this)
        permissionDialog.setTitle("Permission required")
        permissionDialog.setMessage("Please grant access to mic if you want to share audio.")
        permissionDialog.setPositiveButton("Ok") { dialogInterface, _ ->
            dialogInterface.dismiss()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$APPLICATION_ID")
            startActivity(intent)

        }
        permissionDialog.setNegativeButton("Cancel") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        permissionDialog.show()
    }

    private fun setToolBar()
    {

        binding?.toolbar?.apply {
            audioSwitch.setOnCheckedChangeListener { _, b ->

                if (b)
                {
                    shareAudio()
                } else
                {
                    stopSharingAudio()
                }
            }

            stopBtn.setOnClickListener {
                onBackPressed()
            }

            pointer.setOnClickListener {
                singleClick {
                    val colorPickerDialog = ColorPickerDialog(this@ViewRemoteScreenActivity)
                    colorPickerDialog.show()
                }
            }
        }


    }


    private fun setIsSharingAudio(isSharing: Boolean)
    {


        isSharingAudio = isSharing
        binding?.toolbar?.audioSwitch?.isChecked = isSharing


    }

    private fun stopSharingAudio()
    {
        setIsSharingAudio(false)
        if (localAudioTrack != null)
        {
            localParticipant?.unpublishTrack(localAudioTrack!!)
        }

    }

    private fun shareAudio()
    {

        PermissionX.init(this).permissions(Manifest.permission.RECORD_AUDIO)
            .request { allGranted, _, _ ->
                if (allGranted)
                { //check if have RECORD_AUDIO PERMISSION
                    if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                    {
                        localAudioTrack = LocalAudioTrack.create(
                                this,
                                true
                        )
                        val hasPublished = localParticipant?.publishTrack(localAudioTrack!!)
                        Log.d(
                                "HAS PUBLISHED",
                                "$hasPublished"
                        )
                        setIsSharingAudio(true)
                    }


                } else
                {
                    setIsSharingAudio(false)
                    stopSharingAudio()
                    showPermissionDialog()
                }
            }


    }

    private fun setIsLoading(isLoading: Boolean)
    {
        if (activityRunning)
        {


            if (isLoading)
            {
                if (!loadingDialogBuilder.isShowing)
                {
                    loadingDialogBuilder.show()
                }
            } else
            {
                if (loadingDialogBuilder.isShowing)
                {
                    loadingDialogBuilder.dismiss()
                }
            }
        }

    }


    @SuppressLint("LongLogTag") override fun onConnected(r: Room)
    {

        room = r
        localParticipant = r.localParticipant

        setIsLoading(false)
        val remoteParticipants = room!!.remoteParticipants
        for (remoteParticipant in remoteParticipants)
        {
            val remoteVideoTrackPublications = remoteParticipant.remoteVideoTracks
            val remoteAudioTrackPublications = remoteParticipant.remoteAudioTracks
            val remoteDataTrackPublications = remoteParticipant.remoteDataTracks

            Log.d(
                    TAG,
                    "onConnected: $remoteDataTrackPublications"
            )

            Log.i(
                    "REMOTE AUDIO TRACKS",
                    remoteAudioTrackPublications.size.toString()
            )
            remoteParticipant.setListener(remoteParticipantListener()) //set up listeners for each participant. This will allow them to listen to subscription events
            if (remoteVideoTrackPublications.isNotEmpty())
            {
                if (remoteVideoTrackPublications.first().isTrackSubscribed)
                {
                    val videoTrack = remoteVideoTrackPublications.first().remoteVideoTrack
                    videoTrack?.addSink(binding?.remoteVideoView!!)
                }

            }

        }
    }

    override fun onDisconnected(room: Room, twilioException: TwilioException?)
    {
        Toast.makeText(
                this@ViewRemoteScreenActivity,
                "Disconnected",
                Toast.LENGTH_SHORT
        ).show() //            setIsLoading(false)
        Log.d(
                TAG,
                "Room disconnected"
        )
        finish()


    }

    override fun onReconnected(room: Room)
    {
        Log.d(
                TAG,
                "RECONNECTED"
        )
        setIsLoading(false)
    }


    override fun onConnectFailure(room: Room, twilioException: TwilioException)
    {
        setIsLoading(false)
        Log.d(
                TAG,
                "Failed to connect ${twilioException.explanation} ${twilioException.message}"
        )
        Toast.makeText(
                this@ViewRemoteScreenActivity,
                "Failed to connect",
                Toast.LENGTH_SHORT
        ).show()
        finish()
    }


    override fun onParticipantDisconnected(r: Room, remoteParticipant: RemoteParticipant)
    {
        showHostDisconnectedDialog()

    }

    private fun showHostDisconnectedDialog()
    {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(
                R.layout.host_disconnected_dialog,
                null
        )
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        val btnOK = dialogView.findViewById<TextView>(R.id.btnOK)

        btnOK.setOnClickListener {
            alertDialog.dismiss()
            room?.disconnect()
            finish()
        }

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (!isFinishing)
        {
            alertDialog.show()
        }
    }

    private fun remoteParticipantListener(): RemoteParticipant.Listener
    {
        return object : RemoteParticipant.Listener
        {
            override fun onAudioTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
            )
            {

                Log.d(
                        TAG,
                        "onLocalAudioTrackPublished"
                )
            }

            override fun onAudioTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
            )
            {
            }

            override fun onAudioTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                remoteAudioTrack: RemoteAudioTrack,
            )
            {
                Log.d(
                        TAG,
                        "onLocalAudioTrackSubscribed"
                )
                remoteAudioTrack.enablePlayback(true)
                val audioManager = getSystemService(Service.AUDIO_SERVICE) as AudioManager
                audioManager.mode = AudioManager.MODE_NORMAL
                audioManager.isSpeakerphoneOn = true


            }

            override fun onAudioTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                twilioException: TwilioException,
            )
            {
            }

            override fun onAudioTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                remoteAudioTrack: RemoteAudioTrack,
            )
            {
            }

            override fun onVideoTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
            )
            {
                val isNull = remoteVideoTrackPublication.remoteVideoTrack == null

                if (!isNull)
                {
                    remoteVideoTrackPublication.remoteVideoTrack!!.addSink(binding?.remoteVideoView!!)

                }


            }

            override fun onVideoTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
            )
            {
                Log.d(
                        "UNPUBLISHED",
                        "TRACK WAS UNPUBLISHED"
                )
                Log.d(
                        "UNPUBLISHED",
                        remoteVideoTrackPublication.trackName
                )

            }

            override fun onVideoTrackSubscribed(
                participant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                remoteVideoTrack: RemoteVideoTrack,
            )
            {
                Log.d(
                        TAG,
                        "TRACK SUBSCRIBED!"
                )


                if (remoteVideoTrackPublication.isTrackSubscribed)
                {
                    remoteVideoTrackPublication.videoTrack?.addSink(binding?.remoteVideoView!!)
                }


                //                binding?.fabToggleAudio?.visibility = View.VISIBLE
                //                setFullScreen(true)


            }

            override fun onVideoTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                twilioException: TwilioException,
            )
            {

            }

            override fun onVideoTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                remoteVideoTrack: RemoteVideoTrack,
            )
            {

            }

            override fun onDataTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
            )
            {

                Log.d(
                        TAG,
                        "onLocalDataTrackPublished"
                )
            }

            override fun onDataTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
            )
            {


            }

            override fun onDataTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                remoteDataTrack: RemoteDataTrack,
            )
            {
            }

            override fun onDataTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                twilioException: TwilioException,
            )
            {
            }

            override fun onDataTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                remoteDataTrack: RemoteDataTrack,
            )
            {

                Log.d(
                        TAG,
                        "onLocalDataTrackSubscribed"
                )
            }

            override fun onAudioTrackEnabled(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
            )
            {
            }

            override fun onAudioTrackDisabled(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
            )
            {
            }

            override fun onVideoTrackEnabled(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
            )
            {
            }

            override fun onVideoTrackDisabled(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
            )
            {
            }
        }
    }


}