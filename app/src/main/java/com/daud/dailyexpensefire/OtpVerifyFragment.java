package com.daud.dailyexpensefire;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OtpVerifyFragment extends Fragment {
    private TextInputEditText et1, et2, et3, et4, et5, et6;
    private MaterialButton verifyBtn;
    private TextView resendBtn, phoneTv;
    private FirebaseAuth firebaseAuth;
    private PhoneAuthCredential credential;
    private ProgressBar verifyProg;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String Phone,verificationId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_otp_verify, container, false);

        initial(view);

        //phoneTv.setText(Phone);

        Bundle bundle = getArguments();
        if (bundle!=null){
            Phone = bundle.getString("Phone");
            phoneTv.setText(Phone);
            verificationId = bundle.getString("verificationId");
        }

        editTexts();

        verifyBtn.setOnClickListener(view1 -> {
            verifyBtnOnClick();
        });

        resendBtn.setOnClickListener(view1 -> {
            resendBtnOnClick();
        });

        return view;
    }

    private void resendBtnOnClick() {

        AuthActivity.hideKeyboard(getActivity());

        verifyBtn.setVisibility(View.GONE);
        verifyProg.setVisibility(View.VISIBLE);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                verifyProg.setVisibility(View.GONE);
                verifyBtn.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d("onCodeSent", "onCodeSent:" + verificationId);
                verifyProg.setVisibility(View.GONE);
                verifyBtn.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "OTP ReSend to This Number", Toast.LENGTH_SHORT).show();
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(Phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyBtnOnClick() {

        AuthActivity.hideKeyboard(getActivity());

        if (et1.getText().toString().isEmpty()
                || et2.getText().toString().isEmpty()
                || et3.getText().toString().isEmpty()
                || et4.getText().toString().isEmpty()
                || et5.getText().toString().isEmpty()
                || et6.getText().toString().isEmpty()) {

            Toast.makeText(getContext(), "OTP ERROR", Toast.LENGTH_SHORT).show();
        } else {
            verifyBtn.setVisibility(View.GONE);
            verifyProg.setVisibility(View.VISIBLE);

            String OTP = et1.getText().toString() + et2.getText().toString()
                    + et3.getText().toString() + et4.getText().toString()
                    + et5.getText().toString() + et6.getText().toString();

            credential = PhoneAuthProvider
                    .getCredential(verificationId,OTP);

            firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Verification Successful", Toast.LENGTH_SHORT).show();
                        verifyProg.setVisibility(View.GONE);
                        verifyBtn.setVisibility(View.VISIBLE);
                        showAlertDialog();
                    } else {
                        verifyProg.setVisibility(View.GONE);
                        verifyBtn.setVisibility(View.VISIBLE);

                        Toast.makeText(getContext(), task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showAlertDialog() {

        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.name_input_lay, null);
        TextView headTv = view.findViewById(R.id.headTv);
        TextInputEditText nameEt = view.findViewById(R.id.nameEt);
        MaterialButton saveBtn = view.findViewById(R.id.saveBtn);
        dialog.setView(view);

        nameEt.requestFocus();

        saveBtn.setOnClickListener(view1 -> {

            if (nameEt.getText().toString().isEmpty()) {
                nameEt.setError("Empty Name");
                nameEt.requestFocus();
                return;
            }

            if (Phone != null && !Phone.isEmpty()) {

                AuthActivity.editor.putInt("STATE", 1);
                AuthActivity.editor.putString("Name",nameEt.getText().toString());
                AuthActivity.editor.putString("Phone",Phone);
                AuthActivity.editor.commit();

                dialog.dismiss();

                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();

            }else{
                Toast.makeText(getContext(), "Phone Number Error", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void editTexts() {
        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                et2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                et3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                et4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                et5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        et5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                et6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void initial(View view) {
        et1 = view.findViewById(R.id.et1);
        et2 = view.findViewById(R.id.et2);
        et3 = view.findViewById(R.id.et3);
        et4 = view.findViewById(R.id.et4);
        et5 = view.findViewById(R.id.et5);
        et6 = view.findViewById(R.id.et6);
        verifyBtn = view.findViewById(R.id.verifyBtn);
        resendBtn = view.findViewById(R.id.resendBtn);
        phoneTv = view.findViewById(R.id.phoneTv);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
        verifyProg = view.findViewById(R.id.verifyProg);
        Phone = AuthActivity.sharedPreferences.getString("Phone", "");
    }
}