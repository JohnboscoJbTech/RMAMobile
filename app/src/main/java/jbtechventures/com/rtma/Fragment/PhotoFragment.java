package jbtechventures.com.rtma.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import jbtechventures.com.rtma.Model.Result;
import jbtechventures.com.rtma.R;
import jbtechventures.com.rtma.Repository.ResultRepository;

public class PhotoFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    Context context;
    String proofImagePath = "";
    ImageButton photoAttach;
    ImageView imageView;
    private final int REQUEST_EXTERNAL_STORAGE = 1;
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private int formId, resultId;
    private String pollingUnitCode;
    String electionName;
    Button previousButton, saveButton;
    private final String PREVIOUS_FRAGMENT = "PARTY_FRAGMENT";
    private final String NEXT_FRAGMENT = "RESULT_SUMMARY_FRAGMENT";
    TextView electionDisplayName;
    ResultRepository resultRepository;
    Result result;

    public PhotoFragment() {
        // Required empty public constructor
    }

    public static PhotoFragment newInstance() {
        PhotoFragment fragment = new PhotoFragment();
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
            pollingUnitCode = bundle.getString(context.getString(R.string.polling_unit_name));
            resultId = bundle.getInt(context.getString(R.string.result_id));
            if(formId != 0)
                result = resultRepository.getResult(formId, pollingUnitCode);
            electionName = bundle.getString(context.getString(R.string.module_name));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
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

    private void assignView(Result result){
        if(result.ProofImagePath != null && !result.ProofImagePath.equals(""))
            imageView.setImageURI(Uri.parse(result.ProofImagePath));
    }

    private void init(View view){
        photoAttach = view.findViewById(R.id.photoAttach);
        imageView = view.findViewById(R.id.imageView);
        previousButton = view.findViewById(R.id.previous_btn);
        electionDisplayName = view.findViewById(R.id.module_id);
        saveButton = view.findViewById(R.id.save_results);

        /*Default values*/
        electionDisplayName.setText(electionName);
    }

    private void setListeners(){
        photoAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyStoragePermissions(getActivity());
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(context.getString(R.string.module_id), formId);
                bundle.putString(context.getString(R.string.module_name), electionName);
                bundle.putInt(context.getString(R.string.result_id), resultId);
                bundle.putString(context.getString(R.string.polling_unit_name), pollingUnitCode);
                PartyFragment partyFragment = PartyFragment.newInstance();
                mListener.onPreviousButtonClicked(PREVIOUS_FRAGMENT,partyFragment,bundle);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageView.getDrawable() == null){
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) view.findViewById(R.id.toast_layout_root));

                    TextView text = (TextView) layout.findViewById(R.id.toast_text);
                    text.setText("Please attach the picture proof of the result");

                    Toast toast = new Toast(context);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                    return;
                }
                if(!proofImagePath.equals(""))
                    result.ProofImagePath = proofImagePath;
                resultRepository.addResult(result);
                Bundle bundle = new Bundle();
                bundle.putInt(context.getString(R.string.module_id), formId);
                bundle.putString(context.getString(R.string.module_name), electionName);
                bundle.putInt(context.getString(R.string.result_id), resultId);
                bundle.putString(context.getString(R.string.polling_unit_name), pollingUnitCode);
                ResultSummaryFragment resultSummaryFragment = ResultSummaryFragment.newInstance();
                mListener.onNextButtonClicked(NEXT_FRAGMENT,resultSummaryFragment,bundle);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null)
        {
            Uri uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //String imagePath = getRealPathFromURI(uri);
            proofImagePath = getRealPathFromURI(uri);

            /*ContextWrapper cw = new ContextWrapper(context);
            File directory =  cw.getDir("proofImages", Context.MODE_PRIVATE);

            *//*if (!directory.exists()) {
                directory.mkdir();
            }*//*
            File mypath = new File(directory, String.valueOf(pollingUnitCode) + "_image.jpg");

            try {
                mypath.createNewFile();
                copyFile(new File(imagePath), mypath);
                proofImagePath = mypath.toString();
            } catch (Exception e) {
                String ex = e.getMessage();
            }*/
        }
    }

    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Video.Media.DATA };
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }

    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        /*Use this to get you result in the fragment*/
        //int permission = checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            /*Use this to have the dialog on the activity*/
            /*requestPermissions(
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );*/
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Election Proof"), 1);
        }
    }

    public interface OnFragmentInteractionListener {
        void onPreviousButtonClicked(String fragmentTag, Fragment fragment, Bundle bundle);
        void onNextButtonClicked(String fragmentTag, Fragment fragment, Bundle bundle);
    }
}
