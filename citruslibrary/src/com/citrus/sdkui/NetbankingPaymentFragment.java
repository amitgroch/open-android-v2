package com.citrus.sdkui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.citrus.sdkui.classes.NetbankingOption;
import com.citruspay.citruslibrary.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPaymentOptionSelectedListener} interface
 * to handle interaction events.
 * Use the {@link NetbankingPaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public final class NetbankingPaymentFragment extends Fragment {

    private OnPaymentOptionSelectedListener mListener;

    private ArrayList<NetbankingOption> mNetbankingOptionsList;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NetbankingPaymetFragment.
     */
    public static NetbankingPaymentFragment newInstance(ArrayList<NetbankingOption> netbankingOptionsList) {
        NetbankingPaymentFragment fragment = new NetbankingPaymentFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.INTENT_EXTRA_NETBANKING_PARAMS, netbankingOptionsList);
        fragment.setArguments(args);
        return fragment;
    }

    public NetbankingPaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNetbankingOptionsList = getArguments().getParcelableArrayList(Constants.INTENT_EXTRA_NETBANKING_PARAMS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_netbanking_payment, container, false);

        NetbankingAdapter netbankingAdapter = new NetbankingAdapter(mNetbankingOptionsList);

        RecyclerView recylerViewNetbanking = (RecyclerView) view.findViewById(R.id.recycler_view_netbanking);
        recylerViewNetbanking.setAdapter(netbankingAdapter);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recylerViewNetbanking.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recylerViewNetbanking.setLayoutManager(mLayoutManager);

        recylerViewNetbanking.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new OnItemClickListener()));
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPaymentOptionSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPaymentOptionSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private NetbankingOption getItem(int position) {
        NetbankingOption netbankingOption = null;

        if (mNetbankingOptionsList != null && mNetbankingOptionsList.size() > position && position >= -1) {
            netbankingOption = mNetbankingOptionsList.get(position);
        }

        return netbankingOption;
    }

    private class OnItemClickListener extends RecyclerItemClickListener.SimpleOnItemClickListener {

        @Override
        public void onItemClick(View childView, int position) {
            NetbankingOption netbankingOption = getItem(position);
            mListener.onOptionSelected(netbankingOption);
        }
    }
}
