package com.example.cse438.blackjack.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.cse438.blackjack.MainActivity
import com.example.cse438.blackjack.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_create_account.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CreateAccountFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CreateAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

@SuppressLint("ValidFragment")
class CreateAccountFragment(context: Context) : Fragment() {

    private var parentContext = context
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_create_account, container, false)
    }

    override fun onStart() {
        super.onStart()
        create_account.setOnClickListener {
            val firstName = first_name.text.toString()
            val lastName = last_name.text.toString()
            val email = email.text.toString()
            val username = username.text.toString()
            val password = password.text.toString()
            //val profilePic = App.convertDrawableToUri(parentContext.applicationContext, profile_pic.drawable, 150, 150)

            if (firstName != "" && lastName != "" && email != "" && username != "" && password != "") {
                firebaseAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener {it2 ->
                    if (it2.isSuccessful) {
                        val db = FirebaseFirestore.getInstance()
                        val userData = HashMap<String, Any>()
                        userData["first_name"] = firstName
                        userData["last_name"] = lastName
                        userData["email"] = email
                        userData["username"] = username

                        db.document("users/${firebaseAuth?.currentUser?.uid}")
                            .set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(parentContext, "User Created", Toast.LENGTH_LONG).show()
                                var intent = Intent(activity, MainActivity::class.java)
                                startActivity(intent)
                                activity?.finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(parentContext, "Failed to write user data", Toast.LENGTH_SHORT).show()
                            }
                    }
                    else {
                        Toast.makeText(parentContext, "Email and/or password unacceptable", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else {
                Toast.makeText(parentContext, "Must fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
