package com.example.saskenproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context context;
    private List<Note> noteList;
    private OnNoteActionListener listener;

    public interface OnNoteActionListener {
        void onEdit(Note note);
        void onDelete(Note note);
    }

    public NoteAdapter(Context context, List<Note> noteList, OnNoteActionListener listener) {
        this.context = context;
        this.noteList = noteList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);

        // Title
        if (note.getTitle() != null && !note.getTitle().isEmpty()) {
            holder.tvTitle.setText(note.getTitle());
            holder.tvTitle.setVisibility(View.VISIBLE);
        } else {
            holder.tvTitle.setVisibility(View.GONE);
        }

        // Body
        if (note.getBody() != null && !note.getBody().isEmpty()) {
            holder.tvBody.setText(note.getBody());
            holder.tvBody.setVisibility(View.VISIBLE);
        } else {
            holder.tvBody.setVisibility(View.GONE);
        }

        // Labels
        holder.tvLabel.setVisibility(View.GONE);
        if (note.getLabels() != null && !note.getLabels().isEmpty()) {
            holder.tvLabel.setText("Labels: " + String.join(", ", note.getLabels()));
            holder.tvLabel.setVisibility(View.VISIBLE);
        }

        // Reminder
        if (note.getReminder() != null && !note.getReminder().isEmpty()) {
            holder.tvReminder.setText("Reminder: " + note.getReminder());
            holder.tvReminder.setVisibility(View.VISIBLE);
        } else {
            holder.tvReminder.setVisibility(View.GONE);
        }

        // Image
        if (note.getImageUri() != null && !note.getImageUri().isEmpty()) {
            holder.ivNoteImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(note.getImageUri()).into(holder.ivNoteImage);
        } else {
            holder.ivNoteImage.setVisibility(View.GONE);
        }

        // Checklist
        holder.checklistLayout.removeAllViews();
        if (note.getChecklist() != null && !note.getChecklist().isEmpty()) {
            holder.checklistLayout.setVisibility(View.VISIBLE);
            for (String item : note.getChecklist()) {
                CheckBox checkBox = new CheckBox(context);
                checkBox.setText(item);
                checkBox.setEnabled(false); // prevent editing from home page
                holder.checklistLayout.addView(checkBox);
            }
        } else {
            holder.checklistLayout.setVisibility(View.GONE);
        }

        // Buttons
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(note));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(note));
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBody, tvLabel, tvReminder;
        ImageView ivNoteImage, btnEdit, btnDelete;
        LinearLayout checklistLayout;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBody = itemView.findViewById(R.id.tvBody); // Added
            tvLabel = itemView.findViewById(R.id.tvLabel);
            tvReminder = itemView.findViewById(R.id.tvReminder);
            ivNoteImage = itemView.findViewById(R.id.ivNoteImage);
            checklistLayout = itemView.findViewById(R.id.checklistLayout);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
