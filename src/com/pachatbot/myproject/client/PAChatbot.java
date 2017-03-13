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
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
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
			"password", "first name", "last name", 
			"email address (optional)", "cellphone number (optional)",
			"username", "password"};
	
	private static final String[] TITLE_TEXT = {"username or email or cellphone",
			"password", "first name", "last name", 
			"email address (optional)", "cellphone number (optional)",
			"5-20 letters, numbers and _-", 
			"8-30 letters, numbers and _-"};
	
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
	final VerticalPanel westLayout = new VerticalPanel();
	final DisclosurePanel signInDiscPanel = new DisclosurePanel("Sign In");
	final DisclosurePanel registerDiscPanel = new DisclosurePanel("Register");
	final DisclosurePanel optionsDiscPanel = new DisclosurePanel("Options");
	
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
		
		errorLabel.setWidth("100%");
		errorLabel.addStyleName("errorLabel");
		
		/**
		 *  Dialogue UI
		 */
		final TextBox messageField = new TextBox();
		messageField.setSize("97%", "1.2em");
		final Button sendButton = new Button("Send");
		
		final HorizontalPanel sendPanel = new HorizontalPanel();
		sendPanel.setSize("100%", "100%");
		sendPanel.setSpacing(5);
		sendPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		sendPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		sendPanel.add(messageField);
		sendPanel.add(sendButton);
		sendPanel.setCellWidth(sendButton, "3%");
		sendPanel.ensureDebugId("sendPanel");
		
		bubbleLayout.setSize("100%", "100%");
		bubbleLayout.setSpacing(5);
		bubbleLayout.addStyleName("customVerticalPanel");
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
		signInTable.setWidget(0, 0, usrField);
		signInTable.setWidget(1, 0, pwField);
		signInTable.setWidget(2, 0, showPwCheck);
		signInTable.setWidget(3, 0, forgotPwButton);
		signInTable.setWidget(3, 1, signInButton);
		
		final FlexCellFormatter signInTableFormatter = 
				signInTable.getFlexCellFormatter();
		signInTableFormatter.setColSpan(0, 0, 2);
		signInTableFormatter.setColSpan(1, 0, 2);
		signInTableFormatter.setColSpan(2, 0, 2);
		
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
		final TextBox lastNameField = new TextBox();
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
		
