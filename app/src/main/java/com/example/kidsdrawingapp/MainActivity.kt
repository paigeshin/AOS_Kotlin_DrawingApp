package com.example.kidsdrawingapp

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_brush_size.*

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
            } else {
                requestStoragePermission()
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

    companion object {
        private const val STORAGE_PERMISSION_CODE = 1

    }

}