package com.example.skchandran.avs;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.skchandran.avs.util.Network;
import com.example.skchandran.avs.util.NetworkURL;

import java.util.HashMap;
import java.util.Map;

public class AddMember extends AppCompatActivity implements View.OnClickListener{
    private EditText mUserName, mUniqueName, mMoileNo;
    Button register;

    private static String dbName = "name";
    private static String dbUniquename = "uniqueName";
    private static String dbMobile = "mobileNo";

    private String strName, strUniqueName, strMobileNo;

    private static String networkUrlForAddMember = NetworkURL.url+"addMember.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        mUserName = (EditText) findViewById(R.id.name);
        mUniqueName = (EditText) findViewById(R.id.uniqueUserName);
        mMoileNo = (EditText) findViewById(R.id.mobileNo);
        register = (Button) findViewById(R.id.registerBtn);


    }

    public void onClick(View v) {

        if (v.getId() == R.id.registerBtn) {

            strName = mUserName.getText().toString().trim();
            strUniqueName = mUniqueName.getText().toString().trim();
            strMobileNo = mMoileNo.getText().toString().trim();


            Network network = new Network();
            if (!network.isOnline(AddMember.this)) {
                Toast.makeText(AddMember.this, "No Network Connection", Toast.LENGTH_SHORT).show();
                return;
            }

            AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
            mAwesomeValidation.addValidation(AddMember.this, R.id.name, "[a-zA-Z .]+", R.string.err_name);


            if (strName.isEmpty()) {

                Snackbar.make(v, "Please enter Valid Name", Snackbar.LENGTH_LONG)
                        .show();
                return;
            }

            if (strUniqueName.isEmpty()) {

                Snackbar.make(v, "Please enter UserName", Snackbar.LENGTH_LONG)
                        .show();
                return;
            }
            if (strMobileNo.isEmpty()) {

                Snackbar.make(v, "Please enter the cMobile Number", Snackbar.LENGTH_LONG)
                        .show();
                return;
            }
            registerUser();

        }
    }
    int progressStatus=1;
    boolean isCanceled =false;
    public void registerUser()
    {

        final ProgressDialog loading =new ProgressDialog(AddMember.this);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setTitle("Please Wait..");
        loading.setMessage("Loading.........");
        loading.setIndeterminate(false);
        loading.setCancelable(false);

        loading.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener(){
            // Set a click listener for progress dialog cancel button
            @Override
            public void onClick(DialogInterface dialog, int which){
                progressStatus = 0;
                isCanceled = true;
                loading.dismiss();
                // Tell the system about cancellation
            }
        });

        loading.show();

        if(isCanceled) {
            progressStatus = 1;
            return;
        }
        StringRequest stringRequest=new StringRequest(Request.Method.POST, networkUrlForAddMember,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        loading.dismiss();

                        Toast.makeText(AddMember.this,response.split(";")[0],Toast.LENGTH_SHORT).show();
                        resetField();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        loading.dismiss();
                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                // Show timeout error message
                                Toast.makeText(getApplicationContext(), "Please Check Your Network Connection", Toast.LENGTH_LONG).show();

                            }
                        }
                        else
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                            resetField();
                    }
                })
        {
            @Override
            protected Map<String,String> getParams()
            {
                Map <String,String> params=new HashMap<String,String>();
                params.put(dbName,strName);
                params.put(dbUniquename,strUniqueName);
                params.put(dbMobile, strMobileNo);
                return params;
            }


        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue rq= Volley.newRequestQueue(this);
        rq.add(stringRequest);

}
    private void resetField()
    {
        mUserName.setText("");
        mUniqueName.setText("");
        mMoileNo.setText("");
    }


}
