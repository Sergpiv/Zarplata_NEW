package by.android.develop.zarplata_new;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import static by.android.develop.zarplata_new.Add_vipusk.sPref;
import static by.android.develop.zarplata_new.MainActivity.checkDataBase;
import static by.android.develop.zarplata_new.MainActivity.db_read_vipusk;
import static by.android.develop.zarplata_new.MainActivity.month;
import static by.android.develop.zarplata_new.MainActivity.month_array_const;
import static by.android.develop.zarplata_new.MainActivity.year;

public class ReportActivity_year extends AppCompatActivity {
    TextView t_title;
    SQLiteOpenHelper dbHelper_sql;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_year);
        sPref = getSharedPreferences(getString(R.string.APP_PREFERENCES_NAME), MODE_PRIVATE);
        ListView listView_year = (ListView) findViewById(R.id.listview_year);
        t_title = (TextView) findViewById(R.id.textView_rep_year);
        t_title.setText("за " + year + "г.");
        if (checkDataBase("/data/data/by.android.develop.zarplata_new/databases/" + sPref.getString("tab", "none") + ".db")) {

            dbHelper_sql = new SQLiteOpenHelper(this, sPref.getString("tab", "none") + ".db", null, 1) {
                @Override
                public void onCreate(SQLiteDatabase db) {
                }

                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                }
            };
            db_read_vipusk = dbHelper_sql.getReadableDatabase();
            db_read_vipusk.execSQL("ATTACH DATABASE '" + getString(R.string.rasz_path) + "' AS rasz;");
            Cursor cursor = db_read_vipusk.rawQuery("SELECT dat AS [_id], COUNT(day_temp) AS [day], CASE WHEN LENGTH(CAST(ROUND(SUM(summa_temp)/COUNT(day_temp),2) as text))-INSTR(CAST(ROUND(SUM(summa_temp)/COUNT(day_temp),2) as text),'.')=1 " +
                    "THEN CAST(ROUND(SUM(summa_temp)/COUNT(day_temp),2) as text) || '0' ELSE CAST(ROUND(SUM(summa_temp)/COUNT(day_temp),2) as text) END  AS [avg], CASE WHEN LENGTH(CAST(Round(Sum(summa_temp),2) as text))-INSTR(CAST(Round(Sum(summa_temp),2) as text),'.')=1 " +
                    "THEN CAST(Round(Sum(summa_temp),2) as text) || '0' ELSE CAST(Round(Sum(summa_temp),2) as text) END AS [summa] FROM(\n" +
                    "SELECT strftime('%m',data) AS [dat], strftime('%d',data) AS [day_temp], Sum([Colichestvo]*[raszenka]) AS [summa_temp]\n" +
                    "FROM rasz.[Raszenki] INNER JOIN zarplata ON rasz.raszenki.Cod = zarplata.Cod_raszenki\n" +
                    "GROUP BY strftime('%d',data), strftime('%m',data) \n" +
                    "HAVING (strftime('%Y',data)=?))\n" +
                    "GROUP BY dat", new String[]{year});
            startManagingCursor(cursor);
            String[] groupFrom = {"_id", "day", "avg", "summa"};
            int[] groupTo = {R.id.textView_expandable_month, R.id.textView_expandable_rab_day, R.id.textView_expandable_srednii, R.id.textView_expandable_sum_month};
            SimpleCursorAdapter sctAdapter = new SimpleCursorAdapter(getApplication(), R.layout.expandable_year_dat, cursor, groupFrom, groupTo);
            sctAdapter.setViewBinder(new MyViewBinder());
            listView_year.setAdapter(sctAdapter);

        }
    }
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper_sql!=null) dbHelper_sql.close();
    }
}

class MyViewBinder implements SimpleCursorAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        switch (view.getId()){
            case R.id.textView_expandable_month:
                TextView myTextView = (TextView) view.findViewById(R.id.textView_expandable_month);
                myTextView.setText(month_array_const[Integer.parseInt(cursor.getString(0))-1]);
            return true;
        }
        return false;
    }
}