package com.oreilly.mmogroup.interaction;

import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.bukkitTools.text.VariablePrefixer;
import com.oreilly.mmogroup.interaction.admin.ChangePlayersGroup;
import com.oreilly.mmogroup.interaction.admin.ChangePlayersSpecialisation;


public class AdminPlayerMenu extends InteractionPage {
	
	public AdminPlayerMenu() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) {
		Choices choices = new Choices( this, interaction );
		VariablePrefixer variable = new VariablePrefixer( this );
		choices.addPageChoice( variable.define( "ChangePlayerGroup" ), new ChangePlayersGroup() );
		choices.addPageChoice( variable.define( "ChangePlayerSpecial" ), new ChangePlayersSpecialisation() );
		return choices;
	}
	
}
