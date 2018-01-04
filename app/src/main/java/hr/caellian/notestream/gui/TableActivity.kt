package hr.caellian.notestream.gui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow

import java.util.ArrayList

import hr.caellian.notestream.R

/**
 * Created by caellyan on 26/06/17.
 */

@SuppressLint("Registered")
open class TableActivity : NavigationActivity() {

    internal var tableContent: TableLayout? = null
    internal var rows = ArrayList<TableRow>()
    internal var currentRow = 0

    override val drawerLayout: DrawerLayout?
        get() = findViewById<View>(R.id.table_layout) as DrawerLayout?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)

        tableContent = findViewById(R.id.tableContent)
    }

    protected fun addTableRow(): TableRow {
        val row = TableRow(this)
        row.id = currentRow++
        row.gravity = Gravity.CENTER
        tableContent?.addView(row)
        rows.add(row)
        return row
    }
}
