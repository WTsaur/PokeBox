package edu.fsu.cs.PokeBox;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Register extends MainActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.register);
       Button login = findViewById(R.id.lbtn);
       Button register = findViewById(R.id.rbtn);

       mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(v -> {
            final EditText eemail = findViewById(R.id.remail);
            final EditText epassword = findViewById(R.id.rpassword);
            final EditText ecpassword = findViewById(R.id.cpassword);

            String email =  eemail.getText().toString();
            String password =  epassword.getText().toString();
            String cpassword = ecpassword.getText().toString();

            //checking to see if email, password, and confirm password have been typed in
            if(!email.isEmpty() && !password.isEmpty() && !cpassword.isEmpty()) {
                //checking to see if password matches confirm password
                if(password.equals(cpassword)) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                            task -> {
                                if (!task.isSuccessful())
                                {
                                    try
                                    {
                                        throw Objects.requireNonNull(task.getException());
                                    }
                                    // if user enters weak password
                                    catch (FirebaseAuthWeakPasswordException weakPassword)
                                    {
                                        Toast.makeText(Register.this, "Weak password", Toast.LENGTH_LONG).show();
                                    }

                                    // if user enters wrong email.
                                    catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                                    {
                                        Toast.makeText(Register.this, "Invalid Email", Toast.LENGTH_LONG).show();
                                    }
                                    // if user enters existing email.
                                    catch (FirebaseAuthUserCollisionException existEmail)
                                    {
                                        Toast.makeText(Register.this, "Email already exists" , Toast.LENGTH_LONG).show();
                                    }
                                    catch (Exception e)
                                    {
                                        Toast.makeText(Register.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                                else {
                                    Toast.makeText(Register.this, "Successfully registered", Toast.LENGTH_LONG).show();
                                    load(); //update ui
                                }
                            }
                    );
                }
                else Toast.makeText(Register.this, "Password does not match confirm password" , Toast.LENGTH_LONG).show();
            }
            else  Toast.makeText(Register.this, "Please fill in all fields" , Toast.LENGTH_LONG).show();
        });

       //clicking on login button
        login.setOnClickListener(v -> {
            Intent myIntent = new Intent(Register.this, MainActivity.class);
            startActivity(myIntent);
        });


    }
}
