package com.oreilly.mmogroup.bukkitTools.interaction.text;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.PageFailure;
import com.oreilly.mmogroup.bukkitTools.interaction.text.formatter.Formatter;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.PaginationAssistant;
import com.oreilly.mmogroup.bukkitTools.interaction.text.validator.Validator;
import com.oreilly.mmogroup.bukkitTools.text.VariableTool;


// TODO: "Loopback" method, which pops this page back into the front of the queue
// TODO: "Bookmark" method, which adds this page to the bookmark stack (for when player uses "back" command)
// TODO: Decision on pagination doesn't send proposed text to formatter - so misses lines added by border.

abstract public class InteractionPage {
	
	static public final int MAX_LINES = 20;
	
	public Formatter formatter = null;
	public Validator validator = null;
	public String validationFailedMessage = null;
	public HashMap< String, Object > style = new HashMap< String, Object >();
	
	// translation support
	public String translationKey = null;
	public boolean translatableBody = false;
	protected boolean translatableTitle = false;
	
	// title 
	public boolean hasTitle = false;
	protected String defaultTitle = "Title";
	
	// choices
	public boolean hasChoices = false;
	
	
	// variables
	
	public HashMap< String, Object > getVariables( Interaction interaction ) throws PageFailure, ContextDataRequired,
			GeneralInteractionError {
		return null;
	}
	
	// pagination
	
	public int maxLines = 20;
	
	
	// convience function
	
	public void autoPage() {
		translatableBody = true;
		hasTitle = true;
		translatableTitle = true;
	}
	
	
	protected void autoTitle() {
		hasTitle = true;
		translatableTitle = true;
	}
	
	
	// translations
	
	public String getTranslationKey( Interaction interaction ) throws PageFailure, ContextDataRequired,
			GeneralInteractionError {
		if ( translationKey != null )
			return translationKey;
		else {
			String customKey = getCustomTranslationKey( interaction );
			if ( customKey != null )
				return customKey;
			else
				return this.getClass().getSimpleName();
		}
	}
	
	
	// for times when the entire message may change depending on context
	public String getCustomTranslationKey( Interaction interaction ) throws PageFailure, ContextDataRequired,
			GeneralInteractionError {
		return null;
	}
	
	
	// Title functions
	
	public String titleOverrideKey( Interaction interaction ) throws PageFailure, ContextDataRequired,
			GeneralInteractionError {
		return getTranslationKey( interaction ) + ".titleOverride";
	}
	
	
	public String getTitle( Interaction interaction ) throws PageFailure, ContextDataRequired, GeneralInteractionError {
		Object overrideObj = interaction.context.get( titleOverrideKey( interaction ) );
		if ( overrideObj != null )
			return overrideObj.toString();
		else {
			if ( translatableTitle )
				return VariableTool.variable( getTranslationKey( interaction ) + ".title" );
			else
				return defaultTitle;
		}
	}
	
	
	public String getDisplayText( Interaction interaction ) throws ContextDataRequired,
			GeneralInteractionError, AbortInteraction, PageFailure {
		// TOOD: Pagination support...
		// see if we have an active paginator
		PaginationAssistant paginator = interaction.getContextData(
				PaginationAssistant.class, interaction, getTranslationKey( interaction ) + "_paginator" );
		if ( paginator != null ) {
			return paginator.getDisplayText();
		} else {
			// generate page content, and if there's too much, then activate a paginator..
			if ( hasChoices ) {
				Choices choices = Choices.getChoices( this, interaction );
				if ( choices == null )
					choices = generateChoices( interaction );
				String text = generateDisplayBody( interaction ) + "\n";
				String choiceText = interaction.parseMessage(
						StringUtils.join( choices.generateChoiceList(), "" ) );
				if ( text.split( "\n" ).length + choiceText.split( "\n" ).length + 1 > maxLines ) {
					// we'll need a paginator
					paginator = new PaginationAssistant( choiceText, maxLines, text );
					interaction.context.put( getTranslationKey( interaction ) + "_paginator", paginator );
					return paginator.getDisplayText();
				} else
					return text + "\n" + choiceText;
			} else {
				String text = generateDisplayBody( interaction );
				if ( text.split( "\n" ).length > maxLines ) {
					paginator = new PaginationAssistant( text, maxLines, "" );
					interaction.context.put( getTranslationKey( interaction ) + "_paginator", paginator );
					return paginator.getDisplayText();
				} else
					return text;
			}
		}
	}
	
	
	public Choices generateChoices( Interaction interaction ) throws ContextDataRequired,
			GeneralInteractionError, AbortInteraction, PageFailure {
		return null;
	}
	
	
	public String generateDisplayBody( Interaction interaction ) throws PageFailure, ContextDataRequired,
			GeneralInteractionError {
		String text = null;
		if ( translatableBody )
			text = VariableTool.variable( getTranslationKey( interaction ) + ".text" );
		else
			text = getText();
		return interaction.parseMessage( text );
	}
	
	
	public String getText() {
		return "";
	}
	
	
	public String inputHandler( Interaction interaction, Object data ) throws ContextDataRequired,
			GeneralInteractionError, AbortInteraction, PageFailure {
		// first, check for pagination...
		PaginationAssistant paginator = interaction.getContextData(
				PaginationAssistant.class, interaction, getTranslationKey( interaction ) + "_paginator" );
		if ( paginator != null ) {
			boolean paginationCommand = paginator.processPageCommand( data.toString() );
			if ( paginationCommand ) {
				interaction.holdInteraction = true;
				return null;
			} else {
				// it wasn't a pagination command, so we kill the paginator
				// so we don't end up with a cache of old data if the player
				// comes back to this interaction page
				interaction.context.remove( getTranslationKey( interaction ) + "_paginator" );
			}
		}
		// if we don't have pagination (or it wasn't a pagination command..)
		//  see if we are using a choice model, or straight to direct input
		if ( hasChoices ) {
			Choices choices = Choices.getChoices( this, interaction );
			return choices.takeAction( this, interaction, data );
		} else
			return acceptValidatedInput( interaction, data );
	}
	
	
	// Direct Input model:
	// if a string is returned, it is shown to the player
	public String acceptValidatedInput( Interaction interaction, Object data ) throws ContextDataRequired,
			GeneralInteractionError, AbortInteraction, PageFailure {
		return null;
	}
	
	
	// Choice model:
	// as above, a string return is shown to the player
	public String takeAction( Interaction interaction, String key ) throws ContextDataRequired,
			GeneralInteractionError, AbortInteraction, PageFailure {
		return null;
	}
	
	
	// chained init methods
	
	public InteractionPage withFormatter( Formatter formatter ) {
		if ( this.formatter == null )
			this.formatter = formatter;
		else
			this.formatter.chain( formatter );
		return this;
	}
	
	
	public InteractionPage withValidator( Validator validator ) {
		if ( this.validator == null )
			this.validator = validator;
		else
			this.validator.chain( validator );
		return this;
	}
	
	
	public InteractionPage withReplacementFormatter( Formatter formatter ) {
		this.formatter = formatter;
		return this;
	}
	
	
	public InteractionPage withReplacementValidator( Validator validator ) {
		this.validator = validator;
		return this;
	}
	
	
	public InteractionPage withValidationFailedMessage( String message ) {
		this.validationFailedMessage = message;
		return this;
	}
	
	
	public InteractionPage withStyle( String key, Object style ) {
		this.style.put( key, style );
		return this;
	}
	
	
	public InteractionPage withStyles( HashMap< String, Object > source ) {
		style.putAll( source );
		return this;
	}
	
}
