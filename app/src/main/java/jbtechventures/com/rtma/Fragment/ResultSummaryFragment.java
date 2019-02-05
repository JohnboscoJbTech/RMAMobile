package jbtechventures.com.rtma.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jbtechventures.com.rtma.ElectionActivity;
import jbtechventures.com.rtma.Model.PollingUnit;
import jbtechventures.com.rtma.Model.Result;
import jbtechventures.com.rtma.Model.Vote;
import jbtechventures.com.rtma.R;
import jbtechventures.com.rtma.Repository.PartyRepository;
import jbtechventures.com.rtma.Repository.PollingRepository;
import jbtechventures.com.rtma.Repository.ResultRepository;
import jbtechventures.com.rtma.Repository.VotesRepository;
import jbtechventures.com.rtma.Service.PostService;

public class ResultSummaryFragment extends Fragment {

    Context context;
    private OnFragmentInteractionListener mListener;
    Result result;
    private int formId, resultId;
    TextView electionDisplayName;
    private final String PREVIOUS_FRAGMENT = "PHOTO_FRAGMENT";
    VotesRepository votesRepository;
    PollingRepository pollingRepository;
    ArrayList<Vote> votes;
    private String pollingUnitCode;
    ResultRepository resultRepository;
    PartyRepository partyRepository;
    String electionName;
    /*Destination information*/
    TextView state,lga,ward,pollingUnit;
    /*Basic Result Information*/
    TextView accreditedVotes, invalidVotes, regVotes, castVotes;
    /*Picture*/
    ImageView imageView;
    /*Parties*/
    LinearLayout inflatedViewContainer;
    Button previousButton, submitButton;

    public ResultSummaryFragment() {
        // Required empty public constructor
    }

    public static ResultSummaryFragment newInstance() {
        ResultSummaryFragment fragment = new ResultSummaryFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        votesRepository = new VotesRepository(context);
        resultRepository = new ResultRepository(context);
        pollingRepository = new PollingRepository(context);
        partyRepository = new PartyRepository(context);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            formId = bundle.getInt(context.getString(R.string.module_id));
            pollingUnitCode = bundle.getString(context.getString(R.string.polling_unit_name));
            resultId = bundle.getInt(context.getString(R.string.result_id));
            if(formId != 0) {
                result = resultRepository.getResult(formId, pollingUnitCode);
                votes = votesRepository.getModuleVotes(formId, resultId);
            }
            electionName = bundle.getString(context.getString(R.string.module_name));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result_summary, container, false);

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
        electionDisplayName = view.findViewById(R.id.module_id);
        state = view.findViewById(R.id.tv_state);
        lga = view.findViewById(R.id.tv_lga);
        ward = view.findViewById(R.id.tv_ward);
        pollingUnit = view.findViewById(R.id.tv_polling_unit);
        accreditedVotes = view.findViewById(R.id.tv_accred_votes);
        invalidVotes = view.findViewById(R.id.tv_inv_votes);
        regVotes = view.findViewById(R.id.tv_reg_voters);
        castVotes = view.findViewById(R.id.tv_cast_votes);
        imageView = view.findViewById(R.id.imgv_pic_proof);

        inflatedViewContainer = view.findViewById(R.id.parties_score_layout);
        submitButton = view.findViewById(R.id.submit_btn);
        previousButton = view.findViewById(R.id.previous_btn);

        /*Default values*/
        electionDisplayName.setText(electionName);

        if(result != null){
            assignView(result);
        }

    }

    private void setListener(){
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(context.getString(R.string.module_id), formId);
                bundle.putString(context.getString(R.string.module_name), electionName);
                bundle.putInt(context.getString(R.string.result_id), result.Id);
                bundle.putString(context.getString(R.string.polling_unit_name), pollingUnitCode);
                PhotoFragment photoFragment = PhotoFragment.newInstance();
                mListener.onPreviousButtonClicked(PREVIOUS_FRAGMENT, photoFragment, bundle);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Result Submission")
                        .setMessage("Are you sure you want to submit the result! You cannot edit this result once it is submitted")
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(android.R.string.yes,  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO check if there is network connection and sync result in the UI else
                                //go to Election Activity also try syncing here
                                resultRepository.updateCompleted(resultId);
                                PostService.startActionPostResult(context);
                                Intent intent = new Intent(context, ElectionActivity.class);
                                startActivity(intent);
                            }
                        })
                        .create().show();
            }
        });
    }

    private void assignView(Result result){
        PollingUnit polling = pollingRepository.getPollingUnit(result.Unit);
        state.setText(polling.State);
        lga.setText(polling.Lga);
        ward.setText(polling.Ward);
        pollingUnit.setText(polling.PollingUnit);
        regVotes.setText(String.valueOf(result.RegVotes));
        accreditedVotes.setText(String.valueOf(result.AccredVotes));
        invalidVotes.setText(String.valueOf(result.InvalidVoted));
        castVotes.setText(String.valueOf(result.CastVoted));
        imageView.setImageURI(Uri.parse(result.ProofImagePath));

        //Inflate Parties
        for (Vote vote: votes) {
            inflatedViewContainer.addView(addPartyView(vote));
        }

        submitButton.setVisibility(result.Synced == 1 ? View.GONE : View.VISIBLE);
    }

    private View addPartyView(Vote vote){
        final View newView = View.inflate(context, R.layout.parites_view_layout, null);
        TextView party, partyCount;
        party = newView.findViewById(R.id.party);
        partyCount = newView.findViewById(R.id.party_count);

        party.setText(partyRepository.getParty(vote.Party).Name);
        partyCount.setText(String.valueOf(vote.Count));
        return newView;
    }

    public interface OnFragmentInteractionListener {
        void onNextButtonClicked(String fragmentTag, Fragment fragment, Bundle bundle);
        void onPreviousButtonClicked(String fragmentTag, Fragment fragment, Bundle bundle);
    }
}
