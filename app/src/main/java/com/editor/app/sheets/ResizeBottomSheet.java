package com.editor.app.sheets;

import android.view.LayoutInflater;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

import com.editor.app.R;
import com.editor.app.adapters.ResizeAdapter;
import com.editor.app.models.CanvasSize;

import java.util.ArrayList;
import java.util.List;

public class ResizeBottomSheet extends BaseBottomSheet {

    private OnResizeListener listener;

    public interface OnResizeListener {
        void onSizeSelected(CanvasSize size);
    }

    public void setOnResizeListener(OnResizeListener listener) {
        this.listener = listener;
    }

    @Override
    protected void setupChips() {
        addChip("Standard", v -> showStandardSizes());
        addChip("Basic", v -> showBasicSizes());
        addChip("Social Media", v -> showSocialMediaSizes());

        selectFirstChip();
    }

    private void showStandardSizes() {
        List<CanvasSize> sizes = new ArrayList<>();
        sizes.add(new CanvasSize("A4", "2480 x 3508 px", 2480, 3508, "standard"));
        sizes.add(new CanvasSize("A3", "3508 x 4961 px", 3508, 4961, "standard"));
        sizes.add(new CanvasSize("Letter", "2550 x 3300 px", 2550, 3300, "standard"));
        sizes.add(new CanvasSize("Legal", "2550 x 4200 px", 2550, 4200, "standard"));

        showSizeList(sizes);
    }

    private void showBasicSizes() {
        List<CanvasSize> sizes = new ArrayList<>();
        sizes.add(new CanvasSize("Square", "1080 x 1080 px", 1080, 1080, "basic"));
        sizes.add(new CanvasSize("Portrait", "1080 x 1920 px", 1080, 1920, "basic"));
        sizes.add(new CanvasSize("Landscape", "1920 x 1080 px", 1920, 1080, "basic"));
        sizes.add(new CanvasSize("4:3", "1600 x 1200 px", 1600, 1200, "basic"));
        sizes.add(new CanvasSize("16:9", "1920 x 1080 px", 1920, 1080, "basic"));

        showSizeList(sizes);
    }

    private void showSocialMediaSizes() {
        List<CanvasSize> sizes = new ArrayList<>();
        sizes.add(new CanvasSize("Instagram Post", "1080 x 1080 px", 1080, 1080, "social_media", android.R.drawable.ic_menu_gallery));
        sizes.add(new CanvasSize("Instagram Story", "1080 x 1920 px", 1080, 1920, "social_media", android.R.drawable.ic_menu_gallery));
        sizes.add(new CanvasSize("Facebook Post", "1200 x 630 px", 1200, 630, "social_media", android.R.drawable.ic_menu_gallery));
        sizes.add(new CanvasSize("Facebook Cover", "820 x 312 px", 820, 312, "social_media", android.R.drawable.ic_menu_gallery));
        sizes.add(new CanvasSize("Twitter Post", "1200 x 675 px", 1200, 675, "social_media", android.R.drawable.ic_menu_gallery));
        sizes.add(new CanvasSize("Twitter Header", "1500 x 500 px", 1500, 500, "social_media", android.R.drawable.ic_menu_gallery));
        sizes.add(new CanvasSize("LinkedIn Post", "1200 x 627 px", 1200, 627, "social_media", android.R.drawable.ic_menu_gallery));
        sizes.add(new CanvasSize("YouTube Thumbnail", "1280 x 720 px", 1280, 720, "social_media", android.R.drawable.ic_menu_gallery));
        sizes.add(new CanvasSize("Pinterest Pin", "1000 x 1500 px", 1000, 1500, "social_media", android.R.drawable.ic_menu_gallery));

        showSizeList(sizes);
    }

    private void showSizeList(List<CanvasSize> sizes) {
        View resizeView = LayoutInflater.from(requireContext())
                .inflate(R.layout.layout_resize_options, null);

        RecyclerView recyclerView = resizeView.findViewById(R.id.resizeRecyclerView);

        ResizeAdapter adapter = new ResizeAdapter(sizes, size -> {
            if (listener != null) {
                listener.onSizeSelected(size);
            }
        });

        recyclerView.setAdapter(adapter);

        setContent(resizeView);
    }
}