package jbtechventures.com.rtma.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import jbtechventures.com.rtma.Model.Result;
import jbtechventures.com.rtma.R;
import jbtechventures.com.rtma.Repository.PersonRepository;
import jbtechventures.com.rtma.Repository.ResultRepository;

public class BasicResultFragment extends Fragment {

    private OnFragmentInteractionListener mListener;Context context;
    private int formId;
    private String pollingUnitCode;
    EditText regVote, accredVotes, castVotes, invalidVotes;
    String electionName;
    TextView electionDisplayName;
    Button nextButton, previousButton;
    private final String NEXT_FRAGMENT = "PARTY_FRAGMENT";
    private final String PREVIOUS_FRAGMENT = "POLLING_FRAGMENT";
    ResultRepository resultRepository;
    PersonRepository personRepository;
    Result result;
    int userId;

    public BasicResultFragment() {
        // Required empty public constructor
    }

    public static BasicResultFragment newInstance() {
        BasicResultFragment fragment = new BasicResultFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        resultRepository = new ResultRepository(context);
        personRepository = new PersonRepository(context);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            formId = bundle.getInt(context.getString(R.string.module_id));
            pollingUnitCode = bundle.getString(context.getString(R.string.polling_unit_name));
            userId = personRepository.getPersonCurrentlyLoggedIn().UserId;
            if(formId != 0)
                result = resultRepository.getResult(formId,pollingUnitCode);
            electionName = bundle.getString(context.getString(R.string.module_name));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic_result, container, false);
        init(view);
        if(result != null){
            assignView(result);
        }
        setListeners();
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
        regVote = view.findViewById(R.id.registered_votes);
        accredVotes = view.findViewById(R.id.accredited_voters);
        castVotes = view.findViewById(R.id.cast_votes);
        invalidVotes = view.findViewById(R.id.invalid_votes);
        electionDisplayName = view.findViewById(R.id.module_id);

        nextButton = view.findViewById(R.id.next_btn);
        previousButton = view.findViewById(R.id.previous_btn);

        /*initial values*/
        electionDisplayName.setText(electionName);
    }

    private void assignView(Result result){
        regVote.setText(String.valueOf(result.RegVotes));
        accredVotes.setText(String.valueOf(result.AccredVotes));
        castVotes.setText(String.valueOf(result.CastVoted));
        invalidVotes.setText(String.valueOf(result.InvalidVoted));
    }

    /**
     * Validate the form
     * */
    private boolean validate(){
        if(regVote.getText().toString().trim().equals("")) {
            regVote.setError("This field is required");
            regVote.requestFocus();
            return false;
        }
        if(invalidVotes.getText().toString().trim().equals("")) {
            invalidVotes.setError("This field is required");
            invalidVotes.requestFocus();
            return false;
        }
        if(accredVotes.getText().toString().trim().equals("")) {
            accredVotes.setError("This field is required");
            accredVotes.requestFocus();
            return false;
        }
        if(castVotes.getText().toString().trim().equals("")) {
            castVotes.setError("This field is required");
            castVotes.requestFocus();
            return false;
        }
        //check for votes
        if(!checkVoteEntry()){
            return false;
        }
        return true;
    }

    private boolean checkVoteEntry(){
        int cast, accredit, invalid, reg;
        cast = Integer.parseInt(castVotes.getText().toString().trim());
        accredit = Integer.parseInt(accredVotes.getText().toString().trim());
        invalid = Integer.parseInt(invalidVotes.getText().toString().trim());
        reg = Integer.parseInt(regVote.getText().toString().trim());
        if(accredit > reg) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) nextButton.findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText("Accredited Voters cannot be more than Registered Voters");

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            return false;
        }
        if(accredit < (cast + invalid)) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) nextButton.findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText("Accredited Voters cannot be less than the sum of the Votes cast and the invalid Votes");

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            return false;
        }
        /*if(reg < (cast + invalid)){
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) nextButton.findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText("Registered Voters cannot be less than the sum of the Votes cast and the invalid Votes");

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            return false;
        }*/
        return true;
    }

    private void setListeners(){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save the current form
                if(validate()){
                    if(result == null) {
                        result = new Result();
                    }
                    result.ElectionId = formId;
                    result.CastVoted = Integer.parseInt(castVotes.getText().toString().trim());
                    result.AccredVotes = Integer.parseInt(accredVotes.getText().toString().trim());
                    result.InvalidVoted = Integer.parseInt(invalidVotes.getText().toString().trim());
                    result.RegVotes = Integer.parseInt(regVote.getText().toString().trim());
                    result.Unit = pollingUnitCode;
                    result.Synced = 0;
                    result.Completed = 0;
                    result.UserId = new PersonRepository(context).getPersonCurrentlyLoggedIn().UserId;

                    resultRepository.addResult(result);

                    if(mListener != null){
                        Bundle bundle = new Bundle();
                        bundle.putInt(context.getString(R.string.module_id), formId);
                        bundle.putString(context.getString(R.string.module_name), electionName);
                        bundle.putInt(context.getString(R.string.result_id), resultRepository.getResult(formId,pollingUnitCode).Id);
                        bundle.putString(context.getString(R.string.polling_unit_name), pollingUnitCode);
                        PartyFragment partyFragment = PartyFragment.newInstance();
                        mListener.onNextButtonClicked(NEXT_FRAGMENT, partyFragment, bundle);
                    }
                }
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(context.getString(R.string.module_id), formId);
                bundle.putString(context.getString(R.string.module_name), electionName);
                PollingFragment pollingFragment = PollingFragment.newInstance();
                mListener.onPreviousButtonClicked(PREVIOUS_FRAGMENT, pollingFragment, bundle);
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onNextButtonClicked(String fragmentTag, Fragment fragment, Bundle bundle);
        void onPreviousButtonClicked(String fragmentTag, Fragment fragment, Bundle bundle);
    }
}
