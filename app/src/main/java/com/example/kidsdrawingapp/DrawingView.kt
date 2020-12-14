package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

// inherit views, with primary constructor of `Context` and `AttributeSet`
class DrawingView(context: Context, attrs: AttributeSet): View(context, attrs) {

    private var mDrawPath: CustomPath? = null
    // Need Three built-in Objects in order to create drawing app
    /*
        - Bitmap
        - Paint
        - Canvas
    * */
    private var mCanvasBitmap: Bitmap? = null
    private var canvas: Canvas? = null // The Canvas class holds the "draw" calls. To draw something, you need 4 basic components: A Bitmap to hold the pixels, a Canvas to host the draw calls (writing into the bitmap), a drawing primitive (e.g. Rect, Path, text, Bitmap), and a paint (to describe the colors and styles for the drawing).
    private var mDrawPaint: Paint? = null // The Paint class holds the style and color information about how to draw geometries, text and bitmaps.
    private var mCanvasPaint: Paint? = null


    // Default Values
    private var mBrushSize: Float = 0.toFloat()
    private var color: Int = Color.BLACK

    // ** persistent drawing **
    private val mPaths = ArrayList<CustomPath>()
    // ** undo path **
    private val mUndoPaths = ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    fun onClickUndo() {
        if(mPaths.size > 0) {
            mUndoPaths.add(mPaths.removeAt(mPaths.size - 1))
            invalidate()
        }
    }

    private fun setUpDrawing() {
        mDrawPath = CustomPath(color, mBrushSize)
        mDrawPaint = Paint()
        //Basic Initialization of `mDrawPaint`
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        //Basic Initialization of `mCanvasPaint`
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        mBrushSize = 20.toFloat()
    }

    // initialize `mCanvasBitmap` , `canvas`
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    // Change Canvas to Canvas? if fails
    // draw something
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)

        // ** persistent drawing **
        for(path in mPaths) {
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }

        if(!mDrawPath!!.isEmpty) {
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize
                mDrawPath!!.reset() //make it empty
                if(touchX != null) {
                    if(touchY != null) {
                        mDrawPath!!.moveTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                // ** persistent drawing **
                mPaths.add(mDrawPath!!)

                mDrawPath = CustomPath(color, mBrushSize)

            }
            else -> return false
        }
        invalidate()
        return true
    }

    // Change Brush Size
    fun setSizeForBrush(newSize: Float) {
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics)
        mDrawPaint!!.strokeWidth = mBrushSize
    }

    // set Color
    fun setColor(newColor: String) {
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }

    // internal: only available in DrawingView class
    // inner: use outer class's properties
    // for declaring properties and initializing them from the primary constructor, Kotlin has a concise syntax:
    internal inner class CustomPath(
        var color: Int, // using keyword `var`, initialize it on the spot
        var brushThickness: Float): Path() {

    }

}