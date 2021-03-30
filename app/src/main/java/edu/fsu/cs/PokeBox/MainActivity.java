package edu.fsu.cs.PokeBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;
import java.util.Vector;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    public static String CURRENT_USER;

    // list of icons used for page navigation bar
    private final int[] icons = {
            R.drawable.camera_tab_icon,
            R.drawable.cards_tab_icon,
            R.drawable.watchlist_tab_icon,
            R.drawable.settings_tab_icon
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();           //for user authentication

        CURRENT_USER = "NONE";


        //if the user is not logged in, show login page.
        if(mAuth.getCurrentUser() == null){
            setContentView(R.layout.login);

            Button login = (Button) findViewById(R.id.loginbtn);

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText eemail = (EditText) findViewById(R.id.email);
                    EditText epassword = (EditText) findViewById(R.id.password);
                    String email =  eemail.getText().toString();
                    String password =  epassword.getText().toString();
                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                    } else {
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if (!task.isSuccessful())
                                    {
                                        try
                                        {
                                            throw task.getException();
                                        }
                                        // if user enters wrong email.
                                        catch (FirebaseAuthInvalidUserException invalidEmail)
                                        {
                                            Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_LONG).show();
                                        }
                                        // if user enters wrong password.
                                        catch (FirebaseAuthInvalidCredentialsException wrongPassword)
                                        {
                                            Toast.makeText(getApplicationContext(), "Incorrect password", Toast.LENGTH_LONG).show();
                                        }
                                        catch (Exception e)
                                        {
                                            Toast.makeText(getApplicationContext(), "Error signing in: " + e, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "Successfully signed in", Toast.LENGTH_LONG).show();
                                        //update ui
                                        load();
                                    }
                                }
                            }
                        );
                    }
                }
            });


            //clicking on register button takes user to the registration page
            Button register = (Button) findViewById(R.id.registerbtn);
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(MainActivity.this, Register.class);
                    startActivity(myIntent);
                }});

        }
        else{
            //load the main activity
            load();
        }


    }

    //loads activity main
    public void load(){
        setContentView(R.layout.activity_main);
        ViewPager2 viewPager = findViewById(R.id.pager);
        TabLayout tabLayout = findViewById(R.id.navigation_tabs);

        CURRENT_USER = mAuth.getCurrentUser().getUid();

        // setup view pager
        viewPager.setAdapter(new ViewPagerFragmentAdaptor(this));

        // attaching tab mediator
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setIcon(icons[position])).attach();
    }


    // Fragment Adaptor class for View Pager
    private class ViewPagerFragmentAdaptor extends FragmentStateAdapter {

        public ViewPagerFragmentAdaptor(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new ScannerFragment();
                case 1:
                    return new CardViewerFragment();
                case 2:
                    return new WatchlistFragment();
                case 3:
                    return new SettingsFragment();
                default:
                    return new CardViewerFragment();
            }
        }

        @Override
        public int getItemCount() {
            return icons.length;
        }
    }
}