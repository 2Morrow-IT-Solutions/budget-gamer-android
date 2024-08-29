package com.tomorrowit.budgetgamer.presentation.fragments.main_menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tomorrowit.budgetgamer.databinding.FragmentGiveawayBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GiveawayFragment : Fragment() {

    private lateinit var binding: FragmentGiveawayBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentGiveawayBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root
}