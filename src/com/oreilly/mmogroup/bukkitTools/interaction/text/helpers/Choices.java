package com.oreilly.mmogroup.bukkitTools.interaction.text.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.ContextDataRequired;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.GeneralInteractionError;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.PageFailure;


public class Choices {
	
	TreeMap< Integer, Choice > orderedChoices = new TreeMap< Integer, Choice >();
	HashMap< String, Choice > choicesByKey = new HashMap< String, Choice >();
	String prependToNumber = "";
	String appendToNumber = ". ";
	
	
	static public Choices getChoices( InteractionPage page, Interaction interaction ) throws ContextDataRequired {
		return interaction.getContextData( Choices.class, interaction, getChoiceKey( page ) );
	}
	
	
	static protected String getChoiceKey( Object page ) {
		return page.getClass().getName() + "_choices";
	}
	
	
	public Choices( InteractionPage page, Interaction interaction ) {
		interaction.context.put( getChoiceKey( page ), this );
	}
	
	
	void withAlias( Choice choice, String... aliasList ) {
		if ( aliasList != null )
			for ( String key : aliasList )
				choicesByKey.put( key, choice );
	}
	
	
	public Choices withNumberStyle( String prepend, String append ) {
		prependToNumber = prepend;
		appendToNumber = append;
		return this;
	}
	
	
	public ArrayList< String > generateChoiceList() {
		ArrayList< String > result = new ArrayList< String >();
		// make sure we don't have null values
		if ( prependToNumber == null )
			prependToNumber = "";
		if ( appendToNumber == null )
			appendToNumber = "";
		// making a list... 
		for ( Integer i : orderedChoices.keySet() ) {
			Choice choice = orderedChoices.get( i );
			result.add( prependToNumber + i + appendToNumber + choice.text );
		}
		return result;
	}
	
	
	public int getChoiceCount() {
		return orderedChoices.size();
	}
	
	
	public Choice addInternalChoice( String text, String returnValue ) {
		return addInternalChoice( orderedChoices.size() + 1, text, returnValue );
	}
	
	
	public Choice addInternalChoice( int number, String text, String returnValue ) {
		ChoiceInternal choice = new ChoiceInternal( this, text, returnValue );
		orderedChoices.put( number, choice );
		choicesByKey.put( Integer.toString( number ), choice );
		return choice;
	}
	
	
	public Choice addPageChoice( String text, InteractionPage... pages ) {
		return addPageChoice( orderedChoices.size() + 1, text, pages );
	}
	
	
	public Choice addPageChoice( int number, String text, InteractionPage... pages ) {
		ChoicePage choice = new ChoicePage( this, text, pages );
		orderedChoices.put( number, choice );
		choicesByKey.put( Integer.toString( number ), choice );
		return choice;
	}
	
	
	public Choice addAbortChoice( String text ) {
		return addAbortChoice( orderedChoices.size() + 1, text );
	}
	
	
	public Choice addAbortChoice( int number, String text ) {
		ChoiceAbort choice = new ChoiceAbort( this, text );
		orderedChoices.put( number, choice );
		choicesByKey.put( Integer.toString( number ), choice );
		return choice;
	}
	
	
	public Choice addCancel( int number ) {
		return addCancel( number, "Cancel" );
	}
	
	
	public Choice addCancel() {
		return addCancel( orderedChoices.size() + 1, "Cancel" );
	}
	
	
	public Choice addCancel( String text ) {
		return addCancel( orderedChoices.size() + 1, text );
	}
	
	
	public Choice addCancel( int number, String text ) {
		return addFail( number, text );
	}
	
	
	public Choice addFail( String text ) {
		return addFail( orderedChoices.size() + 1, text );
	}
	
	
	public Choice addFail( int number, String text ) {
		ChoiceFailure choice = new ChoiceFailure( this, text );
		orderedChoices.put( number, choice );
		choicesByKey.put( Integer.toString( number ), choice );
		return choice;
	}
	
	
	public String takeAction( InteractionPage page, Interaction interaction, Object data ) throws AbortInteraction,
			ContextDataRequired, GeneralInteractionError, PageFailure {
		String key = data.toString().trim();
		Choice choice = choicesByKey.get( key );
		if ( choice == null )
			throw new GeneralInteractionError( "Unable to determine your choice based on \"" + data.toString() + "\"" );
		if ( choice instanceof ChoiceAbort )
			throw new AbortInteraction();
		if ( choice instanceof ChoicePage ) {
			interaction.addPages( ( (ChoicePage)choice ).pages );
			interaction.context.remove( getChoiceKey( page ) );
			return null;
		}
		if ( choice instanceof ChoiceInternal ) {
			interaction.context.remove( getChoiceKey( page ) );
			return page.takeAction( interaction, ( (ChoiceInternal)choice ).returnValue );
		}
		if ( choice instanceof ChoiceFailure ) {
			interaction.context.remove( getChoiceKey( page ) );
			throw new PageFailure( ( (ChoiceFailure)choice ).message );
		}
		// otherwise...
		throw new GeneralInteractionError( "Unsupported choice type" );
	}
}
