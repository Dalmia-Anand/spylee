package com.spylee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.spylee.model.Guardian;
import com.spylee.model.Person;


public class UpdateStatus extends Fragment {
    Spinner status;
    Button update;
    DatabaseReference databaseReference;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    Guardian guardian;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_status, container, false);
        guardian = new Guardian();
        databaseReference = DbRef.getDbRef().child("persons");
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        status = (Spinner) view.findViewById(R.id.status);
        update = (Button)view.findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child(sharedpreferences.getString("id",null)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final Person person = dataSnapshot.getValue(Person.class);
                        person.setStatus(status.getSelectedItem().toString());
                        databaseReference.child(person.getId()).setValue(person).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(person.getStatus().equals("I Lost My Mobile")) {
                                    Toast.makeText(getContext(),"Status Updated Successfully",Toast.LENGTH_LONG).show();
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.clear();
                                    editor.commit();
                                    getActivity().finish();

                                    Intent intent = new Intent(getContext(),LostModeActivity.class);
                                    startActivity(intent);

                                }else {
                                    Toast.makeText(getContext(),"Status Updated Successfully",Toast.LENGTH_LONG).show();
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.user_container, new UserHome()).commit();
                                }


                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        return  view;
    }
}
