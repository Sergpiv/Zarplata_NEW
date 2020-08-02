package by.android.develop.zarplata_new;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import android.os.Environment;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static android.app.AlertDialog.*;


public class MainActivity extends AppCompatActivity {
    String base = "base";
    SharedPreferences sPref;
   final static int REQUEST_EXTERNAL_STORAGE = 1;
    public static String[] month_array_const = {"ЯНВАРЬ", "ФЕВРАЛЬ", "МАРТ", "АПРЕЛЬ", "МАЙ", "ИЮНЬ", "ИЮЛЬ", "АВГУСТ", "СЕНТЯБРЬ", "ОКТЯБРЬ", "НОЯБРЬ", "ДЕКАБРЬ"};
    String[] year_array, month_array;
    static String month,year;
     public static SQLiteDatabase db_read_vipusk;
    Spinner spinner_month,spinner_year;
    ExpandableListView elvMain;
    Cursor curs_first;
    TextView itog;
    View footer;
    View v;
    SQLiteDatabase db_read;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        itog=findViewById(R.id.footerText);
        elvMain = (ExpandableListView) findViewById(R.id.elvMain);
        footer = createFooter("");
        verifyStoragePermissions(this);
         sPref= getSharedPreferences(getString(R.string.APP_PREFERENCES_NAME), MODE_PRIVATE);
        spinner_month = (Spinner) findViewById(R.id.spinner_month);
        spinner_year = (Spinner) findViewById(R.id.spinner_year);
            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   SharedPreferences sPref = getSharedPreferences(getString(R.string.APP_PREFERENCES_NAME), MODE_PRIVATE);

                    curs_first = db_read.rawQuery(getString(R.string.query_rabotn), new String[]{sPref.getString("tab", "")});
                    if (curs_first.moveToFirst()){
                        Intent intent = new Intent(view.getContext(), Add_vipusk.class);
                         startActivity(intent);
                        }
                    else{ SharedPreferences.Editor editor = sPref.edit();
                        editor.putBoolean( "bool", false );
                        editor.apply();
                   FireMissilesDialogFragment fireMissilesDialogFragment=new FireMissilesDialogFragment();
                       fireMissilesDialogFragment.show(getSupportFragmentManager(), "NoticeDialogFragment");}
                }
            });
        }

    private View createFooter(String text) {
        View v = getLayoutInflater().inflate(R.layout.footer, null);
        ((TextView)v.findViewById(R.id.footerText)).setText(text);
        return v;
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        db_read = databaseHelper.getReadableDatabase();
        if (checkDataBase("/data/data/by.android.develop.zarplata_new/databases/" + sPref.getString("tab", "none") + ".db")) {

            SQLiteOpenHelper dbHelper_sql = new SQLiteOpenHelper(this, sPref.getString("tab", "none") + ".db", null, 1) {
                @Override
                public void onCreate(SQLiteDatabase db) {
                }
                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                }
            };
            db_read_vipusk = dbHelper_sql.getReadableDatabase();
            db_read_vipusk.execSQL("ATTACH DATABASE '" + getString(R.string.rasz_path) + "' AS rasz;");
            year_get(db_read_vipusk);
        }

