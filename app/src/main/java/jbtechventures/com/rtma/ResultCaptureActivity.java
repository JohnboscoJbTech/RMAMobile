package jbtechventures.com.rtma;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jbtechventures.com.rtma.Fragment.BasicResultFragment;
import jbtechventures.com.rtma.Fragment.PartyFragment;
import jbtechventures.com.rtma.Fragment.PhotoFragment;
import jbtechventures.com.rtma.Fragment.PollingFragment;
import jbtechventures.com.rtma.Fragment.ResultSummaryFragment;
import jbtechventures.com.rtma.Session.SessionManager;

public class ResultCaptureActivity extends SessionManager implements PollingFragment.OnFragmentInteractionListener,
        BasicResultFragment.OnFragmentInteractionListener, PartyFragment.OnFragmentInteractionListener,
        PhotoFragment.OnFragmentInteractionListener, ResultSummaryFragment.OnFragmentInteractionListener {

    Context context;
    int moduleId;
    String moduleName;
    private final int REQUEST_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_capture);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        Intent intent = getIntent();
        moduleId = intent.getIntExtra(context.getString(R.string.module_id),0);
        moduleName = intent.getStringExtra(context.getString(R.string.module_name));

        init();
        setListeners();
    }

    private void init(){
        /*Initialize the fragment*/
        if(moduleId != 0) {
            //BasicResultFragment basicResultFragment = new BasicResultFragment();
            PollingFragment pollingFragment = new PollingFragment();
            //PollingFragment basicResultFragment = new PollingFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(context.getString(R.string.module_id), moduleId);
            bundle.putString(context.getString(R.string.module_name), moduleName);
            pollingFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_layout, pollingFragment, "Basic_Result_Fragment");
            //transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void setListeners(){

    }

    @Override
    public void onNextButtonClicked(String fragmentTag, Fragment fragment, Bundle bundle) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        transaction.replace(R.id.fragment_layout, fragment, fragmentTag);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onPreviousButtonClicked(String fragmentTag, Fragment fragment, Bundle bundle) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        transaction.replace(R.id.fragment_layout, fragment, fragmentTag);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Election Proof"),1);
                } else {
                    new AlertDialog.Builder(context)
                            .setIcon(R.drawable.ic_info_primary_24dp)
                            .setTitle(getString(R.string.application_info))
                            .setMessage(getString(R.string.storage_permission_info))
                            .show();
                }
                return;
            }
        }
    }
}
