package com.example.kidsdrawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
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

}