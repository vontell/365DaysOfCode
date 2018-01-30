package org.vontech.chipview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.view_chip.view.*

/**
 * A simple ChipView class for chips in Android. Provides image, text, and listener
 * functionality.
 * @author Aaron Vontell, from Vontech Software, LLC
 */
class ChipView: LinearLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        // Load the correct layout for this view
        inflate(context, R.layout.view_chip, this)

        // Display attributes only after the view has been inflated
        val attributes = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ChipView,
                defStyleAttr, 0)

        try {

            // Set the initial text for this label
            text = attributes.getString(R.styleable.ChipView_text)

            // We set the imageURL last since it takes precedence over a given resource.
            // We also still set the resource in case the user wants to use it as a default
            imageResource = attributes.getDrawable(R.styleable.ChipView_imageSrc)
            imageURL = attributes.getString(R.styleable.ChipView_imageURL)

            // We then initially hide the remove icon (a listener may be added later)
            displayRemoveIcon()

        } finally {
            attributes.recycle()
        }

    }


    // VARIABLE DEFINITIONS AND SETTERS FOR PROPERLY LOADING INFORMATION ---------------------------

    var text : String? = null

        /**
         * Sets the text label for this ChipView
         * @param value The string to set for this label
         */
        set(value) {
            field = value
            displayText()
        }

    var imageResource : Drawable? = null

        /**
         * Sets the image for this ChipView
         * @param value The drawable to set for this chip image
         */
        set(value) {
            field = value
            displayImage()
        }

    var imageURL : String? = null

        /**
         * Sets the image for this ChipView to be loaded from the given URL / path
         * @param value The URL path to an image to set for this chip
         */
        set(value) {
            field = value
            displayImage()
        }

    private var removeListener : OnChipRemovedListener? = null


    // METHODS FOR PROPERLY DISPLAYING THE SET INFORMATION -----------------------------------------

    /**
     * Displays a new ChipView label by setting the layout's text and reloading the view.
     */
    private fun displayText() {
        if (text != null) {
            chip_text.text = text
        } else {
            chip_text.text = ""
        }
        chip_text.invalidate()
        chip_text.requestLayout()
    }

    /**
     * Displays a new ChipView image by setting the layout's image and reloading the view.
     */
    private fun displayImage() {
        when {
            imageURL != null -> {
                chip_image.visibility = View.VISIBLE
                chip_image.load(imageURL!!)
            }
            imageResource != null -> {
                chip_image.visibility = View.VISIBLE
                chip_image.setImageDrawable(imageResource)
            }
            else -> {
                chip_image.visibility = View.GONE
                chip_image.setImageDrawable(null)
            }
        }
        chip_image.invalidate()
        chip_image.requestLayout()
    }

    /**
     * Displays the remove icon if a listener is available
     */
    private fun displayRemoveIcon() {
        if (removeListener != null) {
            chip_close.visibility = View.VISIBLE
            chip_close.setOnClickListener {
                removeListener!!.onRemove(this)
            }
        } else {
            chip_close.visibility = View.GONE
            chip_close.setOnClickListener {}
        }
        chip_close.invalidate()
        chip_close.requestLayout()
    }


    // METHODS AND INTERFACE FOR THE CHIP REMOVAL LISTENER -------------------------------------------------------

    /**
     * Interface definition for a callback to be invoked when a ChipView remove button is clicked.
     */
    interface OnChipRemovedListener {
        /**
         * Called when a ChipView remove button has been clicked
         *
         * @param v The ChipView that the user interacted with.
         */
        fun onRemove(v: View)
    }

    /**
     * Sets the OnClickListener or function to be called when the remove / close
     * button on the ChipView is clicked
     * @param listener The listener to be executed when the close button is clicked
     */
    fun setOnRemoveListener(listener: OnChipRemovedListener?) {
        removeListener = listener
        displayRemoveIcon()
    }

    fun setOnRemoveListener(listener: (v : View) -> Unit) {
        removeListener = object : OnChipRemovedListener {
            override fun onRemove(v: View) {
                listener(v)
            }
        }
        displayRemoveIcon()
    }


    // HELPER FUNCTIONS FOR PICASSO ----------------------------------------------------------------

    private val Context.picasso: Picasso
        get() = Picasso.with(this)

    private fun CircleImageView?.load(path: String) {
        this!!.context.picasso.load(path).into(this)
    }

}