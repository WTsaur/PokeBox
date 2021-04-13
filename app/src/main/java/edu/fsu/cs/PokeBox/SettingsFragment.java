package edu.fsu.cs.PokeBox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {
    private View view;
    private FirebaseAuth mAuth;
    private EditText eoldp, enewp, econfirmp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        mAuth = FirebaseAuth.getInstance();

        //display email in settings fragment
        TextView eemail = view.findViewById(R.id.displayemail);
        if(mAuth.getCurrentUser() != null){
            eemail.setText(mAuth.getCurrentUser().getEmail());

        }

        Button changepassword = view.findViewById(R.id.changepassword);
        changepassword.setOnClickListener(v -> {
            //getting the old and new password from edittext
            eoldp = view.findViewById(R.id.oldp);
            enewp = view.findViewById(R.id.newp);
            econfirmp = view.findViewById(R.id.newcp);
            String oldpass = eoldp.getText().toString();
            String newpass = enewp.getText().toString();
            String confirmpass = econfirmp.getText().toString();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(!oldpass.isEmpty() && !newpass.isEmpty() && !confirmpass.isEmpty()){
                AuthCredential credential = EmailAuthProvider.getCredential(mAuth.getCurrentUser().getEmail(), oldpass);
                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                if (newpass.equals(confirmpass)) {
                                    user.updatePassword(newpass)
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Password Changed", Toast.LENGTH_SHORT).show();
                                                }
                                                else  Toast.makeText(getContext(), "There was a problem", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Toast.makeText(getContext(), "New password does not meet confirm password", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(getContext(), "Old password incorrect", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else Toast.makeText(getContext(), "Missing information", Toast.LENGTH_SHORT).show();
        });

        //user is able to logout
        Button logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(v -> {
            try{
                mAuth.signOut();
                Toast.makeText(getContext(), "Successfully logged out", Toast.LENGTH_SHORT).show();
                //update ui
                Intent myIntent = new Intent(getContext(), MainActivity.class);
                startActivity(myIntent);
            }
            catch (Exception e){
                Toast.makeText(getContext(), "Issues logging out: " + e, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
