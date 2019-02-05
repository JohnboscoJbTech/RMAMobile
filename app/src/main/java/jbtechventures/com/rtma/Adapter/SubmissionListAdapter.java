package jbtechventures.com.rtma.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

import jbtechventures.com.rtma.Model.Election;
import jbtechventures.com.rtma.Model.PollingUnit;
import jbtechventures.com.rtma.Model.Result;
import jbtechventures.com.rtma.R;
import jbtechventures.com.rtma.Repository.ElectionRepository;
import jbtechventures.com.rtma.Repository.PollingRepository;

public class SubmissionListAdapter extends ArrayAdapter {

    ArrayList<Result> results;
    Context context;

    public SubmissionListAdapter(@NonNull Context _context, int resource, @NonNull ArrayList<Result> _results) {
        super(_context, resource, _results);
        context = _context;
        results = _results;
    }

    @Override
    public Result getItem(int i) {
        return results.get(i);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View theView = parent.getFocusedChild();
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.submission_view, parent, false);
        }

        final Result result = getItem(position);
        PollingRepository pollingRepository = new PollingRepository(context);
        ElectionRepository electionRepository = new ElectionRepository(context);

        TextView election_name = convertView.findViewById(R.id.election_name);
        Election election = electionRepository.getElection(result.ElectionId);
        election_name.setText(election.Name);

        TextView name = convertView.findViewById(R.id.name);
        PollingUnit pollingUnit = pollingRepository.getPollingUnit(result.Unit);
        name.setText(pollingUnit.Lga + "/" + pollingUnit.Ward + "/" + pollingUnit.PollingUnit);

        TextView completion_status = convertView.findViewById(R.id.completion_status);
        completion_status.setTextColor(result.Completed == 1 ? Color.GREEN : Color.MAGENTA);
        completion_status.setText(result.Completed == 1 ? "Completed" : "Pending");

        TextView sync_status = convertView.findViewById(R.id.sync_status);
        sync_status.setTextColor(result.Synced == 1 ? Color.GREEN : Color.MAGENTA);
        sync_status.setText(result.Synced == 1 ? "Completed" : "Pending");

        Button error_indicator = convertView.findViewById(R.id.error_indicator);
        error_indicator.setVisibility(result.SyncErrorText == null || result.SyncErrorText.equals("") ? View.GONE : View.VISIBLE);
        error_indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO show snack bar with error
                Snackbar snackbar = Snackbar.make(view.getRootView(), result.SyncErrorText, Snackbar.LENGTH_LONG);

                /*Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
                // Hide the text
                TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
                textView.setVisibility(View.INVISIBLE);

                // Inflate our custom view
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                View snackView = inflater.inflate(R.layout.custom_snack_bar, null);
                // Configure the view
                TextView textViewTop = (TextView) snackView.findViewById(R.id.snack_text);
                textViewTop.setText(result.SyncErrorText);
                textViewTop.setTextColor(Color.WHITE);

                //If the view is not covering the whole snackbar layout, add this line
                layout.setPadding(0,0,0,0);

                // Add the view to the Snackbar's layout
                layout.addView(snackView, 0);
                // Show the Snackbar*/
                snackbar.show();
            }
        });

        LinearLayout container = convertView.findViewById(R.id.the_container);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return convertView;
    }
}
