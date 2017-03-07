package com.pachatbot.myproject.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PAChatbot implements EntryPoint {
	
	private static final boolean IS_MOBILE;
	private static final boolean IS_IPHONE;
	private static final boolean IS_IPAD;
	private static final boolean IS_IPOD;
	private static final boolean IS_ANDROID;
	
	static {
		IS_IPHONE = Window.Navigator.getUserAgent().toLowerCase().contains("iphone");
		IS_IPAD = Window.Navigator.getUserAgent().toLowerCase().contains("ipad");
		IS_IPOD = Window.Navigator.getUserAgent().toLowerCase().contains("ipod");
		IS_ANDROID = Window.Navigator.getUserAgent().toLowerCase().contains("android");
		IS_MOBILE = IS_ANDROID || IS_IPHONE || IS_IPAD || IS_IPOD
				|| Window.Navigator.getUserAgent().toLowerCase().contains("mobile");
	}
	
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
		
		final String userAgent = Window.Navigator.getUserAgent();
		
		final FadeAnimation fadeAnimation = new FadeAnimation();
		
		final Label errorLabel = new Label();
		errorLabel.setWidth("500px");
		errorLabel.addStyleName("serverResponseLabelError");
		
		/**
		 *  Dialogue UI
		 */
		final TextBox messageField = new TextBox();
		messageField.setText("Hello, Pi!");
		messageField.setSize("97%", "1.2em");
		final Button sendButton = new Button("Send");
		
		HorizontalPanel sendPanel = new HorizontalPanel();
		sendPanel.setSpacing(5);
		sendPanel.add(messageField);
		sendPanel.add(sendButton);
		sendPanel.setCellVerticalAlignment(messageField, HorizontalPanel.ALIGN_MIDDLE);
		sendPanel.setCellVerticalAlignment(sendButton, HorizontalPanel.ALIGN_MIDDLE);
		sendPanel.setCellHorizontalAlignment(messageField, HorizontalPanel.ALIGN_CENTER);
		sendPanel.setCellHorizontalAlignment(sendButton, HorizontalPanel.ALIGN_CENTER);
		sendPanel.setCellWidth(sendButton, "3%");
		sendPanel.ensureDebugId("sendPanel");
		
		final VerticalPanel bubbleLayout = new VerticalPanel();
		bubbleLayout.setSpacing(5);
		bubbleLayout.ensureDebugId("messagePanel");
		
		final ScrollPanel chatScrollPanel = new ScrollPanel();
		chatScrollPanel.add(bubbleLayout);
		chatScrollPanel.ensureDebugId("chatPanel");
		
		/**
		 *  Sign in UI
		 */
		final TextBox usrField = new TextBox();
		usrField.setText("username or email or cellphone");
		final PasswordTextBox pwField = new PasswordTextBox();
		pwField.setText("password");
		
		final Button forgotPwButton = new Button("Forgotten?");
		final Button signInButton = new Button("Sign In");
		
		final FlexTable signInTable = new FlexTable();
		signInTable.setCellSpacing(4);
		
		final FlexCellFormatter signInTableFormatter = 
				signInTable.getFlexCellFormatter();
		signInTable.setWidget(0, 0, usrField);
		signInTable.setWidget(1, 0, pwField);
		signInTableFormatter.setColSpan(0, 0, 2);
		signInTableFormatter.setColSpan(1, 0, 2);
		signInTable.setWidget(2, 0, forgotPwButton);
		signInTable.setWidget(2, 1, signInButton);
		signInTableFormatter.setHorizontalAlignment(0, 0, 
				HasHorizontalAlignment.ALIGN_CENTER);
		signInTableFormatter.setHorizontalAlignment(1, 0, 
				HasHorizontalAlignment.ALIGN_CENTER);
		signInTableFormatter.setHorizontalAlignment(2, 1, 
				HasHorizontalAlignment.ALIGN_RIGHT);
		
		/**
		 *  Register UI
		 */
		final TextBox fstNameField = new TextBox();
		fstNameField.setText("first name");
		final TextBox sndNameField = new TextBox();
		sndNameField.setText("second name");
		
		final TextBox emailField = new TextBox();
		emailField.setText("email address");
		final TextBox cellphoneField = new TextBox();
		cellphoneField.setText("cellphone number");
		
		final TextBox newUsrField = new TextBox();
		newUsrField.setText("username");
		final PasswordTextBox newPwField = new PasswordTextBox();
		newPwField.setText("password");
		
		final CheckBox agreeCheck = new CheckBox();
		agreeCheck.setHTML(" I agree to "
				+ "<a href=\"https://www.google.fr/\">"
				+ "the terms & services"
				+ "</a>.");
		final CheckBox showNewPwCheck = new CheckBox(" Show password");
		final Button signUpButton = new Button("Sign Up");
		
		final FlexTable registerTable = new FlexTable();
		registerTable.setCellSpacing(4);

		final FlexCellFormatter registerTableCellFormatter =
				registerTable.getFlexCellFormatter();
		registerTable.setWidget(0, 0, fstNameField);
		registerTable.setWidget(0, 1, sndNameField);
		registerTable.setWidget(1, 0, emailField);
		registerTable.setWidget(2, 0, cellphoneField);
		registerTable.setWidget(3, 0, newUsrField);
		registerTable.setWidget(4, 0, newPwField);
		registerTable.setWidget(5, 0, agreeCheck);
		registerTable.setWidget(6, 0, showNewPwCheck);
		registerTable.setWidget(7, 1, signUpButton);
		
		registerTableCellFormatter.setColSpan(1, 0, 2);
		registerTableCellFormatter.setColSpan(2, 0, 2);
		registerTableCellFormatter.setColSpan(3, 0, 2);
		registerTableCellFormatter.setColSpan(4, 0, 2);
		registerTableCellFormatter.setColSpan(5, 0, 2);
		registerTableCellFormatter.setColSpan(6, 0, 2);
		
		registerTableCellFormatter.setHorizontalAlignment(0, 0,
				HasHorizontalAlignment.ALIGN_CENTER);
		registerTableCellFormatter.setHorizontalAlignment(0, 1,
				HasHorizontalAlignment.ALIGN_CENTER);
		registerTableCellFormatter.setHorizontalAlignment(1, 0, 
				HasHorizontalAlignment.ALIGN_CENTER);
		registerTableCellFormatter.setHorizontalAlignment(2, 0, 
				HasHorizontalAlignment.ALIGN_CENTER);
		registerTableCellFormatter.setHorizontalAlignment(3, 0, 
				HasHorizontalAlignment.ALIGN_CENTER);
		registerTableCellFormatter.setHorizontalAlignment(4, 0, 
				HasHorizontalAlignment.ALIGN_CENTER);
		registerTableCellFormatter.setHorizontalAlignment(7, 1, 
				HasHorizontalAlignment.ALIGN_RIGHT);
		
		/**
		 *  Panels for layout on mobile devices
		 */
		final StackLayoutPanel stackLayout = new StackLayoutPanel(Unit.EM);
		stackLayout.ensureDebugId("stackLayout");
		final ScrollPanel signInScrollPanel = new ScrollPanel();
		signInScrollPanel.ensureDebugId("signInScrollPanel");
		final ScrollPanel registerScrollPanel = new ScrollPanel();
		registerScrollPanel.ensureDebugId("registerScrollPanel");
		
		if (!IS_MOBILE) {
			
			final DisclosurePanel signInPanel = new DisclosurePanel("Sign In");
			signInPanel.setAnimationEnabled(true);
			signInPanel.setOpen(false);
			signInPanel.setContent(signInTable);
			signInPanel.ensureDebugId("signInPanel");
			
			final DisclosurePanel registerPanel = new DisclosurePanel("Register");
			registerPanel.setAnimationEnabled(true);
			registerPanel.setOpen(false);
			registerPanel.setContent(registerTable);
			registerPanel.ensureDebugId("registerPanel");
			
			final HorizontalPanel accountPanel = new HorizontalPanel();
			accountPanel.setSpacing(0);
			accountPanel.add(signInPanel);
			accountPanel.add(registerPanel);
			accountPanel.setCellHorizontalAlignment(signInPanel, HorizontalPanel.ALIGN_LEFT);
			accountPanel.setCellHorizontalAlignment(registerPanel, HorizontalPanel.ALIGN_LEFT);
			accountPanel.ensureDebugId("accountPanel");
			
			/**
			 *  <--- Styling --->
			 */
			messageField.addStyleName("normalSendButton");
			sendButton.addStyleName("normalSendButton");
//			forgotPwButton.addStyleName("normalSendButton");
//			signInButton.addStyleName("normalSendButton");
//			signUpButton.addStyleName("normalSendButton");
			
			agreeCheck.addStyleName("normalDisplayText");
			showNewPwCheck.addStyleName("normalDisplayText");
			
			/**
			 * 	<--- Sizing --->
			 */
			sendPanel.setWidth("540px");
			accountPanel.setWidth("540px");
			bubbleLayout.setSize("540px", "100%");
			chatScrollPanel.setSize("560px", "240px");
			
			usrField.setWidth("230px");
			pwField.setWidth("230px");
			
			fstNameField.setWidth("105px");
			sndNameField.setWidth("105px");
			emailField.setWidth("230px");
			cellphoneField.setWidth("230px");
			newUsrField.setWidth("230px");
			newPwField.setWidth("230px");

			signInPanel.setWidth("280px"); // Do NOT set to 100%
			registerPanel.setWidth("280px"); // Do NOT set to 100%
			signInTable.setWidth("260px");
			registerTable.setWidth("260px");
			accountPanel.setWidth("560px");
			
			/**
			 *  Main panel layout
			 */
			final DockPanel dock = new DockPanel();
//			dock.addStyleName("dock"); // For debugging
			dock.setSize("100%", "100%");
			dock.setSpacing(10);
			dock.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
			dock.setVerticalAlignment(DockPanel.ALIGN_MIDDLE);
			dock.add(new HTML("<h1>Hi, &pi;-chatbot!</h1>"), DockPanel.NORTH);
			dock.add(errorLabel, DockPanel.SOUTH);
			dock.add(accountPanel, DockPanel.NORTH);
			dock.add(sendPanel, DockPanel.SOUTH);
			dock.add(chatScrollPanel, DockPanel.CENTER);
			dock.ensureDebugId("dockPanel");

			// Add the main dock panel to the Root panel 
			RootPanel.get().add(dock);
			
		} else {
			
//			final LayoutPanel signInLayout = new LayoutPanel();
//			signInLayout.add(signInTable);
//			signInLayout.setWidgetLeftRight(signInTable, 10, Unit.PCT, 10, Unit.PCT);
			
//			final LayoutPanel registerLayout = new LayoutPanel();
//			registerLayout.add(registerTable);
//			registerLayout.setWidgetLeftRight(registerTable, 10, Unit.PCT, 10, Unit.PCT);
			
			signInScrollPanel.add(signInTable);
			registerScrollPanel.add(registerTable);
			
			stackLayout.add(chatScrollPanel, new HTML("Dialogue"), 2.4);
			stackLayout.add(signInScrollPanel, new HTML("Sign In"), 2.4);
			stackLayout.add(registerScrollPanel, new HTML("Register"), 2.4);
			stackLayout.showWidget(chatScrollPanel);
			
			/**
			 *  <--- Styling --->
			 */
			messageField.addStyleName("mobileSendButton");
			usrField.addStyleName("mobileSendButton");
			pwField.addStyleName("mobileSendButton");
			fstNameField.addStyleName("mobileSendButton");
			sndNameField.addStyleName("mobileSendButton");
			emailField.addStyleName("mobileSendButton");
			cellphoneField.addStyleName("mobileSendButton");
			newUsrField.addStyleName("mobileSendButton");
			newPwField.addStyleName("mobileSendButton");
			
			sendButton.addStyleName("mobileSendButton");
			forgotPwButton.addStyleName("mobileSendButton");
			signInButton.addStyleName("mobileSendButton");
			signUpButton.addStyleName("mobileSendButton");
			
			agreeCheck.addStyleName("mobileDisplayText");
			showNewPwCheck.addStyleName("mobileDisplayText");
			
			stackLayout.getHeaderWidget(0).setStyleName("customStackPanelHeader");
			stackLayout.getHeaderWidget(1).setStyleName("customStackPanelHeader");
			stackLayout.getHeaderWidget(2).setStyleName("customStackPanelHeader");
			
			/**
			 *  <--- Sizing --->
			 */
			sendPanel.setWidth("100%");
			sendPanel.setHeight("100%");
			bubbleLayout.setSize("100%", "100%");
			
			usrField.setWidth("98%");
			pwField.setWidth("98%");
			
			fstNameField.setWidth("95%");
			sndNameField.setWidth("95%");
			emailField.setWidth("98%");
			cellphoneField.setWidth("98%");
			newUsrField.setWidth("98%");
			newPwField.setWidth("98%");
			
			int width = Window.getClientWidth() - 9;
			chatScrollPanel.setWidth(width + "px");
			signInScrollPanel.setWidth(width + "px");
			registerScrollPanel.setWidth(width + "px");
			signInTable.setWidth(width - 8 + "px");
			registerTable.setWidth(width - 8 + "px");
			
			/**
			 *  Main panel layout
			 */
			final DockLayoutPanel dockLayout = new DockLayoutPanel(Unit.EM);
//			dockLayout.addStyleName("dock"); // For debugging
			dockLayout.addNorth(new HTML("<h1>Hi, &pi;-chatbot!</h1>"), 4);
			dockLayout.addSouth(sendPanel, 4);
			dockLayout.add(stackLayout);
			dockLayout.ensureDebugId("dockLayout");
			
			// Add the main dock layout panel to the Root layout panel
			RootLayoutPanel.get().add(dockLayout);
		}
		
		
		// Focus the cursor on the name field when the app loads
		messageField.setFocus(true);
		messageField.selectAll();
		
		Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				
				if (!IS_MOBILE) {
					
				} else {
					int width = Window.getClientWidth() - 9;
					chatScrollPanel.setWidth(width + "px");
					signInScrollPanel.setWidth(width + "px");
					registerScrollPanel.setWidth(width + "px");
					signInTable.setWidth(width - 8 + "px");
					registerTable.setWidth(width - 8 + "px");
				}
				
			}
		});

		// Create a handler for the sendButton and messageField
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

			private void sendMsgToServer() {
				errorLabel.setText("");
				final String txtToBeSent = messageField.getText();
				displaySentMsgBubble(txtToBeSent);
				
				// send the message to the server
				sendButton.setEnabled(false);
				messageService.connectToDB(new AsyncCallback<String>() {
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(SERVER_ERROR + "\n" + caught.getMessage());
						onceFinishSending();
					}

					@Override
					public void onSuccess(String result) {
						displayReceivedMsgBubble("Hello, there!");
//						Window.alert(result);
						errorLabel.setText(userAgent);
						onceFinishSending();
					}
				});
			}
			
			private void onceFinishSending() {
				sendButton.setEnabled(true);
				messageField.setFocus(true);
				messageField.selectAll();
			}
			
			private void displaySentMsgBubble(String msg) {
				final Label sentMsgLable = new Label(msg);
				if (!IS_MOBILE) sentMsgLable.addStyleName("normalSentMessageText");
				else {
					// Ensure the visibility on mobile device
					stackLayout.showWidget(chatScrollPanel); 
					sentMsgLable.addStyleName("mobileSentMessageText");
				}
				DecoratorPanel sentBubble = new DecoratorPanel();
				sentBubble.addStyleName("sentBubble");
			    sentBubble.setWidget(sentMsgLable);
				bubbleLayout.add(sentBubble);
				bubbleLayout.setCellHorizontalAlignment(sentBubble, VerticalPanel.ALIGN_RIGHT);
				chatScrollPanel.ensureVisible(sentBubble);
				
				// add animation to the message bubble
				fadeAnimation.cancel();
				sentBubble.getElement().getStyle().setOpacity(.1);
				fadeAnimation.setElement(sentBubble);
				fadeAnimation.fade(500, 1.0);
				
			}
			
			private void displayReceivedMsgBubble(String msg) {
				final Label receivedMsgLable = new Label(msg);
				if (!IS_MOBILE) receivedMsgLable.addStyleName("normalReceivedMessageText");
				else {
					// Ensure the visibility on mobile device
					stackLayout.showWidget(chatScrollPanel);
					receivedMsgLable.addStyleName("mobileReceivedMessageText");
				}
				DecoratorPanel receivedBubble = new DecoratorPanel();
				receivedBubble.addStyleName("receivedBubble");
			    receivedBubble.setWidget(receivedMsgLable);
				bubbleLayout.add(receivedBubble);
				bubbleLayout.setCellHorizontalAlignment(receivedBubble, VerticalPanel.ALIGN_LEFT);
				chatScrollPanel.ensureVisible(receivedBubble);
				
				// add animation to the message bubble
				fadeAnimation.cancel();
				receivedBubble.getElement().getStyle().setOpacity(.1);
				fadeAnimation.setElement(receivedBubble);
				fadeAnimation.fade(500, 1.0);
			}
			
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		messageField.addKeyUpHandler(handler);
		
	}
}
