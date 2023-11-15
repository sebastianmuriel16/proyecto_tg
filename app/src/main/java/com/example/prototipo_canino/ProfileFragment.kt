package com.example.prototipo_canino

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.prototipo_canino.databinding.FragmentProfileBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class ProfileFragment : Fragment(),FragmentAux {

    private lateinit var mBinding: FragmentProfileBinding
    private var firebaseStorage: FirebaseStorage? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private lateinit var databaseRef: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentProfileBinding.inflate(inflater,container,false)
        databaseRef = FirebaseDatabase.getInstance().reference
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refresh()
        setupButton()
        cambiarClave()
        cambiarFoto()
        firebaseStorage = FirebaseStorage.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        cargarFotoPerfil()
    }

    private fun cargarFotoPerfil(){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profileImageUrl = snapshot.child("fotos_perfil/$userId/image").getValue(String::class.java)
                    if (profileImageUrl != null) {
                        context?.let {
                            Glide.with(it)
                                .load(profileImageUrl)
                                .into(mBinding.fotoperfil)
                        }
                    } else {
                        mBinding.fotoperfil.setImageResource(R.drawable.icono_foto_perfil)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error de base de datos", Toast.LENGTH_SHORT).show()
                }
            }
            databaseRef.addValueEventListener(valueEventListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        databaseRef.removeEventListener(valueEventListener)
    }

    private fun cambiarClave(){
        mBinding.changePasswordTv.setOnClickListener{
            val intent = Intent(activity,ChangePassword::class.java)
            activity?.startActivity(intent)
        }

    }


    private fun cambiarFoto(){
        mBinding.fotoperfil.setOnClickListener {
            val intent = Intent(activity,FotoPerfil::class.java)
            activity?.startActivity(intent)
        }
    }

    private fun setupButton(){
        mBinding.btnLogout.setOnClickListener {
            context?.let {
                MaterialAlertDialogBuilder(it)
                    .setTitle(R.string.dialog_logout_title)
                    .setPositiveButton(R.string.dialog_logout_confirm){ _, _ ->
                        singOut()
                    }
                    .setNegativeButton(R.string.dialog_logout_cancel,null)
                    .show()
            }
        }
    }



    private fun singOut() {
        context?.let {
            AuthUI.getInstance().signOut(it)
                .addOnCompleteListener {
                    Toast.makeText(context,"hasta pronto...", Toast.LENGTH_SHORT).show()
                    mBinding.tvName.text = ""
                    mBinding.tvEmail.text = ""

                    (activity?.findViewById(R.id.bottomNav) as? BottomNavigationView)?.selectedItemId =
                        R.id.action_home
                }
        }
    }

    override fun refresh() {
        with(mBinding) {
            tvName.text = SnapshotsApplication.currentUser.displayName
            tvEmail.text = SnapshotsApplication.currentUser.email
        }
    }

}