package com.example.androidproject2s23

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

interface EmailListener {
    fun sendEmail(emailAddress: String)
}


class EmailFragment : Fragment() {

    private lateinit var emailAddressEditText: EditText
    private lateinit var saveButton: Button
    private var emailListener: EmailListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EmailListener) {
            emailListener = context
        } else {
            throw RuntimeException("$context must implement EmailListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_email, container, false)
        emailAddressEditText = rootView.findViewById(R.id.emailAddressEditText)
        saveButton = rootView.findViewById(R.id.saveButton)

        // Retrieve the last used email address from SharedPreferences
        val sharedPrefs = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val lastEmail = sharedPrefs.getString("last_email", "")
        emailAddressEditText.setText(lastEmail)

        saveButton.setOnClickListener {
            val email = emailAddressEditText.text.toString()
            emailListener?.sendEmail(email)


            val editor = sharedPrefs.edit()
            editor.putString("last_email", email)
            editor.apply()


        }

        return rootView
    }

}

