package raksam.com.atarukamokun;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SetActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        Button returnButton = (Button)findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //メイン画面に戻る
                finish();
            }
        });
    }
}
