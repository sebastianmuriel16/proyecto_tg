package com.example.prototipo_canino

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.prototipo_canino.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


/// solo es fue para probar pero debes volver a poner todo con las actulizacioones de android 13
class MainActivity : AppCompatActivity(), MainAux {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mActiveFragment: Fragment
    private var mFragmentManager: FragmentManager? = null

    //uso de firebase
    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private var mFirebaseAuth: FirebaseAuth? = null

    private val authResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK) {
            Toast.makeText(this, R.string.main_auth_welcome, Toast.LENGTH_SHORT).show()
        } else {
            if (IdpResponse.fromResultIntent(it.data) == null) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setupAuth()
    }

    private fun setupAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { it ->
            if (it.currentUser == null) {
                authResult.launch(
                    AuthUI.getInstance().createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(
                            listOf(
                                AuthUI.IdpConfig.EmailBuilder().build(),
                                AuthUI.IdpConfig.GoogleBuilder().build()
                            )
                        )
                        .build()
                )
                mFragmentManager = null
            }else{
                SnapshotsApplication.currentUser = it.currentUser!!

                val fragmentProfile = mFragmentManager?.findFragmentByTag(ProfileFragment::class.java.name)
                fragmentProfile?.let {
                    (it as FragmentAux).refresh()
                }

                if (mFragmentManager == null){
                    mFragmentManager = supportFragmentManager
                    setupBottomNav(mFragmentManager!!)
                }
            }
        }
    }


    private fun setupBottomNav(fragmentManager: FragmentManager){
        //limpiar antes para preevenir errores
        mFragmentManager?.let {
            for (fragment in it.fragments){
                it.beginTransaction().remove(fragment!!).commit()
            }
        }


        val homeFragment = HomeFragment()
        val recetasFragment = RecetasFragment()
        val ejerciciosFragment = EjerciciosFragment()
        val enfermedadesFragment = EnfermedadesFragment()
        val profileFragment = ProfileFragment()
        val AddFragment = AddFragment()

        mActiveFragment = homeFragment

        fragmentManager.beginTransaction()
            .add(R.id.hostFragment, profileFragment, ProfileFragment::class.java.name)
            .hide(profileFragment).commit()
        fragmentManager.beginTransaction()
            .add(R.id.hostFragment, enfermedadesFragment, enfermedadesFragment::class.java.name)
            .hide(enfermedadesFragment).commit()
        fragmentManager.beginTransaction()
            .add(R.id.hostFragment, ejerciciosFragment, ejerciciosFragment::class.java.name)
            .hide(ejerciciosFragment).commit()
        fragmentManager.beginTransaction()
            .add(R.id.hostFragment, recetasFragment, recetasFragment::class.java.name)
            .hide(recetasFragment).commit()
        fragmentManager.beginTransaction()
            .add(R.id.hostFragment, AddFragment, AddFragment::class.java.name)
            .hide(AddFragment).commit()
        fragmentManager.beginTransaction()
            .add(R.id.hostFragment, homeFragment, HomeFragment::class.java.name)
            .commit()

        mBinding.addButton.setOnClickListener {
            fragmentManager.beginTransaction().hide(mActiveFragment).show(AddFragment).commit()
            mActiveFragment = AddFragment
            ocultarBtnAdd()
            ocultarBtnBack(true)
        }

        mBinding.backForo.setOnClickListener {
            fragmentManager.beginTransaction().hide(mActiveFragment).show(homeFragment).commit()
            mActiveFragment = homeFragment
            ocultarBtnAdd(true)
            ocultarBtnBack()
        }

        mBinding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.action_home -> {
                    ocultarBtnAdd(true)
                    ocultarBtnBack()
                    fragmentManager.beginTransaction().hide(mActiveFragment).show(homeFragment).commit()
                    mActiveFragment = homeFragment
                    true
                }
                R.id.action_Recetas -> {
                    ocultarBtnAdd()
                    ocultarBtnBack()
                    fragmentManager.beginTransaction().hide(mActiveFragment).show(recetasFragment).commit()
                    mActiveFragment = recetasFragment
                    true
                }
                R.id.action_Ejercicios -> {
                    ocultarBtnAdd()
                    ocultarBtnBack()
                    fragmentManager.beginTransaction().hide(mActiveFragment).show(ejerciciosFragment).commit()
                    mActiveFragment = ejerciciosFragment
                    true
                }
                R.id.action_Enfermedades -> {
                    ocultarBtnAdd()
                    ocultarBtnBack()
                    fragmentManager.beginTransaction().hide(mActiveFragment).show(enfermedadesFragment).commit()
                    mActiveFragment = enfermedadesFragment
                    true
                }
                R.id.action_profile -> {
                    ocultarBtnAdd()
                    ocultarBtnBack()
                    fragmentManager.beginTransaction().hide(mActiveFragment).show(profileFragment).commit()
                    mActiveFragment = profileFragment
                    true
                }
                else -> false
            }
        }

        mBinding.bottomNav.setOnItemReselectedListener {
            when(it.itemId){
                R.id.action_home -> (homeFragment as FragmentAux).refresh()
            }
        }

    }

    private fun ocultarBtnAdd(visible:Boolean = false){
        if(visible){
            mBinding.addButton.visibility = View.VISIBLE
        }
        else{
            mBinding.addButton.visibility = View.GONE
        }
    }

    private fun ocultarBtnBack(visible:Boolean = false){
        if(visible){
            mBinding.backForo.visibility = View.VISIBLE
        }
        else{
            mBinding.backForo.visibility = View.GONE
        }
    }


    override fun onResume() {
        super.onResume()
        mFirebaseAuth?.addAuthStateListener(mAuthListener)
    }

    override fun onPause() {
        super.onPause()
        mFirebaseAuth?.removeAuthStateListener(mAuthListener)
    }

    override fun showMessage(resId: Int, duration: Int) {
        Snackbar.make(mBinding.root, resId, duration)
            .setAnchorView(mBinding.bottomNav)
            .show()
    }
}