else {
            spinner_month.setAdapter(null);
            spinner_year.setAdapter(null);
            elvMain.setAdapter((ExpandableListAdapter) null);
            elvMain.removeFooterView(v);
        }
    }
        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);

            return true;
        }

    public void showToast(String st) {
        //создаём и отображаем текстовое уведомление
        Toast toast = Toast.makeText(getApplicationContext(),
                st,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public boolean onOptionsItemSelected (@NonNull MenuItem item){
            switch (item.getItemId()) {
                //------------------------------------------------------------
                case R.id.menu_exit:
                    finish();
                    break;
                //------------------------------------------------------------
                case R.id.menu_about:
                    final ImageView imageView=findViewById(R.id.imageView_about);


                    Builder adb = new Builder(this);
                    View my_custom_view = (LinearLayout) getLayoutInflater().inflate(R.layout.about, null); //находим разметку
                    adb.setView(my_custom_view); //ставим ее в окно
                   final AlertDialog ad = adb.create();
                    ad.show();
                   // View.OnClickListener oclBtn = new View.OnClickListener() {
                     //   @Override
                     //   public void onClick(View v) {
                     //    ad.hide();
                     //   }

                   // };imageView.setOnClickListener(oclBtn);
                    break;
                //------------------------------------------------------------
                case R.id.menu_rasz:
                    Intent intent2 = new Intent(this, ItemListActivity.class);
                    startActivity(intent2);
                    break;
                //------------------------------------------------------------
                case R.id.menu_settings:
                    Intent intent1 = new Intent(this, PreferenseActivity.class);
                    startActivity(intent1);
                    break;
                //------------------------------------------------------------
                case R.id.in_copy:
                    File[] list = ContextCompat.getExternalFilesDirs(this, null);

                    File src = new File("/data/data/by.android.develop.zarplata_new/databases/" + sPref.getString("tab", "none") + ".db");

                    String show="";
                    if (sPref.getBoolean("internal", false)) {
                        File screenShotDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        File target = new File(screenShotDirPath + "/" + base + "/" + sPref.getString("tab", "none") + ".db");
                        try {
                            getdownloadStorageDir(screenShotDirPath + "/" + base);
                            Files.copy(src.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            show=show+"Копия на внутренней памяти успешно создана!!!";

                        } catch (IOException e) {
                            e.printStackTrace();
                            show=show+"Ошибка при создании копии на внутренней памяти!!!";
                        }
                    }
                    if (sPref.getBoolean("external", false) && (list.length>1)) {
                        File screenShotDirPath = list[1];
                        File target = new File(screenShotDirPath + "/" + base + "/" + sPref.getString("tab", "none") + ".db");
                        try {
                            getdownloadStorageDir(list[1].getAbsolutePath() + "/" + base);
                            Files.copy(src.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            if (show!="") show=show+"\n";
                            show=show+"Копия на карте памяти успешно создана!!!";

                        } catch (IOException e) {
                            e.printStackTrace();
                            if (show!="") show=show+"\n";
                            show=show+"Ошибка при создании копии на карте памяти!!!";
                        }
                    }
                    showToast(show);
                    break;
                 //------------------------------------------------------------
                case R.id.out_copy:
                    DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                    SQLiteDatabase db_read = databaseHelper.getReadableDatabase();
                    curs_first = db_read.rawQuery(getString(R.string.query_rabotn), new String[]{sPref.getString("tab", "none")});
                    if (!curs_first.moveToFirst()){
                        FireMissilesDialogFragment fireMissilesDialogFragment=new FireMissilesDialogFragment();
                        fireMissilesDialogFragment.show(getSupportFragmentManager(), "NoticeDialogFragment");
                    }
else
                    showDialog(1);
                    break;
                //------------------------------------------------------------
                case R.id.dat:
                    Intent intent_month = new Intent(this, ReportActivity_month.class);
                    startActivity(intent_month);
                    break;

                case R.id.det:
                    Intent intent_det = new Intent(this, ReportActivity_det.class);
                    startActivity(intent_det);
                    break;
                //------------------------------------------------------------
                case R.id.rep_year:
                    Intent intent_year = new Intent(this, ReportActivity_year.class);
                    startActivity(intent_year);
                    break;
            }
            return super.onOptionsItemSelected(item);
        }

    protected Dialog onCreateDialog(int n) {
        String vibor[] = { "внутренняя память", "карта памяти", "указанное место..."};
        Builder adb = new Builder(this);
        adb.setTitle( Html.fromHtml("<font color='#0000FF'>ВОССТАНОВИТЬ БАЗУ</font>"));
        adb.setSingleChoiceItems(vibor, -1, myClickListener);
        adb.setPositiveButton(R.string.ok, myClickListener);
        adb.setNegativeButton(R.string.cancel, myClickListener);
        showToast("               ВНИМАНИЕ!!!\n"+"Ваша существующая база будет\n            ПЕРЕЗАПИСАНА!!!");
        return adb.create();
    }

    // обработчик нажатия на пункт списка диалога или кнопку
    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onClick(DialogInterface dialog, int which) {
            File[] list = ContextCompat.getExternalFilesDirs(MainActivity.this, null);
            File target = new File("/data/data/by.android.develop.zarplata_new/databases/" + sPref.getString("tab", "none") + ".db");
            File screenShotDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File src;
            ListView lv = ((AlertDialog) dialog).getListView();
           if (which == Dialog.BUTTON_POSITIVE)
                switch (lv.getCheckedItemPosition()) {
                    case -1:
                        showToast("Ничего не выбрано!!!");
                        break;
                    case 0:
                        src = new File(screenShotDirPath + "/" + base + "/" + sPref.getString("tab", "none") + ".db");
                        try {
                            Files.copy(src.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            showToast("База успешно восстановлена!!!");
                            onResume();
                        } catch (IOException e) {
                            e.printStackTrace();
                            showToast("База не найдена!!!");
                        }
                        break;
                    case 1:
                        if (list.length > 1) {
                            screenShotDirPath = list[1];
                            src = new File(screenShotDirPath + "/" + base + "/" + sPref.getString("tab", "none") + ".db");
                            try {
                                Files.copy(src.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                showToast("База успешно восстановлена!!!");
                                onResume();
                            } catch (IOException e) {
                                e.printStackTrace();
                                showToast("База не найдена!!!");
                            }}
                            break;
                            case 2:
                                Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
                                intent.putExtra("CONTENT_TYPE", "*/*");
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                startActivityForResult(intent,1);
                                break;
                }
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String FilePath = data.getData().getPath();
                    if ((FilePath != null) && (FilePath.endsWith(sPref.getString("tab", "none") + ".db"))) {
                        File src = new File(FilePath);
                        File target = new File("/data/data/by.android.develop.zarplata_new/databases/" + sPref.getString("tab", "none") + ".db");
                        try {
                            Files.copy(src.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            showToast("База успешно восстановлена!!!");
                            onResume();
                        } catch (IOException e) {
                            e.printStackTrace();
                            showToast("Ошибка при восстановлении базы!!!");
                        }
                    }
                    else showToast("Выбранный файл не является"+"\n                     "+sPref.getString("tab","none") +".db\n       Выберите нужный файл");
                }
        }
    }

    public static void verifyStoragePermissions(MainActivity activity) {
        // Storage Permissions

        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public File getdownloadStorageDir(String dirName)
    {
        // Получение каталога для публичного каталога картинок пользователя.
        File file = new File(dirName);
        if (!file.mkdirs())
        {
            file.mkdir();
        }
        return file;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("Разрешение получено");

                } else {
                    showToast("Разрешение не получено");
                }
                return;
        }
    }

    public static boolean checkDataBase(String myPath) {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //файл базы данных отсутствует
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }
    private void year_get(final SQLiteDatabase db){
        final Cursor cursor_Year;
        ArrayAdapter<String> adapter_year;
        cursor_Year = db.rawQuery("select strftime('" + "%Y'" + ",data) AS year from zarplata group by year order by year", null);
        year_array = new String[cursor_Year.getCount()];
        if (cursor_Year.moveToFirst()) {
            for (int i = 0; i < cursor_Year.getCount(); i++) {
                year_array[i] = cursor_Year.getString(0);
                cursor_Year.moveToNext();
            }
            adapter_year = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, year_array);
            adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_year.setAdapter(adapter_year);
            spinner_year.setSelection(-1+cursor_Year.getCount());
        }
        else {spinner_year.setAdapter(null);spinner_month.setAdapter(null);}
        spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
          //       ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
                ((TextView) parent.getChildAt(0)).setTextSize(20);
                cursor_Year.moveToPosition(position);
                year=cursor_Year.getString(0);
                month_get(String.valueOf(year),db);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void month_get(final String year, final SQLiteDatabase db) {
        final Cursor cursor_Month;
        ArrayAdapter<String> adapter_Month;
        String sql = "select strftime('%m',data) AS month, strftime('%Y',data) AS year from zarplata group by month,year having year=? order by month";
        cursor_Month = db.rawQuery(sql, new String[]{year});
        month_array = new String[cursor_Month.getCount()];
        if (cursor_Month.moveToFirst()) {
            for (int i = 0; i < cursor_Month.getCount(); i++) {
                String temp = cursor_Month.getString(0);
                if (temp.charAt(0) == '0') {
                    temp = temp.replace('0', ' ');
                    temp = temp.trim();
                }
                month_array[i] = month_array_const[-1 + Integer.parseInt(temp)];
                cursor_Month.moveToNext();
            }
            adapter_Month = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, month_array);
            adapter_Month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_month.setAdapter(adapter_Month);
            spinner_month.setSelection(-1 + cursor_Month.getCount());
            spinner_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    // ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
                    ((TextView) parent.getChildAt(0)).setTextSize(20);
                    cursor_Month.moveToPosition(position);
                    month = cursor_Month.getString(0);
                    Cursor cursor = db.rawQuery("SELECT zarplata.Cod_detali as [_id], rasz.Detal.Detal as [detal], CASE WHEN LENGTH(CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text))-INSTR(CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text),'.')=1 THEN CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text) || '0' ELSE CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text) END AS [summa]\n" +
                            "FROM zarplata INNER JOIN rasz.Detal ON rasz.Detal.Cod = zarplata.Cod_detali \n" +
                            "LEFT JOIN rasz.Raszenki ON rasz.Raszenki.Cod = zarplata.Cod_raszenki\n" +
                            "GROUP BY zarplata.Cod_detali, detal, strftime('%m',zarplata.data), strftime('%Y',zarplata.data)\n" +
                            "HAVING ((strftime('%m',zarplata.data)=?) AND (strftime('%Y',zarplata.data)=?))", new String[]{month, year});
                    startManagingCursor(cursor);
                    elvMain.removeFooterView(v);
                    // сопоставление данных и View для групп
                    String[] groupFrom = {"detal", "summa"};
                    int[] groupTo = {R.id.textView_expandaple_detal, R.id.textView_expandable_sum};
                    // сопоставление данных и View для элементов
                    String[] childFrom = {"kol", "oper", "poasn", "summa"};
                    int[] childTo = {R.id.textView_exp_kol, R.id.textView_exp_oper, R.id.textView_exp_poasn, R.id.textView_exp_sum};
                    // создаем адаптер и настраиваем список
                    SimpleCursorTreeAdapter sctAdapter = new MyAdapter(getApplication(), cursor,
                            R.layout.detal_expandable_view, groupFrom,
                            groupTo, R.layout.oper_expandable_view, childFrom,
                            childTo);
                    v = getLayoutInflater().inflate(R.layout.footer, null);
                    Cursor itogo = db_read_vipusk.rawQuery("SELECT CASE WHEN LENGTH(CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text))-INSTR(CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text),'.')=1 THEN CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text) || '0' ELSE CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text) END AS [summa]\n" +
                            "                FROM zarplata INNER JOIN rasz.Raszenki ON rasz.Raszenki.Cod = zarplata.Cod_raszenki\n" +
                            "                GROUP BY strftime('%m',zarplata.data), strftime('%Y',zarplata.data)                \n" +
                            "                HAVING ((strftime('%m',zarplata.data)=?) AND (strftime('%Y',zarplata.data)=?))", new String[]{month, year});

                    if (itogo.moveToFirst())
                        ((TextView) v.findViewById(R.id.footerText)).setText(itogo.getString(0));
                    elvMain.addFooterView(v);
                    elvMain.setAdapter(sctAdapter);
                }


                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }
    }
    public static class FireMissilesDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
         Builder builder = new Builder(getActivity());
            builder.setTitle( Html.fromHtml("<font color='#DC143C'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ВНИМАНИЕ</font>"));
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage("Введите корректный табельный номер!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent1 = new Intent(getContext(), PreferenseActivity.class);
                            startActivity(intent1);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public static Cursor get_oper_Cursor(String anInt) {
        String str;
        str="SELECT zarplata.Cod_detali AS [_id], Sum(zarplata.Colichestvo) AS [kol], rasz.Raszenki.nomer_operazii As [oper], rasz.Raszenki.poasnenie as [poasn], CASE WHEN LENGTH(CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text))-INSTR(CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text),'.')=1 THEN CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text) || '0' ELSE CAST(Round(Sum(zarplata.Colichestvo*rasz.Raszenki.raszenka),2) as text) END AS [summa]\n" +
                "FROM zarplata INNER JOIN rasz.Raszenki ON rasz.Raszenki.Cod = zarplata.Cod_raszenki\n" +
                "GROUP BY Cod_detali, oper, strftime('%m',zarplata.data), strftime('%Y',zarplata.data)\n" +
                "HAVING ((Cod_detali=?) AND(strftime('%m',zarplata.data)=?) AND (strftime('%Y',zarplata.data)=?))\n" +
                "ORDER BY oper";

return db_read_vipusk.rawQuery(str,new String[]{anInt,month,year});




    }
    public void close_db(SQLiteDatabase db) {
        db.close();
    }
}


class MyAdapter extends SimpleCursorTreeAdapter {

    public MyAdapter(Context context, Cursor cursor, int groupLayout,
                     String[] groupFrom, int[] groupTo, int childLayout,
                     String[] childFrom, int[] childTo) {
        super( context, cursor, groupLayout, groupFrom, groupTo,
                childLayout, childFrom, childTo);
    }


    @Override
    public Cursor getChildrenCursor(Cursor groupCursor) {
        int idColumn = groupCursor.getColumnIndex("_id");
        return MainActivity.get_oper_Cursor(groupCursor.getString(idColumn));
    }


}
