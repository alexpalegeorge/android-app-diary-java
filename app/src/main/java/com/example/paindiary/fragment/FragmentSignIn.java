package com.example.paindiary.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiary.LoginActivity;
import com.example.paindiary.MainActivity;
import com.example.paindiary.SharedViewModel;
import com.example.paindiary.databinding.FragmentSignInBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FragmentSignIn extends Fragment {

    private FragmentSignInBinding signInBinding;
    private FirebaseAuth auth;

    private CharSequence toast_text = "";

    public FragmentSignIn(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the view for Sign In fragment
        signInBinding = FragmentSignInBinding.inflate(inflater, container, false);
        View view = signInBinding.getRoot();

        // Pre-fills the username field with the registered email address
        SharedViewModel model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        model.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                signInBinding.username.setText(s);
            }
        });

        auth = FirebaseAuth.getInstance();

        // Sign In button listener that verifies the username and password fields, and takes the
        // user to the main activity if successfully verified with Firebase
        signInBinding.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signInBinding.username.getText().toString();
                String password = signInBinding.password.getText().toString();

                if (((LoginActivity) getActivity()).checkValidFields(email, password)) {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            toast_text = e.getMessage();
                                            Toast.makeText(getContext(), toast_text,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                            )
                            .addOnSuccessListener(
                                    new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            toast_text = "Successfully signed in user";
                                            Toast.makeText(getContext(), toast_text,
                                                    Toast.LENGTH_LONG).show();
                                            // Get Firebase user
                                            FirebaseUser user = auth.getCurrentUser();
                                            // Start main activity with logged in user
                                            Intent intent = new Intent(getActivity(),
                                                    MainActivity.class);
                                            intent.putExtra("userParcel", user);
                                            startActivity(intent);
                                        }
                                    }
                            );
                }
            }
        });

        // Register button listener that calls login activity to delete this fragment
        // and replace it with the register fragment
        signInBinding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).replaceFragment(new FragmentRegister());
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        signInBinding = null;
    }

}
