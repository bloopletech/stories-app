package com.nbsp.materialfilepicker.ui;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import com.nbsp.materialfilepicker.R;
import com.nbsp.materialfilepicker.filter.CompositeFilter;
import com.nbsp.materialfilepicker.utils.FileUtils;
import com.nbsp.materialfilepicker.utils.PickerState;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by Dimorinny on 24.10.15.
 */
public class FilePickerActivity extends AppCompatActivity implements DirectoryFragment.FileClickListener, DirectoryFragment.PickerStateListener {
    public static final String ARG_CLASS = "arg_class";
    public static final String ARG_START_PATH = "arg_start_path";
    public static final String ARG_PICKER_STATE = "arg_picker_state";

    public static final String ARG_FILTER = "arg_filter";

    public static final String STATE_START_PATH = "state_start_path";
    private static final String STATE_PICKER_STATE = "state_picker_state";

    public static final String RESULT_PATH = "result_path";
    public static final String RESULT_PICKER_STATE = "result_picker_state";
    private static final int HANDLE_CLICK_DELAY = 150;

    private Toolbar mToolbar;
    private String mStartPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private PickerState mPickerState;
    private Class mClass;

    private CompositeFilter mFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);

        initArguments();
        initViews();
        initToolbar();

        if (savedInstanceState != null) {
            mStartPath = savedInstanceState.getString(STATE_START_PATH);
            mPickerState = (PickerState)savedInstanceState.getSerializable(STATE_PICKER_STATE);
        } else {
            initFragment();
        }


    }

    @SuppressWarnings("unchecked")
    private void initArguments() {
        if (getIntent().hasExtra(ARG_CLASS)) {
            mClass = (Class)getIntent().getSerializableExtra(ARG_CLASS);
        }

        if (getIntent().hasExtra(ARG_FILTER)) {
            mFilter = (CompositeFilter) getIntent().getSerializableExtra(ARG_FILTER);
        }

        if (getIntent().hasExtra(ARG_PICKER_STATE)) {
            mPickerState = (PickerState)getIntent().getSerializableExtra(ARG_PICKER_STATE);
        }

        if (getIntent().hasExtra(ARG_START_PATH)) {
            mStartPath = getIntent().getStringExtra(ARG_START_PATH);
        }

        if (mPickerState.getPath() == null) {
            mPickerState.setPath(mStartPath);
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);

        // Show back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Truncate start of toolbar title
        try {
            Field f = mToolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);

            TextView textView = (TextView) f.get(mToolbar);
            textView.setEllipsize(TextUtils.TruncateAt.START);
        } catch (Exception ignored) {}

        updateTitle();
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void initFragment() {
        getFragmentManager().beginTransaction()
                .add(R.id.container, DirectoryFragment.getInstance(mFilter, mPickerState))
                .commit();
    }

    private void updateTitle() {
        if (getSupportActionBar() != null) {
            String title = mPickerState.getPath().isEmpty() ? "/" : mPickerState.getPath();
            if (title.startsWith(mStartPath)) {
                title = title.replaceFirst(mStartPath, getString(R.string.start_path_name));
            }
            getSupportActionBar().setTitle(title);
        }
    }

    private void addFragmentToBackStack(String path) {
        mPickerState.setPath(path);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, DirectoryFragment.getInstance(mFilter, mPickerState))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();

        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            mPickerState.setPath(FileUtils.cutLastSegmentOfPath(mPickerState.getPath()));
            updateTitle();
        } else {
            moveTaskToBack(true);
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_START_PATH, mStartPath);
        outState.putSerializable(STATE_PICKER_STATE, mPickerState);
    }

    @Override
    public void onFileClicked(final File clickedFile) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                handleFileClicked(clickedFile);
            }
        }, HANDLE_CLICK_DELAY);
    }

    private void handleFileClicked(final File clickedFile) {
        if (clickedFile.isDirectory()) {
            addFragmentToBackStack(clickedFile.getPath());
            updateTitle();
        } else {
            chosen(clickedFile.getPath());
        }
    }

    private void chosen(String path) {
        Intent intent = new Intent(this, mClass);
        intent.putExtra(RESULT_PATH, path);
        intent.putExtra(RESULT_PICKER_STATE, mPickerState);

        startActivity(intent);
    }

    @Override
    public void onPickerStateChanged(PickerState pickerState) {
        mPickerState.setSortMethod(pickerState.getSortMethod());
        mPickerState.setSortDirectionAsc(pickerState.isSortDirectionAsc());
    }
}
