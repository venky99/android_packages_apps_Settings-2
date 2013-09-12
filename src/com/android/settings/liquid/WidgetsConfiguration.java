/*
 * Copyright (C) 2013 Slimroms
 * Copyright (C) 2011 AOKP
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.liquid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.settings.R;

import java.util.ArrayList;

public class WidgetsConfiguration extends DialogFragment {

    private static final String TAG = "Widget";
    public static final String ACTION_ALLOCATE_ID = "com.android.systemui.ACTION_ALLOCATE_ID";
    public static final String ACTION_DEALLOCATE_ID = "com.android.systemui.ACTION_DEALLOCATE_ID";
    public static final String ACTION_SEND_ID = "com.android.systemui.ACTION_SEND_ID";
    public static final String ACTION_DELETE_WIDGETS = "com.android.systemui.ACTION_DELETE_WIDGETS";

    private static final int MENU_SHIFT_LEFT = Menu.FIRST;
    private static final int MENU_DELETE = MENU_SHIFT_LEFT + 1;
    private static final int MENU_RESET = MENU_DELETE + 1;
    private static final int MENU_SHIFT_RIGHT = MENU_RESET + 1;

    private ViewPager mViewPager;
    WidgetPagerAdapter mAdapter;
    Context mContext;
    TextView mTitle;
    TextView mSummary;
    AppWidgetManager mAppWidgetManager;
    View mPrefView;
    ArrayList<NavBarWidget> mWidgets = new ArrayList<NavBarWidget>();
    protected int mCurrentPage;

    BroadcastReceiver mWidgetIdReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                AppWidgetManager.INVALID_APPWIDGET_ID);
            if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                return;
            }
            // Need to De-Allocate the ID that this was replacing.
            if (mWidgets.get(mCurrentPage).getWidgetId() != -1) {
                Intent delete = new Intent();
                int dealloc = mWidgets.get(mCurrentPage).getWidgetId();
                delete.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, dealloc);
                delete.setAction(ACTION_DEALLOCATE_ID);
                mContext.sendBroadcast(delete);
                mWidgets.remove(mCurrentPage);
            }
            mWidgets.add(mCurrentPage, new NavBarWidget(widgetId));
            saveWidgets();
            refreshParams();
            mViewPager.setCurrentItem(mCurrentPage);
        };
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPrefView = inflater.inflate(R.layout.navbar_widgets, container, false);
        mViewPager = (ViewPager) mPrefView.findViewById(R.id.pager);
        mCurrentPage = 0;
        mTitle = (TextView) mPrefView.findViewById(R.id.title);
        mSummary = (TextView) mPrefView.findViewById(R.id.summary);

        setHasOptionsMenu(true);

        return mPrefView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_SHIFT_LEFT, 0, R.string.widgets_shift_left)
                .setIcon(R.drawable.widget_left)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, MENU_DELETE, 0, R.string.widgets_delete)
                .setIcon(R.drawable.widget_delete)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, MENU_RESET, 0, R.string.widgets_reset)
                .setIcon(R.drawable.ic_settings_backup)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, MENU_SHIFT_RIGHT, 0, R.string.widgets_shift_right)
                .setIcon(R.drawable.widget_right)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SHIFT_LEFT:
                shiftleftWidget(mCurrentPage);
                return true;
            case MENU_DELETE:
                removeWidget();
                return true;
            case MENU_RESET:
                resetToDefault();
                return true;
            case MENU_SHIFT_RIGHT:
                shiftrightWidget(mCurrentPage);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    private void resetToDefault() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.widgets_reset);
        alertDialog.setMessage(R.string.widgets_reset_message);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                resetNavBarWidgets();
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, null);
        alertDialog.create().show();
    }

    private void removeWidget() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.widgets_delete);
        alertDialog.setMessage(R.string.widgets_delete_message);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeWidget(mCurrentPage);
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, null);
        alertDialog.create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshParams();
        mViewPager.setCurrentItem(mCurrentPage);
    }

    @Override
    public void onPause() {
        super.onPause();
        refreshParams();
        mViewPager.setCurrentItem(mCurrentPage);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity.getApplicationContext();
        IntentFilter filter = new IntentFilter(ACTION_SEND_ID);
        mContext.registerReceiver(mWidgetIdReceiver, filter);
        mAppWidgetManager = AppWidgetManager.getInstance(mContext);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext.unregisterReceiver(mWidgetIdReceiver);
    }

    private void shiftleftWidget(int page) {
        if (page > 0 && mWidgets.size() > 1 && (page < mWidgets.size()-1)) {
            NavBarWidget moveme = mWidgets.remove(page);
            mWidgets.add(page -1, moveme);
            saveWidgets();
            mCurrentPage = page - 1;
            refreshParams();
            mViewPager.setCurrentItem(mCurrentPage);
            showToastMessage(getResources().getString(R.string.widgets_shifted_left));
        } else {
            showToastMessage(getResources().getString(R.string.widgets_shifted_end));
        }
    }

    private void shiftrightWidget(int page) {
        if (page < (mWidgets.size()-2) && mWidgets.size() > 1) {
            NavBarWidget moveme = mWidgets.remove(page);
            mWidgets.add(page +1, moveme);
            saveWidgets();
            mCurrentPage = page + 1;
            refreshParams();
            mViewPager.setCurrentItem(mCurrentPage);
            showToastMessage(getResources().getString(R.string.widgets_shifted_right));
        } else {
            showToastMessage(getResources().getString(R.string.widgets_shifted_end));
        }
    }

    private void removeWidget(int page) {
        if (page < (mWidgets.size() -1)) {
            NavBarWidget removedWidget =  mWidgets.remove(page);
            saveWidgets();
            refreshParams();
            // remove in system ui
            Intent delete = new Intent();
            delete.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                removedWidget.getWidgetId());
            delete.setAction(ACTION_DEALLOCATE_ID);
            mContext.sendBroadcast(delete);
            mViewPager.setCurrentItem(page);
        }
    }

    private void showToastMessage(String message) {
         Toast.makeText(mContext, message,
                Toast.LENGTH_LONG).show();
    }

    private ArrayList<NavBarWidget> inflateWidgets() {
        ArrayList<NavBarWidget> widgets = new ArrayList<NavBarWidget>();
        String settingWidgets = Settings.System.getString(mContext.getContentResolver(),
                Settings.System.NAVIGATION_BAR_WIDGETS);
        if (settingWidgets != null && settingWidgets.length() > 0) {
            String[] split = settingWidgets.split("\\|");
            widgets.ensureCapacity(split.length + 1);
            for (int i = 0; i < split.length; i++) {
                int appWidgetId = Integer.parseInt(split[i]);
                widgets.add(new NavBarWidget(appWidgetId));
            }
        }
        widgets.add(new NavBarWidget(-1)); // add +1 button!
        return widgets;
    }

    private void refreshParams() {
        if (mViewPager != null) {
            if (mAdapter == null) {
                mWidgets = inflateWidgets();
                mViewPager.setAdapter(mAdapter = new WidgetPagerAdapter());
                mViewPager.setOnPageChangeListener(mNewPageListener);
            } else {
                mViewPager.setAdapter(mAdapter);
            }
            int dp = mAdapter.getHeight(mViewPager.getCurrentItem());
            float px = dp * mContext.getResources().getDisplayMetrics().density;
            mViewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) px));
            updateSummary(mCurrentPage);
        }
    }

    View.OnClickListener mDoPrefClick = new View.OnClickListener() {
        public void onClick(View v) {
            requestNewWidget();
        };
    };

    private void requestNewWidget() {
        // send intent to pick a new widget
        Intent send = new Intent();
        send.setAction(ACTION_ALLOCATE_ID);
        mContext.sendBroadcast(send);
    }

    private void saveWidgets() {
        StringBuilder widgetString = new StringBuilder();
        int numwidgets = mWidgets.size() - 1; // adjust for add button
        for (int i = 0; i < (numwidgets); i++) {
            widgetString.append(mWidgets.get(i).getWidgetId());
            if (i != (numwidgets - 1)) // zero based adjustment
                widgetString.append("|");
        }
        Settings.System.putString(mContext.getContentResolver(),
                Settings.System.NAVIGATION_BAR_WIDGETS,
                widgetString.toString());
    }

    public void resetNavBarWidgets() {
        Intent delete = new Intent();
        delete.setAction(ACTION_DELETE_WIDGETS);
        mContext.sendBroadcast(delete);
        Settings.System.putString(mContext.getContentResolver(),
                Settings.System.NAVIGATION_BAR_WIDGETS, "");
        mWidgets = inflateWidgets();
        refreshParams();
        mAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(mCurrentPage = 0);
    }

    private void updateSummary(int page) {
        int widgets = mWidgets.size() - 1;
        if (widgets > page) {
            mSummary.setText(String.format(mContext.getResources().getString(R.string.navbar_widget_summary),
                    (page + 1),widgets));
            mTitle.setText(mWidgets.get(page).mTitle);
        } else {
            mSummary.setText(mContext.getResources().getString(R.string.navbar_widget_summary_add));
            mTitle.setText("");
        }
        mPrefView.invalidate(); // force redraw
    }

    public SimpleOnPageChangeListener mNewPageListener = new SimpleOnPageChangeListener() {

        @Override
        public void onPageSelected(int page) {
            int dp = mAdapter.getHeight(page);
            float px = dp * mContext.getResources().getDisplayMetrics().density;
            mCurrentPage = page;
            mViewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) px));
            updateSummary(page);
        }

    };

    public class WidgetPagerAdapter extends PagerAdapter {


        public WidgetPagerAdapter() {
        }

        @Override
        public int getCount() {
            if (mWidgets == null){
                return 0;
            } else {
                return mWidgets.size();
            }
        }

        public int getHeight(int pos) {
            if (mWidgets == null) {
                return -1;
            }
            if (pos < mWidgets.size()) {
                if (mWidgets != null && mWidgets.get(pos) != null) {
                    int validHeight = mWidgets.get(pos).getHeight();
                    return validHeight;
                }
            }
            return -1;
        }

        /**
         * Create the page for the given position. The adapter is responsible for
         * adding the view to the container given here, although it only must ensure
         * this is done by the time it returns from {@link #finishUpdate()}.
         *
         * @param container The containing View in which the page will be shown.
         * @param position The page position to be instantiated.
         * @return Returns an Object representing the new page. This does not need
         *         to be a View, but can be some other container of the page.
         */
        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            if (position > (mWidgets.size() - 1))
                return null;
            View v = mWidgets.get(position).getView();
            if (v.getParent() != null) {
                // there is a case where shifting views could result in a view being re-added yet
                // still having a parent.
                ViewGroup vparent = (ViewGroup) v.getParent();
                vparent.removeView(v);
            }
            v.setOnClickListener(mDoPrefClick);
            collection.addView(v);
            return v;
        }

        /**
         * Remove a page for the given position. The adapter is responsible for
         * removing the view from its container, although it only must ensure this
         * is done by the time it returns from {@link #finishUpdate()}.
         *
         * @param container The containing View from which the page will be removed.
         * @param position The page position to be removed.
         * @param object The same object that was returned by
         *            {@link #instantiateItem(View, int)}.
         */
        @Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            if (object instanceof NavBarWidget) {
                return ((NavBarWidget) object).getView().equals(view);
            }
            return view == ((View) object);
        }

        /**
         * Called when the a change in the shown pages has been completed. At this
         * point you must ensure that all of the pages have actually been added or
         * removed from the container as appropriate.
         *
         * @param container The containing View which is displaying this adapter's
         *            page views.
         */
        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }

    public class NavBarWidget {
        int mWidgetId;
        int mHeight;
        ImageView mView = null;
        Drawable mPreview = null;
        String mTitle;

        public NavBarWidget(int appWidgetId) {
            mWidgetId = appWidgetId;
            if (appWidgetId == -1) {
                mPreview = mContext.getResources().getDrawable(R.drawable.widget_add);
                mHeight = mPreview.getMinimumHeight();
                mTitle = mContext.getResources().getString(R.string.navbar_widget_summary_add);
            } else {
                AppWidgetProviderInfo info = mAppWidgetManager.getAppWidgetInfo(mWidgetId);
                if (info == null) {
                    mPreview = mContext.getResources().getDrawable(R.drawable.widget_na);
                    mHeight = mPreview.getMinimumHeight();
                } else {
                    mHeight = info.minHeight;
                    mTitle = info.label;
                    mPreview = mContext.getPackageManager().getDrawable(info.provider.getPackageName(),
                            info.previewImage, null);
                    if (mPreview == null) {
                        try {
                            mPreview =  mContext.getPackageManager().getApplicationIcon(info.provider.getPackageName());
                        } catch (NameNotFoundException e) {
                            mPreview = mContext.getResources().getDrawable(R.drawable.widget_na);
                        }
                    }
                }
            }
            mView = new ImageView(mContext);
            mView.setImageDrawable(mPreview);
        }

        public ImageView getView() {
            if (mView == null) {
                mView = new ImageView(mContext);
                mView.setImageDrawable(getWidgetPreviewDrawable());
            }
            return mView;
        }

        public int getWidgetId() {
            return mWidgetId;
        }

        public int getHeight() {
            return mHeight;
        }

        public Drawable getWidgetPreviewDrawable() {
            return mPreview;
        }
    }
}
