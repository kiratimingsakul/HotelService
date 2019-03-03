package com.example.administrator.hotelservice.Input_event;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.hotelservice.R;
import com.example.administrator.hotelservice.calendar.Calendar_Fragment;
import com.example.administrator.hotelservice.login.Login;
import com.example.administrator.hotelservice.splash.Splash_Screen;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Input_Event_Fragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    View view;
    TextView lat, lon;
    private Calendar calendar;
    private TextView dateView, dateView2;
    Button save_data;
    Bitmap FixBitmap;
    EditText text_title, text_Address, text_description, wed, tel, ex;
    ImageView chackIn;
    JSONObject response;
    String idfacebook;
    String facebook = "";
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private ImageView imageView, datepick;
    String sledate, sledate2;

    private int year, month, day;
    ProgressDialog progressDialog;

    ByteArrayOutputStream byteArrayOutputStream;

    byte[] byteArray;

    String ConvertImage;

    HttpURLConnection httpURLConnection;

    java.net.URL url;

    OutputStream outputStream;

    BufferedWriter bufferedWriter;

    int RC;

    BufferedReader bufferedReader;

    StringBuilder stringBuilder;

    boolean check = true;

    private int GALLERY = 1, CAMERA = 2;


    String title, Address, description, province, latitude, longitude, status, website, contact, note;
    private static final String URL = "http://bnmsgps.hostingerapp.com/event/insert_image.php";
    Spinner spinner, sta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requestStoragePermission();

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_input_event, container, false);
        byteArrayOutputStream = new ByteArrayOutputStream();
        spinner = view.findViewById(R.id.spin_data);
        sta = view.findViewById(R.id.spin_status);
        save_data = view.findViewById(R.id.save_data);
        dateView2 = view.findViewById(R.id.date_view);
        dateView = view.findViewById(R.id.date2);
        imageView = view.findViewById(R.id.add_image);
        datepick = view.findViewById(R.id.date_pick);
        ex = view.findViewById(R.id.ex);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        setHasOptionsMenu(true);
        onBindView();


        Bundle bundle = this.getArguments();
        idfacebook = bundle.getString("idface");

        try {
            response = new JSONObject(idfacebook);
            facebook = response.get("id").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }


        datepick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final DatePicker picker = new DatePicker(getActivity());
                picker.setCalendarViewShown(false);
                builder.setView(picker);
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        year = picker.getYear();
                        month = picker.getMonth() + 1;
                        day = picker.getDayOfMonth();
                        sledate = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);
                        sledate2 = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);
                        dateView2.setText(sledate);
                        dateView.setText(sledate2);
                    }
                });
                builder.show();
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        chackIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });


        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();


        save_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditText();

                if (!ConvertImage.isEmpty()){
                    if (!title.isEmpty() && !Address.isEmpty() && !description.isEmpty() && !province.isEmpty() && !latitude.isEmpty() && !longitude.isEmpty()
                            && !contact.isEmpty() && !sledate.isEmpty()) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setMessage("คุณต้องการบันทึกข้อมูลใช่หรือไม่");
                        alert.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                UploadImageToServer();
                            }
                        });
                        alert.setNegativeButton("ไม่ใช่", null);
                        alert.show();
                    }else {
                        Toast.makeText(getActivity(),"กรุณากรอกข้อมูลให้ครบถ้วน",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getActivity(),"กรุณาใส่รูปภาพ",Toast.LENGTH_LONG).show();
                }



            }
        });

        return view;

    }

    private void showPictureDialog() {
        android.support.v7.app.AlertDialog.Builder pictureDialog = new android.support.v7.app.AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Photo Gallery",
                "Camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    public void UploadImageToServer() {

        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);

        byteArray = byteArrayOutputStream.toByteArray();

        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(getActivity(), "Image is Uploading", "Please Wait", false, false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                progressDialog.dismiss();

                if(string1.equals("Your Image Has Been Uploaded.")){

                Toast.makeText(getActivity(), "บันทึกสำเร็จ", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), Login.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();

                }


            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();


                HashMap<String, String> HashMapParams = new HashMap<String, String>();

                HashMapParams.put("image", ConvertImage);

                HashMapParams.put("title", title);

                HashMapParams.put("event_Address", Address);

                HashMapParams.put("event_description", description);

                HashMapParams.put("event_province", province);

                HashMapParams.put("event_latitude", latitude);

                HashMapParams.put("event_longitude", longitude);

                HashMapParams.put("event_status", status);

                HashMapParams.put("event_wed", website);

                HashMapParams.put("event_tel", contact);

                HashMapParams.put("event_date", sledate2);

                HashMapParams.put("event_note", note);

                HashMapParams.put("user_idfacebook", facebook);


                String FinalData = imageProcessClass.ImageHttpRequest("http://bnmsgps.hostingerapp.com/event/insert_image.php", HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass {

        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(requestURL);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(20000);

                httpURLConnection.setConnectTimeout(20000);

                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);

                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();

                bufferedWriter = new BufferedWriter(

                        new OutputStreamWriter(outputStream, "UTF-8"));

                bufferedWriter.write(bufferedWriterDataFN(PData));

                bufferedWriter.flush();

                bufferedWriter.close();

                outputStream.close();

                RC = httpURLConnection.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReader.readLine()) != null) {

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            stringBuilder = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilder.append("=");

                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilder.toString();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                if (place != null) {
                    String latitudemap = String.valueOf(place.getLatLng().latitude);
                    String longitudemap = String.valueOf(place.getLatLng().longitude);
                    lat.setText(latitudemap);
                    lon.setText(longitudemap);
                }


            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();

                if (contentURI != null) {
                    try {
                        FixBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                        // String path = saveImage(bitmap);
                        //Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                        imageView.setImageBitmap(FixBitmap);


                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }

            }

        } else if (requestCode == CAMERA) {
            FixBitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(FixBitmap);
        }


    }


    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(getActivity(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(getActivity(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void onBindView() {

        text_title = view.findViewById(R.id.addTitle);
        text_Address = view.findViewById(R.id.addAddress);
        text_description = view.findViewById(R.id.add_description);
        lat = view.findViewById(R.id.lat);
        lon = view.findViewById(R.id.lon);
        chackIn = view.findViewById(R.id.chack_in);
        wed = view.findViewById(R.id.wed);
        tel = view.findViewById(R.id.tel);
        sta = view.findViewById(R.id.spin_status);

    }

    private void onEditText() {
        title = text_title.getText().toString();
        Address = text_Address.getText().toString();
        description = text_description.getText().toString();
        latitude = lat.getText().toString();
        longitude = lon.getText().toString();
        website = wed.getText().toString();
        contact = tel.getText().toString();
        province = spinner.getSelectedItem().toString();
        status = sta.getSelectedItem().toString();
        note = ex.getText().toString();


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(chackIn, connectionResult.getErrorMessage() + "", Snackbar.LENGTH_LONG).show();
    }


}
