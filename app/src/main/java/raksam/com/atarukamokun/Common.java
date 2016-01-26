/**
 * グローバル変数を扱うクラス
 * Created by hyosang813 on 16/01/12.
 */
package raksam.com.atarukamokun;

import android.app.Application;
import android.graphics.Point;

public class Common extends Application {

    // グローバルに扱う変数
    Point windowSize; //画面サイズ(x,y)
    int segChecked; //どのセグメントコントロールがタップされていたかを格納
    int pickerContents[][] = {
        {0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0}
    }; //各ピッカービューの選択値
    boolean firstBootFlg;

    // 変数を初期化する
    public void init(){
        windowSize = new Point();
        segChecked = R.id.num3SegButton; //初期値はnum3
        firstBootFlg = false; //一度
    }

}
