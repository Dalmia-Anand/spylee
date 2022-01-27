package com.spylee;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.spylee.model.Guardian;
import com.spylee.model.Person;

import java.util.ArrayList;
import java.util.List;


public class Trace extends Fragment {

    public static final String MyPREFERENCES = "MyPrefs" ;
    EditText username,password;
    Spinner spinner;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    SharedPreferences sharedpreferences;
    Guardian guardian;
    List<Guardian> guardians;
    Person person;
    List<Person> persons;
    List<String> ids ;
    List<String> names ;
    Button trace;
    Person person3;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trace, container, false);
        databaseReference = DbRef.getDbRef().child("guardians");
        databaseReference2 = DbRef.getDbRef().child("persons");
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        spinner = (Spinner)view.findViewById(R.id.spinner);
        trace = (Button)view.findViewById(R.id.trace);
        trace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(names.size() == 0) {
                    Toast.makeText(getContext(), "No One in Lost Mode", Toast.LENGTH_LONG).show();
                } else {

                    databaseReference2.child(ids.get(spinner.getSelectedItemPosition())).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            person3 = dataSnapshot.getValue(Person.class);
                            person3.setStatus("Available");
                            databaseReference2.child(person3.getId()).setValue(person3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    person3.setStatus("I Lost My Mobile");
                                    databaseReference2.child(person3.getId()).setValue(person3).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "Device Details Will get By SMS and Email", Toast.LENGTH_LONG).show();
                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                            fragmentManager.beginTransaction().replace(R.id.user_container, new UserHome()).commit();
                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        getYourFriends();

       return view;
    }

    void getYourFriends() {
        databaseReference.orderByChild("phone").equalTo(sharedpreferences.getString("phone", null)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                persons = new ArrayList<Person>();
                ids = new ArrayList<String>();
                names = new ArrayList<String>();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    Guardian guardian= dataSnapshot1.getValue(Guardian.class);
                    Log.e("errr",guardian.getPersonId());
                    databaseReference2.child(guardian.getPersonId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                            Person person2 = dataSnapshot3.getValue(Person.class);

                           if (person2.getStatus().equals("I Lost My Mobile")) {
                                ids.add(person2.getId());
                                names.add(person2.getName());
                                ArrayAdapter<String> adapter;
                                adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,names);
                                spinner.setAdapter(adapter);
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
