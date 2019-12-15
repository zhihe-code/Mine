package com.example.mine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


import java.util.Random;


public class MineSweepingView extends View {






    Context mContent;

    Point leftTop;//方块左上角的点

    Paint backgroundPaint = new Paint();//背景画笔
    Paint blockPaint = new Paint();     //方块画笔
    Paint minePaint = new Paint();      //炸弹画笔
    Paint numPaint = new Paint();       //数字画笔

    Signal signal;
    boolean finish,start;     //判断游戏结束与开始
    int[][] colors; // 代表每个坐标的颜色，其中0代表银灰色，1代表白色，2代表红色
    int[][] numbers;// 代表每个坐标的数字，其中-1代表雷

    final int width = 100;  //小格子边长
    final int rowCount = 9; //一行格子数
    final int mineCount = 10;//雷的个数
    private Bitmap flagBitmap,mineBitmap;
    private SoundPool soundPool;//音频通知声音播放器
    private int soundID1,soundID2,soundID3,soundID4;//音频资源ID


    //手势操作监听器
    private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return true;  //防止其他事件不执行，所以返回true
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }


//        短按事件
        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            leftTop = findLeftTop(motionEvent.getX(), motionEvent.getY());
            if(numbers[leftTop.x][leftTop.y] == -1){
                PlayMusic(soundID4);
                finish=true;
                if(signal!=null){
                    signal.onFinish();
                }
                MineSweepingView.this.invalidate();
                gameOver();
            }else {
                if (signal != null) {
                    signal.onFlag(mineCount);
                }
                if(signal!=null&&start){
                    signal.onStart();
                    start=false;
                }
                expand(leftTop.x,leftTop.y);
                MineSweepingView.this.invalidate();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            leftTop = findLeftTop(motionEvent.getX(), motionEvent.getY());
            if (colors[leftTop.x][leftTop.y] == 0) {
                colors[leftTop.x][leftTop.y] = 2;
                PlayMusic(soundID2);
                MineSweepingView.this.invalidate();
            }
            else if (colors[leftTop.x][leftTop.y]== 2) {
                colors[leftTop.x][leftTop.y] = 0;
                PlayMusic(soundID2);
                MineSweepingView.this.invalidate();
            }

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

    };

    private GestureDetector detector = new GestureDetector(onGestureListener);

    public MineSweepingView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        mContent = context;
        initSound();
        finish=false;
        start=true;

        flagBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flag);
        mineBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.boom);


        backgroundPaint.setColor(Color.BLACK);
        backgroundPaint.setStrokeWidth(1);

        blockPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        blockPaint.setColor(Color.WHITE);

        minePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        minePaint.setColor(Color.RED);

        numPaint.setColor(Color.BLACK);
        numPaint.setTextAlign(Paint.Align.CENTER);
        numPaint.setTextSize(50);
        numPaint.setStyle(Paint.Style.FILL);

        reset();
    }
    public void setSignal(Signal signal) {
        this.signal = signal;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.GRAY);

        for (int i = 0; i <= getWidth(); i += width) {
            canvas.drawLine(i, 0, i, getWidth(), backgroundPaint);
        }
        for (int j = 0; j <= getHeight(); j += width) {
            canvas.drawLine(0, j, getWidth(), j, backgroundPaint);
        }

        Paint.FontMetrics fontMetrics = numPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;

        int grayCount = 0;
        int redCount = 0;
        for (int x = 0; x < rowCount; x++) {
            for (int y = 0; y < rowCount; y++) {
                switch (colors[x][y]) {//宽高各缩减一单位是为了防止把细线也给覆盖了
                    case 1://白色
                        PlayMusic(soundID1);
                        canvas.drawRect(x * width + 1, y * width + 1, (x + 1) * width - 1, (y + 1) * width - 1, blockPaint);
                        if (numbers[x][y] != -1 && numbers[x][y] != 0) {
                            canvas.drawText(Integer.toString(numbers[x][y]), x * width + 50, y * width + 50 - top / 2 - bottom / 2, numPaint);
                        }
                        else if (numbers[x][y] == -1) {
                            drawMine(canvas,x,y);
//                            canvas.drawRect(x * width + 1, y * width + 1, (x + 1) * width - 1, (y + 1) * width - 1, minePaint);
                        }
                        break;
                    case 2://红色
                        drawFlag(canvas,x,y);
                        redCount++;
                        break;
                    case 0://灰色
                        if(finish&&numbers[x][y] == -1){
                            drawMine(canvas,x,y);
                        }
                        grayCount++;
                        break;
                    default:
                         break;
                }
            }
        }
        if(grayCount == 0 && redCount == 10){
            PlayMusic(soundID3);
            finish=true;
            signal.onFinish();
            gameSuccess();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        使用手势触摸
        return detector.onTouchEvent(event);
    }

    //    根据扫雷逻辑展开方块
