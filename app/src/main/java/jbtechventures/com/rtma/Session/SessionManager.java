package jbtechventures.com.rtma.Session;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import jbtechventures.com.rtma.MainActivity;


/**
 * This activity is the activity that manages session activity fo the apliccation
 * all classes that extendens this activity will have session manged by this activity
 * All sensitive activities should extend this activity
 * */

public class SessionManager extends AppCompatActivity {

    public static final long DISCONNECT_TIMEOUT = 15 * 60 * 1000;

    private static Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            // Perform any required operation on disconnect
            logUserOut();
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }

    private void logUserOut() {
        //user can still be redirected to the login page from here
        //UserRepository userRepository = new UserRepository(SessionManager.this);
        //userRepository.updateUserSession(1);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
