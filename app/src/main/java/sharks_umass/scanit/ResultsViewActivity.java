package sharks_umass.scanit;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class ResultsViewActivity extends AppCompatActivity {

    private TextView titleView, descriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_view);
//        titleView = (TextView) findViewById(R.id.titleView);
//        descriptionView = (TextView) findViewById(R.id.descriptionView);
//        titleView.setText(getIntent().getStringExtra("title"));
//        descriptionView.setText(getIntent().getStringExtra("description"));
    }

}
