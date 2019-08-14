package com.example.deliveryapp.ui.track_delivery

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.databinding.AdapterDeliveryTimelineBinding
import com.github.vipulasri.timelineview.TimelineView
import kotlinx.android.synthetic.main.adapter_delivery_timeline.view.*
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.deliveryapp.R


class DeliveryTimelineAdapter(private val delivery: Delivery) : RecyclerView.Adapter<DeliveryTimelineAdapter.TimeLineViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = DataBindingUtil.inflate<AdapterDeliveryTimelineBinding>(
            layoutInflater, R.layout.adapter_delivery_timeline, parent, false
        )
        return TimeLineViewHolder(binding,viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
        holder.bindTimelineEvent(position)
    }

    inner class TimeLineViewHolder(private var binding: AdapterDeliveryTimelineBinding,
                                   private var viewType: Int) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.timeline.initLine(viewType)
        }

        fun bindTimelineEvent(position: Int){

            when(position){
                0-> {
                    binding.deliveryStatusText.text = itemView.context.getString(R.string.delivery_order_placed)
                    binding.deliveryStatusIcon.setImageResource(R.drawable.delivery_placed)
                }
                1-> {
                    binding.deliveryStatusText.text = itemView.context.getString(R.string.package_in_transit)
                    binding.deliveryStatusIcon.setImageResource(R.drawable.delivery_in_transit)
                }
                2-> {
                    if(delivery.deliveryStatus == Delivery.STATUS_CANCELLED){
                        binding.deliveryStatusText.text = itemView.context.getString(R.string.delivery_cancelled)
                        binding.deliveryStatusIcon.setImageResource(R.drawable.delivery_cancelled)
                    }else {
                        binding.deliveryStatusText.text = itemView.context.getString(R.string.delivery_complete)
                        binding.deliveryStatusIcon.setImageResource(R.drawable.idelivery_completed)
                    }
                }
            }

            @DrawableRes val timelineResMarker :Int
            @ColorRes val markerResColor:Int
            @ColorRes val textResColor:Int
            @ColorRes val lineResColor:Int

            if(delivery.deliveryStatus == Delivery.STATUS_CANCELLED){
                timelineResMarker = R.drawable.ic_marker_complete
                markerResColor = R.color.grey
                textResColor = R.color.grey
                lineResColor = R.color.grey

            } else if(position < delivery.deliveryStatus){
                timelineResMarker = R.drawable.ic_marker_complete
                markerResColor = R.color.colorPrimary
                textResColor = R.color.black
                lineResColor = R.color.blue

            }else if(position == delivery.deliveryStatus){
                timelineResMarker = R.drawable.ic_marker_complete
                markerResColor = R.color.colorAccent
                textResColor = R.color.blue
                lineResColor = R.color.colorAccent

            }else{
                timelineResMarker = R.drawable.ic_marker_incomplete
                markerResColor = R.color.grey
                textResColor = R.color.grey
                lineResColor = R.color.grey
            }

            setMarker(timelineResMarker,markerResColor)
            val textColor = ContextCompat.getColor(itemView.context,textResColor)
            val lineColor = ContextCompat.getColor(itemView.context,lineResColor)
            binding.deliveryStatusText.setTextColor(textColor)
            binding.timeline.setEndLineColor(lineColor,viewType)
            binding.timeline.setStartLineColor(lineColor,viewType)

            binding.executePendingBindings()
        }

        private fun setMarker(drawableResId: Int, colorFilter: Int) {
            binding.timeline.marker = VectorDrawableUtils.getDrawable(itemView.context, drawableResId, colorFilter)
        }


    }
}

object VectorDrawableUtils {

    fun getDrawable(context: Context, drawableResId: Int): Drawable? {
        return VectorDrawableCompat.create(context.resources, drawableResId, context.theme)
    }

    fun getDrawable(context: Context, drawableResId: Int, colorFilter: Int): Drawable {
        val drawable = getDrawable(context, drawableResId)
        drawable!!.setColorFilter(ContextCompat.getColor(context, colorFilter), PorterDuff.Mode.SRC_IN)
        return drawable
    }

}