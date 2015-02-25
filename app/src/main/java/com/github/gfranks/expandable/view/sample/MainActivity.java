package com.github.gfranks.expandable.view.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.github.gfranks.expandable.view.ExpandableView;


public class MainActivity extends ActionBarActivity implements ExpandableView.ExpandableViewListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);

        ExpandableView expandableView = (ExpandableView) findViewById(R.id.expandable_view);
        ExpandableView expandableView2 = (ExpandableView) findViewById(R.id.expandable_view_2);
        ExpandableView expandableView3 = (ExpandableView) findViewById(R.id.expandable_view_3);
        ExpandableView expandableView4 = (ExpandableView) findViewById(R.id.expandable_view_4);
        expandableView.setExpandableViewListener(this);
        expandableView2.setExpandableViewListener(this);
        expandableView3.setExpandableViewListener(this);
        expandableView4.setExpandableViewListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ContentFragment fragment = (ContentFragment) getSupportFragmentManager().findFragmentByTag(ContentFragment.TAG);
        if (fragment == null) {
            fragment = new ContentFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.expandable_view_4_content, fragment, ContentFragment.TAG)
                    .commit();
        } else if (fragment.isDetached()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .attach(fragment)
                    .commit();
        }

        ExpandableView expandableView = (ExpandableView) findViewById(R.id.expandable_view);
        if (expandableView != null && !expandableView.isExpanded()) {
            ImageView indicator = (ImageView) expandableView.findViewById(R.id.expandable_footer_indicator);
            indicator.setImageResource(R.drawable.ic_arrow_expand);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * ***********************************************
     * ExpandableView.ExpandableViewListener callbacks
     * ***********************************************
     */
    @Override
    public boolean canExpand(ExpandableView expandableView) {
        return true;
    }

    @Override
    public boolean canCollapse(ExpandableView expandableView) {
        return true;
    }

    @Override
    public void willExpand(ExpandableView expandableView) {
    }

    @Override
    public void willCollapse(ExpandableView expandableView) {
    }

    @Override
    public void didExpand(ExpandableView expandableView) {
        if (expandableView.getId() == R.id.expandable_view) {
            ImageView indicator = (ImageView) expandableView.findViewById(R.id.expandable_footer_indicator);
            indicator.setImageResource(R.drawable.ic_arrow_collapse);
        }
    }

    @Override
    public void didCollapse(ExpandableView expandableView) {
        if (expandableView.getId() == R.id.expandable_view) {
            ImageView indicator = (ImageView) expandableView.findViewById(R.id.expandable_footer_indicator);
            indicator.setImageResource(R.drawable.ic_arrow_expand);
        }
    }

    @Override
    public void onHeightOffsetChanged(ExpandableView expandableView, float offset) {
        // here you may apply an offset to a custom drawable (maybe a flip indicator)
    }
}
