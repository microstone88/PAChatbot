/**
 * 
 */
package com.pachatbot.myproject.client;

import java.math.BigDecimal;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author micro
 *
 */
public class FadeAnimation extends Animation {

	private Widget widget;
	private double opacityIncrement;
	private double targetOpacity;
	private double baseOpacity;
	 
	/**
	 * 
	 */
	public FadeAnimation() {
		// Nothing to do
	}
	
	public FadeAnimation(Widget widget) {
		this.widget = widget;
	}

	/**
	 * @param scheduler
	 */
	public FadeAnimation(AnimationScheduler scheduler) {
		super(scheduler);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.animation.client.Animation#onUpdate(double)
	 */
	@Override
	protected void onUpdate(double progress) {
		widget.getElement().getStyle().setOpacity(baseOpacity + progress * opacityIncrement);
	}
	
	protected void onComplete() {
		super.onComplete();
		widget.getElement().getStyle().setOpacity(targetOpacity);
	}
	
	public void fade(int duration, double targetOpacity) {
		 if(targetOpacity > 1.0) {
			 targetOpacity = 1.0;
		 }
		 if(targetOpacity < 0.0) {
			 targetOpacity = 0.0;
		 }
		 this.targetOpacity = targetOpacity;
		 String opacityStr = widget.getElement().getStyle().getOpacity();
		 try {
			 baseOpacity = new BigDecimal(opacityStr).doubleValue();
			 opacityIncrement = targetOpacity - baseOpacity;
			 run(duration);
		 } catch(NumberFormatException e) {
			 // set opacity directly
			 onComplete();
			 
		 }
	}

	/**
	 * @param widget the element to set
	 */
	public void setElement(Widget widget) {
		this.widget = widget;
	}

}
