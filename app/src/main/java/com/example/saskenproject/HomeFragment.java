package com.example.saskenproject;

import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private List<Note> noteList;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noteList = new ArrayList<>();

        adapter = new NoteAdapter(getContext(), noteList, new NoteAdapter.OnNoteActionListener() {
            @Override
            public void onEdit(Note note) {
                Intent intent = new Intent(getContext(), AddNoteActivity.class);
                intent.putExtra("noteId", note.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(Note note) {
                databaseReference.child(note.getId()).removeValue();
                Toast.makeText(getContext(), "Note Deleted", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("notes");
        fetchNotes();

        return view;
    }

    private void fetchNotes() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noteList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        Note note = dataSnapshot.getValue(Note.class);
                        if (note != null) {
                            note.setId(dataSnapshot.getKey());

                            // DEBUG LOG Firebase raw data
                            Log.d("HomeFragment", "Raw Note: " + dataSnapshot.getValue());

                            // ✅ Handle checklist
                            if (dataSnapshot.child("checklist").exists()) {
                                Object rawChecklist = dataSnapshot.child("checklist").getValue();
                                Log.d("HomeFragment", "Raw Checklist: " + rawChecklist);

                                List<String> checklist = new ArrayList<>();
                                if (rawChecklist instanceof HashMap) {
                                    for (Object value : ((HashMap<?, ?>) rawChecklist).values()) {
                                        checklist.add(String.valueOf(value));
                                    }
                                } else if (rawChecklist instanceof List) {
                                    for (Object value : (List<?>) rawChecklist) {
                                        checklist.add(String.valueOf(value));
                                    }
                                }
                                note.setChecklist(checklist);
                            }

                            // ✅ Handle labels
                            if (dataSnapshot.child("labels").exists()) {
                                Object rawLabels = dataSnapshot.child("labels").getValue();
                                Log.d("HomeFragment", "Raw Labels: " + rawLabels);

                                List<String> labels = new ArrayList<>();
                                if (rawLabels instanceof HashMap) {
                                    for (Object value : ((HashMap<?, ?>) rawLabels).values()) {
                                        labels.add(String.valueOf(value));
                                    }
                                } else if (rawLabels instanceof List) {
                                    for (Object value : (List<?>) rawLabels) {
                                        labels.add(String.valueOf(value));
                                    }
                                }
                                note.setLabels(labels);
                            }

                            noteList.add(note);
                        }
                    } catch (Exception e) {
                        Log.e("HomeFragment", "Error parsing note: ", e);
                    }
                }

                Log.d("HomeFragment", "Notes count: " + noteList.size());
                adapter.notifyDataSetChanged();

                // Hide "Your Notes" text if there are notes
                View emptyText = getView().findViewById(R.id.text_home);
                if (emptyText != null) {
                    emptyText.setVisibility(noteList.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load notes", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
