package com.spylee;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.spylee.model.Person;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends BackgroundActivity {
    EditText name,username,password,email,phone;
    Person person;
    List<Person> personList;
    DatabaseReference databaseReference;
    Button btn_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = (EditText)findViewById(R.id.name);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        email = (EditText)findViewById(R.id.email);
        phone = (EditText)findViewById(R.id.phone);
        person = new Person();
        personList = new ArrayList<Person>();
        databaseReference = DbRef.getDbRef().child("persons");
        btn_add=findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }
    void register() {

        person = new Person();
        person.setId(databaseReference.push().getKey());
        person.setName(name.getText().toString().trim());
        person.setUsername(username.getText().toString().trim());
        person.setPassword(password.getText().toString().trim());
        person.setEmail(email.getText().toString().trim());
        person.setPhone(phone.getText().toString().trim());
        person.setStatus("Available");
        person.setUsername_password(person.getUsername()+"_"+person.getPassword());
        person.setUsername_email_phone(person.getUsername()+"_"+person.getEmail()+"_"+person.getPhone());

        if(TextUtils.isEmpty(person.getName()) || TextUtils.isEmpty(person.getUsername()) || TextUtils.isEmpty(person.getPassword()) || TextUtils.isEmpty(person.getEmail()) || TextUtils.isEmpty(person.getPhone())) {
            Toast.makeText(getApplicationContext(), "Please Fill All Details", Toast.LENGTH_LONG).show();
        } else {
            try {
                databaseReference.orderByChild("username").equalTo(person.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int count =0;
                        for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                            count ++;
                            Person person1 = dataSnapshot1.getValue(Person.class);

                        }
                        if(count >0 ) {
                            Toast.makeText(getApplicationContext(), "Duplicate User Name", Toast.LENGTH_LONG).show();
                        } else {
                            databaseReference.orderByChild("email").equalTo(person.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int count2 =0;
                                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                                        count2 ++;
                                        Person person1 = dataSnapshot1.getValue(Person.class);

                                    }
                                    if(count2 >0) {
                                        Toast.makeText(getApplicationContext(), "Duplicate Email", Toast.LENGTH_LONG).show();
                                    } else {
                                        databaseReference.orderByChild("phone").equalTo(person.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                int count3 =0;
                                                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                                                    count3 ++;
                                                    Person person1 = dataSnapshot1.getValue(Person.class);

                                                }
                                                if(count3> 0) {
                                                    Toast.makeText(getApplicationContext(), "Duplicate Phone Number", Toast.LENGTH_LONG).show();
                                                } else {
                                                    databaseReference.child(person.getId()).setValue(person).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getApplicationContext(), "User Added Successfully", Toast.LENGTH_LONG).show();
                                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("error",e.toString());
            }
        }
    }

}
