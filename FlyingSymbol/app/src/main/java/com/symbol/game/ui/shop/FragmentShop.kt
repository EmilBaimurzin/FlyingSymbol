package com.symbol.game.ui.shop

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.symbol.game.R
import com.symbol.game.core.library.shortToast
import com.symbol.game.core.library.soundClickListener
import com.symbol.game.databinding.FragmentShopBinding
import com.symbol.game.domain.AppPrefs
import com.symbol.game.ui.main.FragmentMainDirections
import com.symbol.game.ui.other.ViewBindingFragment

class FragmentShop : ViewBindingFragment<FragmentShopBinding>(FragmentShopBinding::inflate) {
    private val sharedPrefs by lazy {
        AppPrefs(requireActivity())
    }
    private val viewModel: ShopViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonParams()
        setStars()
        buttonClickListeners()
        viewModel.isPage1.observe(viewLifecycleOwner) {
            binding.apply {
                firstPageLayout.isVisible = it
                secondPageLayout.isVisible = !it
                backButton.isVisible = !it
                nextButton.isVisible = it
            }
        }

        binding.buttonClose.soundClickListener {
            findNavController().popBackStack()
        }

        binding.backButton.setOnClickListener {
            viewModel.changePage()
        }
        binding.nextButton.setOnClickListener {
            viewModel.changePage()
        }
    }

    private fun buttonClickListeners() {
        binding.apply {
            symbol2Button.setOnClickListener {
                buttonClick(2)
            }
            symbol3Button.setOnClickListener {
                buttonClick(3)
            }
            symbol4Button.setOnClickListener {
                buttonClick(4)
            }
            symbol5Button.setOnClickListener {
                buttonClick(5)
            }
            symbol6Button.setOnClickListener {
                buttonClick(6)
            }
            symbol7Button.setOnClickListener {
                buttonClick(7)
            }
            symbol8Button.setOnClickListener {
                buttonClick(8)
            }
            symbol9Button.setOnClickListener {
                buttonClick(9)
            }
        }
    }

    private fun buttonClick(value: Int) {
        if (sharedPrefs.isSymbolBought(value)) {
            findNavController().popBackStack()
            findNavController().navigate(FragmentMainDirections.actionFragmentMainToFragmentOptions(value))
        } else {
            if (sharedPrefs.getStarsAmount() >= 1000) {
                sharedPrefs.buySymbol(value)
                setStars()
                setButtonParams()
            } else {
                shortToast(requireContext(), "Not enough stars")
            }
        }
    }

    private fun setStars() {
        binding.starsTextView.text = sharedPrefs.getStarsAmount().toString()
    }

    private fun setButtonParams() {
        binding.apply {
            symbol2Button.setImageResource(if (sharedPrefs.isSymbolBought(2)) R.drawable.button_select else R.drawable.button_price)
            symbol3Button.setImageResource(if (sharedPrefs.isSymbolBought(3)) R.drawable.button_select else R.drawable.button_price)
            symbol4Button.setImageResource(if (sharedPrefs.isSymbolBought(4)) R.drawable.button_select else R.drawable.button_price)
            symbol5Button.setImageResource(if (sharedPrefs.isSymbolBought(5)) R.drawable.button_select else R.drawable.button_price)
            symbol6Button.setImageResource(if (sharedPrefs.isSymbolBought(6)) R.drawable.button_select else R.drawable.button_price)
            symbol7Button.setImageResource(if (sharedPrefs.isSymbolBought(7)) R.drawable.button_select else R.drawable.button_price)
            symbol8Button.setImageResource(if (sharedPrefs.isSymbolBought(8)) R.drawable.button_select else R.drawable.button_price)
            symbol9Button.setImageResource(if (sharedPrefs.isSymbolBought(9)) R.drawable.button_select else R.drawable.button_price)
        }
    }
}