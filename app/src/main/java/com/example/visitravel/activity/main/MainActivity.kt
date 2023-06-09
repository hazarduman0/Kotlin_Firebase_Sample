package com.example.visitravel.activity.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.visitravel.R
import com.example.visitravel.activity.login.LoginActivity
import com.example.visitravel.adapters.PlaceListAdapter
import com.example.visitravel.config.AppConfig
import com.example.visitravel.databinding.ActivityMainBinding
import com.example.visitravel.fragments.addedit.AddEditFragment
import com.example.visitravel.models.Location
import com.example.visitravel.service.AuthService
import com.example.visitravel.utils.CustomDividerItemDecoration
import com.example.visitravel.utils.MainFragmentType
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel : MainViewModel by viewModels()

    val authService = AuthService()

    private var flagDecoration = false

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainObject = this

        lifecycleScope.launch {
            viewModel.getAllPlace()
        }

        binding.searchBar.doAfterTextChanged {
            lifecycleScope.launch {
                viewModel.search(it.toString())
            }
        }

        binding.fab.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN)

        viewModel.mainFragmentType.observe(this){mainFragmentType ->
            with(binding){
                when(mainFragmentType){
                    MainFragmentType.MAIN -> {
                        closeFragment()
                        mainFrameLayout.visibility = View.GONE
                        binding.fab.visibility = View.VISIBLE
                    }
                    else -> {
                        supportFragmentManager.beginTransaction().replace(R.id.mainFrameLayout,AddEditFragment()).commit()
                        mainFrameLayout.visibility = View.VISIBLE
                        binding.fab.visibility = View.GONE
                    }
                }
            }
        }

        viewModel.locationList.observe(this){locationList ->
            with(binding){
                if(locationList.isEmpty()){
                    binding.mainScroll.visibility = View.GONE
                }else{
                    binding.mainScroll.visibility = View.VISIBLE

                    val adapter = PlaceListAdapter(locationList)
                    binding.placeListView.layoutManager = LinearLayoutManager(this@MainActivity)
                    binding.placeListView.adapter = adapter
                    adapter.notifyDataSetChanged()
                    adapter.setOnItemClickListener(object: PlaceListAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            onPlaceItemSelect(locationList[position])
                        }

                    })

                    if (!flagDecoration) {
                        binding.placeListView.addItemDecoration(
                            CustomDividerItemDecoration(
                                this@MainActivity,
                                LinearLayoutManager.VERTICAL
                            )
                        )
                        flagDecoration = true
                    }
                }
            }
        }
    }

    private fun onPlaceItemSelect(place: Location){
        AppConfig.selectedLocation = place
        viewModel.rootToPage(MainFragmentType.EDIT)
    }

    private fun closeFragment(){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = fragmentManager.findFragmentById(R.id.mainFrameLayout)

        if (fragment != null){
            fragmentTransaction.remove(fragment).commit()
        }
    }

    suspend fun search(query: String){
        viewModel.search(query)
    }

    fun onFabButtonClick(){
        viewModel.rootToPage(MainFragmentType.ADD)
    }

    fun onLogoutButtonClick(){
        authService.logout()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}