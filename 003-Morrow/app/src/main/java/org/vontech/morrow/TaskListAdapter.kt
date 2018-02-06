package org.vontech.morrow

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import kotlinx.android.synthetic.main.dialog_new.view.*
import kotlinx.android.synthetic.main.view_task.view.*
import kotlinx.android.synthetic.main.view_title.view.*

/**
 * An adapter for a sectioned Task list
 * @author Aaron Vontell (aaron@vontech.org), on behalf of Vontech Software, LLC
 */
class TaskListAdapter(tasks: List<Task>) : BaseAdapter() {

    private var tasks: ArrayList<Task>
    private var sections: ArrayList<ArrayList<Task>>

    init {
        // Order the tasks by day / section, while maintaining order
        sections = arrayListOf<ArrayList<Task>>()
        for (i in Day.values()) {
            sections.add(ArrayList())
        }
        for (task in tasks) {
            sections[task.currentAllocation.ordinal].add(task)
        }
        this.tasks = ArrayList()
        for (i in sections.indices) {
            this.tasks.addAll(sections[i])
        }
    }

    override fun getView(position: Int, convertView: View?, container: ViewGroup) : View {

        val viewType = getItemViewType(position)
        val view: View?
        when {
            viewType < 0 -> {
                view = LayoutInflater.from(container.context).inflate(R.layout.view_task, null)
                val task = getItem(position)
                view.taskCheck.isChecked = task!!.currentAllocation == Day.COMPLETED
                view.taskText.text = task.content

                if (task.currentAllocation == Day.COMPLETED) {
                    view.taskText.setTextColor(container.context.resources.getColor(R.color.completed_text))
                }

                view.taskCheck.setOnClickListener {
                    if (task.currentAllocation == Day.COMPLETED) {
                        task.currentAllocation = Day.TODAY
                    } else {
                        task.currentAllocation = Day.COMPLETED
                    }
                    notifyDataSetChanged()
                }

                view.taskText.setOnClickListener {
                    startEdit(container.context, task)
                }

            }
            sections[viewType].size > 0 -> {
                view = LayoutInflater.from(container.context).inflate(R.layout.view_title, null)
                view.titleView.text = Day.values()[viewType].toString()
                view.titleImage.setImageDrawable(container.context.resources.getDrawable(Day.values()[viewType].getDrawable()))
            }
            else -> view = View(container.context)
        }

        return view!!

    }

    private fun startEdit(context: Context, task: Task) {

        selectedDayFromDialog = task.currentAllocation
        val wrapInScrollView = true
        val dialog = MaterialDialog.Builder(context)
                .title(R.string.edit_dialog_title)
                .customView(R.layout.dialog_new, wrapInScrollView)
                .positiveText(R.string.edit_dialog_create)
                .negativeText(R.string.edit_dialog_cancel)
                .theme(Theme.DARK)
                .backgroundColor(context.resources.getColor(R.color.main_background))
                .typeface(ResourcesCompat.getFont(context, R.font.dosis_bold), ResourcesCompat.getFont(context, R.font.dosis))
                .onPositive { dialog, which ->
                    val view = dialog.customView!!
                    val content = view.task_edit.text.toString()
                    task.content = content
                    task.currentAllocation = selectedDayFromDialog
                    notifyDataSetChanged()
                }
                .show()
        setupEdit(context, dialog, task)

    }

