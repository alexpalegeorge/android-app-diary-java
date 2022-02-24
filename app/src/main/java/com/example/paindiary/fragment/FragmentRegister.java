package com.example.paindiary.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiary.LoginActivity;
import com.example.paindiary.SharedViewModel;
import com.example.paindiary.databinding.FragmentRegisterBinding;
import com.example.paindiary.fragment.FragmentSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentRegister extends Fragment {

    private FragmentRegisterBinding registerBinding;
    private FirebaseAuth auth;

    private CharSequence toast_text = "";

    public FragmentRegister(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the view for Register fragment
        registerBinding = FragmentRegisterBinding.inflate(inflater, container, false);
        View view = registerBinding.getRoot();

        SharedViewModel model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        auth = FirebaseAuth.getInstance();

        // Register button listener that authorizes a new user through Firebase
        registerBinding.buttonCompleteRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = registerBinding.username.getText().toString();
                String password = registerBinding.password.getText().toString();
                // Field validation, will not attempt to authorize with Firebase until fields
                // are valid. If successful, will destroy register fragment and go to sign in
                // fragment.
                if (((LoginActivity) getActivity()).checkValidFields(email, password)) {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        toast_text = "Successfully registered email address";
                                        Toast.makeText(getContext(), toast_text,
                                                Toast.LENGTH_LONG).show();
                                        model.setMessage(email);
                                        ((LoginActivity) getActivity()).replaceFragment(
                                                new FragmentSignIn());
                                    } else {
                                        toast_text = "Failed to register email address";
                                        Toast.makeText(getContext(), toast_text,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                    );
                }
            }
        });

        // Back to sign in button listener that calls login activity to delete this fragment
        // and replace it with the sign in fragment
        registerBinding.buttonBackToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).replaceFragment(new FragmentSignIn());
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        registerBinding = null;
    }

}

