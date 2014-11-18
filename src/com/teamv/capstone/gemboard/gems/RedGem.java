package com.teamv.capstone.gemboard.gems;

import com.teamv.capstone.ResourcesManager;

public class RedGem extends Gem{
	
	public RedGem(int col, int row) {
		super(col, row);
		
		setSprite(ResourcesManager.getInstance().red_gem);
	}

	public String toString(){
		return "Red";
	}
}
