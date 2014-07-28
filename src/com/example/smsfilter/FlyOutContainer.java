package com.example.smsfilter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class FlyOutContainer extends LinearLayout {

	//references to groups contained in this view
	private View menu;
	private View content;
	
	protected static final int menuMargin = 150;
	public enum MenuState{ CLOSED, OPEN};
	
	//position information attributes
	protected int currentContentOffset = 0;
	protected MenuState menuCurrentState = MenuState.CLOSED;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public FlyOutContainer(Context context, AttributeSet attrs, int defstyle)
	{
		super(context, attrs,defstyle);
	}
	public FlyOutContainer(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	public FlyOutContainer(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		this.menu = this.getChildAt(0);
		this.content = this.getChildAt(1);
		this.menu.setVisibility(View.GONE);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		if(changed)
			this.calculateChildDimensions();
		
		this.menu.layout(left, top, right - menuMargin, bottom);
		this.content.layout(left + this.currentContentOffset, top, right+this.currentContentOffset, bottom);
	}
	
	private void calculateChildDimensions() {
		// TODO Auto-generated method stub
		this.content.getLayoutParams().height = this.getHeight();
		this.content.getLayoutParams().width = this.getWidth();
		this.menu.getLayoutParams().width = this.getWidth() - menuMargin;
		this.menu.getLayoutParams().height = this.getHeight();
	}
	
	public void toggleMenu()
	{
		switch(this.menuCurrentState){
		case CLOSED:
			this.menu.setVisibility(View.VISIBLE);
			this.currentContentOffset = this.getMenuWidth();
			this.content.offsetLeftAndRight(currentContentOffset);
			this.menuCurrentState = MenuState.OPEN;
			break;
		case OPEN:
			this.content.offsetLeftAndRight(-currentContentOffset);
			this.currentContentOffset = 0;
			this.menuCurrentState = MenuState.CLOSED;
			this.menu.setVisibility(View.GONE);
			break;
		}
		this.invalidate();
	}
	
	private int getMenuWidth()
	{
		return this.menu.getLayoutParams().width;
	}
	
	
	

}
