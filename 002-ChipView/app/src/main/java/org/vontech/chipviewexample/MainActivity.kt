package org.vontech.chipviewexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.vontech.chipview.ChipView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the remove listener using a lambda function
        customChip.setOnRemoveListener({
            Toast.makeText(this, "Remove chip clicked", Toast.LENGTH_SHORT).show()
        })

        // Set the remove listener using an anonymous interface
        customChipThree.setOnRemoveListener(object: ChipView.OnChipRemovedListener {
            override fun onRemove(v: View) {
                Toast.makeText(baseContext, "Remove chip clicked", Toast.LENGTH_SHORT).show()
            }
        })

        // Programmatically set the drawable for an image
        customChipTwo.imageURL = "http://www.ilr.cornell.edu/sites/ilr.cornell.edu/files/styles/borealis_focussed_thumbnail_respondsmall/public/field_uploads/node_basic_page/field_image/leigh-amaro-04.jpg?itok=TGFueHuz"

        // Programmatically set a click listener for the chip
        customChip.setOnClickListener {
            Toast.makeText(this, "Chip clicked", Toast.LENGTH_SHORT).show()
        }

    }

}
