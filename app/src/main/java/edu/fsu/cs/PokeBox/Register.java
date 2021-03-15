package edu.fsu.cs.PokeBox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class Register extends MainActivity {
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.register);
       Button login = (Button) findViewById(R.id.lbtn);
       Button register = (Button) findViewById(R.id.rbtn);

       mAuth = FirebaseAuth.getInstance();


        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                final EditText eemail = (EditText) findViewById(R.id.remail);
                final EditText epassword = (EditText) findViewById(R.id.rpassword);
                final EditText ecpassword = (EditText) findViewById(R.id.cpassword);

                String email =  eemail.getText().toString();
                String password =  epassword.getText().toString();
                String cpassword = ecpassword.getText().toString();

                //checking to see if email has been typed in
                if(email != null) {
                    //checking to see if password matches confirm password
                    if(password.equals(cpassword)) {

                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                                    new OnCompleteListener<AuthResult>(){
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task)
                                        {
                                            if (!task.isSuccessful())
                                            {
                                                try
                                                {
                                                    throw task.getException();
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
                                    }
                            );

                    }
                    else Toast.makeText(Register.this, "Password does not match confirm password" , Toast.LENGTH_LONG).show();
                }
                else  Toast.makeText(Register.this, "Email is blank" , Toast.LENGTH_LONG).show();
            }
        });


       //clicking on login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Register.this, MainActivity.class);
                startActivity(myIntent);
            }
        });


    }
}
