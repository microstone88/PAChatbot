package com.pachatbot.myproject.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.pachatbot.myproject.shared.FieldVerifier;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PAChatbot implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " 
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	
	private final MessageServiceAsync messageService = GWT.create(MessageService.class);
	
	private final SessionControlAsync sessionService = GWT.create(SessionControl.class);


	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		
		final TextBox toBeSent = new TextBox();
		toBeSent.setSize("98%", "1.2em");
		toBeSent.setText("Hello, Pi!");
		final Button sendButton = new Button("Send");
		
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSize("400px", "100%");
		hPanel.setSpacing(5);
		hPanel.add(toBeSent);
		hPanel.add(sendButton);
		hPanel.setCellVerticalAlignment(toBeSent, HorizontalPanel.ALIGN_MIDDLE);
		hPanel.setCellVerticalAlignment(sendButton, HorizontalPanel.ALIGN_MIDDLE);
		hPanel.setCellHorizontalAlignment(toBeSent, HorizontalPanel.ALIGN_LEFT);
		hPanel.setCellHorizontalAlignment(sendButton, HorizontalPanel.ALIGN_RIGHT);
		hPanel.setCellWidth(sendButton, "50px");
		hPanel.ensureDebugId("sendPanel");
		
		// We can add style names to widgets
		sendButton.addStyleName("sendButton");
		
		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
//		RootPanel.get("nameFieldContainer").add(nameField);
//		RootPanel.get("sendButtonContainer").add(sendButton);
//		RootPanel.get("errorLabelContainer").add(errorLabel);
		
		
		DockPanel dock = new DockPanel();
//		dock.addStyleName("dock");
		dock.setSpacing(2);
		dock.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
		
		final VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSize("400px", "100%");
		vPanel.setSpacing(5);
		vPanel.ensureDebugId("messagePanel");
		
		final ScrollPanel scroller = new ScrollPanel();
		scroller.setSize("420px", "200px");
//		scroller.setAlwaysShowScrollBars(true);
		scroller.add(vPanel);
		
		final Label errorLabel = new Label();
		errorLabel.setWidth("70%");
		errorLabel.addStyleName("serverResponseLabelError");
		
//		dock.add(new HTML("<h1>Hi, &pi;-chatbot</h1>"), DockPanel.NORTH);
		dock.add(scroller, DockPanel.CENTER);
		dock.add(errorLabel, DockPanel.SOUTH);
		dock.add(hPanel, DockPanel.SOUTH);

		
		dock.ensureDebugId("dockPanel");
		dock.setSize("100%", "100%");
		RootPanel.get().add(dock);
		
		// Add "Connect" button
//		final Button connectButton = new Button("Connect");
//		connectButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				messageService.connectToDB(new AsyncCallback<String>() {
//
//					@Override
//					public void onFailure(Throwable caught) {
//						Window.alert("Unable to connect to MySQL server: " 
//								+ caught.getMessage());
//					}
//
//					@Override
//					public void onSuccess(String result) {
//						Window.alert(result);
////						errorLabel.setText("Connection established!");
//					}
//				});
//			}
//		});
//		RootPanel.get("connectButtonContainer").add(connectButton);
		
		
		// Focus the cursor on the name field when the app loads
//		nameField.setFocus(true);
//		nameField.selectAll();
		
		toBeSent.setFocus(true);
		toBeSent.selectAll();

		// Create the popup dialog box
