package com.daud.dailyexpensefire;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class OtpSendFragment extends Fragment {
    private Spinner spinner;
    private TextInputEditText phoneEt;
    private AutoCompleteTextView countryEt;
    private Button sendBtn;
    private String[] cCodes = {"+880","+91","+86","+1","+44","+92","+966","+65","+971"};
    private FirebaseAuth firebaseAuth;
    private ProgressBar sendProg;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_otp_send, container, false);

        initialize(view);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,cCodes);
        countryEt.setAdapter(adapter);

        countryEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String cCode = charSequence.toString();
                if (cCode.length()==4){
                    phoneEt.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        sendBtn.setOnClickListener(view1 -> {
            sendBtnClick();
        });

        return view;
    }


    private void sendBtnClick(){

        AuthActivity.hideKeyboard(getActivity());

        if (countryEt.getText().toString().trim().isEmpty()){
            countryEt.setError("Error");
            countryEt.requestFocus();
            return;
        }
        if (phoneEt.getText().toString().trim().isEmpty()){
            phoneEt.setError("Invalid Phone");
            phoneEt.requestFocus();
            return;
        }
        sendBtn.setVisibility(View.GONE);
        sendProg.setVisibility(View.VISIBLE);
        otpSend();
    }

    private void otpSend() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) { }
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                sendProg.setVisibility(View.GONE);
                sendBtn.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(),e.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d("onCodeSent","onCodeSent:" + verificationId);
                sendProg.setVisibility(View.GONE);
                sendBtn.setVisibility(View.VISIBLE);

                Bundle bundle = new Bundle();
                bundle.putString("verificationId",verificationId);
                bundle.putString("Phone",countryEt.getText().toString()
                        .trim()+phoneEt.getText().toString().trim());
                OtpVerifyFragment otpVerifyFragment = new OtpVerifyFragment();
                otpVerifyFragment.setArguments(bundle);

                Toast.makeText(getContext(),"OTP Send to This Number",Toast.LENGTH_SHORT).show();
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.slide_out_left_to_right)
                        .replace(R.id.authFragment,otpVerifyFragment).commit();
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(countryEt.getText().toString().trim()+phoneEt.getText().toString().trim())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void initialize(View view) {
        countryEt = view.findViewById(R.id.countryEt);
        phoneEt = view.findViewById(R.id.phoneEt);
        sendBtn = view.findViewById(R.id.sendBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        sendProg = view.findViewById(R.id.sendProg);
    }
}