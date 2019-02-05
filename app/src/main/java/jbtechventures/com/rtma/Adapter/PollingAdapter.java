package jbtechventures.com.rtma.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import jbtechventures.com.rtma.Model.Lga;

/**
 * Created by Johnbosco on 20/04/2017.
 */

public class PollingAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<Lga> pollingUnits;

    public PollingAdapter(Context context, ArrayList<Lga> _pollingUnits) {
        _context = context;
        this.pollingUnits = _pollingUnits;
    }
    @Override
    public int getCount() {
        return pollingUnits.size();
    }

    @Override
    public Lga getItem(int i) {
        return pollingUnits.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView=new TextView(_context);
        textView.setText(getItem(i).Name);
        textView.setPadding(10, 10, 10, 10);

        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);
        return textView;
    }
}
