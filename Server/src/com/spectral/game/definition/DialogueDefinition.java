package com.spectral.game.definition;

import com.spectral.game.model.dialogue.Dialogue;
import com.spectral.game.model.dialogue.DialogueExpression;
import com.spectral.game.model.dialogue.DialogueType;

/**
 * Represents a definition for a dialogue.
 * @author Professor Oak
 *
 */
public class DialogueDefinition {
	
	private int id;
	private int next = -1;
	private int npcId = -1;
	private DialogueType type;
	private DialogueExpression expression;
	private String[] lines;
	private String[] item;
	
	/**
	 * Creates an actual dialogue with the data
	 * of this definition.
	 * @return
	 */
	public Dialogue create() {
		return new Dialogue() {
			@Override
			public int id() {
				return id;
			}
			
			@Override
			public int nextDialogueId() {
				return next;
			}
			
			@Override
			public int npcId() {
				return npcId;
			}
			
			@Override
			public DialogueType type() {
				return type;
			}
			
			@Override
			public String[] dialogue() {
				return lines;
			}
			
			@Override
			public DialogueExpression animation() {
				return expression;
			}
			
			@Override
			public String[] item() {
				return item;
			}
		};
	}
}
