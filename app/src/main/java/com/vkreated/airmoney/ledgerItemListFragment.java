package com.vkreated.airmoney;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.data.childledger;
import com.data.ledgeritem;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viewmodels.ledgersViewModelFull;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ledgerItemListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ledgerItemListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ledgerItemListFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_LEDGER_ID = "ledger_id";

    // TODO: Rename and change types of parameters
    private String mItemId;
    private ledgersViewModelFull mViewModel;
    private static String TAG="ledgerItemListFragment";
    private OnFragmentInteractionListener mListener;

    public ledgerItemListFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ledgerItemListFragment newInstance(String itemId) {
        ledgerItemListFragment fragment = new ledgerItemListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           mItemId = getArguments().getString(ARG_LEDGER_ID);
        }

        //Subscribed to model
        mViewModel = ViewModelProviders.of(this).get(ledgersViewModelFull.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewModel.getLedgers(getContext(),mItemId).observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                childledger mChildLedger;
                mChildLedger=convertDataSnapShotToChildLedger(dataSnapshot);

                if(mChildLedger.getMchilLedgerItems()==null){
                    sendLongToast("Add Items to this ledger to see them here, Use the plus for that");
                }else if(!mChildLedger.getMchilLedgerItems().isEmpty()){
                    //TODO: Display Items

                }

            }
        });
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ledger_item_list, container, false);

    }

    private childledger convertDataSnapShotToChildLedger(@Nullable DataSnapshot dataSnapshot) {
        HashMap<String, JSONObject> dataSnapshotValue = (HashMap<String, JSONObject>) dataSnapshot.getValue();
        String jsonString = new Gson().toJson(dataSnapshotValue);
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.create();
        childledger parsedChildLedger = gson.fromJson(jsonString, childledger.class);
        return parsedChildLedger ;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static class ChildrenItemLedgerAdapter extends RecyclerView.Adapter<ChildrenItemLedgerAdapter.MyViewHolder> {
        List<ledgeritem> mDataset;
        Context mContext;

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
        public ChildrenItemLedgerAdapter(List<ledgeritem> myDataset, Context context) {
            mDataset = myDataset;
            mContext= context;

        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                                int viewType) {
            // create a new view
            View v =  LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.child_ledger_for_list, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ChildrenItemLedgerAdapter.MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final int positionFinal=position;
            holder.textView.setText("set");

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
    private void sendLongToast(String message) {
        Toast toast=Toast.makeText(getContext(),message,Toast.LENGTH_LONG);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if( v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }
}
