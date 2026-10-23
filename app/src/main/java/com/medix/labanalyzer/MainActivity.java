
package com.medix.labanalyzer;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends Activity {

    private Spinner testSpinner;
    private EditText resultInput;
    private TextView reportText;

    private final String[] testNames = {
        "السكر الصائم (GLUCOSE)",
        "الهيموغلوبين (HB)",
        "كريات الدم البيضاء (WBC)",
        "الكرياتينين (CREATININE)"
    };

    private final double[] minimum = {70, 12, 4000, 0.6};
    private final double[] maximum = {99, 16, 11000, 1.2};

    private final String[] units = {
        "mg/dL", "g/dL", "/uL", "mg/dL"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createInterface();
    }

    private void createInterface() {

        int backgroundColor = Color.rgb(245, 248, 250);
        int blueColor = Color.rgb(20, 100, 180);
        int darkColor = Color.rgb(30, 45, 60);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(28, 30, 28, 30);
        layout.setBackgroundColor(backgroundColor);
        layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);

        TextView title = new TextView(this);
        title.setText("Medix");
        title.setTextSize(34);
        title.setTextColor(blueColor);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView subtitle = new TextView(this);
        subtitle.setText("قارئ التحاليل الطبية");
        subtitle.setTextSize(20);
        subtitle.setTextColor(darkColor);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 8, 0, 28);

        TextView selectLabel = new TextView(this);
        selectLabel.setText("اختاري نوع التحليل:");
        selectLabel.setTextSize(17);
        selectLabel.setTextColor(darkColor);

        testSpinner = new Spinner(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            testNames
        );

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        );

        testSpinner.setAdapter(adapter);

        TextView valueLabel = new TextView(this);
        valueLabel.setText("أدخلي نتيجة التحليل:");
        valueLabel.setTextSize(17);
        valueLabel.setTextColor(darkColor);
        valueLabel.setPadding(0, 22, 0, 5);

        resultInput = new EditText(this);
        resultInput.setHint("مثال: 95 أو 12.5");
        resultInput.setTextSize(18);
        resultInput.setSingleLine(true);
        resultInput.setInputType(
            android.text.InputType.TYPE_CLASS_NUMBER
            | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        Button analyzeButton = new Button(this);
        analyzeButton.setText("تحليل النتيجة");
        analyzeButton.setTextSize(18);
        analyzeButton.setTextColor(Color.WHITE);
        analyzeButton.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(blueColor)
        );

        reportText = new TextView(this);
        reportText.setText("ستظهر نتيجة التحليل هنا.");
        reportText.setTextSize(17);
        reportText.setTextColor(darkColor);
        reportText.setPadding(18, 22, 18, 22);
        reportText.setBackgroundColor(Color.WHITE);
        reportText.setGravity(Gravity.RIGHT);
        reportText.setTextDirection(View.TEXT_DIRECTION_RTL);

        TextView warning = new TextView(this);
        warning.setText(
            "تنبيه: النتائج إرشادية فقط، ويجب تفسيرها " +
            "حسب مرجع المختبر وبواسطة مختص."
        );
        warning.setTextSize(13);
        warning.setTextColor(Color.DKGRAY);
        warning.setPadding(0, 22, 0, 10);
        warning.setGravity(Gravity.CENTER);

        layout.addView(title, matchWidth());
        layout.addView(subtitle, matchWidth());
        layout.addView(selectLabel, matchWidth());

        layout.addView(testSpinner, matchWidth());
        layout.addView(valueLabel, matchWidth());
        layout.addView(resultInput, matchWidth());

        LinearLayout.LayoutParams buttonParams = matchWidth();
        buttonParams.setMargins(0, 20, 0, 20);
        layout.addView(analyzeButton, buttonParams);

        layout.addView(reportText, matchWidth());
        layout.addView(warning, matchWidth());

        scrollView.addView(layout);
        setContentView(scrollView);

        analyzeButton.setOnClickListener(v -> analyzeResult());
    }

    private LinearLayout.LayoutParams matchWidth() {
        return new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private void analyzeResult() {

        String input = resultInput.getText().toString().trim();

        if (input.isEmpty()) {
            resultInput.setError("أدخلي نتيجة التحليل أولاً");
            return;
        }

        input = input.replace(',', '.');

        double value;

        try {
            value = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            Toast.makeText(
                this,
                "أدخلي رقماً صحيحاً",
                Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (Double.isNaN(value)
                || Double.isInfinite(value)
                || value < 0) {
            resultInput.setError("أدخلي قيمة موجبة أو صفراً");
            return;
        }

        int index = testSpinner.getSelectedItemPosition();

        String status;

        if (value < minimum[index]) {
            status = "أقل من المدى المرجعي";
        } else if (value > maximum[index]) {
            status = "أعلى من المدى المرجعي";
        } else {
            status = "ضمن المدى المرجعي";
        }

        String report =
            "تقرير التحليل الطبي\n\n"
            + "اسم الفحص: " + testNames[index] + "\n\n"
            + "النتيجة: "
            + String.format(Locale.US, "%s", value)
            + " " + units[index] + "\n\n"
            + "المدى المرجعي المستخدم: "
            + minimum[index] + " - "
            + maximum[index] + " " + units[index]
            + "\n\n"
            + "التصنيف: " + status;

        reportText.setText(report);
    }
}
