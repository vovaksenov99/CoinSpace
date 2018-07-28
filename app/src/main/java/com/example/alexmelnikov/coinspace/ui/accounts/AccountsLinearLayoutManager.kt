package com.example.alexmelnikov.coinspace.ui.accounts

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet

class AccountsLinearLayoutManager : LinearLayoutManager {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    override fun supportsPredictiveItemAnimations(): Boolean {
        return true
    }
}
