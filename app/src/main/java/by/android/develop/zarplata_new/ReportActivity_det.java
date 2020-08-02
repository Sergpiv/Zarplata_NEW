package by.android.develop.zarplata_new;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;

import static by.android.develop.zarplata_new.Add_vipusk.sPref;
import static by.android.develop.zarplata_new.MainActivity.checkDataBase;
import static by.android.develop.zarplata_new.MainActivity.db_read_vipusk;
import static by.android.develop.zarplata_new.MainActivity.month;
import static by.android.develop.zarplata_new.MainActivity.year;

public class ReportActivity_det extends AppCompatActivity {
    TextView t_title;
    ExpandableListView elvMain;
    SQLiteOpenHelper dbHelper_sql;
    SimpleCursorTreeAdapter sctAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_det);
        elvMain = (ExpandableListView) findViewById(R.id.elvMain_det);
        sPref = getSharedPreferences(getString(R.string.APP_PREFERENCES_NAME), MODE_PRIVATE);
        t_title = (TextView) findViewById(R.id.textView_rep_det);
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
            db_read_vipusk = dbHelper_sql.getWritableDatabase();
            db_read_vipusk.execSQL("ATTACH DATABASE '" + getString(R.string.rasz_path) + "' AS rasz;");
        }
elvMain.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Cursor cursor_det=sctAdapter.getGroup(groupPosition);
        Cursor cursor_oper = sctAdapter.getChild(groupPosition,childPosition);
        Intent intent = new Intent(v.getContext(), Day_detal_Activity.class);
        intent.putExtra("detal",cursor_det.getString(1));
        intent.putExtra("Cod_oper",cursor_oper.getString(0));
        intent.putExtra("oper",cursor_oper.getString(1));
        intent.putExtra("poasn",cursor_oper.getString(2));
        startActivity(intent);
        return false;
    }
});


    }
@Override
protected void onResume() {
    super.onResume();
    Updatedatabase(elvMain);
}

    private void Updatedatabase(ExpandableListView elvMain) {
        Cursor cursor = db_read_vipusk.rawQuery("SELECT zarplata.Cod_detali AS [_id], rasz.Detal.detal AS [detal], CASE WHEN LENGTH(CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text))-INSTR(CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text),'.')=1 THEN CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text) || '0' ELSE CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text) END AS [summa]\n" +
                "                FROM rasz.[Raszenki] INNER JOIN zarplata ON rasz.raszenki.Cod = zarplata.Cod_raszenki\n" +
                "                LEFT JOIN rasz.[Detal] ON rasz.[Detal].[cod]=zarplata.Cod_detali\n" +
                "                GROUP BY zarplata.Cod_detali, rasz.Detal.detal, strftime('%m',zarplata.data), strftime('%Y',zarplata.data)\n" +
                "               HAVING ((strftime('%m',zarplata.data)=?) AND (strftime('%Y',zarplata.data)=?))\n" +
                "               ORDER BY rasz.detal.detal", new String[]{month,year});
        startManagingCursor(cursor);
        String[] groupFrom = {"detal", "summa"};
        int[] groupTo = {R.id.textView_expandable_detal, R.id.textView_expandable_sum_detal};
        // сопоставление данных и View для элементов
        String[] childFrom = {"oper", "poasn", "summa"};
        int[] childTo = {R.id.textView_exp_det_oper, R.id.textView_exp_det_poasn, R.id.textView_exp_det_sum};
        // создаем адаптер и настраиваем список
        sctAdapter = new MyAdapter_det(getApplication(), cursor,
                R.layout.expandable_mesaz_det, groupFrom,
                groupTo, R.layout.oper_det_expandable, childFrom,
                childTo);
        elvMain.setAdapter(sctAdapter);
    }





    public static Cursor get_oper_Cursor(String anInt) {
        String str;
        str = "SELECT zarplata.Cod_raszenki AS [_id], rasz.Raszenki.nomer_operazii AS [oper], rasz.Raszenki.poasnenie AS [poasn], CASE WHEN LENGTH(CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text))-INSTR(CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text),'.')=1 THEN CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text) || '0' ELSE CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text) END AS [summa]\n" +
                "                FROM rasz.[Raszenki] INNER JOIN zarplata ON rasz.raszenki.Cod = zarplata.Cod_raszenki\n" +
                "                GROUP BY zarplata.Cod_raszenki, rasz.Raszenki.nomer_operazii, rasz.Raszenki.poasnenie, strftime('%m',zarplata.data),strftime('%Y',zarplata.data)\n" +
                "                HAVING ((strftime('%m',zarplata.data)=?) AND (strftime('%Y',zarplata.data)=?) AND (zarplata.Cod_detali=?))\n" +
                "                ORDER BY rasz.Raszenki.nomer_operazii";

        return db_read_vipusk.rawQuery(str, new String[]{month, year, anInt});
    }
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper_sql!=null) dbHelper_sql.close();
    }
}

class MyAdapter_det extends SimpleCursorTreeAdapter {
    public MyAdapter_det(Context context, Cursor cursor, int groupLayout,
                           String[] groupFrom, int[] groupTo, int childLayout,
                           String[] childFrom, int[] childTo) {
        super(context, cursor, groupLayout, groupFrom, groupTo,
                childLayout, childFrom, childTo);
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        int idColumn = groupCursor.getColumnIndex("_id");

        return ReportActivity_det.get_oper_Cursor(groupCursor.getString(idColumn));
    }



}
