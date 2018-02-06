package org.vontech.morrow

import android.content.Context
import android.view.View

/**
 * An interface for items that should display a View
 */
interface Viewable {

    fun generateView(context: Context): View

}