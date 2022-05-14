package com.memoryleak.pocketcopro.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.memoryleak.pocketcopro.util.Constant;

public class DividerDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;
    private int orientation;
    private Drawable divider;
    private final Rect bounds = new Rect();

    public DividerDecoration(Context context, int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL)
            throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        this.orientation = orientation;
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        divider = a.getDrawable(0);
        if (divider == null)
            Log.w(Constant.NAME, "@android:attr/listDivider was not set in the theme used for this " + "DividerDecoration. Please set that attribute all call setDrawable()");
        a.recycle();
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL)
            throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        this.orientation = orientation;
    }

    public void setDrawable(@NonNull Drawable drawable) {
        if (drawable == null) throw new IllegalArgumentException("Drawable cannot be null.");
        divider = drawable;
    }

    @Nullable
    public Drawable getDrawable() {
        return divider;
    }

    @Override
    public void onDraw(@NonNull Canvas c, RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getLayoutManager() == null || divider == null) return;
        if (orientation == VERTICAL) drawVertical(c, parent);
        else drawHorizontal(c, parent);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (divider == null || parent.getChildAdapterPosition(view) == state.getItemCount() - 1)
            outRect.setEmpty();
        else {
            if (orientation == VERTICAL) outRect.set(0, 0, 0, divider.getIntrinsicHeight());
            else outRect.set(0, 0, divider.getIntrinsicWidth(), 0);
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int left;
        final int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right, parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, bounds);
            final int bottom = bounds.bottom + Math.round(child.getTranslationY());
            final int top = bottom - divider.getIntrinsicHeight();
            divider.setBounds(left, top, right, bottom);
            divider.draw(canvas);
        }
        canvas.restore();
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int top;
        final int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top,
                    parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            parent.getLayoutManager().getDecoratedBoundsWithMargins(child, bounds);
            final int right = bounds.right + Math.round(child.getTranslationX());
            final int left = right - divider.getIntrinsicWidth();
            divider.setBounds(left, top, right, bottom);
            divider.draw(canvas);
        }
        canvas.restore();
    }
}
