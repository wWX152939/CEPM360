package com.pm360.cepm360.app.common.view;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class CustomTranslateAnimation {
	static TranslateAnimation mRightShowAction;
	static TranslateAnimation mRightFadeAction;
	static TranslateAnimation mLeftShowAction;
	static TranslateAnimation mLeftFadeAction;
	static TranslateAnimation mUpShowAction;
	static TranslateAnimation mUpFadeAction;
	static TranslateAnimation mDownShowAction;
	static TranslateAnimation mDownFadeAction;
	
	static {
		initLeftShowTranslateAnimation();
		initRightShowTranslateAnimation();
		initUpShowTranslateAnimation();
		initDownShowTranslateAnimation();
		
		initLeftFadeTranslateAnimation();
		initRightFadeTranslateAnimation();
		initUpFadeTranslateAnimation();
		initDownFadeTranslateAnimation();
	}
	
	public static void initRightShowTranslateAnimation() {
		mRightShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,     
                Animation.RELATIVE_TO_SELF, 0.0f, 
                Animation.RELATIVE_TO_SELF, 0.0f, 
                Animation.RELATIVE_TO_SELF, 0.0f);     
    	mRightShowAction.setDuration(500);
	}
	
	public static void initRightFadeTranslateAnimation() {
		mRightFadeAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
        		Animation.RELATIVE_TO_SELF, 1.0f,     
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);    
    	mRightFadeAction.setDuration(500);
	}

	public static void initLeftShowTranslateAnimation() {
    	mLeftShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f,     
                Animation.RELATIVE_TO_SELF, 0.0f, 
                Animation.RELATIVE_TO_SELF, 0.0f, 
                Animation.RELATIVE_TO_SELF, 0.0f);     
    	mLeftShowAction.setDuration(500);
	}
	
	public static void initLeftFadeTranslateAnimation() {
    	mLeftFadeAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
        		Animation.RELATIVE_TO_SELF, -1.0f,     
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);    
    	mLeftFadeAction.setDuration(500);
	}
	
	public static void initUpShowTranslateAnimation() {
		mUpShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,     
                Animation.RELATIVE_TO_SELF, 0.0f, 
                Animation.RELATIVE_TO_SELF, -1.0f, 
                Animation.RELATIVE_TO_SELF, 0.0f);     
    	mUpShowAction.setDuration(500);
	}
	
	public static void initUpFadeTranslateAnimation() {
		mUpFadeAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
        		Animation.RELATIVE_TO_SELF, 0.0f,     
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f);    
    	mUpFadeAction.setDuration(500);
	}

	public static void initDownShowTranslateAnimation() {
    	mDownShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,     
                Animation.RELATIVE_TO_SELF, 0.0f, 
                Animation.RELATIVE_TO_SELF, 1.0f, 
                Animation.RELATIVE_TO_SELF, 0.0f);     
    	mDownShowAction.setDuration(500);
	}
	
	public static void initDownFadeTranslateAnimation() {
    	mDownFadeAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
        		Animation.RELATIVE_TO_SELF, 0.0f,     
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f);    
    	mDownFadeAction.setDuration(500);
	}
	
	/* 
	 * 淡入 
	 */
	public static void showLeft(View view, long delayMillis) {
		mLeftShowAction.setStartOffset(delayMillis);
		
		view.setVisibility(View.VISIBLE);
		view.startAnimation(mLeftShowAction);
	}
	
	public static void showLeft(View view) {
		showLeft(view, 0);
	}
	
	public static void showRight(View view, long delayMillis) {
		mRightShowAction.setStartOffset(delayMillis);
		
		view.setVisibility(View.VISIBLE);
		view.startAnimation(mRightShowAction);
	}
	
	public static void showRight(View view) {
		showRight(view, 0);
	}
	
	public static void showUp(View view, long delayMillis) {
		mUpShowAction.setStartOffset(delayMillis);
		
		view.setVisibility(View.VISIBLE);
		view.startAnimation(mUpShowAction);
	}
	
	public static void showUp(View view) {
		showUp(view, 0);
	}
	
	public static void showDown(View view, long delayMillis) {
		mDownShowAction.setStartOffset(delayMillis);
		
		view.setVisibility(View.VISIBLE);
		view.startAnimation(mDownShowAction);
	}
	
	public static void showDown(View view) {
		showDown(view, 0);
	}
	
	/**
	 * 淡出
	 * @param view
	 */
	public static void fadeLeft(View view, long delayMillis) {
		mLeftFadeAction.setStartOffset(delayMillis);
		
		view.setVisibility(View.GONE);
		view.startAnimation(mLeftFadeAction);
	}
	
	public static void fadeLeft(View view) {
		fadeLeft(view, 0);
	}
	
	public static void fadeRight(View view, long delayMillis) {
		mRightFadeAction.setStartOffset(delayMillis);
		
		view.setVisibility(View.GONE);
		view.startAnimation(mRightFadeAction);
	}
	
	public static void fadeRight(View view) {
		fadeRight(view, 0);
	}
	
	public static void fadeUp(View view, long delayMillis) {
		mUpFadeAction.setStartOffset(delayMillis);
		
		view.setVisibility(View.GONE);
		view.startAnimation(mUpFadeAction);
	}
	
	public static void fadeUp(View view) {
		fadeUp(view, 0);
	}
	
	public static void fadeDown(View view, long delayMillis) {
		mDownFadeAction.setStartOffset(delayMillis);
		
		view.setVisibility(View.GONE);
		view.startAnimation(mDownFadeAction);
	}
	
	public static void fadeDown(View view) {
		fadeDown(view, 0);
	}
	
	// 淡入和淡出
	public static void showRight(View show, View fade) {
		showRight(show);
		fadeLeft(fade);
	}
	
	public static void showLeft(View show, View fade) {
		showLeft(show);
		fadeRight(fade);
	}
	
	public static void showDown(View show, View fade) {
		showDown(show);
		fadeUp(fade);
	}
	
	public static void showUp(View show, View fade) {
		showUp(show);
		fadeDown(fade);
	}
}
