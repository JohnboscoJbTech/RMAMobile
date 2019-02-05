package jbtechventures.com.rtma.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import jbtechventures.com.rtma.R;
import jbtechventures.com.rtma.Utility.ApiGetConn;

public class GetService extends IntentService {

    private Context context;
    private static final String ACTION_GET_PARTIES = "ACTION_GET_PARTIES";
    //private static final String ACTION_BAZ = "jbtechventures.com.inec_rtma.Service.action.BAZ";

    //private static final String EXTRA_PARAM1 = "jbtechventures.com.inec_rtma.Service.extra.PARAM1";
    //private static final String EXTRA_PARAM2 = "jbtechventures.com.inec_rtma.Service.extra.PARAM2";

    public GetService() {
        super("GetService");
        context = this;
    }

    /**
     * Starts this service to perform action Get Parties. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void startActionGetParty(Context context) {
        Intent intent = new Intent(context, GetService.class);
        intent.setAction(ACTION_GET_PARTIES);
        context.startService(intent);
    }

    public static void startActionGetLgas(Context context) {
        Intent intent = new Intent(context, GetService.class);
        /*intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);*/
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_PARTIES.equals(action)) {
                handleActionGetParties();
            } /*else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }*/
        }
    }

    private void handleActionGetParties() {
        String PREFS_NAME = context.getResources().getString(R.string.pref_name);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String apiUrl = sharedPreferences.getString("API_URL", "");
        //call get method here
        ApiGetConn apiConn = new ApiGetConn(context, apiUrl + context.getString(R.string.api_get_parties), ApiGetConn.TAG_PARTY);
        apiConn.execute();
    }

}
