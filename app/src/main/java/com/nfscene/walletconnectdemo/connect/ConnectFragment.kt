package com.nfscene.walletconnectdemo.connect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nfscene.walletconnectdemo.R
import com.nfscene.walletconnectdemo.databinding.FragmentConnectBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ConnectFragment : Fragment() {

    private var _binding: FragmentConnectBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val model: ConnectActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConnectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.isDAppConnected().observe(viewLifecycleOwner) { isConnected ->
            binding.buttonConnect.isEnabled = isConnected
        }
        binding.buttonConnect.setOnClickListener {
            findNavController().navigate(R.id.action_ConnectFragment_to_ConnectingFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}