//		final ListBox countryCodeList = new ListBox();
//		countryCodeList.setSize("50px", "2em");/
//		countryCodeList.addItem("FR(+33)", "+33");
//		final Grid cellphoneGrid = new Grid(1,2);
//		cellphoneGrid.setWidget(0, 0, countryCodeList);
//		cellphoneGrid.setWidget(0, 1, cellphoneField);
		
		final Button signUpButton = new Button("Sign Up");
		signUpButton.setEnabled(false);
		
		final FlexTable registerTable = new FlexTable();
		registerTable.setCellSpacing(6);
		registerTable.setWidget(0, 0, fstNameField);
		registerTable.setWidget(0, 1, lastNameField);
		registerTable.setWidget(1, 0, emailField);
		registerTable.setWidget(2, 0, cellphoneField);
		registerTable.setWidget(3, 0, newUsrField);
		registerTable.setWidget(4, 0, newPwField);
		registerTable.setWidget(5, 0, agreeCheck);
		registerTable.setWidget(6, 0, showNewPwCheck);
		registerTable.setWidget(7, 1, signUpButton);

		final FlexCellFormatter registerTableCellFormatter =
				registerTable.getFlexCellFormatter();
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
		 *  Options UI
		 */
		final Button signOutButton = new Button("Sign out");
		final Button clearButton = new Button("Clear history");
		final Button changePwButton = new Button("Change Password");
		final Button exportButton = new Button("Export History");
		
		final FlexTable optionsTable = new FlexTable();
		optionsTable.setCellSpacing(6);
		
		final FlexCellFormatter signOutTableFormatter = 
				optionsTable.getFlexCellFormatter();
		optionsTable.setWidget(0, 0, changePwButton);
		optionsTable.setWidget(0, 1, signOutButton);
		optionsTable.setWidget(1, 0, exportButton);
		optionsTable.setWidget(2, 0, clearButton);
		signOutTableFormatter.setHorizontalAlignment(0, 1, 
				HasHorizontalAlignment.ALIGN_RIGHT);
		
		TextBox[] fields = {usrField, pwField, fstNameField, lastNameField,
				emailField, cellphoneField, newUsrField, newPwField};
		
		initializeTextFields(fields);
		
		final VerticalPanel northLayout = new VerticalPanel();
		northLayout.setSize("100%", "100%");
		northLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		northLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		northLayout.add(new HTML("<h1>Hi, &pi;-chatbot!</h1>"));
		northLayout.ensureDebugId("northPanel");
		
		if (!IS_MOBILE) {
			
			final ScrollPanel westScrollPanel = new ScrollPanel();
			westScrollPanel.add(westLayout);
			westScrollPanel.ensureDebugId("westScrollPanel");
			
			westLayout.setSpacing(5);
			westLayout.addStyleName("customVerticalPanel");
			westLayout.ensureDebugId("westPanel");
			
			signInDiscPanel.setAnimationEnabled(true);
			signInDiscPanel.setOpen(false);
			signInDiscPanel.setContent(signInTable);
			signInDiscPanel.ensureDebugId("signInDiscPanel");
			
			registerDiscPanel.setAnimationEnabled(true);
			registerDiscPanel.setOpen(false);
			registerDiscPanel.setContent(registerTable);
			registerDiscPanel.ensureDebugId("registerDiscPanel");
			
			optionsDiscPanel.setAnimationEnabled(true);
			optionsDiscPanel.setOpen(false);
			optionsDiscPanel.setContent(optionsTable);
			optionsDiscPanel.ensureDebugId("optionsDiscPanel");
			
			/**
			 *  <--- Styling on PC --->
			 */
			messageField.addStyleName("normalButton");
			sendButton.addStyleName("normalButton");
			
//			signInDiscPanel.getHeader().addStyleName("disclosurePanelHeader");
//			registerDiscPanel.getHeader().setStyleName("disclosurePanelHeader");
//			optionsDiscPanel.getHeader().setStyleName("disclosurePanelHeader");
			
			showPwCheck.addStyleName("normalDisplayText");
			agreeCheck.addStyleName("normalDisplayText");
			showNewPwCheck.addStyleName("normalDisplayText");
			
			/**
			 * 	<--- Sizing on PC --->
			 */
			westLayout.setSize("100%", "100%");
			chatScrollPanel.setSize("100%", "100%");
			westScrollPanel.setSize("100%", "100%");
			
			usrField.setWidth("220px");
			pwField.setWidth("220px");
			fstNameField.setWidth("100px");
			lastNameField.setWidth("100px");
			emailField.setWidth("220px");
			cellphoneField.setWidth("220px");
			newUsrField.setWidth("220px");
			newPwField.setWidth("220px");

			signInDiscPanel.setWidth("100%");
			registerDiscPanel.setWidth("100%");
			optionsDiscPanel.setWidth("100%");
			
			signInTable.setWidth("250px");
			registerTable.setWidth("250px");
			optionsTable.setWidth("250px");
			
			/**
			 *  Main panel layout on PC
			 */
			final DockLayoutPanel dockLayout = new DockLayoutPanel(Unit.PX);
			dockLayout.ensureDebugId("dockLayoutOnPC");
//			dockLayout.addStyleName("dock"); // For debugging
			dockLayout.addNorth(northLayout, 128);
			dockLayout.addSouth(errorLabel, 96);
			dockLayout.addWest(westScrollPanel, 128);
			dockLayout.addSouth(sendPanel, 48);
			dockLayout.add(chatScrollPanel);
			
			// Add the main dock panel to the Root panel 
			RootLayoutPanel.get().add(dockLayout);
			RootLayoutPanel.get().setWidgetLeftRight(dockLayout, 5, Unit.PCT, 5, Unit.PCT);
			
			/**
			 * On-startup settings on PC
			 */
			messageField.setText("Hello, Pi!");
			// Focus the cursor on the name field when the app loads
			messageField.setFocus(true);
			messageField.selectAll();
			
			/**
			 * Handle disclosure panel open and close events
			 */
			class WestDiscPanelsHandler implements OpenHandler<DisclosurePanel>, CloseHandler<DisclosurePanel> {
				@Override
				public void onClose(CloseEvent<DisclosurePanel> event) {
					for (int i = 0; i < westLayout.getWidgetCount(); i++) {
						if (westLayout.getWidget(i) instanceof DisclosurePanel) {
							DisclosurePanel panel = (DisclosurePanel) westLayout.getWidget(i);
							if (panel.isOpen()) return;
						}
					}
					dockLayout.setWidgetSize(westScrollPanel, 128);
					dockLayout.animate(350);
				}
				@Override
				public void onOpen(OpenEvent<DisclosurePanel> event) {
					dockLayout.setWidgetSize(westScrollPanel, 290);
					dockLayout.animate(350);
				}
			}
			
			WestDiscPanelsHandler westHandler = new WestDiscPanelsHandler();
			signInDiscPanel.addOpenHandler(westHandler);
			signInDiscPanel.addCloseHandler(westHandler);
			registerDiscPanel.addOpenHandler(westHandler);
			registerDiscPanel.addCloseHandler(westHandler);
			optionsDiscPanel.addOpenHandler(westHandler);
			optionsDiscPanel.addCloseHandler(westHandler);

		} else {
			
			signInScrollPanel.add(signInTable);
			signInScrollPanel.ensureDebugId("signInScrollPanel");
			
			registerScrollPanel.add(registerTable);
			registerScrollPanel.ensureDebugId("registerScrollPanel");
			
			optionsScrollPanel.add(optionsTable);
			optionsScrollPanel.ensureDebugId("optionsScrollPanel");
			
			stackLayout.add(chatScrollPanel, new HTML("Dialogue"), 2.4);
			stackLayout.showWidget(chatScrollPanel);
			stackLayout.ensureDebugId("stackLayout");
			
			/**
			 *  <--- Styling on mobile device --->
			 */
			messageField.addStyleName("mobileButton");
			sendButton.addStyleName("mobileButton");
			
			forgotPwButton.addStyleName("mobileButton");
			signInButton.addStyleName("mobileButton");
			signUpButton.addStyleName("mobileButton");
			
			signOutButton.addStyleName("mobileButton");
			exportButton.addStyleName("mobileButton");
			clearButton.addStyleName("mobileButton");
			changePwButton.addStyleName("mobileButton");
			
			showPwCheck.setStyleName("mobileDisplayText");
			agreeCheck.setStyleName("mobileDisplayText");
			showNewPwCheck.setStyleName("mobileDisplayText");
			
			stackLayout.getHeaderWidget(0).setStyleName("customStackPanelHeader");
			
			/**
			 *  <--- Sizing on mobile device --->
			 */
			usrField.setWidth("98%");
			pwField.setWidth("98%");
			fstNameField.setWidth("95%");
			lastNameField.setWidth("95%");
			emailField.setWidth("98%");
			cellphoneField.setWidth("98%");
			newUsrField.setWidth("98%");
			newPwField.setWidth("98%");
			
			int width = Window.getClientWidth();
			chatScrollPanel.setWidth(width + "px");
			
			signInScrollPanel.setWidth(width - 9 + "px");
			registerScrollPanel.setWidth(width - 9 + "px");
			optionsScrollPanel.setWidth(width - 9 + "px");
			
			signInTable.setWidth(width - 17 + "px");
			registerTable.setWidth(width - 17 + "px");
			optionsTable.setWidth(width - 17 + "px");
			
			/**
			 *  Main panel layout on mobile device
			 */
			final DockLayoutPanel dockLayout = new DockLayoutPanel(Unit.EM);
			dockLayout.ensureDebugId("dockLayoutOnMobileDevice");
//			dockLayout.addStyleName("dock"); // For debugging
			dockLayout.addNorth(northLayout, 4);
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
					int width = Window.getClientWidth();
					chatScrollPanel.setWidth(width + "px");
					
					signInScrollPanel.setWidth(width - 9 + "px");
					registerScrollPanel.setWidth(width - 9 + "px");
					optionsScrollPanel.setWidth(width - 9 + "px");
					
					signInTable.setWidth(width - 17 + "px");
					registerTable.setWidth(width - 17 + "px");
					optionsTable.setWidth(width - 17 + "px");
				}
			});
			
			//TODO Force resize layout height on ios device
			
		}
		
		// UI layout completion
		String userID = Cookies.getCookie(COOKIE_NAME);
		if (userID == null) loadLogoutSuccessfulView();
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
					pwField.setFocus(false);
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
				
				// Check if empty or default, do nothing
				if (identifier.trim().length() < 1 || password.trim().length() < 1
						|| usrField.getStyleName().toLowerCase().contains("default")
						|| pwField.getStyleName().toLowerCase().contains("default")) {
					return;
				}
				
				// Check if valid, send a warning message
				if (!FieldVerifier.isValidUserForSignIn(identifier) 
						|| !FieldVerifier.Password.isValid(password)) {
					displayReceivedMsgBubble("Invalid username or password!");
					return;
				}
				
				// Verify the combination
				SessionControl.Utils.getInstance().login(identifier, password, 
						new AsyncCallback<Account>() {
					
					@Override
					public void onSuccess(Account result) {
						
						// Verify the result
						if (result.getUid() == 0) {
							displayReceivedMsgBubble("Ummm... maybe try again? Or register now!");
							return;
						}
						
						// Clear bubbles
//						bubbleLayout.clear();
						
						// Load view after successfully logged in
						loadLoginSuccessfulView(result);
						
						// Display sign in success message
						String fstName = StringUtils.CapitalizeFstLetter(result.getFirstname());
						displayReceivedMsgBubble("Welcome back, " + fstName + "!");
						
						// Reset panels
						resetSignInPanel(showPwCheck, usrField, pwField);
						resetRegisterPanel(showNewPwCheck, agreeCheck, 
								fstNameField, lastNameField,
								emailField, cellphoneField,
								newUsrField, newPwField);
						
						// Set session cookie for 8 hours expiry
						String userID = String.valueOf(result.getUid());
				        final long DURATION = 1000 * 60 * 60 * 8 * 1;
				        Date expires = new Date(System.currentTimeMillis() + DURATION);
				        Cookies.setCookie(COOKIE_NAME, userID, expires, null, "/", false);
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
		
		class SignUpActionHandler implements ClickHandler, KeyUpHandler {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					newPwField.setFocus(false);
					if (agreeCheck.getValue()) 
						register();
				}
			}

			@Override
			public void onClick(ClickEvent event) {
				register();
			}
			
			private void register() {
				String first_name = fstNameField.getText().trim();
				String last_name = lastNameField.getText().trim();
				String new_username = newUsrField.getText();
				String new_password = newPwField.getText();
				
				String[] mandatory = {first_name, last_name, new_username, new_password};
				
				// mandatory fields should not be empty
				for (String str : mandatory) {
					if (str.length() < 1) return;
				}
				
				// mandatory fields should not be "default"
				if (fstNameField.getStyleName().toLowerCase().contains("default")
						|| lastNameField.getStyleName().toLowerCase().contains("default")
						|| newUsrField.getStyleName().toLowerCase().contains("default")
						|| newPwField.getStyleName().toLowerCase().contains("default")) {
					return;
				}
				
				// Validate the mandatory fields
				if (!FieldVerifier.Firstname.isValid(first_name)
						|| !FieldVerifier.Lastname.isValid(last_name)
						|| !FieldVerifier.Username.isValid(new_username)
						|| !FieldVerifier.Password.isValid(new_password)) {
					displayReceivedMsgBubble("Invalid inputs!");
					return;
				}
				
				String email_address = emailField.getText().trim();
				String cell_number = cellphoneField.getText().trim();
				
				// if optional fields are "default", set their values as empty string
				if (emailField.getStyleName().toLowerCase().contains("default"))
					email_address = "";
				if (cellphoneField.getStyleName().toLowerCase().contains("default"))
					cell_number = "";
				
				// register new user in database
				SessionControl.Utils.getInstance().register(first_name, last_name, 
						email_address, cell_number, 
						new_username, new_password, 
						new AsyncCallback<Account>() {
					
					@Override
					public void onSuccess(Account result) {
						// Verify the result
						if (result.getUid() == 0) {
							displayReceivedMsgBubble("Ummm... maybe try again?");
							return;
						}
						
						// Clear bubbles
//						bubbleLayout.clear();
						
						// Load view after successfully logged in
						loadLoginSuccessfulView(result);
						
						// Display sing in success message
						String fstName = StringUtils.CapitalizeFstLetter(result.getFirstname());
						displayReceivedMsgBubble("Nice to meet you, " + fstName + "!");
						
						// Reset panels
						resetSignInPanel(showPwCheck, usrField, pwField);
						resetRegisterPanel(showNewPwCheck, agreeCheck, 
								fstNameField, lastNameField,
								emailField, cellphoneField,
								newUsrField, newPwField);
						
						// Set session cookie for 8 hours expiry
						String userID = String.valueOf(result.getUid());
				        final long DURATION = 1000 * 60 * 60 * 8 * 1;
				        Date expires = new Date(System.currentTimeMillis() + DURATION);
				        Cookies.setCookie(COOKIE_NAME, userID, expires, null, "/", false);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(SERVER_ERROR + "\n" + caught.getMessage());
					}
				});
				
			}
			
		}
		
		final SignUpActionHandler signUpHandler = new SignUpActionHandler();
		signUpButton.addClickHandler(signUpHandler);
		newPwField.addKeyUpHandler(signUpHandler);
		
		/**
		 * Handling button click events
		 */
		forgotPwButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				displayReceivedMsgBubble("Sorry! This service is currently not available.");				
			}
		});
		
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
						
						// Clear bubbles
