package com.example.protiuc.sunshinekotlin.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.protiuc.sunshinekotlin.R
import com.example.protiuc.sunshinekotlin.data.network.Status
import com.example.protiuc.sunshinekotlin.di.Injectable

import kotlinx.android.synthetic.main.activity_forecast.*
import java.util.*
import javax.inject.Inject

class MainFragment : Fragment(), Injectable, ForecastAdapter.ForecastAdapterOnItemClickHandler {

    override fun onItemClick(date: Date) {

    }

    companion object {
        fun newInstance() = MainFragment()
    }

    @Inject
    lateinit var factory: MainViewModelFactory
    private lateinit var viewModel: MainViewModel
    private var position = RecyclerView.NO_POSITION
    private lateinit var forecastAdapter: ForecastAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.activity_forecast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        recyclerview_forecast.layoutManager = layoutManager
        recyclerview_forecast.setHasFixedSize(true)

        forecastAdapter = ForecastAdapter(activity!!.applicationContext, this)

        recyclerview_forecast.adapter = forecastAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)

        viewModel.forecast.observe(this, Observer { forecast ->
            if (forecast!!.status == Status.LOADING) {
                showLoading()
            } else {
                forecastAdapter.swapForecast(forecast!!.data!!)

                if (position == RecyclerView.NO_POSITION) position = 0
                recyclerview_forecast.smoothScrollToPosition(position)
                showWeatherDataView()
            }
        })
    }

    /**
     * This method will make the View for the weather data visible and hide the error message and
     * loading indicator.
     *
     *
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private fun showWeatherDataView() {
        // First, hide the loading indicator
        pb_loading_indicator.visibility = View.INVISIBLE
        // Finally, make sure the weather data is visible
        recyclerview_forecast.visibility = View.VISIBLE
    }

    /**
     * This method will make the loading indicator visible and hide the weather View and error
     * message.
     *
     *
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private fun showLoading() {
        // Then, hide the weather data
        recyclerview_forecast.visibility = View.INVISIBLE
        // Finally, show the loading indicator
        pb_loading_indicator.visibility = View.VISIBLE
    }
}
