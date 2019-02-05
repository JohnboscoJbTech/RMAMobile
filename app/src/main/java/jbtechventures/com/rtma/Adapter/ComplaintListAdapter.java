package jbtechventures.com.rtma.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jbtechventures.com.rtma.Model.Complaint;
import jbtechventures.com.rtma.Model.Election;
import jbtechventures.com.rtma.Model.PollingUnit;
import jbtechventures.com.rtma.R;
import jbtechventures.com.rtma.Repository.ElectionRepository;
import jbtechventures.com.rtma.Repository.PollingRepository;

public class ComplaintListAdapter extends ArrayAdapter {

    ArrayList<Complaint> complaints;
    Context context;

    public ComplaintListAdapter(@NonNull Context _context, int resource, @NonNull ArrayList<Complaint> _complaints) {
        super(_context, resource, _complaints);
        context = _context;
        complaints = _complaints;
    }

    @Override
    public Complaint getItem(int i) {
        return complaints.get(i);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.complaint_view, parent, false);
        }

        Complaint complaint = getItem(position);
        PollingRepository pollingRepository = new PollingRepository(context);
        ElectionRepository electionRepository = new ElectionRepository(context);

        TextView election_name = convertView.findViewById(R.id.election_name);
        Election election = electionRepository.getElection(complaint.ElectionId);
        election_name.setText(election.Name);

        TextView name = convertView.findViewById(R.id.name);
        PollingUnit pollingUnit = pollingRepository.getPollingUnit(complaint.PollingUnit);
        name.setText(pollingUnit.Lga + "/" + pollingUnit.Ward + "/" + pollingUnit.PollingUnit);

        TextView sync_status = convertView.findViewById(R.id.sync_status);
        sync_status.setTextColor(complaint.Synced == 1 ? Color.GREEN : Color.MAGENTA);
        sync_status.setText(complaint.Synced == 1 ? "Completed" : "Pending");

        /*Button error_indicator = convertView.findViewById(R.id.error_indicator);
        error_indicator.setVisibility(complaint.SyncErrorText == null || complaint.SyncErrorText.equals("") ? View.GONE : View.VISIBLE);
        error_indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO show snack bar with error
            }
        });*/

        LinearLayout container = convertView.findViewById(R.id.the_container);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return convertView;
    }
}
