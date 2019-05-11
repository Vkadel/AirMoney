package com.vkreated.airmoney;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
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
    FragmentManager mFragmentManager;
    static String selectedItemID="";
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
        FloatingActionButton fab = findViewById(R.id.add_child_ledger_item_fab);
        final List<String> myIteration=new ArrayList<>();
        mFragmentManager=getSupportFragmentManager();
        adapter=new ChildrenLedgerAdapter(myIteration,this,mFragmentManager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(selectedItemID.equals(null)||selectedItemID.isEmpty()||selectedItemID=="")){
                    Intent intent=new Intent(getApplicationContext(),AddChildLedgerItem.class);
                    intent.putExtra(ledgerItemListFragment.ARG_LEDGER_ID,selectedItemID);
                    startActivity(intent);

                }else{
                    sendLongToast(getApplicationContext(),getResources().getString(R.string.please_select_a_ledger));
                }
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


                }
        });
        recyclerView.setAdapter(adapter);

    }


    public static class ChildrenLedgerAdapter extends RecyclerView.Adapter<ChildrenLedgerAdapter.MyViewHolder> {
        List<String> mDataset;
        Context mContext;
        FragmentManager fragmentManager;

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
        public ChildrenLedgerAdapter(List<String> myDataset,Context context,FragmentManager manager) {
            mDataset = myDataset;
            mContext= context;
            fragmentManager=manager;
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

        // Replace the c ontents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final int positionFinal=position;
            holder.textView.setText(mDataset.get(position));
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Generate the fragment and attach it to the frame
                    selectedItemID=mDataset.get(positionFinal);
                    Log.d(TAG,"this item was selected:"+selectedItemID);
                    Bundle arguments = new Bundle();
                    arguments.putString(ledgerItemListFragment.ARG_LEDGER_ID, mDataset.get(positionFinal));
                    ledgerItemListFragment fragment = new ledgerItemListFragment();
                    fragment.setArguments(arguments);
                    fragmentManager.beginTransaction()
                            .replace(R.id.ledgeritems_list_fragment_container, fragment)
                            .commit();
                }
            });

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
                    myTestTv.setText(intent.getStringExtra(Constants.RESULT_OF_SERVICE));
                }

    }


    private void startService()
    {
        Intent serviceIntent = new Intent();
        //Start service to download the data for each ledger
        //Pass the LedgerID you want to download
        serviceIntent.setAction(Constants.mAction_GET_LEDGER_ACTION);
        serviceIntent.putExtra(Constants.mAction_GET_LEDGER_DATA,"myLedger");
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

    public void sendLongToast(Context context,String message)
    {
        Toast toast=Toast.makeText(context,message,Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

}
