package com.symbol.game.ui.game

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.symbol.game.R
import com.symbol.game.core.library.soundClickListener
import com.symbol.game.databinding.FragmentGameBinding
import com.symbol.game.domain.AppPrefs
import com.symbol.game.ui.other.ViewBindingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class FragmentGame : ViewBindingFragment<FragmentGameBinding>(FragmentGameBinding::inflate) {
    private val sharedPrefs by lazy {
        AppPrefs(requireActivity())
    }
    private val viewModel: GameViewModel by viewModels()
    private val callbackViewModel: CallbackViewModel by activityViewModels()
    private val xy by lazy {
        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        Pair(size.x, size.y)
    }
    private var moveScope = CoroutineScope(Dispatchers.Default)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgPlayer.setImageResource(sharedPrefs.getImageBySymbol(sharedPrefs.getCurrentSymbol()))
        callbackViewModel.callback = {
            viewModel.gameState = true
            viewModel.startGame()
            setTouchListener()
            viewModel.generateRings(
                dpToPx(60),
                xy.first.toFloat(),
                binding.topLaser.y,
                binding.bottomLaser.y
            )
            viewModel.generateStars(
                dpToPx(30),
                xy.first.toFloat(),
                binding.topLaser.y,
                binding.bottomLaser.y
            )
            viewModel.generateLives(
                dpToPx(30),
                xy.first.toFloat(),
                binding.topLaser.y,
                binding.bottomLaser.y
            )
        }

        binding.buttonPause.soundClickListener {
            viewModel.stopGame()
            moveScope.cancel()
            viewModel.gameState = false
            findNavController().navigate(R.id.action_fragmentGame_to_dialogPause)
        }
        lifecycleScope.launch {
            delay(50)
            if (viewModel.playerXY.value!! == 0f to 0f) {
                viewModel.initPlayer(dpToPx(60).toFloat(), (xy.second / 2 - dpToPx(35)).toFloat())
            }
            setTouchListener()
            viewModel.playerSize = binding.imgPlayer.height
            viewModel.startGame()

            if (savedInstanceState == null) {
                viewModel.gameState = true
            }

            if (viewModel.gameState) {
                viewModel.generateRings(
                    dpToPx(60),
                    xy.first.toFloat(),
                    binding.topLaser.y,
                    binding.bottomLaser.y
                )
                viewModel.generateStars(
                    dpToPx(30),
                    xy.first.toFloat(),
                    binding.topLaser.y,
                    binding.bottomLaser.y
                )
                viewModel.generateLives(
                    dpToPx(30),
                    xy.first.toFloat(),
                    binding.topLaser.y,
                    binding.bottomLaser.y
                )
            }
        }

        setStars()

        binding.buttonBack.soundClickListener {
            findNavController().popBackStack()
        }

        viewModel.starsCallback = {
            addStar()
            setStars()
        }

        viewModel.playerXY.observe(viewLifecycleOwner) {
            binding.imgPlayer.x = it.first
            binding.imgPlayer.y = it.second

            if (it.second <= binding.topLaser.y + binding.topLaser.height || it.second + dpToPx(60) >= binding.bottomLaser.y ) {
                if (viewModel.gameState) {
                    viewModel.stopGame()
                    end()
                }
            }
        }

        viewModel.lives.observe(viewLifecycleOwner) {
            binding.apply {
                if (viewModel.gameState && it == 0) {
                    viewModel.stopGame()
                    end()
                }

                when (it) {
                    0 -> {
                        binding.heart1.setImageResource(R.drawable.img_heart_not_active)
                        binding.heart2.setImageResource(R.drawable.img_heart_not_active)
                        binding.heart3.setImageResource(R.drawable.img_heart_not_active)
                    }

                    1 -> {
                        binding.heart1.setImageResource(R.drawable.img_heart_active)
                        binding.heart2.setImageResource(R.drawable.img_heart_not_active)
                        binding.heart3.setImageResource(R.drawable.img_heart_not_active)
                    }

                    2 -> {
                        binding.heart1.setImageResource(R.drawable.img_heart_active)
                        binding.heart2.setImageResource(R.drawable.img_heart_active)
                        binding.heart3.setImageResource(R.drawable.img_heart_not_active)
                    }

                    3 -> {
                        binding.heart1.setImageResource(R.drawable.img_heart_active)
                        binding.heart2.setImageResource(R.drawable.img_heart_active)
                        binding.heart3.setImageResource(R.drawable.img_heart_active)
                    }
                }
            }
        }

        viewModel.ringList.observe(viewLifecycleOwner) { list ->
            binding.ringsLayout.removeAllViews()
            list.forEach {
                val ringView = ImageView(requireContext())
                ringView.layoutParams = ViewGroup.LayoutParams(dpToPx(120), dpToPx(60))
                ringView.setImageResource(R.drawable.img_ring)
                ringView.x = it.first
                ringView.y = it.second
                binding.ringsLayout.addView(ringView)
            }
        }

        viewModel.starsList.observe(viewLifecycleOwner) { list ->
            binding.starsAllLayout.removeAllViews()
            list.forEach {
                val starView = ImageView(requireContext())
                starView.layoutParams = ViewGroup.LayoutParams(dpToPx(30), dpToPx(30))
                starView.setImageResource(R.drawable.img_star)
                starView.x = it.first
                starView.y = it.second
                starView.scaleX = 1.5f
                starView.scaleY = 1.5f
                binding.starsAllLayout.addView(starView)
            }
        }

        viewModel.livesList.observe(viewLifecycleOwner) { list ->
            binding.livesAllLayout.removeAllViews()
            list.forEach {
                val liveView = ImageView(requireContext())
                liveView.layoutParams = ViewGroup.LayoutParams(dpToPx(30), dpToPx(30))
                liveView.setImageResource(R.drawable.img_heart_active)
                liveView.x = it.first
                liveView.y = it.second
                liveView.scaleX = 1.5f
                liveView.scaleY = 1.5f
                binding.livesAllLayout.addView(liveView)
            }
        }

        viewModel.scores.observe(viewLifecycleOwner) {
            binding.scoresTextView.text = it.toString()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setTouchListener() {
        binding.gameLayout.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                moveScope.cancel()
                moveScope = CoroutineScope(Dispatchers.Default)
                moveScope.launch {
                    while (true) {
                        viewModel.movePlayerUp()
                        delay(2)
                    }
                }
                true
            } else if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                moveScope.cancel()
                moveScope = CoroutineScope(Dispatchers.Default)
                moveScope.launch {
                    while (true) {
                        viewModel.movePlayerUp()
                        delay(2)
                    }
                }
                true
            } else {
                moveScope.cancel()
                moveScope = CoroutineScope(Dispatchers.Default)
                moveScope.launch {
                    while (true) {
                        viewModel.movePlayerDown(xy.second - dpToPx(70))
                        delay(2)
                    }
                }
                false
            }
        }
    }

    private fun end() {
        viewModel.gameState = false
        moveScope.cancel()
        findNavController().navigate(FragmentGameDirections.actionFragmentGameToDialogEnd(viewModel.scores.value!!))
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    private fun setStars() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.starsTextView.text = sharedPrefs.getStarsAmount().toString()
        }
    }

    private fun addStar() {
        sharedPrefs.addStar()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("i", 0)
    }

    override fun onPause() {
        super.onPause()
        moveScope.cancel()
        viewModel.stopGame()
        binding.gameLayout.setOnTouchListener { _, motionEvent ->
            false
        }
    }
}