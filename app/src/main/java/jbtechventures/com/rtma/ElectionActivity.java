package jbtechventures.com.rtma;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import jbtechventures.com.rtma.Adapter.ListAdapter;
import jbtechventures.com.rtma.Model.Election;
import jbtechventures.com.rtma.Model.Person;
import jbtechventures.com.rtma.Repository.ElectionRepository;
import jbtechventures.com.rtma.Repository.PersonRepository;
import jbtechventures.com.rtma.Session.SessionManager;

public class ElectionActivity extends SessionManager
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context;
    ListView electionList;
    ElectionRepository electionRepository;
    SwipeRefreshLayout refreshLayout;
    FrameLayout no_election_configured;
    PersonRepository personRepository;
    ListAdapter listAdapter;
    ArrayList<Election> elections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        context = this;

        init();

        populate();

        setListener();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.election, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_synchronize) {
            refreshLayout.setRefreshing(true);
            GetActiveElections activeElections = new GetActiveElections();
            //get user credentials
            Person person = personRepository.getPerson();
            activeElections.execute(PostLoginForm(person.Username, person.Password));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_election) {
            // Handle the camera action
        } else if (id == R.id.nav_submission) {
            Intent intent = new Intent(context, SubmissionsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_complaints) {
            Intent intent = new Intent(context, ComplaintSubmissionActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_logout) {
            logUserOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init(){
        electionRepository = new ElectionRepository(context);
        personRepository = new PersonRepository(context);
        electionList = findViewById(R.id.election_list);

        refreshLayout = findViewById(R.id.refresh_layout);
        no_election_configured = findViewById(R.id.no_election_configured);

        /*initial vales*/
        no_election_configured.setVisibility(View.GONE);
    }

    private void populate(){
        elections = electionRepository.getAllActiveElections();
        listAdapter = new ListAdapter(context, R.layout.election_view, elections);

        if(elections.size() > 0) {
            no_election_configured.setVisibility(View.GONE);
            /*new Handler().post(new Runnable() {
                @Override
                public void run() {
                    electionListAdapter = new ElectionListAdapter(context, mCursor, 0);
                    electionList.setAdapter(electionListAdapter);
                    electionListAdapter.changeCursor(mCursor);

                    refreshLayout.setRefreshing(false);
                }
            });*/
            electionList.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
        }
        else{
            no_election_configured.setVisibility(View.VISIBLE);
        }

        refreshLayout.setRefreshing(false);
    }

    private void setListener(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        populate();
                    }
                }, 3000);
            }
        });
        refreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorPrimaryDark)
        );
    }

    private String PostLoginForm(String email, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("Phone", email);
            json.put("Token", password);
            json.put("IncludePollingUnit", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  json.toString();
    }

    /*
    * Logs a user out
    * */
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

    /**
     * Class for synchronizing forms
     * */
    private class GetActiveElections extends AsyncTask<String, Void, String> {
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
                String PREFS_NAME = context.getResources().getString(R.string.pref_name);
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                String apiUrl = sharedPreferences.getString("API_URL", "");
                // Defined URL  where to send data
                URL url = new URL(apiUrl + getString(R.string.api_login));
                // Send POST data request
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
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
                return "Error";
            }
        }

        protected void onPostExecute(String result) {
            if(!(result.equals(null) || result.isEmpty()) && !result.equals("Error")) {

                electionRepository.deleteElection();
                try {
                    JSONObject returnObject = new JSONObject(result);
                    JSONArray jsonArray = new JSONArray(returnObject.getString("activeElection"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Election election = new Election();
                        election.Id = jsonObject.getInt("id");
                        election.Name = jsonObject.getString("name");
                        election.StartDate = jsonObject.getString("startDate");
                        election.EndDate = jsonObject.getString("endDate");
                        election.Description = jsonObject.getString("electionCategory");
                        election.Active = 1;
                        electionRepository.addElection(election);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        populate();
                    }
                });
            }
            else {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) refreshLayout.findViewById(R.id.toast_layout_root));

                TextView text = (TextView) layout.findViewById(R.id.toast_text);
                text.setText("There was an error getting data from server");

                Toast toast = new Toast(context);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                });
            }
            super.onPostExecute(result);
        }
    }
}
