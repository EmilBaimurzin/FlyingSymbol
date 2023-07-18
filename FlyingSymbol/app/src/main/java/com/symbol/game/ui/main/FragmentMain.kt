package com.symbol.game.ui.main

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.symbol.game.R
import com.symbol.game.core.library.soundClickListener
import com.symbol.game.databinding.FragmentMainBinding
import com.symbol.game.domain.AppPrefs
import com.symbol.game.ui.other.ViewBindingFragment

class FragmentMain : ViewBindingFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {
    private val sharedPrefs by lazy {
        AppPrefs(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefs.buySymbol(1)
        binding.apply {
            buttonPlay.soundClickListener {
                findNavController().navigate(R.id.action_fragmentMain_to_fragmentGame)
            }
            buttonOptions.soundClickListener {
                findNavController().navigate(
                    FragmentMainDirections.actionFragmentMainToFragmentOptions(
                        sharedPrefs.getCurrentSymbol()
                    )
                )
            }

            buttonShop.soundClickListener {
                findNavController().navigate(R.id.action_fragmentMain_to_fragmentShop)
            }
        }
    }
}