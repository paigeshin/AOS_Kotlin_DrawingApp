# Custom View

```kotlin
package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
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

    init {
        setUpDrawing()
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
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }
        invalidate()
        return true
    }

    // internal: only available in DrawingView class
    // inner: use outer class's properties
    // for declaring properties and initializing them from the primary constructor, Kotlin has a concise syntax:
    internal inner class CustomPath(
        var color: Int, // using keyword `var`, initialize it on the spot
        var brushThickness: Float): Path() {

    }

}
```

# XML

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <com.example.kidsdrawingapp.DrawingView
        android:id="@+id/drawingView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

# Persistent Drawing

```kotlin

package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
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

    init {
        setUpDrawing()
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

    // internal: only available in DrawingView class
    // inner: use outer class's properties
    // for declaring properties and initializing them from the primary constructor, Kotlin has a concise syntax:
    internal inner class CustomPath(
        var color: Int, // using keyword `var`, initialize it on the spot
        var brushThickness: Float): Path() {

    }

}

```

# Custom Background Drawable Resource

- background_drawing_view_layout0

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="#FFFFFF"/>
    <stroke
        android:width="0.5dp"
        android:color="#9AA2AF"/>
</shape>
```

- XML

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <com.example.kidsdrawingapp.DrawingView
        android:id="@+id/drawingView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_margin="5dp"
        android:background="@drawable/background_drawing_view_layout"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

# Drawable/small.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:dither="true"
    android:shape="oval">

    <size
        android:height="10dp"
        android:width="10dp"/>

    <solid android:color="#FF6666"/>

</shape>
```

# ScaleType Android

![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/4360f037-ad4b-4503-bca7-a285e679bc32/scaleType_imageButton.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/4360f037-ad4b-4503-bca7-a285e679bc32/scaleType_imageButton.png)

# Implement Custom Dialog

```kotlin
package com.example.kidsdrawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_brush_size.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
```

- Custom Dialog

```kotlin
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
```

# Layer-List, in drawable

- Use `Layer-List` if you want to nest different shapes on top of each other.
- drawable/pallet_normal

```xml
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <shape android:shape="rectangle">
            <stroke
                android:shape="rectangle"
                android:color="#FF9999"/>
            <solid
                android:color="#00000000"/>
        </shape>
    </item>
    <item>
        <shape android:shape="rectangle">
            <stroke
                android:width="2dp"
                android:color="#FF9999"/>
            <solid
                android:color="#00000000"/>
            <corners
                android:radius="10dp"/>
        </shape>
    </item>
</layer-list>
```

# LinearLayout, retrieve child element

```kotlin
//llPaintColors is linearlayout. you can retrieve its element as if it were array
mImageButtonCurrentPaint = llPaintColors[1] as ImageButton
```

# Get Drawable Resource

```kotlin
mImageButtonCurrentPaint!!.setImageDrawable(
        ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
)
```

# Implement Colors

@keyword ⇒ `color picker`

- MainActivity

```kotlin
package com.example.kidsdrawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
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

}
```

- DrawingView

```kotlin
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

    init {
        setUpDrawing()
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
```

# UIComponents to check out

- Snackbar
- custom progress bar

# Permission

```kotlin
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
```

# onActivityForResult for gallery

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

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
```

# ImageView

- centerCrop - aspect fill

# Save Image into the local

1. Create `path.xml` in res folder

    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <paths xmlns:android="http://schemas.android.com/apk/res/android">
        <external-path
            name="captured"
            path="Android/data/com.example.kidsdrawingapp/files" />
    </paths>
    ```

2. write it down on manifest

    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="com.example.kidsdrawingapp">

        <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

        <application
            android:allowBackup="true"
            android:icon="@mipmap/ic_launcher"
            android:label="@string/app_name"
            android:roundIcon="@mipmap/ic_launcher_round"
            android:supportsRtl="true"
            android:theme="@style/Theme.KidsDrawingApp">
            <activity android:name=".MainActivity"
                android:screenOrientation="portrait">
                <intent-filter>
                    <action android:name="android.intent.action.MAIN" />

                    <category android:name="android.intent.category.LAUNCHER" />
                </intent-filter>

            </activity>

            <provider
                android:authorities="com.example.kidsdrawingapp.fileprovider"
                android:name="androidx.core.content.FileProvider"
                android:exported="false"
                android:grantUriPermissions="true">
                <meta-data
                    android:name="android.support.FILE_PROVIDER_PATHS"
                    android:resource="@xml/path" />
            </provider>

        </application>

    </manifest>
    ```

    - on MainActivity, prepare this code

    ```kotlin
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
    ```

3. Write code using AsyncTask

    ```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ibSave.setOnClickListener {
            if(isReadStorageAllowed()) {
                BitmapAsyncTask(getBitmapFromView(flDrawingViewContainer)).execute()
            } else {
                requestStoragePermission()
            }
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
    	
    	      if(!result!!.isEmpty()) {
    	          Toast.makeText(applicationContext, "File saved successfully :$result", Toast.LENGTH_SHORT).show()
    	      } else {
    	          Toast.makeText(applicationContext, "Something went wrong while saving the file. :$result", Toast.LENGTH_SHORT).show()
    	      }
    	
    	  }

    }
    ```

### Grant Permission Error

`java.lang.SecurityException: Provider must grant uri permissions`

```xml
<!-- add grantUriPermissions -->
<provider
    android:authorities="com.example.kidsdrawingapp.fileprovider"
    android:name="androidx.core.content.FileProvider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/path" />
</provider>
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.kidsdrawingapp">

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.KidsDrawingApp">
        <activity android:name=".MainActivity"
            android:screenOrientation="portrait">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>

        </activity>

        <provider
            android:authorities="com.example.kidsdrawingapp.fileprovider"
            android:name="androidx.core.content.FileProvider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/path" />
        </provider>

    </application>

</manifest>
```

# Add Custom ProgressBar

- custom_progress_bar.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!--TODO(Step 1 : Creating a view for custom progress dialog)-->
<!--START-->
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:gravity="center"
    android:orientation="horizontal"
    android:padding="10dp">

    <ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="50dp"
        android:layout_height="50dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toStartOf="@+id/textView"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginLeft="16dp"
        android:text="Please Wait..."
        android:textColor="@android:color/black"
        android:textSize="16sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toEndOf="@+id/progressBar"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
<!--END-->
```

```kotlin
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
```

# Share

```kotlin
// ** Share & Email Functionality **
MediaScannerConnection.scanFile(this@MainActivity, arrayOf(result), null) { path, uri ->
    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
    shareIntent.type = "image/png"
    startActivity(Intent.createChooser(shareIntent, "Share"))
}
```