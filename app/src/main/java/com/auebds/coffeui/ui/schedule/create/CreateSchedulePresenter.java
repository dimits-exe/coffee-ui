package com.auebds.coffeui.ui.schedule.create;

import com.auebds.coffeui.dao.ScheduleDao;
import com.auebds.coffeui.dao.ScheduleNameException;
import com.auebds.coffeui.entity.Day;
import com.auebds.coffeui.entity.DrinkType;
import com.auebds.coffeui.entity.Schedule;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Collection;

/**
 * The concrete implementation of the schedule creation MVP Presenter.
 * @author Dimitris Tsirmpas
 */
class CreateSchedulePresenter implements CreateScheduleMvp.CreateSchedulePresenter {
    private final CreateScheduleMvp.CreateScheduleView view;
    private final ScheduleDao scheduleDao;

    /**
     * Create a presenter for creating schedules.
     * @param view the UI view
     */
    public CreateSchedulePresenter(CreateScheduleMvp.CreateScheduleView view,
                                   ScheduleDao scheduleDao) {
        this.view = view;
        this.scheduleDao = scheduleDao;
    }

    @Override
    public void save() {
        String name = view.getName();
        Collection<Day> days = view.getDays();
        boolean isRepeatable = view.isRepeatable();
        LocalTime time = view.getTime();
        DrinkType type = this.view.getSelectedDrink();


        if(name.trim().isEmpty()) {
            view.displayError("Please select a non-empty schedule name.");
            return;
        }

        if(days.size() == 0){
            view.displayError("Please select one or more days.");
            return;
        }

        if(time == null) {
            view.displayError("Please select a valid time for your schedule.");
            return;
        }

        if(type == null) {
            view.displayError("An internal error has occurred. Please restart the app and inform " +
                    "the developers");
            return;
        }

        Schedule schedule = new Schedule(name,isRepeatable, days, time, type);
        try {
            scheduleDao.save(schedule);
            view.displaySuccess(schedule);
            // should we return automatically to the menu or would it be annoying?
            //view.toMenu();
        } catch (ScheduleNameException se) {
            view.displayNameConflictError(name);
        } catch (IOException ioe) {
            view.displayError(ioe.getLocalizedMessage());
        }
    }

    @Override
    public void groupSelected(RepetitionPeriod repetitionPeriod) {
        view.groupSelected(repetitionPeriod);
    }

    @Override
    public void daySelected(Day day) {
        view.daySelected(day);
    }
}
