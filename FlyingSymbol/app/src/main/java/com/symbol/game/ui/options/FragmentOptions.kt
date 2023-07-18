package com.symbol.game.ui.options

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.slider.Slider
import com.symbol.game.R
import com.symbol.game.core.library.l
import com.symbol.game.core.library.soundClickListener
import com.symbol.game.databinding.FragmentOptionsBinding
import com.symbol.game.domain.AppPrefs
import com.symbol.game.ui.other.MainActivity
import com.symbol.game.ui.other.ViewBindingFragment

class FragmentOptions :
    ViewBindingFragment<FragmentOptionsBinding>(FragmentOptionsBinding::inflate) {
    private val sharedPrefs by lazy {
        AppPrefs(requireActivity())
    }
    private val args: FragmentOptionsArgs by navArgs()
    private lateinit var viewModel: OptionsViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.musicSlider.setCustomThumbDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.img_music_thumb,
                null
            )!!
        )
        viewModel = ViewModelProvider(
            this,
            OptionsViewModelFactory(args.skin)
        )[OptionsViewModel::class.java]

        if (viewModel.list.value!!.isEmpty()) {
            viewModel.initList(getAvailableSkins())
        }

        binding.musicSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {

            }

            override fun onStopTrackingTouch(slider: Slider) {
                sharedPrefs.setVolume(slider.value.toInt())
                (requireActivity() as MainActivity).startMusic()
            }
        })

        binding.buttonClose.soundClickListener {
            findNavController().popBackStack()
        }

        binding.buttonLeft.setOnClickListener {
            viewModel.left { value ->
                l(value.toString())
                sharedPrefs.setCurrentSymbol(value)
            }
        }

        binding.buttonRight.setOnClickListener {
            viewModel.right { value ->
                l(value.toString())
                sharedPrefs.setCurrentSymbol(value)
            }
        }

        viewModel.current.observe(viewLifecycleOwner) {
            l(it.toString() + "wee")
            binding.skin.setImageResource(sharedPrefs.getImageBySymbol(it))
        }
    }

    private fun getAvailableSkins(): MutableList<Int> {
        val listToReturn = mutableListOf<Int>()
        repeat(9) {
            if (sharedPrefs.isSymbolBought(it + 1)) {
                listToReturn.add(it + 1)
            }
        }
        return listToReturn
    }
}