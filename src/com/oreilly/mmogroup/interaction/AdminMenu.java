package com.oreilly.mmogroup.interaction;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.helpers.Choices;
import com.oreilly.common.text.VariablePrefixer;
import com.oreilly.mmogroup.interaction.admin.CreateGroup;
import com.oreilly.mmogroup.interaction.admin.DeleteGroup;
import com.oreilly.mmogroup.interaction.admin.ModifyGroup;


// TODO:
public class AdminMenu extends InteractionPage {
	
	public AdminMenu() {
		autoPage();
		hasChoices = true;
	}
	
	
	@Override
	public String getTranslationKey() {
		return "AdminMenu";
	}
	
	
	@Override
	public Choices generateChoices( Interaction interaction ) {
		Choices choices = new Choices( this, interaction );
		VariablePrefixer variable = new VariablePrefixer( this );
		choices.addPageChoice( variable.define( "CreateNewGroup" ), new CreateGroup() );
		choices.addPageChoice( variable.define( "ModifyGroup" ), new SelectGroup(), new ModifyGroup() );
		choices.addPageChoice( variable.define( "DeleteGroup" ), new SelectGroup(), new DeleteGroup() );
		choices.addPageChoice( variable.define( "ChangeSettings" ), new SettingsMenu() );
		choices.addPageChoice( variable.define( "ModifyPlayer" ), new SelectPlayer(), new AdminPlayerMenu() );
		return choices;
	}
}
