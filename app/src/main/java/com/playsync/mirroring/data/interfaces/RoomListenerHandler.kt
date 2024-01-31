package com.playsync.mirroring.data.interfaces

import com.twilio.video.RemoteParticipant
import com.twilio.video.Room
import com.twilio.video.TwilioException

interface RoomListenerHandler {
    fun onConnected(r: Room)
    fun onDisconnected(room: Room, twilioException: TwilioException?)
    fun onReconnected(room: Room)
    fun onConnectFailure(room: Room, twilioException: TwilioException)
    fun onParticipantDisconnected(r: Room, remoteParticipant: RemoteParticipant)
}
