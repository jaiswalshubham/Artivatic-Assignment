package ai.artivatic.assignment

import android.app.AlertDialog
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_text_recognizer.*


class TextRecognizerActivity : AppCompatActivity() {

    private lateinit var bitmap: Bitmap
    private lateinit var mutableBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_recognizer)

        bitmap = (application as MyApplication).bitmap

        imageView.setOnTouchListener(ImageMatrixTouchHandler(baseContext))

        imageView.setImageBitmap(bitmap)

        processBitmap(bitmap)

        back_btn.setOnClickListener {
            recapture()
        }

        resetBitmap()

    }

    private fun recapture() {
        startActivity(Intent(this@TextRecognizerActivity, MainActivity::class.java))
        finish()
    }

    private fun resetBitmap() {
        mutableBitmap = bitmap.copy(bitmap.config, true)
    }

    private fun processBitmap(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val detector = FirebaseVision.getInstance()
            .onDeviceTextRecognizer

        detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->
                processText(firebaseVisionText)
            }
            .addOnFailureListener {
                showNoContentAlert()
            }
    }

    private fun processText(firebaseVisionText: FirebaseVisionText) {
        var allText = "";
        for (block in firebaseVisionText.textBlocks) {
            allText += block.text
        }
        searchText.setText(allText)
        if(allText == ""){
            showNoContentAlert()
        }
    }

    private fun showNoContentAlert() {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog)
        } else {
            AlertDialog.Builder(this)
        }
        builder.setMessage(getString(R.string.no_text_found_text))
            .setPositiveButton(getString(R.string.recapture)) { dialog, _ ->
                recapture()
                dialog.dismiss()
            }
        runOnUiThread {
            builder.show()
        }
    }
}
