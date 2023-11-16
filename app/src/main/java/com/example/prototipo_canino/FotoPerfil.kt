package com.example.prototipo_canino

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.prototipo_canino.databinding.ActivityFotoPerfilBinding
import com.example.prototipo_canino.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


class FotoPerfil : AppCompatActivity() {
    private lateinit var mBinding: ActivityFotoPerfilBinding
    private var uri: Uri? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var firebaseStorage: FirebaseStorage? = null
    private lateinit var storageReference: StorageReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFotoPerfilBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        storageReference = FirebaseStorage.getInstance().reference
        auth = FirebaseAuth.getInstance()

        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        mBinding.imageView.setOnClickListener {
            selecionarFoto() //uploadFoto
        }
        mBinding.button.setOnClickListener{
            subirFoto() //subirfoto
        }
    }

    private fun subirFoto(){
        val user = auth.currentUser
        val filname = "userFoto"
        if(user != null){
            val reference = firebaseStorage!!.reference.child("fotos/${user.uid}$filname")
            reference.putFile(uri!!).addOnSuccessListener {
                reference.downloadUrl.addOnSuccessListener { uri ->
                    val model = Model()
                    model.image = uri.toString()
                    firebaseDatabase!!.reference.child("fotos_perfil").child(user.uid)
                        .setValue(model).addOnSuccessListener {
                            Toast.makeText(this@FotoPerfil, "Subiendo Foto... Esperar por favor", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@FotoPerfil, "Error al Subir Imagen...", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
        else{
            Toast.makeText(this@FotoPerfil, "Usuario no logeado", Toast.LENGTH_SHORT).show()
        }

    }

    private fun selecionarFoto(){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(intent, 101)
                }

                override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                    Toast.makeText(this@FotoPerfil, "Permiso Denegado", Toast.LENGTH_SHORT).show()
                }
                override fun onPermissionRationaleShouldBeShown(permissionRequest: PermissionRequest, permissionToken: PermissionToken) {
                    permissionToken.continuePermissionRequest()
                }
            }).check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            uri = data!!.data
            mBinding.imageView.setImageURI(uri)
        }
    }
}