package by.android.develop.zarplata_new;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;

import static by.android.develop.zarplata_new.Add_vipusk.sPref;
import static by.android.develop.zarplata_new.MainActivity.checkDataBase;
import static by.android.develop.zarplata_new.MainActivity.db_read_vipusk;
import static by.android.develop.zarplata_new.MainActivity.month;
import static by.android.develop.zarplata_new.MainActivity.year;

public class ReportActivity_month extends AppCompatActivity {
    TextView t_title;
    ExpandableListView elvMain;
    SQLiteOpenHelper dbHelper_sql;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_month);
        elvMain = (ExpandableListView) findViewById(R.id.elvMain_month);
        sPref = getSharedPreferences(getString(R.string.APP_PREFERENCES_NAME), MODE_PRIVATE);
        t_title = (TextView) findViewById(R.id.textView_rep_month);
        t_title.setText("за " + MainActivity.month_array_const[Integer.parseInt(month) - 1] + " " + year + "г.");
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
            Updatedatabase(elvMain);
        }



    }

    private void Updatedatabase(ExpandableListView elvMain) {
        Cursor cursor = db_read_vipusk.rawQuery("SELECT strftime('%d',zarplata.data) AS [_id], strftime('%d',data) || '.'|| strftime('%m',data)||'.'||strftime('%Y',data) AS [dat], CASE WHEN LENGTH(CAST(Round(Sum([Colichestvo]*[raszenka]),2) as text))-INSTR(CAST(Round(Sum([Colichestvo]*[raszenka]),2) as text),'.')=1 THEN CAST(Round(Sum([Colichestvo]*[raszenka]),2) as text) || '0' ELSE CAST(Round(Sum([Colichestvo]*[raszenka]),2) as text) END AS [summa]\n" +
                "FROM rasz.[Raszenki] INNER JOIN zarplata ON rasz.raszenki.Cod = zarplata.Cod_raszenki\n" +
                "GROUP BY strftime('%m',data), strftime('%d',data)\n" +
                "HAVING (strftime('%m',data)=?);", new String[]{month});
        startManagingCursor(cursor);
        String[] groupFrom = {"dat", "summa"};
        int[] groupTo = {R.id.textView_expandable_day, R.id.textView_expandable_sum_day};
        // сопоставление данных и View для элементов
        String[] childFrom = {"detal", "oper", "summa"};
        int[] childTo = {R.id.textView_exp_mesaz_detal, R.id.textView_exp_mesaz_oper, R.id.textView_exp_mesaz_kol};
        // создаем адаптер и настраиваем список
        SimpleCursorTreeAdapter sctAdapter = new MyAdapter_month(getApplication(), cursor,
                R.layout.expandable_mesaz_dat, groupFrom,
                groupTo, R.layout.oper_mesaz_expandable, childFrom,
                childTo);
        elvMain.setAdapter(sctAdapter);
    }

    public static Cursor get_oper_Cursor(String anInt) {
        String str;
        str = "SELECT zarplata.Cod_detali AS [_id], rasz.Detal.detal AS [detal], rasz.Raszenki.nomer_operazii AS [oper], rasz.Raszenki.poasnenie AS [poasn], Sum(Colichestvo) AS [summa]\n" +
                "FROM rasz.[Raszenki] INNER JOIN zarplata ON rasz.raszenki.Cod = zarplata.Cod_raszenki\n" +
                "LEFT JOIN rasz.[Detal] ON rasz.[Detal].[cod]=zarplata.Cod_detali\n" +
                "GROUP BY zarplata.Cod_detali, rasz.Detal.detal, rasz.Raszenki.nomer_operazii, rasz.Raszenki.poasnenie, zarplata.data\n" +
                "HAVING ((strftime('%d',zarplata.data)=?) AND(strftime('%m',zarplata.data)=?) AND (strftime('%Y',zarplata.data)=?))\n" +
                "ORDER BY rasz.Raszenki.nomer_operazii";

        return db_read_vipusk.rawQuery(str, new String[]{anInt, month, year});
    }
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper_sql!=null) dbHelper_sql.close();
    }
}

class MyAdapter_month extends SimpleCursorTreeAdapter {
    public MyAdapter_month(Context context, Cursor cursor, int groupLayout,
                           String[] groupFrom, int[] groupTo, int childLayout,
                           String[] childFrom, int[] childTo) {
        super(context, cursor, groupLayout, groupFrom, groupTo,
                childLayout, childFrom, childTo);
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        int idColumn = groupCursor.getColumnIndex("_id");
        return ReportActivity_month.get_oper_Cursor(groupCursor.getString(idColumn));
    }


}