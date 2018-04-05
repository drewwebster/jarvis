package com.arunkumarsampath.jarvis.data.chat

import android.view.View
import android.widget.TextView
import butterknife.BindView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.arunkumarsampath.jarvis.R
import com.arunkumarsampath.jarvis.util.epoxy.BaseEpoxyHolder

@EpoxyModelClass(layout = R.layout.layout_conversation_item)
internal abstract class ConversationModel : EpoxyModelWithHolder<ConversationModel.Holder>() {
    @EpoxyAttribute
    lateinit var message: String
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var clickListener: View.OnClickListener? = null

    override fun bind(holder: Holder) {
        holder.conversationText.text = message
        holder.conversationText.setOnClickListener(clickListener)
    }

    internal class Holder : BaseEpoxyHolder() {
        @BindView(R.id.conversationText)
        lateinit var conversationText: TextView
    }
}