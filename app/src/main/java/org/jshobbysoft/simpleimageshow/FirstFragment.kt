/*
 * Copyright (c) 2023.  JS Hobbysoft  All rights reserved.
 */

package org.jshobbysoft.simpleimageshow

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import org.jshobbysoft.simpleimageshow.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireActivity() /* Activity context */)

//      Set up spinner for delay time
        var item = sharedPreferences.getLong("userDelayTime", 1)

        val spinner: Spinner = binding.userDelaySpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.delaySeconds,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
            // Set default value for spinner selection, either from SharedPreferences or the default of 1
            spinner.setSelection(adapter.getPosition(item.toString()))
        }
//      https://stackoverflow.com/questions/58173556/kotlin-spinners-in-fragments-using
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                item = parent?.getItemAtPosition(position).toString().toLong()
            }
        }

//      Set up spinner for background color
        var item2 = sharedPreferences.getString("userBgColor", "white")

        val spinner2: Spinner = binding.bgColorSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.bgColors,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner2.adapter = adapter
            // Set default value for spinner selection, either from SharedPreferences or the default of "white"
            spinner2.setSelection(adapter.getPosition(item2))
        }
//        https://stackoverflow.com/questions/58173556/kotlin-spinners-in-fragments-using
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                item2 = parent?.getItemAtPosition(position).toString()
            }
        }

//      Launch image viewing
        binding.buttonFirst.setOnClickListener {
            sharedPreferences.edit().putLong("userDelayTime", item).apply()
            sharedPreferences.edit().putString("userBgColor", item2).apply()
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

//      Get a persistable directory URI with the images
//        https://stackoverflow.com/questions/70933313/android-folder-picker-intent-launches-file-picker
        val dirRequest =
            registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
                uri?.let {
                    // call this to persist permission across device reboots
                    requireActivity().contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    // do your stuff
//                    println("uri = " + uri)
                    sharedPreferences.edit()
                        .putString("userDirKey", uri.toString()).apply()
                }
            }

//      Choose directory
        binding.buttonSecond.setOnClickListener {
            dirRequest.launch(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}