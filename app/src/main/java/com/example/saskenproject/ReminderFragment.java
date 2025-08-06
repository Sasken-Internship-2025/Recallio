package com.example.saskenproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ReminderFragment extends Fragment {

    private String title;
    private String content;

    public ReminderFragment(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public ReminderFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        TextView titleView = view.findViewById(R.id.reminder_fragment_title);
        TextView contentView = view.findViewById(R.id.reminder_fragment_content);

        titleView.setText("⏰ Reminder: " + title);
        contentView.setText(content);

        return view;
    }
}


