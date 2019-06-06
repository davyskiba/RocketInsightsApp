package com.example.shalomhalbert.rocketinsightsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_day_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_day_list)

        (recyclerView as RecyclerView).apply {

            layoutManager = LinearLayoutManager(context)
            adapter = DayAdapter()
        }

        viewModel.dates.observe(this, Observer {
            val adapter = recyclerView.adapter as DayAdapter
            adapter.addDates(it)
        })

    }

}
