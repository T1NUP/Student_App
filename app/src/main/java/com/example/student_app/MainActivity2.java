package com.example.student_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity2 extends AppCompatActivity {

    TextView status;
    EditText id,code;
    Button mark,submit;
    FirebaseAuth fa;
    GoogleSignInAccount account;
    GoogleSignInClient mGoogleSignInClient;
    DatabaseReference write,make,update;
    String date,emailId,name,flag,tokId;
    String tid, cid;
    int go=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        id= findViewById(R.id.id);
        code= findViewById(R.id.code);
        mark= findViewById(R.id.present);
        submit= findViewById(R.id.submit);
        mark.setVisibility(View.INVISIBLE);

        fa= FirebaseAuth.getInstance();
        account = GoogleSignIn.getLastSignedInAccount(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //title.setText(account.getEmail().toString());

        //date = DateFormat.getDateTimeInstance().format(new Date());
        //date= date.substring(0,11);
        date =new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        tokId= account.getId();
        emailId=account.getEmail();

        name= account.getDisplayName();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go= check();
                if(check()==1)
                {
                   // Toast.makeText(MainActivity2.this,"ATTENDENCE MARKED!!",Toast.LENGTH_LONG).show();
                    update= FirebaseDatabase.getInstance().getReference(date).child(tid).child(cid);
                    int le= emailId.indexOf("@");
                    emailId=emailId.substring(0,le);
                    //String upl = write.push().getKey();
                    update.child("PRESENT").child(emailId).setValue(name);
                    go=0;
                    submit.setVisibility(View.INVISIBLE);

                    AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity2.this);
                    builder.setCancelable(true);
                    builder.setTitle("INFO:");
                    builder.setMessage("Attendance Marked!!");
                    builder.show();
                }
                else
                {
                    Toast.makeText(MainActivity2.this,"SERVER NOT ACTIVE!!",Toast.LENGTH_LONG).show();
                    go=0;
                }
            }
        });

    }

    public int check()
    {
        if((code.getText().toString()).equals("")||(id.getText().toString()).equals(""))
        {
            Toast.makeText(MainActivity2.this,"Please Enter data..",Toast.LENGTH_LONG).show();
        }
        else {


            tid = id.getText().toString();
            cid = code.getText().toString();


            //Toast.makeText(MainActivity2.this, date + " " + tid + " " + cid, Toast.LENGTH_LONG).show();
            make = FirebaseDatabase.getInstance().getReference();
            make.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild(date))
                        if (dataSnapshot.child(date).hasChild(tid))
                            if (dataSnapshot.child(date).child(tid).hasChild(cid))
                            {

                                write = FirebaseDatabase.getInstance().getReference(date).child(tid).child(cid);
                                write.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        //Toast.makeText(MainActivity2.this, dataSnapshot.child("Flag").getValue().toString(), Toast.LENGTH_LONG).show();
                                        flag = dataSnapshot.child("Flag").getValue().toString();
                                        if(flag.equals("1"))
                                        go=1;
                                        else
                                            go=0;
                                        return;
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(MainActivity2.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                    return;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity2.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

        return go;
    }
}