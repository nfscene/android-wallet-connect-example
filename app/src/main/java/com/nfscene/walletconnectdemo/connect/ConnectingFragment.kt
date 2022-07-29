package com.nfscene.walletconnectdemo.connect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nfscene.walletconnectdemo.R
import com.nfscene.walletconnectdemo.databinding.FragmentConnectingBinding

class ConnectingFragment : Fragment() {
    private var _binding: FragmentConnectingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val model: ConnectActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConnectingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.isWalletConnected().observe(viewLifecycleOwner) { isConnected ->
            findNavController().navigate(R.id.action_ConnectingFragment_to_DisconnectFragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.signIn()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}