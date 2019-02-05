package jbtechventures.com.rtma;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplashActivity extends AppCompatActivity {


    Context context;
    private String PREFS_NAME;
    private String API_URL = "API_URL";
    SharedPreferences sharedPreferences;
    String apiUrl;
    EditText app_url;
    Button configure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = this;

        PREFS_NAME = this.getResources().getString(R.string.pref_name);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        apiUrl = sharedPreferences.getString(API_URL, "");
        if(apiUrl != ""){
            app_url = findViewById(R.id.app_url);
            configure = findViewById(R.id.configure);
            app_url.setVisibility(View.GONE);
            configure.setVisibility(View.GONE);
        }
        final int waitTime = new Random().nextInt(3) * 1000;
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(3000 + waitTime);
                    startRightActivity();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Method check for the activity to start
     * */
    public void startRightActivity() {
        if(apiUrl != ""){
            //go to main activity
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else{
            init();
        }
    }

    public void init(){
        //Initialize the config view here

        app_url = findViewById(R.id.app_url);
        configure = findViewById(R.id.configure);
        app_url.setVisibility(View.VISIBLE);
        configure.setVisibility(View.VISIBLE);

        configure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()) {
                    sharedPreferences.edit().putString(API_URL, app_url.getText().toString().trim()).commit();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else{
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) view.findViewById(R.id.toast_layout_root));

                    TextView text = (TextView) layout.findViewById(R.id.toast_text);
                    text.setText("Enter a valid URL");

                    Toast toast = new Toast(context);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }
        });
    }

    private boolean validate(){
        String url = app_url.getText().toString().trim();
        /*return url.matches("^(http:\\/\\/|https:\\/\\/)?(www.)([a-zA-Z0-9]+)?.[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?(:[0-9])?$");*/
        //validate for urls
        //Pattern p = Pattern.compile("(http://|https://)?(www.)?([a-zA-Z0-9]+)(.[a-z]{3})?(:[0-9]{4})?");
        /*validate for ip address*/
        //Pattern p = Pattern.compile("(http://|https://)?([0-9]{3})(.[0-9]{3})(.[0-9]{3})(.[0-9]{3})(:[0-9]{4})?");
        /*Matcher m;
        m = p.matcher(url);
        boolean ft = m.matches();
        return m.matches();*/
        return true;
    }
}
