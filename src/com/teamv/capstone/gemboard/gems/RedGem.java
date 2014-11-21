package com.teamv.capstone.gemboard.gems;

import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.teamv.capstone.ResourcesManager;

public class RedGem extends Gem{
	
	public RedGem(float pX, float pY, VertexBufferObjectManager vbo, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().red_gem, vbo, physicsWorld);
		
		setUserData("Red");
	}
}
