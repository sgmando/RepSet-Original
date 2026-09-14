package com.kingdomunderground.repsettracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String PREFS = "rep_set_prefs";
    private static final String KEY_SETS = "sets";
    private static final String KEY_REPS = "reps";

    private static final int BLACK = Color.rgb(0, 0, 0);
    private static final int CARD = Color.rgb(18, 18, 18);
    private static final int CARD_2 = Color.rgb(28, 28, 28);
    private static final int RED = Color.rgb(229, 9, 20);
    private static final int RED_DARK = Color.rgb(139, 0, 8);
    private static final int WHITE = Color.WHITE;
    private static final int MUTED = Color.rgb(170, 170, 170);

    private SharedPreferences prefs;
    private int sets;
    private int reps;
    private TextView setsValue;
    private TextView repsValue;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setStatusBarColor(BLACK);
        getWindow().setNavigationBarColor(BLACK);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        sets = prefs.getInt(KEY_SETS, 0);
        reps = prefs.getInt(KEY_REPS, 0);

        setContentView(buildUi());
        refresh();
    }

    private View buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(18));
        root.setBackgroundColor(BLACK);
        root.setFitsSystemWindows(true);

        TextView title = text("REPSET", 28, WHITE, Typeface.BOLD);
        title.setLetterSpacing(0.08f);
        root.addView(title, fullWrap());

        TextView subtitle = text("Fast gym counter • tap and keep moving", 14, MUTED, Typeface.NORMAL);
        LinearLayout.LayoutParams subtitleLp = fullWrap();
        subtitleLp.topMargin = dp(2);
        root.addView(subtitle, subtitleLp);

        root.addView(space(16));

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.setWeightSum(2f);

        LinearLayout setsCard = statCard("SETS");
        setsValue = (TextView) setsCard.getChildAt(1);
        LinearLayout repsCard = statCard("REPS");
        repsValue = (TextView) repsCard.getChildAt(1);

        LinearLayout.LayoutParams cardLp1 = new LinearLayout.LayoutParams(0, dp(138), 1f);
        cardLp1.rightMargin = dp(7);
        stats.addView(setsCard, cardLp1);

        LinearLayout.LayoutParams cardLp2 = new LinearLayout.LayoutParams(0, dp(138), 1f);
        cardLp2.leftMargin = dp(7);
        stats.addView(repsCard, cardLp2);

        root.addView(stats, fullWrap());

        root.addView(space(18));

        TextView repButton = bigButton("+  REP", RED, WHITE, dp(92), 27);
        repButton.setOnClickListener(v -> {
            reps++;
            saveAndRefresh("Rep +1");
            haptic(v);
        });
        root.addView(repButton, fullWrap());

        root.addView(space(12));

        TextView completeButton = outlinedButton("COMPLETE SET", RED, WHITE, dp(92), 27);
        completeButton.setOnClickListener(v -> {
            sets++;
            reps = 0;
            saveAndRefresh("Set logged • reps reset");
            haptic(v);
        });
        root.addView(completeButton, fullWrap());

        root.addView(space(12));

        TextView setButton = bigButton("+  SET", RED_DARK, WHITE, dp(92), 27);
        setButton.setOnClickListener(v -> {
            sets++;
            saveAndRefresh("Set +1");
            haptic(v);
        });
        root.addView(setButton, fullWrap());

        root.addView(space(14));

        LinearLayout corrections = new LinearLayout(this);
        corrections.setOrientation(LinearLayout.HORIZONTAL);
        corrections.setWeightSum(2f);

        TextView minusRep = smallButton("− REP");
        minusRep.setOnClickListener(v -> {
            if (reps > 0) reps--;
            saveAndRefresh("Rep −1");
            haptic(v);
        });
        LinearLayout.LayoutParams minusRepLp = new LinearLayout.LayoutParams(0, dp(60), 1f);
        minusRepLp.rightMargin = dp(7);
        corrections.addView(minusRep, minusRepLp);

        TextView minusSet = smallButton("− SET");
        minusSet.setOnClickListener(v -> {
            if (sets > 0) sets--;
            saveAndRefresh("Set −1");
            haptic(v);
        });
        LinearLayout.LayoutParams minusSetLp = new LinearLayout.LayoutParams(0, dp(60), 1f);
        minusSetLp.leftMargin = dp(7);
        corrections.addView(minusSet, minusSetLp);

        root.addView(corrections, fullWrap());

        Space flex = new Space(this);
        root.addView(flex, new LinearLayout.LayoutParams(1, 0, 1f));

        statusText = text("Ready", 13, MUTED, Typeface.NORMAL);
        statusText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams statusLp = fullWrap();
        statusLp.bottomMargin = dp(10);
        root.addView(statusText, statusLp);

        TextView reset = text("RESET WORKOUT", 14, RED, Typeface.BOLD);
        reset.setGravity(Gravity.CENTER);
        reset.setPadding(dp(10), dp(14), dp(10), dp(14));
        reset.setBackground(roundRect(CARD, dp(14), Color.rgb(50, 50, 50), dp(1)));
        reset.setOnClickListener(v -> confirmReset());
        root.addView(reset, fullWrap());

        return root;
    }

    private LinearLayout statCard(String label) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setBackground(roundRect(CARD, dp(20), Color.rgb(44, 44, 44), dp(1)));

        TextView labelView = text(label, 15, MUTED, Typeface.BOLD);
        labelView.setGravity(Gravity.CENTER);
        card.addView(labelView, fullWrap());

        TextView value = text("0", 58, WHITE, Typeface.BOLD);
        value.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams valueLp = fullWrap();
        valueLp.topMargin = dp(2);
        card.addView(value, valueLp);
        return card;
    }

    private TextView bigButton(String label, int bg, int fg, int height, int textSize) {
        TextView button = text(label, textSize, fg, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setAllCaps(false);
        button.setBackground(roundRect(bg, dp(22), bg, 0));
        button.setElevation(dp(4));
        button.setMinHeight(height);
        button.setPadding(dp(16), 0, dp(16), 0);
        button.setClickable(true);
        button.setFocusable(true);
        return button;
    }

    private TextView outlinedButton(String label, int stroke, int fg, int height, int textSize) {
        TextView button = text(label, textSize, fg, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setBackground(roundRect(CARD, dp(22), stroke, dp(2)));
        button.setMinHeight(height);
        button.setClickable(true);
        button.setFocusable(true);
        return button;
    }

    private TextView smallButton(String label) {
        TextView button = text(label, 18, WHITE, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setBackground(roundRect(CARD_2, dp(16), Color.rgb(62, 62, 62), dp(1)));
        button.setClickable(true);
        button.setFocusable(true);
        return button;
    }

    private void confirmReset() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Reset workout?")
                .setMessage("Sets and reps will both go back to zero.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Reset", (d, which) -> {
                    sets = 0;
                    reps = 0;
                    saveAndRefresh("Workout reset");
                })
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(RED));
        dialog.show();
    }

    private void saveAndRefresh(String status) {
        prefs.edit().putInt(KEY_SETS, sets).putInt(KEY_REPS, reps).apply();
        refresh();
        statusText.setText(status);
    }

    private void refresh() {
        if (setsValue != null) setsValue.setText(String.valueOf(sets));
        if (repsValue != null) repsValue.setText(String.valueOf(reps));
    }

    private void haptic(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
    }

    private TextView text(String value, int sizeSp, int color, int style) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(sizeSp);
        t.setTextColor(color);
        t.setTypeface(Typeface.create("sans", style));
        return t;
    }

    private Space space(int heightDp) {
        Space s = new Space(this);
        s.setLayoutParams(new LinearLayout.LayoutParams(1, dp(heightDp)));
        return s;
    }

    private LinearLayout.LayoutParams fullWrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private GradientDrawable roundRect(int fill, int radius, int strokeColor, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(radius);
        if (strokeWidth > 0) drawable.setStroke(strokeWidth, strokeColor);
        return drawable;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
