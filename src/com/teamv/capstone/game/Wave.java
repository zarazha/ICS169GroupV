package com.teamv.capstone.game;

import java.util.ArrayList;


public class Wave{

	/*
	 * an individual wave within a level
	 * 
	 */
	
	ArrayList<Enemy> enemies;
	
	public Wave(ArrayList<Enemy> enemies){
		//load level
		this.enemies = enemies;
	}
	
	public Wave(){
		enemies = new ArrayList<Enemy>();
	}
	
	public void parseLevel(){
		
	}
	
	public void initPlacement(){
		switch(enemies.size()){
		case 1:
			enemies.get(0).setPosition(600, 600);
			break;
		case 2:
			enemies.get(0).setPosition(600, 400);
			enemies.get(1).setPosition(600, 700);
			break;
		case 3:
			enemies.get(0).setPosition(600, 200);
			enemies.get(1).setPosition(600, 475);
			enemies.get(2).setPosition(600, 750);
			break;
		default:
				
		}
	}
	
	public boolean isFinished(){
		for(Enemy enemy : enemies){
			if(enemy.isDead){
				return false;
			}
		}
		return true;
	}
	
	public void add(Enemy enemy){
		enemies.add(enemy);
	}

	public ArrayList<Enemy> getEnemies() {
		return enemies;
	}
}