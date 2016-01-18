/**
 * グローバル変数を扱うクラス
 * Created by hyosang813 on 16/01/12.
 */
package raksam.com.atarukamokun;

import android.app.Application;
import android.graphics.Point;

public class Common extends Application {

    // グローバルに扱う変数
    Point windowSize;        //画面サイズ(x,y)

    // 変数を初期化する
    public void init(){
        windowSize = new Point();
    }

}
