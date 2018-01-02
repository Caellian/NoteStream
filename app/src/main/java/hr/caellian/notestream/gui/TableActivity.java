package hr.caellian.notestream.gui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

import hr.caellian.notestream.R;

/**
 * Created by caellyan on 26/06/17.
 */

public class TableActivity extends NavigationActivity {

    TableLayout tableContent;
    ArrayList<TableRow> rows = new ArrayList<>();
    int currentRow = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        tableContent = findViewById(R.id.tableContent);
    }

    protected TableRow addTableRow() {
        TableRow row = new TableRow(this);
        row.setId(currentRow++);
        row.setGravity(Gravity.CENTER);
        tableContent.addView(row);
        rows.add(row);
        return row;
    }

    @Override
    protected DrawerLayout getDrawerLayout() {
        return (DrawerLayout) findViewById(R.id.table_layout);
    }
}
