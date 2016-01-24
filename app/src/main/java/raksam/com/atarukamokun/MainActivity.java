package raksam.com.atarukamokun;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;
import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    //********************************************************************
    //インスタンス変数セクション

    private Common common; // グローバル変数を扱うクラス
    private int resultLabelList[]; // 結果表示ラベルのArray
    private boolean animeSwitch = false; //アニメーション中か否かの判定用
    private int buttonCount = 0; //ボタンが押されて何回目か？
    private int buttonKind = 0; //押されたボタンのID情報格納
    private Handler handle = new Handler(); //TimerをUIスレッドで使えるようにするためのハンドラ
    private ScrollAnime scrollAnime = null; //左右アニメーション用のスケジュールタスク
    private HighRotateAnime highRotateAnime = null; //高速回転用タイマー用のスケジュールタスク
    private BlinkAnime blinkAnime = null; //点滅表示用タイマー用のスケジュールタスク(ナンバーズ)
    private SequentialDisplayLoto sequentialDisplayLoto = null; //ロト順次表示用タイマー用のスケジュールタスク
    private Timer scrollTm = null; //左右アニメーション用タイマー
    private Timer highTm = null; //高速回転用タイマー
    private Timer blinkTm = null; //点滅表示用タイマー
    private Timer sequentialDisplayTm = null; //ロト順次表示用タイマー
    private int revolCount = 0; //高速回転開始後にボタン操作可能にするためのカウント
    private int animationCount = 0; //左右アニメーションカウンター
    private ArrayList<String> startArray = new ArrayList<>(); //左右アニメーション乱数配列
    private ArrayList<String> tempStartArray = new ArrayList<>(); //左右アニメーションをなめらか表示するための乱数配列
    private ArrayList<String> endArray = new ArrayList<>(); //最終表示用の配列
    private boolean blinkSwitch = true; //点滅状態を作り出すためのスイッチ
    private int orderCount = 0; //ロト系順次表示アニメーション用のカウンタ変数



    //インスタンス変数セクション
    //********************************************************************

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //commonインスタンスがなければ作る(シングルトン)
        if (common == null) {
            // グローバル変数を扱うクラスを取得する
            common = (Common) getApplication();
        }

        //commonのWindowSizeがnullだったら画面サイズ取得
        if (common.windowSize == null) {
            // グローバル変数を扱うクラスを初期化する
            common.init();

            // 画面サイズを取得してグローバル変数に格納
            Display display = getWindowManager().getDefaultDisplay();
            Point windowSize = new Point();
            display.getSize(windowSize);
            common.windowSize = windowSize;
        }

        //ボタンタップイベントをリスナー登録
        int buttonList[] = {R.id.num3Button, R.id.num4Button, R.id.miniLotoButton, R.id.loto6Button, R.id.loto7Button};
        for (int buttonID: buttonList) {
            ImageButton btn = (ImageButton)findViewById(buttonID);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //心臓部メソッドをボタンIDを引数にコール
                    push(v.getId());
                }
            });
        }

        //設定画面ボタン
        ImageButton setButton = (ImageButton)findViewById(R.id.setButton);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SetActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //画面サイズに応じて土台resultViewのサイズを指定
        TextView resultView = (TextView)findViewById(R.id.resultView);
        resultView.setWidth((int) ((double) common.windowSize.x / 1.7));
        resultView.setHeight(common.windowSize.y / 7);

        //画面サイズに応じて「あ」「た」「る」「かも」「クン」のサイズ指定
        int ataruList[] = {R.id.viewA, R.id.viewTA, R.id.viewRU, R.id.viewKAMO, R.id.viewKUN};
        for(int i = 0; i < ataruList.length; i++) {
            TextView ataruView = (TextView)findViewById(ataruList[i]);
            ataruView.setWidth(common.windowSize.x / 10);
            ataruView.setHeight(common.windowSize.y / 6);
            if (i < 3) {
                ataruView.setTextSize(common.windowSize.x / 40);
            } else {
                ataruView.setTextSize(common.windowSize.x / 80);
            }
        }

        //画面サイズに応じて黒点のサイズを指定
        int blackPointList[] = {R.id.BP1, R.id.BP2, R.id.BP3, R.id.BP4, R.id.BP5};
        for(int bpID: blackPointList) {
            TextView ataruView = (TextView)findViewById(bpID);
            ataruView.setWidth(common.windowSize.x / 6);
            ataruView.setHeight(common.windowSize.y / 10);
            ataruView.setTextSize(common.windowSize.x / 50);
            ataruView.setTextColor(Color.BLACK);
        }

        //画面サイズに応じて７つの結果表示ラベルのサイズを指定
        resultLabelList = new int[]{R.id.resultLabel1, R.id.resultLabel2, R.id.resultLabel3, R.id.resultLabel4, R.id.resultLabel5, R.id.resultLabel6, R.id.resultLabel7};

        //土台のresultViewの横幅と縦幅をゲット
        int resultWidth = (((int) ((double) common.windowSize.x / 1.7) / 7)) - 4; //-4はあくまでNexsus5のエミュレーターで目視確認した値！！！！！！！！！！！
        int resultHeight = (common.windowSize.y / 7) - 20; //-20はあくまでNexsus5のエミュレーターで目視確認した値！！！！！！！！！！！

        for (int labelID: resultLabelList) {
            TextView resultLabel = (TextView)findViewById(labelID);
            resultLabel.setWidth(resultWidth);
            resultLabel.setHeight(resultHeight);
            resultLabel.setTextColor(Color.BLACK);
            resultLabel.setTypeface(Typeface.createFromAsset(getAssets(), "7barSPBd.otf")); //外部デジタルフォント指定
            resultLabel.setTextSize(resultWidth / 4); //フォントサイズの調整
            /***
             *とりあえずnexsus5のエミュレータだとwidth/4がちょうど良いサイズになるけど他はどうだろうナーーーーーーーー
             */
        }
    }

    //起動時は表示されていない枠を表示
    private void drawLabelFrame() {

        for (int labelID: resultLabelList) {
            TextView resultLabel = (TextView)findViewById(labelID);
            //縁取り
            GradientDrawable gd = new GradientDrawable();
            gd.setStroke(2, Color.BLACK);
            resultLabel.setBackground(gd);

            /***
             * 以降で下部中央をあける処理と重なる部分が2ptの線幅になる問題の処理が必要！！！！！！！！！！！
             */
        }
    }

    //ボタン押下時にコールされる心臓部メソッド
    private void push(int buttonID) {

        //animeSwitchがtrue(アニメーション中)は以降の処理を全スキップ
        if (animeSwitch) return;

        //ボタンが何回目に押されているかによって処理のディスパッチ
        switch (buttonCount) {
            case 0: //表示窓枠を描画し左右&高速アニメーション　※設定ボタンは選択不可能状態に非活性化 NENDはインスタンス破棄して非表示
                /***
                 *NENDの破棄と非表示を行う！！！！！！！
                 */

                //別スレッドでタイマー回しまくってるからタイミングがずれるっぽいのでここで色々初期化

                revolCount = 0; //revolCountの初期化
                blinkSwitch = true; //点滅状態を作り出すためのスイッチ
                animeSwitch = true; //左右アニメーション中はボタン押しても反応しないよー

                /***
                 *設定ボタン系を非選択にする！！！！！！
                 * setButton.enabled = false みたいな
                 * setButton.enabled = false みたいな
                 */


                drawLabelFrame(); //結果表示ラベル自体を表示
                firstPress(buttonID); //１回目のボタン押下では左右アニメーション開始
                break;
            case 1: //ボタンカウント1の時はラベルを点滅表示　NENDは生成して非表示状態で待機
                /***
                 *NENDの準備
                 */

                secondPress(); //２回目のボタン押下で各数字を確定させて点滅表示
                break;
            case 2: //ボタンカウント2の時はラベルの点滅終了　※選択不可状態の設定ボタンを活性化　NENDは表示
                /***
                 *NENDの表示と設定系ボタンの活性化
                 */

                blinkStop();
                break;
        }
    }

    //初回ボタン操作時は左右アニメーションからの高速回転まで
    private void firstPress(int buttonID) {
        //押されたボタンのIDを保存
        buttonKind = buttonID;

        //左右アニメーションタイマーがもし動いていたら一旦ストップ
        if (scrollTm != null) {
            scrollTm.cancel();
            scrollTm = null;
            scrollAnime = null;
        }

        //左右アニメーションタイマーインスタンス生成
        scrollTm = new Timer();

        //左右アニメーションタイマータスクインスタンスを作成
        scrollAnime = new ScrollAnime();

        //左右アニメーションタイマースケジュールを遅延なし(0)で130ミリ秒(0.13秒)間隔で実行
        scrollTm.schedule(scrollAnime, 0, 130);
    }

    //2回目にボタンを押されたら数字を確定させて点滅表示
    private void secondPress() {
        //高速回転タイマーをストップ
        highTm.cancel();
        highTm = null;
        highRotateAnime = null;

        //ナンバーズ系共通処理 ナンバーズは高速回転の最後の値を使用
        switch (buttonKind) {
            case R.id.num3Button:
            case R.id.num4Button:

                //endArrayに入れ直してナンバーズ3なら最後の要素を削除 ※単純な代入だと参照渡し(シャローコピー)になっちまうからaddAllで値渡し(ディープコピー)する
                endArray.addAll(startArray);
                int bKind = 1;
                if (buttonKind == R.id.num3Button) {
                    endArray.remove(endArray.size() - 1);
                    bKind = 0;
                }

                //設定画面の情報に置き換え
                for (int i = 0; i < endArray.size(); i++ ) {
                    if (common.pickerContents[bKind][i] != 0) {
                        endArray.set(i, String.valueOf(common.pickerContents[bKind][i] - 1));
                    }
                }

                blinkTm = new Timer(); //点滅表示タイマーインスタンス生成
                blinkAnime = new BlinkAnime(); //点滅表示タイマータスクインスタンスを作成
                blinkTm.schedule(blinkAnime, 0, 500); //点滅表示タイマースケジュールを遅延なし(0)で500ミリ秒(0.5秒)間隔で実行

                //数字点滅状態を表す
                buttonCount = 2;

                break;
            default:
                //範囲指定変数
                int range;
                int kind;
                int blKind;

                //くじ別範囲設定
                switch (buttonKind) {
                    case R.id.miniLotoButton:
                        range = 31;
                        kind = 5;
                        blKind = 2;
                        break;
                    case R.id.loto6Button:
                        range = 43;
                        kind = 6;
                        blKind = 3;
                        break;
                    default:
                        range = 37;
                        kind = 7;
                        blKind = 4;
                        break;
                }

                //まずは重複がない前提の設定画面で選択された値をendArrayに格納
                for (int i = 0; i < common.pickerContents[blKind].length; i++) {
                    if (common.pickerContents[blKind][i] != 0) {
                        endArray.add(String.format("%02d", common.pickerContents[blKind][i]));
                    }
                }

                //重複を排除しつつ乱数生成
                for (int i = endArray.size(); i < kind; i++) {
                    String addStr = String.format("%02d", new Random().nextInt(range) + 1);
                    if (endArray.contains(addStr)) {
                        i--;
                        continue;
                    }
                    endArray.add(addStr);
                }

                //ソート
                Collections.sort(endArray);

                //順次表示アニメーション中はまたボタン操作を無効にする
                animeSwitch = true;

                sequentialDisplayTm = new Timer(); //点滅表示タイマーインスタンス生成
                sequentialDisplayLoto = new SequentialDisplayLoto(); //点滅表示タイマータスクインスタンスを作成
                sequentialDisplayTm.schedule(sequentialDisplayLoto, 0, 100); //点滅表示タイマースケジュールを遅延なし(0)で100ミリ秒(0.5秒)間隔で実行

                break;
        }
    }

    //３回目にボタンを押されたら点滅表示を終了して広告表示
    private void blinkStop() {
        //点滅表示タイマーを停止
        blinkTm.cancel();
        blinkTm = null;
        blinkAnime = null;

        //点滅終了
        blinkSwitch = true;
        labelBlink();

        //次に備えて各ArrayListを初期化
        startArray.clear();
        tempStartArray.clear();
        endArray.clear();

        //buttonCount初期化
        buttonCount = 0;


        /***
         *結果保存用の処理がここに入る
         */

    }

    // 左右アニメーションタイマータスク用のインナークラス　※MainActivity内に定義されているよ！！！
    class ScrollAnime extends TimerTask {
        @Override
        public void run() {
            handle.post( new Runnable() {
                public void run() {
                    //ボタン押下時にアニメーションする２桁数字×７を生成(タイマー処理初回時のみ実施)
                    if (animationCount == 0) {
                        //結果表示ラベルを中央寄せにする
                        labelAlignmentAdjust(0);

                        //左右アニメーション用の適当数値文字列の生成
                        for (int i = 0; i < resultLabelList.length; i++) {
                            //[01]〜[99]の乱数生成
                            startArray.add(String.format("%02d", new Random().nextInt(99) + 1));

                            //なめらか表現用のArray生成 //最初の要素は２桁目だけで、あとは左側の１桁目と右側の２桁目をガッチャンコ
                            if (i == 0) {
                                tempStartArray.add(startArray.get(0).substring(0, 1));
                            } else {
                                String s1 = startArray.get(i - 1).substring(1, 2);
                                String s2 = startArray.get(i).substring(0, 1);
                                tempStartArray.add(s1 + s2);
                            }
                        }
                    }

                    //左右アニメーション
                    switch (animationCount) {
                        case 0: //最初はstartArrayの情報をそのまま表示
                        case 28:
                            scrollAnimationDisplay(startArray, 0);
                            break;
                        case 1: //次はなめらかArrayの情報をそのまま表示
                        case 27:
                            scrollAnimationDisplay(tempStartArray, 0);
                            break;
                        case 2:
                        case 26:
                            scrollAnimationDisplay(startArray, 1);
                            break;
                        case 3:
                        case 25:
                            scrollAnimationDisplay(tempStartArray, 1);
                            break;
                        case 4:
                        case 24:
                            scrollAnimationDisplay(startArray, 2);
                            break;
                        case 5:
                        case 23:
                            scrollAnimationDisplay(tempStartArray, 2);
                            break;
                        case 6:
                        case 22:
                            scrollAnimationDisplay(startArray, 3);
                            break;
                        case 7:
                        case 21:
                            scrollAnimationDisplay(tempStartArray, 3);
                            break;
                        case 8:
                        case 20:
                            scrollAnimationDisplay(startArray, 4);
                            break;
                        case 9:
                        case 19:
                            scrollAnimationDisplay(tempStartArray, 4);
                            break;
                        case 10:
                        case 18:
                            scrollAnimationDisplay(startArray, 5);
                            break;
                        case 11:
                        case 17:
                            scrollAnimationDisplay(tempStartArray, 5);
                            break;
                        case 12:
                        case 16:
                            scrollAnimationDisplay(startArray, 6);
                            break;
                        case 13:
                        case 15:
                            scrollAnimationDisplay(tempStartArray, 6);
                            break;
                        case 14: //全消し
                            scrollAnimationDisplay(tempStartArray,7);
                            break;
                    }

                    //右に消えきって左に出きった数を数える為にカウンターをインクリメント
                    animationCount++;

                    //左右アニメーション中は以下の処理はスキップ
                    if (animationCount <= 30) return;

                    //この時点で次に備えてtempStartArrayを初期化
                    tempStartArray.clear();

                    //左右アニメーションが終了したら左右アニメーションカウンターを初期化
                    animationCount = 0;

                    //左右アニメーション用のタイマーをストップ
                    scrollTm.cancel();
                    scrollTm = null;
                    scrollAnime = null;

                    //ナンバーズ系は結果表示ラベルのAlighnmentをLeftに
                    if (buttonKind == R.id.num3Button || buttonKind == R.id.num4Button) {
                        labelAlignmentAdjust(1);
                    }

                    //0.5秒待ってから高速回転用タイマーをスタート
                    try { Thread.sleep(500); } catch(InterruptedException e) {/* 本当はここにcatchした例外処理が必要 */}

                    //高速回転タイマーインスタンス生成
                    highTm = new Timer();

                    // 高速回転タイマータスクインスタンスを作成
                    highRotateAnime = new HighRotateAnime();

                    // 高速回転タイマースケジュールを遅延なし(0)で100ミリ秒(0.1秒)間隔で実行
                    highTm.schedule(highRotateAnime, 0, 100);
                }
            });
        }
    }

    // 高速回転タイマータスク用のインナークラス　※MainActivity内に定義されているよ！！！
    class HighRotateAnime extends TimerTask {
        @Override
        public void run() {
            handle.post( new Runnable() { //ハンドラーを他のタイマーと使い回ししてるけどいける？？？？？？
                public void run() {
                    //適当な乱数配列を空にする
                    startArray.clear();

                    //ナンバーズ系とロト系で処理ディスパッチ
                    switch (buttonKind) {
                        case R.id.num3Button:
                        case R.id.num4Button:
                            //ナンバーズ系は４つの１桁数値を毎回生成
                            for (int i = 0; i < 4; i++) {
                                startArray.add(String.format("%01d", new Random().nextInt(10)));
                            }
                            //ナンバーズ３の場合と４の場合で分けて表示
                            if (buttonKind == R.id.num3Button) {
                                displayLabel(Arrays.asList(1, 2, 3));
                            } else {
                                displayLabel(Arrays.asList(0, 1, 4, 6));
                            }
                            break;
                        default:
                            //ロト系は７つの２桁数値を毎回生成
                            for (int i = 0; i < 7; i++) {
                                startArray.add(String.format("%02d", new Random().nextInt(100)));
                            }
                            //ミニロトの場合とロト６の場合とロト７の場合で分けて表示
                            switch (buttonKind) {
                                case R.id.miniLotoButton:
                                    displayLabel(Arrays.asList(0, 1, 2, 3, 6));
                                    break;
                                case R.id.loto6Button:
                                    displayLabel(Arrays.asList(0, 1, 2, 4, 5, 6));
                                    break;
                                default:
                                    displayLabel(Arrays.asList(0, 1, 2, 3, 4, 5, 6));
                                    break;
                            }
                            break;
                    }

                    //左右アニメーション終了後はボタン操作を有効にするのでスイッチをoff：false　にしてボタンカウントを１にアップ
                    if (revolCount == 2) {
                        animeSwitch = false;
                        buttonCount = 1;
                    }

                    //ボタン操作可能にするためのカウンターをアップ
                    revolCount++;
                }
            });
        }
    }

    // 点滅表示タイマータスク用のインナークラス(ナンバーズ)　※MainActivity内に定義されているよ！！！
    class BlinkAnime extends TimerTask {
        @Override
        public void run() {
            handle.post( new Runnable() { //ハンドラーを他のタイマーと使い回ししてるけどいける？？？？？？
                public void run() {
                    labelBlink(); //ナンバーズはもう準備できてるからそのまま点滅表示メソッドをタイマーで呼び出す
                }
            });
        }
    }

    // ロト順次表示用タイマータスク用のインナークラス　※MainActivity内に定義されているよ！！！
    class SequentialDisplayLoto extends TimerTask {
        @Override
        public void run() {
            handle.post( new Runnable() { //ハンドラーを他のタイマーと使い回ししてるけどいける？？？？？？
                public void run() {
                    //適当乱数Arrayを空にして再生成
                    startArray.clear();
                    for (int i = 0; i < 7; i++) {
                        startArray.add(String.format("%02d", new Random().nextInt(100)));
                    }

                    //カウンタの数値で表示を切り分ける　※１段階５フレーム
                    if (orderCount < 5) {
                        displayLabelSequential(0);
                    } else if (5 <= orderCount && orderCount < 10) {
                        displayLabelSequential(1);
                    } else if (10 <= orderCount && orderCount < 15) {
                        displayLabelSequential(2);
                    } else if (15 <= orderCount && orderCount < 20) {
                        displayLabelSequential(3);
                    } else if (20 <= orderCount && orderCount < 25) {
                        displayLabelSequential(4);
                    } else if (25 <= orderCount && orderCount < 30) {
                        if (buttonKind == R.id.miniLotoButton) {
                            orderCount = -1;
                        } else {
                            displayLabelSequential(5);
                        }
                    } else if (30 <= orderCount && orderCount < 35) {
                        if (buttonKind == R.id.loto6Button) {
                            orderCount = -1;
                        } else {
                            displayLabelSequential(6);
                        }
                    } else {
                        orderCount = -1;
                    }

                    //カウントアップ
                    orderCount++;

                    //順次表示が終了したら点滅表示処理
                    if (orderCount > 0) return;

                    //順次表示タイマーを停止
                    sequentialDisplayTm.cancel();
                    sequentialDisplayTm = null;
                    sequentialDisplayLoto = null;

                    blinkTm = new Timer(); //点滅表示タイマーインスタンス生成
                    blinkAnime = new BlinkAnime(); //点滅表示タイマータスクインスタンスを作成
                    blinkTm.schedule(blinkAnime, 0, 500); //点滅表示タイマースケジュールを遅延なし(0)で500ミリ秒(0.5秒)間隔で実行

                    //数字点滅状態を表す
                    buttonCount = 2;

                    //順次表示アニメーション終了後にボタン操作を有効にする
                    animeSwitch = false;
                }
            });
        }
    }

    //左右アニメーション表示用メソッド
    private void scrollAnimationDisplay(ArrayList<String> valueArray, int onOff) {
        for (int i = 0; i < resultLabelList.length; i++) {
            TextView resultLabel = (TextView)findViewById(resultLabelList[i]);
            //onOffカウントより小さい間は空文字を入れる
            if (i < onOff) {
                resultLabel.setText("");
            } else {
                resultLabel.setText(valueArray.get(i - onOff));
            }
        }
    }

    //高速回転用ラベル表示メソッド
    private void displayLabel(List<Integer> displayNum) {
        int j = 0;
        for (int i = 0; i < 7; i++) {
            TextView resultLabel = (TextView)findViewById(resultLabelList[i]);
            if (displayNum.contains(i)) { //存在チェック
                resultLabel.setText(startArray.get(j));
                j++;
            } else {
                resultLabel.setText("");
            }
        }
    }

    //順次表示用ラベル表示メソッド
    private void displayLabelSequential(int frameCount) {
        int localFrameCount = frameCount;
        List<Integer> displayNum;
        switch (buttonKind) {
            case R.id.miniLotoButton:
                displayNum = Arrays.asList(0, 1, 2, 3, 6);
                break;
            case R.id.loto6Button:
                displayNum = Arrays.asList(0, 1, 2, 4, 5, 6);
                break;
            default:
                displayNum = Arrays.asList(0, 1, 2, 3, 4, 5, 6);
                break;
        }

        int j = 0;
        for (int i = 0; i < 7; i++) {
            TextView resultLabel = (TextView)findViewById(resultLabelList[i]);
            if (displayNum.contains(i)) { //存在チェック
                if (localFrameCount >= i) {
                    resultLabel.setText(endArray.get(j));
                } else {
                    resultLabel.setText(startArray.get(j));
                }
                j++;
            } else {
                resultLabel.setText("");
                localFrameCount++;
            }
        }
    }

    //結果表示ラベルのAlignment調整メソッド 0:Center 1:Left
    private void labelAlignmentAdjust(int direction) {
        for (int labelID: resultLabelList) {
            TextView resultLabel = (TextView)findViewById(labelID);
            if (direction == 0) {
                resultLabel.setGravity(Gravity.CENTER);
            } else {
                resultLabel.setGravity(Gravity.START | Gravity.CENTER); //LEFTとかRIGHTはデュプリケートなんだって　STARTとENDに置き換え
            }
        }
    }

    //点滅表示メソッド
    private void labelBlink() {
        for (int i = 0; i < resultLabelList.length; i++) {
            TextView resultLabel = (TextView) findViewById(resultLabelList[i]);
            if (!blinkSwitch) {
                resultLabel.setText("");
            } else {
                if (i < endArray.size()) {
                    resultLabel.setText(endArray.get(i));
                } else {
                    resultLabel.setText("");
                }
            }
        }
        //交互に点滅させる
        blinkSwitch = !blinkSwitch;
    }

}
