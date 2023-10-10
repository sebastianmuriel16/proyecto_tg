package com.example.prototipo_canino

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import com.example.prototipo_canino.databinding.FragmentEnfermedadesBinding
import com.example.prototipo_canino.databinding.FragmentRecetasBinding

class EnfermedadesFragment : Fragment() {

    private lateinit var mBinding: FragmentEnfermedadesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentEnfermedadesBinding.inflate(inflater, container, false)
        mBinding = FragmentEnfermedadesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webSettings: WebSettings = mBinding.webEnfermedades.settings
        webSettings.javaScriptEnabled = true
        mBinding.webEnfermedades.webViewClient = WebViewClient()
        mBinding.webEnfermedades.loadUrl("https://proyectotg-8ef06.web.app/Enfermedades/index.html")

    }

}