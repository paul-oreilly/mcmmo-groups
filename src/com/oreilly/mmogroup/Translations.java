package com.oreilly.mmogroup;

import java.util.HashMap;

import com.oreilly.common.text.Translater;


public class Translations {
	
	public MMOGroup plugin;
	public Translater translater = null;
	
	
	public Translations( MMOGroup plugin ) {
		this.plugin = plugin;
		translater = new Translater( plugin.config.translationDirectory, plugin.log );
	}
	
	
	public HashMap< String, String > getTranslations() {
		return getTranslations( plugin.config.defaultTranslation );
	}
	
	
	public HashMap< String, String > getTranslations( String name ) {
		return translater.getTranslation( name );
	}
	
}
