package com.lori.ui.presenter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.lori.R;
import com.lori.core.event.MultipleDaysUpdateEvent;
import com.lori.core.service.TimeEntryService;
import com.lori.core.service.exception.UserCancelledLoginException;
import com.lori.ui.base.BasePresenter;
import com.lori.ui.fragment.WeekFragment;
import com.lori.ui.util.DateHelper;

import javax.inject.Inject;
import java.util.Calendar;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

/**
 * @author artemik
 */
public class WeekFragmentPresenter extends BasePresenter<WeekFragment> {
    private static final String TAG = WeekFragmentPresenter.class.getSimpleName();

    private static final int LOAD_WEEK_TIME_ENTRIES = 0;

    private static final int NUMBER_OF_DAYS_IN_WEEK = 7;

    @Inject
    TimeEntryService timeEntryService;

    private Calendar mondayDate;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableFirst(LOAD_WEEK_TIME_ENTRIES,
                () -> timeEntryService.loadPersonalWeekTimeEntries(mondayDate)
                        .observeOn(mainThread()),
                (weekFragment, timeEntries) -> {
                    Calendar sundayDate = ((Calendar) mondayDate.clone());
                    sundayDate.add(Calendar.DAY_OF_MONTH, NUMBER_OF_DAYS_IN_WEEK - 1);

                    eventBus.post(new MultipleDaysUpdateEvent(timeEntries, mondayDate, sundayDate));
                },
                (weekFragment, throwable) -> {
                    Log.e(TAG, "Failed to load week time entries", throwable);
                    if (throwable instanceof UserCancelledLoginException) {
                        weekFragment.showToast(R.string.login_required);
                    } else {
                        weekFragment.showNetworkError();
                    }
                });
    }

    public void setMondayDate(Calendar mondayDate) {
        this.mondayDate = mondayDate;
        start(LOAD_WEEK_TIME_ENTRIES);
    }

    public void onFragmentVisibilityToUserChanged(boolean visible) {
        if (visible) {
            setToolbarTitleToCurrentMondayDate();
        }
    }

    private void setToolbarTitleToCurrentMondayDate() {
        String monthName = DateHelper.getMonthName(mondayDate, getView().getContext());
        getView().getActivity().setTitle(monthName);

        int year = mondayDate.get(Calendar.YEAR);
        ((AppCompatActivity) getView().getActivity()).getSupportActionBar().setSubtitle(String.valueOf(year));
    }
}
