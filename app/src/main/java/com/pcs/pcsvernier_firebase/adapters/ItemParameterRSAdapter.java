package com.pcs.pcsvernier_firebase.adapters;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pcs.pcsvernier_firebase.MyUtil;
import com.pcs.pcsvernier_firebase.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class ItemParameterRSAdapter extends RecyclerView.Adapter<ItemParameterRSAdapter.IPViewHolder> {
    Context context;
    ArrayList recyclerListData;
    FirebaseFirestore fStore;

    public ItemParameterRSAdapter(Context context, ArrayList recyclerListData) {
        this.context = context;
        this.recyclerListData = recyclerListData;
    }

    @NonNull
    @Override
    public ItemParameterRSAdapter.IPViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parameter_list_design, parent, false);
        return new IPViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemParameterRSAdapter.IPViewHolder holder, int position) {
        Map map = (Map) recyclerListData.get(position);
        holder.txtParameter.setText(String.valueOf(map.get("parameter")));
        holder.txtLTolerance.setText(String.valueOf(map.get("lower_tolerance")));
        holder.txtUTolerance.setText(String.valueOf(map.get("upper_tolerance")));
    }

    @Override
    public int getItemCount() {
        return recyclerListData.size();
    }

    public class IPViewHolder extends RecyclerView.ViewHolder {
        TextView txtParameter, txtLTolerance, txtUTolerance;
        EditText edtReading;
        Button btnSave;
        ProgressBar inlineProgressBar;
        boolean saveFlag = true;

        public IPViewHolder(@NonNull View itemView) {
            super(itemView);
            txtParameter = itemView.findViewById(R.id.txtParameter);
            txtLTolerance = itemView.findViewById(R.id.txtLTolerance);
            txtUTolerance = itemView.findViewById(R.id.txtUTolerance);
            edtReading = itemView.findViewById(R.id.edtReading);
            edtReading.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    Log.i("KeyPressed", String.valueOf(event));
                    if (keyCode == KeyEvent.KEYCODE_ENTER && saveFlag) {
                        btnSave.callOnClick();
                        saveFlag = false;
                        edtReading.setText("");
                        return true;
                    }
                    return false;
                }
            });
            btnSave = itemView.findViewById(R.id.btnSave);
            inlineProgressBar = itemView.findViewById(R.id.inlineProgressBar);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String reading = edtReading.getText().toString().trim();
                    if (reading.equals("")) {
                        edtReading.setError("Reading can not empty.");
                        return;
                    }
                    inlineProgressBar.setVisibility(View.VISIBLE);
                    double readingValue = Double.valueOf(reading);
                    double lTolerance = Double.valueOf(txtLTolerance.getText().toString().trim());
                    double uTolerance = Double.valueOf(txtUTolerance.getText().toString().trim());
                    int result = 1;

                    if (readingValue > uTolerance || readingValue < lTolerance) {
                        edtReading.setError("Out of tolerance");
                        result = 0;
                    }

                    MyUtil.readingMap.put(MyUtil.parameterKey, txtParameter.getText().toString());
                    MyUtil.readingMap.put(MyUtil.lowerToleranceKey, lTolerance);
                    MyUtil.readingMap.put(MyUtil.upperToleranceKey, uTolerance);
                    MyUtil.readingMap.put(MyUtil.readingKey, reading);
                    MyUtil.readingMap.put(MyUtil.resultKey, result);

                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Calendar cal = Calendar.getInstance();
                    MyUtil.readingMap.put(MyUtil.dateTimeKey, dateFormat.format(cal.getTime()));

                    fStore = FirebaseFirestore.getInstance();
                    fStore.collection("readings")
                            .add(MyUtil.readingMap)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        MyUtil.showMessage(context, "Record Saved Successfully");
                                        edtReading.setText("");
                                    } else {
                                        MyUtil.showMessage(context, "Record Save Failed.");
                                    }
                                    inlineProgressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                }
            });
        }
    }


}
