package com.example.timeadder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button btnAdd, btnMeridiem, btnClear, btnRemove, btnManualAdd, btnCurrentAdd, btnRestore;
    private TextView txtList, txtCalc, txtTest;
    private EditText edtHour, edtMinute, edtSecond;
    private RadioButton rbMeridiem, rbMilitary;
    private RelativeLayout layoutRelativeManual;

    private timeStampManipulator times;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String IS_MERIDIAN = "AM";
    public static final String AM_PM = "btnMeridian";

    private String AmOrPm;
    private boolean isMeridian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        btnMeridiem = findViewById(R.id.btnMeridiem);
        btnClear = findViewById(R.id.btnClear);
        btnRemove = findViewById(R.id.btnRemove);
        btnManualAdd = findViewById(R.id.btnManualAdd);
        btnCurrentAdd = findViewById(R.id.btnCurrentAdd);
        btnRestore = findViewById(R.id.btnRestore);

        txtList = findViewById(R.id.txtList);
        txtCalc = findViewById(R.id.txtCalc);
        txtTest = findViewById(R.id.txtTest);

        edtHour = findViewById(R.id.edtHour);
        edtMinute = findViewById(R.id.edtMinute);
        edtSecond = findViewById(R.id.edtSecond);

        rbMeridiem = findViewById(R.id.rbMeridiem);
        rbMilitary = findViewById(R.id.rbMilitary);

        layoutRelativeManual = findViewById(R.id.layoutRelativeManualAdd);

        loadData();
        loadPreferences();
        updatePreferences();

        txtList.setMovementMethod(new ScrollingMovementMethod());

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Checks if the whole LayoutLinearTime is empty
                if (!(edtHour.getText().toString().equals("") && edtMinute.getText().toString().equals("") && edtSecond.getText().toString().equals(""))) {
                    // Adds "00" to empty spaces
                    if (edtHour.getText().toString().equals("")) {
                        edtHour.setText("00");
                    }
                    Toast.makeText(MainActivity.this, "Goes into", Toast.LENGTH_SHORT).show();

                    if (edtMinute.getText().toString().equals("")) {
                        edtMinute.setText("00");
                    }

                    if (edtSecond.getText().toString().equals("")) {
                        edtSecond.setText("00");
                    }

                    // Formats to 24 hour time
                    if (Integer.parseInt(edtHour.getText().toString()) < 12 && btnMeridiem.getVisibility() == View.VISIBLE && btnMeridiem.getText().toString().equals("PM")) {
                        times.add(new timeStamp(Integer.parseInt(edtHour.getText().toString()) + 12, Integer.parseInt(edtMinute.getText().toString()), Integer.parseInt(edtSecond.getText().toString())));
                    // If already in military time, adds to 'times' array
                    } else {
                        times.add(new timeStamp(Integer.parseInt(edtHour.getText().toString()), Integer.parseInt(edtMinute.getText().toString()), Integer.parseInt(edtSecond.getText().toString())));
                    }
                    // Displays new time
                    updateTxtListAndCalc();

                    // Clears layoutLinearTime
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
                    btnMeridiem.setVisibility(View.VISIBLE);
                } else {
                    btnMeridiem.setVisibility(View.GONE);
                }
                txtList.setText(times.sortedToString(rbMeridiem.isChecked()));
                savePreferences();
            }
        });

        btnMeridiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchBtnMeridiemText();
                savePreferences();
            }
        });

        btnManualAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutRelativeManual.getVisibility() == View.VISIBLE) {
                    layoutRelativeManual.setVisibility(View.INVISIBLE);
                } else {
                    layoutRelativeManual.setVisibility(View.VISIBLE);
                }
            }
        });

        btnCurrentAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean switchback = false;
                if (rbMeridiem.isChecked()) {
                    switchback = true;
                    switchBtnMeridiemText();
                }
                Calendar curTime = Calendar.getInstance();

                edtHour.setText(Integer.toString(curTime.get(Calendar.HOUR_OF_DAY)));
                edtMinute.setText(Integer.toString(curTime.get(Calendar.MINUTE)));
                edtSecond.setText(Integer.toString(curTime.get(Calendar.SECOND)));

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
                savePreferences();
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

        btnRestore.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                times.restore();
                updateTxtListAndCalc();
                saveData();
                return false;
            }
        });
    }

    // My understanding is that "gson" is a way to convert a java object into a string,
    // and this method just does all of the conversions all at once.
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

    // 'Preferences' functions only save the radio group choice and the AM or PM on btnMeridiem
    public void savePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_MERIDIAN, rbMeridiem.isChecked());
        editor.putString(AM_PM, btnMeridiem.getText().toString());

        editor.apply();
    }

    public void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        isMeridian = sharedPreferences.getBoolean(IS_MERIDIAN, true);
        AmOrPm = sharedPreferences.getString(AM_PM, "AM");

    }

    public void updatePreferences() {
        btnMeridiem.setText(AmOrPm);
        rbMeridiem.setChecked(isMeridian);
        rbMilitary.setChecked(!isMeridian);

        if (isMeridian) {
            btnMeridiem.setVisibility(View.VISIBLE);
        } else {
            btnMeridiem.setVisibility(View.GONE);
        }
    }

    public void updateTxtListAndCalc() {
        txtList.setText(times.sortedToString(rbMeridiem.isChecked()));
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