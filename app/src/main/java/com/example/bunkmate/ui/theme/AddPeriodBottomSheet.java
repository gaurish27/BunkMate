package com.example.bunkmate.ui;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.bunkmate.R;
import com.example.bunkmate.database.TimetableDAO;
import com.example.bunkmate.database.SubjectDAO;
import com.example.bunkmate.model.Subject;
import com.example.bunkmate.ui.theme.SelectSubjectBottomSheet;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.Calendar;
import java.util.List;

public class AddPeriodBottomSheet extends BottomSheetDialogFragment {

    public interface OnSaved { void onSaved(); }
    private final OnSaved onSaved;

    public AddPeriodBottomSheet(OnSaved onSaved) {
        this.onSaved = onSaved;
    }

    private Subject pickedSubject = null;
    private int periodVal = 1;
    private String pickedTime = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottomsheet_add_period, container, false);

        Spinner spinnerDay = v.findViewById(R.id.spinnerDay);
        TextView tvSubject = v.findViewById(R.id.tvSubject);
        TextView tvTime = v.findViewById(R.id.tvTime);
        TextView tvPeriodValue = v.findViewById(R.id.tvPeriodValue);
        ImageButton btnMinus = v.findViewById(R.id.btnMinus);
        ImageButton btnPlus = v.findViewById(R.id.btnPlus);
        Button btnSave = v.findViewById(R.id.btnSave);
        LinearLayout rowSubject = v.findViewById(R.id.rowSubject);

        String[] days = {"Mon","Tue","Wed","Thu","Fri","Sat"};
        ArrayAdapter<String> aa = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, days);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(aa);

        // subject list for selection
        SubjectDAO sdao = new SubjectDAO(requireContext());
        List<Subject> subjects = sdao.getAll();

        rowSubject.setOnClickListener(view -> {
            // open subject picker bottomsheet
            new SelectSubjectBottomSheet(subject -> {
                pickedSubject = subject;
                tvSubject.setText(subject.name);
            }).show(getParentFragmentManager(), "select_subject");
        });

        btnMinus.setOnClickListener(l -> {
            if (periodVal > 1) {
                periodVal--;
                tvPeriodValue.setText(String.valueOf(periodVal));
            }
        });
        btnPlus.setOnClickListener(l -> {
            periodVal++;
            tvPeriodValue.setText(String.valueOf(periodVal));
        });

        tvTime.setOnClickListener(l -> {
            Calendar now = Calendar.getInstance();
            int h = now.get(Calendar.HOUR_OF_DAY), m = now.get(Calendar.MINUTE);
            new TimePickerDialog(requireContext(), (tp, hourOfDay, minute) -> {
                String ampm = (hourOfDay >= 12) ? "PM" : "AM";
                int h12 = hourOfDay % 12; if (h12 == 0) h12 = 12;
                pickedTime = String.format("%d:%02d %s", h12, minute, ampm);
                tvTime.setText(pickedTime);
            }, h, m, false).show();
        });

        btnSave.setOnClickListener(l -> {
            String day = (String) spinnerDay.getSelectedItem();
            if (pickedSubject == null) {
                Toast.makeText(requireContext(), "Pick a subject", Toast.LENGTH_SHORT).show();
                return;
            }
            TimetableDAO tt = new TimetableDAO(requireContext());
            long id = tt.addPeriod(day, periodVal, pickedSubject.id, pickedTime);
            if (id != -1) {
                Toast.makeText(requireContext(), "Period added", Toast.LENGTH_SHORT).show();
                if (onSaved != null) onSaved.onSaved();
                dismiss();
            } else {
                Toast.makeText(requireContext(), "Failed to add period", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}
