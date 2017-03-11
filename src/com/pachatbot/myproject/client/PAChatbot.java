package com.pachatbot.myproject.client;

import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Cookies;
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
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.pachatbot.myproject.client.animation.FadeAnimation;
import com.pachatbot.myproject.client.animation.ScrollAnimation;
import com.pachatbot.myproject.shared.FieldVerifier;
import com.pachatbot.myproject.shared.StringUtils;
import com.pachatbot.myproject.shared.Bean.Account;
import com.pachatbot.myproject.shared.Bean.Message;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PAChatbot implements EntryPoint {
	
	private static final boolean IS_MOBILE;
	private static final boolean IS_IPHONE;
	private static final boolean IS_IPAD;
	private static final boolean IS_IPOD;
	private static final boolean IS_IOS;
	private static final boolean IS_ANDROID;
	
	static {
		IS_IPHONE = Window.Navigator.getUserAgent().toLowerCase().contains("iphone");
		IS_IPAD = Window.Navigator.getUserAgent().toLowerCase().contains("ipad");
		IS_IPOD = Window.Navigator.getUserAgent().toLowerCase().contains("ipod");
		IS_ANDROID = Window.Navigator.getUserAgent().toLowerCase().contains("android");
		IS_IOS = IS_IPHONE || IS_IPAD || IS_IPOD;
		IS_MOBILE = IS_ANDROID || IS_IOS
				|| Window.Navigator.getUserAgent().toLowerCase().contains("mobile");
	}
	
	private static final String[] DEFAULT_TEXT = {"username or email or cellphone",
			"password", "first name", "second name", "email address", "cellphone number",
			"username", "password"};
	
	private static final int FADING_DURATION = 500;
	private static final int SCROLLING_DURATION = 200;
	private static final int MAX_SCROLLING_DURATION = 800;
	
	private static final String COOKIE_NAME = "pac_uid";
	
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " 
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side service.
	 * Note: (by micro) this has been simplified by using the inner Utils class.
	 */
//	private final MessageServiceAsync messageService = GWT.create(MessageService.class);
//	private final SessionControlAsync sessionService = GWT.create(SessionControl.class);

	/**
	 * Customized animation
	 */
	final FadeAnimation fadeAnimator = new FadeAnimation();
	final ScrollAnimation scrollAnimator = new ScrollAnimation();
	
	/**
	 * Dialogue container
	 */
	final VerticalPanel bubbleLayout = new VerticalPanel();
	final ScrollPanel chatScrollPanel = new ScrollPanel();
	
	/**
	 * Panels for layout on PC
	 */
	final HorizontalPanel accountPanel = new HorizontalPanel();
	final DisclosurePanel signInPanel = new DisclosurePanel("Sign In");
	final DisclosurePanel registerPanel = new DisclosurePanel("Register");
	/**
	 * Panels for layout on mobile device
	 */
	final StackLayoutPanel stackLayout = new StackLayoutPanel(Unit.EM);
	final ScrollPanel signInScrollPanel = new ScrollPanel();
	final ScrollPanel registerScrollPanel = new ScrollPanel();
	final ScrollPanel optionsScrollPanel = new ScrollPanel();
	
	/**
	 * For debugging
	 */
	final Label errorLabel = new Label();
	
	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		
		errorLabel.setWidth("500px");
		errorLabel.addStyleName("errorLabel");
		
		/**
		 *  Dialogue UI
		 */
		final TextBox messageField = new TextBox();
		messageField.setSize("97%", "1.2em");
		final Button sendButton = new Button("Send");
		
		HorizontalPanel sendPanel = new HorizontalPanel();
		sendPanel.setSpacing(5);
		sendPanel.add(messageField);
		sendPanel.add(sendButton);
		sendPanel.setCellVerticalAlignment(messageField, HasVerticalAlignment.ALIGN_MIDDLE);
		sendPanel.setCellVerticalAlignment(sendButton, HasVerticalAlignment.ALIGN_MIDDLE);
		sendPanel.setCellHorizontalAlignment(messageField, HasHorizontalAlignment.ALIGN_CENTER);
		sendPanel.setCellHorizontalAlignment(sendButton, HasHorizontalAlignment.ALIGN_CENTER);
		sendPanel.setCellWidth(sendButton, "3%");
		sendPanel.ensureDebugId("sendPanel");
		
		bubbleLayout.setSpacing(5);
		bubbleLayout.ensureDebugId("messagePanel");
		chatScrollPanel.add(bubbleLayout);
		chatScrollPanel.ensureDebugId("chatPanel");
		scrollAnimator.setScrollPanel(chatScrollPanel);
		
		/**
		 *  Sign in UI
		 */
		final TextBox usrField = new TextBox();
		final PasswordTextBox pwField = new PasswordTextBox();
		pwField.getElement().setAttribute("type", "text");
		final CheckBox showPwCheck = new CheckBox(" Show password");
		showPwCheck.setValue(true);
		
		final Button forgotPwButton = new Button("Forgotten?");
		final Button signInButton = new Button("Sign In");
		
		final FlexTable signInTable = new FlexTable();
		signInTable.setCellSpacing(6);
		
		final FlexCellFormatter signInTableFormatter = 
				signInTable.getFlexCellFormatter();
		signInTable.setWidget(0, 0, usrField);
		signInTable.setWidget(1, 0, pwField);
		signInTable.setWidget(2, 0, showPwCheck);
		signInTableFormatter.setColSpan(0, 0, 2);
		signInTableFormatter.setColSpan(1, 0, 2);
		signInTableFormatter.setColSpan(2, 0, 2);
		signInTable.setWidget(3, 0, forgotPwButton);
		signInTable.setWidget(3, 1, signInButton);
		signInTableFormatter.setHorizontalAlignment(0, 0, 
				HasHorizontalAlignment.ALIGN_CENTER);
		signInTableFormatter.setHorizontalAlignment(1, 0, 
				HasHorizontalAlignment.ALIGN_CENTER);
		signInTableFormatter.setHorizontalAlignment(3, 1, 
				HasHorizontalAlignment.ALIGN_RIGHT);
		
		/**
		 *  Register UI
		 */
		final TextBox fstNameField = new TextBox();
		final TextBox sndNameField = new TextBox();
		final TextBox emailField = new TextBox();
		final TextBox cellphoneField = new TextBox();
		final TextBox newUsrField = new TextBox();
		final PasswordTextBox newPwField = new PasswordTextBox();
		newPwField.getElement().setAttribute("type", "text");
		
		final CheckBox agreeCheck = new CheckBox();
		agreeCheck.setHTML(" I agree to "
				+ "<a href=\"https://www.google.fr/\">"
				+ "the terms & services"
				+ "</a>.");
		final CheckBox showNewPwCheck = new CheckBox(" Show password");
		showNewPwCheck.setValue(true);
		final Button signUpButton = new Button("Sign Up");
		signUpButton.setEnabled(false);
		
		final FlexTable registerTable = new FlexTable();
		registerTable.setCellSpacing(6);

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
		
		TextBox[] fields = {usrField, pwField, fstNameField, sndNameField,
				emailField, cellphoneField, newUsrField, newPwField};
		initializeTextFields(fields);
		
		if (!IS_MOBILE) {
			
			signInPanel.setAnimationEnabled(true);
			signInPanel.setOpen(false);
			signInPanel.setContent(signInTable);
			signInPanel.ensureDebugId("signInPanel");
			
			registerPanel.setAnimationEnabled(true);
			registerPanel.setOpen(false);
			registerPanel.setContent(registerTable);
			registerPanel.ensureDebugId("registerPanel");
			
			accountPanel.setSpacing(0);
			accountPanel.ensureDebugId("accountPanel");
			
			/**
			 *  <--- Styling on PC --->
			 */
			messageField.addStyleName("normalSendButton");
			sendButton.addStyleName("normalSendButton");
//			forgotPwButton.addStyleName("normalSendButton");
//			signInButton.addStyleName("normalSendButton");
//			signUpButton.addStyleName("normalSendButton");
			
			signInPanel.getHeader().addStyleName("disclosurePanelHeader");
			registerPanel.getHeader().addStyleName("disclosurePanelHeader");
			
			showPwCheck.addStyleName("normalDisplayText");
			agreeCheck.addStyleName("normalDisplayText");
			showNewPwCheck.addStyleName("normalDisplayText");
			
			/**
			 * 	<--- Sizing on PC --->
			 */
			sendPanel.setWidth("540px");
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
			 *  Main panel layout on PC
			 */
			final DockPanel dock = new DockPanel();
			dock.ensureDebugId("dockPanel");
//			dock.addStyleName("dock"); // For debugging
			dock.setSize("100%", "100%");
			dock.setSpacing(10);
			dock.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			dock.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			dock.add(new HTML("<h1>Hi, &pi;-chatbot!</h1>"), DockPanel.NORTH);
			dock.add(errorLabel, DockPanel.SOUTH);
			dock.add(accountPanel, DockPanel.NORTH);
			dock.add(sendPanel, DockPanel.SOUTH);
			dock.add(chatScrollPanel, DockPanel.CENTER);

			// Add the main dock panel to the Root panel 
			RootPanel.get().add(dock);
			
			/**
			 * On-startup settings on PC
			 */
			messageField.setText("Hello, Pi!");
			// Focus the cursor on the name field when the app loads
			messageField.setFocus(true);
			messageField.selectAll();
			
		} else {
			
			signInScrollPanel.ensureDebugId("signInScrollPanel");
			registerScrollPanel.ensureDebugId("registerScrollPanel");
			signInScrollPanel.add(signInTable);
			registerScrollPanel.add(registerTable);
			
			stackLayout.ensureDebugId("stackLayout");
			stackLayout.add(chatScrollPanel, new HTML("Dialogue"), 2.4);
			stackLayout.showWidget(chatScrollPanel);
			
			/**
			 *  <--- Styling on mobile device --->
			 */
			messageField.addStyleName("mobileSendButton");
			
			sendButton.addStyleName("mobileSendButton");
			forgotPwButton.addStyleName("mobileSendButton");
			signInButton.addStyleName("mobileSendButton");
			signUpButton.addStyleName("mobileSendButton");
			
			showPwCheck.setStyleName("mobileDisplayText");
			agreeCheck.setStyleName("mobileDisplayText");
			showNewPwCheck.setStyleName("mobileDisplayText");
			
			stackLayout.getHeaderWidget(0).setStyleName("customStackPanelHeader");
			
			/**
			 *  <--- Sizing on mobile device --->
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
			 *  Main panel layout on mobile device
			 */
			final DockLayoutPanel dockLayout = new DockLayoutPanel(Unit.EM);
			dockLayout.ensureDebugId("dockLayout");
//			dockLayout.addStyleName("dock"); // For debugging
			dockLayout.addNorth(new HTML("<h1>Hi, &pi;-chatbot!</h1>"), 4);
			dockLayout.addSouth(sendPanel, 4);
			dockLayout.add(stackLayout);
			
			// Add the main dock layout panel to the Root layout panel
			RootLayoutPanel.get().add(dockLayout);
			
			/**
			 * On-startup settings on mobile device
			 */
			messageField.setText("");
			
			/**
			 * Handle window resizing event on mobile device
			 */
			Window.addResizeHandler(new ResizeHandler() {
				
				@Override
				public void onResize(ResizeEvent event) {
					// extra resizing width
					int width = Window.getClientWidth() - 9;
					chatScrollPanel.setWidth(width + "px");
					signInScrollPanel.setWidth(width + "px");
					registerScrollPanel.setWidth(width + "px");
					signInTable.setWidth(width - 8 + "px");
					registerTable.setWidth(width - 8 + "px");
				}
			});
			
			//TODO Force resize layout height on ios device
			
		}
		
		
		// UI layout completion
		String userID = Cookies.getCookie(COOKIE_NAME);
		if (userID == null) loadViewBeforeLoginSuccessful();
		else checkWithServerIfSessionIsStillLegal(userID);

		
		/**
		 * Handling sending action triggered by either clicking "Send" 
		 * button or pressing "Enter" key after typing.
		 * 
		 * @author micro
		 */
		class SendActionHandler implements ClickHandler, KeyUpHandler {

			@Override
			public void onClick(ClickEvent event) {
				String txtToBeSent = messageField.getText();
				if (!FieldVerifier.isEmpty(txtToBeSent)) {
					sendMsgToServer(txtToBeSent);
				}
			}

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					String txtToBeSent = messageField.getText();
					if (!FieldVerifier.isEmpty(txtToBeSent)) {
						sendMsgToServer(txtToBeSent);
					}
				}
			}

			private void sendMsgToServer(String message) {
				displaySentMsgBubble(message);
				
				// send the message to the server
				sendButton.setEnabled(false);
				MessageService.Utils.getInstance().getResponse(message, 
						new AsyncCallback<Message>() {
					
					@Override
					public void onSuccess(Message result) {
						displayReceivedMsgBubble(result.getMessage());
						onceFinishSending();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						displayReceivedMsgBubble(SERVER_ERROR);
//						Window.alert(SERVER_ERROR + "\n" + caught.getMessage());
						onceFinishSending();
					}
				});
				
			}
			
			private void onceFinishSending() {
				sendButton.setEnabled(true);
				if (!IS_MOBILE) {
					messageField.setFocus(true);
					messageField.selectAll();
				} else {
					messageField.setText("");
				}
			}
			
		}
		
		// Add send action handler to "Send" button and response to "Enter" key press
		final SendActionHandler sendhandler = new SendActionHandler();
		sendButton.addClickHandler(sendhandler);
		messageField.addKeyUpHandler(sendhandler);
		
		class SignInActionHandler implements ClickHandler, KeyUpHandler {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					login();
				}
			}

			@Override
			public void onClick(ClickEvent event) {
				login();
			}
			
			private void login() {
				String identifier = usrField.getText();
				String password = pwField.getText();
				SessionControl.Utils.getInstance().login(identifier, password, 
						new AsyncCallback<Account>() {
					
					@Override
					public void onSuccess(Account result) {
						// Verify the result
						if (result.getUid() == 0) {
							displayReceivedMsgBubble("Ummm... maybe try again? Or register now!");
							return;
						}
						loadLoginSuccessfulView(result);
						
						// Reset panels
						resetSignInPanel(showPwCheck, usrField, pwField);
						resetRegisterPanel(showNewPwCheck, agreeCheck, 
								fstNameField, sndNameField,
								emailField, cellphoneField,
								newUsrField, newPwField);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(SERVER_ERROR + "\n" + caught.getMessage());
					}
				});
			}
			
		}
		
		final SignInActionHandler signInHandler = new SignInActionHandler();
		signInButton.addClickHandler(signInHandler);
		pwField.addKeyUpHandler(signInHandler);
		
		forgotPwButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				displayReceivedMsgBubble("Sorry! This service is currently not available.");				
			}
		});
		
		/**
		 * Handling other UI events
		 */
		messageField.addFocusHandler(new FocusHandler() {
			
			@Override
			public void onFocus(FocusEvent event) {
				if (IS_MOBILE) stackLayout.showWidget(chatScrollPanel);
				// ensure that the scroll animation triggered by sending has finished.
				if (!scrollAnimator.isRunning())
					scrollAnimator.scrollToEnd(MAX_SCROLLING_DURATION);
			}
		});
		
		showPwCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				toggleShowPassword(pwField);
			}
		});
		
		showNewPwCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				toggleShowPassword(newPwField);
			}
		});
		
		agreeCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (agreeCheck.getValue()) signUpButton.setEnabled(true);
				else signUpButton.setEnabled(false);
				
			}
		});
		
		
	}
	
	
	private void toggleShowPassword(PasswordTextBox passwdField) {
		String currentType = passwdField.getElement().getAttribute("type");
		if (currentType.contains("password"))
			passwdField.getElement().setAttribute("type", "text");
		if (currentType.contains("text"))
			passwdField.getElement().setAttribute("type", "password");
	}
	

	private void resetSignInPanel(CheckBox showPwCheck, TextBox... fields) {
		showPwCheck.setValue(true);
		fields[0].setText(DEFAULT_TEXT[0]);
		fields[1].setText(DEFAULT_TEXT[1]);
		if (IS_MOBILE) {
			fields[0].addStyleName("mobileDefaultTextFieldText");
			fields[1].addStyleName("mobileDefaultTextFieldText");
		}
		else {
			fields[0].addStyleName("normalDefaultTextFieldText");
			fields[1].addStyleName("normalDefaultTextFieldText");
		}
		signInPanel.setOpen(false);
	}
	
	private void resetRegisterPanel(CheckBox showNewPwCheck, CheckBox agreeCheck, TextBox... fields) {
		showNewPwCheck.setValue(true);
		agreeCheck.setValue(false);
		for (int i = 0; i < 6; i++) {
			fields[i].setText(DEFAULT_TEXT[i+2]);
			if (IS_MOBILE) fields[i].addStyleName("mobileDefaultTextFieldText");
			else fields[i].addStyleName("normalDefaultTextFieldText");
		}
		registerPanel.setOpen(false);
	}
	
	
	private void loadLoginSuccessfulView(Account account) {

		// Display success message
		String fstName = StringUtils.CapitalizeFstLetter(account.getFirstname());
		displayReceivedMsgBubble("Welcome back, " + fstName + "!");
		
		// Update the UI
		if (!IS_MOBILE) {
			accountPanel.clear();
			addOptionsPanel();
		} else {
			stackLayout.remove(signInScrollPanel); 
			stackLayout.remove(registerScrollPanel); 
			addOptionsPanel();
		}
		
		// Set session cookie for 6 hours expiry
		String userID = Integer.toString(account.getUid());
        final long DURATION = 1000 * 60 * 60 * 6 * 1;
        Date expires = new Date(System.currentTimeMillis() + DURATION);
        Cookies.setCookie(COOKIE_NAME, userID, expires, null, "/", false);

		// For debugging
//		displayReceivedMsgBubble(result.toString());
	}
	
	
	private void loadViewBeforeLoginSuccessful() {
		if (!IS_MOBILE) {
			accountPanel.add(signInPanel);
			accountPanel.add(registerPanel);
			accountPanel.setCellHorizontalAlignment(signInPanel, 
					HasHorizontalAlignment.ALIGN_LEFT);
			accountPanel.setCellHorizontalAlignment(registerPanel, 
					HasHorizontalAlignment.ALIGN_LEFT);
			
		} else {
			stackLayout.add(signInScrollPanel, new HTML("Sign In"), 2.4);
			stackLayout.add(registerScrollPanel, new HTML("Register"), 2.4);
			stackLayout.getHeaderWidget(1).setStyleName("customStackPanelHeader");
			stackLayout.getHeaderWidget(2).setStyleName("customStackPanelHeader");
		}
	}
	
	
	private void displaySentMsgBubble(String msg) {
		final Label sentMsgLable = new Label(msg);
		if (!IS_MOBILE) sentMsgLable.setStyleName("normalSentMessageText");
		else {
			// Ensure the visibility on mobile device
			stackLayout.showWidget(chatScrollPanel); 
			sentMsgLable.setStyleName("mobileSentMessageText");
		}
		DecoratorPanel sentBubble = new DecoratorPanel();
		sentBubble.addStyleName("sentBubble");
	    sentBubble.setWidget(sentMsgLable);
		bubbleLayout.add(sentBubble);
		bubbleLayout.setCellHorizontalAlignment(sentBubble, HasHorizontalAlignment.ALIGN_RIGHT);
		scrollAnimator.scrollToEnd(SCROLLING_DURATION);
//		chatScrollPanel.ensureVisible(sentBubble);
		
		// add animation to the message bubble
		fadeAnimator.cancel();
		sentBubble.getElement().getStyle().setOpacity(.01);
		fadeAnimator.setElement(sentBubble);
		fadeAnimator.fade(FADING_DURATION, 1.0);
	}
	
	
	private void displayReceivedMsgBubble(String msg) {
		final Label receivedMsgLable = new Label(msg);
		if (!IS_MOBILE) receivedMsgLable.setStyleName("normalReceivedMessageText");
		else {
			// Ensure the visibility on mobile device
			stackLayout.showWidget(chatScrollPanel);
			receivedMsgLable.setStyleName("mobileReceivedMessageText");
		}
		DecoratorPanel receivedBubble = new DecoratorPanel();
		receivedBubble.addStyleName("receivedBubble");
	    receivedBubble.setWidget(receivedMsgLable);
		bubbleLayout.add(receivedBubble);
		bubbleLayout.setCellHorizontalAlignment(receivedBubble, HasHorizontalAlignment.ALIGN_LEFT);
		scrollAnimator.scrollToEnd(SCROLLING_DURATION);
//		chatScrollPanel.ensureVisible(receivedBubble);
		
		// add animation to the message bubble
		fadeAnimator.cancel();
		receivedBubble.getElement().getStyle().setOpacity(.1);
		fadeAnimator.setElement(receivedBubble);
		fadeAnimator.fade(FADING_DURATION, 1.0);
	}
	
	
	private void initializeTextFields(final TextBox... fields) {
		for (int i = 0; i < fields.length; i++) {
			final int index = i; final TextBox textbox = fields[i];
			
			if (IS_MOBILE) {
				textbox.addStyleName("mobileTextFieldText");
				textbox.addStyleName("mobileDefaultTextFieldText");
			}
			else textbox.addStyleName("normalDefaultTextFieldText");
			textbox.setText(DEFAULT_TEXT[i]);
			textbox.setTitle(DEFAULT_TEXT[i]);

			textbox.addFocusHandler(new FocusHandler() {
				
				@Override
				public void onFocus(FocusEvent event) {
					textbox.selectAll();
					textbox.setText("");
					if (IS_MOBILE) textbox.removeStyleName("mobileDefaultTextFieldText");
					else textbox.removeStyleName("normalDefaultTextFieldText");
				}
			});
			
			textbox.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					String currentText = textbox.getText();
					
					// if empty, reset the default text
					if (currentText.trim().length() < 1) {
						if (IS_MOBILE) textbox.addStyleName("mobileDefaultTextFieldText");
						else textbox.addStyleName("normalDefaultTextFieldText");
						textbox.setText(DEFAULT_TEXT[index]);
					}
					
					// if not empty, validate the content
//					else TODO validate the content
					
				}
			});
			
			// Move to the next field when press "Enter" (except for password fields)
			if (i != 1 && i != 7) {
				textbox.addKeyUpHandler(new KeyUpHandler() {
					@Override
					public void onKeyUp(KeyUpEvent event) {
						if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
							fields[index + 1].setFocus(true);
						}
					}
				});
			}
		}
	}
	
	
	private void checkWithServerIfSessionIsStillLegal(final String userID) {
		
		SessionControl.Utils.getInstance().loginFromSessionServer(
				new AsyncCallback<Account>() {
			
			@Override
			public void onSuccess(Account result) {
				// Verify the result
				if (result.getUid() == 0) {
//					displayReceivedMsgBubble("Session expired! Please sign in again.");
					loadViewBeforeLoginSuccessful();
					return;
				} else {
					if (result.getUid() == Integer.valueOf(userID)) 
						loadLoginSuccessfulView(result);
					else loadViewBeforeLoginSuccessful();
				}
				
				
				// Reset panels
//				resetSignInPanel(showPwCheck, usrField, pwField);
//				resetRegisterPanel(showNewPwCheck, agreeCheck, 
//						fstNameField, sndNameField,
//						emailField, cellphoneField,
//						newUsrField, newPwField);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(SERVER_ERROR + "\n" + caught.getMessage());
				
			}
		});
		
	}
	
	
	private void addOptionsPanel() {
		
		final Button signOutButton = new Button("Sign out");
		signOutButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SessionControl.Utils.getInstance().logout(new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(SERVER_ERROR + "\n" + caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						displayReceivedMsgBubble("See you later, then!");
						if (IS_MOBILE) stackLayout.remove(optionsScrollPanel);
						else accountPanel.clear();
						loadViewBeforeLoginSuccessful();
					}
				});
			}
		});
		
		final Button changePwButton = new Button("Change Password");
		changePwButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				displayReceivedMsgBubble("Sorry! This service is currently not available.");
			}
		});
		
		final Button exportButton = new Button("Export History");
		exportButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				displayReceivedMsgBubble("Sorry! This service is currently not available.");
			}
		});
		
		final FlexTable signOutTable = new FlexTable();
		signOutTable.setCellSpacing(6);
		
		final FlexCellFormatter signOutTableFormatter = 
				signOutTable.getFlexCellFormatter();
		signOutTable.setWidget(0, 0, changePwButton);
		signOutTable.setWidget(0, 1, signOutButton);
		signOutTable.setWidget(1, 0, exportButton);
		signOutTableFormatter.setHorizontalAlignment(0, 1, 
				HasHorizontalAlignment.ALIGN_RIGHT);
		
		if (!IS_MOBILE) {
			
//			accountPanel.add(signOutTable);
			accountPanel.setCellHorizontalAlignment(signOutTable, 
					HasHorizontalAlignment.ALIGN_RIGHT);
			
			accountPanel.add(exportButton);
			accountPanel.add(changePwButton);
			accountPanel.add(signOutButton);
			
		} else {
			
			optionsScrollPanel.add(signOutTable);
			optionsScrollPanel.ensureDebugId("signOutScrollPanel");
			
			stackLayout.add(optionsScrollPanel, new HTML("Options"), 2.4);
			
			/**
			 * <--- Styling on mobile device --->
			 */
			signOutButton.addStyleName("mobileSendButton");
			exportButton.addStyleName("mobileSendButton");
			changePwButton.addStyleName("mobileSendButton");
			
			stackLayout.getHeaderWidget(1).setStyleName("customStackPanelHeader");
			
			/**
			 * <--- Sizing on mobile device --->
			 */
			int width = Window.getClientWidth() - 9;
			optionsScrollPanel.setWidth(width + "px");
			signOutTable.setWidth(width - 8 + "px");
			
			/**
			 * Handle window resizing event on mobile device
			 */
			Window.addResizeHandler(new ResizeHandler() {
				
				@Override
				public void onResize(ResizeEvent event) {
					// extra resizing width
					int width = Window.getClientWidth() - 9;
					optionsScrollPanel.setWidth(width + "px");
					signOutTable.setWidth(width - 8 + "px");
				}
			});
		}
	}
	
	
	
	
}
