package com.example.prototipo_canino

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.prototipo_canino.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class ChangePassword : AppCompatActivity() {
    private lateinit var mBinding: ActivityChangePasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        auth = Firebase.auth


        val passwordRegex = Pattern.compile("^" +
                "(?=.*[-@#$%^&+=])" +     // Al menos 1 carácter especial
                ".{6,}" +                // Al menos 4 caracteres
                "$")

        mBinding.btGuardarPassword.setOnClickListener {
            val currentPassword = mBinding.actualPasswordEt.text.toString()
            val newPassword = mBinding.newPassword.text.toString()
            val repeatNewPassword = mBinding.confirmNewPassword.text.toString()

            if (newPassword.isEmpty() || !passwordRegex.matcher(newPassword).matches()){
                Toast.makeText(this,"Debil",Toast.LENGTH_SHORT).show()
            }
            else if (newPassword != repeatNewPassword){
                Toast.makeText(this,"Las contraseña no coincide",Toast.LENGTH_SHORT).show()
            }
            else{
                changePassword(currentPassword,newPassword)
            }
        }
    }

    private fun changePassword(current: String,password: String){
        val user = auth.currentUser

        if(user !=null){
            val correo = user.email
            val credential = EmailAuthProvider.getCredential(correo!!,current)

            user.reauthenticate(credential)
                .addOnCompleteListener{
                    task ->
                    if (task.isSuccessful){

                        user.updatePassword(password)
                            .addOnCompleteListener { taskUpdatePassword ->
                                if (taskUpdatePassword.isSuccessful) {
                                    Toast.makeText(this, "Se cambio la contraseña.",
                                        Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    this.startActivity(intent)
                                }
                            }
                    }
                    else{
                        Toast.makeText(this, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show()
                    }

                }
        }
    }




}