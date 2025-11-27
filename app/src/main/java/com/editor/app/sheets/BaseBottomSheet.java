package com.editor.app.sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.editor.app.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public abstract class BaseBottomSheet extends BottomSheetDialogFragment {

    protected ChipGroup chipGroup;
    protected FrameLayout contentContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_base, container, false);

        chipGroup = view.findViewById(R.id.chipGroup);
        contentContainer = view.findViewById(R.id.contentContainer);

        setupChips();

        return view;
    }

    protected abstract void setupChips();

    protected void addChip(String label, View.OnClickListener listener) {
        Chip chip = new Chip(requireContext());
        chip.setText(label);
        chip.setCheckable(true);
        chip.setChipBackgroundColorResource(R.color.chip_unselected);
        chip.setTextColor(getResources().getColor(R.color.md_theme_onSurface, null));

        chip.setOnClickListener(v -> {
            // Update chip selection state
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip c = (Chip) chipGroup.getChildAt(i);
                if (c == chip) {
                    c.setChipBackgroundColorResource(R.color.chip_selected);
                    c.setTextColor(getResources().getColor(R.color.white, null));
                } else {
                    c.setChipBackgroundColorResource(R.color.chip_unselected);
                    c.setTextColor(getResources().getColor(R.color.md_theme_onSurface, null));
                }
            }

            if (listener != null) {
                listener.onClick(v);
            }
        });

        chipGroup.addView(chip);
    }

    protected void setContent(View content) {
        contentContainer.removeAllViews();
        contentContainer.addView(content);
    }

    protected void selectFirstChip() {
        if (chipGroup.getChildCount() > 0) {
            Chip firstChip = (Chip) chipGroup.getChildAt(0);
            firstChip.performClick();
        }
    }
}
