package vsys.projekt.maednlan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Spiel {

    static String spielerName = "Spieler";
	static int spielernummer = 1;
    static boolean server = false;

	static int spielerAnzahl = 1;
	static HashMap<Integer, Spieler> spieler = new HashMap<Integer, Spieler>();
	static HashMap<Integer, String> plaetze = new HashMap<Integer, String>();

    static BufferedReader in;
    static PrintWriter out;

	static HashMap<Integer, BufferedReader> clientIn = new HashMap<>();
	static HashMap<Integer, PrintWriter> clientOut = new HashMap<>();


	//Spieleinstieg
	public static void main(String[] args) {
		try {
			GUI.oeffneFenster(); //Spielfenster öffnen
			erzeugeSpieler(); //Spieleranzahl und Namen holen, Spiel vorbereiten
			GUI.zeigeSpielfeld(); //Spielfeld mit Figuren Anzeigen
			spielAblauf(); //Ablauf des Spiels
		} catch (IOException e) {
			System.out.println("Fehler beim Spielablauf! - " + e);
		}
	}

	private static void erzeugeSpieler() throws IOException {

		spielerName = GUI.holeSpielerName();
		server = GUI.server();
		if(server) {
			spieler.put(1, new Spieler(1, spielerName));
			ServerSocket listener = new ServerSocket(8901);
			GUI.zeigeSpieler(spielerName);


			//TODO Schleife -> beliebig viele Spieler
			Socket clientSocket = listener.accept();
			spielerAnzahl++;
			clientIn.put(spielerAnzahl, new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
			clientOut.put(spielerAnzahl, new PrintWriter(clientSocket.getOutputStream(), true));
			String neuerSpielerName = clientIn.get(spielerAnzahl).readLine();
            GUI.neuerSpieler(neuerSpielerName);
			spieler.put(spielerAnzahl, new Spieler(spielerAnzahl, neuerSpielerName));
			clientOut.get(spielerAnzahl).println(spielerAnzahl);


            GUI.okButton();

			clientOut.get(spielerAnzahl).println("START");
		} else {
			String serverAdresse = GUI.holeServerAdresse();
            GUI.zeigeText("Warte auf Start...");
			Socket clientSocket = new Socket(serverAdresse, 8901);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(spielerName);
            spielernummer = Integer.parseInt(in.readLine());

			in.readLine();
		}

	}

	private static void spielAblauf() throws IOException {
		if(server) {
			int beendet = 0;
			while (beendet < spielerAnzahl) { //solange Spiel nicht beendet ist
				for (int spielernummer : spieler.keySet()) { //für jeden Spieler nacheinander durchgehen
					Spieler aktuellerSpieler = spieler.get(spielernummer);

					if (aktuellerSpieler.beendet == false) {

						int neuesFeld = 0;
						int augenzahl = 0;

						String endText = "Zug beendet! Bitte Bestätigen...";

						if (aktuellerSpieler.dreimalWuerfeln() == true) { //darf Spieler dreimal würfeln?
							GUI.zeigeText(aktuellerSpieler.name
									+ " ist dran und darf dreimal würfeln! Würfelbecher anklicken zum würfeln...");
							if (Wuerfel.dreimalWuerfeln() == true) { //wurde nach 3 mal würfeln eine 6 gewürfelt?
								augenzahl = 6;
							} else {
								endText = "Keine 6 gewürfelt - Zug beenden! Bitte Bestätigen...";
							}
						} else {
							GUI.zeigeText(aktuellerSpieler.name + " ist dran. Würfelbecher anklicken zum würfeln...");
							augenzahl = Wuerfel.einmalWuerfeln();
						}

						if (augenzahl > 0) {
							neuesFeld = aktuellerSpieler.rutschen(augenzahl); //Felder rutschen

							if (neuesFeld != -99) { // War Zug möglich?
								if (neuesFeld == aktuellerSpieler.spielernummer * 10) { //wurde eine Figur ausgerückt?

									rauswerfen(neuesFeld, aktuellerSpieler.spielernummer); //werfe Figur raus
									neuesFeld = aktuellerSpieler.nachausruecken(); //nochmal würfeln
								}
								if (rauswerfen(neuesFeld, aktuellerSpieler.spielernummer)) {
									endText = "Spieler rausgeworfen! Zug beendet! Bitte Bestätigen...";
								}
							} else {
								endText = "Zug nicht möglich - beenden! Bitte Bestätigen...";
							}

						}

						if (aktuellerSpieler.pruefeBeendet() == true) { //prüfe, ob Spieler im Ziel ist
							beendet++;
							plaetze.put(beendet, aktuellerSpieler.name);
							endText = aktuellerSpieler.name + " ist im Ziel!  Bitte Bestätigen...";
						}
						GUI.zeigeText(endText);
						GUI.warteAufBeenden(); //warte auf Bestätigung des Spielers
					}
				}
			}

			GUI.zeigeText("-----------Spiel beendet!--------------");
			GUI.ende(plaetze); //Zeige Plätze an
		} else {
			while (true){
				String befehl = in.readLine();
				if (befehl.startsWith("ENDE")) {
					break;
				}
			}
		}
	}

	private static boolean rauswerfen(int neuesFeld, int spielerNummer) {
		for (int rspielerNummer : spieler.keySet()) { //gehe alle anderen Spielsteine durch; prüfe, ob jemand auf Feld stand
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
