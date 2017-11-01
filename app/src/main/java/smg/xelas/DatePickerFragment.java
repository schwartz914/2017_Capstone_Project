package smg.xelas;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sathya on 4/10/2017.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog DPD = new DatePickerDialog(getActivity(), this, year, month, day);
        Long time = new Date().getTime();
        Long minDate = time - 3469000000000L;
        DPD.getDatePicker().setMinDate(minDate);
        Long maxDate = time;
        DPD.getDatePicker().setMaxDate(maxDate);
        // Create a new instance of DatePickerDialog and return it
        return DPD;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        Activity activity = getActivity();
        EditText editText = (EditText)activity.findViewById(R.id.et_dobSignup);
        String date = day + "/" + (month+1) +"/" + year;
        editText.setText(date);
    }


}
