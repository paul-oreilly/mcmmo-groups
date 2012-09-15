package com.oreilly.mmogroup.bukkitTools.interaction.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.Plugin;

import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.InterfaceDependencyError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.PageFailure;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ValidationFailedError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.formatter.Formatter;
import com.oreilly.mmogroup.bukkitTools.interaction.text.validator.Validator;
import com.oreilly.mmogroup.bukkitTools.text.ColorTool;
import com.oreilly.mmogroup.bukkitTools.text.MessageTool;
import com.oreilly.mmogroup.bukkitTools.text.Translater;
import com.oreilly.mmogroup.bukkitTools.text.VariableTool;


public class Interaction {
	
	public static final boolean DEBUG = false;
	
	enum MessageType {
		NORMAL, DEBUG, ERROR, RESPONSE;
	}
	
	static public final String STYLE_ERROR_COLOR = "errorColor";
	static public ChatColor defaultErrorColor = ChatColor.DARK_RED;
	
	static public HashMap< CommandSender, Interaction > currentInteractions =
			new HashMap< CommandSender, Interaction >();
	static private boolean eventListenerRegisterd = false;
	
	// interaction-wide text processors 
	public Formatter formatter = null;
	public Validator validator = null;
	
	// pages holds the next set of InteractionPage objects to show the user
	public ArrayList< InteractionPage > pages = new ArrayList< InteractionPage >();
	
	// history is used for the 'back' function, to return to the previous page
	public ArrayList< InteractionPage > history = new ArrayList< InteractionPage >();
	
	// TODO: Add timeout support
	public int timeout = 20; // seconds
	
	// subject of the interaction
	public CommandSender user = null;
	
	// command strings
	public Set< String > exitStrings = new HashSet< String >();
	public Set< String > returnStrings = new HashSet< String >();
	
	// a buffer holding all the player chat that would otherwise interrupt the interaction
	public List< String > chatBuffer = new ArrayList< String >();
	
	// style and replacement settings
	public HashMap< String, Object > style = new HashMap< String, Object >();
	public HashMap< String, Object > variables = new HashMap< String, Object >();
	public HashMap< String, ChatColor > tagsToColors = new HashMap< String, ChatColor >();
	
	// translation service, and which translation to use 
	public Translater translator = null;
	public String translation = null;
	public HashMap< String, String > translationValues = null;
	
	// default text colors for each message type
	public HashMap< MessageType, ArrayList< ChatColor >> messageStyles =
			new HashMap< MessageType, ArrayList< ChatColor >>();
	
	// called if current page is still 'holding' the player interaction
	public boolean holdInteraction = false;
	
	// for holding any data required
	public HashMap< String, Object > context = new HashMap< String, Object >();
	
	// the current page 
	public InteractionPage currentPage = null;
	
	// is the interaction about to end? (Delayed for any last messages)
	public boolean queueEndOfInteraction = false;
	
	// at the end of the interaction, after the history is blanked, what is the last thing
	// a player should see? (Before the chat history catches up!)
	public String lastMessage = null;
	
