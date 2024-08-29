package com.tomorrowit.budgetgamer.presentation.fragments.main_menu


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.tomorrowit.budgetgamer.BuildConfig
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.common.config.Constants
import com.tomorrowit.budgetgamer.common.config.extensions.UI
import com.tomorrowit.budgetgamer.common.config.extensions.openActivityFade
import com.tomorrowit.budgetgamer.common.config.extensions.openActivityPush
import com.tomorrowit.budgetgamer.common.config.extensions.openActivityPushAny
import com.tomorrowit.budgetgamer.common.utils.Logic
import com.tomorrowit.budgetgamer.common.utils.ViewHandler
import com.tomorrowit.budgetgamer.common.utils.ViewHelper
import com.tomorrowit.budgetgamer.databinding.FragmentSettingsBinding
import com.tomorrowit.budgetgamer.domain.listeners.OnSingleClickListener
import com.tomorrowit.budgetgamer.domain.usecases.LoadImageUseCase
import com.tomorrowit.budgetgamer.domain.usecases.activity.OpenApplicationSettingsUseCase
import com.tomorrowit.budgetgamer.domain.usecases.activity.OpenUrlAddressUseCase
import com.tomorrowit.budgetgamer.domain.usecases.permissions.CanAskForPermissionUseCase
import com.tomorrowit.budgetgamer.domain.usecases.permissions.notifications.IsNotificationsPermissionGivenUseCase
import com.tomorrowit.budgetgamer.domain.usecases.permissions.notifications.RequireNotificationsPermissionUseCase
import com.tomorrowit.budgetgamer.presentation.activities.HtmlActivity
import com.tomorrowit.budgetgamer.presentation.activities.LicensesActivity
import com.tomorrowit.budgetgamer.presentation.activities.SplashScreenActivity
import com.tomorrowit.budgetgamer.presentation.activities.NotificationsActivity
import com.tomorrowit.budgetgamer.presentation.activities.AuthActivity
import com.tomorrowit.budgetgamer.presentation.dialogs.InfoDialog
import com.tomorrowit.budgetgamer.presentation.dialogs.QuestionDialog
import com.tomorrowit.budgetgamer.presentation.activities.EditAccountActivity
import com.tomorrowit.budgetgamer.presentation.viewmodels.MainViewModel
import com.tomorrowit.budgetgamer.presentation.viewmodels.main_menu.SettingsState
import com.tomorrowit.budgetgamer.presentation.viewmodels.main_menu.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private val viewModel: SettingsViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var requireNotificationsPermissionUseCase: RequireNotificationsPermissionUseCase

    @Inject
    lateinit var isNotificationsPermissionGivenUseCase: IsNotificationsPermissionGivenUseCase

    @Inject
    lateinit var canAskForPermissionUseCase: CanAskForPermissionUseCase

    @Inject
    lateinit var openApplicationSettingsUseCase: OpenApplicationSettingsUseCase

    @Inject
    lateinit var openUrlAddressUseCase: OpenUrlAddressUseCase

    @Inject
    lateinit var loadImageUseCase: LoadImageUseCase

    private val logoutDialog: QuestionDialog by lazy {
        QuestionDialog(
            requireContext(),
            getString(R.string.sign_out),
            getString(R.string.are_you_sure_sign_out),
            object : QuestionDialog.QuestionDialogListener {
                override fun YesButtonAction() {
                    viewModel.signOut()
                    requireActivity().openActivityFade(SplashScreenActivity())
                }

                override fun NoButtonAction() {
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSettingsBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    override fun onStart() {
        super.onStart()
        observeState()
    }

    private fun observeState() {
        viewModel.state.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is SettingsState.Init -> {

                    }

                    is SettingsState.IsAuthenticated -> {
                        withContext(Dispatchers.UI) {
                            showUserStuff()
                        }
                    }

                    is SettingsState.IsError -> {
                        withContext(Dispatchers.UI) {
                            InfoDialog(
                                this@SettingsFragment.requireContext(),
                                getString(R.string.error),
                                state.message
                            ).show()
                        }
                    }

                    is SettingsState.IsNotAuthenticated -> {
                        withContext(Dispatchers.UI) {
                            hideUserStuff()
                        }
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        activityViewModel.showBanner.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .distinctUntilChanged()
            .onEach {
                if (it) {
                    ViewHelper.showView(binding.settingsFragmentBanner.root)
                } else {
                    ViewHelper.hideView(binding.settingsFragmentBanner.root)
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshUser()
    }

    private fun showNotifications() {
        if (isNotificationsPermissionGivenUseCase.invoke()) {
            requireActivity().openActivityPush(NotificationsActivity())
        } else {
            if (canAskForPermissionUseCase.invoke(
                    this@SettingsFragment.requireActivity(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                requireNotificationsPermissionUseCase.invoke(this@SettingsFragment)
            } else {
                QuestionDialog(
                    this@SettingsFragment.requireContext(),
                    getString(R.string.attention),
                    getString(R.string.notifications_disabled_popup),
                    object : QuestionDialog.QuestionDialogListener {
                        override fun YesButtonAction() {
                            openApplicationSettingsUseCase.invoke(OpenApplicationSettingsUseCase.NOTIFICATION_SETTINGS)
                        }

                        override fun NoButtonAction() {

                        }
                    }).show()
            }
        }
    }


    private fun setViews() {
        //region Click listeners
        binding.fragmentSettingsNotifications.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                showNotifications()
            }
        })

        binding.fragmentSettingsOption1.setOnClickListener {
            ViewHelper.toggleVisibility(binding.fragmentSettingsList1)
            ViewHandler.toggleChevronRotation(binding.fragmentSettingsOption1Chevron)
            ViewHandler.toggleTextValues(
                binding.fragmentSettingsOption1Description,
                getString(R.string.show_more),
                getString(R.string.hide)
            )
        }

        binding.fragmentSettingsOption2.setOnClickListener {
            ViewHelper.toggleVisibility(binding.fragmentSettingsList2)
            ViewHandler.toggleChevronRotation(binding.fragmentSettingsOption2Chevron)
            ViewHandler.toggleTextValues(
                binding.fragmentSettingsOption2Description,
                getString(R.string.show_more),
                getString(R.string.hide)
            )
        }

        binding.fragmentSettingsEditAccount.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                requireActivity().openActivityPush(EditAccountActivity())
            }
        })

        binding.fragmentSettingsLogOut.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                logoutDialog.show()
            }
        })

        binding.fragmentSettingsAppInfo.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                requireActivity().openActivityPushAny(
                    HtmlActivity(),
                    mapOf(HtmlActivity.PAGE_CODE to HtmlActivity.ABOUT_APP)
                )
            }
        })

        binding.fragmentSettingsRateUs.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                openUrlAddressUseCase.invoke(Constants.URL.PLAY_STORE)
            }
        })

        binding.fragmentSettingsShareApp.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                openApplicationSettingsUseCase.invoke(OpenApplicationSettingsUseCase.SHARE_APP)
            }
        })

        binding.fragmentSettingsJoinDiscord.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {

                openUrlAddressUseCase.invoke(Constants.URL.DISCORD)
            }
        })

        binding.fragmentSettingsCheckSource.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {

                openUrlAddressUseCase.invoke(Constants.URL.GITHUB)
            }
        })

        binding.fragmentSettingsContactUs.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                openApplicationSettingsUseCase.invoke(OpenApplicationSettingsUseCase.SEND_EMAIL)
            }
        })

        binding.fragmentSettingsLinkedin.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                openUrlAddressUseCase.invoke(Constants.URL.LINKEDIN)
            }
        })

        binding.fragmentSettingsGdpr.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                requireActivity().openActivityPushAny(
                    HtmlActivity(),
                    mapOf(HtmlActivity.PAGE_CODE to HtmlActivity.GDPR)
                )
            }
        })

        binding.fragmentSettingsTerms.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                requireActivity().openActivityPushAny(
                    HtmlActivity(),
                    mapOf(HtmlActivity.PAGE_CODE to HtmlActivity.TERMS_AND_CONDITIONS)
                )
            }
        })

        binding.fragmentSettingsOpenSource.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                requireActivity().openActivityPush(LicensesActivity())
            }
        })
        //endregion

        binding.fragmentSettingsAppVersion.text = if (BuildConfig.BUILD_TYPE.equals("release")) {
            getString(R.string.app_version_release, BuildConfig.VERSION_NAME)
        } else {
            getString(R.string.app_version_debug, BuildConfig.VERSION_NAME, BuildConfig.BUILD_TYPE)
        }
    }

    private fun hideUserStuff() {
        ViewHelper.hideView(binding.fragmentSettingsProfile)
        ViewHelper.hideView(binding.fragmentSettingsName)
        ViewHelper.hideView(binding.fragmentSettingsEmail)

        ViewHelper.disableView(binding.fragmentSettingsNotifications)
        ViewHelper.disableView(binding.fragmentSettingsEditAccount)
        ViewHelper.disableView(binding.fragmentSettingsLogOut)

        binding.settingsFragmentBanner.root.setOnClickListener(object :
            OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                requireActivity().openActivityPush(AuthActivity())
            }
        })

        binding.settingsFragmentBanner.bannerLayoutClose.setOnClickListener(object :
            OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                activityViewModel.dismissBanner()
            }
        })
    }

    private fun showUserStuff() {
        binding.fragmentSettingsName.text = viewModel.getUserName()
        binding.fragmentSettingsEmail.text = viewModel.getUserEmail()

        if (viewModel.getUserPhotoUrl() != null) {
            ViewHelper.hideView(binding.fragmentSettingsProfileInitials)
            loadImageUseCase.invoke(
                binding.fragmentSettingsProfilePicture,
                viewModel.getUserPhotoUrl().toString(),
                LoadImageUseCase.PROFILE_IMAGE
            )
        } else {
            binding.fragmentSettingsProfileInitials.text =
                Logic.getInitialsFromName(viewModel.getUserName())
            ViewHelper.showView(binding.fragmentSettingsProfileInitials)
        }
    }
}