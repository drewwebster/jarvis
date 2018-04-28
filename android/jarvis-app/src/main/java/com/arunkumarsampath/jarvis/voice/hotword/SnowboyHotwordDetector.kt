package com.arunkumarsampath.jarvis.voice.hotword

import ai.kitt.snowboy.SnowboyDetect
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Application
import android.arch.lifecycle.Lifecycle.Event.*
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import android.os.Process
import android.os.Process.setThreadPriority
import android.support.v4.content.ContextCompat.checkSelfPermission
import com.arunkumarsampath.jarvis.di.scopes.PerActivity
import com.arunkumarsampath.jarvis.voice.hotword.HotwordDetector.HotwordEvent
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

@PerActivity
class SnowboyHotwordDetector
@Inject
constructor(
        private val application: Application,
        lifecycleOwner: LifecycleOwner
) : HotwordDetector, LifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    private var hotwordEventSubject: Subject<HotwordEvent> = BehaviorSubject.create<HotwordEvent>().toSerialized()

    override var hotwordEvents: Observable<HotwordEvent> = hotwordEventSubject
            .map { event ->
                if (event is HotwordEvent.Error) {
                    stop()
                    start()
                }
                event
            }

    private var recordingThread: Thread? = null
    private var recognitionActive = false

    private var snowboyDetector: SnowboyDetect? = null

    private fun initAssets() {
        val assetManager = application.assets
        if (!RECOGNITION_MODEL_FILE.exists()) {
            // Copy from asset to sdcard
            RECOGNITION_MODEL_FILE
                    .apply { parentFile.mkdirs() }
                    .copyInputStreamToFile(assetManager.open(RECOGNITION_MODEL_ASSET))
        }

        if (!COMMON_RES_FILE.exists()) {
            COMMON_RES_FILE
                    .apply { parentFile.mkdirs() }
                    .copyInputStreamToFile(assetManager.open(COMMON_RES_ASSET))
        }
    }

    @OnLifecycleEvent(ON_START)
    override fun start() {
        Timber.d("Start")
        if (recordingThread != null) {
            return
        }
        if (PERMISSION_GRANTED != checkSelfPermission(application, WRITE_EXTERNAL_STORAGE)
                || PERMISSION_GRANTED != checkSelfPermission(application, RECORD_AUDIO)) {
            hotwordEventSubject.onNext(HotwordEvent.Error())
            return
        }
        recognitionActive = true
        recordingThread = Thread { recordLoop() }.apply { start() }
    }

    @OnLifecycleEvent(ON_STOP)
    override fun stop() {
        Timber.d("Stop")
        if (recordingThread != null) {
            recognitionActive = false
            recordingThread = null
        }
    }

    @OnLifecycleEvent(ON_DESTROY)
    override fun cleanup(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.removeObserver(this)
        stop()
    }

    private fun recordLoop() {
        initAssets()
        setThreadPriority(Process.THREAD_PRIORITY_AUDIO)

        snowboyDetector = SnowboyDetect(
                COMMON_RES_FILE.absolutePath,
                RECOGNITION_MODEL_FILE.absolutePath
        ).apply {
            SetAudioGain(1F)
            SetSensitivity("0.4")
            ApplyFrontend(true)
        }

        var bufferSize = (SAMPLE_RATE * 0.1 * 2).toInt()
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2
        }
        val audioBuffer = ByteArray(bufferSize)

        val record = AudioRecord(
                MediaRecorder.AudioSource.DEFAULT,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize)

        if (record.state != AudioRecord.STATE_INITIALIZED) {
            Timber.e("Audio Record can't initialize!")
            hotwordEventSubject.onNext(HotwordEvent.Error())
            return
        }

        Timber.i("Start recording")

        record.startRecording()

        var shortsRead: Long = 0
        while (recognitionActive) {
            try {
                record.read(audioBuffer, 0, audioBuffer.size)

                // Converts to short array.
                val audioData = ShortArray(audioBuffer.size / 2)
                ByteBuffer.wrap(audioBuffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(audioData)

                shortsRead += audioData.size.toLong()

                val result = snowboyDetector!!.RunDetection(audioData, audioData.size)

                when {
                    result == SNOWBOY_ERROR -> {
                        hotwordEventSubject.onNext(HotwordEvent.Error())
                    }
                    result > SNOWBOY_DETECTED_THRESHOLD -> {
                        hotwordEventSubject.onNext(HotwordEvent.Detected())
                        Timber.i("Hotword ${Integer.toString(result)} detected!")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                hotwordEventSubject.onNext(HotwordEvent.Error())
            }
        }

        record.stop()
        record.release()
        snowboyDetector?.Reset()
    }

    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        inputStream.use { input ->
            this.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }

    companion object {
        private val STORAGE_LOCATION = Environment.getExternalStorageDirectory().absolutePath + "/jarvis/snowboy"
        const val RECOGNITION_MODEL_ASSET = "jarvis.pmdl"
        val RECOGNITION_MODEL_FILE = File(STORAGE_LOCATION + File.separator + RECOGNITION_MODEL_ASSET)
        const val COMMON_RES_ASSET = "common.res"
        val COMMON_RES_FILE = File(STORAGE_LOCATION + File.separator + COMMON_RES_ASSET)
        const val SAMPLE_RATE = 16000
        const val SNOWBOY_ERROR = -1
        const val SNOWBOY_DETECTED_THRESHOLD = 0

        init {
            System.loadLibrary("snowboy-detect-android")
        }
    }
}