package edu.fsu.cs.PokeBox;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {
    View view;
    private FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button logout = (Button) view.findViewById(R.id.logout);
        mAuth = FirebaseAuth.getInstance();
        logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try{
                    mAuth.signOut();
                    Toast.makeText(getContext(), "Successfully logged out", Toast.LENGTH_LONG).show();
                    //update ui
                    Intent myIntent = new Intent(getContext(), MainActivity.class);
                    startActivity(myIntent);
                }
                catch (Exception e){
                    Toast.makeText(getContext(), "Issues logging out: " + e, Toast.LENGTH_LONG).show();
                }
            }
        });


        return view;
    }
}
