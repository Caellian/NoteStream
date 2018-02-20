package hr.caellian.notestream.gui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import hr.caellian.notestream.R
import java.util.*

/**
 * Created by caellyan on 26/06/17.
 */

@SuppressLint("Registered")
open class TableActivity : NavigationActivity() {

    internal var tableContent: TableLayout? = null
    internal var rows = ArrayList<TableRow>()
    internal var currentRow = 0

    override val drawerLayout: DrawerLayout?
        get() = findViewById(R.id.table_layout)

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
