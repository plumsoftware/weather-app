package ru.plumsoftware.weatherapp.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.plumsoftware.weatherapp.R;
import ru.plumsoftware.weatherapp.adapters.ThemeAdapter;
import ru.plumsoftware.weatherapp.data.Settings;

public class MyBottomSheetDialog extends BottomSheetDialogFragment {

    private List<Pair<String, Integer>> list;

    public void setData(List<Pair<String, Integer>> list) {
        this.list = list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewThemes);

        Settings settings = Settings.getUserSettings(requireContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ThemeAdapter(getContext(), getActivity(), list, settings));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow()
                    .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow()
                    .getAttributes().windowAnimations = R.style.AnimationDialog;
        }
    }
}
