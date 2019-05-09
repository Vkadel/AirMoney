package com.vkreated.airmoney;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedListAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.data.FirebaseLiveDataLedgers;
import com.data.LedgerIdAtParent;
import com.google.firebase.database.DataSnapshot;
import com.intentservices.Constants;
import com.intentservices.GetLedgersIntentService;
import com.viewmodels.ledgersViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChildrenLedgerListActivity extends AppCompatActivity {
    final static String TAG = "ChildrenLedgerListAc";
    RecyclerView mRecycler;
    ChildrenLedgerAdapter adapter;
    DownloadStateReceiver downloadStateReceiver;
    TextView myTestTv;

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadStateReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_ledger_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myTestTv=findViewById(R.id.text_tv);
        FloatingActionButton fab = findViewById(R.id.fab);
        final List<String> myIteration=new ArrayList<>();
        adapter=new ChildrenLedgerAdapter(myIteration);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.list_of_children_ledgers);

        startService();

        //Get User children ledgers list
        ledgersViewModel mViewModel = ViewModelProviders.of(this).get(ledgersViewModel.class);

        FirebaseLiveDataLedgers ledgersUnderUser = mViewModel.getLedgers(this);

        ledgersUnderUser.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                long childCount = dataSnapshot.getChildrenCount();
                Log.d(TAG, "Child Count: " + childCount + dataSnapshot.getValue().toString());

                    dataSnapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
                        @Override
                        public void accept(DataSnapshot dataSnapshot) {
                            Log.d(TAG,"entered loop with this:"+dataSnapshot.getKey().toString());
                            //Add to Room
                            myIteration.add(dataSnapshot.getKey());

                        }
                    });
                    adapter.notifyDataSetChanged();
                   //TODO: Pull each ledger may need to save in local database use Service
                }
        });
        recyclerView.setAdapter(adapter);
    }


    public static class ChildrenLedgerAdapter extends RecyclerView.Adapter<ChildrenLedgerAdapter.MyViewHolder> {
        List<String> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView textView;
            public MyViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.child_ledger_name);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public ChildrenLedgerAdapter(List<String> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ChildrenLedgerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            // create a new view
            View v =  LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.child_ledger_for_list, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.textView.setText(mDataset.get(position));

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }


    // Broadcast receiver for receiving status updates from the IntentService.
    private class DownloadStateReceiver extends BroadcastReceiver
    {
        Context mContext;
        DownloadStateReceiver(Context context){
            mContext=context;
        }
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(final Context context, Intent intent) {
                    //android.os.Debug.waitForDebugger();
                    sendLongToast(mContext,"Recieved broadcast from Service + "+intent.getStringExtra(Constants.mMESSAGE));
                    Log.e(TAG,"Received broadcast from Service");
                    myTestTv.setText(intent.getStringExtra(Constants.EXTENDED_DATA_STATUS));
                }

    }


    private void startService()
    {
        Intent serviceIntent = new Intent();
        serviceIntent.putExtra("download_url", "http");
        // Starts the JobIntentService
        final int RSS_JOB_ID = 1000;
        GetLedgersIntentService service=new GetLedgersIntentService();
        service.enqueueWork(this, GetLedgersIntentService.class, RSS_JOB_ID, serviceIntent);
        sendLongToast(getApplicationContext(), "sent job");
        // The filter's action is BROADCAST_ACTION
        IntentFilter statusIntentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION);

        // Adds a data filter for the HTTP scheme
        //statusIntentFilter.addDataScheme("http");

        // Instantiates a new DownloadStateReceiver

        downloadStateReceiver= new DownloadStateReceiver(this);
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                downloadStateReceiver,
                statusIntentFilter);
    }

    private void sendLongToast(Context context,String message)
    {
        Toast toast=Toast.makeText(context,message,Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

}
