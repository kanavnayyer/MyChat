package com.awesome.mychat.ui.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import com.awesome.mychat.R
import com.awesome.mychat.util.Constants.FormatTimestamp
import com.awesome.mychat.util.Constants.FormattedLastSeen
import com.awesome.mychat.util.Constants.ImageUrlchat
import com.awesome.mychat.util.Constants.MessageAlignment
import com.awesome.mychat.util.Constants.imageUrl
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BindingAddapter {
    @JvmStatic
    @BindingAdapter(imageUrl)
    fun loadImage(view: ShapeableImageView, url: String?) {
        url?.let {
            Glide.with(view.context)
                .load(it)
                .placeholder(R.drawable.ic_profile)
                .into(view)
        }
    }


    @JvmStatic
    @BindingAdapter(FormattedLastSeen)
    fun formatLastSeen(textView: TextView, lastSeen: Long?) {
        lastSeen?.let {
            val date = Date(it)
            val sdf = SimpleDateFormat(textView.context.getString(R.string.hh_mm_a), Locale.getDefault())
            textView.text = "Last Seen: ${sdf.format(date)}"
        }
    }
    @JvmStatic
    @BindingAdapter(FormatTimestamp)
    fun formatTimestamp(view: TextView, timestamp: Long) {
        if (timestamp > 0) {
            val sdf = SimpleDateFormat(view.context.getString(R.string.hh_mm_a), Locale.getDefault())
            val formattedTime = sdf.format(Date(timestamp))
            view.text = formattedTime
        } else {
            view.text = ""
        }
    }
    
    @JvmStatic
    @BindingAdapter(MessageAlignment)
    fun setMessageAlignment(view: TextView, isSent: Boolean) {
        view.updateLayoutParams<ConstraintLayout.LayoutParams> {
       
            val maxWidth = (view.context.resources.displayMetrics.widthPixels * 0.7).toInt()
            view.maxWidth = maxWidth

            if (isSent) {
                marginStart = maxWidth / 3  
                marginEnd = 20
                horizontalBias = 1f
            } else {
                marginStart = 20
                marginEnd = maxWidth / 3  
                horizontalBias = 0f
            }
        }
    }

    @JvmStatic
    @BindingAdapter(MessageAlignment)
    fun setMessageAlignmentImage(view: ImageView, isSent: Boolean) {
        view.updateLayoutParams<ConstraintLayout.LayoutParams> {

            val maxWidth = (view.context.resources.displayMetrics.widthPixels * 0.7).toInt()
            view.maxWidth = maxWidth

            if (isSent) {
                marginStart = maxWidth / 3
                marginEnd = 20
                horizontalBias = 1f
            } else {
                marginStart = 20
                marginEnd = maxWidth / 3
                horizontalBias = 0f
            }
        }
    }

    @JvmStatic
    @BindingAdapter(ImageUrlchat)
    fun loadImage(view: ImageView, imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(view.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(view)
        } else {
            view.setImageResource(R.drawable.ic_profile)
        }
    }}