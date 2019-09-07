package com.example.deliveryapp.ui.track_delivery

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.test.espresso.idling.CountingIdlingResource
import com.example.deliveryapp.R
import com.example.deliveryapp.data.local.entities.Delivery
import com.example.deliveryapp.databinding.ActivityTrackDeliveryBinding
import com.example.deliveryapp.di.Injectable
import com.example.deliveryapp.utils.ViewModelFactory
import dagger.android.AndroidInjection
import io.acsint.heritageGhana.MtnHeritageGhanaApp.data.remote.Status
import javax.inject.Inject

class TrackDeliveryActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var trackDeliveryViewModel: TrackDeliveryViewModel

    lateinit var mDelivery: Delivery
    lateinit var binding:ActivityTrackDeliveryBinding

    private lateinit var timelineAdapter:DeliveryTimelineAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidInjection.inject(this)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_track_delivery)

        binding.toolbar.title = getString(R.string.track_delivery)
        binding.toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        trackDeliveryViewModel = ViewModelProviders.of(this,viewModelFactory).get(TrackDeliveryViewModel::class.java)

        mDelivery = intent.getSerializableExtra(DELIVERY_EXTRA) as Delivery

        binding.delivery = mDelivery

        setUpDeliveryTimeline()

        setUpTrackDeliveryDate()

        startObservers()
    }

    private fun setUpTrackDeliveryDate(){

        when(mDelivery.deliveryStatus){
            Delivery.STATUS_PLACED ->{
                binding.trackDateTitle.text = getString(R.string.pickup_date_title)
                binding.itemEtaTextview.text = mDelivery.pickUpTimeDate!!.getDateFormat1()
            }
            Delivery.STATUS_IN_TRANSIT ->{
                binding.trackDateTitle.text = getString(R.string.estimated_time_of_arrival)
                binding.itemEtaTextview.text = mDelivery.estimatedTimeOfArrivalDate!!.getDateFormat1()
            }
            Delivery.STATUS_COMPLETED ->{
                binding.trackDateTitle.text = getString(R.string.delivered_on_title)
                binding.itemEtaTextview.text = mDelivery.deliveryTimeDate!!.getDateFormat1()
            }
            Delivery.STATUS_CANCELLED ->{
                binding.trackDateTitle.text = getString(R.string.cancelled_on)
                binding.itemEtaTextview.text = mDelivery.deliveryTimeDate!!.getDateFormat1()
            }
        }
    }

    private fun startObservers(){
        trackDeliveryViewModel.getNetWorkState().observe(this, Observer {
            if(it.status == Status.SUCCESS) finish()
        })
    }

    private fun stopObservers(){
        trackDeliveryViewModel.getNetWorkState().removeObservers(this)
    }

    private fun setUpDeliveryTimeline(){

        timelineAdapter = DeliveryTimelineAdapter(mDelivery)
        layoutManager = LinearLayoutManager(this)

        binding.deliveryTimelineRecycler.adapter = timelineAdapter
        binding.deliveryTimelineRecycler.layoutManager = layoutManager

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        if(mDelivery.deliveryStatus == Delivery.STATUS_PLACED ||
                mDelivery.deliveryStatus == Delivery.STATUS_IN_TRANSIT) {
            menuInflater.inflate(R.menu.track_delivery_menu, menu)
            return true
        }else return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
            R.id.menu_cancel_delivery -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.cancel_delivery))
                    .setMessage(getString(R.string.confirm_cancel_delivery))
                    .setNegativeButton(getString(R.string.yes)){ dialog, _->
                        trackDeliveryViewModel.cancelDelivery(mDelivery)
                        dialog.dismiss()
                    }.setPositiveButton(getString(R.string.no)){ dialog, _->
                        dialog.dismiss()
                    }.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        stopObservers()
        super.onDestroy()
    }

    companion object{
        const val DELIVERY_EXTRA = "delivery"

        fun newInstance(context: Context,delivery: Delivery): Intent {
            val intent = Intent(context,TrackDeliveryActivity::class.java)
            intent.putExtra(DELIVERY_EXTRA,delivery)
            return intent
        }
    }

}
