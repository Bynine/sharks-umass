package sharks_umass.scanit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ResultsViewActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView titleView, descriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_view);
        imageView = (ImageView) findViewById(R.id.imageView);
        titleView = (TextView) findViewById(R.id.titleView);
        descriptionView = (TextView) findViewById(R.id.descriptionView);
        Intent shared = getIntent();
        titleView.setText(shared.getStringExtra("title"));
        descriptionView.setText(shared.getStringExtra("description"));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
