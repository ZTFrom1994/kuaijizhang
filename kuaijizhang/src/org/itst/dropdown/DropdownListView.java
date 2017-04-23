package org.itst.dropdown;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.LinkedList;
import java.util.List;

import com.example.kuaijizhang.R;

/**
 * Created by Administrator on 2015/5/28.
 */
public class DropdownListView extends ScrollView {
    public LinearLayout linearLayout;
    public String text ;
    public DropdownItemObject currentItemObject;

    List<? extends DropdownItemObject> list;

    public DropdownButton button;

    public DropdownListView(Context context) {
        this(context, null);
    }

    public DropdownListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropdownListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view =  LayoutInflater.from(getContext()).inflate(R.layout.dropdown_tab_list, this,true);
        linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
    }


    public void flush() {
        for (int i = 0, n = linearLayout.getChildCount(); i < n; i++) {
            View view = linearLayout.getChildAt(i);
            if (view instanceof DropdownListItemView) {
                DropdownListItemView itemView = (DropdownListItemView) view;
                DropdownItemObject data = (DropdownItemObject) itemView.getTag();
                if (data == null) {
                    return;
                }
                boolean checked = data == currentItemObject;
                String suffix = data.getSuffix();
                itemView.bind(TextUtils.isEmpty(suffix) ? data.text : data.text + suffix, checked);
                if (checked) button.setText(data.text);
                this.text=data.text;
            }

        }
    }

    public void bind(List<? extends DropdownItemObject> list,
                     DropdownButton button,
                     final Container container,
                     int selectedId
    ) {
        currentItemObject = null;
        this.list = list;
        this.button = button;
        LinkedList<View> cachedDividers = new LinkedList<View>();
        LinkedList<DropdownListItemView> cachedViews = new LinkedList<DropdownListItemView>();
        for (int i = 0, n = linearLayout.getChildCount(); i < n; i++) {
            View view = linearLayout.getChildAt(i);
            if (view instanceof DropdownListItemView) {
                cachedViews.add((DropdownListItemView) view);
            } else {
                cachedDividers.add(view);
            }
        }
        linearLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        boolean isFirst = true;
        for (DropdownItemObject item : list) {
            if (isFirst) {
                isFirst = false;
            } else {
                View divider = cachedDividers.poll();
                if (divider == null) {
                    divider = inflater.inflate(R.layout.dropdown_tab_list_divider, linearLayout, false);
                }
                linearLayout.addView(divider);
            }
            DropdownListItemView view = cachedViews.poll();
            if (view == null) {
                view = (DropdownListItemView) inflater.inflate(R.layout.dropdown_tab_list_item, linearLayout, false);
            }
            view.setTag(item);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DropdownItemObject data = (DropdownItemObject) v.getTag();
                    if (data == null) return;
                    DropdownItemObject oldOne = currentItemObject;
                    currentItemObject = data;
                    flush();
                    container.hide();
                    if (oldOne != currentItemObject) {
                        container.onSelectionChanged(DropdownListView.this);
                    }
                }
            });
            linearLayout.addView(view);
            if (item.id == selectedId && currentItemObject == null) {
                currentItemObject = item;
            }
        }

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getVisibility() == VISIBLE) {
                    container.hide();
                } else {
                    container.show(DropdownListView.this);
                }
            }
        });

        if (currentItemObject == null && list.size() > 0) {
            currentItemObject = list.get(0);
        }
        flush();
    }


    public static interface Container {
        void show(DropdownListView listView);
        void hide();
        void onSelectionChanged(DropdownListView view);
    }


}
