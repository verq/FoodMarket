package utilities;

import jade.Boot;

import java.util.EnumMap;

import constants.Participants;

public class Main extends Boot {

	private static EnumMap<Participants, Integer> numberOfAgents = new EnumMap<Participants, Integer>(
			Participants.class);

	public Main() {
		super();
	}

	public static void main(String[] args) {
		int howManyAgents = 20;
		runAgent(howManyAgents);

		Boot.main(new String[] { "-gui", generateArgumentsAgentString() });

	}

	private static void runAgent(int howManyAgents) {
		for (Participants participant : Participants.values()) {
			numberOfAgents.put(participant, howManyAgents);
		}
	}

	private static String generateArgumentsAgentString() {
		StringBuilder sb = new StringBuilder();
		for (Participants participant : Participants.values()) {
			sb.append(generateArgumentSpecifiedAgentString(participant, numberOfAgents.get(participant)));
		}
		return sb.toString();
	}
	private static String generateArgumentSpecifiedAgentString(Participants participant, int number) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < number; i++) {
			String className = participant.getParticipant().toString()
					.replace("class ", "");
			sb.append(participant.name() + i + ":" + className + ";");
		}
		return sb.toString();
	}
}
