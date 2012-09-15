package com.oreilly.mmogroup.interaction;

import org.apache.commons.lang.WordUtils;

import com.oreilly.mmogroup.MMOGroup;
import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.PageFailure;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;


public class SelectGroup extends InteractionPage {
	
	public SelectGroup() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction, PageFailure {
		Choices choices = new Choices( this, interaction );
		for ( String groupName : MMOGroup.instance.groups.getAllGroupNames() ) {
			choices.addInternalChoice( groupName, groupName )
					.withAlias( WordUtils.capitalize( groupName ) )
					.withAlias( groupName.toLowerCase() );
		}
		if ( choices.getChoiceCount() == 0 )
			throw new PageFailure( "Right now, there are no groups to choose from" );
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String key ) {
		interaction.context.put( Constants.SELECTED_GROUP, key );
		return null;
	}
}
