package com.fsmsh.checkpad.ui.tags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fsmsh.checkpad.R;
import com.fsmsh.checkpad.databinding.ActivityEditBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class GeralFilterBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "ModalBottomSheet";

    Button button;
    View view;
    public TagsFragment parent;

    public GeralFilterBottomSheet(TagsFragment parent) {
        this.parent = parent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_geral_filter, container, false);

        button = view.findViewById(R.id.geral_filter_confirm_buttom);
        button.setOnClickListener(v -> confirmFilter());

        return view;
    }

    public void confirmFilter() {


        this.dismiss();
    }

}
