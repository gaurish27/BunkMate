package com.example.bunkmate.ui.theme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bunkmate.R;
import com.example.bunkmate.model.Subject;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.VH> {

    public interface OnSubjectClickListener {
        void onSubjectClick(Subject subject);
    }

    private List<Subject> data;
    private final OnSubjectClickListener listener;

    // Updated constructor
    public SubjectAdapter(List<Subject> data, OnSubjectClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    public void setData(List<Subject> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_subject, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Subject s = data.get(position);
        h.tvName.setText(s.name);
        h.tvCounts.setText("Attended " + s.attended + " / " + s.total);
        h.tvPercent.setText(s.getPercent() + "%");
        h.tvSafeBunks.setText("Safe bunks: " + s.getSafeBunksForDisplay());

        // 👇 Add click support
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onSubjectClick(s);
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvCounts, tvPercent, tvSafeBunks;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSubjectName);
            tvCounts = itemView.findViewById(R.id.tvAttendance);
            tvPercent = itemView.findViewById(R.id.tvPercentage);
            tvSafeBunks = itemView.findViewById(R.id.tvSafeBunks);
        }
    }
}