//		final DialogBox dialogBox = new DialogBox();
//		dialogBox.setText("Remote Procedure Call");
//		dialogBox.setAnimationEnabled(true);
//		final Button closeButton = new Button("Close");
//		// We can set the id of a widget by accessing its Element
//		closeButton.getElement().setId("closeButton");
//		final Label textToServerLabel = new Label();
//		final HTML serverResponseLabel = new HTML();
//		VerticalPanel dialogVPanel = new VerticalPanel();
//		dialogVPanel.addStyleName("dialogVPanel");
//		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
//		dialogVPanel.add(textToServerLabel);
//		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
//		dialogVPanel.add(serverResponseLabel);
//		dialogVPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
//		dialogVPanel.add(closeButton);
//		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
//		closeButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				dialogBox.hide();
//				sendButton.setEnabled(true);
//				sendButton.setFocus(true);
//			}
//		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			@Override
			public void onClick(ClickEvent event) {
				sendMsgToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendMsgToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
//			private void sendNameToServer() {
//				// First, we validate the input.
//				errorLabel.setText("");
//				String textToServer = nameField.getText();
//				if (!FieldVerifier.isValidName(textToServer)) {
//					errorLabel.setText("Please enter at least four characters");
//					return;
//				}
//
//				// Then, we send the input to the server.
//				sendButton.setEnabled(false);
//				textToServerLabel.setText(textToServer);
//				serverResponseLabel.setText("");
//				greetingService.greetServer(textToServer, new AsyncCallback<String>() {
//					@Override
//					public void onFailure(Throwable caught) {
//						// Show the RPC error message to the user
//						dialogBox.setText("Remote Procedure Call - Failure");
//						serverResponseLabel.addStyleName("serverResponseLabelError");
//						serverResponseLabel.setHTML(SERVER_ERROR);
//						dialogBox.center();
//						closeButton.setFocus(true);
//					}
//
//					@Override
//					public void onSuccess(String result) {
//						dialogBox.setText("Remote Procedure Call");
//						serverResponseLabel.removeStyleName("serverResponseLabelError");
//						serverResponseLabel.setHTML(result);
//						dialogBox.center();
//						closeButton.setFocus(true);
//					}
//				});
//			}
			
			private void sendMsgToServer() {
				errorLabel.setText("");
				final String txtToBeSent = toBeSent.getText();
				
				// send the message to the server
				sendButton.setEnabled(false);
				messageService.connectToDB(new AsyncCallback<String>() {
					
					@Override
					public void onFailure(Throwable caught) {
						errorLabel.setText("Unable to connect to MySQL server:\n" 
								+ caught.getMessage());
						onceFinishSending();
					}

					@Override
					public void onSuccess(String result) {
						displaySentMsgBubble(txtToBeSent);
						displayReceivedMsgBubble("Hello!");
						errorLabel.setText("Succeeded!\n" + result);
						onceFinishSending();
					}
				});
			}
			
			private void onceFinishSending() {
				sendButton.setEnabled(true);
				toBeSent.setFocus(true);
				toBeSent.selectAll();
			}
			
			private void displaySentMsgBubble(String msg) {
				final Label sentMsgLable = new Label(msg);
				sentMsgLable.addStyleName("sentBubble");
				DecoratorPanel sentMsgBubble = new DecoratorPanel();
			    sentMsgBubble.setWidget(sentMsgLable);
				vPanel.add(sentMsgBubble);
				vPanel.setCellHorizontalAlignment(sentMsgBubble, VerticalPanel.ALIGN_RIGHT);
				scroller.ensureVisible(sentMsgBubble);
			}
			
			private void displayReceivedMsgBubble(String msg) {
				final Label receivedMsgLable = new Label(msg);
				receivedMsgLable.addStyleName("receivedBubble");
				DecoratorPanel receivedMsgBubble = new DecoratorPanel();
			    receivedMsgBubble.setWidget(receivedMsgLable);
				vPanel.add(receivedMsgBubble);
				vPanel.setCellHorizontalAlignment(receivedMsgBubble, VerticalPanel.ALIGN_LEFT);
				scroller.ensureVisible(receivedMsgBubble);
			}
			
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
//		sendButton.addClickHandler(handler);
//		nameField.addKeyUpHandler(handler);
		
		sendButton.addClickHandler(handler);
		toBeSent.addKeyUpHandler(handler);
	}
}