	// set of 'bookmarks' - on PageFailure and also on reaching the end of the interaction queue,
	//  the interaction comes back to the latest bookmark. 
	// (any of the returnStrings, if used on the currently bookmarked page, will remove that bookmark)
	public LinkedList< InteractionPage > bookmarked = new LinkedList< InteractionPage >();
	
	
	static public boolean registerEventListener( Plugin plugin ) {
		if ( eventListenerRegisterd )
			return false;
		else {
			plugin.getServer().getPluginManager().registerEvents( new EventHook(), plugin );
			return true;
		}
	}
	
	
	// returns true if the message was for an interaction, and was passed on.
	static public boolean chatEvent( PlayerChatEvent event ) {
		// for every active interaction, hold the chat event in a buffer for later delivery
		Set< Player > recipients = event.getRecipients();
		for ( Interaction interaction : currentInteractions.values() ) {
			if ( recipients.remove( interaction.user ) )
				if ( interaction.user != event.getPlayer() )
					interaction.chatBuffer.add( event.getPlayer().getDisplayName() + ":" + event.getMessage() );
		}
		// see if the player who talked is currently interacting
		Interaction interaction = currentInteractions.get( event.getPlayer() );
		if ( interaction == null )
			return false;
		// pass the message to the interaction, and cancel the event
		interaction.acceptInput( event.getMessage() );
		event.setCancelled( true );
		return true;
	}
	
	
	public Interaction( CommandSender user ) {
		this();
		this.user = user;
		currentInteractions.put( user, this );
		// load default message colors
		withMessageStyle( MessageType.NORMAL, ChatColor.WHITE );
		withMessageStyle( MessageType.DEBUG, ChatColor.BLUE, ChatColor.ITALIC );
		withMessageStyle( MessageType.ERROR, ChatColor.DARK_RED );
		withMessageStyle( MessageType.RESPONSE, ChatColor.GRAY );
	}
	
	
	protected Interaction() {
		
	}
	
	
	public void endOfInteraction() {
		// let user know the interaction is finished...
		if ( user instanceof Player )
			for ( int i = 0; i < 20; i++ )
				user.sendMessage( " " );
		// repeat the last message
		if ( lastMessage != null )
			for ( String line : lastMessage.split( "\n" ) )
				user.sendMessage( line );
		// send any messages from other players
		sendQueuedMessages();
		// reset data values for easier garbage collection.
		currentInteractions.remove( user );
		formatter = null;
		validator = null;
		pages = null;
		exitStrings = null;
		user = null;
		chatBuffer = null;
	}
	
	
	public void acceptInput( String input ) {
		String universalInput = input.toLowerCase().trim();
		// exit the conversation if input matches one of the exit strings
		if ( exitStrings.contains( universalInput ) )
			queueEndOfInteraction = true;
		// return to the previous page, if input matches one of the return strings
		if ( ( !queueEndOfInteraction ) & ( returnStrings.contains( universalInput ) ) ) {
			// if we are on a bookmark, then remove it.
			if ( bookmarked.size() > 0 )
				if ( bookmarked.getLast() == currentPage )
					bookmarked.removeLast();
			// add the current page back into the queue
			pages.add( 0, currentPage );
			// set the previous page as the current one 
			if ( history.size() > 1 ) {
				currentPage = history.remove( history.size() - 1 );
				display();
			} else
				queueEndOfInteraction = true;
		}
		// exit the conversation if there is no current page
		if ( currentPage == null )
			queueEndOfInteraction = true;
		try {
			if ( !queueEndOfInteraction ) {
				// validate input
				Object validatedInput = input;
				if ( validator != null )
					validatedInput = validator.startValidation( validatedInput, currentPage );
				if ( currentPage.validator != null )
					validatedInput = currentPage.validator.startValidation( validatedInput, currentPage );
				// pass input to the page to take action on
				String reply = currentPage.inputHandler( this, validatedInput );
				// progress to the next page.. unless the current page has a 'lock' on interaction
				if ( holdInteraction ) {
					holdInteraction = false;
				} else {
					history.add( currentPage );
					if ( pages.size() > 0 )
						currentPage = pages.remove( 0 );
					else if ( bookmarked.size() > 0 )
						currentPage = bookmarked.getLast();
					else
						currentPage = null;
				}
				// show the next / repeat page
				display();
				// if we had a reply from the last page, show it now (so the bottom of this page)
				if ( reply != null ) {
					if ( !reply.isEmpty() )
						sendMessage( MessageType.RESPONSE, user, reply );
				} else
					lastMessage = null;
			}
			// the actual end of the interaction happens last, so any final messages can be sent.
			if ( queueEndOfInteraction )
				endOfInteraction();
		} catch ( ValidationFailedError error ) {
			onValidationFailedError( error, input );
		} catch ( InterfaceDependencyError error ) {
			sendMessage( MessageType.ERROR, user, "Unmet interface dependency " + error.interfaceRequired );
		} catch ( ContextDataRequired error ) {
			onContextDataRequiredError( error );
		} catch ( GeneralInteractionError error ) {
			onGeneralInteractionError( error );
		} catch ( AbortInteraction error ) {
			onAbortInteractionError( error );
		} catch ( PageFailure error ) {
			onPageFailureError( error );
		}
	}
	
	
	private void onValidationFailedError( ValidationFailedError error, String input ) {
		// display the current page again
		display();
		// show what went wrong last time
		if ( currentPage.validationFailedMessage != null )
			sendValidationError( currentPage.validationFailedMessage, input );
		else
			sendValidationError( error.message, input );
	}
	
	
	private void onContextDataRequiredError( ContextDataRequired error ) {
		// show the previous page, then an error about context
		if ( history.size() > 1 ) {
			currentPage = history.remove( history.size() - 1 );
			display();
		}
		sendMessage( MessageType.ERROR, user, "Unable to display page, as required context " +
				error.key + "(" + error.classType + ") does not exist" );
	}
	
	
	private void onGeneralInteractionError( GeneralInteractionError error ) {
		if ( history.size() > 1 ) {
			currentPage = history.remove( history.size() - 1 );
			display();
		}
		sendMessage( MessageType.ERROR, user, error.reason );
	}
	
	
	private void onAbortInteractionError( AbortInteraction error ) {
		lastMessage = ( error.message != null ) ? error.message : null;
		endOfInteraction();
	}
	
	
	private void onPageFailureError( PageFailure error ) {
		lastMessage = ( error.reason != null ) ? error.reason : null;
		// remove any queued pages
		pages.clear();
		if ( bookmarked.size() > 0 ) {
			// if the current page is the next bookmark, remove that bookmark
			if ( currentPage == bookmarked.getLast() )
				bookmarked.removeLast();
			// remove any future pages, and return to the lastest bookmark
			currentPage = bookmarked.getLast();
			display();
			if ( error.reason != null )
				sendMessage( MessageType.RESPONSE, user, error.reason );
		} else {
			endOfInteraction();
		}
	}
	
	
	protected void sendValidationError( String message, String input ) {
		// get the colour for errors
		Object rawStyle = style.get( Interaction.STYLE_ERROR_COLOR );
		String errorColor = null;
		if ( rawStyle instanceof ChatColor )
			errorColor = ( (ChatColor)rawStyle ).toString();
		else
			errorColor = defaultErrorColor.toString();
		// replace variables that may be in the message
		message = errorColor + message.replace( "%input", input );
		// send the message to the player
		sendMessage( MessageType.ERROR, user, message );
	}
	
	
	public void begin() {
		if ( pages.size() == 0 ) {
			sendMessage( MessageType.DEBUG, user, "No pages exist for this interaction" );
			return;
		}
		// get the translation data for this interaction...
		if ( translator != null )
			if ( translation != null )
				translationValues = translator.getTranslation( translation );
		// show the page
		currentPage = pages.remove( 0 );
		display();
	}
	
	
	protected void display() {
		if ( currentPage == null ) {
			queueEndOfInteraction = true;
			return;
		}
		currentPage.style.putAll( style );
		try {
			String currentDisplay = currentPage.getDisplayText( this );
			if ( currentPage.formatter != null )
				currentDisplay = currentPage.formatter.startFormatting( currentDisplay, currentPage, this );
			if ( formatter != null )
				currentDisplay = formatter.startFormatting( currentDisplay, currentPage, this );
			// send the display to the user
			sendMessage( MessageType.NORMAL, user, currentDisplay );
		} catch ( ContextDataRequired error ) {
			onContextDataRequiredError( error );
		} catch ( GeneralInteractionError error ) {
			onGeneralInteractionError( error );
		} catch ( AbortInteraction error ) {
			onAbortInteractionError( error );
		} catch ( PageFailure error ) {
			onPageFailureError( error );
		}
	}
	
	
	// methods to make pages easier
	
