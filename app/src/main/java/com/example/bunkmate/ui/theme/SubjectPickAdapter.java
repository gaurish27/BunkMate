package com.example.bunkmate.ui.theme;
//package com.example.bunkmate.ui;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bunkmate.R;
import com.example.bunkmate.model.Subject;
import java.util.List;

class SubjectPickAdapter extends RecyclerView.Adapter<SubjectPickAdapter.Holder> {

    interface OnClick { void onClick(Subject s); }
    private final List<Subject> data;
    private final OnClick click;

    SubjectPickAdapter(List<Subject> data, OnClick click) {
        this.data = data; this.click = click;
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tvName, tvMeta;
        Holder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvMeta = itemView.findViewById(R.id.tvMeta);
        }
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_pick, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int pos) {
        Subject s = data.get(pos);
        h.tvName.setText(s.name);
        h.tvMeta.setText(s.getPercent() + "%");
        h.itemView.setOnClickListener(v -> click.onClick(s));
    }

    @Override public int getItemCount() { return data == null ? 0 : data.size(); }
}
