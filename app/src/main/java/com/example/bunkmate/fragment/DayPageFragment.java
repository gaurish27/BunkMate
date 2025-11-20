package com.example.bunkmate.fragment;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import com.example.bunkmate.R;
import com.example.bunkmate.database.AttendanceDAO;
import com.example.bunkmate.database.TimetableDAO;
import java.text.SimpleDateFormat;
import java.util.*;

public class DayPageFragment extends Fragment {

    private static final String ARG_DAY = "arg_day";
    private String dayName;
    private LinearLayout layoutTimetable;
    private LinearLayout layoutAttendance;

    public static DayPageFragment newInstance(String day) {
        DayPageFragment f = new DayPageFragment();
        Bundle b = new Bundle();
        b.putString(ARG_DAY, day);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dayName = getArguments() != null ? getArguments().getString(ARG_DAY) : "Mon";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_day_page, container, false);
        layoutTimetable = v.findViewById(R.id.layoutTimetableDay);
        layoutAttendance = v.findViewById(R.id.layoutAttendanceDay);
        loadDayData();
        return v;
    }

    public void loadDayData() {
        layoutTimetable.removeAllViews();
        layoutAttendance.removeAllViews();

        TimetableDAO tt = new TimetableDAO(requireContext());
        List<TimetableDAO.PeriodRow> rows = tt.getByDay(dayName);

        LayoutInflater inf = LayoutInflater.from(requireContext());
        for (TimetableDAO.PeriodRow r : rows) {
            View card = inf.inflate(R.layout.item_timetable_period, layoutTimetable, false);
            TextView tvPeriod = card.findViewById(R.id.tvPeriod);
            TextView tvSubject = card.findViewById(R.id.tvSubject);
            TextView tvTime = card.findViewById(R.id.tvTime);

            tvPeriod.setText("Period " + r.periodNumber);
            tvSubject.setText(getSubjectName(r.subjectId));
            tvTime.setText(r.time == null ? "" : r.time);

            card.setOnLongClickListener(view -> {
                Toast.makeText(requireContext(), "Long press to edit (not implemented)", Toast.LENGTH_SHORT).show();
                return true;
            });

            layoutTimetable.addView(card);
        }

        // === Attendance Section (only for today's tab) ===
        String today = new SimpleDateFormat("EEE", Locale.getDefault()).format(new Date());
        boolean isToday = dayName.equalsIgnoreCase(mapShortDay(today));

        if (isToday) {
            for (TimetableDAO.PeriodRow r : rows) {
                View card = inf.inflate(R.layout.item_today_attendance, layoutAttendance, false);

                TextView tvAPeriod = card.findViewById(R.id.tvAttendancePeriod);
                TextView tvASubject = card.findViewById(R.id.tvAttendanceSubject);
                TextView tvATime = card.findViewById(R.id.tvAttendanceTime);
                TextView tvStatus = card.findViewById(R.id.tvStatusMarked); // Added from new layout

                Button btnPresent = card.findViewById(R.id.btnPresent);
                Button btnAbsent = card.findViewById(R.id.btnAbsent);
                Button btnCancelled = card.findViewById(R.id.btnCancelled);

                tvAPeriod.setText("Period " + r.periodNumber);
                tvASubject.setText(getSubjectName(r.subjectId));
                tvATime.setText(r.time == null ? "" : r.time);

                final long subjectId = r.subjectId;
                final Integer periodNo = r.periodNumber;
                final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                AttendanceDAO aDao = new AttendanceDAO(requireContext());
                String existing = aDao.getExistingStatusForUI(date, subjectId, periodNo);

                updateButtonsState(btnPresent, btnAbsent, btnCancelled, tvStatus, existing);

                View.OnClickListener listener = view -> {
                    String status = view == btnPresent ? "Present" :
                            view == btnAbsent ? "Absent" : "Cancelled";
                    boolean ok = false;

                    switch (status) {
                        case "Present": ok = aDao.markPresent(date, subjectId, periodNo); break;
                        case "Absent": ok = aDao.markAbsent(date, subjectId, periodNo); break;
                        case "Cancelled": ok = aDao.markCancelled(date, subjectId, periodNo); break;
                    }

                    if (ok) {
                        updateButtonsState(btnPresent, btnAbsent, btnCancelled, tvStatus, status);
                        Toast.makeText(requireContext(), "Marked as " + status, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to mark attendance!", Toast.LENGTH_SHORT).show();
                    }
                };

                btnPresent.setOnClickListener(listener);
                btnAbsent.setOnClickListener(listener);
                btnCancelled.setOnClickListener(listener);

                layoutAttendance.addView(card);
            }
        } else {
            TextView tv = new TextView(requireContext());
            tv.setText("Switch to today's tab to mark attendance.");
            tv.setPadding(24, 24, 24, 24);
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.textSecondary));
            layoutAttendance.addView(tv);
        }
    }

    private void updateButtonsState(Button present, Button absent, Button cancelled, TextView tvStatus, String status) {
        // Reset
        present.setEnabled(true);
        absent.setEnabled(true);
        cancelled.setEnabled(true);
        tvStatus.setVisibility(View.GONE);

        present.setText("Present");
        absent.setText("Absent");
        cancelled.setText("Cancelled");

        present.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.success));
        absent.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.destructive));
        cancelled.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.cancelled));

        // Update selected state
        if (status == null) return;

        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText("Marked: " + status + " ✓");
        tvStatus.setTextColor(ContextCompat.getColor(requireContext(),
                status.equals("Present") ? R.color.success :
                        status.equals("Absent") ? R.color.destructive :
                                R.color.cancelled));

        // Disable other buttons
        if (status.equals("Present")) {
            present.setEnabled(false);
            present.setText("✓ Present");
        } else if (status.equals("Absent")) {
            absent.setEnabled(false);
            absent.setText("✓ Absent");
        } else if (status.equals("Cancelled")) {
            cancelled.setEnabled(false);
            cancelled.setText("✓ Cancelled");
        }
    }

    private String getSubjectName(long subjectId) {
        android.database.sqlite.SQLiteDatabase db = new com.example.bunkmate.database.DBHelper(requireContext()).getReadableDatabase();
        String name = "Subject";
        try (android.database.Cursor c = db.query(
                com.example.bunkmate.database.DBHelper.TABLE_SUBJECTS,
                new String[]{com.example.bunkmate.database.DBHelper.COL_SUBJ_NAME},
                com.example.bunkmate.database.DBHelper.COL_SUBJ_ID + "=?",
                new String[]{String.valueOf(subjectId)},
                null, null, null
        )) {
            if (c != null && c.moveToFirst()) name = c.getString(0);
        }
        db.close();
        return name;
    }

    private String mapShortDay(String sd) {
        return sd; // already short form like Mon, Tue, etc.
    }
}
