/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.github.dachhack.sprout.items;

import java.util.ArrayList;
import java.util.Calendar;

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Statistics;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Invisibility;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.mobs.Mob;
import com.github.dachhack.sprout.items.artifacts.DriedRose;
import com.github.dachhack.sprout.items.artifacts.TimekeepersHourglass;
import com.github.dachhack.sprout.items.food.Blackberry;
import com.github.dachhack.sprout.items.food.Blueberry;
import com.github.dachhack.sprout.items.food.Cloudberry;
import com.github.dachhack.sprout.items.food.FullMoonberry;
import com.github.dachhack.sprout.items.food.Moonberry;
import com.github.dachhack.sprout.scenes.InterlevelScene;
import com.github.dachhack.sprout.sprites.ItemSprite.Glowing;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;

public class CavesKey extends Item {
	
	private static final String TXT_PREVENTING = "Strong magic aura of this place prevents you from using the ancient key!";
		
	
	public static final float TIME_TO_USE = 1;

	public static final String AC_PORT = "OPEN PORTAL";

	private int specialLevel = 29;
	private int returnDepth = -1;
	private int returnPos;

	{
		name = "ancient caves key";
		image = ItemSpriteSheet.ANCIENTKEY;

		stackable = false;
	}
	
	private static final String DEPTH = "depth";
	private static final String POS = "pos";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DEPTH, returnDepth);
		if (returnDepth != -1) {
			bundle.put(POS, returnPos);
		}
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		returnDepth = bundle.getInt(DEPTH);
		returnPos = bundle.getInt(POS);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_PORT);
		
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		if (action == AC_PORT) {

			if (Dungeon.bossLevel()) {
				hero.spend(TIME_TO_USE);
				GLog.w(TXT_PREVENTING);
				return;
			}
			
			if (Dungeon.depth>25 && Dungeon.depth!=specialLevel) {
				hero.spend(TIME_TO_USE);
				GLog.w(TXT_PREVENTING);
				return;
			}
			if (Dungeon.depth==1) {
				hero.spend(TIME_TO_USE);
				GLog.w(TXT_PREVENTING);
				return;
			}


		}

		if (action == AC_PORT) {

				Buff buff = Dungeon.hero
						.buff(TimekeepersHourglass.timeFreeze.class);
				if (buff != null)
					buff.detach();

				for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
					if (mob instanceof DriedRose.GhostHero)
						mob.destroy();
              if (Dungeon.depth<25 && !Dungeon.bossLevel()){
            	
            	returnDepth = Dungeon.depth;
       			returnPos = hero.pos;
				InterlevelScene.mode = InterlevelScene.Mode.PORTCAVES;
			} else {
				FishingBomb bomb = Dungeon.hero.belongings.getItem(FishingBomb.class);
				if (bomb!=null){bomb.detachAll(Dungeon.hero.belongings.backpack);}
				updateQuickslot();
				 
				hero.invisible=0;
								
				this.doDrop(hero);
				
				if (Statistics.albinoPiranhasKilled>99){
					Moonberry berry1 = new Moonberry(10);
					berry1.doPickUp(Dungeon.hero);	
					Cloudberry berry2 = new Cloudberry(10);
					berry2.doPickUp(Dungeon.hero);	
					Blueberry berry3 = new Blueberry(10);
					berry3.doPickUp(Dungeon.hero);	
					Blackberry berry4 = new Blackberry(10);
					berry4.doPickUp(Dungeon.hero);
					
					if (Dungeon.checkNight()){
						FullMoonberry berry = new FullMoonberry();
						berry.doPickUp(Dungeon.hero);
						}
				}
				
								
				InterlevelScene.mode = InterlevelScene.Mode.RETURN;	
			}
                hero.invisible=0;
				InterlevelScene.returnDepth = returnDepth;
				InterlevelScene.returnPos = returnPos;
				Game.switchScene(InterlevelScene.class);
					
		} else {

			super.execute(hero, action);

		}
	}
	
	public void reset() {
		returnDepth = -1;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}


	private static final Glowing BLACK = new Glowing(0x00000);

	@Override
	public Glowing glowing() {
		return BLACK;
	}

	@Override
	public String info() {
		return "This ancient key is rusty and fragile, but you still feel some power left. "
				+"Looks like it might transport you to a new level. ";
	}
}
