package com.kingdomunderground.repsettracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
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
    private boolean compactMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BLACK);
        getWindow().setNavigationBarColor(BLACK);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        sets = prefs.getInt(KEY_SETS, 0);
        reps = prefs.getInt(KEY_REPS, 0);

        compactMode = isCompactDisplay();
        if (compactMode) enableCompactFullscreen();

        setContentView(compactMode ? buildCompactUi() : buildFullUi());
        refresh();
    }

    private View buildFullUi() {
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

    private View buildCompactUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(10), dp(10), dp(10), dp(10));
        root.setBackgroundColor(BLACK);
        root.setFitsSystemWindows(true);

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.setWeightSum(2f);

        LinearLayout setsCard = compactStatCard("SETS");
        setsValue = (TextView) setsCard.getChildAt(1);
        LinearLayout repsCard = compactStatCard("REPS");
        repsValue = (TextView) repsCard.getChildAt(1);

        LinearLayout.LayoutParams leftStat = new LinearLayout.LayoutParams(0, dp(80), 1f);
        leftStat.rightMargin = dp(5);
        stats.addView(setsCard, leftStat);

        LinearLayout.LayoutParams rightStat = new LinearLayout.LayoutParams(0, dp(80), 1f);
        rightStat.leftMargin = dp(5);
        stats.addView(repsCard, rightStat);
        root.addView(stats, fullWrap());

        root.addView(space(8));

        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setWeightSum(2f);

        TextView setButton = bigButton("+  SET", RED_DARK, WHITE, dp(84), 20);
        setButton.setOnClickListener(v -> {
            sets++;
            saveAndRefresh("Set +1");
            haptic(v);
        });
        LinearLayout.LayoutParams setLp = new LinearLayout.LayoutParams(0, dp(84), 1f);
        setLp.rightMargin = dp(5);
        topRow.addView(setButton, setLp);

        TextView repButton = bigButton("+  REP", RED, WHITE, dp(84), 20);
        repButton.setOnClickListener(v -> {
            reps++;
            saveAndRefresh("Rep +1");
            haptic(v);
        });
        LinearLayout.LayoutParams repLp = new LinearLayout.LayoutParams(0, dp(84), 1f);
        repLp.leftMargin = dp(5);
        topRow.addView(repButton, repLp);
        root.addView(topRow, fullWrap());

        root.addView(space(8));

        TextView completeButton = outlinedButton("COMPLETE SET", RED, WHITE, dp(72), 20);
        completeButton.setOnClickListener(v -> {
            sets++;
            reps = 0;
            saveAndRefresh("Set logged • reps reset");
            haptic(v);
        });
        root.addView(completeButton, fullWrap());

        root.addView(space(8));

        LinearLayout bottom = new LinearLayout(this);
        bottom.setOrientation(LinearLayout.HORIZONTAL);
        bottom.setWeightSum(3.1f);

        TextView minusRep = compactSmallButton("− REP");
        minusRep.setOnClickListener(v -> {
            if (reps > 0) reps--;
            saveAndRefresh("Rep −1");
            haptic(v);
        });
        LinearLayout.LayoutParams minusRepLp = new LinearLayout.LayoutParams(0, dp(48), 1f);
        minusRepLp.rightMargin = dp(4);
        bottom.addView(minusRep, minusRepLp);

        TextView minusSet = compactSmallButton("− SET");
        minusSet.setOnClickListener(v -> {
            if (sets > 0) sets--;
            saveAndRefresh("Set −1");
            haptic(v);
        });
        LinearLayout.LayoutParams minusSetLp = new LinearLayout.LayoutParams(0, dp(48), 1f);
        minusSetLp.leftMargin = dp(4);
        minusSetLp.rightMargin = dp(4);
        bottom.addView(minusSet, minusSetLp);

        TextView reset = text("RESET", 12, RED, Typeface.BOLD);
        reset.setGravity(Gravity.CENTER);
        reset.setBackground(roundRect(CARD, dp(14), Color.rgb(53, 53, 53), dp(1)));
        reset.setClickable(true);
        reset.setFocusable(true);
        reset.setOnClickListener(v -> confirmReset());
        LinearLayout.LayoutParams resetLp = new LinearLayout.LayoutParams(0, dp(48), 1.1f);
        resetLp.leftMargin = dp(4);
        bottom.addView(reset, resetLp);

        root.addView(bottom, fullWrap());
        return root;
    }

    private LinearLayout compactStatCard(String label) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER);
        card.setBackground(roundRect(CARD, dp(16), Color.rgb(44, 44, 44), dp(1)));

        TextView labelView = text(label, 13, MUTED, Typeface.BOLD);
        labelView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        labelLp.rightMargin = dp(8);
        card.addView(labelView, labelLp);

        TextView value = text("0", 44, WHITE, Typeface.BOLD);
        value.setGravity(Gravity.CENTER);
        card.addView(value, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return card;
    }

    private TextView compactSmallButton(String label) {
        TextView button = text(label, 13, WHITE, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setBackground(roundRect(CARD_2, dp(14), Color.rgb(58, 58, 58), dp(1)));
        button.setClickable(true);
        button.setFocusable(true);
        return button;
    }

    private boolean isCompactDisplay() {
        float density = getResources().getDisplayMetrics().density;
        float widthDp = getResources().getDisplayMetrics().widthPixels / density;
        float heightDp = getResources().getDisplayMetrics().heightPixels / density;
        float minDp = Math.min(widthDp, heightDp);
        float maxDp = Math.max(widthDp, heightDp);
        float ratio = maxDp / Math.max(1f, minDp);

        // Razr-style cover screens are close to square. The open phone is much taller.
        return ratio < 1.45f && maxDp < 700f;
    }

    private void enableCompactFullscreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decor = getWindow().getDecorView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            decor.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
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
        if (statusText != null) statusText.setText(status);
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
