package com.example.saskenproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private List<OnboardingItem> onboardingItems;
    private OnStartClickListener onStartClickListener;

    public interface OnStartClickListener {
        void onClick();
    }

    public void setOnStartClickListener(OnStartClickListener listener) {
        this.onStartClickListener = listener;
    }

    public OnboardingAdapter(List<OnboardingItem> onboardingItems) {
        this.onboardingItems = onboardingItems;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding_page, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        OnboardingItem item = onboardingItems.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.lottieView.setAnimation(item.getLottieFile());

        // Show start button only on last page
        if (position == onboardingItems.size() - 1) {
            holder.startButton.setVisibility(View.VISIBLE);
            holder.startButton.setOnClickListener(v -> {
                if (onStartClickListener != null) {
                    onStartClickListener.onClick();
                }
            });
        } else {
            holder.startButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return onboardingItems.size();
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        LottieAnimationView lottieView;
        Button startButton;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.pageTitle);
            description = itemView.findViewById(R.id.pageDescription);
            lottieView = itemView.findViewById(R.id.lottieView);
            startButton = itemView.findViewById(R.id.startButton); // make sure your XML has this
        }
    }
}
