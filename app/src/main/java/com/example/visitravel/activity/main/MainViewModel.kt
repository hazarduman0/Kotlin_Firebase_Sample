package com.example.visitravel.activity.main


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.visitravel.models.Location
import com.example.visitravel.service.FirestoreService
import com.example.visitravel.utils.MainFragmentType

class MainViewModel : ViewModel()  {
    var mainFragmentType = MutableLiveData<MainFragmentType>(MainFragmentType.MAIN)
    var locationList = MutableLiveData<MutableList<Location>>()
    val firestoreService = FirestoreService()

    fun rootToPage(fragmentType: MainFragmentType){
        mainFragmentType.value = fragmentType
    }

    suspend fun getAllPlace(){
        locationList.value = firestoreService.getPlacesByUserID()
    }

    suspend fun search(query: String) {
        if (query.isEmpty()) {
            getAllPlace()
        } else {
            locationList.value = firestoreService.searchPlacesByQuery(query)
        }
    }
}