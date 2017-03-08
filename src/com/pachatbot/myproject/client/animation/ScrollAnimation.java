/**
 * 
 */
package com.pachatbot.myproject.client.animation;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * @author micro
 *
 */
public class ScrollAnimation extends Animation {

	private ScrollPanel panel;
	private int scrollPositionInc;
	private int targetScrollPos; 
	private int startScrollPos;
	/**
	 * 
	 */
	public ScrollAnimation() {
		// Nothing to do
	}
	
	public ScrollAnimation(ScrollPanel panel) {
		this.panel = panel;
	}

	/**
	 * @param scheduler
	 */
	public ScrollAnimation(AnimationScheduler scheduler) {
		super(scheduler);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.animation.client.Animation#onUpdate(double)
	 */
	@Override
	protected void onUpdate(double progress) {
		panel.getElement().setScrollTop(startScrollPos + (int)(progress * scrollPositionInc));
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.animation.client.Animation#onComplete()
	 */
	@Override
	protected void onComplete() {
		super.onComplete();
		panel.scrollToBottom();
	}
	
	public void scrollToEnd(int duration) { 
		targetScrollPos = panel.getElement().getScrollHeight() - panel.getElement().getClientHeight(); 
		// Animate the scrolling. 
		startScrollPos = panel.getElement().getScrollTop();
		scrollPositionInc = targetScrollPos - startScrollPos;
		run(duration); 
	}

	/**
	 * @param panel the panel to set
	 */
	public void setScrollPanel(ScrollPanel panel) {
		this.panel = panel;
	}

}
