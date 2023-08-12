package com.example.prototipo_canino

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import com.example.prototipo_canino.databinding.FragmentRecetasBinding

class RecetasFragment : Fragment() {

    private lateinit var mBinding: FragmentRecetasBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentRecetasBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webSettings: WebSettings = mBinding.webRecetas.settings
        webSettings.javaScriptEnabled = true
        mBinding.webRecetas.webViewClient = WebViewClient()
        mBinding.webRecetas.loadUrl("https://proyectotg-8ef06.web.app/")

    }


}