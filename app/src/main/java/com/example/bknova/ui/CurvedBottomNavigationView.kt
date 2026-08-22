package com.example.bknova.ui

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.bknova.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class CurvedBottomNavigationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr) {

    private val mPath: Path = Path()
    private val mPaint: Paint = Paint()
    private val mCirclePaint: Paint = Paint()

    private val CURVE_CIRCLE_RADIUS = 90f
    private var mNavigationBarWidth = 0
    private var mNavigationBarHeight = 0
    
    private var mSelectedX = 0f
    private var mAnimator: ValueAnimator? = null
    
    // Posisi vertikal bar putih
    private val BAR_MARGIN_VERTICAL = 40f
    private val BAR_HEIGHT = 160f

    init {
        mPaint.apply {
            style = Paint.Style.FILL
            color = Color.WHITE
            isAntiAlias = true
            setShadowLayer(15f, 0f, 8f, Color.parseColor("#25000000"))
        }
        
        mCirclePaint.apply {
            style = Paint.Style.FILL
            color = Color.WHITE
            isAntiAlias = true
            setShadowLayer(15f, 0f, -5f, Color.parseColor("#20000000"))
        }

        setLayerType(LAYER_TYPE_SOFTWARE, null)
        setBackgroundColor(Color.TRANSPARENT)
        
        itemActiveIndicatorColor = ColorStateList.valueOf(Color.TRANSPARENT)
        clipChildren = false
        clipToPadding = false
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        
        val menuView = getChildAt(0) as? ViewGroup
        menuView?.clipChildren = false
        menuView?.clipToPadding = false
        
        for (i in 0 until (menuView?.childCount ?: 0)) {
            val itemView = menuView?.getChildAt(i) as? ViewGroup
            itemView?.clipChildren = false
            itemView?.clipToPadding = false
            // M3 icon container
            val iconContainer = itemView?.findViewById<View>(com.google.android.material.R.id.navigation_bar_item_icon_container)
            (iconContainer?.parent as? ViewGroup)?.clipChildren = false
        }
        
        updateIconsVerticalPosition()
    }

    private fun updateIconsVerticalPosition() {
        val menuView = getChildAt(0) as? ViewGroup ?: return
        for (i in 0 until menuView.childCount) {
            val itemView = menuView.getChildAt(i) ?: continue
            val isSelected = itemView.isSelected
            
            // Ikon terpilih naik ke atas (melayang di lingkaran)
            // Ikon tidak terpilih tetap di tengah bar
            val targetTranslationY = if (isSelected) -45f else 15f
            
            itemView.animate()
                .translationY(targetTranslationY)
                .setDuration(300)
                .start()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mNavigationBarWidth = w
        mNavigationBarHeight = h
        post {
            mSelectedX = getSelectedItemX(selectedItemId)
            invalidate()
        }
    }

    private fun getSelectedItemX(itemId: Int): Float {
        try {
            val menuView = getChildAt(0) as? ViewGroup
            if (menuView != null) {
                for (i in 0 until menuView.childCount) {
                    val itemView = menuView.getChildAt(i)
                    if (itemView.id == itemId) {
                        val location = IntArray(2)
                        itemView.getLocationInWindow(location)
                        val parentLocation = IntArray(2)
                        this.getLocationInWindow(parentLocation)
                        return (location[0] - parentLocation[0]) + (itemView.width / 2f)
                    }
                }
            }
        } catch (e: Exception) { }
        return mNavigationBarWidth / 2f
    }

    override fun onDraw(canvas: Canvas) {
        val top = BAR_MARGIN_VERTICAL
        val bottom = top + BAR_HEIGHT
        val left = 40f
        val right = mNavigationBarWidth - 40f
        val cornerRadius = BAR_HEIGHT / 2f

        mPath.reset()
        
        // Gambar Kapsul Putih
        val rectF = RectF(left, top, right, bottom)
        mPath.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW)
        
        // Gambar Lingkaran Pop-out untuk item terpilih
        canvas.drawCircle(mSelectedX, top, CURVE_CIRCLE_RADIUS, mCirclePaint)
        
        // Gambar Bar Utama
        canvas.drawPath(mPath, mPaint)
        
        super.onDraw(canvas)
    }

    fun onItemSelected(itemId: Int) {
        post {
            val targetX = getSelectedItemX(itemId)
            if (targetX != mSelectedX) {
                mAnimator?.cancel()
                mAnimator = ValueAnimator.ofFloat(mSelectedX, targetX).apply {
                    duration = 400
                    addUpdateListener {
                        mSelectedX = it.animatedValue as Float
                        invalidate()
                    }
                    start()
                }
            }
            updateIconsVerticalPosition()
        }
    }
}