//    如果要展开的小方块下为大于零的数字，则展开该方块；
//    如果要展开的小方块下为-1（也就是雷），则直接返回；
//    如果要展开的小方块的数字为零，则展开该方块并将盖方块周围的方块执行expand逻辑.
    private void expand(int x,int y){
        if(numbers[x][y]==-1){
            return;
        }
        else if(numbers[x][y]==0&&colors[x][y]==0){
            colors[x][y]=1;
//            左上
            if(x-1>=0&&y-1>=0){
                expand(x-1,y-1);
            }
//            上
            if(y-1>=0){
                expand(x,y-1);
            }
//            右上
            if(x+1<rowCount&&y-1>=0){
                expand(x+1,y-1);
            }
//            左
            if (x-1>=0){
                expand(x-1,y);
            }
//            右
            if (x+1<rowCount){
                expand(x+1,y);
            }
//            左下
            if (x-1>=0&&y+1<rowCount){
                expand(x-1,y+1);
            }
//            下
            if (y+1<rowCount){
                expand(x,y+1);
            }
//            右下
            if (x+1<rowCount&&y+1<rowCount){
                expand(x+1,y+1);
            }
        }
        else {
            colors[x][y]=1;
        }
    }

//    填充雷附近数字
    private void setnumbers(int x,int y){
//        左上
        if(x-1>=0&&y-1>=0&&numbers[x-1][y-1]!=-1){
            numbers[x-1][y-1]++;
        }
//        上
        if(y-1>=0&&numbers[x][y-1]!=-1){
            numbers[x][y-1]++;
        }
//        右上
        if(x+1<rowCount&&y-1>=0&&numbers[x+1][y-1]!=-1){
            numbers[x+1][y-1]++;
        }
//        左
        if(x-1>=0&&numbers[x-1][y]!=-1){
            numbers[x-1][y]++;
        }
//        右
        if(x+1<rowCount&&numbers[x+1][y]!=-1){
            numbers[x+1][y]++;
        }
//        左下
        if(x-1>=0&&y+1<rowCount&&numbers[x-1][y+1]!=-1){
            numbers[x-1][y+1]++;
        }
//        下
        if(y+1<rowCount&&numbers[x][y+1]!=-1){
            numbers[x][y+1]++;
        }
//        右下
        if(x+1<rowCount&&y+1<rowCount&&numbers[x+1][y+1]!=-1){
            numbers[x+1][y+1]++;
        }

    }

//    埋雷
    private void creatMines(){
        int x,y;
        int minesCount = 0;
        Random random = new Random();
        while (minesCount<mineCount){
            x = random.nextInt(rowCount);
            y = random.nextInt(rowCount);

            if(numbers[x][y]!= -1){
                numbers[x][y] = -1;
                minesCount++;
                setnumbers(x,y);
            }
        }

    }

//    游戏重置
    private void reset(){
        colors = new int[rowCount][rowCount];
        numbers = new int[rowCount][rowCount];
        creatMines();
    }

//    找到触点方块
    private Point findLeftTop(float x,float y){
        Point point = new Point();
        for(int i=0;i< rowCount;i++){
            if(x-i*width>0&&x-i*width<width){
                point.x=i;
            }
            if(y-i*width>0&&y-i*width<width){
                point.y=i;
            }
        }
        return point;
    }

//    画旗帜
    private void drawFlag(Canvas canvas, int x, int y) {
        RectF rectF = new RectF(x * width + 1, y * width + 1, (x + 1) * width - 1, (y + 1) * width - 1);
        canvas.drawBitmap(flagBitmap, null, rectF, null);
    }

//    画炸弹
    private void drawMine(Canvas canvas, int x, int y) {
        RectF rectF = new RectF(x * width + 1, y * width + 1, (x + 1) * width - 1, (y + 1) * width - 1);
        canvas.drawBitmap(mineBitmap, null, rectF, null);
    }

//    根据音效ID播放
    private void PlayMusic(int MusicId) {
        soundPool.play(
                MusicId,
                0.1f,
                0.5f,
                0,
                0,
                1
        );
    }
//    初始化音效
    private void initSound(){
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC,0);
        soundID1 = soundPool.load(mContent,R.raw.clicksound,1);
        soundID2 = soundPool.load(mContent,R.raw.flagsound,1);
        soundID3 = soundPool.load(mContent,R.raw.vectory,1);
        soundID4 = soundPool.load(mContent,R.raw.boom,1);
    }

    private void gameSuccess(){
        new AlertDialog.Builder(mContent)
                .setMessage("恭喜你!你已经找出了所有雷")
                .setCancelable(false)
                .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reset();
                        finish=false;
                        MineSweepingView.this.invalidate();
                        start=true;
                    }
                })
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        signal.exitGame();
                    }
                })
                .create()
                .show();

    }

    private void gameOver(){
        new AlertDialog.Builder(mContent)
                .setMessage("恭喜你，你看到了所有的雷！")
                .setCancelable(false)
                .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reset();
                        finish=false;
                        MineSweepingView.this.invalidate();
                        start=true;

                    }
                })
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        signal.exitGame();

                    }
                })
                .create()
                .show();

    }

    public boolean getStart(){
        return start;
    }

    interface Signal {

        void onStart();

        void onFinish();

        void onFlag(int num);

        void onWin(String name);

        void exitGame();
    }

}
