package raksam.com.atarukamokun;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class SetActivity extends Activity {

    //ピッカー配列の生成
    final int[] pickerArray = {R.id.num3Pick, R.id.num4Pick, R.id.minilotoPick, R.id.loto6Pick, R.id.loto7Pick};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);


        NumberPicker pick = (NumberPicker)findViewById(R.id.num3_1);
        pick.setMinValue(0);
        pick.setMaxValue(20);



        //「戻る」ボタン
        Button returnButton = (Button)findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //メイン画面に戻る
                finish();
            }
        });
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//
//    }
}
