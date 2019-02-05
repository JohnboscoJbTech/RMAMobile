package jbtechventures.com.rtma.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jbtechventures.com.rtma.Model.Complaint;
import jbtechventures.com.rtma.Model.Result;
import jbtechventures.com.rtma.Model.Vote;
import jbtechventures.com.rtma.R;
import jbtechventures.com.rtma.Repository.ComplaintRepository;
import jbtechventures.com.rtma.Repository.PartyRepository;
import jbtechventures.com.rtma.Repository.PersonRepository;
import jbtechventures.com.rtma.Repository.ResultRepository;
import jbtechventures.com.rtma.Repository.VotesRepository;
import jbtechventures.com.rtma.Utility.ApiPostConn;
import jbtechventures.com.rtma.Utility.ApplicationUtil;

public class PostService extends IntentService {

    Context context;
    private static final String ACTION_POST_RESULT = "ACTION_POST_RESULT";
    private static final String ACTION_POST_COMPALINT = "ACTION_POST_COMPALINT";
    ResultRepository resultRepository;
    VotesRepository votesRepository;
    ComplaintRepository complaintRepository;

    public PostService() {
        super("PostService");
        context = this;
    }

    public static void startActionPostResult(Context context) {
        Intent intent = new Intent(context, PostService.class);
        intent.setAction(ACTION_POST_RESULT);
        context.startService(intent);
    }

    public static void startActionPostComplaint(Context context) {
        Intent intent = new Intent(context, PostService.class);
        intent.setAction(ACTION_POST_COMPALINT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_POST_RESULT.equals(action)) {
                handleActionPostResult();
            } else if (ACTION_POST_COMPALINT.equals(action)) {
                handleActionPostComplaint();
            }
        }
    }

    /**
     * Handle action result in the provided background thread with the provided
     * parameters.
     */
    private void handleActionPostResult() {
        //query the result and send one after the other to the server;
              resultRepository = new ResultRepository(context);
        votesRepository = new VotesRepository(context);
        ArrayList<Result> results = resultRepository.getNonSyncedResults();
        if(results.size() > 0) {
            //build sync model here
            //returnValue = (results);
            for (Result result: results) {
                String PREFS_NAME = context.getResources().getString(R.string.pref_name);
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                String apiUrl = sharedPreferences.getString("API_URL", "");
                ApiPostConn apiConn = new ApiPostConn(context, apiUrl + context.getString(R.string.api_post_result), ApiPostConn.RESULT_POST_TAG, String.valueOf(result.Id));
                apiConn.execute(StringifyResult(result));
            }
        }
    }

    /**
     * Handle action Post Complaint in the provided background thread with the provided
     * parameters.
     */
    private void handleActionPostComplaint() {
        String PREFS_NAME = context.getResources().getString(R.string.pref_name);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String apiUrl = sharedPreferences.getString("API_URL", "");
        complaintRepository = new ComplaintRepository(context);
        ArrayList<Complaint> complaints = complaintRepository.getNonSyncedComplaints();
        if(complaints.size() > 0){
            for(Complaint complaint : complaints){
                ApiPostConn apiConn = new ApiPostConn(context, apiUrl + context.getString(R.string.api_post_complaint), ApiPostConn.COMPLAINT_POST_TAG, String.valueOf(complaint.Id));
                apiConn.execute(StringifyComplaint(complaint));
            }
        }
    }

    private String StringifyResult(Result result){
        //SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aaa");

        JSONObject json = new JSONObject();

        try {
            JSONArray innerArray=new JSONArray();
            for (Vote v: votesRepository.getModuleVotes(result.ElectionId, result.Id)) {
                JSONObject innerJson = new JSONObject();
                innerJson.put("Votes", v.Count);
                innerJson.put("Party", new PartyRepository(context).getParty(v.Party).Code);
                innerArray.put(innerJson);
            }
            //json.put("Id", result.Id);
            json.put("ElectionMetaDataId", result.ElectionId);
            json.put("UserProfileId", result.UserId);
            json.put("AccreditedVoters", result.AccredVotes);
            json.put("TotalVotesCast", result.CastVoted);
            json.put("InvalidVotes", result.InvalidVoted);
            json.put("PollingUnitCode", result.Unit);
            json.put("RegisteredVoters", result.RegVotes);
            json.put("PartyVotes", innerArray);
            JSONArray pictureInnerArray=new JSONArray();
            JSONObject pictureInnerJson = new JSONObject();
            pictureInnerJson.put("PictureEvidence", result.ProofImagePath != null ? Base64.encodeToString(ApplicationUtil.getBytesFromImageFile(result.ProofImagePath), Base64.DEFAULT) : "");
            //pictureInnerJson.put("PictureEvidenceUrl", "");
            pictureInnerArray.put(pictureInnerJson);
            json.put("PictureEvidence", pictureInnerJson);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }


    private String StringifyComplaint(Complaint complaint){
        JSONObject json = new JSONObject();

        try {
            json.put("Message", complaint.Message);
            json.put("UserProfileId", complaint.UserId);
            json.put("PollingUnitCode", complaint.PollingUnit);
            json.put("Title", complaint.Title);
            json.put("ElectionMetaDataId", complaint.ElectionId);
            json.put("Date", ApplicationUtil.parseDate(complaint.Date));
            json.put("Image", !complaint.ImagePath.equals("") ? Base64.encodeToString(ApplicationUtil.getBytesFromImageFile(complaint.ImagePath), Base64.DEFAULT) : "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
