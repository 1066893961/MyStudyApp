package application.app.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("StringFormatMatches")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView)findViewById(R.id.textview);

//        textView.setText(Html.fromHtml("<font color=\"#ff0000\">红色</font>其它颜色"));
//        textView.setText(getResources().getString(R.string.str_text1,"张里","张里"));
        textView.setText(Html.fromHtml(getResources().getString(R.string.distribution_tip,"张翠山","张翠山")));
    }
}
