package com.vkreated.airmoney;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.data.ledgeritem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utils.SendALongToast;

import java.util.Arrays;
import java.util.Calendar;

public class AddChildLedgerItem extends AppCompatActivity {
    final static String TAG = "AddChildLedgerItem:";
    public static final String ARG_LEDGER_ID = "ledger_id";
    public static final String ARG_DATE_FROM_PICKER = "date_from_picker";
    TextView currency_label;
    TextView enterDate;
    TextView ledgerItemDescription;
    TextView value;
    TextView addLedgerItemDate;
    int direction;
    Switch directionSwitch;
    FloatingActionButton addLedgerItemtoFirebaseFAB;
    String childLedgerID;
    String ledgerType;
    String message = "";
    Spinner minutes;
    Spinner hours;
    int timeInMinutes;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra(ARG_LEDGER_ID)) {
            childLedgerID = getIntent().getStringExtra(ARG_LEDGER_ID);
        }
        if (getIntent().hasExtra(ARG_LEDGER_ID)) {
            ledgerType = getIntent().getStringExtra(ledgerItemListFragment.LEDGER_TYPE_ARG);
        }

        switch (Integer.parseInt(ledgerType)) {
            case 1:
                //Money
                setContentView(R.layout.activity_add_child_ledger_item);
                break;
            case 2:
                //Time
                setContentView(R.layout.activity_add_child_ledger_item_time);

                break;
            case 3:
                //Other
                setContentView(R.layout.activity_add_child_ledger_item_other);
                break;
        }
        mContext = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //enterDate.setText(R.string.today);
        enterDate = findViewById(R.id.add_ledger_item_date);
        addLedgerItemtoFirebaseFAB = findViewById(R.id.add_ledger_item_fab);
        ledgerItemDescription = findViewById(R.id.ledger_item_description_tv);
        value = findViewById(R.id.enter_value_of_transaction_et);
        directionSwitch = findViewById(R.id.direction_switch);
        currency_label = findViewById(R.id.currency_label);
        directionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String message;
                if (directionSwitch.isChecked()) {
                    directionSwitch.setText(R.string.direction_plus);
                    directionSwitch.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                    message = getResources().getString(R.string.this_will_add_money);
                } else {
                    directionSwitch.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                    directionSwitch.setText(R.string.direction_minus);
                    message = getResources().getString(R.string.this_will_subtract_money);
                }
                new SendALongToast(mContext, message).show();
            }
        });

        addLedgerItemtoFirebaseFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if there is a childLedger
                if (!(childLedgerID.isEmpty() || childLedgerID.equals(null) || childLedgerID.equals(""))
                        && checkInputsAreOk()) {
                    uploadItemtoFirebase(childLedgerID);
                }
            }
        });

        //SetUp the UI
        switch (Integer.parseInt(ledgerType)) {
            case 1:
                //Money
                break;
            case 2:
                //Time
                minutes = findViewById(R.id.spinner_minutes);
                hours = findViewById(R.id.spinner_hours);
                value.setHint("           ");
                value.setVisibility(View.INVISIBLE);
                enterDate.setHint(getResources().getString(R.string.add_date));
                currency_label.setText("");
                break;
            case 3:
                //Other
                currency_label.setText("");
                ;
                break;
        }

    }

    public TextView getEnterDate() {
        return enterDate;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private boolean checkInputsAreOk() {
        Boolean isOk = true;
        message = "";
        if (Integer.parseInt(ledgerType) == 2) {
            calculateTime();
        }
        if (ledgerItemDescription.getText().toString().equals("") ||
                ledgerItemDescription.getText().equals(null)||ledgerItemDescription.getText().toString().trim().isEmpty()) {
            isOk = false;
            message = getResources().getString(R.string.add_description);
        }
        if (value.getText().equals(null) || value.getText().toString().equals("") || Integer.parseInt(value.getText().toString().trim()) == 0) {
            isOk = false;
            message = getResources().getString(R.string.add_value);
        }
        if (enterDate.getText().toString().equals("") ||
                enterDate.getText().equals(null)||enterDate.getText().toString().trim().isEmpty()) {
            isOk = false;
            message = getResources().getString(R.string.add_date);
        }
        if (!isOk) {
            new SendALongToast(mContext, message).show();
        }
        return isOk;

    }

    private void uploadItemtoFirebase(String ledgerId) {
        final String ledgerItemId = ledgerId + Calendar.getInstance().getTime();
        ledgerId.replace(" ", "");
        ledgerId.trim();
        final int direction = getDirection();

        //Get current Total
        final String myReftoChangeTotalString = getResources().getString(R.string.firebase_ref_child_ledger_total, ledgerId);
        final DatabaseReference myReftoChangeTotal = FirebaseDatabase.getInstance().getReference(myReftoChangeTotalString);
        myReftoChangeTotal.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Determine total
                double myTotal = dataSnapshot.getValue(Double.class);
                double newTotal = Double.valueOf(0);

                if (direction == 1) {
                    newTotal = myTotal + Long.parseLong(value.getText().toString());
                }
                if (direction == 0) {
                    newTotal = myTotal - Long.parseLong(value.getText().toString());
                }

                //Set Total
                myReftoChangeTotal.setValue(newTotal);

                //Send Item up once the total is updated
                ledgeritem mledgerItem = new ledgeritem(ledgerItemId, 1, FirebaseAuth.getInstance().getUid(),
                        Long.parseLong(value.getText().toString()), ledgerItemDescription.getText().toString(),
                        direction, FirebaseAuth.getInstance().getUid(), childLedgerID, enterDate.getText().toString());
                DatabaseReference addledgerItemRef = FirebaseDatabase.getInstance().getReference
                        (getString(R.string.firebase_ref_mchildren_mchilLedgerItem_add_one, ledgerItemId));
                addledgerItemRef.setValue(mledgerItem);
                Intent intent = new Intent(getApplicationContext(), ChildrenLedgerListActivity.class);
                intent.setAction(ChildrenLedgerListActivity.RESTART_ACTION);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int getDirection() {
        int direction;
        if (directionSwitch.isChecked()) {
            direction = 1;
        } else {
            direction = 0;
        }
        return direction;
    }

    private void calculateTime() {
        String currentMinSelected = minutes.getSelectedItem().toString().trim();
        String currentHourSelected = hours.getSelectedItem().toString().trim();
        String baseLineMin = Arrays.asList(getResources().getStringArray(R.array.spinner_for_time_minutes)).get(0).trim();
        String baselineHour = Arrays.asList(getResources().getStringArray(R.array.spinner_for_time_hours)).get(0).trim();
        int TotalMin = 0;
        int minMin = 0;
        int minHour = 0;
        if (!currentMinSelected.equals(baseLineMin)) {
            minMin = Integer.parseInt((String) minutes.getSelectedItem());
        }
        if (!currentHourSelected.equals(baselineHour)) {
            minHour = Integer.parseInt((String) hours.getSelectedItem()) * 60;
        }
        timeInMinutes = minMin + minHour;
        value.setText(String.valueOf(timeInMinutes));
    }

}
