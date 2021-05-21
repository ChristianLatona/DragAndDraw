package com.christianlatona.android.draganddraw

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

private const val TAG = "BoxDrawingView"
private const val VIEW_STATE = "VIEW_STATE"
private const val BOXEN = "BOXEN"

class BoxDrawingView(context: Context, attrs: AttributeSet? = null):
    View(context, attrs) {

    private var currentBox: Box? = null
    private var boxen = mutableListOf<Box>()
    private val backgroundPaint = Paint().apply {
        color = 0xfff8efe0.toInt()
    }
    private val boxPaint = Paint().apply {
        color = 0x22ff0000
    }
    private var isRotationMode = false
    private var rotationDegreeStart = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val current = PointF(event.x, event.y)
        var action = ""
        val pointerIndex = event.actionIndex // you can use this data with getPointerID(Int)
        //and other functions
        val pointerId = event.getPointerId(pointerIndex)

        when (event.action and event.actionMasked) { // i think actionMasked is used for multi-touch
            MotionEvent.ACTION_DOWN -> {
                action = "ACTION_DOWN"
                // Reset drawing state
                currentBox = Box(current).also {
                    boxen.add(it)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                action = "ACTION_MOVE"
                updateCurrentBox(current)
            }
            MotionEvent.ACTION_UP -> {
                action = "ACTION_UP"
                updateCurrentBox(current)
                currentBox = null
            }
            MotionEvent.ACTION_CANCEL -> {
                action = "ACTION_CANCEL"
                currentBox = null
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                action = "ACTION_POINTER_DOWN"
                if (pointerId == 1){
                    rotationDegreeStart = current.y
                    isRotationMode = true
                    updateCurrentBox(current)
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                action = "ACTION_POINTER_UP"
                isRotationMode = false
                currentBox = null
            }
        }
        Log.i(TAG, "$action at x=${current.x}, y=${current.y}")
        return true
    }

    override fun onDraw(canvas: Canvas) {
        // fill the background
        canvas.drawPaint(backgroundPaint)
        boxen.forEach { box ->
            canvas.apply{
                save() // how fucking time spent for this save()
                rotate(box.rotation, (box.right + box.left)/2, (box.bottom + box.top)/2)
                drawRect(box.left, box.top, box.right, box.bottom, boxPaint)
                restore()
            }
        }
    }

    // these functions would get called only if the view has an ID
    override fun onSaveInstanceState(): Parcelable {
        val state = super.onSaveInstanceState()
        val bundle = Bundle()
        bundle.putParcelable(VIEW_STATE, state)
        bundle.putParcelableArrayList(BOXEN, ArrayList(boxen)) // casting
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle){
            boxen = state.getParcelableArrayList<Box>(BOXEN)?.toMutableList() ?: mutableListOf()
            super.onRestoreInstanceState(state.getParcelable(VIEW_STATE))
        }
    }

    private fun updateCurrentBox(current: PointF) {
        currentBox?.let {
            if (isRotationMode) {
                it.rotation = current.y - rotationDegreeStart
            }else{
                it.end = current
            }
            invalidate() // this forces view to redraw itself, so you
            // can see the dragged box
        }
    }
}