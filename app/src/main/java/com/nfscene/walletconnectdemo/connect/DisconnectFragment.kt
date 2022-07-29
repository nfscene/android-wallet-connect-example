package com.nfscene.walletconnectdemo.connect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nfscene.walletconnectdemo.R
import com.nfscene.walletconnectdemo.databinding.FragmentDisconnectBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DisconnectFragment : Fragment() {

    private var _binding: FragmentDisconnectBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val model: ConnectActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDisconnectBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            model.signOut()
            findNavController().navigate(R.id.action_DisconnectFragment_to_ConnectFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}