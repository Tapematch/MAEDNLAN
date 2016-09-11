package vsys.projekt.maednlan;

public class Wuerfel {

	public static int einmalWuerfeln(int spielernummer) { //gib Zufalsszahl zw. 1 und 6 zurück
		Netzwerk.warteAufWuerfel(spielernummer);
		int zufall = (int) (6 * Math.random()) + 1;
		Netzwerk.zeigeAugenzahl(zufall);
		Netzwerk.warteWuerfelKlick(spielernummer);
		return zufall;
	}

	public static boolean dreimalWuerfeln(int spielernummer) { //gib boolschen Wert zurück, ob nach 3 mal Würfeln eine 6 gewürfelt wurde
		boolean sechsGewuerfelt = false;
		for (int i = 3; i > 0; i--) {
			int zufall = einmalWuerfeln(spielernummer);
			if (zufall == 6) {
				sechsGewuerfelt = true;
				break;
			}
		}
		return sechsGewuerfelt;
	}
}
