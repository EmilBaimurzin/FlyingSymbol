package com.symbol.game.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.symbol.game.R
import com.symbol.game.core.library.ViewBindingDialog
import com.symbol.game.core.library.soundClickListener
import com.symbol.game.databinding.DialogEndBinding
import com.symbol.game.domain.AppPrefs

class DialogEnd: ViewBindingDialog<DialogEndBinding>(DialogEndBinding::inflate) {
    private val args: DialogEndArgs by navArgs()
    private val sharedPrefs by lazy {
        AppPrefs(requireActivity())
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().popBackStack(R.id.fragmentMain, false, false)
                true
            } else {
                false
            }
        }

        binding.buttonClose.soundClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }

        binding.starsTextView.text = sharedPrefs.getStarsAmount().toString()
        binding.scoresTextView.text = args.scores.toString()
    }
}