//						bubbleLayout.clear();
						
						// Display sign out success message
						displayReceivedMsgBubble("See you later, then!");
						
						// Load view after successfully logged out
						loadLogoutSuccessfulView();
						
						// Reset panels
						resetOptionsPanel();
					}
				});
			}
		});
		
		changePwButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				displayReceivedMsgBubble("Sorry! This service is currently not available.");
			}
		});
		
		clearButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				bubbleLayout.clear();
				displayReceivedMsgBubble("Yeah! I feel very \"clean\", now.");
			}
		});
		
		exportButton.addClickHandler(new ClickHandler() {
			
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
		signInDiscPanel.setOpen(false);
	}
	
	private void resetOptionsPanel() {
		optionsDiscPanel.setOpen(false);
	}
	
	private void resetRegisterPanel(CheckBox showNewPwCheck, CheckBox agreeCheck, TextBox... fields) {
		showNewPwCheck.setValue(true);
		agreeCheck.setValue(false);
		for (int i = 0; i < 6; i++) {
			fields[i].setText(DEFAULT_TEXT[i+2]);
			if (IS_MOBILE) fields[i].addStyleName("mobileDefaultTextFieldText");
			else fields[i].addStyleName("normalDefaultTextFieldText");
		}
		registerDiscPanel.setOpen(false);
	}
	
	
	private void loadLoginSuccessfulView(Account account) {

		// Update the UI
		if (!IS_MOBILE) {
			westLayout.clear();
			westLayout.add(optionsDiscPanel);
		} else {
			stackLayout.remove(signInScrollPanel); 
			stackLayout.remove(registerScrollPanel); 
			stackLayout.add(optionsScrollPanel, new HTML("Options"), 2.4);
			stackLayout.getHeaderWidget(1).setStyleName("customStackPanelHeader");
		}

	}
	
	
	private void loadLogoutSuccessfulView() {
		
		// Update the UI
		if (!IS_MOBILE) {
			westLayout.clear();
			westLayout.add(signInDiscPanel);
			westLayout.add(registerDiscPanel);
			
		} else {
			stackLayout.remove(optionsScrollPanel);
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
	
	
	private void initializeTextFields(final TextBox[] fields) {
		for (int i = 0; i < fields.length; i++) {
			final int index = i; final TextBox textbox = fields[i];
			
			if (IS_MOBILE) {
				textbox.addStyleName("mobileTextFieldText");
				textbox.addStyleName("mobileDefaultTextFieldText");
			}
			else textbox.addStyleName("normalDefaultTextFieldText");
			textbox.setText(DEFAULT_TEXT[i]);
			textbox.setTitle(TITLE_TEXT[i]);

			textbox.addFocusHandler(new FocusHandler() {
				
				@Override
				public void onFocus(FocusEvent event) {
					if (textbox.getStyleName().toLowerCase().contains("default")) 
						textbox.setText("");
					if (IS_MOBILE) textbox.removeStyleName("mobileDefaultTextFieldText");
					else textbox.removeStyleName("normalDefaultTextFieldText");
//					textbox.selectAll();
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
					else {
						switch (index) {
						case 0: //username or email or cellphone
							if (!FieldVerifier.isValidUserForSignIn(currentText)) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("This may not be a valid username or email address or cellphone number.");
							} else if (textbox.getStyleName().toLowerCase().contains("error")) textbox.removeStyleName("errorTextField");
							break;

						case 1: //password
							if (!FieldVerifier.Password.isValid(currentText)) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your password is invalid.");
							} else if (textbox.getStyleName().toLowerCase().contains("error")) textbox.removeStyleName("errorTextField");
							break;
							
						case 2: //first name
							if (currentText.trim().length() > 30) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your first name is too long. It should contain no more than 30 characters.");
							} 
							else if (!FieldVerifier.Firstname.isValid(currentText)) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your first name should contain only letters, spaces and ’.'-");
							} 
							else if (textbox.getStyleName().toLowerCase().contains("error")) textbox.removeStyleName("errorTextField");
							break;
							
						case 3: //last name
							if (currentText.trim().length() > 60) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your last name is too long. It should contain no more than 60 characters.");
							} 
							else if (!FieldVerifier.Lastname.isValid(currentText)) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your last name should contain only letters, spaces and ’.'-");
							} 
							else if (textbox.getStyleName().toLowerCase().contains("error")) textbox.removeStyleName("errorTextField");
							break;
						
						case 4: //email address
							if (!FieldVerifier.Email.isValid(currentText)) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("This is not a valid email address.");
							} 
							else if (textbox.getStyleName().toLowerCase().contains("error")) textbox.removeStyleName("errorTextField");
							break;
						
						case 5: //cellphone number
							if (!FieldVerifier.Cellphone.isValid(currentText)) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("This is not a valid cellphone number.");
							} 
							else if (textbox.getStyleName().toLowerCase().contains("error")) textbox.removeStyleName("errorTextField");
							break;
						
						case 6: // username
							if (currentText.length() < 5) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your username must contain at least 5 characters.");
							} 
							else if (currentText.length() > 20) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your username must have at most 20 characters.");
							}
							else if (!FieldVerifier.Username.isValid(currentText)) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your username should contain only letters, numbers, \"_\" and \"-\".");
							} 
							else if (textbox.getStyleName().toLowerCase().contains("error")) textbox.removeStyleName("errorTextField");
							break;
						
						case 7: //password
							if (currentText.length() < 8) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your password must contain at least 8 characters.");
							}
							else if (currentText.length() > 30) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your password must have at most 30 characters.");
							}
							else if (!FieldVerifier.Password.isValid(currentText)) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your password should contain only letters, numbers, \"_\" and \"-\".");
							}
							else if (textbox.getStyleName().toLowerCase().contains("error")) textbox.removeStyleName("errorTextField");
							break;
						
						default:
							break;
						}
					}
					
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
					loadLogoutSuccessfulView();
					return;
				}
				
				if (result.getUid() == Integer.valueOf(userID)) {
					// Load view after successfully logged in
					loadLoginSuccessfulView(result);
					
					// Display sign in success message
					String fstName = StringUtils.CapitalizeFstLetter(result.getFirstname());
					displayReceivedMsgBubble("Welcome back, " + fstName + "!");
				}
					
				else loadLogoutSuccessfulView();
				
				// Reset panels
//				resetSignInPanel(showPwCheck, usrField, pwField);
//				resetRegisterPanel(showNewPwCheck, agreeCheck, 
//						fstNameField, lastNameField,
//						emailField, cellphoneField,
//						newUsrField, newPwField);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(SERVER_ERROR + "\n" + caught.getMessage());
				
			}
		});
		
	}
	
	
}
