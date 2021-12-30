package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MainActivity2 extends AppCompatActivity {

    TextView query;
    ImageView back, report;

    String dataBase;

    Call<searchModel> searchModelCall = null;

    ArrayList<RecyclerView_Model> data2 = new ArrayList<>();

    RecyclerView recyclerView;
    RecyclerView_Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        buildRecyclerView();

        Bundle bundle = getIntent().getExtras();

        String q = bundle.getString("q");
        String lan1 = bundle.getString("lan1");
        String lan2 = bundle.getString("lan2");

        query = findViewById(R.id.queryText);
        back = findViewById(R.id.backBtn);
        report = findViewById(R.id.report);

        query.setText(q);

        if (lan1.equals("انگلیسی") && lan2.equals("فارسی"))
            dataBase = "en2fa";
        else if (lan1.equals("عربی") && lan2.equals("فارسی"))
            dataBase = "ar2fa";
        else if (lan1.equals("فارسی") && lan2.equals("انگلیسی"))
            dataBase = "none";
        else if (lan1.equals("فارسی") && lan2.equals("عربی"))
            dataBase = "none";
        else if (lan1.equals("فارسی")) {
            if (lan2.equals("لغتنامه دهخدا"))
                dataBase = "dehkhoda";
            else if (lan2.equals("فرهنگ معین"))
                dataBase = "moein";
            else if (lan2.equals("فرهنگ عمید"))
                dataBase = "amid";
            else if (lan2.equals("مترادف و متضاد"))
                dataBase = "motaradef";
            else if (lan2.equals("فرهنگ نام ها"))
                dataBase = "name";
            else if (lan2.equals("واژه نامه آزاد"))
                dataBase = "wiki";
        }
        else {
            Toast.makeText(MainActivity2.this,
                    "ترجمه " + lan1 + " به " + lan2 + " در پایگاه داده وجود ندارد! " ,
                    Toast.LENGTH_SHORT).show();
        }

        if (dataBase.equals("none")) {
            getSearchWithoutFilter(ApiUtilities.token, q, "exact");
        }
        else {
            getSearch(ApiUtilities.token, q, "exact", dataBase);
        }

        // click on back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        // click on report button
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

    }


    private void getSearch(String token, String q, String type, String filter) {

        ApiInterface apiInterface = ApiUtilities.getApiInterface(this).create(ApiInterface.class);
        searchModelCall = apiInterface.search(token, q, type, filter);

        searchModelCall.enqueue(new Callback<searchModel>() {
            @Override
            public void onResponse(Call<searchModel> call, Response<searchModel> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().data == null) {
                    Toast.makeText(MainActivity2.this, "ترجمه " + q + " پیدا نشد!" , Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.body() != null) {
                    for (int i = 0; i < response.body().data.results.size(); i++) {
                        data2.add(new RecyclerView_Model( response.body().data.results.get(i).source, response.body().data.results.get(i).text));

                    }
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<searchModel> call, Throwable t) {
                Toast.makeText(MainActivity2.this, "مشکلی در ارتباط با سرور پیش آمده است!", Toast.LENGTH_SHORT).show();
                return;
            }

        });

    }


    private void getSearchWithoutFilter(String token, String q, String type) {

        ApiInterface apiInterface = ApiUtilities.getApiInterface(this).create(ApiInterface.class);
        searchModelCall = apiInterface.search(token, q, type);

        searchModelCall.enqueue(new Callback<searchModel>() {
            @Override
            public void onResponse(Call<searchModel> call, Response<searchModel> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().data == null) {
                    Toast.makeText(MainActivity2.this, "ترجمه " + q + " پیدا نشد!" , Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.body() != null) {
                    for (int i = 0; i < response.body().data.results.size(); i++) {
                        data2.add(new RecyclerView_Model( response.body().data.results.get(i).source, response.body().data.results.get(i).text));

                    }
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<searchModel> call, Throwable t) {
                Toast.makeText(MainActivity2.this, "مشکلی در ارتباط با سرور پیش آمده است!", Toast.LENGTH_SHORT).show();
                return;
            }

        });

    }


    public void buildRecyclerView() {

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(MainActivity2.this);
        mAdapter = new RecyclerView_Adapter(data2);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RecyclerView_Adapter.OnItemClickListener() {

            @Override
            public void onShareClick(int position, RecyclerView_Model data) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, data.getSource() + "\n" + data.getText());
                startActivity(Intent.createChooser(shareIntent, "انتخاب کنید"));
            }

            @Override
            public void onCopyClick(int position, RecyclerView_Model data) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", data.getText());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getApplicationContext(), "کپی شد!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);

        LinearLayout reportTitle = bottomSheetDialog.findViewById(R.id.problem);
        LinearLayout first = bottomSheetDialog.findViewById(R.id.first);
        LinearLayout second = bottomSheetDialog.findViewById(R.id.second);
        LinearLayout third = bottomSheetDialog.findViewById(R.id.third);
        LinearLayout button = bottomSheetDialog.findViewById(R.id.btn);

        bottomSheetDialog.show();

    }


}