package com.example.bunkmate.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bunkmate.AttendanceLogActivity;
import com.example.bunkmate.R;
import com.example.bunkmate.database.SubjectDAO;
import com.example.bunkmate.model.Subject;
import com.example.bunkmate.ui.theme.SubjectAdapter;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rv;
    private SubjectAdapter adapter;
    private SubjectDAO subjectDAO;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        subjectDAO = new SubjectDAO(requireContext());

        rv = v.findViewById(R.id.recyclerSubjects);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapter with click listener for each subject card
        adapter = new SubjectAdapter(new ArrayList<>(), subject -> {
            // On subject click → open Attendance Log screen
            Intent intent = new Intent(requireContext(), AttendanceLogActivity.class);
            intent.putExtra("subject_id", subject.getId());
            intent.putExtra("subject_name", subject.getName());
            startActivity(intent);
        });

        rv.setAdapter(adapter);

        Button fab = v.findViewById(R.id.btnAddSubject);
        fab.setOnClickListener(view -> showAddDialog());

        loadSubjects();
        return v;
    }

    private void loadSubjects() {
        List<Subject> list = subjectDAO.getAll();
        for (Subject s : list) {
            int weekly = subjectDAO.getWeeklyClassCount(s.getId());
            s.setSafeBunksForDisplay(s.calculateSafeBunks(weekly));
        }
        adapter.setData(list);
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_subject, null, false);

        EditText etName = dialogView.findViewById(R.id.etSubjectName);
        EditText etMin = dialogView.findViewById(R.id.etMinRequired);
        EditText etProfessor = dialogView.findViewById(R.id.etProfessor);

        new AlertDialog.Builder(requireContext())
                .setTitle("Add Subject")
                .setView(dialogView)
                .setPositiveButton("Save", (d, which) -> {
                    String name = etName.getText().toString().trim();
                    String minStr = etMin.getText().toString().trim();
                    String prof = etProfessor.getText().toString().trim();

                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(getContext(), "Subject name required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int min = 75;
                    try {
                        if (!TextUtils.isEmpty(minStr))
                            min = Math.max(1, Math.min(100, Integer.parseInt(minStr)));
                    } catch (Exception ignored) {}

                    long id = subjectDAO.add(name, min, prof);
                    if (id == -1) {
                        Toast.makeText(getContext(), "Subject already exists", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Added successfully", Toast.LENGTH_SHORT).show();
                        loadSubjects();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
