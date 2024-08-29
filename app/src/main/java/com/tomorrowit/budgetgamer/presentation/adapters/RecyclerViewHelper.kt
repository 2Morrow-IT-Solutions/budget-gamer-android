package com.tomorrowit.budgetgamer.presentation.adapters

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tomorrowit.budgetgamer.R

object RecyclerViewHelper {

    @JvmStatic
    fun getGridLayoutManager(context: Context): LinearLayoutManager {
        return if (context.resources.getInteger(R.integer.recyclerViewColumns) != 1) {
            GridLayoutManager(
                context,
                context.resources.getInteger(R.integer.recyclerViewColumns)
            )
        } else {
            LinearLayoutManager(context)
        }
    }

    @JvmStatic
    fun getGridSpacingItemDecoration(context: Context): GridSpacingItemDecoration {
        return GridSpacingItemDecoration(
            context.resources.getInteger(R.integer.recyclerViewColumns),
            context.resources.getDimension(R.dimen.screen_margin).toInt(),
            true
        )
    }
}