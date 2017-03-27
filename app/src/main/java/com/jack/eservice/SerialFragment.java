package com.jack.eservice;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.Runnable;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SerialFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SerialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SerialFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ViewPager viewPager;
    private ArrayList<View> viewList;
//    private String[] titles = {"工作一", "工作二", "工作三", "工作四", "工作五", "工作六"};
    private String[] titles = {"出貨", "Tracking","Key parts"};

    private OnFragmentInteractionListener mListener;
    private RadioGroup mradio;
    private ImageButton imageButton,imageButton2;
    private IntentIntegrator integrator;
    private TextView tvSerial;
    private Toolbar toolbar;
    private EditText ed_serial;
    private View v1,v2,v3;

    String func1[] = {"2016/12/13","2016/12/14","2016/12/15","2016/12/17","2016/12/19","2016/12/24"};
    String func2[] = {"10:23:33","13:36:33","15:34:33","13:33:12","09:03:13","16:56:34"};
    String func3[] = {"SMT","DIP","FA","FIX","FA","PACKING"};
    String func4[] = {"OK","OK","NG","OK","OK","OK"};
    String func5[] = {"","","Label Error","","",""};

    private ListView lvSerial;

    OkHttpClient client = new OkHttpClient();

    public SerialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SerialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SerialFragment newInstance(String param1, String param2) {
        SerialFragment fragment = new SerialFragment();
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_serial, container, false);
        ed_serial = (EditText)view.findViewById(R.id.ed_serial);

        initViewpager(view, savedInstanceState);
        initBarcode(view);
