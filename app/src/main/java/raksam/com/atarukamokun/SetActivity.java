package raksam.com.atarukamokun;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioGroup;

import java.util.HashMap;

public class SetActivity extends Activity {

    private Common common; // グローバル変数を扱うクラス

    //ピッカー配列の生成
    final int[] pickerArray = {R.id.num3Pick, R.id.num4Pick, R.id.minilotoPick, R.id.loto6Pick, R.id.loto7Pick};

    //ピッカーの中身の２次元配列
    final int[][] pickerContentArray = {
            {R.id.num3_1, R.id.num3_2, R.id.num3_3},
            {R.id.num4_1, R.id.num4_2, R.id.num4_3, R.id.num4_4},
            {R.id.mini_1, R.id.mini_2, R.id.mini_3, R.id.mini_4},
            {R.id.loto6_1, R.id.loto6_2, R.id.loto6_3, R.id.loto6_4, R.id.loto6_5},
            {R.id.loto7_1, R.id.loto7_2, R.id.loto7_3, R.id.loto7_4, R.id.loto7_5, R.id.loto7_6}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        //共通クラス取得
        common = (Common) getApplication();

        //前回最終で表示されたピッカー群を表示
        View picker = findViewById(radio2picker(common.segChecked));
        picker.setVisibility(View.VISIBLE);

        //セグメントコントロール
        RadioGroup segGroup = (RadioGroup)findViewById(R.id.pickerSegment);
        segGroup.check(common.segChecked); //commonに格納されている情報でセグメントコントロールをチェック状態に
        segGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //ラジオボタンとピッカーを紐付け
                int checkedPickerId = radio2picker(checkedId);

                //選択されたピッカーだけ表示してあとは非表示
                for (int i = 0; i < pickerArray.length; i++) {
                    View picker = findViewById(pickerArray[i]);
                    if (pickerArray[i] == checkedPickerId) {
                        picker.setVisibility(View.VISIBLE);
                    } else {
                        picker.setVisibility(View.GONE);
                    }
                }

                //共通クラスにチェックされてるIDを格納
                common.segChecked = checkedId;
            }
        });

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

    //ラジオボタンと表示するピッカー群を紐付け
    private int radio2picker(int checkedId) {
        int returnValue;
        switch (checkedId) {
            case R.id.num3SegButton:
                returnValue = R.id.num3Pick;
                break;
            case R.id.num4SegButton:
                returnValue = R.id.num4Pick;
                break;
            case R.id.minilotoSegButton:
                returnValue = R.id.minilotoPick;
                break;
            case R.id.loto6SegButton:
                returnValue = R.id.loto6Pick;
                break;
            default:
                returnValue = R.id.loto7Pick;
                break;
        }
        return returnValue;
    }
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//
//    }
}
