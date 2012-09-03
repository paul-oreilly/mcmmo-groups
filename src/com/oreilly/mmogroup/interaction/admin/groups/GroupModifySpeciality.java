package com.oreilly.mmogroup.interaction.admin.groups;

import java.util.HashMap;

import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.error.AbortInteraction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.bukkitTools.text.VariablePrefixer;
import com.oreilly.mmogroup.interaction.helpers.GroupHelper;


public class GroupModifySpeciality extends InteractionPage {
	
	public GroupModifySpeciality() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public HashMap< String, Object > getVariables( Interaction interaction ) {
		GroupHelper helper = new GroupHelper( interaction );
		return helper.getVariables();
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) throws AbortInteraction {
		Choices choices = new Choices( this, interaction );
		VariablePrefixer variable = new VariablePrefixer( this, interaction );
		choices.addPageChoice( variable.define( "ChangeName" ),
				new GroupModifySpecialName() );
		choices.addPageChoice( variable.define( "ChangeSkill" ),
				new GroupModifySpecialSkill() );
		choices.addPageChoice( variable.define( "ChangeAmount" ),
				new GroupModifySpecialAmount() );
		return choices;
	}
	
}
