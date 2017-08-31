package app.ixiongpeng.com.win8ui;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by xiongpeng on 2017/8/31.
 */

public class MyImageView extends ImageView {

    private static final String TAG = "MyImagView";

    private static final int SCALL_REDUCE_INIT = 0;
    private static final  int SCALING = 1;
    private static final int SCALL_ADD_INIT = 2;

    private int mWidth;
    private int mheight;
    private int mCenterWight;
    private int mCenterHeight;
    private float mMinScale = 0.85f;
    private boolean isFinish = true;

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        mheight = getHeight() - getPaddingTop() - getPaddingBottom();
        mCenterWight = mWidth / 2;
        mCenterHeight = mheight / 2;

        Drawable drawable = getDrawable();
        BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
        bitmapDrawable.setAntiAlias(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Float X = event.getX();
                Float Y = event.getY();
                mScaleHandler.sendEmptyMessage(SCALL_REDUCE_INIT);
                break;

            case MotionEvent.ACTION_UP:
                mScaleHandler.sendEmptyMessage(SCALL_ADD_INIT);
                break;

        }
        return super.onTouchEvent(event);
    }

    private Handler mScaleHandler = new Handler(){
      private Matrix matrix = new Matrix();
        private int count = 0;
        private float s;

        private boolean isClicked;
        public void handleMessage(android.os.Message msg){
            matrix.set(getImageMatrix());
            switch (msg.what){
                case SCALL_REDUCE_INIT:
                    if(!isFinish){
                        mScaleHandler.sendEmptyMessage(SCALL_REDUCE_INIT);
                    }else{
                        isFinish = false;
                        count = 0;
                        s = (float)Math.sqrt(Math.sqrt(mMinScale));
                        beginScale(matrix, s);
                        mScaleHandler.sendEmptyMessage(SCALING);
                    }
                    break;
                case SCALING:
                    beginScale(matrix, s);
                    if(count < 4){
                        mScaleHandler.sendEmptyMessage(SCALING);
                    }else{
                        isFinish = true;
                        if(MyImageView.this.mOnViewClickListener != null && !isClicked){
                            isClicked = true;
                            MyImageView.this.mOnViewClickListener.onViewClick(MyImageView.this);
                        }else{
                            isClicked = false;
                        }
                    }
                    count++;
                    break;
                case SCALL_ADD_INIT:
                    if(!isFinish){
                        mScaleHandler.sendEmptyMessage(SCALL_ADD_INIT);
                    }else{
                        isClicked = false;
                        count = 0;
                        s = (float)Math.sqrt(Math.sqrt(1.0f/s));
                        beginScale(matrix, s);
                        mScaleHandler.sendEmptyMessage(SCALING);
                    }
                    break;
            }

        }



    };

    protected void sleep(int i){
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    private synchronized void beginScale(Matrix matrix, float scale){
        matrix.postScale(scale, scale, mCenterWight, mCenterHeight);
        setImageMatrix(matrix);
    }

    private OnViewClickListener mOnViewClickListener;

    public void SetOnclickListener(OnViewClickListener onViewClickListener){
        this.mOnViewClickListener = onViewClickListener;
    }

    public interface OnViewClickListener{
        void onViewClick(MyImageView View);
    }


}
