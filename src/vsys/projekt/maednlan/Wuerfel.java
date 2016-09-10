package vsys.projekt.maednlan;

public class Wuerfel {

	public static int einmalWuerfeln() { //gib Zufalsszahl zw. 1 und 6 zur�ck
		GUI.warteAufWuerfel();
		int zufall = (int) (6 * Math.random()) + 1;
		GUI.zeigeAugenzahl(zufall);
		return zufall;
	}

	public static boolean dreimalWuerfeln() { //gib boolschen Wert zur�ck, ob nach 3 mal W�rfeln eine 6 gew�rfelt wurde
		boolean sechsGewuerfelt = false;
		for (int i = 3; i > 0; i--) {
			int zufall = einmalWuerfeln();
			if (zufall == 6) {
				sechsGewuerfelt = true;
				break;
			}
		}
		return sechsGewuerfelt;
	}
}
