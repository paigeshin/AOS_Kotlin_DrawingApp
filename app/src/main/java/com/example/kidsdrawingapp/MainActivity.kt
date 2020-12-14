package com.example.kidsdrawingapp

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_brush_size.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var mImageButtonCurrentPaint: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView.setSizeForBrush(20.toFloat())

        //llPaintColors is linearlayout. you can retrieve its element as if it were array
        mImageButtonCurrentPaint = llPaintColors[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )

        ibBrush.setOnClickListener {
            showBrushSizeChooseDialog()
        }

        ibGallery.setOnClickListener {
            if(isReadStorageAllowed()) {
                // run our code to get the image from the gallery
                // pick image from gallery
                val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhotoIntent, GALLERY)
            } else {
                requestStoragePermission()
            }
        }

        ibUndo.setOnClickListener {
            drawingView.onClickUndo()
        }

        ibSave.setOnClickListener {
            if(isReadStorageAllowed()) {
                BitmapAsyncTask(getBitmapFromView(flDrawingViewContainer)).execute()
            } else {
                requestStoragePermission()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == GALLERY) {
                try {
                    if(data != null) {
                        if(data.data != null) {
                            ivBackground.visibility = View.VISIBLE
                            ivBackground.setImageURI(data.data) //URI of your device
                            return
                        }
                    }
                    Toast.makeText(this, "Error in parseing the image or it's corrupted.", Toast.LENGTH_SHORT).show()
                } catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    //Show Custom Dialog with prepared layout
    private fun showBrushSizeChooseDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size: ")
        val smallButton = brushDialog.ibSmallBrush
        smallButton.setOnClickListener{
            drawingView.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }
        val mediumButton = brushDialog.ibMediumBrush
        mediumButton.setOnClickListener {
            drawingView.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }
        val largeButton = brushDialog.ibLargeBrush
        largeButton.setOnClickListener {
            drawingView.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()
    }

    fun paintClicked(view: View) {
        //이미 선택된 버튼이 아니라면 ...
        if(view !== mImageButtonCurrentPaint) {
            val imagebutton = view as ImageButton
            val colorTag = imagebutton.tag.toString()
            drawingView.setColor(colorTag)
            //change current the color of selected button
            imagebutton.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
            )
            mImageButtonCurrentPaint!!.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.pallet_normal)
            )
            mImageButtonCurrentPaint = view
        }

    }

    private fun requestStoragePermission() {
        val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionString = permission.toString()
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, permissionString)) {
            Toast.makeText(this, "Need permission to add a Background image", Toast.LENGTH_SHORT).show()
        }
        ActivityCompat.requestPermissions(this, permission, STORAGE_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == STORAGE_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted now you can read the storage files", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Oops you just denied the permission.", Toast.LENGTH_LONG).show()
            }
//            if(grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission granted now you can write the storage files", Toast.LENGTH_LONG).show()
//            }
        }
    }

    private fun isReadStorageAllowed() : Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun getBitmapFromView(view: View) : Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap) // prepare for canvas

        //if there's a background, draw on the canvas
        val bgDrawable = view.background
        if(bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)

        return returnedBitmap
    }

    private inner class BitmapAsyncTask(val mBitmap: Bitmap) : AsyncTask<Any, Void, String>() {

        // ** custom progress bar **
        private lateinit var mProgressDialog: Dialog

        override fun onPreExecute() {
            super.onPreExecute()

            showProgressDialog()

        }

        override fun doInBackground(vararg params: Any?): String {

            var result = ""

            //save file into the background
            try {
                val bytes:ByteArrayOutputStream = ByteArrayOutputStream()
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
                val file = File(
                            externalCacheDir!!.absoluteFile.toString()
                            + File.separator + "KidDrawingApp_" + System.currentTimeMillis() / 1000
                            + ".png"
                        )
                val fileOutput = FileOutputStream(file)
                fileOutput.write(bytes.toByteArray())
                fileOutput.close()
                result = file.absolutePath
            } catch (e: Exception) {
                result = ""
                e.printStackTrace()
            }

            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            cancelProgressDialog()

            if(!result!!.isEmpty()) {
                Toast.makeText(this@MainActivity, "File saved successfully :$result", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Something went wrong while saving the file. :$result", Toast.LENGTH_SHORT).show()
            }

            // ** Share & Email Functionality **
            MediaScannerConnection.scanFile(this@MainActivity, arrayOf(result), null) { path, uri ->
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                shareIntent.type = "image/png"
                startActivity(Intent.createChooser(shareIntent, "Share"))
            }

        }

        // ** custom progress bar **
        private fun showProgressDialog() {
            mProgressDialog = Dialog(this@MainActivity)
            mProgressDialog.setContentView(R.layout.custom_progress_bar)
            mProgressDialog.show()
        }

        // ** custom progress bar **
        private fun cancelProgressDialog() {
            mProgressDialog.dismiss()
        }

    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 1
        private const val GALLERY = 2
    }

}