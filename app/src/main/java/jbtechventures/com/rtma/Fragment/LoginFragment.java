package jbtechventures.com.rtma.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import jbtechventures.com.rtma.ElectionActivity;
import jbtechventures.com.rtma.Model.AppError;
import jbtechventures.com.rtma.Model.Election;
import jbtechventures.com.rtma.Model.Party;
import jbtechventures.com.rtma.Model.Person;
import jbtechventures.com.rtma.Model.PollingUnit;
import jbtechventures.com.rtma.R;
import jbtechventures.com.rtma.Repository.ElectionRepository;
import jbtechventures.com.rtma.Repository.PartyRepository;
import jbtechventures.com.rtma.Repository.PersonRepository;
import jbtechventures.com.rtma.Repository.PollingRepository;
import jbtechventures.com.rtma.Utility.BindingMeths;

public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    Context context;
    private UserLoginTask mAuthTask = null;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private ElectionRepository electionRepository;
    private PersonRepository personRepository;
    //private PollingRepository pollingRepository;
    private AppError mAppError;
    private int mUserId;
    LinearLayout setup_layout;
    ProgressBar pb;
    TextView cur_val;
    Thread operation;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        /*args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();

        electionRepository = new ElectionRepository(context);
        personRepository = new PersonRepository(context);
        //pollingRepository = new PollingRepository(context);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initView(View view){
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) view.findViewById(R.id.email);
        //populateAutoComplete();

        mPasswordView = (EditText) view.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) view.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = view.findViewById(R.id.login_form);
        mProgressView = view.findViewById(R.id.login_progress);

        setup_layout = view.findViewById(R.id.setup_layout);
        setup_layout.setVisibility(View.GONE);

        cur_val = (TextView) view.findViewById(R.id.cur_pg_tv);

        pb = (ProgressBar)view.findViewById(R.id.progress_bar);
        pb.setProgress(0);
        pb.setProgressDrawable(getResources().getDrawable(R.drawable.setup_progress));
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } /*else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            if (personRepository.personCurrentlyLoggedIn(email, password)) {

                //personRepository.updatePersonLogin(new Date().toString(), user_email);
                Intent intent = new Intent(context, ElectionActivity.class);
                startActivity(intent);
                //finish();
                showProgress(false);
            }else {
                //disable activity login button through the interface
                mListener.onApiLoginIntiated(true);
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);
            }
            /*mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);*/
        }

    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showSetup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            mLoginFormView.setVisibility(View.GONE);
            mProgressView.setVisibility(View.GONE);

        } else {
            mProgressView.setVisibility(View.GONE);
            mLoginFormView.setVisibility(View.GONE);
        }
    }


    private String PostLoginForm(String email, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("Phone", email);
            json.put("Token", password);
            json.put("IncludePollingUnit", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  json.toString();
    }

    void showSetUpProgress(final JSONArray pollingJsonArray){
        showSetup();
        setup_layout.setVisibility(View.VISIBLE);

        operation = new Thread(new Runnable() {
            public void run() {
                try {
                    PollingRepository pollingRepository = new PollingRepository(context);
                    //ArrayList<PollingUnit> pollings = new ArrayList<>();

                    pollingRepository.deletePollingUnits();

                    for (int i = 0; i < pollingJsonArray.length(); i++) {
                        JSONObject jsonObject = pollingJsonArray.getJSONObject(i);
                        PollingUnit pollingUnit = new PollingUnit();
                        pollingUnit.State = jsonObject.getString("state");
                        pollingUnit.StateCode = jsonObject.getString("state_code");
                        pollingUnit.Lga = jsonObject.getString("lga");
                        pollingUnit.LgaCode = jsonObject.getString("lga_code");
                        pollingUnit.Ward = jsonObject.getString("ward");
                        pollingUnit.WardCode = jsonObject.getString("wardCode");
                        pollingUnit.PollingUnit = jsonObject.getString("pollingUnit");
                        pollingUnit.PollingUnitCode = jsonObject.getString("pollinguitCode");

                        final float per = ((float)i/pollingJsonArray.length()) * 100;
                        final int progerss = i;
                        pollingRepository.addPollingUnit(pollingUnit);
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                pb.setMax(pollingJsonArray.length());
                                pb.setProgress(progerss);

                                cur_val.setText("Setup " + (int) per + "% Completion");
                            }
                        });
                        //pollings.add(pollingUnit);
                    }

                    //pollingRepository.addPollingUnits(pollings);
                    //Get parties with Intent service
                    //GetService.startActionGetParty(context);
                    //Prepopulate parties
                    final ArrayList<Party> parties = new BindingMeths(context).bindPartyData();
                    PartyRepository partyRepository = new PartyRepository(context);
                    for (int i = 0; i < parties.size(); i++) {

                        Party party = parties.get(i);
                        partyRepository.addParty(party);

                        final int progress = i;
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                pb.setMax(parties.size());
                                pb.setProgress(progress);

                                cur_val.setText("Finishig up...");
                            }
                        });
                    }
                    //We are done inserting
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onApiLoginIntiated(false);
                        }
                    });
                    Intent intent = new Intent(context, ElectionActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        operation.start();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        BufferedReader reader=null;
        String data;
        String text = "";
        JSONArray jsonArray;
        JSONArray pollingJsonArray;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            data = PostLoginForm(mEmail, mPassword);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mAppError = new AppError();
            try {
                String PREFS_NAME = context.getResources().getString(R.string.pref_name);
                SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
                String apiUrl = sharedPreferences.getString("API_URL", "");
                String login_url = apiUrl + getResources().getString(R.string.api_login);
                URL url = new URL(login_url);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                //conn.setConnectTimeout(90000);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null)
                {
                    sb.append(line);
                }
                text = sb.toString();
                conn.disconnect();

                //save the returned result
                JSONObject returnObject = new JSONObject(text);
                jsonArray = new JSONArray(returnObject.getString("activeElection"));
                pollingJsonArray = new JSONArray(returnObject.getString("pollingunits"));

            } catch (ProtocolException e) {
                mAppError.ErrorType = "ProtocolException";
                mAppError.ErrorMessage = "Please Check your Credentials or Internet Connection";
                return false;
            } catch (MalformedURLException e) {
                mAppError.ErrorType = "MalformedURLException";
                mAppError.ErrorMessage = "Please Check your Credentials or Internet Connection";
                return false;
            } catch (IOException e) {
                mAppError.ErrorType = "IOException";
                mAppError.ErrorMessage = "Please Check your Credentials or Internet Connection";
                return false;
            } catch (JSONException e) {
                mAppError.ErrorType = "JSONException";
                mAppError.ErrorMessage = "Please Check your Credentials or Internet Connection";
                e.printStackTrace();
            }

            /*try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }*/

            /*for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }*/

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            //showProgress(false);

            if (success) {
                //delete all election data and save new ones
                electionRepository.deleteElection();
                try {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Election election = new Election();
                        election.Id = jsonObject.getInt("id");
                        election.Name = jsonObject.getString("name");
                        election.StartDate = jsonObject.getString("startDate");
                        election.EndDate = jsonObject.getString("endDate");
                        election.Description = jsonObject.getString("electionCategory");
                        mUserId = jsonObject.getInt("userId");
                        election.Active = 1;
                        electionRepository.addElection(election);
                    }

                    personRepository.updatePersonLogin();
                    Person person = new Person();
                    person.Username = mEmail;
                    person.Password = mPassword;
                    person.UserId = mUserId;
                    person.LoggedIn = 1;
                    personRepository.addPerson(person);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Get the lga and ward and Pus

                /*Intent intent = new Intent(context, SetupActivity.class);
                intent.putExtra("JSON_ARRAY", pollingJsonArray.toString());
                startActivity(intent);*/
                showSetUpProgress(pollingJsonArray);
            } else {
                if(mAppError != null){
                    mPasswordView.setError(mAppError.ErrorMessage);
                    mPasswordView.requestFocus();
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showProgress(false);
                            }
                        });
                    }
                }
                /*mPasswordView.setError(getString(R.string.error_incorrect_credential));
                mPasswordView.requestFocus();*/
                if(mListener != null)
                    mListener.onApiLoginIntiated(false);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
            if(mListener != null)
                mListener.onApiLoginIntiated(false);
        }
    }

    public interface OnFragmentInteractionListener {
        void onApiLoginIntiated(Boolean value);
    }
}
