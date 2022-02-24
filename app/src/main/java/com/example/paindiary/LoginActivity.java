package com.example.paindiary;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.paindiary.databinding.ActivityLoginBinding;
import com.example.paindiary.fragment.FragmentSignIn;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        // Display the Sign In fragment when the Login activity starts
        replaceFragment(new FragmentSignIn());
    }

    // Public method that the Login activity's fragments can call to replace themselves
    public void replaceFragment(Fragment nextFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.login_container_view, nextFragment);
        fragmentTransaction.commit();
    }

    public Boolean checkValidFields(String email, String password) {
        Boolean valid_flag;
        String toast_text = "";
        if (email.isEmpty() && password.isEmpty()) {
            toast_text = "Enter a username and password";
            valid_flag = false;
        }
        else if (email.isEmpty()) {
            toast_text = "Enter a username";
            valid_flag = false;
        }
        else if (password.isEmpty()) {
            toast_text = "Enter a password";
            valid_flag = false;
        }
        else if (!email.contains("@")) {
            toast_text = "Enter a valid email address";
            valid_flag = false;
        }
        else {
            valid_flag = true;
        }
        if (!valid_flag) {
            Toast.makeText(getApplicationContext(), toast_text, Toast.LENGTH_LONG).show();
        }
        return valid_flag;
    }

}
