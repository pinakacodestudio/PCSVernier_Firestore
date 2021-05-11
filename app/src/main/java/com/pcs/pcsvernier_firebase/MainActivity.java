package com.pcs.pcsvernier_firebase;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pcs.pcsvernier_firebase.adapters.ItemParameterRSAdapter;
import com.pcs.pcsvernier_firebase.adapters.ItemSpinnerAdapter;
import com.pcs.pcsvernier_firebase.adapters.MachineSpinnerAdapter;
import com.pcs.pcsvernier_firebase.adapters.ProcessSpinnerAdapter;
import com.pcs.pcsvernier_firebase.adapters.UserSpinnerAdapter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    AppCompatAutoCompleteTextView spiUsers, spiItems, spiProcesses, spiMachines;
    ProgressBar progressBar;
    EditText edtHeight, edtLength, edtBore;
    LinearLayout lytSettingLayout, lytReadingLayout;
    Button btnChangeSettings, btnSaveReading, btnTakeReadings, btnResetSettings;
    TextView txtUser, txtMachine, txtProcess, txtItem;

    RecyclerView recyclerList;
    ItemParameterRSAdapter recyclerAdapter;
    ArrayList recyclerListData = null;

    static String valUser, valItem, valProcess, valMachine;
    FirebaseFirestore fStore;

    static int count;
    static JSONArray array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        fStore = FirebaseFirestore.getInstance();
        spiUsers = findViewById(R.id.spiUsers);
        spiItems = findViewById(R.id.spiItems);
        spiProcesses = findViewById(R.id.spiProcesses);
        spiMachines = findViewById(R.id.spiMachines);

        spiUsers.setOnItemClickListener(this);
        spiItems.setOnItemClickListener(this);
        spiProcesses.setOnItemClickListener(this);
        spiMachines.setOnItemClickListener(this);

        txtUser = findViewById(R.id.txtUser);
        txtItem = findViewById(R.id.txtItem);
        txtProcess = findViewById(R.id.txtProcess);
        txtMachine = findViewById(R.id.txtMachine);

        progressBar = findViewById(R.id.progressBar);

        //Recycler View
        recyclerList = findViewById(R.id.recyclerList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerList.setLayoutManager(mLayoutManager);
        recyclerList.setItemAnimator(new DefaultItemAnimator());
        recyclerList.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        recyclerListData = new ArrayList<Map<String, Object>>();
        recyclerAdapter = new ItemParameterRSAdapter(this, recyclerListData);
        recyclerList.setAdapter(recyclerAdapter);

        edtHeight = findViewById(R.id.edtHeight);
        edtLength = findViewById(R.id.edtLength);
        edtBore = findViewById(R.id.edtBore);

        lytSettingLayout = findViewById(R.id.lytSettingLayout);
        lytReadingLayout = findViewById(R.id.lytReadingLayout);

        btnChangeSettings = findViewById(R.id.btnChangeSettings);
        btnSaveReading = findViewById(R.id.btnSaveReading);
        btnTakeReadings = findViewById(R.id.btnTakeReadings);
        btnResetSettings = findViewById(R.id.btnResetSettings);
        btnChangeSettings.setOnClickListener(this);
        btnSaveReading.setOnClickListener(this);
        btnTakeReadings.setOnClickListener(this);
        btnResetSettings.setOnClickListener(this);

        loadSpinners();
    }

    private void loadSpinners() {
        spiUsers.setAdapter(new UserSpinnerAdapter(this));
        spiItems.setAdapter(new ItemSpinnerAdapter(this));
        spiProcesses.setAdapter(new ProcessSpinnerAdapter(this));
        spiMachines.setAdapter(new MachineSpinnerAdapter(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTakeReadings:
                lytSettingLayout.setVisibility(View.INVISIBLE);
                lytReadingLayout.setVisibility(View.VISIBLE);
                fetchItemParameters();
                break;

            case R.id.btnChangeSettings:
                lytSettingLayout.setVisibility(View.VISIBLE);
                lytReadingLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.btnResetSettings:
                MyUtil.readingMap.put(MyUtil.userKey, "");
                MyUtil.readingMap.put(MyUtil.itemKey, "");
                MyUtil.readingMap.put(MyUtil.processKey, "");
                MyUtil.readingMap.put(MyUtil.machineKey, "");

                spiItems.setText("");
                spiProcesses.setText("");
                spiMachines.setText("");
                spiUsers.setText("");
                break;
            case R.id.btnSaveReading:
//                saveReading();
                break;
        }
    }

    private void fetchItemParameters() {
        progressBar.setVisibility(View.VISIBLE);

        Object item = MyUtil.readingMap.get(MyUtil.itemKey);
        Object machine = MyUtil.readingMap.get(MyUtil.machineKey);
        Object process = MyUtil.readingMap.get(MyUtil.processKey);
        fStore = FirebaseFirestore.getInstance();
        fStore.collection("item_process_machine_parameter")
                .whereEqualTo("item", item)
                .whereEqualTo("machine", machine)
                .whereEqualTo("process", process)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        TextView txtTempOutput = findViewById(R.id.txtTempOutput);
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                recyclerListData.add(document.getData());
                            }
                            recyclerAdapter.notifyDataSetChanged();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void saveReading() {
        float height = Float.valueOf(edtHeight.getText().toString().trim());
        float length = Float.valueOf(edtHeight.getText().toString().trim());
        float bore = Float.valueOf(edtHeight.getText().toString().trim());

        MyUtil.readingMap.put(MyUtil.parameterKey, "height");
        MyUtil.readingMap.put(MyUtil.lowerToleranceKey, 52.000);
        MyUtil.readingMap.put(MyUtil.upperToleranceKey, 55.000);
        MyUtil.readingMap.put(MyUtil.readingKey, height);
        MyUtil.readingMap.put(MyUtil.resultKey, "ok");
        saveReadingToCloud(MyUtil.readingMap);

        MyUtil.readingMap.put(MyUtil.parameterKey, "length");
        MyUtil.readingMap.put(MyUtil.lowerToleranceKey, 112.000);
        MyUtil.readingMap.put(MyUtil.upperToleranceKey, 114.000);
        MyUtil.readingMap.put(MyUtil.readingKey, length);
        MyUtil.readingMap.put(MyUtil.resultKey, "ok");
        saveReadingToCloud(MyUtil.readingMap);

        MyUtil.readingMap.put(MyUtil.parameterKey, "bore");
        MyUtil.readingMap.put(MyUtil.upperToleranceKey, 11.000);
        MyUtil.readingMap.put(MyUtil.lowerToleranceKey, 11.500);
        MyUtil.readingMap.put(MyUtil.readingKey, bore);
        MyUtil.readingMap.put(MyUtil.resultKey, "ok");
        saveReadingToCloud(MyUtil.readingMap);
    }

    private void saveReadingToCloud(Map<String, Object> readingMap) {
        fStore.collection("readings")
                .add(readingMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Record Saved Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Record Save Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getAdapter() == spiUsers.getAdapter()) {
            String valUser = spiUsers.getText().toString();
            MyUtil.readingMap.put(MyUtil.userKey, valUser);
            MyUtil.showMessage(getApplicationContext(), valUser);
            txtUser.setText(valUser);
        }
        if (parent.getAdapter() == spiItems.getAdapter()) {
            String valItem = spiItems.getText().toString();
            MyUtil.readingMap.put(MyUtil.itemKey, valItem);
            MyUtil.showMessage(getApplicationContext(), valItem);
            txtItem.setText(valItem);
        }
        if (parent.getAdapter() == spiProcesses.getAdapter()) {
            String valProcess = spiProcesses.getText().toString();
            MyUtil.readingMap.put(MyUtil.processKey, valProcess);
            MyUtil.showMessage(getApplicationContext(), valProcess);
            txtProcess.setText(valProcess);
        }
        if (parent.getAdapter() == spiMachines.getAdapter()) {
            String valMachine = spiMachines.getText().toString();
            MyUtil.readingMap.put(MyUtil.machineKey, valMachine);
            MyUtil.showMessage(getApplicationContext(), valMachine);
            txtMachine.setText(valMachine);
        }
    }
}