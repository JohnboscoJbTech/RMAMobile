package jbtechventures.com.rtma.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import jbtechventures.com.rtma.Service.PostService;
import jbtechventures.com.rtma.Utility.Connectivity;

public class InternetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Connectivity.isConnected(context)) {
            syncResults(context);
            syncComplaints(context);
        }
    }

    /**
     * Method handles syncing for Results to the server
     * calls a bg service that does the sync
     * */
    private void syncResults(Context context){
        PostService.startActionPostResult(context);
    }


    /**
     * Method handles syncing for Complaint to the server
     * calls a bg service that does the sync
     * */
    private void syncComplaints(Context context){
        PostService.startActionPostComplaint(context);
    }
}