    private var selectedDayFromDialog = Day.TODAY
    private fun setupEdit(context: Context, dialog: MaterialDialog, task: Task) {

        val view = dialog.customView!!

        val boldFont = ResourcesCompat.getFont(context, R.font.dosis_bold)
        val font = ResourcesCompat.getFont(context, R.font.dosis)

        view.complete_view.visibility = View.VISIBLE
        view.complete_text.visibility = View.VISIBLE
        view.task_edit.text = Editable.Factory.getInstance().newEditable(task.content)

        // TODO: Clean this up
        view.today_view.setOnClickListener {
            view.today_view.setImageDrawable(context.resources.getDrawable(R.drawable.today_sel))
            view.tomorrow_view.setImageDrawable(context.resources.getDrawable(R.drawable.tomorrow))
            view.overmorrow_view.setImageDrawable(context.resources.getDrawable(R.drawable.overmorrow))
            view.complete_view.setImageDrawable(context.resources.getDrawable(R.drawable.save))
            view.today_text.typeface = boldFont
            view.tomorrow_text.typeface = font
            view.overmorrow_text.typeface = font
            view.complete_text.typeface = font
            selectedDayFromDialog = Day.TODAY
        }

        view.tomorrow_view.setOnClickListener {
            view.today_view.setImageDrawable(context.resources.getDrawable(R.drawable.today))
            view.tomorrow_view.setImageDrawable(context.resources.getDrawable(R.drawable.tomorrow_sel))
            view.overmorrow_view.setImageDrawable(context.resources.getDrawable(R.drawable.overmorrow))
            view.complete_view.setImageDrawable(context.resources.getDrawable(R.drawable.save))
            view.today_text.typeface = font
            view.tomorrow_text.typeface = boldFont
            view.overmorrow_text.typeface = font
            view.complete_text.typeface = font
            selectedDayFromDialog = Day.TOMORROW
        }
        view.overmorrow_view.setOnClickListener {
            view.today_view.setImageDrawable(context.resources.getDrawable(R.drawable.today))
            view.tomorrow_view.setImageDrawable(context.resources.getDrawable(R.drawable.tomorrow))
            view.overmorrow_view.setImageDrawable(context.resources.getDrawable(R.drawable.overmorrow_sel))
            view.complete_view.setImageDrawable(context.resources.getDrawable(R.drawable.save))
            view.today_text.typeface = font
            view.tomorrow_text.typeface = font
            view.overmorrow_text.typeface = boldFont
            view.complete_text.typeface = font
            selectedDayFromDialog = Day.OVERMORROW
        }
        view.complete_view.setOnClickListener {
            view.today_view.setImageDrawable(context.resources.getDrawable(R.drawable.today))
            view.tomorrow_view.setImageDrawable(context.resources.getDrawable(R.drawable.tomorrow))
            view.overmorrow_view.setImageDrawable(context.resources.getDrawable(R.drawable.overmorrow))
            view.complete_view.setImageDrawable(context.resources.getDrawable(R.drawable.save_sel))
            view.today_text.typeface = font
            view.tomorrow_text.typeface = font
            view.overmorrow_text.typeface = font
            view.complete_text.typeface = boldFont
            selectedDayFromDialog = Day.COMPLETED
        }

        when (selectedDayFromDialog) {
            Day.TODAY -> view.today_view.callOnClick()
            Day.TOMORROW -> view.tomorrow_view.callOnClick()
            Day.OVERMORROW -> view.overmorrow_view.callOnClick()
            Day.COMPLETED -> view.complete_view.callOnClick()
        }

    }

    fun addTask(task: Task) {
        tasks.add(task)
    }

    fun clearTasks() {
        tasks.clear()
        notifyDataSetChanged()
    }

    fun makeAllTasks(day: Day) {
        tasks.forEach { it.currentAllocation = day }
        notifyDataSetChanged()
    }

    override fun notifyDataSetChanged() {

        // TODO: Instead of being non-DRY and recomputing on every add, find the right
        //       index based off title labels and insert into sections and tasks there

        // TODO: This shouldnt be called right away

        // Order the tasks by day / section, while maintaining order
        sections = arrayListOf<ArrayList<Task>>()
        for (i in Day.values()) {
            sections.add(ArrayList())
        }
        for (task in tasks) {
            sections[task.currentAllocation.ordinal].add(task)
        }
        this.tasks = ArrayList()
        for (i in sections.indices) {
            this.tasks.addAll(sections[i])
        }

        super.notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val titlePositions = getLabelIndices()
        return titlePositions.indexOf(position)
    }

    override fun getCount() : Int {

        // Total number of tasks + total number of labels
        return tasks.size + sections.size

    }

    private fun getLabelIndices(): List<Int> {

        val titlePositions = arrayListOf<Int>()
        var totalCount = 0
        for (i in sections.indices) {
            val size = sections[i].size
            titlePositions.add(totalCount)
            totalCount += size + 1
        }

        return titlePositions

    }

    override fun getItem(position: Int): Task? {

        val titlePositions = getLabelIndices()

        if (position in titlePositions) {
            return null
        }

        var lastValue = 0
        for (i in titlePositions.indices) {
            val value = titlePositions[i]
            if (position < value) {
                return sections[i - 1][position - lastValue - 1]
            } else {
                lastValue = value
            }
        }
        return sections[sections.size - 1][position - lastValue - 1]

    }

    override fun getItemId(position: Int): Long {
        val item = getItem(position) ?: return 0
        return getItem(position)!!.hashCode().toLong()
    }

    fun getTasks(): ArrayList<Task> {
        return tasks
    }

}