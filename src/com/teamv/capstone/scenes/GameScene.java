package com.teamv.capstone.scenes;

import org.andengine.entity.scene.background.Background;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.util.color.Color;
import com.badlogic.gdx.math.Vector2;
import com.teamv.capstone.BaseScene;
import com.teamv.capstone.SceneManager.SceneType;
import com.teamv.capstone.gemboard.Gemboard;

public class GameScene extends BaseScene
{
	///VARIABLES
	//private HUD gameHUD;
	//private Text scoreText;
	private PhysicsWorld physicsWorld;
	
	private Gemboard gemboard;
	
    @Override
    public void createScene()
    {
    	createBackground();
        createHUD();
        createPhysics();
        
        gemboard = new Gemboard(this, physicsWorld);
        this.setOnSceneTouchListener(gemboard);
    }

    @Override
    public void onBackKeyPressed()
    {
        
    }

    @Override
    public SceneType getSceneType()
    {
        return SceneType.SCENE_GAME;
    }

    @Override
    public void disposeScene()
    {
    	
    }
    
    ///CREATE STUFF
    
    private void createBackground()
    {
//    	float r = 99.0f	/255;
//    	float g = 33.0f	/255;
//    	float b = 10.0f	/255;
//      setBackground(new Background(r, g, b));
        setBackground(new Background(Color.BLACK));
    }

    
    private void createHUD()
    {
//        gameHUD = new HUD();
//        
//        // CREATE SCORE TEXT
//        scoreText = new Text(20, 420, resourcesManager.font, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
//        scoreText.setSkewCenter(0, 0);    
//        scoreText.setText("Score: 0");
//        gameHUD.attachChild(scoreText);
//        
//        camera.setHUD(gameHUD);
    }
    
    private void createPhysics()
    {
        physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false);
        registerUpdateHandler(physicsWorld);
    }
}