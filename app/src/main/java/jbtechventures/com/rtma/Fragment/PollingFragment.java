package jbtechventures.com.rtma.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.satsuware.usefulviews.LabelledSpinner;

import jbtechventures.com.rtma.Model.Lga;
import jbtechventures.com.rtma.R;
import jbtechventures.com.rtma.Repository.ResultRepository;
import jbtechventures.com.rtma.Utility.BindingMeths;

public class PollingFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    Context context;
    private int formId;
    String electionName;
    TextView electionDisplayName;
    Button nextButton;
    LabelledSpinner lgaSpinner, wardSpinner, puSpinner;
    private final String NEXT_FRAGMENT = "BASIC_RESULT_FRAGMENT";
    ResultRepository resultRepository;

    public PollingFragment() {
        // Required empty public constructor
    }

    public static PollingFragment newInstance() {
        PollingFragment fragment = new PollingFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        resultRepository = new ResultRepository(context);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            formId = bundle.getInt(context.getString(R.string.module_id));

            electionName = bundle.getString(context.getString(R.string.module_name));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_polling, container, false);
        init(view);

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
        electionDisplayName = view.findViewById(R.id.module_id);
        lgaSpinner = view.findViewById(R.id.lga_spinner);
        wardSpinner = view.findViewById(R.id.ward_spinner);
        puSpinner = view.findViewById(R.id.pu_spinner);

        nextButton = view.findViewById(R.id.next_btn);

        /*initial values*/
        electionDisplayName.setText(electionName);
        BindingMeths bindingMeths = new BindingMeths(context);
        bindingMeths.bindLga(lgaSpinner);
    }

    /**
     * Validate the form
     * */
    private boolean validate(View view){
        if(puSpinner.getSpinner().getSelectedItemPosition() == 0){
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) view.findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText("All fields are required");

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            //Toast.makeText(context, "All fields are required", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void setListeners(){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save the current form
                if(validate(view)){
                    /*result = new Result();
                    result.ElectionId = formId;
                    result.Unit = ((Lga)puSpinner.getSpinner().getSelectedItem()).Code;

                    result.Id = resultRepository.addResult(result);*/
                    String pollingUnit = ((Lga)puSpinner.getSpinner().getSelectedItem()).Code;

                    if(mListener != null){
                        Bundle bundle = new Bundle();
                        bundle.putInt(context.getString(R.string.module_id), formId);
                        bundle.putString(context.getString(R.string.module_name), electionName);
                        bundle.putString(context.getString(R.string.polling_unit_name), pollingUnit);
                        BasicResultFragment basicResultFragment = BasicResultFragment.newInstance();
                        mListener.onNextButtonClicked(NEXT_FRAGMENT, basicResultFragment, bundle);
                    }
                }
            }
        });

        lgaSpinner.setOnItemChosenListener(new LabelledSpinner.OnItemChosenListener() {
            @Override
            public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
                BindingMeths bindingMeths = new BindingMeths(context);
                String selectedLga = ((Lga)lgaSpinner.getSpinner().getSelectedItem()).Code;

                bindingMeths.bindWard(wardSpinner, selectedLga);
            }

            @Override
            public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {

            }
        });

        wardSpinner.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                BindingMeths bindingMeths = new BindingMeths(context);
                String selectedWard = ((Lga)wardSpinner.getSpinner().getSelectedItem()).Code;

                bindingMeths.bindPu(puSpinner, selectedWard);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onNextButtonClicked(String fragmentTag, Fragment fragment, Bundle bundle);
    }
}
