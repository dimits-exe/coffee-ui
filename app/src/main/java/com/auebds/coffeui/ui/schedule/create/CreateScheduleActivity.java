package com.auebds.coffeui.ui.schedule.create;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.auebds.coffeui.R;
import com.auebds.coffeui.dao.DebugScheduleDao;
import com.auebds.coffeui.dao.SettingsDao;
import com.auebds.coffeui.databinding.ActivityCreateScheduleBinding;
import com.auebds.coffeui.ui.tutorial.TutorialActivity;
import com.auebds.coffeui.ui.schedule.manage.ManageScheduleActivity;
import com.auebds.coffeui.util.SingletonTTS;

import java.util.ArrayList;

/**
 * The Activity that manages the creation of a schedule.
 *
 * @author Dimitris Tsirmpas
 */
public class CreateScheduleActivity extends AppCompatActivity {

    private final CreateScheduleView view = new CreateScheduleView(this);

    private final CreateScheduleMvp.CreateSchedulePresenter presenter =
            new CreateSchedulePresenter(view, DebugScheduleDao.getInstance());

    private ArrayList<SwitchableFragment> fragmentList;
    private ArrayList<Button> navButtonList;
    private int currentFragmentIdx = -1;

    private ActivityCreateScheduleBinding binding;

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ScheduleDayFragment dayFragment = ScheduleDayFragment.newInstance(presenter);
        fragmentList = new ArrayList<>();
        fragmentList.add(ScheduleNameFragment.newInstance(presenter));
        fragmentList.add(ScheduleDrinkFragment.newInstance(presenter));
        fragmentList.add(ScheduleTimeFragment.newInstance(presenter));
        fragmentList.add(dayFragment);

        navButtonList = new ArrayList<>();
        navButtonList.add(binding.nameButton);
        navButtonList.add(binding.drinkButton);
        navButtonList.add(binding.timeButton);
        navButtonList.add(binding.dayButton);

        binding.nameButton.setOnClickListener(v -> switchFragments(0));
        binding.drinkButton.setOnClickListener(v -> switchFragments(1));
        binding.timeButton.setOnClickListener(v -> switchFragments(2));
        binding.dayButton.setOnClickListener(v -> switchFragments(3));
        binding.doneButton.setOnClickListener(v -> this.presenter.save());

        binding.previousButton.setOnClickListener(v -> switchFragments(currentFragmentIdx - 1));
        binding.nextButton.setOnClickListener(v -> switchFragments(currentFragmentIdx + 1));
        binding.buttonBack.setOnClickListener(v -> toMenu());

        ImageButton helpButton = binding.helpButton;
        helpButton.setOnClickListener(view -> {
            Intent intent = new Intent(CreateScheduleActivity.this, TutorialActivity.class);
            Bundle b = new Bundle();
            b.putString("path", "android.resource://" + getPackageName() + "/" + R.raw.tutorial_create_schedule);
            intent.putExtras(b);
            startActivity(intent);
            finish(); // lord forgive me for I have sinned
        });

        this.view.setDayManager(dayFragment);

        switchFragments(0);

        SingletonTTS.getInstance(getApplicationContext(),
                SettingsDao.getInstance(getApplicationContext()))
                .speakOnce(getString(R.string.tts_create_schedules));
    }


    View getRootView() {
        return getWindow().getDecorView().getRootView();
    }

    void toMenu() {
        this.finish();
    }

    void toMenuWithMessage(String message) {
        Intent menuIntent = new Intent();
        menuIntent.putExtra(ManageScheduleActivity.ARG_MESSAGE, message);
        this.setResult(RESULT_OK, menuIntent);
        this.finish();
    }

    String getStringRes(@StringRes int stringId, String... args) {
        return getString(stringId, (Object []) args);
    }

    /**
     * Switch the fragment shown to a new one.
     * @param newIndex the index of the new fragment
     */
    private void switchFragments(int newIndex) {
        if(currentFragmentIdx != newIndex && newIndex >= 0 && newIndex < this.navButtonList.size()) {
            // notify fragment
            try {
                ((SwitchableFragment) binding.fragmentView.getFragment()).onSwitch();
            } catch(RuntimeException e) {
                Log.e("CREATE_SCHEDULE", e.getLocalizedMessage(), e);
            }

            // switch fragments
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentView, fragmentList.get(newIndex));
            transaction.addToBackStack(null);
            transaction.commit();

            // switch buttons appearance
            for(int i=0; i<=newIndex; i++) {
                setCompletedColor(navButtonList.get(i));
            }

            for(int i=newIndex+1; i<navButtonList.size(); i++) {
                setNotCompletedColor(navButtonList.get(i));
            }

            // update fragment pointer
            this.currentFragmentIdx = newIndex;
        }

        // if next on last panel, act like the DONE button was pressed
        if(newIndex == this.navButtonList.size()) {
            this.presenter.save();
        }
    }

    /**
     * Set the button's background color to represent completion.
     * @param b the button
     */
    private void setCompletedColor(Button b) {
        b.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button_selected));
    }

    /**
     * Set the button's background color to represent non-completion.
     * @param b the button
     */
    private void setNotCompletedColor(Button b) {
        b.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_grey));
    }

}