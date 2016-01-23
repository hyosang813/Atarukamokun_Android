package raksam.com.atarukamokun;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioGroup;

import android.util.Log;

public class SetActivity extends Activity {

    private Common common; // グローバル変数を扱うクラス

    //ピッカー配列の生成
    private final int[] pickerArray = {R.id.num3Pick, R.id.num4Pick, R.id.minilotoPick, R.id.loto6Pick, R.id.loto7Pick};

    //ピッカーの中身の２次元配列
    private final int[][] pickerContentArray = {
            {R.id.num3_1, R.id.num3_2, R.id.num3_3},
            {R.id.num4_1, R.id.num4_2, R.id.num4_3, R.id.num4_4},
            {R.id.mini_1, R.id.mini_2, R.id.mini_3, R.id.mini_4},
            {R.id.loto6_1, R.id.loto6_2, R.id.loto6_3, R.id.loto6_4, R.id.loto6_5},
            {R.id.loto7_1, R.id.loto7_2, R.id.loto7_3, R.id.loto7_4, R.id.loto7_5, R.id.loto7_6}
    };

    //ピッカーに表示する文字列配列
    private final String[] numbersStrings = new String[] { "ANY", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
    private final String[] minilotoStrings = new String[] {"ANY",
            "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
            "31"};
    private final String[] loto6Strings = new String[] {"ANY",
            "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
            "31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
            "41", "42", "43"};
    private final String[] loto7Strings = new String[] {"ANY",
            "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
            "31", "32", "33", "34", "35", "36", "37"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        //共通クラス取得
        common = (Common) getApplication();

        //各ピッカーの数列生成と表示
        for (int i = 0; i < pickerContentArray.length; i++) {
            for (int j = 0; j < pickerContentArray[i].length; j++) {
                int maxVal;
                String[] pickerStrings;
                switch (i) {
                    case 0:
                    case 1:
                        maxVal = numbersStrings.length - 1;
                        pickerStrings = numbersStrings;
                        break;
                    case 2:
                        maxVal = minilotoStrings.length - 1;
                        pickerStrings = minilotoStrings;
                        break;
                    case 3:
                        maxVal = loto6Strings.length - 1;
                        pickerStrings = loto6Strings;
                        break;
                    default:
                        maxVal = loto7Strings.length - 1;
                        pickerStrings = loto7Strings;
                        break;
                }
                NumberPicker numberPicker = (NumberPicker)findViewById(pickerContentArray[i][j]);
                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(maxVal);
                numberPicker.setDisplayedValues(pickerStrings);
                numberPicker.setFocusable(true);
                numberPicker.setFocusableInTouchMode(true);
                numberPicker.setValue(common.pickerContents[i][j]);
            }
        }

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

                //各ピッカービューの選択値を共通クラスに格納
                for (int i = 0; i < pickerContentArray.length; i++) {
                    for (int j = 0; j < pickerContentArray[i].length; j++) {
                        NumberPicker numberPicker = (NumberPicker)findViewById(pickerContentArray[i][j]);
                        common.pickerContents[i][j] = numberPicker.getValue();
                    }
                }

                //メイン画面に戻る
                finish();
            }
        });

        //リセットボタン
        final Button resetButton = (Button)findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //表示されているセグメントコントロールに紐づくピッカー群を全てANYに戻す
                RadioGroup segGroup = (RadioGroup)findViewById(R.id.pickerSegment);
                int[] resetPicker = pickerContentArray[segGroup.indexOfChild(findViewById(segGroup.getCheckedRadioButtonId()))];
                for (int i = 0; i < resetPicker.length; i++) {
                    NumberPicker numberPicker = (NumberPicker)findViewById(resetPicker[i]);
                    numberPicker.setValue(0);
                }
            }
        });
    }

    //ラジオボタンと表示するピッカー群を紐付け
    private int radio2picker(int checkedId) {
        switch (checkedId) {
            case R.id.num3SegButton: return R.id.num3Pick;
            case R.id.num4SegButton: return R.id.num4Pick;
            case R.id.minilotoSegButton: return R.id.minilotoPick;
            case R.id.loto6SegButton: return R.id.loto6Pick;
            default: return R.id.loto7Pick;
        }
    }
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//
//    }
}
