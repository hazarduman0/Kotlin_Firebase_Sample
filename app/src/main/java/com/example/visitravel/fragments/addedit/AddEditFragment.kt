package com.example.visitravel.fragments.addedit

import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.visitravel.activity.main.MainViewModel
import com.example.visitravel.config.AppConfig
import com.example.visitravel.databinding.FragmentAddEditBinding
import com.example.visitravel.models.Location
import com.example.visitravel.service.FirestoreService
import com.example.visitravel.utils.MainFragmentType
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class AddEditFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel

    private var _binding: FragmentAddEditBinding? = null

    private val binding get() = _binding!!

    val firestoreService: FirestoreService = FirestoreService()

    companion object {
        fun newInstance() = AddEditFragment()
    }

    private lateinit var viewModel: AddEditViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddEditBinding.inflate(inflater, container, false)
        val root: View = binding.root
        viewModel = ViewModelProvider(this)[AddEditViewModel::class.java]
        binding.addEditObject = this

        if(AppConfig.selectedLocation != null){
            binding.addEditButton.text = "Update"
            binding.addEditDeleteButton.visibility = View.VISIBLE
            binding.locationEditText.setText(AppConfig.selectedLocation!!.locationName)
            binding.descriptionEditText.setText(AppConfig.selectedLocation!!.locationDescription)
            binding.addEditDeleteButton.setOnClickListener {
                lifecycleScope.launch {
                    deleteButton()
                }
            }
        }

        binding.addEditButton.setOnClickListener {
            lifecycleScope.launch {
                addEditButtonClick()
            }
        }

        //return inflater.inflate(R.layout.fragment_add_edit, container, false)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    fun backButtonClick(){
        mainViewModel.rootToPage(MainFragmentType.MAIN)
    }

    private suspend fun deleteButton(){
        val status = firestoreService.deleteLocation(AppConfig.selectedLocation!!.locationId!!)
        if (status){
            Toast.makeText(requireActivity(), "Deleted", Toast.LENGTH_SHORT).show()
            mainViewModel.getAllPlace()
            mainViewModel.rootToPage(MainFragmentType.MAIN)
            AppConfig.selectedLocation = null
        }else{
            Toast.makeText(requireActivity(), "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addEditButtonClick(){
        if (binding.locationEditText.text.isEmpty()){
            Toast.makeText(requireActivity(), "Location text cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (AppConfig.selectedLocation == null){
            val locationStr = binding.locationEditText.text.toString()
            val descriptionStr = binding.descriptionEditText.text.toString()
            val date = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())

            val location = Location(null, locationStr, descriptionStr, date)

            val status = firestoreService.addPlace(location)

            if (status){
                Toast.makeText(requireActivity(), "Added", Toast.LENGTH_SHORT).show()
                mainViewModel.getAllPlace()
                mainViewModel.rootToPage(MainFragmentType.MAIN)
            } else{
                Toast.makeText(requireActivity(), "Failed", Toast.LENGTH_SHORT).show()
            }
        }else{

            val locationStr = binding.locationEditText.text.toString()
            val descriptionStr = binding.descriptionEditText.text.toString()

            val location = Location(AppConfig.selectedLocation!!.locationId, locationStr, descriptionStr, AppConfig.selectedLocation!!.date)

            val status = firestoreService.updatePlace(location)

            if (status){
                Toast.makeText(requireActivity(), "Updated", Toast.LENGTH_SHORT).show()
                mainViewModel.getAllPlace()
                mainViewModel.rootToPage(MainFragmentType.MAIN)
                AppConfig.selectedLocation = null
            } else{
                Toast.makeText(requireActivity(), "Failed", Toast.LENGTH_SHORT).show()
            }


        }
    }

}