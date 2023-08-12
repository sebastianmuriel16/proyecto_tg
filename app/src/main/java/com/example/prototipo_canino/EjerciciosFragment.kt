package com.example.prototipo_canino

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import com.example.prototipo_canino.databinding.FragmentEjerciciosBinding
import com.example.prototipo_canino.databinding.FragmentRecetasBinding


class EjerciciosFragment : Fragment() {
    private lateinit var mBinding: FragmentEjerciciosBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentEjerciciosBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webSettings: WebSettings = mBinding.webEjercicios.settings
        webSettings.javaScriptEnabled = true
        webSettings.mediaPlaybackRequiresUserGesture = false
        webSettings.allowFileAccess = true

        webSettings.domStorageEnabled = true
        webSettings.allowContentAccess = true
        webSettings.allowUniversalAccessFromFileURLs = true
        webSettings.allowFileAccessFromFileURLs = true

        mBinding.webEjercicios.webViewClient = WebViewClient()
        mBinding.webEjercicios.loadUrl("https://proyectotg-8ef06.web.app/videos/index.html")

    }
}