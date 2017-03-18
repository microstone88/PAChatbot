package com.pachatbot.myproject.client;

import java.util.Arrays;
import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.pachatbot.myproject.client.UI.PopupConfirmBox;
import com.pachatbot.myproject.client.animation.FadeAnimation;
import com.pachatbot.myproject.client.animation.ScrollAnimation;
import com.pachatbot.myproject.shared.FieldVerifier;
import com.pachatbot.myproject.shared.PreDefined.TInfo;
import com.pachatbot.myproject.shared.PreDefined.TInfo.Column;
import com.pachatbot.myproject.shared.PreDefined.UCivility;
import com.pachatbot.myproject.shared.PreDefined.UGroup;
import com.pachatbot.myproject.shared.PreDefined.ULocale;
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
	
	private static final String[] FIELD_DEFAULT_TEXT = {"username or email or cellphone",
			"password", "first name", "last name", 
			"email address (optional)", "cellphone number (optional)",
			"username", "password",
			"current password", "new password    "};
	
	private static final String[] FIELD_TITLE_TEXT = {"username or email or cellphone",
			"password", "first name", "last name", 
			"email address (optional)", "cellphone number (optional)",
			"5-20 letters, numbers and _-", "8-30 letters, numbers and _-", 
			"8-30 letters, numbers and _-",	"8-30 letters, numbers and _-"};
	
	private static final String[] CURRENT_USER = {};
	
	private static final String[] EDITOR_DEFAULT_TEXT = {"Enter your email address", "Enter your cellphone number", 
			"Enter your PayPal ID", "Enter your Alipay ID", "Enter your WeChat ID"};
	
	private static final String[] EDITOR_TITLE_TEXT = {"email address", "cellphone number", 
			"PayPal account", "Alipay account", "WeChat account"};
	
	private static final String[] EDITOR_CHECK_TEXT = {" e-mail address", " cellphone number",
			" PayPal ID", " Alipay ID", " WeChat ID"};
	
	/**
	 * Animation duration configuration
	 */
	private static final int FADING_DURATION = 500;
	private static final int MAX_FADING_DURATION = 2000;
	private static final int SCROLLING_DURATION = 200;
	private static final int MAX_SCROLLING_DURATION = 800;
	private static final int FADING_DELAY = 3000;
	
	/**
	 * Cookie configuration
	 */
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
	final DisclosurePanel myAccountDiscPanel = new DisclosurePanel("My Account");
	
	/**
	 * Panels for layout on mobile device
	 */
	final StackLayoutPanel stackLayout = new StackLayoutPanel(Unit.EM);
	final ScrollPanel signInScrollPanel = new ScrollPanel();
	final ScrollPanel registerScrollPanel = new ScrollPanel();
	final ScrollPanel optionsScrollPanel = new ScrollPanel();
	final ScrollPanel myAccountScrollPanel = new ScrollPanel();
	
	/**
	 * Information labels
	 */
	final Label nameLabel = new Label("", false);
	
	final FlexTable accInfoTable = new FlexTable();
	final Label label3 = new Label("User group:", false);
	final Label userGroupLabel = new Label("", false);
	
	final Label lastActiveLabel = new Label("", false);
	final Label lastIpAddrLabel = new Label("", false);
	
	final ListBox chooseCivility = new ListBox();
	final ListBox chooseLanguage = new ListBox();
	
	/**
	 * Widgets for update user information
	 */
	final HorizontalPanel[] updatePanels = {};
	final VerticalPanel[] updateTabs = {};
	
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
		registerTableCellFormatter.setColSpan(5, 0, 2);
		registerTableCellFormatter.setColSpan(6, 0, 2);
		
		registerTableCellFormatter.setHorizontalAlignment(0, 0,
				HasHorizontalAlignment.ALIGN_CENTER);
		registerTableCellFormatter.setHorizontalAlignment(0, 1,
				HasHorizontalAlignment.ALIGN_CENTER);
		registerTableCellFormatter.setHorizontalAlignment(7, 1, 
				HasHorizontalAlignment.ALIGN_RIGHT);
		
		for (int i = 1; i <= 4; i++) {
			registerTableCellFormatter.setColSpan(i, 0, 2);
			registerTableCellFormatter.setHorizontalAlignment(i, 0, 
					HasHorizontalAlignment.ALIGN_CENTER);
		}
		
		/**
		 *  Options UI
		 */
		final VerticalPanel optionsHolder = new VerticalPanel();
		optionsHolder.setSpacing(2);
		optionsHolder.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		final Button clearButton = new Button("Clear History");
		final Button exportButton = new Button("Export History");
		
		final FlexTable historyTable = new FlexTable();
		historyTable.setCellSpacing(6);
		historyTable.setWidget(0, 0, clearButton);
		historyTable.setWidget(0, 1, exportButton);
		
		final FlexCellFormatter historyTableFormatter = 
				historyTable.getFlexCellFormatter();
		historyTableFormatter.setHorizontalAlignment(0, 1, 
				HasHorizontalAlignment.ALIGN_RIGHT);
		
		final Label label1 = new Label("Civility:", false);
		final Label label2 = new Label("Language:", false);
		
		for (UCivility civil : UCivility.values()) 
			chooseCivility.addItem(civil.toString(), civil.name());
		for (ULocale loc : ULocale.values()) 
			chooseLanguage.addItem(loc.toString(), loc.name());
		
		final ListBox[] chooselists = {chooseCivility, chooseLanguage};
		
		final FlexTable chooseTable = new FlexTable();
		chooseTable.setCellSpacing(6);
		chooseTable.setWidget(0, 0, label1);
		chooseTable.setWidget(0, 1, chooseCivility);
		chooseTable.setWidget(1, 0, label2);
		chooseTable.setWidget(1, 1, chooseLanguage);
		
		final FlexCellFormatter chooseTableFormatter = 
				chooseTable.getFlexCellFormatter();
		chooseTableFormatter.setWidth(0, 1, "70%");
		
		final CheckBox[] editChecks = {};
		final TextBox[] updateFields = {};
		final Button[] updateButtons = {};
		
		for (int i = 0; i < 5; i++) {
			editChecks[i] = new CheckBox();
			updateFields[i] = new TextBox();
			updateButtons[i] = new Button("Apply");
			
			updatePanels[i] = new HorizontalPanel();
			updatePanels[i].setWidth("100%");
			updatePanels[i].setSpacing(3);
			updatePanels[i].setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			updatePanels[i].setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			updatePanels[i].add(updateFields[i]);
			updatePanels[i].add(updateButtons[i]);
			updatePanels[i].setCellWidth(updateButtons[i], "5%");
			
			updateTabs[i] = new VerticalPanel();
			updateTabs[i].setSpacing(3);
			updateTabs[i].add(editChecks[i]);
			
		}
		
		final Button op_removeButton = new Button("Remove Selected");
		op_removeButton.setEnabled(false);
		
		final HTML line1 = newHTMLSplitLine("99%");
		final HTML line2 = newHTMLSplitLine("99%");
		
		optionsHolder.add(historyTable);
		optionsHolder.add(line1);
		optionsHolder.add(chooseTable);
		optionsHolder.add(line2);
		optionsHolder.add(updateTabs[2]);
		optionsHolder.add(updateTabs[3]);
		optionsHolder.add(updateTabs[4]);
		optionsHolder.add(op_removeButton);
		
		optionsHolder.setCellHeight(op_removeButton, "42px");
		optionsHolder.setCellHorizontalAlignment(op_removeButton, 
				HasHorizontalAlignment.ALIGN_RIGHT);
		
		/**
		 *  My Account UI
		 */
		final VerticalPanel myAccountHolder = new VerticalPanel();
		myAccountHolder.setSpacing(2);
		myAccountHolder.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		final Label label4 = new Label("Last active:", false);
		final Label label5 = new Label("Last IP:", false);
		
		Label[] labels = {label1, label2, nameLabel, label3, userGroupLabel, 
				label4, lastActiveLabel,
				label5, lastIpAddrLabel};
		
		final Button signOutButton = new Button("Sign out");
		
		final FlexTable signOutTable = new FlexTable();
		signOutTable.setCellSpacing(6);
		signOutTable.setWidget(0, 0, nameLabel);
		signOutTable.setWidget(0, 1, signOutButton);
		
		final FlexCellFormatter signOutTableFormatter = 
				signOutTable.getFlexCellFormatter();
		signOutTableFormatter.setHorizontalAlignment(0, 1, 
				HasHorizontalAlignment.ALIGN_RIGHT);
		
		accInfoTable.setCellSpacing(6);
		accInfoTable.setWidget(0, 0, label4);
		accInfoTable.setWidget(0, 1, lastActiveLabel);
		accInfoTable.setWidget(1, 0, label5);
		accInfoTable.setWidget(1, 1, lastIpAddrLabel);
		
		final PasswordTextBox ma_currentPwField = new PasswordTextBox();
		ma_currentPwField.getElement().setAttribute("type", "text");
		final PasswordTextBox ma_newPwField = new PasswordTextBox();
		ma_newPwField.getElement().setAttribute("type", "text");
		final CheckBox ma_showNewPwCheck = new CheckBox(" Show password");
		ma_showNewPwCheck.setValue(true);
		final Button changePwButton = new Button("Change Password");
		
		final FlexTable changePwTable = new FlexTable();
		changePwTable.setCellSpacing(6);
		
		changePwTable.setWidget(0, 0, ma_currentPwField);
		changePwTable.setWidget(1, 0, ma_newPwField);
		changePwTable.setWidget(2, 0, ma_showNewPwCheck);
		changePwTable.setWidget(3, 1, changePwButton);
		
		final FlexCellFormatter changePwTableFormatter = 
				changePwTable.getFlexCellFormatter();
		changePwTableFormatter.setColSpan(0, 0, 2);
		changePwTableFormatter.setColSpan(1, 0, 2);
		changePwTableFormatter.setColSpan(2, 0, 2);
		changePwTableFormatter.setHorizontalAlignment(3, 1, 
				HasHorizontalAlignment.ALIGN_RIGHT);
		
		final Button ma_removeButton = new Button("Remove Selected");
		ma_removeButton.setEnabled(false);
		
		final HTML line3 = newHTMLSplitLine("99%");
		final HTML line4 = newHTMLSplitLine("99%");
		final HTML line5 = newHTMLSplitLine("99%");
		
		myAccountHolder.add(signOutTable);
		myAccountHolder.add(line3);
		myAccountHolder.add(accInfoTable);
		myAccountHolder.add(line4);
		myAccountHolder.add(changePwTable);
		myAccountHolder.add(line5);
		myAccountHolder.add(updateTabs[0]);
		myAccountHolder.add(updateTabs[1]);
		myAccountHolder.add(ma_removeButton);
		
		myAccountHolder.setCellHeight(ma_removeButton, "42px");
		myAccountHolder.setCellHorizontalAlignment(ma_removeButton, 
				HasHorizontalAlignment.ALIGN_RIGHT);
		
		TextBox[] fields = {usrField, pwField, fstNameField, lastNameField,
				emailField, cellphoneField, newUsrField, newPwField,
				ma_currentPwField, ma_newPwField};
		initializeTextFields(fields);
		
		initializeUpdateTabs(editChecks, updateFields, updateButtons, 
				ma_removeButton, op_removeButton);
		
		/**
		 * Pop-up confirmation UI
		 */
		final PopupConfirmBox confirmSignOutBox = new PopupConfirmBox(false, true);
		confirmSignOutBox.setConfirmText("Your chat history will be cleared" 
				+ " when you sign out. Are you sure?");
		confirmSignOutBox.setText("Sign Out");
		
		/**
		 * Header (north) UI
		 */
		final VerticalPanel northLayout = new VerticalPanel();
		northLayout.setSize("100%", "100%");
		northLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		northLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		northLayout.add(new HTML("<h1>Hi, &pi;-chatbot!</h1>"));
		northLayout.ensureDebugId("northPanel");
		
		/**
		 * Layout on PC
		 */
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
			optionsDiscPanel.setContent(optionsHolder);
			optionsDiscPanel.ensureDebugId("optionsDiscPanel");
			
			myAccountDiscPanel.setAnimationEnabled(true);
			myAccountDiscPanel.setOpen(false);
			myAccountDiscPanel.setContent(myAccountHolder);
			myAccountDiscPanel.ensureDebugId("myAccountDiscPanel");
			
			/**
			 *  <--- Styling on PC --->
			 */
			messageField.addStyleName("normalButton");
			sendButton.addStyleName("normalButton");
			
			showPwCheck.addStyleName("normalDisplayText");
			agreeCheck.addStyleName("normalDisplayText");
			showNewPwCheck.addStyleName("normalDisplayText");
			ma_showNewPwCheck.addStyleName("normalDisplayText");
			
			for (Label label : labels) {
				label.addStyleName("normalDisplayText");
			}
			
			confirmSignOutBox.addStyleName("normalPopupConfirmBoxText");
			confirmSignOutBox.getLabel().addStyleName("popupConfirmBoxText");
			confirmSignOutBox.getYesButton().addStyleName("normalButton");
			confirmSignOutBox.getCancelButton().addStyleName("normalButton");
			
			/**
			 * 	<--- Sizing on PC --->
			 */
			westLayout.setSize("100%", "100%");
			chatScrollPanel.setSize("100%", "100%");
			westScrollPanel.setSize("100%", "100%");
			
			signInDiscPanel.setWidth("100%");
			registerDiscPanel.setWidth("100%");
			optionsDiscPanel.setWidth("100%");
			myAccountDiscPanel.setWidth("100%");
			
			signInTable.setWidth("250px");
			registerTable.setWidth("250px");
			signOutTable.setWidth("250px");
			accInfoTable.setWidth("250px");
			changePwTable.setWidth("250px");
			historyTable.setWidth("250px");
			chooseTable.setWidth("250px");
			for (int i = 0; i < 5; i++) {
				updateTabs[i].setWidth("250px");
			}
			