	public Interaction nextPage( InteractionPage page ) {
		pages.add( 0, page );
		return this;
	}
	
	
	public Interaction addPages( Collection< InteractionPage > collection ) {
		pages.addAll( 0, collection );
		return this;
	}
	
	
	public Interaction addPages( InteractionPage... pageList ) {
		addPages( Arrays.asList( pageList ) );
		return this;
	}
	
	
	// chained init methods
	
	public Interaction withMessageStyle( MessageType type, ChatColor... style ) {
		ArrayList< ChatColor > styleList = new ArrayList< ChatColor >();
		for ( ChatColor c : style )
			styleList.add( c );
		messageStyles.put( type, styleList );
		return this;
	}
	
	
	public Interaction withMessageStyles( HashMap< MessageType, ArrayList< ChatColor >> styles ) {
		for ( MessageType type : styles.keySet() ) {
			ArrayList< ChatColor > source = styles.get( type );
			ArrayList< ChatColor > list = new ArrayList< ChatColor >();
			if ( source != null ) {
				for ( ChatColor c : source )
					list.add( c );
				messageStyles.put( type, list );
			}
		}
		return this;
	}
	
	
	public Interaction withFormatter( Formatter formatter ) {
		if ( this.formatter == null )
			this.formatter = formatter;
		else
			this.formatter.chain( formatter );
		return this;
	}
	
	
	public Interaction withValidator( Validator validator ) {
		if ( this.validator == null )
			this.validator = validator;
		else
			this.validator.chain( validator );
		return this;
	}
	
	
	public Interaction withReplacementFormatter( Formatter formatter ) {
		this.formatter = formatter;
		return this;
	}
	
	
	public Interaction withReplacementValidator( Validator validator ) {
		this.validator = validator;
		return this;
	}
	
	
	public Interaction withExitStrings( String... sequence ) {
		for ( String item : sequence )
			exitStrings.add( item );
		return this;
	}
	
	
	public Interaction withExitStrings( Set< String > set ) {
		exitStrings.addAll( set );
		return this;
	}
	
	
	public Interaction withReturnStrings( String... sequence ) {
		for ( String item : sequence )
			returnStrings.add( item );
		return this;
	}
	
	
	public Interaction withReturnStrings( Set< String > set ) {
		returnStrings.addAll( set );
		return this;
	}
	
	
	public Interaction withPages( ArrayList< InteractionPage > pages ) {
		this.pages = pages;
		return this;
	}
	
	
	public Interaction withPages( InteractionPage... pageList ) {
		addPages( pageList );
		return this;
	}
	
	
	public Interaction withTimeout( int timeout ) {
		this.timeout = timeout;
		return this;
	}
	
	
	public Interaction withStyle( String key, Object style ) {
		this.style.put( key, style );
		return this;
	}
	
	
	public Interaction withStyles( HashMap< String, Object > source ) {
		style.putAll( source );
		return this;
	}
	
	
	public Interaction withTranslator( Translater translator, String translation ) {
		this.translator = translator;
		this.translation = translation;
		return this;
	}
	
	
	// Helper functions...
	
