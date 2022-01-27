package com.spylee;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.spylee.model.Guardian;
import com.spylee.model.Person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyFireBaseService extends Service {
    FusedLocationProviderClient fusedLocationClient;
    Intent intentThatCalled;
    public double latitude;
    public double longitude;
    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;
    private static final int REQUEST_READ_PHONE_STATE = 1;
    private final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    List<Guardian> guardians;
    private static final int PERMISSION_SEND_SMS = 123;

    @Override
    public void onCreate() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // SimpleMail.sendEmail("dhaatrisolutions2@gmail.com","Hii", "Hello");
        databaseReference = DbRef.getDbRef().child("persons");
        databaseReference2 = DbRef.getDbRef().child("guardians");
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        guardians= new ArrayList<Guardian>();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("rrrrrr","Started");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"Service Started",Toast.LENGTH_LONG).show();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.getString("id", null) != null) {
            databaseReference.child(sharedpreferences.getString("id", null)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Person person = dataSnapshot.getValue(Person.class);
                    if (person!=null) {
                        if (person.getStatus().equals("I Lost My Mobile")) {
                            try {
                                //  getLocation2();
                                Toast.makeText(getApplicationContext(), "Changed", Toast.LENGTH_LONG).show();
                                getLocation("lost");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Getting Null", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return START_STICKY;
    }
    public static boolean isLocationEnabled(Context context) {
        //...............
        return true;
    }

    protected void getLocation(final String status) throws IOException {
        try {
            if (isLocationEnabled(getApplicationContext())) {
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                criteria = new Criteria();
                bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
                //You can still do this if you like, you might get lucky:

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }


                Location location = locationManager.getLastKnownLocation(bestProvider);
                if (location != null) {
                    Log.e("TAG", "GPS is on");
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());

                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    final String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();

                    if (sharedpreferences.getString("id", null) == null) {
                        Toast.makeText(getApplicationContext(), "Please Login", Toast.LENGTH_SHORT).show();
                    } else {
                        databaseReference2.orderByChild("personId").equalTo(sharedpreferences.getString("id", null)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                guardians = new ArrayList<Guardian>();
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    Guardian guardian = dataSnapshot1.getValue(Guardian.class);
                                    guardians.add(guardian);
                                    String subject = "";
                                    String message = "";
                                    if (status.equals("help")) {
                                        subject = sharedpreferences.getString("name", null) + " is In Emergency";
                                        message = sharedpreferences.getString("name", null) + " is In Emergency Please Find her/him . " + sharedpreferences.getString("name", null) + " is in " + address + ". For Perfect Location Find in Google Maps with Latitude " + latitude + " and Longitudes " + longitude;
                                    } else {
                                        subject = sharedpreferences.getString("name", null) + "  Device is Traced";
                                        message = sharedpreferences.getString("name", null) + " Device is at " + address + ". For Perfect Location Find in Google Maps with Latitude " + latitude + " and Longitudes " + longitude;

                                    }

                                    SimpleMail.sendEmail(guardian.getEmail(), subject, message);

                                    int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE);
                                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.SEND_SMS},
                                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                                    } else if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                        SmsManager sms = SmsManager.getDefault();
                                        sms.sendTextMessage(guardian.getPhone(), null, message, sentPI, deliveredPI);

                                    }


                                    Toast.makeText(getApplicationContext(), "sent to " + guardian.getName(), Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }


                } else {
                    //This is what you need:


                }
            } else {
                //prompt user to enable location....
                //.................
            }
        }catch (Exception e) {
            Log.d("errrrr",e.toString());
        }
    }

}
