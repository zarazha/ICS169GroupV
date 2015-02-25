package com.teamv.capstone.game;

import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import com.teamv.capstone.scenes.BaseScene;
import com.teamv.capstone.utility.Point;

public abstract class Enemy extends HealthBarEntity{
	
	public static final int LEFTALIGN = 0;
	public static final int RIGHTALIGN = 1;

	public boolean isTarget = false;
	public boolean isDead = false;
	protected int turnCounter;
	protected int startTurnCount, currentTurnCount;
	
	private Point buffer;
	
	public Enemy(float x, float y, ITextureRegion region, VertexBufferObjectManager vbom) {
		super(x, y, region, vbom);
	}
	
	public Enemy(ITextureRegion region, VertexBufferObjectManager vbom){
		super(0, 0, region, vbom);
	}
	
	protected void init(){
//		startHealth = 10;
//		currentHealth = startHealth;
		buffer = new Point(100, 0);
		
		typeIcon.setPosition(this.getX(), this.getY());
		typeIcon.setWidth(40);
		typeIcon.setHeight(40);
		
//		startTurnCount = 3;
//		resetCurrentTurnCount();

		String healthBarStatus = "HP: " + currentHealth + "/" + startHealth;
		healthBarText.setPosition(this);
		healthBarText.setText(healthBarStatus);
		
		turnCountText.setPosition(this);
//		updateTurnCount();
		
		healthBarWidth  = (int) (this.getWidth() + buffer.x);
		healthBarHeight = 25;
		healthBar.setPosition(this.getX() + buffer.x, this.getY() - buffer.y);
		healthBar.setWidth(healthBarWidth);
		healthBar.setHeight(25);
		healthBar.setColor(Color.RED);
	}
	
	protected void setup(int health, int attack, int turnCounter, ColorType type, float scale){
		this.startHealth = health;
		this.attack = attack;
		this.turnCounter = turnCounter;
		this.setUserData(type);
		this.setScale(scale);
	}
	
	public void onDie(){
		Battleground.currentWave.remove(this);
		isDead = true;
		cleanUp();
		updateTurnCount();
	}
	
	public void cleanUp(){
		super.cleanUp();
	}
	
	public void attachToScene(BaseScene gameScene) {
		super.attachToScene(gameScene);
		gameScene.registerTouchArea(this);
	}
	
	public void setPosition(float pX, float pY){
		start.set(pX, pY);
		super.setPosition(pX, pY);
		healthBarText.setX(pX + (buffer.x));
		healthBarText.setY(pY - (buffer.y + 20));
		
		healthBar.setX(pX + buffer.x);
		healthBar.setY(pY - buffer.y);
		
		typeIcon.setX(pX);
		typeIcon.setY(pY - 10);
		
		turnCountText.setX(pX);
		turnCountText.setY(pY - (buffer.y + 20));
	}
	
	public void setScale(float scale){
		super.setScale(scale);
//		healthBarText.setScale(scale);
//		healthBar.setScale(scale);
//		turnCountText.setScale(scale);
//		typeIcon.setScale(scale);
	}
	
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float X, float Y) 
    {
		if (pSceneTouchEvent.isActionUp()){
			this.setColor(Color.WHITE);
			if(isTarget){
				this.clearEntityModifiers();
				this.isTarget = false;
				return true;
			}
			
			resetTarget();
			this.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new ScaleModifier(1.5f, 0.7f, 0.8f))));
			isTarget = true;
	    } else if(pSceneTouchEvent.isActionDown()){
			switch(this.getUserData().toString()){
			case "RED":
				this.setColor(Color.RED);
				break;
			case "BLUE":
				this.setColor(Color.BLUE);
				break;
			case "GREEN":
				this.setColor(Color.GREEN);
				break;
			case "YELLOW":
				this.setColor(Color.YELLOW);
				break;
			}
	    }
		
		return true;
    }
	
	private void resetTarget(){
		for(Enemy enemy : Battleground.currentWave.getEnemies()){
			enemy.clearEntityModifiers();
			enemy.isTarget = false;
		}
	}
	
	protected void setType(ColorType type){
		// temp typing
		switch(type){
		case RED:
//			this.setColor(Color.RED);
			this.typeIcon.setColor(Color.RED);
			break;
		case BLUE:
//			this.setColor(Color.BLUE);
			this.typeIcon.setColor(Color.BLUE);
			break;
		case GREEN:
//			this.setColor(Color.GREEN);
			this.typeIcon.setColor(Color.GREEN);
			break;
		case YELLOW:
//			this.setColor(Color.YELLOW);
			this.typeIcon.setColor(Color.YELLOW);
			break;
		}
	}
	
	public void updateTurnCount(){
		turnCountText.setText(currentTurnCount + "");
	}
	
	public int getCurrentTurnCount(){
		return currentTurnCount;
	}
	
	public void resetCurrentTurnCount(){
		currentTurnCount = startTurnCount;
	}
	
	public void decrementCurrentTurnCount(){
		if (currentTurnCount > 0)
			currentTurnCount--;
	}
}
