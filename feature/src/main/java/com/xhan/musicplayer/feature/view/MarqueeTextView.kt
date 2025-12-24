package com.xhan.musicplayer.feature.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/** Marquee 애니메이션이 자동으로 활성화된 TextView */
class MarqueeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        ellipsize = TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = -1 // marquee_forever
        isSingleLine = true

        isSelected = true
        isFocusable = true
        isFocusableInTouchMode = true
    }
}