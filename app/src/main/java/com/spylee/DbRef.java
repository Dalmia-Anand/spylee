package com.spylee;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DbRef {
    public static DatabaseReference getDbRef() {
        return FirebaseDatabase.getInstance().getReference("spylee");
    }
}
