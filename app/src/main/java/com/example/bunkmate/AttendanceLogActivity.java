package com.example.bunkmate;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bunkmate.database.AttendanceDAO;
import java.util.*;

public class AttendanceLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_log);

        long subjectId = getIntent().getLongExtra("subject_id", -1);
        String subjectName = getIntent().getStringExtra("subject_name");
        setTitle(subjectName + " - Attendance Log");

        ListView listView = findViewById(R.id.listAttendance);

        AttendanceDAO dao = new AttendanceDAO(this);
        List<Map<String, String>> logs = dao.getLogsForSubject(subjectId);

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                logs,
                android.R.layout.simple_list_item_2,
                new String[]{"date", "status"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        listView.setAdapter(adapter);
    }
}
