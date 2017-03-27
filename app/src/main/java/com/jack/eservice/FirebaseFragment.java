package com.jack.eservice;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirebaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirebaseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView firebaseList;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private DatabaseReference reference;

    private DatabaseReference reference2;

    private View view;

    private static final int REQUEST_LOGIN = 1;

    private RecyclerView recycle;
    private FirebaseRecyclerAdapter adapter;

    private HashMap<String, String> data;

    private Button addbtn, btn_remove;
    private EditText ed_add;

    private ArrayList<Contacts> addlist;

    private String userid;

    public FirebaseFragment() {


        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirebaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirebaseFragment newInstance(String param1, String param2) {
        FirebaseFragment fragment = new FirebaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_firebase, container, false);
        recycle = (RecyclerView) view.findViewById(R.id.recycle);
        addbtn = (Button) view.findViewById(R.id.btn_add);
        ed_add = (EditText) view.findViewById(R.id.ed_add);
        btn_remove = (Button) view.findViewById(R.id.btn_remove);
        initLogin();
        return view;
    }

    private void initAddlist() {
        addlist = new ArrayList<Contacts>();
        int j = Integer.valueOf(ed_add.getText().toString());
        for (int i = 1; i <= j; i++) {
            Contacts cont = new Contacts();
            cont.setAddr(String.valueOf(i));
            cont.setName(String.valueOf(i));
            cont.setPhone(String.valueOf(i));
            addlist.add(cont);
        }
    }

    private void initFirebaseUI() {
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAddlist();
                reference2 = FirebaseDatabase.getInstance().getReference(Contacts.REF_CONTACTS+"/"+userid);
                reference2.setValue(addlist).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "新增資料成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String removepoint = ed_add.getText().toString();
                reference2 = FirebaseDatabase.getInstance().getReference(Contacts.REF_CONTACTS + "/" +userid+"/"+ removepoint);
                reference2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "刪除資料成功", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        reference = FirebaseDatabase.getInstance().getReference(Contacts.REF_CONTACTS+"/"+userid);
        adapter = new FirebaseRecyclerAdapter<Contacts, ContactHolder>(Contacts.class,
                R.layout.serial3, ContactHolder.class, reference) {

            @Override
            protected void populateViewHolder(ContactHolder viewHolder, Contacts model, int position) {
                viewHolder.setValues(model);
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);

        recycle.setLayoutManager(gridLayoutManager);
        recycle.setAdapter(adapter);
    }

    private void initLogin() {
        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivityForResult(new Intent(getActivity(), FirebaseLoginActivity.class), REQUEST_LOGIN);
                } else {
                    userid = user.getUid();
                    initFirebaseUI();
                }
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOGIN:
                if (resultCode == getActivity().RESULT_OK) {
                    FirebaseUser user = auth.getCurrentUser();
                    userid = user.getUid();
                    initFirebaseUI();
                }
                break;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (auth != null) {
            auth.signOut();
        }
        super.onDestroy();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {
        private final TextView mname;
        private final TextView mphone;
        private final TextView mempty;

        public ContactHolder(View itemView) {
            super(itemView);
//            mname = (TextView) itemView.findViewById(android.R.id.text1);
//            mphone = (TextView) itemView.findViewById(android.R.id.text2);
            mname = (TextView) itemView.findViewById(R.id.col_date);
            mphone = (TextView) itemView.findViewById(R.id.col_amount);
            mempty = (TextView) itemView.findViewById(R.id.col_type);

        }

        public void setValues(Contacts contacts) {
            mname.setText(contacts.getName());
            mphone.setText(contacts.getPhone());
            mempty.setText("");
        }
    }
}
