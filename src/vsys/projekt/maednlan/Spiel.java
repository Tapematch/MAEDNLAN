package vsys.projekt.maednlan;

import java.util.HashMap;

public class Spiel {

	static int spielerAnzahl = 0;
	static HashMap<Integer, Spieler> spieler = new HashMap<Integer, Spieler>();
	static HashMap<Integer, String> plaetze = new HashMap<Integer, String>();

	//Spieleinstieg
	public static void main(String[] args) {

		GUI.oeffneFenster(); //Spielfenster �ffnen
		erzeugeSpieler(); //Spieleranzahl und Namen holen, Spiel vorbereiten
		GUI.zeigeSpielfeld(); //Spielfeld mit Figuren Anzeigen
		spielAblauf(); //Ablauf des Spiels
	}

	private static void erzeugeSpieler() {

		spielerAnzahl = GUI.holeSpielerAnzahl();

		for (int i = 1; i <= spielerAnzahl; i++) {
			spieler.put(i, new Spieler(i));
		}

	}

	private static void spielAblauf() {
		int beendet = 0;
		while (beendet < spielerAnzahl) { //solange Spiel nicht beendet ist
			for (int spielernummer : spieler.keySet()) { //f�r jeden Spieler nacheinander durchgehen
				Spieler aktuellerSpieler = spieler.get(spielernummer);

				if (aktuellerSpieler.beendet == false) {

					int neuesFeld = 0;
					int augenzahl = 0;
					
					String endText = "Zug beendet! Bitte Best�tigen...";

					if (aktuellerSpieler.dreimalWuerfeln() == true) { //darf Spieler dreimal w�rfeln?
						GUI.zeigeText(aktuellerSpieler.name
								+ " ist dran und darf dreimal w�rfeln! W�rfelbecher anklicken zum w�rfeln...");
						if (Wuerfel.dreimalWuerfeln() == true) { //wurde nach 3 mal w�rfeln eine 6 gew�rfelt?
							augenzahl = 6;
						} else {
							endText= "Keine 6 gew�rfelt - Zug beenden! Bitte Best�tigen...";
						}
					} else {
						GUI.zeigeText(aktuellerSpieler.name + " ist dran. W�rfelbecher anklicken zum w�rfeln...");
						augenzahl = Wuerfel.einmalWuerfeln();
					}

					if (augenzahl > 0) {
						neuesFeld = aktuellerSpieler.rutschen(augenzahl); //Felder rutschen

						if (neuesFeld != -99) { // War Zug m�glich?
							if (neuesFeld == aktuellerSpieler.spielernummer * 10) { //wurde eine Figur ausger�ckt?

								rauswerfen(neuesFeld, aktuellerSpieler.spielernummer); //werfe Figur raus
								neuesFeld = aktuellerSpieler.nachausruecken(); //nochmal w�rfeln
							}
							if (rauswerfen(neuesFeld, aktuellerSpieler.spielernummer)) {
								endText = "Spieler rausgeworfen! Zug beendet! Bitte Best�tigen...";
							}
						} else {
							endText = "Zug nicht m�glich - beenden! Bitte Best�tigen...";
						}

					}

					if (aktuellerSpieler.pruefeBeendet() == true) { //pr�fe, ob Spieler im Ziel ist
						beendet++;
						plaetze.put(beendet, aktuellerSpieler.name);
						endText = aktuellerSpieler.name + " ist im Ziel!  Bitte Best�tigen...";
					}
					GUI.zeigeText(endText);
					GUI.warteAufBeenden(); //warte auf Best�tigung des Spielers
				}
			}
		}

		GUI.zeigeText("-----------Spiel beendet!--------------");
		GUI.ende(plaetze); //Zeige Pl�tze an
	}

	private static boolean rauswerfen(int neuesFeld, int spielerNummer) {
		for (int rspielerNummer : spieler.keySet()) { //gehe alle anderen Spielsteine durch; pr�fe, ob jemand auf Feld stand
			if (rspielerNummer != spielerNummer) {
				Spieler rspieler = spieler.get(rspielerNummer);
				for (int spielFigurNummer : rspieler.holeSpielfiguren().keySet()) {
					Spielfigur spielFigur = rspieler.holeSpielfiguren().get(spielFigurNummer);
					if (spielFigur.feldnummer == neuesFeld) {
						spielFigur.rauswerfen();
						return true;
					}
				}
			}

		}
		return false;
	}
}
