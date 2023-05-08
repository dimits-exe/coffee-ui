package com.auebds.coffeui.ui.schedule.manage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.auebds.coffeui.R;
import com.auebds.coffeui.entity.Day;
import com.auebds.coffeui.entity.DrinkType;
import com.auebds.coffeui.entity.Schedule;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

/**
 * A {@link Fragment} which displays information about a {@link Schedule}.
 *
 * @author Dimitris Tsirmpas
 */
public class ScheduleFragment extends Fragment {

    // the fragment initialization parameters
    private static final String ARG_SCHEDULE = "schedule";

    public ScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * A factory method to create a new instance of
     * this fragment using the schedule parameter.
     *
     * @param schedule the schedule which will be represented by the fragment.
     * @return A new instance of fragment ScheduleFragment.
     */
    public static ScheduleFragment newInstance(Schedule schedule) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SCHEDULE, schedule);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null){
            Schedule schedule = (Schedule) getArguments().getSerializable(ARG_SCHEDULE);
            this.displayInfo(schedule);
        } else {
            Log.e("SCHEDULE_FRAGMENT", "null schedule passed to new fragment");
        }

    }

    /**
     * Display the information of a schedule on the created fragment.
     * @param schedule the schedule to be displayed
     */
    private void displayInfo(Schedule schedule) {
        TextView nameView = requireView().findViewById(R.id.scheduleNameLabel);
        nameView.setText(schedule.getName());

        TextView typeView = requireView().findViewById(R.id.scheduleDrinkTypeLabel);
        typeView.setText(getLocalizedTypeString(schedule.getType()));

        TextView frequencyView = requireView().findViewById(R.id.scheduleFrequencyLabel);
        frequencyView.setText(formatFrequency(schedule.getDays()));

        TextView timeView = requireView().findViewById(R.id.scheduleTimeLabel);
        timeView.setText(schedule.getTime().format(DateTimeFormatter.ofPattern("HH : mm")));

        TextView activatedView = requireView().findViewById(R.id.scheduleLabelStatus);
        int resCode =  schedule.isActive() ? R.string.status_active: R.string.status_not_active;
        activatedView.setText(getString(resCode));
    }

    /**
     * Get the localized string name for any drink type from the .xml resource files.
     * @param type the DrinkType
     * @return the localized string assigned to this type
     * @implNote The translation will be retrieved by matching the 1st character in the xml
     * string array with the DrinkType's ID.
     */
    private String getLocalizedTypeString(DrinkType type) {
        int typeId = type.getId();
        String[] typeStrings = requireActivity().getResources().getStringArray(R.array.drinks_array);
        return Arrays.stream(typeStrings)
                .filter(typeString -> Character.getNumericValue(typeString.charAt(0)) == typeId)
                .map(typeString -> typeString.substring(2)) // remove number from localized string
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format(Locale.getDefault(),
                        "Type %s does not match with any provided DrinkType ID. " +
                                "See @implNote for more details", type)));
    }

    /**
     * Get the frequency of the schedule by listing the days it's active.
     * @param days the days the schedule will be active
     * @return a string describing the provided days
     */
    private String formatFrequency(Collection<Day> days) {
        //TODO: This should be localized
        StringBuilder sb = new StringBuilder();

        for(Day day: days) {
            sb.append(day.toString().substring(0, 3)); //get first 3 chars of each day
            sb.append(", ");
        }

        return sb.substring(0, sb.length() - 2); // remove last comma
    }

}