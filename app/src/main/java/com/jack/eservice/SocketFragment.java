package com.jack.eservice;

import android.content.Context;
import android.net.Uri;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SocketFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SocketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocketFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private EditText ed_ip, ed_port, ed_input, ed_in_data, ed_out_data;
    private Button btn_connect, btn_stop, btn_chat;

    private Socket socket;
    private BufferedReader in;
    private DataInputStream indata;
    private PrintWriter out;
    private String mip, mport, minput;

    private String usermsg = null;
    public static Handler receiveServer = null;

    private SocketThread socketthread;

    public SocketFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SocketFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SocketFragment newInstance(String param1, String param2) {
        SocketFragment fragment = new SocketFragment();
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
        View view = inflater.inflate(R.layout.fragment_socket, container, false);
        initSocket(view);
        return view;
    }

    private void initSocket(View view) {
        ed_ip = (EditText) view.findViewById(R.id.ed_ip);
        ed_port = (EditText) view.findViewById(R.id.ed_port);
        ed_input = (EditText) view.findViewById(R.id.ed_input);
        ed_in_data = (EditText) view.findViewById(R.id.ed_in_data);
        ed_out_data = (EditText) view.findViewById(R.id.ed_out_data);
        btn_connect = (Button) view.findViewById(R.id.btnConnect);
        btn_stop = (Button) view.findViewById(R.id.btnStop);
        btn_chat = (Button) view.findViewById(R.id.btnChat);
        setConn();
        setSendAction();
        setStopAction();
    }

    private void setConn() {
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socketthread = new SocketThread();
                socketthread.start();
                receiveServer = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        ed_in_data.setText(ed_in_data.getText() + "\n" + (String) msg.obj);
                    }
                };
            }
        });
    }

    public class SocketThread extends Thread {
        BufferedReader serverin;
        String obj;
        Message servermsg = new Message();

        public SocketThread() {
//            if (socket.isConnected()) {
//                serverin = in;
//            }
        }

        @Override
        public void run() {
            try {
                mip = ed_ip.getText().toString();
                mport = ed_port.getText().toString();
                socket = new Socket(mip, Integer.parseInt(mport));
                if (socket.isConnected()) {
                    Log.d("socket", "connect success");
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    // indata = new DataInputStream(socket.getInputStream());
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "BIG5")), true);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (socket.isConnected()) {
                try {
                    socket.setSoTimeout(2000);
                    //obj = "" + indata.readUTF();
                    obj = "" + in.readLine();
                    servermsg = receiveServer.obtainMessage(1, obj);
                    Log.d("receive", obj);
                    receiveServer.sendMessage(servermsg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    SocketThread.sleep(2000);
                    Log.d("thread", "wait 2 seconds");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("thread", "socket disconnected");
                    try {
                        socket.close();
                        in.close();
                        out.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    break;
                }catch (Exception e){
                    try {
                        socket.close();
                        in.close();
                        out.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    private void setStopAction() {
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (socketthread != null && socketthread.isAlive()) {
                    socketthread.interrupt();
//                    try {
//                        while (socketthread.isAlive()) {
//                            Thread.sleep(5000);
//                        }
//                        socketthread = null;
//                        socket.close();
//                        in.close();
//                        out.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        });
    }

    private void setSendAction() {
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usermsg = ed_input.getText().toString();
                if (socket.isConnected()) {
                    if (!socket.isOutputShutdown() && usermsg.length() > 0) {
                        out.println(usermsg);
                        out.flush();
                        ed_out_data.setText(ed_out_data.getText() + "\n" + usermsg);
                        ed_input.setText("");
                    }
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