//        initContextMenu(view);
        initToolBar(view);
        initPage1();
        initPage2(inflater);
        initPage3();
        return view;
    }

    private void initPage3() {
        //new TransTask().execute("http://atm201605.appspot.com/h");
        Request request = new Request.Builder()
                .url("http://atm201605.appspot.com/h")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.d("okhttp",json);
//                parseJSON(json);
                parseGson(json);

            }
        });
    }

    class TransTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            try{
                URL url = new URL(params[0]);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(url.openStream()));
                String line = in.readLine();
                while (line != null){
                    Log.d("http",line);
                    sb.append(line);
                    line = in.readLine();
                }
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("jsoon",s);
            parseJSON(s);
            parseGson(s);
        }

    }

    private void parseJSON(String s) {
        ArrayList<Transaction> trans = new ArrayList<>();
        try{
            JSONArray array = new JSONArray(s);
            for (int i=0;i<array.length();i++){
                JSONObject obj = array.getJSONObject(i);
                String account = obj.getString("account");
                String date = obj.getString("date");
                int amount = obj.getInt("amount");
                int type = obj.getInt("type");
                Log.d("json",account+"/"+date+"/"+amount+"/"+type);
                Transaction t = new Transaction(account,date,amount,type);
                trans.add(t);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseGson(String s) {
        Gson gson = new Gson();
        final ArrayList<Transaction> list = gson.fromJson(s,
                new TypeToken<ArrayList<Transaction>>(){}.getType());
        Log.d("gson",list.size()+"/"+list.get(0).getAmount());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupRecycleView(list);
            }
        });

    }

    private void setupRecycleView(List<Transaction> list){
        RecyclerView recyclerView = (RecyclerView)v3.findViewById(R.id.recycler);
        TransactionAdapter adapter = new TransactionAdapter(list);
        recyclerView.setAdapter(adapter);;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initPage1(){
    }

    private void initPage2(LayoutInflater inflater) {
        lvSerial = (ListView)v2.findViewById(R.id.lvSerial);
        trackAdpater adpater = new trackAdpater(inflater);
        lvSerial.setAdapter(adpater);
    }


    private void initToolBar(View view) {
        toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
//        appCompatActivity.getSupportActionBar().setHomeButtonEnabled(false);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("test");
        //((MainActivity)getActivity()).getSupportActionBar();
//        getActivity().toolbar
    }

    //隐藏动画
    private void hideToolBar(){
        Animator animator = ObjectAnimator.ofFloat(toolbar, "translationY",
                toolbar.getTranslationY(), -toolbar.getHeight());
        animator.setDuration(600);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setTarget(toolbar);
        animator.start();
    }
    //显示动画
    private void showToolBar(){
        Animator animator = ObjectAnimator.ofFloat(toolbar, "translationY",
                toolbar.getTranslationY(), 0);
        animator.setDuration(600);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setTarget(toolbar);
        animator.start();
    }


//    private void initContextMenu(View view) {
//        tvSerial = (TextView)view.findViewById(R.id.tvSerial);
//        registerForContextMenu(tvSerial);
//    }
//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        getActivity().getMenuInflater().inflate(R.menu.serial,menu);
//    }

    private void initBarcode(View view) {
        imageButton = (ImageButton) view.findViewById(R.id.scanBtn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateScanning();
            }
        });
        imageButton2 = (ImageButton) view.findViewById(R.id.scanBtn2);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initZbar();
            }
        });

    }

    private void initZbar() {
        Intent intent = new Intent(getActivity(),BarcodeActivity.class);
        startActivity(intent);
    }

    private void initViewpager(View view,Bundle savedInstanceState) {
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        final LayoutInflater minflater = getLayoutInflater(savedInstanceState).from(getActivity());

        v1 = minflater.inflate(R.layout.viewpager_serial1, null);
        v2 = minflater.inflate(R.layout.viewpager_serial2, null);
        v3 = minflater.inflate(R.layout.viewpager_serial3, null);
//        View v4 = minflater.inflate(R.layout.viewpager_serial3, null);
//        View v5 = minflater.inflate(R.layout.viewpager_serial3, null);
//        View v6 = minflater.inflate(R.layout.viewpager_serial3, null);

        viewList = new ArrayList<View>();
        viewList.add(v1);
        viewList.add(v2);
        viewList.add(v3);
//        viewList.add(v4);
//        viewList.add(v5);
//        viewList.add(v6);

        viewPager.setAdapter(new myViewpagerAdapter(viewList));
        viewPager.setCurrentItem(0);

        mradio = (RadioGroup) view.findViewById(R.id.radiogroup);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mradio.check(R.id.radioButton);
                        break;
                    case 1:
                        mradio.check(R.id.radioButton2);
                        break;
                    case 2:
                        mradio.check(R.id.radioButton3);
                        break;
                    case 3:
                        mradio.check(R.id.radioButton3);
                        break;
                    case 4:
                        mradio.check(R.id.radioButton3);
                        break;
                    case 5:
                        mradio.check(R.id.radioButton3);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setViewPager(viewPager);
    }

    private void initiateScanning() {
        integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Scan something");
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Scanned:" + result.getContents(), Toast.LENGTH_SHORT).show();
                ed_serial.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public class myViewpagerAdapter extends PagerAdapter {
        private ArrayList<View> mListView;

        public myViewpagerAdapter(ArrayList<View> viewList) {
            mListView = viewList;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //          return super.instantiateItem(container, position);
            View view = mListView.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return mListView.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
            //return super.getPageTitle(position);
        }
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

    class trackAdpater extends BaseAdapter{
        LayoutInflater minflater;
        public trackAdpater(LayoutInflater inflater) {
            super();
            minflater = inflater;
        }

        @Override
        public int getCount() {
            return func1.length;
        }

        @Override
        public Object getItem(int position) {
            return func1[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null){
                row = minflater.inflate(R.layout.serial2,null);
                TextView ed_date = (TextView)row.findViewById(R.id.ed_date);
                TextView ed_time = (TextView)row.findViewById(R.id.ed_time);
                TextView ed_process = (TextView)row.findViewById(R.id.ed_process);
                TextView ed_status = (TextView)row.findViewById(R.id.ed_status);
                TextView ed_ngcode = (TextView)row.findViewById(R.id.ed_ngcode);
                ed_date.setText(func1[position]);
                ed_time.setText(func2[position]);
                ed_process.setText(func3[position]);
                ed_status.setText(func4[position]);
                ed_ngcode.setText(func5[position]);
            }
            return row;
        }
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.serial, menu);
        //return true;
//        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.barcode) {
            Toast.makeText(getActivity(), "barcode", Toast.LENGTH_SHORT).show();
            initiateScanning();
            //initZbar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
