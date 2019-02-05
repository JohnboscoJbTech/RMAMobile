package jbtechventures.com.rtma.Utility;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import jbtechventures.com.rtma.Model.Party;
import jbtechventures.com.rtma.Repository.PartyRepository;


/**
 * Created by JOHNBOSCO on 3/9/2018.
 */

public class ApiGetConn extends AsyncTask<Void, Void, String> {

    private Context appContext;
    private String apiurl;
    private String tag;
    public static final String TAG_PARTY = "PARTY_TAG";
    public static final String TAG_MODULE = "MODULE_TAG";

    public ApiGetConn(Context context, String _url, String _tag) {
        appContext = context;
        apiurl = _url;
        tag = _tag;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected String doInBackground(Void... voids) {
        String text = "";
        BufferedReader reader;
        // Send data
        try
        {
            URL url = new URL(apiurl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
            text = stringBuilder.toString();
            conn.disconnect();
            return text;
        }
        catch(Exception e) {
            return "Error";
        }
    }

    protected void onPostExecute(String result) {
        if(!(result.equals(null) || result.isEmpty()) && !result.equals("Error")) {
            JSONArray jsonArray = null;
            switch (tag) {
                case TAG_PARTY:
                    try {
                        jsonArray = new JSONArray(result);
                        JSONObject jreader;
                        PartyRepository partyRepository = new PartyRepository(appContext);
                        partyRepository.deleteParties();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Party party = new Party();
                            jreader = jsonArray.getJSONObject(i);
                            party.Id = jreader.getInt("id");
                            party.Name = jreader.getString("fullName");
                            party.Code = jreader.getString("shortName");
                            partyRepository.addParty(party);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case TAG_MODULE:
                    try {
                        jsonArray = new JSONArray(result);
                        JSONObject jreader;
                        /*ModuleRepository moduleRepository = new ModuleRepository(appContext);
                        moduleRepository.deleteModules();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Module module = new Module();
                            jreader = jsonArray.getJSONObject(i);
                            module.Id = jreader.getInt("Id");
                            module.Name = jreader.getString("Name");
                            moduleRepository.addModule(module);
                        }*/
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                /*case TAG_EXPIRY_DATE:
                    try {
                        jsonArray = new JSONArray(result);
                        JSONObject jreader;
                        LotNumberRepository lotNumberRepository = new LotNumberRepository(appContext);
                        ArrayList<LotNumber> lotNumbers = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            LotNumber lotNumber = new LotNumber();
                            jreader = jsonArray.getJSONObject(i);
                            lotNumber.ExpiryDate = jreader.getString("ExpiryDate");
                            lotNumbers.add(lotNumber);
                        }
                        lotNumberRepository.addExpiryDate(lotNumbers);
                        Toast.makeText(appContext, "Application Refresh Completed", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;*/
            }
        }
        super.onPostExecute(result);
    }
}
