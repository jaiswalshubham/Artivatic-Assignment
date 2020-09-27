package ai.artivatic.assignment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import com.otaliastudios.cameraview.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_text_recognizer.*


class MainActivity : AppCompatActivity() {

    val REQUEST_CODE = 100;
    val PERMISSION_CODE_READ = 1000;
    val PERMISSION_CODE_WRITE = 1001;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraView.setLifecycleOwner(this)
        cameraView.open()
        cameraView.mode = Mode.PICTURE
        cameraView.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER) // Tap to focus!

        cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                result.toBitmap(900, 1800) { bitmap ->
                    if (bitmap != null) {
                        (application as MyApplication).bitmap = bitmap
                        val intent = Intent(this@MainActivity, TextRecognizerActivity::class.java)
                        startActivity(intent)
                        cameraView.close()
                    } else {
                        Toast.makeText(
                            baseContext,
                            "Something went wrong, Try again later!",
                            LENGTH_LONG
                        ).show()
                    }
                }
            }
        })

        captureButton.setOnClickListener {
            cameraView.takePicture()
        }
        openGallary.setOnClickListener {

            openGalleryForImage();
        }
    }
    private fun checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                val permissionCoarse = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                requestPermissions(permission, PERMISSION_CODE_READ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_READ LIKE 1001
                requestPermissions(permissionCoarse, PERMISSION_CODE_WRITE) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_WRITE LIKE 1002
            } else {
                openGalleryForImage()
            }
        }
    }
    private fun openGalleryForImage() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            (application as MyApplication).bitmap =  BitmapFactory.decodeStream(getContentResolver().openInputStream(data?.data))// handle chosen image
            val intent = Intent(this@MainActivity, TextRecognizerActivity::class.java)
            startActivity(intent)
            cameraView.close()
        }else if(requestCode == PERMISSION_CODE_READ || requestCode == PERMISSION_CODE_WRITE){
            checkPermissionForImage()
        }
    }
    override fun onBackPressed() {
        (application as MyApplication).showBackButtonActionDialog(this, this@MainActivity)
    }
}
