package com.example.bunkmate.ui.theme;
//package com.example.bunkmate.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.bunkmate.R;
import com.example.bunkmate.database.SubjectDAO;
import com.example.bunkmate.model.Subject;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.ArrayList;
import java.util.List;

public class SelectSubjectBottomSheet extends BottomSheetDialogFragment {

    public interface OnSubjectPicked { void onPicked(Subject subject); }

    private OnSubjectPicked callback;

    public SelectSubjectBottomSheet(OnSubjectPicked cb) { this.callback = cb; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottomsheet_select_subject, container, false);

        androidx.recyclerview.widget.RecyclerView rv = v.findViewById(R.id.rvSubjects);
        com.google.android.material.textfield.TextInputEditText etSearch = v.findViewById(R.id.etSearch);

        SubjectDAO dao = new SubjectDAO(requireContext());
        List<Subject> all = dao.getAll();
        List<Subject> filtered = new ArrayList<>(all);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        SubjectPickAdapter adapter = new SubjectPickAdapter(filtered, s -> {
            if (callback != null) callback.onPicked(s);
            dismiss();
        });
        rv.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                filtered.clear();
                String q = s.toString().trim().toLowerCase();
                for (Subject sub : all) {
                    if (sub.name.toLowerCase().contains(q)) filtered.add(sub);
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        return v;
    }
}
