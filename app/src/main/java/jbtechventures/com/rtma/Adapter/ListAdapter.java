package jbtechventures.com.rtma.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import jbtechventures.com.rtma.ComplaintActivity;
import jbtechventures.com.rtma.Model.Election;
import jbtechventures.com.rtma.R;
import jbtechventures.com.rtma.ResultCaptureActivity;
import jbtechventures.com.rtma.SubmissionsActivity;

public class ListAdapter extends ArrayAdapter {

    ArrayList<Election> elections;
    Context context;

    public ListAdapter(@NonNull Context _context, int resource, @NonNull ArrayList<Election> _elections) {
        super(_context, resource, _elections);
        context = _context;
        elections = _elections;
    }

    @Override
    public Election getItem(int i) {
        return elections.get(i);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.election_view, parent, false);
        }

        Election election = getItem(position);

        TextView name = convertView.findViewById(R.id.name);
        final String theName = election.Name;
        final int theId = election.Id;
        name.setText(theName);
        TextView startDate = convertView.findViewById(R.id.start_date);
        startDate.setText(election.StartDate);
        TextView endDate = convertView.findViewById(R.id.end_date);
        endDate.setText(election.EndDate);

        Button enter_result = convertView.findViewById(R.id.enter_result);
        enter_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ResultCaptureActivity.class);
                intent.putExtra(context.getString(R.string.module_id), theId);
                intent.putExtra(context.getString(R.string.module_name), theName);
                context.startActivity(intent);
            }
        });
        Button view_activity = convertView.findViewById(R.id.view_activity);
        view_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ComplaintActivity.class);
                intent.putExtra(context.getString(R.string.module_id), theId);
                intent.putExtra(context.getString(R.string.module_name), theName);
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