	public < T > T getContextData( Class< T > tClass, Interaction interaction, String key ) throws ContextDataRequired {
		return getContextData( tClass, interaction, key, false );
	}
	
	
	@SuppressWarnings("unchecked")
	public < T > T getContextData( Class< T > tClass, Interaction interaction, String key, boolean throwError )
			throws ContextDataRequired {
		Object obj = interaction.context.get( key );
		if ( obj == null ) {
			if ( throwError )
				throw new ContextDataRequired( key, tClass );
			else
				return null;
		}
		if ( obj.getClass().isAssignableFrom( tClass ) )
			return (T)obj;
		else {
			if ( throwError )
				throw new ContextDataRequired( key, tClass );
			else
				return null;
		}
	}
	
	
	public void addBookmark( InteractionPage page ) {
		if ( bookmarked.size() > 0 ) {
			if ( bookmarked.getLast() != page )
				bookmarked.add( page );
		} else
			bookmarked.add( page );
	}
	
	
	public void removeLastBookmark() {
		if ( bookmarked.size() > 0 )
			bookmarked.removeLast();
	}
	
	
	public void removeLastBookmarkIfMatched( InteractionPage page ) {
		if ( bookmarked.size() > 0 )
			if ( bookmarked.getLast() == page )
				bookmarked.removeLast();
	}
	
	
	public String parseMessage( String message ) throws PageFailure, ContextDataRequired, GeneralInteractionError {
		if ( DEBUG ) {
			StackTraceElement element = Thread.currentThread().getStackTrace()[3];
			System.out.println( "ParseMessage caller is " + element.getClassName() + "." + element.getMethodName() +
					":" + element.getLineNumber() );
		}
		HashMap< String, Object > combinedVariables = new HashMap< String, Object >();
		// add the translation data
		if ( translationValues != null )
			combinedVariables.putAll( translationValues );
		// add interaction level variables
		combinedVariables.putAll( variables ); //TODO: Have interaction add things like "playername" etc
		if ( DEBUG ) {
			System.out.println( "Interaction variables are:\n" );
			for ( String key : variables.keySet() )
				System.out.println( "  " + key + ": " + variables.get( key ) );
		}
		// add variables from the current page
		if ( currentPage != null ) {
			HashMap< String, Object > pageVariables = currentPage.getVariables( this );
			if ( pageVariables != null ) {
				combinedVariables.putAll( pageVariables );
				if ( DEBUG ) {
					System.out.println( "Page variables are:\n" );
					for ( String key : pageVariables.keySet() )
						System.out.println( "  " + key + ": " + pageVariables.get( key ) );
				}
			}
		}
		// apply variables to the text
		message = VariableTool.applyVariables( combinedVariables, message );
		// Replace style tags...
		for ( String key : tagsToColors.keySet() ) {
			message = message.replace( MessageTool.tagOpen( key ), ColorTool.begin( tagsToColors.get( key ) ) );
			message = message.replace( MessageTool.tagClose( key ), ColorTool.end() );
		}
		// strip out any remaining tags
		message = MessageTool.stripTags( message );
		// add color
		message = ColorTool.apply( message );
		return message;
	}
	
	
	// internal methods
	
	protected void sendQueuedMessages() {
		for ( String message : chatBuffer )
			user.sendMessage( message.split( "\n" ) );
	}
	
	
	protected void sendMessage( MessageType type, CommandSender to, String message ) {
		// TODO: depending on type, add a color style
		try {
			message = parseMessage( message );
			// TODO: Use a log object for these errors (and others)
		} catch ( PageFailure e ) {
			MessageTool.sendToUser( to, "PARSE ERROR: Page Failure.\n" + message );
		} catch ( ContextDataRequired e ) {
			MessageTool.sendToUser( to, "PARSE ERROR: Context data required. Expected " + e.key +
					" (" + e.classType + ")\n" + message );
		} catch ( GeneralInteractionError e ) {
			MessageTool.sendToUser( to, "PARSE ERROR: " + e.reason + "\n" + message );
		}
		// send to user
		MessageTool.sendToUser( to, message );
		lastMessage = message;
	}
}
