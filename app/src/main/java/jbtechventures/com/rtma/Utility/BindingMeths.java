package jbtechventures.com.rtma.Utility;

import android.content.Context;

import com.satsuware.usefulviews.LabelledSpinner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jbtechventures.com.rtma.Adapter.PollingAdapter;
import jbtechventures.com.rtma.Model.Lga;
import jbtechventures.com.rtma.Model.Party;
import jbtechventures.com.rtma.R;
import jbtechventures.com.rtma.Repository.PollingRepository;

/**
 * Created by Johnbosco on 12/07/2017.
 * This class binds all spinner drop down used in the application
 * it also gets the items by the id when queried
 */

public class BindingMeths {
    private Context context;

    public BindingMeths(Context _context) {
        context = _context;
    }

    public void bindLga(LabelledSpinner spinner) {
        PollingRepository pollingRepository = new PollingRepository(context);
        ArrayList<Lga> lgas = pollingRepository.getLgas();
        Lga lga = new Lga();
        lga.Name = "Select Lga";
        lgas.add(0, lga);
        PollingAdapter adapter = new PollingAdapter(context, lgas);
        spinner.getSpinner().setAdapter(adapter);
    }

    public void bindWard(LabelledSpinner spinner, String lgaCode) {
        PollingRepository pollingRepository = new PollingRepository(context);
        ArrayList<Lga> lgas = pollingRepository.getWards(lgaCode);
        Lga lga = new Lga();
        lga.Name = "Select Ward";
        lgas.add(0, lga);
        PollingAdapter adapter = new PollingAdapter(context, lgas);
        spinner.getSpinner().setAdapter(adapter);
    }

    public void bindPu(LabelledSpinner spinner, String WardCode) {
        PollingRepository pollingRepository = new PollingRepository(context);
        ArrayList<Lga> lgas = pollingRepository.getPus(WardCode);
        Lga lga = new Lga();
        lga.Name = "Select Polling Unit";
        lgas.add(0, lga);
        PollingAdapter adapter = new PollingAdapter(context, lgas);
        spinner.getSpinner().setAdapter(adapter);
    }

    public ArrayList<Party> bindPartyData() {

        XMLParser parser = new XMLParser();
        final ArrayList<Party> parties = new ArrayList<>();
        Document doc = parser.getDomElement(readParty());
        NodeList nl = doc.getElementsByTagName("row");

        for (int i = 0; i < nl.getLength(); i++){
            Element element = (Element)nl.item(i);
            Party party = new Party();
            party.Id = Integer.parseInt(parser.getValue(element, "id"));
            party.Name = parser.getValue(element, "name");
            party.Code = parser.getValue(element, "code");
            parties.add(party);
        }
        return  parties;
    }

    private String readParty(){
        InputStream inputStream = context.getResources().openRawResource(R.raw.party);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }
}
