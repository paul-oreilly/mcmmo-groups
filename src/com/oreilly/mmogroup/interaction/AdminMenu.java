package com.oreilly.mmogroup.interaction;

import com.oreilly.mmogroup.bukkitTools.interaction.text.Interaction;
import com.oreilly.mmogroup.bukkitTools.interaction.text.InteractionPage;
import com.oreilly.mmogroup.bukkitTools.interaction.text.helpers.Choices;
import com.oreilly.mmogroup.bukkitTools.text.VariablePrefixer;
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
	public Choices generateChoices( Interaction interaction ) {
		Choices choices = new Choices( this, interaction );
		VariablePrefixer variable = new VariablePrefixer( this, interaction );
		choices.addPageChoice( variable.define( "CreateNewGroup" ), new CreateGroup(), new ModifyGroup(),
				new AdminMenu() );
		choices.addPageChoice( variable.define( "ModifyGroup" ), new SelectGroup(), new ModifyGroup(), new AdminMenu() );
		choices.addPageChoice( variable.define( "DeleteGroup" ), new SelectGroup(), new DeleteGroup(), new AdminMenu() );
		choices.addPageChoice( variable.define( "ChangeSettings" ), new SettingsMenu(), new AdminMenu() );
		choices.addPageChoice( variable.define( "ModifyPlayer" ), new SelectPlayer(), new AdminPlayerMenu(),
				new AdminMenu() );
		return choices;
	}
}
