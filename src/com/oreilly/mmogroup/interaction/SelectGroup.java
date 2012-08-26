package com.oreilly.mmogroup.interaction;

import org.apache.commons.lang.WordUtils;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.error.AbortInteraction;
import com.oreilly.common.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.MMOGroup;


public class SelectGroup extends InteractionPage {
	
	public SelectGroup() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction {
		Choices choices = new Choices( this, interaction );
		for ( String groupName : MMOGroup.instance.groups.getAllGroupNames() ) {
			choices.addInternalChoice( WordUtils.capitalize( groupName ), groupName )
					.withAlias( WordUtils.capitalize( groupName ) )
					.withAlias( groupName.toLowerCase() );
		}
		return choices;
	}
	
	
	@Override
	public String takeAction( Interaction interaction, String key ) {
		interaction.context.put( Constants.SELECTED_GROUP, key );
		return null;
	}
}
