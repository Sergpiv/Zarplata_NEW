package by.android.develop.zarplata_new;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.Html;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.io.File;

import static by.android.develop.zarplata_new.ItemListActivity.sPref;


public class PreferenseActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String Tab_summary = "введите табельный номер";
    Boolean bool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);


    }

    @Override
    public void onResume() {
        super.onResume();
        bool = false;
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
            Preference preference = getPreferenceScreen().getPreference(i);
            updatePreference(preference);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreference(findPreference(key));
    }

    private void updatePreference(Preference preference) {


        if (preference instanceof EditTextPreference) {
            switch (preference.getKey()) {
                case "tab":
                    EditTextPreference editTextPreference = (EditTextPreference) preference;
                    DatabaseHelper databaseHelper = new DatabaseHelper(this);
                    SQLiteDatabase db_read = databaseHelper.getReadableDatabase();
                    if (editTextPreference.getText() == null) {editTextPreference.setText("");bool=false;}
                    Cursor curs_first = db_read.rawQuery(getString(R.string.query_rabotn), new String[]{editTextPreference.getText()});
                    if (curs_first.moveToFirst()) {
                        editTextPreference.setSummary(editTextPreference.getText());
                    } else {
                        editTextPreference.setSummary(Tab_summary);

                        if (bool) {


                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle(Html.fromHtml("<font color='#DC143C'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ВНИМАНИЕ</font>"));
                            builder.setIcon(android.R.drawable.ic_dialog_alert);
                            builder.setMessage("Введённый табельный номер отсутствует в базе данных")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    })
                            ;
                            AlertDialog alert = builder.create();
                            try {
                                alert.show();
                            } catch (WindowManager.BadTokenException e) {
                            }
                        }
                        bool=true;
                    }
                    break;
            }
        }
            if (preference instanceof CheckBoxPreference) {
                String base = "/base";
                File[] list = ContextCompat.getExternalFilesDirs(this, null);
                switch (preference.getKey()) {

                    case "internal":

                        CheckBoxPreference checkBoxPreference_internal = (CheckBoxPreference) preference;
                        checkBoxPreference_internal.setEnabled(false);
                        if (list.length > 0) {
                            checkBoxPreference_internal.setEnabled(true);
                            checkBoxPreference_internal.setSummary(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + base);
                        }

                        break;
                    case "external":
                        CheckBoxPreference checkBoxPreference_external = (CheckBoxPreference) preference;
                        checkBoxPreference_external.setEnabled(false);
                        if (list.length > 1) {
                            checkBoxPreference_external.setEnabled(true);
                            checkBoxPreference_external.setSummary(list[1].getAbsolutePath() + base);
                        }

                        break;
                }

            }



    }
}


