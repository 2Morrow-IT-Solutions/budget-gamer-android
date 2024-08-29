package com.tomorrowit.budgetgamer.presentation.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import com.tomorrowit.budgetgamer.R
import com.tomorrowit.budgetgamer.common.config.extensions.finishAnimation
import com.tomorrowit.budgetgamer.databinding.ActivityLicensesBinding
import com.tomorrowit.budgetgamer.presentation.base.BaseSlideActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LicensesActivity : BaseSlideActivity() {

    private lateinit var binding: ActivityLicensesBinding
    private lateinit var licenseNames: Array<String>
    private lateinit var licenseLinks: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        //region Needed
        super.onCreate(savedInstanceState)

        binding = ActivityLicensesBinding.inflate(layoutInflater)

        setContentView(binding.root)
        //endregion

        licenseNames = this@LicensesActivity.resources.getStringArray(R.array.license_names)
        licenseLinks = this@LicensesActivity.resources.getStringArray(R.array.license_links)

        val adapter: ArrayAdapter<String> =
            ArrayAdapter(this@LicensesActivity, android.R.layout.simple_list_item_1, licenseNames)
        binding.licensesActivityListview.adapter = adapter

        binding.licensesActivityListview.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                openUrl(licenseLinks[p2])
            }
        }

        binding.licensesActivityBar.setNavigationOnClickListener {
            this@LicensesActivity.finish()
        }
    }

    private fun openUrl(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    override fun finish() {
        super.finish()
        finishAnimation()
    }
}