//			confirmSignOutBox.setWidth("360px");
			
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
					dockLayout.setWidgetSize(westScrollPanel, 300);
					dockLayout.animate(350);
				}
			}
			
			WestDiscPanelsHandler westHandler = new WestDiscPanelsHandler();
			signInDiscPanel.addOpenHandler(westHandler);
			signInDiscPanel.addCloseHandler(westHandler);
			registerDiscPanel.addOpenHandler(westHandler);
			registerDiscPanel.addCloseHandler(westHandler);
			myAccountDiscPanel.addOpenHandler(westHandler);
			myAccountDiscPanel.addCloseHandler(westHandler);
			optionsDiscPanel.addOpenHandler(westHandler);
			optionsDiscPanel.addCloseHandler(westHandler);

		/**
		 * Layout on mobile device
		 */
		} else {
			
			signInScrollPanel.add(signInTable);
			signInScrollPanel.ensureDebugId("signInScrollPanel");
			registerScrollPanel.add(registerTable);
			registerScrollPanel.ensureDebugId("registerScrollPanel");
			optionsScrollPanel.add(optionsHolder);
			optionsScrollPanel.ensureDebugId("optionsScrollPanel");
			myAccountScrollPanel.add(myAccountHolder);
			myAccountScrollPanel.ensureDebugId("myAccountScrollPanel");
			
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
			changePwButton.addStyleName("mobileButton");
			exportButton.addStyleName("mobileButton");
			clearButton.addStyleName("mobileButton");
			
			op_removeButton.addStyleName("mobileButton");
			ma_removeButton.addStyleName("mobileButton");
			
			chooseCivility.addStyleName("mobileListBox");
			chooseLanguage.addStyleName("mobileListBox");
			
			showPwCheck.addStyleName("mobileDisplayText");
			agreeCheck.addStyleName("mobileDisplayText");
			showNewPwCheck.addStyleName("mobileDisplayText");
			ma_showNewPwCheck.addStyleName("mobileDisplayText");
			
			for (Label label : labels) {
				label.addStyleName("mobileDisplayText");
			}
			
			stackLayout.getHeaderWidget(0).setStyleName("customStackPanelHeader");
			
			confirmSignOutBox.addStyleName("mobilePopupConfirmBoxText");
			confirmSignOutBox.getLabel().addStyleName("mobileDisplayText");
			confirmSignOutBox.getYesButton().addStyleName("mobileButton");
			confirmSignOutBox.getCancelButton().addStyleName("mobileButton");
			
			/**
			 *  <--- Sizing on mobile device --->
			 */
			int width = Window.getClientWidth();
			chatScrollPanel.setWidth(width + "px");
			
			signInScrollPanel.setWidth(width - 9 + "px");
			registerScrollPanel.setWidth(width - 9 + "px");
			optionsScrollPanel.setWidth(width - 9 + "px");
			myAccountScrollPanel.setWidth(width - 9 + "px");
			
//			confirmSignOutBox.setWidth(width + "px");
			
			signInTable.setWidth(width - 17 + "px");
			registerTable.setWidth(width - 17 + "px");
			signOutTable.setWidth(width - 17 +"px");
			accInfoTable.setWidth(width - 17 + "px");
			changePwTable.setWidth(width - 17 + "px");
			historyTable.setWidth(width - 17 + "px");
			chooseTable.setWidth(width - 17 + "px");
			for (int i = 0; i < 5; i++) {
				updateTabs[i].setWidth(width - 20 + "px");
			}
			
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
					myAccountScrollPanel.setWidth(width - 9 + "px");

//					confirmSignOutBox.setWidth(width + "px");
					
					signInTable.setWidth(width - 17 + "px");
					registerTable.setWidth(width - 17 + "px");
					signOutTable.setWidth(width - 17 +"px");
					accInfoTable.setWidth(width - 17 + "px");
					changePwTable.setWidth(width - 17 + "px");
					historyTable.setWidth(width - 17 + "px");
					chooseTable.setWidth(width - 17 + "px");
					for (int i = 0; i < 5; i++) {
						updateTabs[i].setWidth(width - 20 + "px");
					}
				}
			});
			
			//TODO Force resize layout height on ios device
			
		}
		
		// UI layout completion
		String userID = Cookies.getCookie(COOKIE_NAME);
		if (userID == null) loadLogoutSuccessfulView();
		else checkWithServerIfSessionIsStillLegal(Integer.valueOf(userID));

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
				if (usrField.getStyleName().toLowerCase().contains("default")
						|| pwField.getStyleName().toLowerCase().contains("default")) {
					return;
				}
				
				// Check if valid, send a warning message
				if (!FieldVerifier.isValidUserForSignIn(identifier) 
						|| !FieldVerifier.Password.isValid(password)) {
					displayReceivedMsgBubble("Invalid username or password!", FADING_DELAY);
					return;
				}
				
				// Verify the combination
				SessionControl.Utils.getInstance().login(identifier, password,
						new AsyncCallback<Account>() {
					
					@Override
					public void onSuccess(Account result) {
						
						// Verify the result
						switch ((int)result.getUid()) {
						case 0:
							displayReceivedMsgBubble("Ummm... maybe try again? Something wrong. Sorry about that.", FADING_DELAY); return;
						case -1:
							displayReceivedMsgBubble("Your username is not registered. Try again? or register now!", FADING_DELAY); 
							if (!IS_MOBILE) usrField.selectAll(); return;
						case -2:
							displayReceivedMsgBubble("Sorry, your email address is not registered.", FADING_DELAY);
							if (!IS_MOBILE) usrField.selectAll(); return;
						case -3:
							displayReceivedMsgBubble("Sorry, your cellphone number is not registered.", FADING_DELAY);
							if (!IS_MOBILE) usrField.selectAll(); return;
						case -4:
							displayReceivedMsgBubble("Wrong combination! Try again?", FADING_DELAY);
							if (!IS_MOBILE) pwField.selectAll(); return;
						default:
							break;
						}
						
						// Clear bubbles
//						bubbleLayout.clear();
						
						// Load view after successfully logged in
						loadLoginSuccessfulView(result);
						
						// Display sign in success message
						String fstName = StringUtils.CapFstLetter(result.getFirstname());
						displayReceivedMsgBubble("Welcome back, " + fstName + "!");
						
						// Reset panels
						resetSignInPanel(showPwCheck, usrField, pwField);
						resetRegisterPanel(showNewPwCheck, agreeCheck, 
								fstNameField, lastNameField,
								emailField, cellphoneField,
								newUsrField, newPwField);
						
						// Set session cookie for 8 hours expiry
						setSessionCookieExpiry(result.getUid());
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
					if (agreeCheck.getValue()) register();
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
					displayReceivedMsgBubble("Invalid inputs!", FADING_DELAY);
					return;
				}
				
				String email_address = emailField.getText().trim().toLowerCase();
				String cell_number = cellphoneField.getText().trim().toLowerCase();
				
				// if optional fields are "default", set their values as NULL
				if (emailField.getStyleName().toLowerCase().contains("default"))
					email_address = "NULL";
				else if (!FieldVerifier.Email.isValid(email_address)) {
					displayReceivedMsgBubble("Invalid email address!", FADING_DELAY);
					return;
				}
				if (cellphoneField.getStyleName().toLowerCase().contains("default"))
					cell_number = "NULL";
				else if (!FieldVerifier.Cellphone.isValid(cell_number)) {
					displayReceivedMsgBubble("Invalid cellphone number!", FADING_DELAY);
					return;
				}
				
				// register new user in database
				SessionControl.Utils.getInstance().register(first_name, last_name, 
						email_address, cell_number, 
						new_username, new_password, 
						new AsyncCallback<Account>() {
					
					@Override
					public void onSuccess(Account result) {
						
						// Verify the result
						switch ((int)result.getUid()) {
						case 0:
							displayReceivedMsgBubble("Ummm... maybe try again? Something wrong. Sorry about that.", FADING_DELAY); return;
						case -1:
							displayReceivedMsgBubble("This username already exists. Try another one?", FADING_DELAY); 
							if (!IS_MOBILE) newUsrField.selectAll(); return;
						case -2:
							displayReceivedMsgBubble("This email address is already registered.", FADING_DELAY);
							if (!IS_MOBILE) emailField.selectAll(); return;
						case -3:
							displayReceivedMsgBubble("This cellphone number is already registered.", FADING_DELAY);
							if (!IS_MOBILE) cellphoneField.selectAll(); return;
						default:
							break;
						}
						
						// Clear bubbles
//						bubbleLayout.clear();
						
						// Load view after successfully logged in
						loadLoginSuccessfulView(result);
						
						// Display sing in success message
						String fstName = StringUtils.CapFstLetter(result.getFirstname());
						displayReceivedMsgBubble("Nice to meet you, " + fstName + "!");
						
						// Reset panels
						resetSignInPanel(showPwCheck, usrField, pwField);
						resetRegisterPanel(showNewPwCheck, agreeCheck, 
								fstNameField, lastNameField,
								emailField, cellphoneField,
								newUsrField, newPwField);
						
						// Set session cookie for 8 hours expiry
						setSessionCookieExpiry(result.getUid());
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
		
		class ChangePwActionHandler implements ClickHandler, KeyUpHandler {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					ma_newPwField.setFocus(false);
					changePassword();
				}
			}

			@Override
			public void onClick(ClickEvent event) {
				changePassword();
			}
			
			private void changePassword() {
				String oldPasswd = ma_currentPwField.getText();
				String newPasswd = ma_newPwField.getText();
				
				// Check if empty or default, do nothing
				if (ma_currentPwField.getStyleName().toLowerCase().contains("default")
						|| ma_newPwField.getStyleName().toLowerCase().contains("default")) {
					return;
				}
				
				// Check if valid, send a warning message
				if (!FieldVerifier.Password.isValid(newPasswd)) {
					displayReceivedMsgBubble("Your new password is invalid!", FADING_DELAY);
					return;
				}
				
				// Check if the new password differs from the old one
				if (newPasswd.equals(oldPasswd)) {
					displayReceivedMsgBubble("Your new password should differ from the old one.", FADING_DELAY);
					return;
				}
				
				SessionControl.Utils.getInstance().changePassword(
						Integer.valueOf(Cookies.getCookie(COOKIE_NAME)), oldPasswd, newPasswd, 
						new AsyncCallback<Boolean>() {
					
					@Override
					public void onSuccess(Boolean result) {
						if (result) {
							displayReceivedMsgBubble("Your password has been updated successfully.");
						} else {
							displayReceivedMsgBubble("Oops! Something wrong. Your password remains unchanged.", FADING_DELAY);
						}
						resetMyAccountPanel(ma_showNewPwCheck, ma_removeButton,
								ma_currentPwField, ma_newPwField);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(SERVER_ERROR + "\n" + caught.getMessage());
					}
				});
			}
		}
		
		final ChangePwActionHandler changePwHandler = new ChangePwActionHandler();
		changePwButton.addClickHandler(changePwHandler);
		ma_newPwField.addKeyUpHandler(changePwHandler);
		
		confirmSignOutBox.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (event.isAutoClosed()) {
					SessionControl.Utils.getInstance().logout(
							Integer.valueOf(Cookies.getCookie(COOKIE_NAME)), 
							new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(SERVER_ERROR + "\n" + caught.getMessage());
						}

						@Override
						public void onSuccess(Void result) {
							
							// Clear bubbles
							bubbleLayout.clear();
							
							// Display sign out success message
							displayReceivedMsgBubble("See you later, then!", FADING_DELAY);
							
							// Load view after successfully logged out
							loadLogoutSuccessfulView();
							
							// Reset panels
							resetOptionsPanel(editChecks, updateFields, 
									updateButtons, chooselists, op_removeButton);
							resetMyAccountPanel(ma_showNewPwCheck, ma_removeButton,
									ma_currentPwField, ma_newPwField);
							
							// remove Cookie when sign out
							Cookies.removeCookie(COOKIE_NAME, "/");
						}
					});
				}
			}
		});
		
		/**
		 * Handling button click events
		 */
		signOutButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				confirmSignOutBox.center();
				confirmSignOutBox.show();
			}
		});
		
		forgotPwButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				displayReceivedMsgBubble("Sorry! This service is currently not available.", FADING_DELAY);				
			}
		});
		
		clearButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				bubbleLayout.clear();
				displayReceivedMsgBubble("Yeah! I feel very \"clean\", now.", FADING_DELAY);
			}
		});
		
		exportButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				displayReceivedMsgBubble("Sorry! This service is currently not available.", FADING_DELAY);
			}
		});
		
		ma_removeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for (int i = 0; i < 2; i++) {
					if (CURRENT_USER[i+4] != EDITOR_DEFAULT_TEXT[i]
							&& editChecks[i].getValue()){
						PopupConfirmBox confirmBox = newConfirmRemoveBox(i);
						confirmBox.setText("Remove Contact Information");
						String info = "";
						switch (i) {
						case 0: info = "email address";	break;
						case 1: info = "cellphone number"; break;	
						default:
							break;
						}
						info += ": " + CURRENT_USER[i+4] + "";
						confirmBox.setConfirmText("Your " + info + " will "
								+ "be removed from your account. Are you sure?");
						confirmBox.center();
						confirmBox.show();
					}
				}
				
			}
		});
		
		
		op_removeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for (int i = 2; i < 5; i++) {
					if (CURRENT_USER[i+4] != EDITOR_DEFAULT_TEXT[i]
							&& editChecks[i].getValue()){
						PopupConfirmBox confirmBox = newConfirmRemoveBox(i);
						confirmBox.setText("Remove Payment Information");
						String info = "";
						switch (i) {
						case 2: info = "PayPal ID";	break;
						case 3: info = "AliPay ID"; break;
						case 4: info = "WeChat ID"; break;
						default:
							break;
						}
						info += ": " + CURRENT_USER[i+4] + "";
						confirmBox.setConfirmText("Your " + info + " will "
								+ "be removed from your account. Are you sure?");
						confirmBox.center();
						confirmBox.show();
					}
					
				}
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
		
		ma_showNewPwCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				toggleShowPassword(ma_currentPwField);
				toggleShowPassword(ma_newPwField);
			}
		});
		
		agreeCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (agreeCheck.getValue()) signUpButton.setEnabled(true);
				else signUpButton.setEnabled(false);
			}
		});
		
		chooseCivility.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String civil = chooseCivility.getSelectedValue();
				if (civil.toLowerCase().equals("unknown")) civil = "NULL";
				
				final long uid = Integer.valueOf(Cookies.getCookie(COOKIE_NAME));
				SessionControl.Utils.getInstance().update(uid, Column.CIVILITY, civil, 
						new AsyncCallback<Account>() {
					
					@Override
					public void onSuccess(Account result) {
						if (result.getUid() == 0) {
							displayReceivedMsgBubble("Server session expired! Please sign in again.", FADING_DELAY);
							loadLogoutSuccessfulView();
							return;
						}
						if (result.getUid() == uid) {
							String msg = "";
							if (result.getCivility() != UCivility.UNKNOWN) {
								String lastname = StringUtils.CapFstLetter(CURRENT_USER[1]);
								msg = "Hello, " + result.getCivility().toString() + " " + lastname + "!";
							} else msg = "Hi, " + CURRENT_USER[0] + "!";
							displayReceivedMsgBubble(msg, 2*FADING_DELAY);
						}
					}
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(SERVER_ERROR + "\n" + caught.getMessage());
					}
				});
			}
		});
		
		chooseLanguage.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String lang = chooseLanguage.getSelectedValue();
				
				final long uid = Integer.valueOf(Cookies.getCookie(COOKIE_NAME));
				SessionControl.Utils.getInstance().update(uid, Column.LOCALE, lang, 
						new AsyncCallback<Account>() {
					
					@Override
					public void onSuccess(Account result) {
						if (result.getUid() == 0) {
							displayReceivedMsgBubble("Server session expired! Please sign in again.", FADING_DELAY);
							loadLogoutSuccessfulView();
							return;
						}
						if (result.getUid() == uid) {
							String msg = "";
							switch (result.getLocale()) {
							case fr_FR:
								msg = "Bonjour! Je m'appelle Pi.";
								break;
							case zh_CN:
								msg = "你好！请叫我小派。";
								break;
							case en_GB:
								msg = "Hello! My name is Pi.";
								break;
							case en_US:
								msg = "Hey, man! I'm Pi. What's up?";
								break;
							default:
								break;
							}
							displayReceivedMsgBubble(msg, 2*FADING_DELAY);
						}
					}
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(SERVER_ERROR + "\n" + caught.getMessage());
					}
				});
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
	
	private void removeColumnFromDatabase(final int index) {
		
		final CheckBox editChk = (CheckBox) updateTabs[index].getWidget(0);
		final Button updateBtn = (Button) updatePanels[index].getWidget(1);
		
		String newStr = "NULL";
		TInfo.Column ref = Column.UNDEFINED;
		switch (index) {
		case 0: ref = Column.EMAIL;	break; 		// email address
		case 1: ref = Column.CELLPHONE;	break; 	// cellphone number
		case 2: ref = Column.PayPal; break; 	// PayPal account
		case 3: ref = Column.Alipay; break;		// Alipay account
		case 4: ref = Column.WeChat; break;		// WeChat account
		default: break;	
		}
		final long uid = Integer.valueOf(Cookies.getCookie(COOKIE_NAME));
		SessionControl.Utils.getInstance().update(
				uid, ref, newStr, new AsyncCallback<Account>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(SERVER_ERROR + "\n" + caught.getMessage());
			}

			@Override
			public void onSuccess(Account result) {
				if (result.getUid() == 0) {
					displayReceivedMsgBubble("Server session expired! Please sign in again.", FADING_DELAY);
					loadLogoutSuccessfulView();
					return;
				}
				
				if (result.getUid() == uid) {
					editChk.setValue(false, true);
					updateBtn.setEnabled(false);
					String msg = "";
					switch (index) {
					case 0: 
						msg = "email address"; 
						CURRENT_USER[4] = result.getEmail(); 
						break;
					case 1: 
						msg = "cellphone number"; 
						CURRENT_USER[5] = result.getCellphone();
						break;
					case 2: 
						msg = "PayPal account";	
						CURRENT_USER[6] = result.getPayPal();
						break;
					case 3: 
						msg = "Alipay account";	
						CURRENT_USER[7] = result.getAlipay();
						break;
					case 4: 
						msg = "WeChat account";	
						CURRENT_USER[8] = result.getWeChat();
						break;
					default: 
						msg = "account"; break;
					}
					editChk.setText(" Add" + EDITOR_CHECK_TEXT[index]);
					CURRENT_USER[index+4] = EDITOR_DEFAULT_TEXT[index];
					displayReceivedMsgBubble("Your " + msg 
							+ " is successfully removed.", FADING_DELAY);
					
					// Suggest to add the removed contact information
					if (index < 2) editChk.setValue(true, true);
				}
			}
		});
	}
	
	private void resetSignInPanel(CheckBox showPwCheck, TextBox... fields) {
		showPwCheck.setValue(true, true);
		for (int i = 0; i < fields.length; i++) {
			fields[i].removeStyleName("errorTextField");
			fields[i].setText(FIELD_DEFAULT_TEXT[i]);
			if (IS_MOBILE) fields[i].addStyleName("mobileDefaultTextFieldText");
			else fields[i].addStyleName("normalDefaultTextFieldText");
		}
		signInDiscPanel.setOpen(false);
	}
	
	private void resetRegisterPanel(CheckBox showNewPwCheck, CheckBox agreeCheck, TextBox... fields) {
		showNewPwCheck.setValue(true, true);
		agreeCheck.setValue(false);
		for (int i = 0; i < fields.length; i++) {
			fields[i].removeStyleName("errorTextField");
			fields[i].setText(FIELD_DEFAULT_TEXT[i+2]);
			if (IS_MOBILE) fields[i].addStyleName("mobileDefaultTextFieldText");
			else fields[i].addStyleName("normalDefaultTextFieldText");
		}
		registerDiscPanel.setOpen(false);
	}
	
	private void resetMyAccountPanel(CheckBox showPwCheck, Button removeButton, 
			TextBox... fields) {
		removeButton.setEnabled(false);
		showPwCheck.setValue(true, true);
		for (int i = 0; i < fields.length; i++) {
			fields[i].removeStyleName("errorTextField");
			fields[i].setText(FIELD_DEFAULT_TEXT[i+8]);
			if (IS_MOBILE) fields[i].addStyleName("mobileDefaultTextFieldText");
			else fields[i].addStyleName("normalDefaultTextFieldText");
		}
		myAccountDiscPanel.setOpen(false);
	}
	
	private void resetOptionsPanel(CheckBox[] editChecks, TextBox[] updateFields, 
			Button[] updateButtons, ListBox[] chooselists, Button removeButton) {
		removeButton.setEnabled(false);
		for (ListBox chooseBox : chooselists) {
			chooseBox.setSelectedIndex(0);
		}
		
		for (int i = 0; i < updatePanels.length; i++) {
			updateTabs[i].remove(updatePanels[i]);
			editChecks[i].setValue(false);
			updateFields[i].setEnabled(false);
			updateButtons[i].setEnabled(false);
			
			updateFields[i].removeStyleName("errorTextField");
			updateFields[i].setText(EDITOR_DEFAULT_TEXT[i]);
			if (IS_MOBILE) updateFields[i].addStyleName("mobileDefaultTextFieldText");
			else updateFields[i].addStyleName("normalDefaultTextFieldText");
		}
		optionsDiscPanel.setOpen(false);
	}
	
	private void resetCurrentUser() {
		for (int i = 0; i < CURRENT_USER.length; i++) {
			CURRENT_USER[i] = "";
		}
	}
	
	private void retrieveCurrentUser(Account account) {
		if (account.getFirstname() != null) 
			CURRENT_USER[0] = StringUtils.CapFstLetter(account.getFirstname());
		else CURRENT_USER[0] = "";
		
		if (account.getLastname() != null)
			CURRENT_USER[1] = StringUtils.CapAllLetter(account.getLastname());
		else CURRENT_USER[1] = "";
		
		if (account.getLastActive() != null) {
			DateTimeFormat formatter = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
			CURRENT_USER[2] = formatter.format(account.getLastActive());
		}
		else CURRENT_USER[2] = "";
		
		if (account.getLastIP() != null)
			CURRENT_USER[3] = account.getLastIP();
		else CURRENT_USER[3] = "";
		
		if (account.getEmail() != null)
			CURRENT_USER[4] = account.getEmail();
		else CURRENT_USER[4] = EDITOR_DEFAULT_TEXT[0];
		
		if (account.getCellphone() != null)
			CURRENT_USER[5] = account.getCellphone();
		else CURRENT_USER[5] = EDITOR_DEFAULT_TEXT[1];
		
		if (account.getPayPal() != null)
			CURRENT_USER[6] = account.getPayPal();
		else CURRENT_USER[6] = EDITOR_DEFAULT_TEXT[2];
		
		if (account.getAlipay() != null)
			CURRENT_USER[7] = account.getAlipay();
		else CURRENT_USER[7] = EDITOR_DEFAULT_TEXT[3];
		
		if (account.getWeChat() != null)
			CURRENT_USER[8] = account.getWeChat();
		else CURRENT_USER[8] = EDITOR_DEFAULT_TEXT[4];
		
	}
	
	
	private void loadLoginSuccessfulView(Account account) {
		
		userGroupLabel.setText(account.getGroup().toString());
		
		// Mask user group if not an administrator account
		if (account.getGroup() != UGroup.admin) {
			accInfoTable.removeRow(0);
		} else {
			accInfoTable.insertRow(0);
			accInfoTable.setWidget(0, 0, label3);
			accInfoTable.setWidget(0, 1, userGroupLabel);
		}
		
		// Retrieve current user information
		retrieveCurrentUser(account);
		
		// Retrieve the account basic information (in "My Account")
		nameLabel.setText(CURRENT_USER[1] + " " + CURRENT_USER[0]);
		lastActiveLabel.setText(CURRENT_USER[2]);
		lastIpAddrLabel.setText(CURRENT_USER[3]);
		
		// Retrieve more account information (in "Options")
		for (int i = 0; i < updatePanels.length; i++) {
			TextBox field = (TextBox) updatePanels[i].getWidget(0);
			field.setText(CURRENT_USER[i+4]);
			
			CheckBox check = (CheckBox) updateTabs[i].getWidget(0);
			if (CURRENT_USER[i+4] == EDITOR_DEFAULT_TEXT[i]) {
				check.setText(" Add" + EDITOR_CHECK_TEXT[i]);
				if (i < 2) check.setValue(true, true);
			} else check.setText(" Edit my" + EDITOR_CHECK_TEXT[i]);
		}
		
		// Retrieve civility
		for (int i = 0; i < UCivility.values().length; i++) {
			if (account.getCivility() == UCivility.values()[i])
				chooseCivility.setSelectedIndex(i);
		}
		
		// Retrieve language
		for (int i = 0; i < ULocale.values().length; i++) {
			if (account.getLocale() == ULocale.values()[i])
				chooseLanguage.setSelectedIndex(i);
		}
		
		// Update the UI
		if (!IS_MOBILE) {
			westLayout.clear();
			
			westLayout.add(myAccountDiscPanel);
			westLayout.add(optionsDiscPanel);
			
		} else {
			stackLayout.remove(signInScrollPanel); 
			stackLayout.remove(registerScrollPanel);
			
			stackLayout.add(myAccountScrollPanel, new HTML("My Account"), 2.4);
			stackLayout.getHeaderWidget(1).setStyleName("customStackPanelHeader");
			stackLayout.add(optionsScrollPanel, new HTML("Options"), 2.4);
			stackLayout.getHeaderWidget(2).setStyleName("customStackPanelHeader");
		}

	}
	
	private void loadLogoutSuccessfulView() {
		
		resetCurrentUser();
		
		nameLabel.setText("");
		userGroupLabel.setText("");
		lastActiveLabel.setText("");
		lastIpAddrLabel.setText("");
		
		// Update the UI
		if (!IS_MOBILE) {
			westLayout.clear();
			westLayout.add(signInDiscPanel);
			westLayout.add(registerDiscPanel);
			
		} else {
			stackLayout.remove(optionsScrollPanel);
			stackLayout.remove(myAccountScrollPanel);
			
			stackLayout.add(signInScrollPanel, new HTML("Sign In"), 2.4);
			stackLayout.getHeaderWidget(1).setStyleName("customStackPanelHeader");
			stackLayout.add(registerScrollPanel, new HTML("Register"), 2.4);
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
		
		final DecoratorPanel sentBubble = new DecoratorPanel();
		sentBubble.addStyleName("sentBubble");
	    sentBubble.setWidget(sentMsgLable);
		bubbleLayout.add(sentBubble);
		bubbleLayout.setCellHorizontalAlignment(sentBubble, HasHorizontalAlignment.ALIGN_RIGHT);
		scrollAnimator.scrollToEnd(SCROLLING_DURATION);
//		chatScrollPanel.ensureVisible(sentBubble);
		
		// add animation to the message bubble
		if (fadeAnimator.isRunning()) fadeAnimator.cancel();
		sentBubble.getElement().getStyle().setOpacity(.01);
		fadeAnimator.setElement(sentBubble);
		fadeAnimator.fade(FADING_DURATION, 1.0);
	}
	
	private DecoratorPanel displayReceivedMsgBubble(String msg) {
		
		final Label receivedMsgLable = new Label(msg);
		if (!IS_MOBILE) receivedMsgLable.setStyleName("normalReceivedMessageText");
		else {
			// Ensure the visibility on mobile device
			stackLayout.showWidget(chatScrollPanel);
			receivedMsgLable.setStyleName("mobileReceivedMessageText");
		}
		
		final DecoratorPanel receivedBubble = new DecoratorPanel();
		receivedBubble.addStyleName("receivedBubble");
	    receivedBubble.setWidget(receivedMsgLable);
		bubbleLayout.add(receivedBubble);
		bubbleLayout.setCellHorizontalAlignment(receivedBubble, HasHorizontalAlignment.ALIGN_LEFT);
		scrollAnimator.scrollToEnd(SCROLLING_DURATION);
//		chatScrollPanel.ensureVisible(receivedBubble);
		
		// add animation to the message bubble
		if (fadeAnimator.isRunning()) fadeAnimator.cancel();
		receivedBubble.getElement().getStyle().setOpacity(.1);
		fadeAnimator.setElement(receivedBubble);
		fadeAnimator.fade(FADING_DURATION, 1.0);
		return receivedBubble;
	}
	
	private void displayReceivedMsgBubble(String msg, int delay) {
		final DecoratorPanel receivedBubble = displayReceivedMsgBubble(msg);
		final FadeAnimation animator = new FadeAnimation(receivedBubble);
		Timer timer = new Timer() {
			@Override
			public void run() {
				animator.fade(MAX_FADING_DURATION, .05);
				Timer remover = new Timer() {
					@Override
					public void run() {bubbleLayout.remove(receivedBubble);}
				};
				remover.schedule(MAX_FADING_DURATION);
//				int h = receivedBubble.getOffsetHeight();
//				int w = receivedBubble.getOffsetWidth();
//				errorLabel.setText("Cell height = " + h + " Cell width = " + w);
			}
		};
		timer.schedule(delay);
	}
	
	private void initializeUpdateTabs(CheckBox[] editChecks, TextBox[] updateFields, 
			Button[] updateButtons, final Button... removeButtons) {

		final CheckBox[] ma_editChecks = {editChecks[0], editChecks[1]};
		final CheckBox[] op_editChecks = {editChecks[2], editChecks[3], editChecks[4]};
		
		for (int i = 0; i < updateFields.length; i++) {
			final int index = i;
			final CheckBox editCheck = editChecks[i];
			final TextBox updateField = updateFields[i];
			final Button updateButton = updateButtons[i];
			
			if (IS_MOBILE) {
				editCheck.addStyleName("mobileDisplayText");
				updateField.addStyleName("mobileTextFieldText");
				updateField.addStyleName("mobileDefaultTextFieldText");
				updateButton.addStyleName("mobileButton");
				updateField.setWidth("95%");
			} else {
				editCheck.addStyleName("normalDisplayText");
				updateField.addStyleName("normalDefaultTextFieldText");
				updateField.setWidth("93%");
			}
			
			updateField.setTitle(EDITOR_TITLE_TEXT[i]);
			editCheck.setValue(false);
			updateField.setEnabled(false);
			updateButton.setEnabled(false);
			
			editCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					
					// toggle to enable update field and panel
					updateField.setEnabled(!updateField.isEnabled());
					if (updatePanels[index].isAttached()) 
						updateTabs[index].remove(updatePanels[index]);
					else updateTabs[index].add(updatePanels[index]);
					
					// ensure the default display text
					if (editCheck.getValue()) {
						updateField.removeStyleName("errorTextField");
						updateField.setText(CURRENT_USER[index+4]);
						if (IS_MOBILE) updateField.addStyleName("mobileDefaultTextFieldText");
						else updateField.addStyleName("normalDefaultTextFieldText");
					}
					
					// if "edit" not "add"
					if (CURRENT_USER[index+4] != EDITOR_DEFAULT_TEXT[index]) {
						
						// Enabling the "remove" button
						if (editCheck.getValue()) { 
							if (Arrays.asList(ma_editChecks).contains(editCheck)) 
								removeButtons[0].setEnabled(true);
							if (Arrays.asList(op_editChecks).contains(editCheck))
								removeButtons[1].setEnabled(true);
						} 
						// Disabling the "remove button"
						else {
							int checked = 0;
							if (Arrays.asList(ma_editChecks).contains(editCheck)) {
								for (CheckBox chk : ma_editChecks) {
									String chkTxt = chk.getText().toLowerCase();
									if (chk.getValue() && chkTxt.contains("edit")) checked++;
								}
								if (checked < 1) removeButtons[0].setEnabled(false);
							}
							if (Arrays.asList(op_editChecks).contains(editCheck)) {
								for (CheckBox chk : op_editChecks) {
									String chkTxt = chk.getText().toLowerCase();
									if (chk.getValue() && chkTxt.contains("edit")) checked++;
								}
								if (checked < 1) removeButtons[1].setEnabled(false);
							}
						}
					}
				}
			});
			
			updateField.addFocusHandler(new FocusHandler() {
				
				@Override
				public void onFocus(FocusEvent event) {
					if (updateField.getStyleName().toLowerCase().contains("default")) 
						updateField.setText("");
					if (IS_MOBILE) updateField.removeStyleName("mobileDefaultTextFieldText");
					else updateField.removeStyleName("normalDefaultTextFieldText");
				}
			});
			
			updateField.addBlurHandler(new BlurHandler() {
				
				@Override
				public void onBlur(BlurEvent event) {
					String currentText = updateField.getText();
					
					// if empty, reset the default text
					if (currentText.trim().length() < 1) {
						updateField.removeStyleName("errorTextField");
						updateField.setText(CURRENT_USER[index+4]);
						if (IS_MOBILE) updateField.addStyleName("mobileDefaultTextFieldText");
						else updateField.addStyleName("normalDefaultTextFieldText");
					}
					// if not empty, validate the content
					else {
						switch (index) {
						case 0: //email address
							if (!FieldVerifier.Email.isValid(currentText)) {
								updateField.addStyleName("errorTextField");
								displayReceivedMsgBubble("This is not a valid email address.", FADING_DELAY);
							} else updateField.removeStyleName("errorTextField");
							break;
						case 1: //cellphone number
							if (!FieldVerifier.Cellphone.isValid(currentText)) {
								updateField.addStyleName("errorTextField");
								displayReceivedMsgBubble("This is not a valid cellphone number.", FADING_DELAY);
							} else updateField.removeStyleName("errorTextField");
							break;
						case 2: //PayPal account
							if (!FieldVerifier.PayPalAcc.isValid(currentText)) {
								updateField.addStyleName("errorTextField");
								displayReceivedMsgBubble("This is probably not a valid account.", FADING_DELAY);
							} else updateField.removeStyleName("errorTextField");
							break;
						case 3: //Alipay account
							if (!FieldVerifier.AlipayAcc.isValid(currentText)) {
								updateField.addStyleName("errorTextField");
								displayReceivedMsgBubble("This is probably not a valid account.", FADING_DELAY);
							} else updateField.removeStyleName("errorTextField");
							break;
						case 4: //WeChat account
							if (!FieldVerifier.WeChatAcc.isValid(currentText)) {
								updateField.addStyleName("errorTextField");
								displayReceivedMsgBubble("This is probably not a valid account.", FADING_DELAY);
							} else updateField.removeStyleName("errorTextField");
							break;
						default:
							break;
						}
					}
					
				}
			});
			
			class UpdateActionHandler implements KeyUpHandler, ClickHandler {

				@Override
				public void onClick(ClickEvent event) {
					update();
				}

				@Override
				public void onKeyUp(KeyUpEvent event) {
					if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
						updateField.setFocus(false);
						if (updateButton.isEnabled()) update();
					}
					String input = updateField.getText().trim();
					switch (index) {
					case 0: //email address
						if (FieldVerifier.Email.isValid(input)) {
							updateField.removeStyleName("errorTextField");
							updateButton.setEnabled(true);
						} else updateButton.setEnabled(false);
						break;
					case 1: //cellphone number
						if (FieldVerifier.Cellphone.isValid(input)) {
							updateField.removeStyleName("errorTextField");
							updateButton.setEnabled(true);
						} else updateButton.setEnabled(false);
						break;
					case 2: //PayPal account
						if (FieldVerifier.PayPalAcc.isValid(input)) {
							updateField.removeStyleName("errorTextField");
							updateButton.setEnabled(true);
						} else updateButton.setEnabled(false);
						break;
					case 3: //Alipay account
						if (FieldVerifier.AlipayAcc.isValid(input)) {
							updateField.removeStyleName("errorTextField");
							updateButton.setEnabled(true);
						} else updateButton.setEnabled(false);
						break;
					case 4: //WeChat account
						if (FieldVerifier.WeChatAcc.isValid(input)) {
							updateField.removeStyleName("errorTextField");
							updateButton.setEnabled(true);
						} else updateButton.setEnabled(false);
						break;
					default:
						break;
					}
					
				}
				
				private void update() {
					
					String newStr = updateField.getText().trim().toLowerCase();
					if (newStr == CURRENT_USER[index+4]) {
						editCheck.setValue(false, true);
						displayReceivedMsgBubble("Nothing changed.", FADING_DELAY);
						return;
					}
					
					TInfo.Column ref = Column.UNDEFINED;
					switch (index) {
					case 0: ref = Column.EMAIL;	break; 		// email address
					case 1: ref = Column.CELLPHONE;	break; 	// cellphone number
					case 2: ref = Column.PayPal; break; 	// PayPal account
					case 3: ref = Column.Alipay; break;		// Alipay account
					case 4: ref = Column.WeChat; break;		// WeChat account
					default: break;	
					}
					
					final long uid = Integer.valueOf(Cookies.getCookie(COOKIE_NAME));
					SessionControl.Utils.getInstance().update(uid, ref, newStr, 
							new AsyncCallback<Account>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(SERVER_ERROR + "\n" + caught.getMessage());
						}

						@Override
						public void onSuccess(Account result) {
							if (result.getUid() == 0) {
								displayReceivedMsgBubble("Server session expired! Please sign in again.", FADING_DELAY);
								loadLogoutSuccessfulView();
								return;
							}
							
							if (result.getUid() == -1) {
								switch (index) {
								case 0: //email address
									displayReceivedMsgBubble("This email address "
											+ "is already registered. Try another one?", FADING_DELAY);
									break;
								case 1: //cellphone number
									displayReceivedMsgBubble("This cellphone number "
											+ "is already registered. Try another one?", FADING_DELAY);
									break;
								default:
									break;
								}
								if (!IS_MOBILE) updateField.selectAll();
							}
							
							if (result.getUid() == uid) {
								editCheck.setValue(false, true);
								updateButton.setEnabled(false);
								String info = "";
								switch (index) {
								case 0: 
									info = "email address"; 
									CURRENT_USER[4] = result.getEmail(); 
									break;
								case 1: 
									info = "cellphone number"; 
									CURRENT_USER[5] = result.getCellphone();
									break;
								case 2: 
									info = "PayPal account";	
									CURRENT_USER[6] = result.getPayPal();
									break;
								case 3: 
									info = "Alipay account";	
									CURRENT_USER[7] = result.getAlipay();
									break;
								case 4: 
									info = "WeChat account";	
									CURRENT_USER[8] = result.getWeChat();
									break;
								default: 
									info = "account"; break;
								}
								editCheck.setText(" Edit my" + EDITOR_CHECK_TEXT[index]);
								displayReceivedMsgBubble("Your " + info 
										+ " is now up-to-date.", FADING_DELAY);
							}
						}
					});
				}
			}
			
			final UpdateActionHandler updateHandler = new UpdateActionHandler();
			updateField.addKeyUpHandler(updateHandler);
			updateButton.addClickHandler(updateHandler);
			
		}
		
	}
	
	private void initializeTextFields(final TextBox[] fields) {
		for (int i = 0; i < fields.length; i++) {
			final int index = i; final TextBox textbox = fields[i];
			
			if (IS_MOBILE) {
				textbox.addStyleName("mobileTextFieldText");
				textbox.addStyleName("mobileDefaultTextFieldText");
				if (i == 2 || i == 3) textbox.setWidth("95%"); // first name and last name
				else textbox.setWidth("98%");
			}
			else {
				textbox.addStyleName("normalDefaultTextFieldText");
				if (i == 2 || i == 3) textbox.setWidth("100px"); // first name and last name
				else textbox.setWidth("220px");
			}
			
			textbox.setText(FIELD_DEFAULT_TEXT[i]);
			textbox.setTitle(FIELD_TITLE_TEXT[i]);

			textbox.addFocusHandler(new FocusHandler() {
				
				@Override
				public void onFocus(FocusEvent event) {
					if (textbox.getStyleName().toLowerCase().contains("default")) 
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
						textbox.removeStyleName("errorTextField");
						textbox.setText(FIELD_DEFAULT_TEXT[index]);
						if (IS_MOBILE) textbox.addStyleName("mobileDefaultTextFieldText");
						else textbox.addStyleName("normalDefaultTextFieldText");
					}
					// if not empty, validate the content
					else {
						switch (index) {
						case 0: //username or email or cellphone
							if (!FieldVerifier.isValidUserForSignIn(currentText)) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("This may not be a valid username or email address or cellphone number.", FADING_DELAY);
							} else textbox.removeStyleName("errorTextField");
							break;

						case 1: //password (in "Sign In" panel)
						case 8: //current password (in "My Account" panel)
							if (!FieldVerifier.Password.isValid(currentText)) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your password is invalid.", FADING_DELAY);
							} else textbox.removeStyleName("errorTextField");
							break;
							
						case 2: //first name
							if (currentText.trim().length() > 30) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your first name is too long. It should contain no more than 30 characters.", FADING_DELAY);
							} 
							else if (!FieldVerifier.Firstname.isValid(currentText)) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your first name should contain only letters, spaces and ’.'-", FADING_DELAY);
							} 
							else textbox.removeStyleName("errorTextField");
							break;
							
						case 3: //last name
							if (currentText.trim().length() > 60) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your last name is too long. It should contain no more than 60 characters.", FADING_DELAY);
							} 
							else if (!FieldVerifier.Lastname.isValid(currentText)) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your last name should contain only letters, spaces and ’.'-", FADING_DELAY);
							} 
							else textbox.removeStyleName("errorTextField");
							break;
						
						case 4: //email address
							if (!FieldVerifier.Email.isValid(currentText)) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("This is not a valid email address.", FADING_DELAY);
							} 
							else textbox.removeStyleName("errorTextField");
							break;
						
						case 5: //cellphone number
							if (!FieldVerifier.Cellphone.isValid(currentText)) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("This is not a valid cellphone number.", FADING_DELAY);
							} 
							else textbox.removeStyleName("errorTextField");
							break;
						
						case 6: // username
							if (currentText.length() < 5) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your username must contain at least 5 characters.", FADING_DELAY);
							} 
							else if (currentText.length() > 20) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your username must have no more than 20 characters.", FADING_DELAY);
							}
							else if (!FieldVerifier.Username.isValid(currentText)) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your username should contain only letters (at least one), numbers, \"_\" and \"-\".", FADING_DELAY);
							} 
							else textbox.removeStyleName("errorTextField");
							break;
						
						case 7: //new password (in "Register" panel)
						case 9: //new password (in "My Account" panel)
							if (currentText.length() < 8) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your password must contain at least 8 characters.", FADING_DELAY);
							}
							else if (currentText.length() > 30) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your password must have no more than 30 characters.", FADING_DELAY);
							}
							else if (!FieldVerifier.Password.isValid(currentText)) {
								textbox.addStyleName("errorTextField");
								displayReceivedMsgBubble("Your password should contain only letters, numbers, \"_\" and \"-\".", FADING_DELAY);
							}
							else textbox.removeStyleName("errorTextField");
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
							fields[index + 1].selectAll();
						}
					}
				});
			}
		}
	}
	
	private void checkWithServerIfSessionIsStillLegal(final long uid) {
		
		SessionControl.Utils.getInstance().loginFromSessionServer(uid,
				new AsyncCallback<Account>() {
			
			@Override
			public void onSuccess(Account result) {
				// Verify the result
				if (result.getUid() == 0) {
					displayReceivedMsgBubble("Server session expired! Please sign in again.", FADING_DELAY);
					loadLogoutSuccessfulView();
					return;
				}
				
				if (result.getUid() == uid) {
					// Load view after successfully logged in
					loadLoginSuccessfulView(result);
					
					// Display sign in success message
					String fstName = StringUtils.CapFstLetter(result.getFirstname());
					displayReceivedMsgBubble("Welcome back, " + fstName + "!");
					
					// Display warning message
					if (result.getNumOfIncoherents() > 0)
						displayReceivedMsgBubble("Your account details have been modified "
								+ "on other devices. Please be careful with your account "
								+ "security if it wasn't you.", FADING_DELAY);
					
				}

				else loadLogoutSuccessfulView();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(SERVER_ERROR + "\n" + caught.getMessage());
				
			}
		});
		
	}
	
	private void setSessionCookieExpiry(long uid) {
		// Set session cookie for 8 hours expiry
		String userID = String.valueOf(uid);
        final long DURATION = 1000 * 60 * 60 * 8 * 1;
        Date expires = new Date(System.currentTimeMillis() + DURATION);
        Cookies.setCookie(COOKIE_NAME, userID, expires, null, "/", false);
	}
	
	
	private PopupConfirmBox newConfirmRemoveBox(final int index) {
		
		final PopupConfirmBox re = new PopupConfirmBox(false, true);
		if (!IS_MOBILE) {
			re.addStyleName("normalPopupConfirmBoxText");
			re.getLabel().addStyleName("popupConfirmBoxText");
			re.getYesButton().addStyleName("normalButton");
			re.getCancelButton().addStyleName("normalButton");
		} else {
			re.addStyleName("mobilePopupConfirmBoxText");
			re.getLabel().addStyleName("mobileDisplayText");
			re.getYesButton().addStyleName("mobileButton");
			re.getCancelButton().addStyleName("mobileButton");
		}
		re.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (event.isAutoClosed()) {
					removeColumnFromDatabase(index);
				}
			}
		});
		return re;
	}
	
	
	private HTML newHTMLSplitLine(String width) {
		return new HTML("<hr width = \"" + width + "\" size=\"3\" color=\"#62b0ff\" noshade>");
	}
	
	
}
