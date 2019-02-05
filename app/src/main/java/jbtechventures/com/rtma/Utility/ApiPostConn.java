package jbtechventures.com.rtma.Utility;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import jbtechventures.com.rtma.Model.Result;
import jbtechventures.com.rtma.Model.Vote;
import jbtechventures.com.rtma.Repository.ComplaintRepository;
import jbtechventures.com.rtma.Repository.PartyRepository;
import jbtechventures.com.rtma.Repository.PersonRepository;
import jbtechventures.com.rtma.Repository.ResultRepository;
import jbtechventures.com.rtma.Repository.VotesRepository;

/**
 * Created by JOHNBOSCO on 3/9/2018.
 */

public class ApiPostConn extends AsyncTask<String, Void, String> {

    private Context appContext;
    private String apiurl;
    private String tag;
    public static final String RESULT_POST_TAG = "RESULT_POST";
    public static final String COMPLAINT_POST_TAG = "COMPLAINT_POST";
    public int postId;


    public ApiPostConn(Context context, String _url, String... _tag) {
        appContext = context;
        apiurl = _url;
        tag = _tag[0];
        postId = Integer.parseInt(_tag[1]);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected String doInBackground(String... param) {
        // Create data variable for sent values to server
        String data = param[0];

        String text = "";
        BufferedReader reader;
        // Send data
        try
        {
            // Defined URL  where to send data
            URL url = new URL(apiurl);
            // Send POST data request
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            /*if(tag == RESULT_POST_TAG) {
                //--
                File bin1 = new File("/storage/emulated/0/IMG_20181105_085755602.jpg");

            }*/
            //conn.setConnectTimeout(90000);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            // Read Server Response
            while((line = reader.readLine()) != null)
            {   // Append server response in string
                sb.append(line);
            }
            text = sb.toString();
            conn.disconnect();
            return text;
        }
        catch(Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    protected void onPostExecute(String result) {
        if(!(result.equals(null) || result.isEmpty()) && !(result.equals("Error"))) {
            JSONObject jObject = null;
            switch (tag){
                case RESULT_POST_TAG:
                    ResultRepository resultRepository = new ResultRepository(appContext);
                    if(result.equals("Successfully posted"))
                        resultRepository.updateSynced(postId);
                    else{
                        //TODO log post message
                        resultRepository.updateSyncError(postId, result);
                    }
                    break;
                case COMPLAINT_POST_TAG:
                    ComplaintRepository complaintRepository = new ComplaintRepository(appContext);
                    if(result.equals("Success"))
                        complaintRepository.updateSynced(postId);
                    else{
                        //TODO log post message
                    }
                    break;
            }
        }
        super.onPostExecute(result);
    }

    private String getData() {
        String returnValue = "";
        switch (tag) {
            case RESULT_POST_TAG:
                ResultRepository resultRepository = new ResultRepository(appContext);
                ArrayList<Result> results = resultRepository.getNonSyncedResults();
                if(results.size() > 0) {
                    //build sync model here
                    returnValue = StringifyResult(results);
                }
                break;
            case COMPLAINT_POST_TAG:
                /*PersonRepository personRepository = new PersonRepository(appContext);
                Person person = personRepository.getPerson();
                if(person != null) {
                    returnValue = StringifyPerson(person);;
                }*/
                break;
        }
        return returnValue;
    }

    /*private String StringifyPerson(Person person) {
        JSONObject json = new JSONObject();
        try {
            json.put("Email", person.Email);
            json.put("Password", "");
            json.put("Code", person.Code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }*/

    private String StringifyResult(ArrayList<Result> results){
        JSONArray array=new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aaa");

        JSONObject json = new JSONObject();
        for (int i = 0; i < results.size(); i++){
            Result result = results.get(i);
            //JSONObject json = new JSONObject();

            try {
                JSONArray innerArray=new JSONArray();
                for (Vote v:new VotesRepository(appContext).getModuleVotes(result.ElectionId, result.Id)) {
                    JSONObject innerJson = new JSONObject();
                    innerJson.put("Votes", v.Count);
                    //innerJson.put("Module", v.ElectionId);
                    innerJson.put("Party", new PartyRepository(appContext).getParty(v.Party).Code);
                    innerArray.put(innerJson);
                }
                //json.put("Id", result.Id);
                json.put("ElectionMetaDataId", result.ElectionId);
                json.put("UserProfileId", new PersonRepository(appContext).getPersonCurrentlyLoggedIn().UserId);
                json.put("AccreditedVoters", result.AccredVotes);
                json.put("TotalVotesCast", result.CastVoted);
                json.put("InvalidVotes", result.InvalidVoted);
                json.put("PollingUnitCode", result.Unit);
                json.put("RegisteredVoters", result.RegVotes);
                json.put("PartyVotes", innerArray);
                JSONArray pictureInnerArray=new JSONArray();
                JSONObject pictureInnerJson = new JSONObject();
                pictureInnerJson.put("PictureEvidence", result.ProofImagePath != null ? Base64.encodeToString(ApplicationUtil.getBytesFromImageFile(result.ProofImagePath), Base64.DEFAULT) : "");
                pictureInnerArray.put(pictureInnerJson);
                json.put("PictureEvidence", pictureInnerArray);

                array.put(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json.toString();
    }
}
