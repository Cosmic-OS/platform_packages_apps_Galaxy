/*
 * Copyright (C) 2018 The Potato Open Sauce Project
 *
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

package com.cosmic.settings.fragments;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.om.IOverlayManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.annotation.VisibleForTesting;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.wrapper.OverlayManagerWrapper;
import com.android.settings.wrapper.OverlayManagerWrapper.OverlayInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccentPicker extends InstrumentedDialogFragment implements OnClickListener {

    private static final String TAG_ACCENT_PICKER = "accent_picker";

    private Context mContext;
    private static Preference mAccentPickerPref;
    private LinearLayout ll;
    private OverlayManagerWrapper mOverlayService;
    private PackageManager mPackageManager;
    private View mView;
    private AlertDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();

        // OMS and PMS setup
        mOverlayService = ServiceManager.getService(Context.OVERLAY_SERVICE) != null ? new OverlayManagerWrapper()
                : null;
        mPackageManager = mContext.getPackageManager();

        // Current accent picker
        mView = LayoutInflater.from(mContext).inflate(R.layout.accent_picker, null);
        ll = (LinearLayout) mView.findViewById(R.id.accent_ll);

        if (mView != null) {
            initView();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(mView).setNegativeButton(R.string.cancel, this).setCancelable(false);

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    private void initView() {
        GridLayout grid = new GridLayout(mContext);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        grid.setLayoutParams(lp);
        grid.setColumnCount(6);
        for (String pkgName : getAvailableThemes()) {
            grid.addView(accentButton(pkgName));
        }
        ll.addView(grid);
    }

    private boolean isTheme(OverlayInfo oi) {
        if (!OverlayInfo.CATEGORY_THEME.equals(oi.category)) {
            return false;
        }
        try {
            PackageInfo pi = mPackageManager.getPackageInfo(oi.packageName, 0);
            return pi != null && !pi.isStaticOverlayPackage();
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @VisibleForTesting
    String[] getAvailableThemes() {
        List<OverlayInfo> infos = mOverlayService.getOverlayInfosForTarget("android", UserHandle.myUserId());
        List<String> pkgs = new ArrayList<>(infos.size());
        for (int i = 0, size = infos.size(); i < size; i++) {
            if (isTheme(infos.get(i))) {
                pkgs.add(infos.get(i).packageName);
            }
        }
        return pkgs.toArray(new String[pkgs.size()]);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        ContentResolver resolver = getActivity().getContentResolver();
    }

    public static void show(Fragment parent, Preference preference) {
        if (!parent.isAdded())
            return;
        mAccentPickerPref = preference;
        final AccentPicker dialog = new AccentPicker();
        dialog.setTargetFragment(parent, 0);
        dialog.show(parent.getFragmentManager(), TAG_ACCENT_PICKER);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.FRIES;
    }

    private Button accentButton(String pkg) {
        int bg = getColorFromPackage(pkg);
        int dpVal = (int) (50 * mContext.getResources().getDisplayMetrics().density + 0.5f);
        int margin = dpVal / 10;

        // Create layout params for our buttons
        GridLayout.LayoutParams lParams = new GridLayout.LayoutParams();
        lParams.width = 0;
        lParams.setMargins(margin, margin, margin, margin);
        lParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        lParams.setGravity(Gravity.FILL_HORIZONTAL);

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(dpVal);
        shape.setColor(bg);

        CustomButton button = new CustomButton(mContext);
        button.setBackground(new RippleDrawable(
                new ColorStateList(new int[][] { new int[] {} }, new int[] { Color.WHITE }), shape, null));
        button.setGravity(Gravity.CENTER);
        button.setLayoutParams(lParams);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CharSequence label = null;
                try {
                    label = mPackageManager.getApplicationInfo(pkg, 0).loadLabel(mPackageManager);
                } catch (NameNotFoundException e) {
                    label = pkg;
                }
                mAccentPickerPref.setSummary(label);
                mOverlayService.setEnabledExclusiveInCategory(pkg, UserHandle.myUserId());
                dialog.dismiss();
            }
        });
        return button;
    }

    private int getColorFromPackage(String pkg) {
        String colResName = "accent_device_default_dark";
        Resources res = null;
        try {
            res = mContext.getPackageManager().getResourcesForApplication(pkg);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        int resId = res.getIdentifier(pkg + ":color/" + colResName, null, null);
        return res.getColor(resId);
    }

    public class CustomButton extends Button {

        public CustomButton(Context context) {
            super(context);
        }

        public CustomButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(width, width);
        }
    }
}
