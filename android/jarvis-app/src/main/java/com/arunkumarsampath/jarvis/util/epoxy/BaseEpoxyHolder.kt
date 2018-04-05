package com.arunkumarsampath.jarvis.util.epoxy

import android.support.annotation.CallSuper
import android.view.View
import butterknife.ButterKnife
import com.airbnb.epoxy.EpoxyHolder

abstract class BaseEpoxyHolder : EpoxyHolder() {
    @CallSuper
    override fun bindView(itemView: View) {
        ButterKnife.bind(this, itemView)
    }
}