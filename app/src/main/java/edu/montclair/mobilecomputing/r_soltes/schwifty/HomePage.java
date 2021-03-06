package edu.montclair.mobilecomputing.r_soltes.schwifty;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import butterknife.ButterKnife;
import edu.montclair.mobilecomputing.r_soltes.schwifty.fragments.ErrorFragment;
import edu.montclair.mobilecomputing.r_soltes.schwifty.fragments.HomeFragment;
import edu.montclair.mobilecomputing.r_soltes.schwifty.fragments.ManagerPanelFragment;
import edu.montclair.mobilecomputing.r_soltes.schwifty.fragments.ScheduleFragment;
import edu.montclair.mobilecomputing.r_soltes.schwifty.fragments.TimeOffFragment;
import edu.montclair.mobilecomputing.r_soltes.schwifty.utils.schwiftyInterface;
import edu.montclair.mobilecomputing.r_soltes.schwifty.utils.sessionUser;

public class HomePage extends AppCompatActivity implements schwiftyInterface {

    public Snackbar snackbar;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference, userRef;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Fragment fragment = null;
    public sessionUser session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Binding
        ButterKnife.bind(this);

        fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, fragment).addToBackStack(null).commit();

        session = new sessionUser(getApplicationContext());


        //get firebase user
        FirebaseUser user = mFirebaseAuth.getInstance().getCurrentUser();


        //get reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        //IMPORTANT: .getReference(user.getUid()) will not work although user.getUid() is unique. You need a full path!

        final String uid = user.getUid().toString();
        mDatabaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://schwifty-33650.firebaseio.com/");
        userRef = mDatabaseReference.child("users");
        userRef.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String userRole = dataSnapshot.child(uid).child("userRole").getValue().toString();
                String username = dataSnapshot.child(uid).child("username").getValue().toString();
                String userEmail = dataSnapshot.child(uid).child("email").getValue().toString();

                session.createSessionUser(userEmail,uid,username,userRole);
                HashMap<String, String> currentUser = session.getUserDetails();

                if(currentUser.get(sessionUser.KEY_SESSION_USERROLE).equals("Manager")){
                    createManagerNavigation();
                }else{
                    createNavigation();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public void createNavigation() {

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        // Listener to display the correct information according to tab that is selected.
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tab_manager:
                        fragment = new ErrorFragment();
                        break;
                    case R.id.tab_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.tab_schedule:
                        fragment = new ScheduleFragment();
                        break;
                    case R.id.tab_time_off:
                        fragment = new TimeOffFragment();
                        break;
//                    case R.id.tab_notification:
//                        fragment = new NotificationFragment();
//                        break;
                    case R.id.tab_logout:
                        snackbar.make(findViewById(android.R.id.content), "Are you sure you want to logout?",
                                Snackbar.LENGTH_LONG).setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFirebaseAuth.getInstance().signOut();
                                session.logoutUser();
                                startActivity(new Intent(HomePage.this, LoginPage.class));
                                finish();
                            }
                        }).setActionTextColor(getResources().getColor(R.color.colorPrimary)).show();
                        break;
                    default:
                        fragment = new HomeFragment();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, fragment).addToBackStack(null).commit();
                return true;
            }

        });
    }

    public void createManagerNavigation() {

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        // Listener to display the correct information according to tab that is selected.
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tab_manager:
                        fragment = new ManagerPanelFragment();
                        break;
                    case R.id.tab_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.tab_schedule:
                        fragment = new ScheduleFragment();
                        break;
                    case R.id.tab_time_off:
                        fragment = new TimeOffFragment();
                        break;
//                    case R.id.tab_notification:
//                        fragment = new NotificationFragment();
//                        break;
                    case R.id.tab_logout:
                        snackbar.make(findViewById(android.R.id.content), "Are you sure you want to logout?",
                                Snackbar.LENGTH_LONG).setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFirebaseAuth.getInstance().signOut();
                                session.logoutUser();
                                startActivity(new Intent(HomePage.this, LoginPage.class));
                                finish();
                            }
                        }).setActionTextColor(getResources().getColor(R.color.colorPrimary)).show();
                        break;
                    default:
                        fragment = new HomeFragment();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, fragment).addToBackStack(null).commit();
                return true;
            }

        });
    }

    @Override
    public void startMyIntent(Intent i) {
        startActivity(i);
    }
}
