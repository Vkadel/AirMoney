package com.vkreated.airmoney;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.data.childledger;
import com.data.ledgeritem;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.utils.ConvertMinsToStringSec;
import com.utils.ResizeTextSizeBaseOnNumberSize;
import com.viewmodels.ledgersViewModelAllLedgers;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;


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
    final static public String LEDGER_TYPE_ARG = "ledger_type";
    // TODO: Rename and change types of parameters

    private String mItemId;
    private ledgersViewModelAllLedgers mViewModel;
    private static String TAG = "ledgerItemListFragment";
    private OnFragmentInteractionListener mListener;
    private ChildrenItemLedgerAdapter adapter;
    private RecyclerView recyclerView;
    private String mLedgertype;

    public ledgerItemListFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ledgerItemListFragment newInstance(String itemId, int ledgerType) {
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
            mLedgertype = getArguments().getString(LEDGER_TYPE_ARG);
        }

        //Subscribed to model
        mViewModel = ViewModelProviders.of(this).get(ledgersViewModelAllLedgers.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myRootLayout = inflater.inflate(R.layout.fragment_ledger_item_list, container, false);

        // Inflate the layout for this fragment
        return myRootLayout;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.ledgeritem_list_recycler);

        mViewModel.getLedgers(getContext(), mItemId).observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                childledger mChildLedger;
                final List<ledgeritem> myListofItems = new ArrayList<>();
                //TODO: may want to send this work to another thread
                dataSnapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
                    @Override
                    public void accept(DataSnapshot dataSnapshot) {
                        ledgeritem mledgeritem = dataSnapshot.getValue(ledgeritem.class);
                        if (!myListofItems.contains(mledgeritem)) {
                            myListofItems.add(mledgeritem);
                        }

                    }
                });
                adapter = new ChildrenItemLedgerAdapter(myListofItems, getContext(), mLedgertype);
                recyclerView.setAdapter(adapter);
            }

        });
        super.onViewCreated(view, savedInstanceState);
    }

    private childledger convertDataSnapShotToChildLedger(@Nullable DataSnapshot dataSnapshot) {
        HashMap<String, JSONObject> dataSnapshotValue = (HashMap<String, JSONObject>) dataSnapshot.getValue();
        String jsonString = new Gson().toJson(dataSnapshotValue);
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.create();
        childledger parsedChildLedger = gson.fromJson(jsonString, childledger.class);
        return parsedChildLedger;
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
        String mLedgertype;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView description_tv;
            public TextView my_item_value_tv;
            public TextView symbol_tv;
            public TextView direction_tv;


            public MyViewHolder(View v) {
                super(v);
                description_tv = v.findViewById(R.id.short_ledger_item_description_label_tv);
                my_item_value_tv = v.findViewById(R.id.ledger_item_value);
                symbol_tv = v.findViewById(R.id.ledger_item_currency);
                direction_tv = v.findViewById(R.id.ledger_item_direction_tv);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public ChildrenItemLedgerAdapter(List<ledgeritem> myDataset, Context context, String ledgertype) {
            mDataset = myDataset;
            mContext = context;
            mLedgertype = ledgertype;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ledgeritem_list_item, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ChildrenItemLedgerAdapter.MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final int positionFinal = position;
            String description = mDataset.get(position).getMdescription();


            //Resize the description_tv
            DisplayMetrics displayMetrics = new DisplayMetrics();
            Activity myActivity = (Activity) mContext;
            myActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            int width = displayMetrics.widthPixels;
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) holder.description_tv.getLayoutParams();

            double widthtrans = 0;

            switch (Integer.parseInt(mLedgertype)) {
                case 1:
                    //money Ledger Item
                    new ResizeTextSizeBaseOnNumberSize(holder.my_item_value_tv,mDataset.get(position).getMvalue());
                    holder.my_item_value_tv.setText(String.valueOf(mDataset.get(position).getMvalue()));
                    widthtrans = width / 2.45;
                    break;

                case 2:
                    //Time Ledger
                    holder.my_item_value_tv.setText(new ConvertMinsToStringSec(mDataset.get(position)
                            .getMvalue(), mContext).getTheTime().toString());
                    holder.my_item_value_tv.setTextSize(mContext.getResources().getDimension(R.dimen.smaller_item_text_size));
                    holder.symbol_tv.setText("");
                    widthtrans = width / 2.7;

                    break;
                case 3:
                    holder.my_item_value_tv.setText(String.valueOf(mDataset.get(position).getMvalue()));
                    holder.symbol_tv.setText("");
                    break;
                default:
                    holder.my_item_value_tv.setText(String.valueOf(mDataset.get(position).getMvalue()));
                    widthtrans = width / 2.45;
                    break;

            }
            params.width = (int) widthtrans;
            holder.description_tv.setLayoutParams(params);
            //Readjusting what its shown on the unit and value
            holder.description_tv.setText(description);
            setUpdirection(holder, position);
        }

        private void setUpdirection(MyViewHolder holder, int position) {
            int mydirection = mDataset.get(position).getMdirection();
            if (mydirection == 1) {
                holder.direction_tv.setText(mContext.getResources().getString(R.string.direction_plus));
            }
            if (mydirection == 0) {
                holder.direction_tv.setText(mContext.getResources().getString(R.string.direction_minus));
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

}
