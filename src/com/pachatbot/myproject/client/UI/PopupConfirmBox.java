/**
 * 
 */
package com.pachatbot.myproject.client.UI;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author micro
 *
 */
public class PopupConfirmBox extends DialogBox {
	
	final Button yesButton = new Button("Yes");
	final Button cancelButton = new Button("Cencel");
	
	final Label label = new Label();
	
	private void construct() {
		
		yesButton.setWidth("80px");
		cancelButton.setWidth("80px");
		
		yesButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide(true);
			}
		});
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide(false);
			}
		});
		
		final HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(10);
		buttons.add(yesButton);
		buttons.add(cancelButton);
		
		final VerticalPanel panel = new VerticalPanel();
		panel.setSpacing(6);
		panel.add(label);
		panel.add(buttons);
		
		panel.setCellHorizontalAlignment(label, HasHorizontalAlignment.ALIGN_JUSTIFY);
		panel.setCellHorizontalAlignment(buttons, HasHorizontalAlignment.ALIGN_RIGHT);
		
		label.addStyleName("popupConfirmBox");
		this.setGlassEnabled(true);
		this.setAnimationEnabled(true);
		this.add(panel);

	}
	
	public Label getLabel() {
		return this.label;
	}
	
	public Button getYesButton() {
		return this.yesButton;
	}
	
	public Button getCancelButton() {
		return this.cancelButton;
	}
	
	public void setConfirmText(String message) {
		this.label.setText(message);
	}

	/**
	 * 
	 */
	public PopupConfirmBox() {
		this.construct();
	}

	/**
	 * @param autoHide
	 */
	public PopupConfirmBox(boolean autoHide) {
		super(autoHide);
		this.construct();
	}

	/**
	 * @param captionWidget
	 */
	public PopupConfirmBox(Caption captionWidget) {
		super(captionWidget);
		this.construct();
	}

	/**
	 * @param autoHide
	 * @param modal
	 */
	public PopupConfirmBox(boolean autoHide, boolean modal) {
		super(autoHide, modal);
		this.construct();
	}

	/**
	 * @param autoHide
	 * @param modal
	 * @param captionWidget
	 */
	public PopupConfirmBox(boolean autoHide, boolean modal, Caption captionWidget) {
		super(autoHide, modal, captionWidget);
		this.construct();
	}

}
