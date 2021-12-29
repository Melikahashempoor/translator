package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    String[] items = {"فارسی", "انگلیسی", "عربی", "لغتنامه دهخدا", "فرهنگ معین", "فرهنگ عمید", "مترادف و متضاد", "فرهنگ نام ها", "واژه نامه آزاد"};

    AutoCompleteTextView autoCompleteTextView1, autoCompleteTextView2, editText;
    ArrayAdapter<String> adapterItems, adapterItemsReverse, suggestionList;
    ImageButton paste, reverse, clear;
    AppCompatButton translate;
    String pasteData = "";

    private Call<suggestionModel> suggestions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autoCompleteTextView1 = findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView2 = findViewById(R.id.autoCompleteTextView2);
        editText = findViewById(R.id.searchText);
        paste = findViewById(R.id.pasteBtn);
        reverse = findViewById(R.id.reverseBtn);
        clear = findViewById(R.id.clearBtn);
        translate = findViewById(R.id.translateBtn);

        clear.setVisibility(View.GONE);

        adapterItems = new ArrayAdapter<String>(this, R.layout.dropdown_item, items);
        autoCompleteTextView1.setAdapter(adapterItems);
        autoCompleteTextView2.setAdapter(adapterItems);
        adapterItems.setNotifyOnChange(true);

        // paste from clipboard to editText
        paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                // if the clipboard does not contain the data
                if (!(clipboardManager.hasPrimaryClip())) {
                    Toast.makeText(MainActivity.this, "متنی در کلیپ بورد شما وجود ندارد", Toast.LENGTH_SHORT).show();
                    return;
                }
                // if the data in clipboard was not plain text
                else if (!(clipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))) {
                    Toast.makeText(MainActivity.this, "فقط امکان paste کردن متن وجود دارد", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
                    pasteData = item.getText().toString();
                    editText.setText(pasteData);
                }
            }
        });

        // reversing autoCompleteTextViews texts
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (autoCompleteTextView1.getText().toString().equals(autoCompleteTextView2.getText().toString())) {
                    Toast.makeText(MainActivity.this, "زبان مبدا و مقصد نمیتوانند یکسان باشند!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String lan1 = autoCompleteTextView1.getText().toString();
                autoCompleteTextView1.setText(autoCompleteTextView2.getText().toString());
                autoCompleteTextView2.setText(lan1);
                adapterItemsReverse = new ArrayAdapter<String>(MainActivity.this, R.layout.dropdown_item, items);
                autoCompleteTextView1.setAdapter(adapterItemsReverse);
                autoCompleteTextView2.setAdapter(adapterItemsReverse);
                adapterItemsReverse.notifyDataSetChanged();
            }
        });

        // clear text
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });

        // translate button method
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String q = editText.getText().toString();
                String lan1 = autoCompleteTextView1.getText().toString();
                String lan2 = autoCompleteTextView2.getText().toString();
                if (q.trim().equals("")) {
                    Toast.makeText(MainActivity.this, "متنی برای ترجمه وارد نشده است!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (lan1.equals(lan2)) {
                    Toast.makeText(MainActivity.this, "زبان مبدا و مقصد نمیتوانند یکسان باشند!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
                intent.putExtra("q", q);
                intent.putExtra("lan1", lan1);
                intent.putExtra("lan2", lan2);
                startActivity(intent);
            }
        });

        // change editText text method
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (editText.isPerformingCompletion()) {
                    editText.dismissDropDown();
                }
                else if (editText.getText().length() > 0) {
                    getSuggestion(ApiUtilities.token, editText.getText().toString());
                    clear.setVisibility(View.VISIBLE);
                }
                else {
                    clear.setVisibility(View.GONE);
                }
            }

            public void afterTextChanged(Editable editable) {

            }
        });

    }

    // get suggestion list method
    private void getSuggestion(String token, String q) {

        ApiInterface apiInterface = ApiUtilities.getApiInterface(this).create(ApiInterface.class);
        suggestions = apiInterface.suggest(token, q);

        suggestions.enqueue(new Callback<suggestionModel>() {
            @Override
            public void onResponse(Call<suggestionModel> call, Response<suggestionModel> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().data == null) {
                    Toast.makeText(MainActivity.this, "مشکلی در ارتباط با سرور پیش آمده است!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.body() != null) {
                    suggestionList = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, response.body().data.suggestion);
                    editText.setAdapter(suggestionList);
                    if (!editText.isPopupShowing())
                        editText.showDropDown();
                }

            }

            @Override
            public void onFailure(Call<suggestionModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, "مشکلی در ارتباط با سرور پیش آمده است!", Toast.LENGTH_SHORT).show();
                return;
            }

        });

    }


}