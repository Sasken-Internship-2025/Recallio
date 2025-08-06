package com.example.saskenproject;

import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.*;

public class AddNoteActivity extends AppCompatActivity {

    EditText editTextTitle, editTextBody, editTextChecklistItem, editTextLabel;
    Spinner fontSpinner;
    Button buttonAddChecklistItem, buttonSaveNote, buttonAddImage, buttonAddAudio;
    Button buttonBold, buttonItalic, buttonSetReminder, buttonAddLabel;
    LinearLayout checklistContainer, labelContainer;
    TextView textReminder;
    ImageView ivNoteImage; // Added for showing image

    Uri imageUri = null;
    Uri audioUri = null;

    ArrayList<String> checklistItems = new ArrayList<>();
    ArrayList<String> customLabels = new ArrayList<>();
    String reminderDateTime = "";
    String editingNoteId = null; // To track if we are editing

    DatabaseReference notesRef;

    final int IMAGE_PICK_CODE = 1001;
    final int AUDIO_PICK_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextBody = findViewById(R.id.editTextBody);
        ivNoteImage = findViewById(R.id.ivNoteImage); // Make sure you have this in XML

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createNotificationChannel();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        notesRef = database.getReference("notes");

        fontSpinner = findViewById(R.id.fontSpinner);
        checklistContainer = findViewById(R.id.checklistContainer);
        editTextChecklistItem = findViewById(R.id.editTextChecklistItem);
        buttonAddChecklistItem = findViewById(R.id.buttonAddChecklistItem);
        buttonSaveNote = findViewById(R.id.buttonSaveNote);
        buttonAddImage = findViewById(R.id.buttonAddImage);

        textReminder = findViewById(R.id.textReminder);
        buttonSetReminder = findViewById(R.id.buttonSetReminder);
        buttonBold = findViewById(R.id.buttonBold);
        buttonItalic = findViewById(R.id.buttonItalic);
        labelContainer = findViewById(R.id.labelLayout);
        editTextLabel = findViewById(R.id.editLabel);
        buttonAddLabel = findViewById(R.id.buttonAddLabel);

        // Handle labels
        buttonAddLabel.setOnClickListener(v -> {
            String labelText = editTextLabel.getText().toString().trim();
            if (!labelText.isEmpty()) {
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(labelText);
                checkBox.setTextColor(getResources().getColor(android.R.color.black));
                labelContainer.addView(checkBox);
                customLabels.add(labelText);
                editTextLabel.setText("");
            } else {
                Toast.makeText(this, "Enter label", Toast.LENGTH_SHORT).show();
            }
        });

        // Fonts
        String[] fontOptions = {"Default", "Sans", "Serif", "Monospace"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fontOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSpinner.setAdapter(adapter);

        fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: editTextBody.setTypeface(Typeface.DEFAULT); break;
                    case 1: editTextBody.setTypeface(Typeface.SANS_SERIF); break;
                    case 2: editTextBody.setTypeface(Typeface.SERIF); break;
                    case 3: editTextBody.setTypeface(Typeface.MONOSPACE); break;
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        buttonBold.setOnClickListener(v -> editTextBody.setTypeface(null, Typeface.BOLD));
        buttonItalic.setOnClickListener(v -> editTextBody.setTypeface(null, Typeface.ITALIC));

        buttonAddChecklistItem.setOnClickListener(v -> {
            String itemText = editTextChecklistItem.getText().toString().trim();
            if (!itemText.isEmpty()) {
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(itemText);
                checklistContainer.addView(checkBox);
                checklistItems.add(itemText);
                editTextChecklistItem.setText("");
            } else {
                Toast.makeText(this, "Enter checklist item", Toast.LENGTH_SHORT).show();
            }
        });

        buttonAddImage.setOnClickListener(v -> {
            Intent pickImageintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickImageintent, IMAGE_PICK_CODE);
        });



        buttonSetReminder.setOnClickListener(v -> showDateTimePicker());
        buttonSaveNote.setOnClickListener(v -> saveNoteToFirebase());

