package by.android.develop.zarplata_new;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static by.android.develop.zarplata_new.Add_vipusk.sPref;
import static by.android.develop.zarplata_new.MainActivity.db_read_vipusk;
import static by.android.develop.zarplata_new.MainActivity.month;
import static by.android.develop.zarplata_new.MainActivity.year;

public class Day_detal_Activity extends AppCompatActivity {
    SQLiteDatabase db_write;
    SimpleCursorAdapter simpleCursorAdapter;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       SQLiteOpenHelper dbHelper_sql = new SQLiteOpenHelper(this, sPref.getString("tab", "none") + ".db", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            }
        };
        db_write = dbHelper_sql.getReadableDatabase();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_detal_);
        TextView textView_detal=findViewById(R.id.textView_det_day);
        TextView textView_oper=findViewById(R.id.textView_day_oper);
        TextView textView_poasn=findViewById(R.id.textView_day_poasn);
        Intent intent=getIntent();
        textView_detal.setText(intent.getStringExtra("detal"));
        textView_oper.setText(intent.getStringExtra("oper"));
        textView_poasn.setText(intent.getStringExtra("poasn"));
        cursor=db_read_vipusk.rawQuery("SELECT _id, Cod_raszenki, strftime('%d',data) || '.'|| strftime('%m',data)||'.'||strftime('%Y',data)||'г.' AS [dat], Colichestvo||' шт.' AS [Colichestvo]\n" +
                "        FROM zarplata\n" +
                "        GROUP BY Cod_raszenki, data, Colichestvo\n" +
                "        HAVING ((Cod_raszenki=?) AND (strftime('%m',data)=?) AND (strftime('%Y',data)=?));",new String[]{intent.getStringExtra("Cod_oper"),month,year});
        startManagingCursor(cursor);
        String[] from = new String[] { "dat", "Colichestvo" };
        int[] to = new int[] { R.id.textView_expandaple_dat_day, R.id.textView_expandable_kol_day };
        simpleCursorAdapter=new SimpleCursorAdapter(this, R.layout.detal_day_expandable_view, cursor, from, to);
        ListView lvData = (ListView) findViewById(R.id.listView_day);
        lvData.setAdapter(simpleCursorAdapter);
        registerForContextMenu(lvData);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.cont_menu_day,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {

            case R.id.menu_edit_day:{


                final ContentValues cv = new ContentValues();
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
               Cursor cursor_temp=db_write.query("zarplata",new String[] {"_id", "Colichestvo"},null,null,"_id, Colichestvo","_id="+acmi.id,null);
                cursor_temp.moveToFirst();
                alert.setTitle("Количество: "+cursor_temp.getString(1)+" шт.");
                alert.setMessage("Введите новое значение:");
                final EditText input = new EditText(this);
                alert.setView(input);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        cv.put("Colichestvo",input.getText().toString());
                        db_write.update("zarplata", cv, "_id=?", new String[]{String.valueOf(acmi.id)});
                        cursor.requery();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();
                break;}
            case R.id.menu_delete_day:
                db_write.delete("zarplata", " _id=" + acmi.id, null);
                cursor.requery();
                simpleCursorAdapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }
}
