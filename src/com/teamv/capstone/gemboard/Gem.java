package com.teamv.capstone.gemboard;

import org.andengine.entity.primitive.Line;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.teamv.capstone.game.ColorType;
import com.teamv.capstone.gemboard.gems.SpecialGem;
import com.teamv.capstone.managers.ResourcesManager;
import com.teamv.capstone.managers.SceneManager;
import com.teamv.capstone.utility.Pointf;

public abstract class Gem extends Sprite{

	protected Pointf start		= Gemboard.getStartPoint();
	protected Pointf end		= Gemboard.getEndPoint();
	public Body body;
	private int row, col;
	private boolean isDropping = false;
	private float targetY;
	
	public Gem(int col, int row, float pX, float pY, ITextureRegion region, VertexBufferObjectManager vbo, PhysicsWorld physicsWorld){
		super(pX, pY, region, vbo);
		
		this.col = col;
		this.row = row;
		
		// gem size
		setWidth(Gemboard.RADIUS);
		setHeight(Gemboard.RADIUS);
		
		targetY = pY;
		
		createPhysics(physicsWorld);
	}
	
	
	private void createPhysics(PhysicsWorld physicsWorld)
	{        
	    body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.KinematicBody, PhysicsFactory.createFixtureDef(0.5f, 0, 0.5f));
	    body.setFixedRotation(true);
	    
