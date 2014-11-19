package com.teamv.capstone.gemboard.gems;

import org.andengine.entity.primitive.Line;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import com.teamv.capstone.BaseScene;
import com.teamv.capstone.ResourcesManager;
import com.teamv.capstone.SceneManager;
import com.teamv.capstone.gemboard.Gemboard;
import com.teamv.capstone.gemboard.Pointf;

public abstract class Gem{

	public Sprite gemSprite;
	
	protected final int RADIUS 	= Gemboard.RADIUS;
	protected int startingY 	= Gemboard.startingY;
	protected int buffer 		= Gemboard.buffer;
	
	protected int col, row;
	protected Pointf start, end;
	
	public Gem(int col, int row){
		
		this.col = col;
		this.row = row;
		
		start = Gemboard.getStartPoint();
		end = Gemboard.getEndPoint();
	}
	
	// update gem variables
	public void update(){
		gemSprite.setX(col * RADIUS);
		gemSprite.setY(startingY + row * (RADIUS + buffer) + buffer);
		
		// if odd, stagger location by half of radius
		if(col%2 != 0){
			gemSprite.setY(gemSprite.getY() + RADIUS/2);
		}
	}
	
	// update gem variables given col/row
	// can remove this if not used
	public void update(int col, int row){
		this.col = col;
		this.row = row;
		update();
	}
	
	// drop the gem, so row++
	public void drop(){
		row = row + 1;
		update();
	}
	
	// each gem should set their sprite in this method
	// this is important as setting sprite also includes setting their user interaction
	protected void setSprite(ITextureRegion sprite){
		gemSprite = new Sprite(col * RADIUS, startingY + row * (RADIUS + buffer) + buffer,
				sprite, ResourcesManager.getInstance().vbom){
			
			@Override
		    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float X, float Y) 
		    {
				// when finger touches gem
		        if (pSceneTouchEvent.isActionDown())
		        {
		        	start.set(gemSprite.getX() + RADIUS/2, gemSprite.getY() + RADIUS/2);
		        	
		        	int c = (int) (gemSprite.getX() / RADIUS);
		        	int r = (int) ((gemSprite.getY() - buffer - startingY) / (RADIUS + buffer));
		        	Gemboard.startList(c, r);
		        }
		        if (pSceneTouchEvent.isActionMove()){
		        	end.set(gemSprite.getX() + RADIUS/2, gemSprite.getY() + RADIUS/2);
		        	
		        	drawLine(this.getVertexBufferObjectManager());
		        }
		        // when finger releases gem
		        if (pSceneTouchEvent.isActionUp())
		        {
		        	drawLine(this.getVertexBufferObjectManager());
		        	Gemboard.executeGems();
		        }
		        return true;
		    };
		};
		
		// gem size, which is constant for now
		gemSprite.setWidth(RADIUS);
		gemSprite.setHeight(RADIUS);
				
		// if odd, stagger location by half of radius
		if(col%2 != 0){
			gemSprite.setY(gemSprite.getY() + RADIUS/2);
		}
	}
	
	// used to attach gem sprites to gameScene
	public void attachToScene(BaseScene gameScene){
		gameScene.registerTouchArea(gemSprite);
		gameScene.attachChild(gemSprite);
	}
	
	// what happens when gem dies, cleans up
	public void onDie(BaseScene gameScene){
		gameScene.unregisterTouchArea(gemSprite);
		gemSprite.detachSelf();
		gemSprite.dispose();
	}
	
	protected void drawLine(VertexBufferObjectManager vbom){
		
    	// location is adjacent to gem
		// gem list is not empty
		// gem is matching colors
		// gem is not already in the chain
		if( isAdjacent() &&
			!Gemboard.connectedGems.isEmpty() &&
			this.sameColor(Gemboard.connectedGems.get(Gemboard.connectedGems.size() - 1)) &&
			!Gemboard.connectedGems.contains(this)){
			
        	Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY(), vbom);

        	line.setLineWidth(5);
        	line.setColor(Color.BLACK);
            SceneManager.getInstance().getCurrentScene().attachChild(line);
            Gemboard.lines.add(line);
            
            // set point to middle of gem
            start.setX(gemSprite.getX() + RADIUS/2);
        	start.setY(gemSprite.getY() + RADIUS/2);
        	
        	Gemboard.connectedGems.add(this);
    	}
	}
	
	// determines if the gems are the same
	protected boolean sameColor(Gem gem) {
		if(this.toString().equals(gem.toString())){
			return true;
		}
		return false;
	}
	
	// used for drawing lines; the basic math
	private boolean isAdjacent(float x1, float x2, float y1, float y2){
		// distance formula
		float x = x1 - x2;
		float y = y1 - y2;
		float distance = (float) Math.sqrt(x*x + y*y);
    	
    	return distance < RADIUS * 1.5;		
	}
	
	// used for drawing lines, can be combined with the other isAdjacents
	private boolean isAdjacent(){
		return isAdjacent(start.getX(), end.getX(), start.getY(), end.getY());
	}
	
	// used publicly for gem adjacency, remove if not needed
	public boolean isAdjacent(Gem gem){
    	return isAdjacent(	gemSprite.getX(), gem.gemSprite.getX(), 
    						gemSprite.getY(), gem.gemSprite.getY());
	}
	
	///////////////////////////////////////////
	// SETTERS AND GETTERS
	///////////////////////////////////////////
	
	public int getRow(){
		return row;
	}
	
	public int getCol(){
		return col;
	}
}
