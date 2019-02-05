package jbtechventures.com.rtma;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.satsuware.usefulviews.LabelledSpinner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;

import jbtechventures.com.rtma.Model.Complaint;
import jbtechventures.com.rtma.Model.Lga;
import jbtechventures.com.rtma.Repository.ComplaintRepository;
import jbtechventures.com.rtma.Repository.PersonRepository;
import jbtechventures.com.rtma.Service.PostService;
import jbtechventures.com.rtma.Session.SessionManager;
import jbtechventures.com.rtma.Utility.BindingMeths;

public class ComplaintActivity extends SessionManager {

    Context context;
    String proofImagePath = "";
    ImageButton photoAttach;
    ImageView imageView;
    LabelledSpinner lgaSpinner, wardSpinner, puSpinner;
    EditText message, title;
    TextView electionDisplayName;
    int moduleId;
    String moduleName;
    private final int REQUEST_EXTERNAL_STORAGE = 1;
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Button saveButton;
    ComplaintRepository complaintRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        Intent intent = getIntent();
        moduleId = intent.getIntExtra(context.getString(R.string.module_id),0);
        moduleName = intent.getStringExtra(context.getString(R.string.module_name));

        init();
        setListeners();
    }

    private void init(){
        lgaSpinner = findViewById(R.id.lga_spinner);
        wardSpinner = findViewById(R.id.ward_spinner);
        puSpinner = findViewById(R.id.pu_spinner);
        photoAttach = findViewById(R.id.photoAttach);
        imageView = findViewById(R.id.imageView);
        title = findViewById(R.id.activity_title);
        message = findViewById(R.id.message);
        saveButton = findViewById(R.id.save_complaint);
        electionDisplayName = findViewById(R.id.module_id);

        /*initial values*/
        BindingMeths bindingMeths = new BindingMeths(context);
        bindingMeths.bindLga(lgaSpinner);
        electionDisplayName.setText(moduleName);

        complaintRepository = new ComplaintRepository(context);
    }

    private void setListeners(){
        photoAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyStoragePermissions((Activity) context);
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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    new AlertDialog.Builder(context)
                            .setTitle("Complaint Submission")
                            .setMessage("Are you sure you want to submit this complain")
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
                                    Complaint complaint = new Complaint();
                                    complaint.PollingUnit = ((Lga)puSpinner.getSpinner().getSelectedItem()).Code;
                                    complaint.Message = message.getText().toString().trim();
                                    complaint.Title = title.getText().toString().trim();
                                    complaint.ElectionId = moduleId;
                                    complaint.ImagePath = proofImagePath;
                                    complaint.UserId = new PersonRepository(context).getPersonCurrentlyLoggedIn().UserId;
                                    complaint.Synced = 0;
                                    complaint.Date = new Date().toString();

                                    int added = complaintRepository.addComplain(complaint);
                                    if(added > 0){
                                        PostService.startActionPostComplaint(context);
                                        LayoutInflater inflater = getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout_root));

                                        TextView text = (TextView) layout.findViewById(R.id.toast_text);
                                        text.setText("Complaint saved successfully");

                                        Toast toast = new Toast(context);
                                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                        toast.setDuration(Toast.LENGTH_LONG);
                                        toast.setView(layout);
                                        toast.show();

                                        Intent intent = new Intent(context, ElectionActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        LayoutInflater inflater = getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout_root));

                                        TextView text = (TextView) layout.findViewById(R.id.toast_text);
                                        text.setText("There was an error saving the complain");

                                        Toast toast = new Toast(context);
                                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                        toast.setDuration(Toast.LENGTH_LONG);
                                        toast.setView(layout);
                                        toast.show();
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
    }

    private boolean validate(){
        if(puSpinner.getSpinner().getSelectedItemPosition() == 0){
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText("The polling unit is required");

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            //Toast.makeText(context, "All fields are required", Toast.LENGTH_LONG).show();
            return false;
        }
        if(message.getText().toString().trim().equals("")){
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup)findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText("the message field is required");

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            return false;
        }
        if(title.getText().toString().trim().equals("")){
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup)findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText("the title field is required");

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            return false;
        }
        return true;
    }

    public void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
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
            File directory = cw.getDir("complaintsImages", Context.MODE_PRIVATE);
            if (!directory.exists()) {
                directory.mkdir();
            }
            File mypath = new File(directory, String.valueOf(puSpinner.getSpinner().getSelectedItem().toString()) + "_image.jpg");

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
        Cursor cursor = this.managedQuery(contentUri, proj, null, null, null);
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
}
