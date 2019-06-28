package com.vkreated.airmoney;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.data.FirebaseLiveDataLedgers;
import com.data.FirebaseLiveDataLedgersLedgerItemAdd;
import com.data.childledger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.intentservices.Constants;
import com.intentservices.GetLedgersIntentService;
import com.utils.ConvertMinsToStringSec;
import com.utils.SendALongToast;
import com.viewmodels.ledgersViewModel;
import com.viewmodels.ledgersViewModelFull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ChildrenLedgerListActivity extends AppCompatActivity {
    final static public String RESTART_ACTION="restart_activty";
    final static String TAG = "ChildrenLedgerListAc";
    RecyclerView mRecycler;
    ChildrenLedgerAdapter adapter;
    DownloadStateReceiver downloadStateReceiver;
    TextView myTestTv;
    FragmentManager mFragmentManager;
    static String selectedItemID="";
    static String selectedTypeOfLedger="";
    final List<childledger> myIteration=new ArrayList<>();
    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadStateReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast toast;
        switch (item.getItemId()) {
            case R.id.add_child:
                //startActivity(new Intent(this, About.class));
                toast = Toast.makeText(this, "I will add a child", Toast.LENGTH_SHORT);
                toast.show();
                Intent gotoChildLedgerSetup = new Intent(this, AddChildLedgerActivity.class);
                gotoChildLedgerSetup.putExtra(AddChildLedgerActivity.USER_ID_LABEL, FirebaseAuth.getInstance().getUid());
                startActivity(gotoChildLedgerSetup);
                return true;
            case R.id.edit_child:
                //startActivity(new Intent(this, Help.class));
                toast = Toast.makeText(this, "I will edit this child", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            case R.id.help_menu:
                //startActivity(new Intent(this, Help.class));
                toast = Toast.makeText(this, "We will help you", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            case R.id.log_out:
                //startActivity(new Intent(this, Help.class));
                toast = Toast.makeText(getApplication(), getResources().getString(R.string.logging_out), Toast.LENGTH_LONG);
                toast.show();
                /*logWithAnotherAccount();
                authenticationKickoff();*/
                return true;
            case R.id.sign_in:
                //startActivity(new Intent(this, Help.class));
                toast = Toast.makeText(getApplication(), getResources().getString(R.string.logging), Toast.LENGTH_LONG);
                toast.show();
                /*  authenticationKickoff();*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myIteration.clear();
        setContentView(R.layout.activity_children_ledger_list);
        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.ledgers));
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();

        ab.setDisplayHomeAsUpEnabled(true);

        myTestTv=findViewById(R.id.my_Total);
        FloatingActionButton fab = findViewById(R.id.add_child_ledger_item_fab);

        mFragmentManager=getSupportFragmentManager();
        adapter=new ChildrenLedgerAdapter(myIteration,this,mFragmentManager);
        final Activity context=this;


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!(selectedItemID.equals(null)||selectedItemID.isEmpty()||selectedItemID=="")){
                        //Money Ledger
                        Intent intent=new Intent(getApplicationContext(),AddChildLedgerItem.class);
                        intent.putExtra(ledgerItemListFragment.ARG_LEDGER_ID,selectedItemID);
                        intent.putExtra(ledgerItemListFragment.LEDGER_TYPE_ARG,selectedTypeOfLedger);
                        startActivity(intent);

                }else{
                    sendLongToast(getApplicationContext(),getResources().getString(R.string.please_select_a_ledger));
                }
            }
        });

        RecyclerView recyclerView = findViewById(R.id.list_of_children_ledgers);



        //Get User children ledgers list
        ledgersViewModel mViewModel = ViewModelProviders.of(this).get(ledgersViewModel.class);

        FirebaseLiveDataLedgers ledgersUnderUser = mViewModel.getLedgers(this);

        ledgersUnderUser.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                //Clear the iteration list otherwise additional items that are in the item will show
                myIteration.clear();
                long childCount = dataSnapshot.getChildrenCount();
                Log.d(TAG, "Child Count: " + childCount + dataSnapshot.getValue().toString());
                //Add each of the childre and subscribe via a model to get updates on the
                //any ledger changes.
                    dataSnapshot.getChildren().forEach(new Consumer<DataSnapshot>() {

                        @Override
                        public void accept(DataSnapshot dataSnapshot) {
                            ledgersViewModelFull theLedgerViewModel=new ledgersViewModelFull();
                            //Observe the ledgerItems to add each item that gets updated
                            theLedgerViewModel.getLedgers(getApplicationContext(),dataSnapshot.getKey())
                                    .observe((LifecycleOwner) context, new Observer<DataSnapshot>() {
                                @Override
                                public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                                    final childledger mledger=dataSnapshot.getValue(childledger.class);
                                    if(!(myIteration.contains(mledger))){
                                        RemoveExistingItemsfromList(mledger, myIteration);
                                        myIteration.add(mledger);
                                        //Sort Items after adding the missing item
                                        sortMyListofChildledgersByName(myIteration);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    });

                }
        });
        recyclerView.setAdapter(adapter);

    }

    private void RemoveExistingItemsfromList(final childledger mledger, List<childledger> myIteration) {
        myIteration.removeIf(new Predicate<childledger>() {
            @Override
            public boolean test(childledger childledger) {
                return mledger.getMledgerid().equals(childledger.getMledgerid());
            }
        });
    }

    private void sortMyListofChildledgersByName(List<childledger> myIteration) {
        myIteration.sort(new Comparator<childledger>() {
            @Override
            public int compare(childledger o1, childledger o2) {
                return o1.getMchildname().compareTo(o2.getMchildname());
            }
        });
    }


    public static class ChildrenLedgerAdapter extends RecyclerView.Adapter<ChildrenLedgerAdapter.MyViewHolder> {
        List<childledger> mDataset;
        Context mContext;
        FragmentManager fragmentManager;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView childLedgerNameTV;
            public TextView myTotalTV;
            public TextView symbolTV;
            public LinearLayout cell;
            public MyViewHolder(View v) {
                super(v);
                childLedgerNameTV = v.findViewById(R.id.child_ledger_name);
                myTotalTV=v.findViewById(R.id.child_ledger_total);
                symbolTV=v.findViewById(R.id.currency_symbol);
                cell=v.findViewById(R.id.my_child_ledgername_layout);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public ChildrenLedgerAdapter(List<childledger> myDataset,Context context,FragmentManager manager) {
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

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final int positionFinal=position;

            View.OnClickListener listener= new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Get recycler View info depending on user click
                    View parent=(View)v.getParent().getParent();
                    try{
                    RecyclerView rv=(RecyclerView) parent.getParent();
                    rv.getAdapter().notifyDataSetChanged();}catch
                    (Exception io){
                        //
                    }
                    try{
                        RecyclerView rv=(RecyclerView) parent.getParent().getParent();
                        rv.getAdapter().notifyDataSetChanged();}catch
                    (Exception io){
                        //
                    }

                    //Generate the fragment and attach it to the frame
                    selectedItemID=mDataset.get(positionFinal).getMledgerid();
                    selectedTypeOfLedger=mDataset.get(positionFinal).getMunit();
                    Log.d(TAG,"this item was selected:"+selectedItemID);
                    Bundle arguments = new Bundle();
                    arguments.putString(ledgerItemListFragment.ARG_LEDGER_ID, selectedItemID);
                    arguments.putString(ledgerItemListFragment.LEDGER_TYPE_ARG,mDataset.get(position).getMunit());
                    ledgerItemListFragment fragment = new ledgerItemListFragment();
                    fragment.setArguments(arguments);
                    fragmentManager.beginTransaction()
                            .replace(R.id.ledgeritems_list_fragment_container, fragment)
                            .commit();

                }
            };
            holder.cell.setBackgroundColor(mContext.getColor(R.color.whiteColor));
            holder.childLedgerNameTV.setText(mDataset.get(position).getMchildname());
            int check=Integer.parseInt(mDataset.get(position).getMunit());

            switch (check){
                default:
                    //This is a money Ledger
                    holder.myTotalTV.setText(String.valueOf(mDataset.get(position).getMledgetotal()));
                    break;
                case 2:
                    //This ledger is a Time ledger
                    holder.symbolTV.setText("");
                    //This ledger is a Othertype ledger
                    ConvertMinsToStringSec mConversion=new ConvertMinsToStringSec
                            (mDataset.get(position).getMledgetotal(),mContext);
                    holder.myTotalTV.setText(mConversion.getTheTime());
                    break;
                case 3:
                    //This ledger is a Othertype ledger
                    holder.symbolTV.setText(mContext.getResources()
                            .getString(R.string.other));
                    //This ledger is a Othertype ledger
                    holder.myTotalTV.setText(String.valueOf(mDataset.get(position).getMledgetotal()));
                    break;

            }

            holder.myTotalTV.setOnClickListener(listener);
            holder.childLedgerNameTV.setOnClickListener(listener);
            holder.symbolTV.setOnClickListener(listener);
            if(selectedItemID==mDataset.get(position).getMledgerid()){
                //Highlight cell and invert color for text
                holder.cell.setBackgroundColor(mContext.getColor(R.color.colorAccent));
                holder.childLedgerNameTV.setTextColor(mContext.getResources().getColor(R.color.whiteColor,mContext.getTheme()));
                holder.myTotalTV.setTextColor(mContext.getResources().getColor(R.color.whiteColor,mContext.getTheme()));
            }
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
                    sendLongToast(mContext,"Received broadcast from Service + "+intent.getStringExtra(Constants.mMESSAGE));
                    Log.e(TAG,"Received broadcast from Service");
                    myTestTv.setText(intent.getStringExtra(Constants.RESULT_OF_SERVICE));
                }

    }


    private void startService(DataSnapshot dataSnapshot)
    {
        //android.os.Debug.waitForDebugger();
        Intent serviceIntent = new Intent();
        //Start service to download the data for each ledger
        //Pass the LedgerID you want to download
        String mMessage=dataSnapshot.getValue().toString();
        List<String> map= Arrays.asList(mMessage.split("="));
        serviceIntent.setAction(Constants.mAction_GET_LEDGER_ACTION);
        serviceIntent.putExtra(Constants.mAction_GET_LEDGER_DATA,mMessage);
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

    public static void sendLongToast(Context context, String message)
    {
        new SendALongToast(context,message).show();
    }

    private String getLedgerName(String childLedgerID){
        final  String myChildrenLedgerItemRefString = getResources()
                .getString(R.string.firebase_ref_mchild_ledger_get_name, childLedgerID);
        final DatabaseReference LEDGERS_REF =
                FirebaseDatabase.getInstance().getReference(myChildrenLedgerItemRefString);
        FirebaseLiveDataLedgersLedgerItemAdd childLedgersData=new FirebaseLiveDataLedgersLedgerItemAdd(LEDGERS_REF);



        LEDGERS_REF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String name = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return "";
    }

    private String getLedgerType(String childLedgerID){
        final  String myChildrenLedgerItemRefString = getResources()
                .getString(R.string.firebase_ref_mchild_ledger_get_unit, childLedgerID);
        final DatabaseReference LEDGERS_REF =
                FirebaseDatabase.getInstance().getReference(myChildrenLedgerItemRefString);
        FirebaseLiveDataLedgersLedgerItemAdd childLedgersData=new FirebaseLiveDataLedgersLedgerItemAdd(LEDGERS_REF);
       // return childLedgersData.getValue().getKey().toString();
        return "";
    }


}
