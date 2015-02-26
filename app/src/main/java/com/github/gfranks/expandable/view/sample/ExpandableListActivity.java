package com.github.gfranks.expandable.view.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.github.gfranks.expandable.view.ExpandableListView;
import com.github.gfranks.expandable.view.ExpandableView;

public class ExpandableListActivity extends ActionBarActivity {

    boolean mUseCustomAdapter = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view);
        String[] array = {"This", "Is", "My", "Expandable", "ListView"};
        ArrayAdapter<String> arrayAdapter;
        if (mUseCustomAdapter) {
            arrayAdapter = new ExpandableAdapter<String>(this, R.layout.layout_expandable_list_view_item, R.id.expandable_view_header, array);
        } else {
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.layout_expandable_list_view_item, R.id.expandable_view_header, array);
        }
        expandableListView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class ExpandableAdapter<String> extends ArrayAdapter<String> {

        public ExpandableAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            ExpandableView expandableView;
            if (view instanceof ExpandableView) {
                expandableView = (ExpandableView) view;
            } else {
                expandableView = (ExpandableView) view.findViewWithTag(ExpandableView.class.getName());
            }

            if (expandableView != null) {
                expandableView.getHeaderView().setBackgroundColor(getColorForPosition(position));
            }


            return view;
        }

        public int getColorForPosition(int position) {
            switch (position) {
                default:
                case 0:
                    return getContext().getResources().getColor(R.color.theme_red);
                case 1:
                    return getContext().getResources().getColor(R.color.theme_blue);
                case 2:
                    return getContext().getResources().getColor(R.color.theme_green);
                case 3:
                    return getContext().getResources().getColor(R.color.theme_yellow);
                case 4:
                    return getContext().getResources().getColor(R.color.theme_orange);
            }
        }
    }
}
