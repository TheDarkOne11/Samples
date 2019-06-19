package me.ibrahimsn.viewmodel.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import javax.inject.Inject;

import me.ibrahimsn.viewmodel.R;
import me.ibrahimsn.viewmodel.base.BaseActivity;
import me.ibrahimsn.viewmodel.ui.list.ListFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected int layoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(R.id.screenContainer, new ListFragment()).commit();
    }
}
