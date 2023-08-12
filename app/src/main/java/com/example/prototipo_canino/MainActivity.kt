package com.example.prototipo_canino

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.prototipo_canino.databinding.ActivityMainBinding

/// solo es fue para probar pero debes volver a poner todo con las actulizacioones de android 13
class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mActiveFragment: Fragment
    private lateinit var mFragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setupBottomNav()
    }


    private fun setupBottomNav(/*fragmentManager: FragmentManager*/){
        //limpiar antes para preevenir errores
/*        mFragmentManager?.let {
            for (fragment in it.fragments){
                it.beginTransaction().remove(fragment!!).commit()
            }
        }*/

        mFragmentManager = supportFragmentManager

        val homeFragment = HomeFragment()
        val recetasFragment = RecetasFragment()
        val ejerciciosFragment = EjerciciosFragment()
        val enfermedadesFragment = EnfermedadesFragment()
        val profileFragment = ProfileFragment()

        mActiveFragment = homeFragment

        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, profileFragment, ProfileFragment::class.java.name)
            .hide(profileFragment).commit()
        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, enfermedadesFragment, enfermedadesFragment::class.java.name)
            .hide(enfermedadesFragment).commit()
        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, ejerciciosFragment, ejerciciosFragment::class.java.name)
            .hide(ejerciciosFragment).commit()
        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, recetasFragment, recetasFragment::class.java.name)
            .hide(recetasFragment).commit()
        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, homeFragment, HomeFragment::class.java.name)
            .commit()

        mBinding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.action_home -> {
                    mFragmentManager.beginTransaction().hide(mActiveFragment).show(homeFragment).commit()
                    mActiveFragment = homeFragment
                    true
                }
                R.id.action_Recetas -> {
                    mFragmentManager.beginTransaction().hide(mActiveFragment).show(recetasFragment).commit()
                    mActiveFragment = recetasFragment
                    true
                }
                R.id.action_Ejercicios -> {
                    mFragmentManager.beginTransaction().hide(mActiveFragment).show(ejerciciosFragment).commit()
                    mActiveFragment = ejerciciosFragment
                    true
                }
                R.id.action_Enfermedades -> {
                    mFragmentManager.beginTransaction().hide(mActiveFragment).show(enfermedadesFragment).commit()
                    mActiveFragment = enfermedadesFragment
                    true
                }
                R.id.action_profile -> {
                    mFragmentManager.beginTransaction().hide(mActiveFragment).show(profileFragment).commit()
                    mActiveFragment = profileFragment
                    true
                }
                else -> false
            }
        }

    }
}