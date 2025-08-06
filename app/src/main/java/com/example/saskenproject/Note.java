package com.example.saskenproject;

import java.util.List;

public class Note {
    private String id;
    private String title;
    private String body; // <-- Added
    private String imageUri; // <-- Changed from imageUrl
    private String audioUri; // <-- Added
    private List<String> checklist;
    private List<String> labels; // <-- Changed from label
    private String reminder;

    public Note() {} // Required empty constructor for Firebase

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public String getAudioUri() { return audioUri; }
    public void setAudioUri(String audioUri) { this.audioUri = audioUri; }

    public List<String> getChecklist() { return checklist; }
    public void setChecklist(List<String> checklist) { this.checklist = checklist; }

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }

    public String getReminder() { return reminder; }
    public void setReminder(String reminder) { this.reminder = reminder; }
}
