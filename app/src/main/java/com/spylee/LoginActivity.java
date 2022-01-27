package com.spylee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.spylee.model.Person;

import java.util.List;


public class LoginActivity extends BackgroundActivity {
    public static final String MyPREFERENCES = "MyPrefs" ;
    EditText username,password;
    Person person;
    DatabaseReference databaseReference;
    SharedPreferences sharedpreferences;
    Button btn_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        databaseReference = DbRef.getDbRef().child("persons");
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if(sharedpreferences.getString("name",null)!= null){
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
            startActivity(intent);
        }
        btn_add=findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }
    void login() {
        if(TextUtils.isEmpty(username.getText().toString().trim()) || TextUtils.isEmpty(password.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Please Fill All Details", Toast.LENGTH_LONG).show();

        } else {
            databaseReference.orderByChild("username_password").equalTo(username.getText().toString().trim()+"_"+password.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int count = 0;
                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                        person = dataSnapshot1.getValue(Person.class);
                        count ++;
                    }
                    if(count > 0){
                        if(person.getStatus().equals("I Lost My Mobile")) {
                            Intent intent = new Intent(getApplicationContext(),LostModeActivity.class);
                            startActivity(intent);
                        } else {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString("id", person.getId());
                            editor.putString("name", person.getName());
                            editor.putString("phone", person.getPhone());
                            editor.putString("email", person.getEmail());
                            editor.commit();
                            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                            startActivity(intent);
                        }
                    }else {
                        Toast.makeText(getApplicationContext(), "Invalid Login Details", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
