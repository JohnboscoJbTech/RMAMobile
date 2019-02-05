package jbtechventures.com.rtma;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;

import jbtechventures.com.rtma.Adapter.SubmissionListAdapter;
import jbtechventures.com.rtma.Model.Result;
import jbtechventures.com.rtma.Repository.PersonRepository;
import jbtechventures.com.rtma.Repository.ResultRepository;
import jbtechventures.com.rtma.Service.PostService;
import jbtechventures.com.rtma.Session.SessionManager;

public class SubmissionsActivity extends SessionManager {

    Context context;
    ListView submission_list;
    SubmissionListAdapter submissionListAdapter;
    ResultRepository resultRepository;
    FrameLayout no_election_configured;
    ArrayList<Result> results;
    PersonRepository personRepository;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submissions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        resultRepository = new ResultRepository(context);
        personRepository = new PersonRepository(context);

        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            results = new ArrayList<>();
        }
        else{
            userId = personRepository.getPersonCurrentlyLoggedIn().UserId;
            results = resultRepository.getSubmittedResults(userId);
        }

        init();

        //TODO add search, sync to menu,
        //TODO give option for discard *only for non-synced forms*
        //TODO give option for particular sync
        //TODO show election little details and status on pop-up when user clicks
        //TODO show result status completed/pending, synced/ not synced, differentiate with color green for success
        //TODO add info for sync from server by default it will be syncing
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.submission_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_synchronize) {
            PostService.startActionPostResult(context);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void init(){
        no_election_configured = findViewById(R.id.no_election_configured);

        submission_list = findViewById(R.id.submission_list);

        submissionListAdapter = new SubmissionListAdapter(context, R.layout.submission_view, results);

        submission_list.setAdapter(submissionListAdapter);

        /*initial vales*/
        if(results.size() > 0)
            no_election_configured.setVisibility(View.GONE);
    }
}