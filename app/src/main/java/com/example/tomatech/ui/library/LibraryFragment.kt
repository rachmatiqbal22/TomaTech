package com.example.tomatech.ui.library

import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.example.tomatech.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val libraryViewModel =
            ViewModelProvider(this).get(LibraryViewModel::class.java)

        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Observe LiveData from ViewModel
        libraryViewModel.penyakitList.observe(viewLifecycleOwner, Observer { penyakitList ->
            for (i in penyakitList.indices) {
                val imageView: ImageView = root.findViewById(
                    resources.getIdentifier("imageViewPenyakit${i + 1}", "id", requireContext().packageName)
                )
                val textView: TextView = root.findViewById(
                    resources.getIdentifier("textPenyakit${i + 1}", "id", requireContext().packageName)
                )
                val descView: TextView = root.findViewById(
                    resources.getIdentifier("descPenyakit${i + 1}", "id", requireContext().packageName)
                )

                val penyakit = penyakitList[i]

                // Set image and text data
                imageView.setImageResource(penyakit.gambarResId)
                textView.text = penyakit.nama
                descView.text = penyakit.deskripsi

                // Apply justification for API 26 and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    descView.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                }
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
