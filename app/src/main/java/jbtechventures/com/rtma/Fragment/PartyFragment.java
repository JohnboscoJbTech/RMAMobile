package jbtechventures.com.rtma.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jbtechventures.com.rtma.Model.Vote;
import jbtechventures.com.rtma.R;
import jbtechventures.com.rtma.Repository.PartyRepository;
import jbtechventures.com.rtma.Repository.ResultRepository;
import jbtechventures.com.rtma.Repository.VotesRepository;
import jbtechventures.com.rtma.Utility.ApplicationUtil;

public class PartyFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    Context context;
    PartyRepository partyRepository;
    private ArrayList<String> PARTIES = new ArrayList<>();
    LinearLayout inflatedViewContainer;
    Button addPartyButton, nextButton, previousButton;
    private final String NEXT_FRAGMENT = "PHOTO_FRAGMENT";
    private final String PREVIOUS_FRAGMENT = "BASIC_RESULT_FRAGMENT";
    private ArrayList<String> SELECTED_PARTIES = new ArrayList<>();
    private ArrayList<String> REMAINING_PARTIES = new ArrayList<>();
    private ArrayList<Integer> SELECTED_PARTIES_IDS = new ArrayList<>();
    private int formId, resultId;
    private String pollingUnitCode;
    String electionName;
    VotesRepository votesRepository;
    ArrayList<Vote> votes;
    ImageView helpButton;
    TextView electionDisplayName;
    ResultRepository resultRepository;

    public PartyFragment() {
        // Required empty public constructor
    }

    public static PartyFragment newInstance() {
        PartyFragment fragment = new PartyFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();

        votesRepository = new VotesRepository(context);
        partyRepository = new PartyRepository(context);
        resultRepository = new ResultRepository(context);

        PARTIES = ApplicationUtil.getPartyNames(partyRepository.getParties());
        REMAINING_PARTIES = ApplicationUtil.getPartyNames(partyRepository.getParties());
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            formId = bundle.getInt(context.getString(R.string.module_id));
            resultId = bundle.getInt(context.getString(R.string.result_id));
            pollingUnitCode = bundle.getString(context.getString(R.string.polling_unit_name));
            if(formId != 0)
                votes = votesRepository.getModuleVotes(formId, resultId);
            electionName = bundle.getString(context.getString(R.string.module_name));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_party, container, false);

        init(view);
        setListener();

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

    private void init(View view){
        inflatedViewContainer = view.findViewById(R.id.parties_result_view);
        addPartyButton = view.findViewById(R.id.add_party_button);
        nextButton = view.findViewById(R.id.next_btn);
        previousButton = view.findViewById(R.id.previous_btn);
        helpButton = view.findViewById(R.id.help_button);
        electionDisplayName = view.findViewById(R.id.module_id);

        //default values
        addPartyButton.setVisibility(View.GONE);
        electionDisplayName.setText(electionName);
        if(votes != null && votes.size() > 0){
            //loop through the votes and assign the to the views with their parties
            //assign views
            assignView(votes);
        }
        else {
            View newView = addPartyView();
            inflatedViewContainer.addView(newView);
        }

    }

    private void setListener(){
        addPartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inflatedViewContainer.addView(addPartyView());
                addPartyButton.setVisibility(View.GONE);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SELECTED_PARTIES_IDS.size() >= 1){
                    ArrayList<Vote> enteredVotes = getPartiesAndScore(inflatedViewContainer);

                    //compare the votes cast to the parties result
                    int castVotes = resultRepository.getResult(formId, pollingUnitCode).CastVoted;
                    if(!ApplicationUtil.isVotesEqualToPartiesCount(castVotes, enteredVotes)){
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) view.findViewById(R.id.toast_layout_root));

                        TextView text = (TextView) layout.findViewById(R.id.toast_text);
                        text.setText("Votes cast is not equal to total parties count, Please check and try again");

                        Toast toast = new Toast(context);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                        return;
                    }

                    //save the votes

                    if(!resultRepository.resultCompleted(pollingUnitCode, formId)) {
                        for (Vote vote : enteredVotes) {
                            votesRepository.addVote(vote);
                        }
                    }

                    Bundle bundle = new Bundle();
                    bundle.putInt(context.getString(R.string.module_id), formId);
                    bundle.putString(context.getString(R.string.module_name), electionName);
                    bundle.putInt(context.getString(R.string.result_id), resultId);
                    bundle.putString(context.getString(R.string.polling_unit_name), pollingUnitCode);
                    PhotoFragment photoFragment = PhotoFragment.newInstance();
                    mListener.onNextButtonClicked(NEXT_FRAGMENT,photoFragment,bundle);
                }
                else{
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) view.findViewById(R.id.toast_layout_root));

                    TextView text = (TextView) layout.findViewById(R.id.toast_text);
                    text.setText("Please enter at least a party's result");

                    Toast toast = new Toast(context);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(context.getString(R.string.module_id), formId);
                bundle.putString(context.getString(R.string.module_name), electionName);
                bundle.putString(context.getString(R.string.polling_unit_name), pollingUnitCode);
                BasicResultFragment basicResultFragment = BasicResultFragment.newInstance();
                mListener.onPreviousButtonClicked(PREVIOUS_FRAGMENT,basicResultFragment,bundle);
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show instruction as alert
                DialogFragment fragment = new FragmentDialog();
                fragment.setCancelable(false);
                fragment.show(getFragmentManager(), "Instruction");
            }
        });
    }

    private View addPartyView(){
        final View newView = View.inflate(context, R.layout.parites_layout, null);
        AutoCompleteTextView name = newView.findViewById(R.id.party_name);
        final EditText editText = newView.findViewById(R.id.party_id);
        Button deleteRow = newView.findViewById(R.id.delete_button);
        final RelativeLayout partyBaseView = newView.findViewById(R.id.parties_base_view);
        //editText.setId(p.Id);

        //partyView = view.findViewById(R.id.party_name);

        /*Cursor cursor = partyRepository.getParties("");
        partySearchAdapter = new PartySearchAdapter(context, cursor);

        partyView.setAdapter(partySearchAdapter);*/
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, PARTIES);
        name.setAdapter(adapter);
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    //we just lost the focus
                    //check if the user typed the correct thing
                    String userInput = ((AutoCompleteTextView)view).getText().toString();
                    if(!REMAINING_PARTIES.contains(userInput)){
                        ((AutoCompleteTextView)view).setText("");
                    }
                    else{
                        int partyId = partyRepository.getParty(userInput).Id;
                        editText.setId(partyId);
                        addPartyButton.setVisibility(View.VISIBLE);
                        REMAINING_PARTIES.remove(userInput);
                        SELECTED_PARTIES.add(userInput);
                        SELECTED_PARTIES_IDS.add(partyId);
                    }
                }
                else{
                    //we just gained the focus
                    //check if the user cleared what they typed before
                    //or is typing a new text
                    String userInput = ((AutoCompleteTextView)view).getText().toString();
                    if(SELECTED_PARTIES.size() > 0 && !userInput.equals("") && SELECTED_PARTIES.contains(userInput)){
                        REMAINING_PARTIES.add(userInput);
                        SELECTED_PARTIES.remove(userInput);
                        SELECTED_PARTIES_IDS.remove((Integer) partyRepository.getParty(userInput).Id);
                    }
                    else{
                        /*int partyId = partyRepository.getParty(userInput).Id;
                        editText.setId(partyId);
                        addPartyButton.setVisibility(View.VISIBLE);
                        REMAINING_PARTIES.remove(userInput);
                        SELECTED_PARTIES.add(userInput);
                        SELECTED_PARTIES_IDS.add(partyId);*/
                    }
                }
            }
        });
        //partyBaseView.setOnTouchListener(new OnSwipeTouchListener(context, inflatedViewContainer, newView));
        deleteRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int childCount = inflatedViewContainer.getChildCount();
                inflatedViewContainer.removeView(newView);
                //also remove this party for the selected items,
                //and add it to the remaining
                int id = editText.getId();
                String partyName = partyRepository.getParty(id).Code;
                if(partyName != null){
                    REMAINING_PARTIES.add(partyName);
                    SELECTED_PARTIES.remove(partyName);
                    SELECTED_PARTIES_IDS.remove((Integer) id);
                }
                if(childCount <= 1){
                    addPartyButton.setVisibility(View.VISIBLE);
                }
            }
        });
        return newView;
    }

    private View addPartyView(Vote vote){
        final View newView = View.inflate(context, R.layout.parites_layout, null);
        AutoCompleteTextView name = newView.findViewById(R.id.party_name);
        final EditText editText = newView.findViewById(R.id.party_id);
        Button deleteRow = newView.findViewById(R.id.delete_button);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, PARTIES);
        name.setAdapter(adapter);
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    //we just lost the focus
                    //check if the user typed the correct thing
                    String userInput = ((AutoCompleteTextView)view).getText().toString();
                    if(!REMAINING_PARTIES.contains(userInput)){
                        ((AutoCompleteTextView)view).setText("");
                    }
                    else{
                        int partyId = partyRepository.getParty(userInput).Id;
                        editText.setId(partyId);
                        addPartyButton.setVisibility(View.VISIBLE);
                        REMAINING_PARTIES.remove(userInput);
                        SELECTED_PARTIES.add(userInput);
                        SELECTED_PARTIES_IDS.add(partyId);
                    }
                }
                else{
                    //we just gained the focus
                    //check if the user cleared what they typed before
                    //or is typing a new text
                    String userInput = ((AutoCompleteTextView)view).getText().toString();
                    if(SELECTED_PARTIES.size() > 0 && !userInput.equals("") && SELECTED_PARTIES.contains(userInput)){
                        REMAINING_PARTIES.add(userInput);
                        SELECTED_PARTIES.remove(userInput);
                        SELECTED_PARTIES_IDS.remove((Integer) partyRepository.getParty(userInput).Id);
                    }
                    else{
                    }
                }
            }
        });
        deleteRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int childCount = inflatedViewContainer.getChildCount();
                inflatedViewContainer.removeView(newView);
                //also remove this party for the selected items,
                //and add it to the remaining
                int id = editText.getId();
                String partyName = partyRepository.getParty(id).Code;
                if(partyName != null){
                    REMAINING_PARTIES.add(partyName);
                    SELECTED_PARTIES.remove(partyName);
                    SELECTED_PARTIES_IDS.remove((Integer) id);
                }
                if(childCount <= 1){
                    addPartyButton.setVisibility(View.VISIBLE);
                }
            }
        });
        /*Assign values*/
        //set the party name
        name.setText(partyRepository.getParty(vote.Party).Code);
        //set the vote and the id of the edittext
        editText.setId(vote.Party);
        editText.setText(String.valueOf(vote.Count));

        return newView;
    }

    private void assignView(ArrayList<Vote> existingVotes){
        for(Vote vote: existingVotes){
            //add a view to the inflated Container with default values
            //also add the SELECTED_ID array
            //and do all the neccessary adds
            inflatedViewContainer.addView(addPartyView(vote));
            SELECTED_PARTIES.add(partyRepository.getParty(vote.Party).Code);
            SELECTED_PARTIES_IDS.add(vote.Party);
            REMAINING_PARTIES.remove(partyRepository.getParty(vote.Party).Code);
        }
        addPartyButton.setVisibility(View.VISIBLE);
    }

    private ArrayList<Vote> getPartiesAndScore(LinearLayout view){
        ArrayList<Vote> userEnteredVotes = new ArrayList<>();
        for(int id: SELECTED_PARTIES_IDS){
            String voteCount = ((EditText)view.findViewById(id)).getText().toString().trim();
            Vote vote = new Vote();
            vote.ElectionId = formId;
            vote.Party = id;
            vote.Count = voteCount.equals("") ? 0 : Integer.parseInt(voteCount);
            vote.Result = resultId;
            userEnteredVotes.add(vote);
        }
        return userEnteredVotes;
    }

    public interface OnFragmentInteractionListener {
        void onNextButtonClicked(String fragmentTag, Fragment fragment, Bundle bundle);
        void onPreviousButtonClicked(String fragmentTag, Fragment fragment, Bundle bundle);
    }
}