	    physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
	    {
	        @Override
	        public void onUpdate(float pSecondsElapsed)
	        {
	            super.onUpdate(pSecondsElapsed);
	            
	            if(mY >= targetY){
            		isDropping = false;
            	}
	            if(isDropping){
	            	body.setLinearVelocity(new Vector2(0, 50f));
//	            	isDropping = false;
	            } else{
	            	body.setLinearVelocity(new Vector2(0, 0));
	            	alignGem();
	            }
	        }
	    });
	}
	
	/*
	 *	needed to align the gems since gravity doesn't pull perfectly 
	 */
	private void alignGem(){
		float y = Gemboard.STARTY + row * (Gemboard.RADIUS);
		if(col%2 != 0){
			y += Gemboard.RADIUS/2;
		}
		setY(y);
	}

	public void drop(){
		row++;
		this.setY(mY + Gemboard.RADIUS);
		targetY = getY();
		isDropping = true;
		}


	// what happens when gem dies, cleans up
	public void cleanUp(){
		if(!this.isDisposed()){
			this.dispose();
		}
		if(this.hasParent()){
			this.detachSelf();
		}
	}
	
	@Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float X, float Y) 
    {
		if(!isDropping)
		{
			// when finger touches gem
		    if (pSceneTouchEvent.isActionDown())
		    {
		    	ResourcesManager.getInstance().gemSelectSound.play();
		    	start.set(mX + Gemboard.RADIUS/2, mY + Gemboard.RADIUS/2);
	    		Gemboard.setCurrentColor(this);
	    		Gemboard.setCurrentSpecial(ColorType.NONE);
		    	
	    		Gemboard.connectedGems.add(this);
		    	highlightGem();
		    	SceneManager.getInstance().getGameScene().gemboard.shadeBoard(this);
//		    	System.out.println("Added " + this.getUserData());
//	        	System.out.println(start.x + " " + start.y);
		    }
		    if (pSceneTouchEvent.isActionMove())
		    {
		    	end.set(mX + Gemboard.RADIUS/2, mY + Gemboard.RADIUS/2);
	        	//System.out.println(end.x + " " + end.y);
		    	drawLine(this.getVertexBufferObjectManager());
		    }
		    // when finger releases gem
		    if (pSceneTouchEvent.isActionUp())
		    {
		    	if (Gemboard.connectedGems.size() < 3){
		    		Gemboard.connectedGems.clear();
		    	}
		    	drawLine(this.getVertexBufferObjectManager());
		    	SceneManager.getInstance().getGameScene().gemboard.handleMove();
		    }
		}
        return true;
    };
    
	protected void drawLine(VertexBufferObjectManager vbom){
		//Log.d("MyActivity", "Current Color: " + Gemboard.getCurrentColor() + ", Current Special: " + Gemboard.getCurrentSpecial());
		
    	/* location is adjacent to gem, gem list is not empty
		 * gem is matching colors, gem is not already in the chain */
		if( isAdjacent() &&
			!Gemboard.connectedGems.isEmpty() &&
			//this.possibleColor() &&
			//this.getUserData().equals(Gemboard.getCurrentColor()) || this.getUserData().equals(ColorType.BOMB) &&
			this.sameColor(Gemboard.connectedGems.get(Gemboard.connectedGems.size() - 1)) &&
			!Gemboard.connectedGems.contains(this)){
			
			highlightGem();
			
        	Line line = new Line(start.x, start.y, end.x, end.y, vbom);

        	line.setLineWidth(5);
        	line.setColor(Color.YELLOW);
            SceneManager.getInstance().getCurrentScene().attachChild(line);
            Gemboard.lines.add(line);
            
            // set point to middle of gem
            start.setX(getX() + Gemboard.RADIUS/2);
        	start.setY(getY() + Gemboard.RADIUS/2);
        	
        	Gemboard.connectedGems.add(this);
    	}
		
		/* gem list is not empty, gem is matching colors,
		 * gem is already in the chain, gem is not at end of list */
		else if ( !Gemboard.connectedGems.isEmpty() &&
				  this.sameColor(Gemboard.connectedGems.get(Gemboard.connectedGems.size() - 1)) &&
				  Gemboard.connectedGems.contains(this) &&
				  this != Gemboard.connectedGems.get(Gemboard.connectedGems.size() - 1)) {
			
			ResourcesManager.getInstance().gemSelectSound.play();
			
			// Store the location of this gem in connectedGems
			int index = Gemboard.connectedGems.indexOf(this);
			
			// Iterate down the list to the gem you're on now
			for (int i = Gemboard.connectedGems.size() - 1; i > index; i--) {
				
				Gemboard.connectedGems.get(i).revertColor();
				
				// Remove each gem past current index from connectedGems
				Gemboard.connectedGems.remove(i);
				
				// Store the line
				if(Gemboard.lines.size() >= 0){
					Line line = null;
					if(Gemboard.lines.size() - 1 == 0)
						line = Gemboard.lines.get(0);
					else
						line = Gemboard.lines.get(Gemboard.lines.size()-1);
					
					// Remove the line from lines ArrayList
					Gemboard.lines.remove(i-1);
					
					// Eliminate the line
					line.detachSelf();
					line.dispose();
					line = null;
				}
			
				
				// set point to middle of gem
	            start.setX(getX() + Gemboard.RADIUS/2);
	        	start.setY(getY() + Gemboard.RADIUS/2);
			}
		}
	}
	
	// determines if the gems are the same
	public boolean sameColor(Gem gem) {
		return(this.getUserData().equals(Gemboard.getCurrentColor())
				|| this.getUserData().equals(ColorType.BOMB) 
				|| this.getUserData().equals(ColorType.POTION));
	}
	
	// determines if the move is possible
	protected boolean possibleColor() {
		if ((this.getUserData().equals(Gemboard.getCurrentColor()) || Gemboard.getCurrentColor() == ColorType.NONE) &&
			(this.getUserData().equals(Gemboard.getCurrentSpecial()) || Gemboard.getCurrentSpecial() == ColorType.NONE)) {
			return true;
		}
		return false;
	}
	
	// determines if the gem is a special gem
	protected boolean isSpecial(Gem gem) {
		return (gem instanceof SpecialGem);
	}
	
	// used for drawing lines; the basic math
	private boolean isAdjacent(float x1, float x2, float y1, float y2){
		// distance formula
		float x = x1 - x2;
		float y = y1 - y2;
		float distance = (float) Math.sqrt(x*x + y*y);
    	
    	return distance < Gemboard.RADIUS * 1.5;		
	}
	
	// used for drawing lines, can be combined with the other isAdjacents
	private boolean isAdjacent(){
		return isAdjacent(start.x, end.x, start.y, end.y);
	}
	
	public void highlightGem() {
		this.setColor(Color.YELLOW);
		ResourcesManager.getInstance().gemSelectSound.play();
	}
	
	public void shadeGem() {
		this.setColor(new Color(0.5f,0.5f,0.5f));
	}
	
	public void revertColor() {
		this.setColor(Color.WHITE);
	}
	
	///////////////////////////////////////////
	// SETTERS AND GETTERS
	///////////////////////////////////////////
	
//	float x = col * RADIUS;
//	float y = STARTY + row * (RADIUS) + BUFFER;
	public int getRow(){
		return row;
	}
	
	public int getCol(){
		return col;
	}
}