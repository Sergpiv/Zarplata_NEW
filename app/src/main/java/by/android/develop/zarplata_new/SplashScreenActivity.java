package by.android.develop.zarplata_new;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.widget.TextView;

public class SplashScreenActivity extends Activity {

    // Время в милесекундах, в течение которого будет отображаться Splash Screen
    private final int SPLASH_DISPLAY_LENGTH = 1500;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        TextView textView=findViewById(R.id.textView_version);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            textView.setText("v. "+ pInfo.versionName+"         base v. "+DatabaseHelper.DB_VERSION);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // По истечении времени, запускаем главный активити, а Splash Screen закрываем
                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                SplashScreenActivity.this.startActivity(mainIntent);
                SplashScreenActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}