package com.spectral.game.content.combat.method.impl.npcs;

import com.spectral.game.content.combat.CombatType;
import com.spectral.game.content.combat.WeaponInterfaces;
import com.spectral.game.content.combat.hit.PendingHit;
import com.spectral.game.content.combat.method.CombatMethod;
//import com.spectral.game.content.combat.method.impl.npcs.KBDCombatMethod.KBDAttackTypes;
import com.spectral.game.entity.impl.Character;
import com.spectral.game.entity.impl.player.Player;
import com.spectral.game.model.Animation;
import com.spectral.game.model.Flag;
import com.spectral.game.model.Graphic;
import com.spectral.game.model.GraphicHeight;
import com.spectral.game.model.Item;
import com.spectral.game.model.Projectile;
import com.spectral.game.model.equipment.BonusManager;
import com.spectral.util.Misc;

/**
 * 
 * @author Austin Foye
 *
 * https://www.rune-server.ee/members/44../
 *
 * Combat factory class for the King Black Dragons combat methods, IE: Poison, Fire, Freeze, and Melee.
 *
 */

public class KBDCombatMethod implements CombatMethod {
	
	/**
	 * Basically, for this we're just using multiple integers to decide the animation 
	 * and graphic for the KBD's melee and projectile for ranged hits.
	 */

	private static final Animation MELEE_ATTACK_ANIMATION = new Animation(123);
	
	
	

	private static enum KBDAttackTypes {
		DEFAULT(321),
		FIRE(22),
		FREEZE(22),
		POISON(22);
		
		KBDAttackTypes(final int projectile) {
			this.projectile = projectile;
			
		}
		
		private final int projectile;
	}
	
	private KBDAttackTypes currentAttack = KBDAttackTypes.DEFAULT;
	
	/**
	 * 
	 * Getting the seconds for the combo timer to switch combat methods randomly.
	 * Also setting the default method of attack melee. As per usual.
	 * 
	 */
	private CombatType combatType = CombatType.MELEE;
	
	
	public CombatType getCombatType() {
		return combatType;
	}
	
	public boolean canAttack(Character character, Character target) {
		return true;
	}
	
	//dunno why this throws an error tbh
	//public PendingHit[] getHits(Character character, Character target) {
		//return new PendingHit[]{new PendingHit(character, target, this, true, 2)};
	//}
	
	public void preQueueAdd(Character character, Character target) {
		new Projectile(character, target, currentAttack.projectile, 40, 70, 31, 43, 0).sendProjectile();
	}
	
	
	public int getAttackSpeed(Character character) {
		return character.getBaseAttackSpeed();
	}

	
	public int getAttackDistance(Character character) {
		return 4;
	}

	@Override
	public void startAnimation(Character character) {
		character.performAnimation(MELEE_ATTACK_ANIMATION);
	}

	@Override
	public void finished(Character character) {
		
		/**
		 * Handles special attacks
		 */
		currentAttack = KBDAttackTypes.DEFAULT;
	
		if(Misc.getRandom(80) <= 10) {
			currentAttack = KBDAttackTypes.FIRE;
		} else if(Misc.getRandom(90) <= 10) {
			currentAttack = KBDAttackTypes.FREEZE;
		} else if(Misc.getRandom(100) <= 10) {
			currentAttack = KBDAttackTypes.POISON;
		}
		
		/**
		 * Always switch to random combat type
		 */
		int randomAtackType = Misc.getRandom(CombatType.values().length);
		combatType = CombatType.values()[randomAtackType];
	}

	
	public void handleAfterHitEffects(PendingHit hit) {
		if(hit.getTarget() == null || !hit.getTarget().isPlayer()) {
			return;
		}
		
		final Player player = hit.getTarget().getAsPlayer();
		
		if(combatType == CombatType.MAGIC) {
			
		}
	}

	@Override
	public PendingHit[] getHits(Character character, Character target) {
		return new PendingHit[]{new PendingHit(character, target, this, true, 2)};
	}
	
	
}
