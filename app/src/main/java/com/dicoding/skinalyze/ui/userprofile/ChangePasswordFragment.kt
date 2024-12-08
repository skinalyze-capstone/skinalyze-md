package com.dicoding.skinalyze.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dicoding.skinalyze.R
import com.dicoding.skinalyze.databinding.FragmentChangePasswordBinding

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle reset password button click
        binding.btnResetPassword.setOnClickListener {
            val newPassword = binding.newPasswordInput.text.toString().trim()

            if (newPassword.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.enter_valid_pass), Toast.LENGTH_SHORT).show()
            } else {
                updatePassword(newPassword)
            }
        }
    }

    private fun updatePassword(newPassword: String) {
        // Simulate updating password
        val isPasswordUpdated = true // Simulated result

        if (isPasswordUpdated) {
            Toast.makeText(requireContext(), getString(R.string.password_updated), Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed() // Navigate back after success
        } else {
            Toast.makeText(requireContext(), getString(R.string.error_updating_pass), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
