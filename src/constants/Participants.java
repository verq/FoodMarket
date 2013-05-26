package constants;

import agents.Baker;
import agents.Client;
import agents.Farmer;
import agents.Grower;
import agents.Keeper;
import agents.Milkman;

public enum Participants {
	CLIENT(Client.class), BAKER(Baker.class), MILKMAN(Milkman.class), GROWER(Grower.class), KEEPER(Keeper.class), FARMER(
			Farmer.class);

	private final Class participantClass;

	private Participants(Class participant) {
		this.participantClass = participant;
	}

	private Class getParticipant() {
		return participantClass;
	}
}
