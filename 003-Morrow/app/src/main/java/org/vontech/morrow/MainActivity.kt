package org.vontech.morrow

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.DateTime
import kotlin.collections.ArrayList
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.content_main.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.dialog_new.view.*
import org.joda.time.MutableDateTime
import org.joda.time.Days
import kotlin.math.max


/**
 * The main user interface for the Morrow application.
 * @author Aaron Vontell (aaron@vontech.org), on behalf of Vontech Software, LLC
 */
class MainActivity : AppCompatActivity() {

    // Constants
    private val PREFS_FILENAME = "org.vontech.morrow.prefs"

    // Activity variables
    private lateinit var prefs: SharedPreferences
    private lateinit var adapter: TaskListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Grab preferences to load tasks
        prefs = this.getSharedPreferences(PREFS_FILENAME, 0)

        loadSavedTasks()

        fab.setOnClickListener { view ->
            startNewTaskProcess()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_clear -> {
                clearTasks()
                return true
            }
            R.id.action_today -> {
                makeAllTasks(Day.TODAY)
                return true
            }
            R.id.action_complete -> {
                makeAllTasks(Day.COMPLETED)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun loadSavedTasks() {

        // Retrieve a list of the tasks
        val tasks: ArrayList<Task> = Gson().fromJson(prefs.getString("SAVED_TASKS", "[]"), object : TypeToken<List<Task>>() {}.type)
        Log.e("Got tasks", prefs.getString("SAVED_TASKS", "[]"))

        // Update allocations
        updateDestinations(tasks)

        // Supply these tasks to the recycler
        adapter = TaskListAdapter(tasks)
        list_view.adapter = adapter
        adapter.notifyDataSetChanged()

    }

    private fun updateDestinations(tasks: ArrayList<Task>) {

        for (t in tasks) {
            if (t.currentAllocation != Day.COMPLETED) {
                val epoch = MutableDateTime()
                epoch.setDate(0)
                val daysSinceEpoch = Days.daysBetween(epoch, MutableDateTime(DateTime.now())).days
                val offset = daysSinceEpoch - t.lastAllocated
                t.currentAllocation = Day.values()[max(0, t.currentAllocation.ordinal - offset)]
                t.lastAllocated = daysSinceEpoch
            }
        }

    }

    private fun saveTasks() {

        Log.e("TASKS", "About to save tasks")
        val gson = Gson()
        val jsonString = gson.toJson(adapter.getTasks())

        val editor = prefs.edit()
        editor.putString("SAVED_TASKS", jsonString)
        editor.apply()
        Log.e("TASKS", "Saved tasks")
        Log.e("TASKS", jsonString)

    }

    private fun clearTasks() {
        adapter.clearTasks()
        saveTasks()
    }

    private fun makeAllTasks(day: Day) {
        adapter.makeAllTasks(day)
        saveTasks()
    }

    private fun startNewTaskProcess() {

        val wrapInScrollView = true
        val dialog = MaterialDialog.Builder(this)
                .title(R.string.new_dialog_title)
                .customView(R.layout.dialog_new, wrapInScrollView)
                .positiveText(R.string.new_dialog_create)
                .negativeText(R.string.new_dialog_cancel)
                .theme(Theme.DARK)
                .backgroundColor(resources.getColor(R.color.main_background))
                .typeface(ResourcesCompat.getFont(this, R.font.dosis_bold), ResourcesCompat.getFont(this, R.font.dosis))
                .onPositive { dialog, which ->
                    val view = dialog.customView!!
                    val content = view.task_edit.text.toString()
                    addTask(content, selectedDayFromDialog)
                    selectedDayFromDialog = Day.TODAY
                }
                .show()
        setDialogListeners(dialog)

    }

    private var selectedDayFromDialog = Day.TODAY
    private fun setDialogListeners(dialog: MaterialDialog) {

        val view = dialog.customView!!

        view.task_edit.requestFocus()

        val boldFont = ResourcesCompat.getFont(this, R.font.dosis_bold)
        val font = ResourcesCompat.getFont(this, R.font.dosis)

        // TODO: Clean this up
        view.today_view.setOnClickListener {
            view.today_view.setImageDrawable(resources.getDrawable(R.drawable.today_sel))
            view.tomorrow_view.setImageDrawable(resources.getDrawable(R.drawable.tomorrow))
            view.overmorrow_view.setImageDrawable(resources.getDrawable(R.drawable.overmorrow))
            view.today_text.typeface = boldFont
            view.tomorrow_text.typeface = font
            view.overmorrow_text.typeface = font
            selectedDayFromDialog = Day.TODAY
        }

        view.tomorrow_view.setOnClickListener {
            view.today_view.setImageDrawable(resources.getDrawable(R.drawable.today))
            view.tomorrow_view.setImageDrawable(resources.getDrawable(R.drawable.tomorrow_sel))
            view.overmorrow_view.setImageDrawable(resources.getDrawable(R.drawable.overmorrow))
            view.today_text.typeface = font
            view.tomorrow_text.typeface = boldFont
            view.overmorrow_text.typeface = font
            selectedDayFromDialog = Day.TOMORROW
        }

        view.overmorrow_view.setOnClickListener {
            view.today_view.setImageDrawable(resources.getDrawable(R.drawable.today))
            view.tomorrow_view.setImageDrawable(resources.getDrawable(R.drawable.tomorrow))
            view.overmorrow_view.setImageDrawable(resources.getDrawable(R.drawable.overmorrow_sel))
            view.today_text.typeface = font
            view.tomorrow_text.typeface = font
            view.overmorrow_text.typeface = boldFont
            selectedDayFromDialog = Day.OVERMORROW
        }

    }

    private fun addTask(content: String, day: Day) {

        val epoch = MutableDateTime()
        epoch.setDate(0)
        val daysSinceEpoch = Days.daysBetween(epoch, MutableDateTime(DateTime.now())).days
        val task = Task(content, day, daysSinceEpoch, daysSinceEpoch)
        adapter.addTask(task)
        adapter.notifyDataSetChanged()
        saveTasks()

    }

}
