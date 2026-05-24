package com.momentjournal.util

import android.content.Context
import android.media.MediaRecorder
import java.io.File

class MediaManager(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null

    fun getMediaDir(): File {
        val dir = File(context.filesDir, "media")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun createImageFile(): File {
        return File(getMediaDir(), "img_${System.currentTimeMillis()}.jpg")
    }

    fun createVideoFile(): File {
        return File(getMediaDir(), "vid_${System.currentTimeMillis()}.mp4")
    }

    fun createVoiceFile(): File {
        return File(getMediaDir(), "voice_${System.currentTimeMillis()}.m4a")
    }

    fun startRecording(file: File) {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }
}
