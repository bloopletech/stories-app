package com.nbsp.materialfilepicker.ui;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.nbsp.materialfilepicker.utils.FileComparator;
import com.nbsp.materialfilepicker.utils.FileUtils;
import com.nbsp.materialfilepicker.utils.PickerState;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by Dimorinny on 24.10.15.
 */
public class FilePickerActivity extends AppCompatActivity implements DirectoryFragment.DirectoryFragmentListener {
    public static final String ARG_CLASS = "arg_class";
    public static final String ARG_PATH = "arg_path";
    public static final String ARG_ROOT_PATH = "arg_root_path";

    public static final String ARG_FILTER = "arg_filter";

    public static final String STATE_PATH = "state_path";
    public static final String STATE_ROOT_PATH = "state_root_path";
    private static final String STATE_PICKER_STATE = "state_picker_state";

    public static final String RESULT_PATH = "result_path";
    private static final int HANDLE_CLICK_DELAY = 150;

    private Toolbar mToolbar;
    private String mPath;
    private String mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private DirectoryFragment mCurrentFragment;
    private Class mClass;

    private CompositeFilter mFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);

        initArguments();

        if (savedInstanceState != null) {
            mPath = savedInstanceState.getString(STATE_PATH);
            mRootPath = savedInstanceState.getString(STATE_ROOT_PATH);
        } else {
            PickerState pickerState = loadPreferences();
            mPath = pickerState.getPath();
            if (mPath == null) {
                mPath = mRootPath;
            }
            pickerState.setPath(mPath);

            System.out.println("mPath: " + mPath);
            System.out.println("mRootPath: " + mRootPath);
            initFragment(pickerState);
        }

        initViews();
        initToolbar();
    }

    @SuppressWarnings("unchecked")
    private void initArguments() {
        if (getIntent().hasExtra(ARG_CLASS)) {
            mClass = (Class)getIntent().getSerializableExtra(ARG_CLASS);
        }

        if (getIntent().hasExtra(ARG_FILTER)) {
            mFilter = (CompositeFilter) getIntent().getSerializableExtra(ARG_FILTER);
        }

        if (getIntent().hasExtra(ARG_PATH)) {
            mPath = getIntent().getStringExtra(ARG_PATH);
        }

        if (getIntent().hasExtra(ARG_ROOT_PATH)) {
            mRootPath = getIntent().getStringExtra(ARG_ROOT_PATH);
        }

        if (mPath == null) {
            mPath = mRootPath;
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
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void initFragment(PickerState pickerState) {
        getFragmentManager().beginTransaction()
                .add(R.id.container, DirectoryFragment.getInstance(mFilter, pickerState))
                .commit();
    }

    private void addFragmentToBackStack(String path) {
        PickerState pickerState = new PickerState(mCurrentFragment.getPickerState());
        pickerState.setPath(path);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, DirectoryFragment.getInstance(mFilter, pickerState))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            addFragmentToBackStack(FileUtils.cutLastSegmentOfPath(
                    mCurrentFragment.getPickerState().getPath()));
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();

        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            moveTaskToBack(true);
            finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mCurrentFragment != null) savePreferences(mCurrentFragment.getPickerState());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_ROOT_PATH, mRootPath);
        outState.putSerializable(STATE_PICKER_STATE, mCurrentFragment.getPickerState());
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
        } else {
            chosen(clickedFile.getPath());
        }
    }

    private void chosen(String path) {
        savePreferences(mCurrentFragment.getPickerState());

        Intent intent = new Intent(this, mClass);
        intent.putExtra(RESULT_PATH, path);

        startActivity(intent);
    }

    private PickerState loadPreferences() {
        PickerState pickerState = new PickerState();
        SharedPreferences preferences = getPreferences();

        pickerState.setPath(preferences.getString("last_path", null));
        pickerState.setSortMethod(preferences.getInt("last_sort_method",
                FileComparator.SORT_ALPHABETIC));
        pickerState.setSortDirectionAsc(preferences.getBoolean("last_sort_direction_asc", true));

        return pickerState;
    }

    private void savePreferences(PickerState pickerState) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString("last_path", pickerState.getPath());
        editor.putInt("last_sort_method", pickerState.getSortMethod());
        editor.putBoolean("last_sort_direction_asc", pickerState.isSortDirectionAsc());
        editor.apply();
    }

    private SharedPreferences getPreferences() {
        return getApplicationContext().getSharedPreferences("file-picker", Context.MODE_PRIVATE);
    }

    @Override
    public void onFragmentLoaded(DirectoryFragment fragment) {
        mCurrentFragment = fragment;
        updateTitle(fragment.getPickerState().getPath());
    }

    private void updateTitle(String path) {
        if (getSupportActionBar() != null) {
            String title = path.isEmpty() ? "/" : path;
            if (title.startsWith(mRootPath)) {
                title = title.replaceFirst(mRootPath, getString(R.string.start_path_name));
            }
            getSupportActionBar().setTitle(title);
        }
    }
}
