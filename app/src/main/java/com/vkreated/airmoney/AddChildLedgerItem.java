package com.vkreated.airmoney;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AddChildLedgerItem extends AppCompatActivity {
    final static String TAG = "AddChildLedgerItem:";
    public static final String ARG_LEDGER_ID = "ledger_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child_ledger_item);
    }
}
