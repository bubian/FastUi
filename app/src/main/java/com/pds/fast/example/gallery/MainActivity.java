package com.pds.fast.example.gallery;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.pds.fast.example.gallery.ViewPager.MyTransformation;
import com.pds.fast.example.gallery.util.ImageUtil;
import com.pds.fast.ui.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int pagerWidth;
    private List<ImageView> imageViewList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_main);
        final ViewPager viewPager= (ViewPager) findViewById(R.id.viewPager);
        imageViewList=new ArrayList<>();
        ImageView first=new ImageView(MainActivity.this);
        /**
         * 为imageview生成的带犹豫倒影的bitmap
         */
        first.setImageBitmap(ImageUtil.getReverseBitmapById(R.mipmap.first,MainActivity.this));
        ImageView second=new ImageView(MainActivity.this);
        second.setImageBitmap(ImageUtil.getReverseBitmapById(R.mipmap.second,MainActivity.this));
        ImageView third=new ImageView(MainActivity.this);
        third.setImageBitmap(ImageUtil.getReverseBitmapById(R.mipmap.third,MainActivity.this));
        ImageView fourth=new ImageView(MainActivity.this);
        fourth.setImageBitmap(ImageUtil.getReverseBitmapById(R.mipmap.fourth,MainActivity.this));
        ImageView fifth=new ImageView(MainActivity.this);
        fifth.setImageBitmap(ImageUtil.getReverseBitmapById(R.mipmap.fifth,MainActivity.this));
        imageViewList.add(first);
        imageViewList.add(second);
        imageViewList.add(third);
        imageViewList.add(fourth);
        imageViewList.add(fifth);
        viewPager.setOffscreenPageLimit(3);
        pagerWidth= (int) (getResources().getDisplayMetrics().widthPixels*3.0f/5.0f);
        ViewGroup.LayoutParams lp=viewPager.getLayoutParams();
        if (lp==null){
            lp=new ViewGroup.LayoutParams(pagerWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        }else {
            lp.width=pagerWidth;
        }
        viewPager.setLayoutParams(lp);
        viewPager.setPageMargin(-50);
        findViewById(R.id.activity_main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return viewPager.dispatchTouchEvent(motionEvent);
            }
        });
        viewPager.setPageTransformer(true,new MyTransformation());
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return imageViewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(imageViewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView=imageViewList.get(position);
                container.addView(imageView,position);
                return imageView;
            }
        });

    }
}