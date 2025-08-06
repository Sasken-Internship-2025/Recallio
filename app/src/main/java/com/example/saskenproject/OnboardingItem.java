package com.example.saskenproject;

public class OnboardingItem {

    private String title;
    private String description;
    private String lottieFile;

    public OnboardingItem(String title, String description, String lottieFile) {
        this.title = title;
        this.description = description;
        this.lottieFile = lottieFile;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLottieFile() {
        return lottieFile;
    }
}
