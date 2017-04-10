package com.example.alex.currencyconverter;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alex.currencyconverter.databinding.CurrenciesDialogBinding;
import com.example.alex.currencyconverter.databinding.CurrenciesDialogItemBinding;
import com.example.alex.currencyconverter.model.app.Currency;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 4/10/2017.
 */

public class CurrencyDialogFragment extends DialogFragment {

    /**
     * Listener interface, implemented by Activity.
     */
    public interface CurrencySelectionListener {
        void onCurrencySelected(String currencyId, String currencyType);
    }

    private static final String KEY_CURRENCY_IDS = "currency_ids";
    private static final String KEY_CURRENCY_NAMES = "currency_names";
    private static final String KEY_CURRENCY_TYPE = "currency_type";

    public static CurrencyDialogFragment newInstance(List<Currency> currencies, String currencyType){
        CurrencyDialogFragment dialogFragment = new CurrencyDialogFragment();
        Bundle args = new Bundle();
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        for (Currency c : currencies){
            ids.add(c.getCurrencyId());
            names.add(c.getName());
        }
        args.putStringArrayList(KEY_CURRENCY_IDS, ids);
        args.putStringArrayList(KEY_CURRENCY_NAMES, names);
        args.putString(KEY_CURRENCY_TYPE, currencyType);
        dialogFragment.setArguments(args);
        return dialogFragment;

    }

    private CurrenciesDialogBinding binding;
    private String currencyType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.currencies_dialog, container, false);
        binding = DataBindingUtil.bind(root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        List<String> ids = args.getStringArrayList(KEY_CURRENCY_IDS);
        List<String> names = args.getStringArrayList(KEY_CURRENCY_NAMES);
        currencyType = args.getString(KEY_CURRENCY_TYPE);
        //  init 'cancel' button
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        // setup list of currencies
        RecyclerView list = binding.currenciesList;
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(new CurrencyListAdapter(getActivity(), ids, names));
    }

    /**
     * List adapter is made an inner class for convenience
     */
    private class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.ViewHolder>{
        private List<String> ids;
        private List<String> currencyNames;
        private LayoutInflater inflater;

        public CurrencyListAdapter(Context context, List<String> ids, List<String> currencyNames){
            this.ids = ids;
            this.currencyNames = currencyNames;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = inflater.inflate(R.layout.currencies_dialog_item, parent, false);
            ViewHolder vh = new ViewHolder(root);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            // setup view content
            String name = currencyNames.get(position);
            int itemBackground = position % 2 == 0 ? R.color.green_100 : R.color.green_200;
            holder.binding.currencyName.setText(name);
            holder.binding.dialogItemContainer.setBackgroundResource(itemBackground);
            // setup view click listener
            holder.binding.dialogItemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String currencyId = ids.get(position);
                    CurrencySelectionListener listener = (CurrencySelectionListener) getActivity();
                    listener.onCurrencySelected(currencyId, currencyType);
                    dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return currencyNames.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CurrenciesDialogItemBinding binding;
            public ViewHolder(View view){
                super(view);
                binding = DataBindingUtil.bind(view);
            }
        }
    }
}
