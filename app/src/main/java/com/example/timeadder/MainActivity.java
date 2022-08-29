package com.example.timeadder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private Button btnAdd, btnMeridiem, btnClear, btnRemove;
    private TextView txtList, txtCalc;
    private EditText edtHour, edtMinute, edtSecond;
    private RadioButton rbMeridiem;
    private TextClock txtClock;

    private timeStampManipulator times;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        btnMeridiem = findViewById(R.id.btnMeridiem);
        btnClear = findViewById(R.id.btnClear);
        btnRemove = findViewById(R.id.btnRemove);

        txtList = findViewById(R.id.txtList);
        txtCalc = findViewById(R.id.txtCalc);
        txtClock = findViewById(R.id.txtClock);

        edtHour = findViewById(R.id.edtHour);
        edtMinute = findViewById(R.id.edtMinute);
        edtSecond = findViewById(R.id.edtSecond);

        rbMeridiem = findViewById(R.id.rbMeridiem);


//        times = new timeStampManipulator();
        loadData();

        txtList.setMovementMethod(new ScrollingMovementMethod());

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(edtHour.getText().toString().equals("") && edtMinute.getText().toString().equals("") && edtSecond.getText().toString().equals(""))) {
                    if (edtHour.getText().toString().equals("")) {
                        edtHour.setText("00");
                    }

                    if (edtMinute.getText().toString().equals("")) {
                        edtMinute.setText("00");
                    }

                    if (edtSecond.getText().toString().equals("")) {
                        edtSecond.setText("00");
                    }

                    if (Integer.parseInt(edtHour.getText().toString()) < 12 && btnMeridiem.getVisibility() == View.VISIBLE && btnMeridiem.getText().toString().equals("PM")) {
                        times.add(new timeStamp(Integer.parseInt(edtHour.getText().toString()) + 12, Integer.parseInt(edtMinute.getText().toString()), Integer.parseInt(edtSecond.getText().toString())));
                    } else {
                        times.add(new timeStamp(Integer.parseInt(edtHour.getText().toString()), Integer.parseInt(edtMinute.getText().toString()), Integer.parseInt(edtSecond.getText().toString())));
                    }

                    updateTxtListAndCalc();

                    edtHour.getText().clear();
                    edtMinute.getText().clear();
                    edtSecond.getText().clear();
                    saveData();
                } else {
                    Toast.makeText(MainActivity.this, "Enter a time!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edtHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int cont) {
                if (cont >= 2) {
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                edtHour.requestFocus(edtHour.getNextFocusRightId());
            }
        });

        rbMeridiem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    txtClock.setFormat24Hour(null);
                    txtClock.setFormat12Hour("hh:mm:ss a");
                    btnMeridiem.setVisibility(View.VISIBLE);
                } else {
                    txtClock.setFormat12Hour(null);
                    txtClock.setFormat24Hour("HH:mm:ss");
                    btnMeridiem.setVisibility(View.INVISIBLE);
                }
                txtList.setText(times.sortedToString(rbMeridiem.isChecked()));
            }
        });

        btnMeridiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchBtnMeridiemText();
            }
        });

        txtClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Here's how I checked how txtClock.getText() works.

//                String temp = txtClock.getText().toString();
//                String temp2 = "";
//                for (int i = 0; i < temp.length(); i++) {
//                    temp2 += "[" + temp.substring(i,i+1) + "] ";
//                }
//                txtCalc.setText(temp2);
                boolean switchback = false;

                if (rbMeridiem.isChecked() && !txtClock.getText().subSequence(9, 11).equals(btnMeridiem.getText())) {
                    switchback = true;
                    switchBtnMeridiemText();
                }
                edtHour.setText(txtClock.getText().subSequence(0,2));
                edtMinute.setText(txtClock.getText().subSequence(3, 5));
                edtSecond.setText(txtClock.getText().subSequence(6, 8));

                btnAdd.performClick();

                if (switchback) {
                    switchBtnMeridiemText();
                }
            }
        });

        btnClear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                times.clear();
                updateTxtListAndCalc();
                saveData();
                return false;
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Just removes the most recent one for now. may add buttons on the side of
                // txtList that will scroll up and down txtList.
                if (!times.getLog().isEmpty()) {
                    times.remove(times.getLog().size()-1);
                    updateTxtListAndCalc();
                    saveData();
                } else {
                    Toast.makeText(MainActivity.this, "No data to remove.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveData() {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(times);
        prefsEditor.putString("times", json);
        prefsEditor.commit();
    }

    public void loadData() {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("times", "");
        times = gson.fromJson(json, timeStampManipulator.class);

        if (times == null) {
            times = new timeStampManipulator();
        }

        updateTxtListAndCalc();
    }

    public void updateTxtListAndCalc() {
        txtList.setText(times.sortedToString(rbMeridiem.isChecked()));
//        txtCalc.setText(times.getTimeTotal().formatCalculatedTime(times.getSorted()));
        txtCalc.setText(times.totalToString());
    }

    public void switchBtnMeridiemText() {
        if (btnMeridiem.getText().toString().equals("PM")) {
            btnMeridiem.setText("AM");
        } else {
            btnMeridiem.setText("PM");
        }
    }
}