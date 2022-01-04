package com.example.covid_guide;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.covid_guide.databinding.ActivityStatisticsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Statistics extends AppCompatActivity {
    ActivityStatisticsBinding binding;
    ArrayList<String> usersList1;
    ArrayAdapter<String> listAdapter1;
    Handler mainHandler = new Handler();
    Handler mainHandler2 = new Handler();
    ProgressDialog progressDialog;
    String cases, deaths, recovered,active,critical, formatDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatisticsBinding.inflate(getLayoutInflater());
        Date current = Calendar.getInstance().getTime();
        formatDate = DateFormat.getDateInstance(DateFormat.FULL).format(current);
        setContentView(binding.getRoot());
        initializeUserlist();
        new fetchData2().start();

        binding.refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new fetchData().start();
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }
    private void initializeUserlist() {
        usersList1 = new ArrayList<>();
        listAdapter1 = new ArrayAdapter<String>(this, R.layout.text_color_layout, usersList1);
        binding.userList1.setAdapter(listAdapter1);
    }
    class fetchData2 extends Thread{
        String data2 = "";

        @Override
        public void run() {
            mainHandler2.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(Statistics.this);
                    progressDialog.setMessage("Loading Data");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });
            try {
                URL url2 = new URL("https://covidbd-api.herokuapp.com/status");
                HttpURLConnection httpURLConnection2 = (HttpURLConnection) url2.openConnection();
                InputStream inputStream2 = httpURLConnection2.getInputStream();
                BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(inputStream2));
                String line2;

                while((line2 = bufferedReader2.readLine())!= null){
                    data2 = data2 + line2;
                }
                if(!data2.isEmpty()){
                    JSONObject jsonObject2 = new JSONObject(data2);

                    cases = jsonObject2.getString("cases");
                    deaths = jsonObject2.getString("deaths");
                    recovered = jsonObject2.getString("recovered");
                    active = jsonObject2.getString("active");
                    critical = jsonObject2.getString("critical");


                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mainHandler2.post(new Runnable() {
                @Override
                public void run() {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    binding.date.setText(formatDate);
                    binding.totalCase.setText(cases);
                    binding.totalDeath.setText(deaths);
                    binding.totalRecovered.setText(recovered);
                    binding.activeCase.setText(active);
                    binding.criticalCase.setText(critical);

                }
            });




        }
    }
    class fetchData extends Thread{
        String data1 = "";


        @Override
        public void run() {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(Statistics.this);
                    progressDialog.setMessage("Fetching Data");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });

            try {
                URL url1 = new URL("https://covidbd-api.herokuapp.com/districts");
                HttpURLConnection httpURLConnection1 = (HttpURLConnection) url1.openConnection();
                InputStream inputStream1 = httpURLConnection1.getInputStream();
                BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(inputStream1));
                String line1;

                while((line1 = bufferedReader1.readLine())!= null){
                    data1 = data1 + line1;
                }

                if(!data1.isEmpty()){
                    JSONObject jsonObject1 = new JSONObject(data1);
                    JSONArray users1 = jsonObject1.getJSONArray("district");
                    usersList1.clear();


                    for(int i=0; i<users1.length(); i++){
                        JSONObject names = users1.getJSONObject(i);
                        String name = names.getString("name");
                        String total = names.getString("count");
                        usersList1.add(name+'\n'+"Total Case: "+total);

                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    listAdapter1.notifyDataSetChanged();
                }
            });
        }
    }
}