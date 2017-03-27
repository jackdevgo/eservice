package com.jack.eservice;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseLoginActivity extends AppCompatActivity {
    private EditText ed_mail, ed_pw;
    private Button btn_login;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String userUID;
    private static  String edmail="",edpw="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_login);
        ed_mail = (EditText) findViewById(R.id.ed_mail);
        ed_pw = (EditText) findViewById(R.id.ed_pw);
        btn_login = (Button) findViewById(R.id.btn_login);

        auth = FirebaseAuth.getInstance();
        authStateListener  = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d("auth","登入"+user.getUid());
                    userUID = user.getUid();
                }else{
                    Log.d("auth","已登出");
                }
            }
        };

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edmail = ed_mail.getText().toString();
                edpw = ed_pw.getText().toString();
                auth.signInWithEmailAndPassword(edmail,edpw)
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                Log.d("auth","onComplete:"+task.isSuccessful());
//                            }
//                        });
                       .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                setResult(RESULT_OK);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //btn_login.setError("fail");
                                register(edmail,edpw);
                            }
                        });

            }
        });

    }

    private void register(final String edmail, final String edpw) {
        new AlertDialog.Builder(this)
                .setTitle("登入")
                .setMessage("無此帳號,是否要以此帳號密碼註冊?")
                .setPositiveButton("註冊", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        createUser(edmail,edpw);
                    }
                })
                .setNeutralButton("取消",null)
                .show();
    }

    private void createUser(String edmail, String edpw) {
        auth.createUserWithEmailAndPassword(edmail,edpw)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String message = task.isComplete() ? "註冊成功":"註冊失敗";
                        new AlertDialog.Builder(FirebaseLoginActivity.this)
                                .setMessage(message)
                                .setPositiveButton("ok",null)
                                .show();
                        if (task.isSuccessful()){
                            insertUser();
                            setResult(RESULT_OK);
                            finish();
                        }
                    }

                });
    }

    private void insertUser() {
        FirebaseUser user = auth.getCurrentUser();
        String userUID = user.getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference userref = db.getReference("users");
        userref.child(userUID).child("name").setValue(user.getEmail());
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener!=null){
        auth.removeAuthStateListener(authStateListener);}
    }
}
