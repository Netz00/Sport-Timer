package com.example.sporttimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import static java.lang.Integer.parseInt;

public class settings extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final TextView runde = (TextView) findViewById(R.id.editText3);
        final TextView vrimeRunde = (TextView) findViewById(R.id.editText);
        final TextView vrimePauze = (TextView) findViewById(R.id.editText2);
        final TextView delay = (TextView) findViewById(R.id.editText4);


        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); // 0 - for private mode
        final SharedPreferences.Editor editor = pref.edit();


            runde.setText(String.valueOf((pref.getInt("br_rundi", 4))));
            vrimeRunde.setText(String.valueOf((pref.getLong("vrime_runde", 6000)) / 1000));
            vrimePauze.setText(String.valueOf((pref.getLong("vrime_pauze", 3000) / 1000)));
            delay.setText(String.valueOf((pref.getLong("delay", 3000) / 1000)));



        runde.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    return;
                }
                int ulaz1 = parseInt(runde.getText().toString());
                editor.putInt("br_rundi", ulaz1);
                editor.commit();

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        vrimeRunde.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    return;
                }
                long ulaz2 = Long.parseLong(vrimeRunde.getText().toString()) * 1000;
                editor.putLong("vrime_runde", ulaz2);
                editor.commit();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        vrimePauze.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    return;
                }
                long ulaz3 = Long.parseLong(vrimePauze.getText().toString()) * 1000;
                editor.putLong("vrime_pauze", ulaz3);
                editor.commit();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        delay.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    return;
                }
                long ulaz4 = Long.parseLong(delay.getText().toString()) * 1000;
                editor.putLong("delay", ulaz4);
                editor.commit();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


    }
}