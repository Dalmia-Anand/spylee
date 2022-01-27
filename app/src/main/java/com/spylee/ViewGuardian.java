package com.spylee;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.spylee.model.Guardian;

import java.util.ArrayList;
import java.util.List;


public class ViewGuardian extends Fragment {
    TextView name,email,phone,relation;
    Button delete,update;
    DatabaseReference databaseReference;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    Guardian guardian;
    List<Guardian> guardians;
    ListView guardianList;
    FragmentManager fragmentManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_guardian, container, false);
        guardians = new ArrayList<Guardian>();
        guardian = new Guardian();
        guardianList = (ListView)view.findViewById(R.id.guardianList);
        databaseReference = DbRef.getDbRef().child("guardians");
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        fragmentManager =getActivity().getSupportFragmentManager();


        getGuardians();
        return view;
    }

    class CustomAdoptor extends BaseAdapter {


        @Override
        public int getCount() {
            return guardians.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view2, ViewGroup viewGroup)
        {
            view2 = getActivity().getLayoutInflater().inflate(R.layout.guardian_list,null);
            name = (TextView)view2.findViewById(R.id.name);
            email = (TextView)view2.findViewById(R.id.email);
            phone = (TextView)view2.findViewById(R.id.phone);
            relation = (TextView)view2.findViewById(R.id.relation);
            delete = (Button) view2.findViewById(R.id.delete);
            update = (Button) view2.findViewById(R.id.update);
            name.setText(guardians.get(i).getName());
            email.setText(guardians.get(i).getEmail());
            phone.setText(guardians.get(i).getPhone());
            relation.setText(guardians.get(i).getRelation());
            delete.setTag(guardians.get(i).getId());
            update.setTag(guardians.get(i).getId());
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseReference.child(view.getTag().toString()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            getGuardians();
                        }
                    });
                }
            });
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle=new Bundle();
                    bundle.putString("guardianId", view.getTag().toString());
                    UpdateGuardian updateGuardian = new UpdateGuardian();
                    updateGuardian.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.user_container,updateGuardian).commit();
                }
            });
            return view2;
        }
    }
    void getGuardians() {
        databaseReference.orderByChild("personId").equalTo(sharedpreferences.getString("id",null)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                guardians = new ArrayList<Guardian>();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    guardians.add(dataSnapshot1.getValue(Guardian.class));
                }
                CustomAdoptor customAdoptor= new CustomAdoptor();
                guardianList.setAdapter(customAdoptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