        // ✅ Check if editing existing note
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("noteId")) {
            editingNoteId = intent.getStringExtra("noteId");
            loadNoteForEditing(editingNoteId);
        }
    }

    private void loadNoteForEditing(String noteId) {
        notesRef.child(noteId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Note note = snapshot.getValue(Note.class);
                if (note != null) {
                    editTextTitle.setText(note.getTitle());
                    editTextBody.setText(note.getBody());

                    // Load checklist
                    if (note.getChecklist() != null) {
                        checklistItems.clear();
                        checklistContainer.removeAllViews();
                        for (String item : note.getChecklist()) {
                            CheckBox checkBox = new CheckBox(AddNoteActivity.this);
                            checkBox.setText(item);
                            checkBox.setChecked(true);
                            checklistContainer.addView(checkBox);
                            checklistItems.add(item);
                        }
                    }

                    // Load labels
                    if (note.getLabels() != null) {
                        customLabels.clear();
                        labelContainer.removeAllViews();
                        for (String label : note.getLabels()) {
                            CheckBox cb = new CheckBox(AddNoteActivity.this);
                            cb.setText(label);
                            cb.setChecked(true);
                            labelContainer.addView(cb);
                            customLabels.add(label);
                        }
                    }

                    // Load reminder
                    if (note.getReminder() != null) {
                        reminderDateTime = note.getReminder();
                        textReminder.setText(reminderDateTime);
                    }

                    // Load image
                    if (note.getImageUri() != null && !note.getImageUri().isEmpty()) {
                        ivNoteImage.setVisibility(View.VISIBLE);
                        Glide.with(AddNoteActivity.this).load(note.getImageUri()).into(ivNoteImage);
                        imageUri = Uri.parse(note.getImageUri());
                    }

                    // Load audio
                    if (note.getAudioUri() != null) {
                        audioUri = Uri.parse(note.getAudioUri());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddNoteActivity.this, "Failed to load note", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveNoteToFirebase() {
        String title = editTextTitle.getText().toString().trim();
        String body = editTextBody.getText().toString().trim();

        if (title.isEmpty() && body.isEmpty() && checklistItems.isEmpty() && imageUri == null && audioUri == null) {
            Toast.makeText(this, "Cannot save empty note", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> finalLabels = new ArrayList<>();
        for (int i = 0; i < labelContainer.getChildCount(); i++) {
            View view = labelContainer.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox cb = (CheckBox) view;
                if (cb.isChecked()) {
                    finalLabels.add(cb.getText().toString());
                }
            }
        }

        HashMap<String, Object> noteMap = new HashMap<>();
        noteMap.put("title", title);
        noteMap.put("body", body);
        noteMap.put("checklist", checklistItems);
        noteMap.put("labels", finalLabels);
        noteMap.put("reminder", reminderDateTime);

        if (imageUri != null) noteMap.put("imageUri", imageUri.toString());
        if (audioUri != null) noteMap.put("audioUri", audioUri.toString());

        // 🔑 Check if editing an existing note
        String noteId = getIntent().getStringExtra("noteId");
        if (noteId != null) {
            // Update existing note
            notesRef.child(noteId).setValue(noteMap)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Note updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error updating note", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Create a new note
            String newId = notesRef.push().getKey();
            if (newId != null) {
                notesRef.child(newId).setValue(noteMap)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error saving note", Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }


    private void showDateTimePicker() {
        final Calendar current = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth, hourOfDay, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                reminderDateTime = sdf.format(selected.getTime());
                textReminder.setText(reminderDateTime);

                String noteTitle = editTextTitle.getText().toString().trim();
                String noteContent = editTextBody.getText().toString().trim();

                if (!noteTitle.isEmpty()) {
                    scheduleReminder(selected, noteTitle, noteContent);
                } else {
                    scheduleReminder(selected, "Reminder from NotesApp", "");
                }
            }, current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE), false).show();
        }, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ReminderChannel";
            String description = "Channel for Note Reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ReminderChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void scheduleReminder(Calendar calendar, String noteTitle, String noteContent) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("noteTitle", noteTitle);
        intent.putExtra("noteContent", noteContent);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            try {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(this, "Permission denied to set exact alarm", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == IMAGE_PICK_CODE) {
                imageUri = data.getData();
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
                ivNoteImage.setVisibility(View.VISIBLE);
                ivNoteImage.setImageURI(imageUri);
            } else if (requestCode == AUDIO_PICK_CODE) {
                audioUri = data.getData();
                Toast.makeText(this, "Audio selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
