package jbtechventures.com.rtma;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.util.Random;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import jbtechventures.com.rtma.Fragment.LoginFragment;
import jbtechventures.com.rtma.Fragment.WelcomeFragment;
import jbtechventures.com.rtma.Service.PostWorker;
import jbtechventures.com.rtma.Session.SessionManager;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener,
    WelcomeFragment.OnFragmentInteractionListener{

    Context context;
    private FrameLayout GoToLogin;
    private FrameLayout GoToRegister;
    private FrameLayout ResetApp;
    Animation slideUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        init();
        setListener();
        startWorkManageer();
    }

    private void init(){
        GoToLogin = findViewById(R.id.go_to_login);
        GoToRegister = findViewById(R.id.go_to_register);
        ResetApp = findViewById(R.id.refresh_app_data);
        animateLandingButtons(GoToLogin);
        animateLandingButtons(GoToRegister);
        animateLandingButtons(ResetApp);

        /*Initialize the fragment*/
        WelcomeFragment welcomeFragment = new WelcomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, welcomeFragment, "Welcome_Fragment").commit();
    }

    private void setListener(){
        GoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFragment loginFragment = new LoginFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, loginFragment, "Login_Fragment").commit();
            }
        });
        /*GoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterFragment registerFragment = new RegisterFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, registerFragment, "Register").commit();
            }
        });*/
        ResetApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String PREFS_NAME = context.getResources().getString(R.string.pref_name);
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                sharedPreferences.edit().putString("API_URL", "").commit();
                Intent intent = new Intent(context, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void animateLandingButtons(FrameLayout button) {
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_anim);//animations[new Random().nextInt(2)]
        slideUp.setDuration((new Random().nextInt(3) + 1) * 500);
        button.startAnimation(slideUp);
    }

    @Override
    public void onApiLoginIntiated(Boolean value) {
        if(value) {
            GoToLogin.setVisibility(View.GONE);
            GoToRegister.setVisibility(View.GONE);
            ResetApp.setVisibility(View.GONE);
        }else{
            GoToLogin.setVisibility(View.VISIBLE);
            GoToRegister.setVisibility(View.VISIBLE);
            ResetApp.setVisibility(View.VISIBLE);
        }
    }

    private void startWorkManageer(){
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest postingWork = new OneTimeWorkRequest.Builder(PostWorker.class)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance().enqueue(postingWork);
    }
}
