package com.dicoding.skinalyze.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dicoding.skinalyze.R

class EditProfileFragment : Fragment() {

    private lateinit var editPictureButton: ImageView
    private lateinit var updateButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        // Initialize views
        editPictureButton = view.findViewById(R.id.img_edit)
        updateButton = view.findViewById(R.id.btn_update_profile)

        editPictureButton.setOnClickListener {
            Toast.makeText(context, "You clicked the edit picture button!", Toast.LENGTH_SHORT).show()
        }

        updateButton.setOnClickListener {
            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            // Navigate back to User Fragment
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }
}
