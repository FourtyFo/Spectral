package com.spectral.game.content.combat.method.impl.specials;

import com.spectral.game.content.combat.CombatSpecial;
import com.spectral.game.content.combat.CombatType;
import com.spectral.game.content.combat.hit.PendingHit;
import com.spectral.game.content.combat.method.CombatMethod;
import com.spectral.game.entity.impl.Character;
import com.spectral.game.model.Animation;
import com.spectral.game.model.Graphic;
import com.spectral.game.model.GraphicHeight;
import com.spectral.game.model.Priority;

public class AbyssalDaggerCombatMethod implements CombatMethod {

	private static final Animation ANIMATION = new Animation(3300, Priority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(1283, Priority.HIGH);

	@Override
	public CombatType getCombatType() {
		return CombatType.MELEE;
	}

	@Override
	public PendingHit[] getHits(Character character, Character target) {
		PendingHit hit1 = new PendingHit(character, target, this, true, 0);
		PendingHit hit2 = new PendingHit(character, target, this, true, target.isNpc() ? 1 : 0);
		
		if(!hit1.isAccurate() || hit1.getTotalDamage() <= 0) {
			hit2.getHits()[0].setDamage(0);
			hit2.updateTotalDamage();
		}
		
		return new PendingHit[]{hit1, hit2};
	}

	@Override
	public boolean canAttack(Character character, Character target) {
		return true;
	}

	@Override
	public void preQueueAdd(Character character, Character target) {
		CombatSpecial.drain(character, CombatSpecial.ABYSSAL_DAGGER.getDrainAmount());
	}

	@Override
	public int getAttackSpeed(Character character) {
		return character.getBaseAttackSpeed();
	}

	@Override
	public int getAttackDistance(Character character) {
		return 1;
	}

	@Override
	public void startAnimation(Character character) {
		character.performAnimation(ANIMATION);
		character.performGraphic(GRAPHIC);
	}

	@Override
	public void finished(Character character) {

	}

	@Override
	public void handleAfterHitEffects(PendingHit hit) {
	}
}