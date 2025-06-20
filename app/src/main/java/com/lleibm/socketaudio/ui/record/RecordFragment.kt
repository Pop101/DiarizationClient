package com.lleibm.socketaudio.ui.record

import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import com.lleibm.socketaudio.R
import com.lleibm.socketaudio.databinding.FragmentRecordBinding
import android.widget.ImageView

class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!
    private var isRecording = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecordButton()
        setupTranscriptField()
    }

    private fun setupRecordButton() {
        binding.recordButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }
    }

    private fun setupTranscriptField() {
        var expanded = false
        binding.transcriptLabel.text = getString(R.string.transcript_placeholder)
        val chevron = binding.transcriptChevron
        binding.transcriptLabelContainer.setOnClickListener {
            expanded = !expanded
            animateTranscriptExpansion(expanded)
            animateChevron(chevron, expanded)
        }
        binding.transcriptText.setOnClickListener {
            if (binding.transcriptText.text.isEmpty()) {
                expanded = !expanded
                animateTranscriptExpansion(expanded)
                animateChevron(chevron, expanded)
            }
        }
    }

    private fun animateTranscriptExpansion(expand: Boolean) {
        val scrollView = binding.transcriptScroll
        val duration = 300L
        if (expand) {
            scrollView.measure(
                View.MeasureSpec.makeMeasureSpec(scrollView.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.UNSPECIFIED
            )
            val targetHeight = scrollView.getChildAt(0).measuredHeight
            scrollView.visibility = View.VISIBLE
            val animator = ValueAnimator.ofInt(0, targetHeight)
            animator.addUpdateListener {
                val value = it.animatedValue as Int
                val params = scrollView.layoutParams
                params.height = value
                scrollView.layoutParams = params
            }
            animator.duration = duration
            animator.start()
            binding.transcriptLabel.text = getString(R.string.transcript_title)
        } else {
            val initialHeight = scrollView.height
            val animator = ValueAnimator.ofInt(initialHeight, 0)
            animator.addUpdateListener {
                val value = it.animatedValue as Int
                val params = scrollView.layoutParams
                params.height = value
                scrollView.layoutParams = params
            }
            animator.duration = duration
            animator.start()
            animator.doOnEnd {
                scrollView.visibility = View.GONE
                if (binding.transcriptText.text.isEmpty()) {
                    binding.transcriptLabel.text = getString(R.string.transcript_placeholder)
                }
            }
        }
    }

    private fun animateChevron(chevron: ImageView, expanded: Boolean) {
        val from = if (expanded) 0f else 180f
        val to = if (expanded) 180f else 0f
        chevron.animate().rotation(to).setDuration(300).start()
    }

    private fun startRecording() {
        isRecording = true
        binding.recordButton.isSelected = true // Triggers red background
        binding.recordButton.text = getString(R.string.title_record)
        binding.statusText.text = getString(R.string.status_recording)
        animateTranscriptExpansion(true)
        animateChevron(binding.transcriptChevron, true)
        binding.transcriptText.text = getString(R.string.dummy_transcript)
        binding.transcriptLabel.text = getString(R.string.transcript_title)
        // TODO: Start actual audio streaming
    }

    private fun stopRecording() {
        isRecording = false
        binding.recordButton.isSelected = false // Triggers green background
        binding.recordButton.text = getString(R.string.title_record)
        binding.statusText.text = getString(R.string.status_ready)
        // Optionally clear transcript or keep it
        // binding.transcriptText.text = ""
        // animateTranscriptExpansion(false)
        // animateChevron(binding.transcriptChevron, false)
        // TODO: Stop audio streaming
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}