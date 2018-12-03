package edu.uw.acevedoj.sos

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder


class SoundRecorderFragment(): Fragment() {

    private  val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 0
    private  val MY_PERMISSIONS_REQUEST_WRITE_TO_STORAGE = 1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_sound_recorder, container, false)

        checkForRecordPermission()
        checkForWriteToExternaStorage()

        val filePath = Environment.getExternalStorageDirectory().toString() + "/recorded_audio.wav"

        val color = getResources().getColor(R.color.colorPrimaryDark)
        val requestCode = 0
        AndroidAudioRecorder.with(this)
            // Required
            .setFilePath(filePath)
            .setColor(color)
            .setRequestCode(requestCode)

        return root


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // Great! User has recorded and saved the audio file
            } else if (resultCode == RESULT_CANCELED) {
                // Oops! User has canceled the recording
            }
        }
    }

    private fun checkForRecordPermission() {

        if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), android.Manifest.permission.RECORD_AUDIO)){


            } else {
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(android.Manifest.permission.RECORD_AUDIO),
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO
                )
            }

        }

    }

    private fun checkForWriteToExternaStorage() {

        if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){


            } else {
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_WRITE_TO_STORAGE
                )
            }

        }

    }

}