package by.android.develop.zarplata_new;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Add_vipusk extends AppCompatActivity implements View.OnClickListener {
    private static SQLiteDatabase db_read,db_write;
    ContentValues cv;
    EditText currentDateTime;
    Cursor curs,curs_first;
    EditText kol_edit;
   String[] details;
    Detal_id[] details_class;
    Detal_id detal;
    public static SharedPreferences sPref;
    DatabaseHelper databaseHelper;
    DBHelper dbHelper;
    Calendar dateAndTime = Calendar.getInstance();
    Button button_ok;
   public ListView lvData;
    DateFormat dateFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vipusk);
        lvData=(ListView) findViewById(R.id.lvData);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        kol_edit=findViewById(R.id.editText_kol);
        button_ok=(Button) findViewById(R.id.button_Ok);
        cv = new ContentValues();
        sPref = getSharedPreferences(getString(R.string.APP_PREFERENCES_NAME), MODE_PRIVATE);
        databaseHelper = new DatabaseHelper(this);
       dbHelper=new DBHelper(this,sPref.getString("tab","none")+".db");
        db_write = dbHelper.getWritableDatabase();
        db_read = databaseHelper.getReadableDatabase();
        curs_first = db_read.rawQuery(getString(R.string.query_rabotn), new String[]{sPref.getString("tab", "")});
        if (curs_first.moveToFirst())
            cv.put("Cod_rabotnika",curs_first.getInt(curs_first.getColumnIndex("cod_rab")));
        currentDateTime=(EditText) findViewById(R.id.currentDateTime);
        setInitialDateTime();
        curs = db_read.rawQuery(getString(R.string.query_detal), new String[]{sPref.getString("tab", "")});
        if (curs != null)
        details_class = new Detal_id[curs.getCount()];details=new String[curs.getCount()];
        if (curs.moveToFirst()) {
for (int i=0;i<curs.getCount();i++){
    detal=new Detal_id(curs.getInt(curs.getColumnIndex("cod")),curs.getString(curs.getColumnIndex("detal")));
    details_class[i]=detal;
    details[i]=detal.name;
    curs.moveToNext();

}

        }

     Spinner spinner = (Spinner) findViewById(R.id.spinner_det);
    // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, details);
    // Определяем разметку для использования при выборе элемента
     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Применяем адаптер к элементу spinner
     spinner.setAdapter(adapter);
        // выделяем элемент
        spinner.setSelection(0);

        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
               cv.put("Cod_detali",details_class[position].cod);
                spiner_rasz_add(String.valueOf(details_class[position].cod));




            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
});


        registerForContextMenu(lvData);

    }
    public void onCreateContextMenu(ContextMenu menu, View v,
                                       ContextMenu.ContextMenuInfo menuInfo){
        getMenuInflater().inflate(R.menu.cont_menu, menu);


    }




    public void list_day_arb(String data) {
        double sum=0;
        SimpleCursorAdapter userAdapter;
        SQLiteOpenHelper dbHelper_sql=new SQLiteOpenHelper(this,sPref.getString("tab","none")+".db",null,1) {
            @Override
            public void onCreate(SQLiteDatabase db) { }
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
        };
Cursor curs_read;
SQLiteDatabase db_read_vipusk = dbHelper_sql.getReadableDatabase();

        db_read_vipusk.execSQL("ATTACH DATABASE '"+getString(R.string.rasz_path)+"' AS rasz;");
        curs_read = db_read_vipusk.rawQuery(getString(R.string.query_vipusk), new String[]{curs_first.getString(curs_first.getColumnIndex("cod_rab")),data});
        startManagingCursor(curs_read);

        String[] headers = new String[] {"Detal","Oper","Colichestvo" };
        userAdapter= new SimpleCursorAdapter(this,R.layout.item_list_content_add_vipusk,curs_read,headers,new int[]{R.id.name_detal, R.id.oper, R.id.kol},0);
        lvData.setAdapter(userAdapter);
       if (curs_read.moveToFirst())
            do{
        sum=sum+curs_read.getDouble(curs_read.getColumnIndex("Rasz"))*curs_read.getInt(curs_read.getColumnIndex("Colichestvo"));}
        while (curs_read.moveToNext());
                sum=new BigDecimal(sum).setScale(2, RoundingMode.UP).doubleValue();
        TextView itog=findViewById(R.id.itogo);
        itog.setText(String.valueOf(sum)+"руб.");






        //curs_read.close();




    }



    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

     void spiner_rasz_add(String cod) {
        String[] raszs;
       final Rasz_id[] raszs_class;
        Rasz_id rasz;
        Cursor cc;
        //  if (bool)
        cc = db_read.rawQuery(getString(R.string.query_rasz), new String[]{cod, sPref.getString("tab", "")});
        // else {cc=db.rawQuery(ItemListActivity.getAppResources().getString(R.string.query_rasz_all), new String[]{cod} );};
       raszs_class = new Rasz_id[cc.getCount()];raszs=new String[cc.getCount()];

        if (cc != null) {

            if (cc.moveToFirst()) {
                for (int i = 0; i < cc.getCount(); i++) {
                    rasz = new Rasz_id(cc.getInt(4), cc.getString(0)+" ("+cc.getString(2)+")");
                    raszs_class[i] = rasz;
                    raszs[i] = rasz.name;
                    cc.moveToNext();
                }

            }
        }
        Spinner spinner = (Spinner) findViewById(R.id.spinner_oper);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, raszs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                cv.put("Cod_raszenki",raszs_class[position].cod);
                kol_edit.requestFocus();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }


    @Override
    public boolean onContextItemSelected(final MenuItem item) {
       final AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_edit: {


                final ContentValues cv = new ContentValues();
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                Cursor cursor=null;
                cursor=db_write.query("zarplata",new String[] {"_id", "Colichestvo"},null,null,"_id, Colichestvo","_id="+acmi.id,null);
                cursor.moveToFirst();
                alert.setTitle("Количество: "+cursor.getString(1));
                alert.setMessage("Введите новое значение:");

// Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        cv.put("Colichestvo",input.getText().toString());
                        db_write.update("zarplata", cv, "_id=?", new String[]{String.valueOf(acmi.id)});
                        setInitialDateTime();
                        kol_edit.clearFocus();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();




                break;}

            case R.id.menu_delete: {
                db_write.delete("zarplata", " _id=" + acmi.id, null);
                setInitialDateTime();
                break;
            }
            case R.id.menu_clear: {
                db_write.delete("zarplata", " data='" + String.valueOf(dateFormat.format(dateAndTime.getTime()))+"'", null);
                setInitialDateTime();
                break;
            }

        }return super.onContextItemSelected(item);

    }
    public void showToast(String st) {
        //создаём и отображаем текстовое уведомление
        Toast toast = Toast.makeText(getApplicationContext(),
                st,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    @Override
    public void onClick(View v) {
        db_write=dbHelper.getWritableDatabase();
        switch (v.getId()){
            case R.id.button_Ok:
String st=null;
                View parentLayout = findViewById(android.R.id.content);
               if ((kol_edit.getText().toString().equals("")) | (kol_edit.getText().toString().equals("0"))) cv.put("Colichestvo", st);
                  else cv.put("Colichestvo", kol_edit.getText().toString());

                hideKeyboard(this);

                if (cv.getAsLong("Cod_rabotnika")==null)
                    showToast("Ваш табельный номер отсутствует в базе???");
                else if (cv.getAsLong("Cod_detali")==null)
                   showToast("Отсутствует база данных для деталей???");
                else if (cv.getAsLong("Cod_raszenki")==null)
                   showToast("Отсутствует база данных для расценок???");
                else if (cv.getAsLong("Colichestvo")==null) {
                    showToast("Введите ненулевое значение для количества???");
                    kol_edit.selectAll();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
                else {showToast("Запись добавлена");
                    db_write.insert("zarplata",null,cv);
                kol_edit.setText("");}
                setInitialDateTime();
                break;
            case R.id.button_cancel:
                kol_edit.setText("");
                break;
        }
    }
    private void setInitialDateTime() {

        currentDateTime.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        cv.put("data",dateFormat.format(dateAndTime.getTime()));
        list_day_arb(dateFormat.format(dateAndTime.getTime()));
    }

    public void setDate(View v) {
        new DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };
}
class Detal_id {
    String name;
    int cod;

    Detal_id(int cod, String name) {
        this.cod = cod;
        this.name = name;
    }
}
class Rasz_id {
    String name;
    int cod;

    Rasz_id(int cod, String name) {
        this.cod = cod;
        this.name = name;
    }
}
class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name_base) {
        // конструктор суперкласса
        super(context, name_base, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table zarplata ("
                + "_id integer primary key autoincrement,"
                + "Cod_rabotnika integer,"
                + "Cod_raszenki integer,"
                + "Cod_detali integer,"
                + "data text,"
                + "Colichestvo integer"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

