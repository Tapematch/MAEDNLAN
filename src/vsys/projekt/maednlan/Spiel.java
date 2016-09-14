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

	static boolean spielGestartet = false;
	static boolean spielBeendet = false;

	static ServerSocket listener;

	//Spieleinstieg
	public static void main(String[] args) {
		GUI.oeffneFenster(); //Spielfenster öffnen
		try {
			erzeugeSpieler(); //Spieleranzahl und Namen holen, Spiel vorbereiten
		} catch (IOException e) {
			System.out.println("Fehler beim Spielablauf! - " + e);
		}
	}

	private static void erzeugeSpieler() throws IOException {

		spielerName = GUI.holeSpielerName();
		server = GUI.server();
		if(server) {
			listener = new ServerSocket(8901);
			GUI.zeigeSpieler(spielerName);
			spieler.put(1, new Spieler(1, spielerName));

			new Thread(() -> {
				try {
					warteAufSpieler();
				} catch (IOException e) {
					GUI.executeAsync(() -> GUI.zeigeText(e.getMessage()));
				}
			}).start();

            GUI.okButton();
			spielGestartet = true;

			for (Spieler sp : spieler.values()){
				sp.erzeugeSpielfiguren();
			}

			Netzwerk.zeigeSpielfeld();
			spielAblauf(); //Ablauf des Spiels
		} else {
			GUI.zeigeText("Serveradresse eingeben...");
			verbindeZuServer();
		}
	}

	public static void verbindeZuServer(){
		String serverAdresse = GUI.holeServerAdresse();
		try {
			Socket clientSocket = new Socket(serverAdresse, 8901);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			out.println(spielerName);
			GUI.zeigeText("Warte auf Start...");
			spielernummer = Integer.parseInt(in.readLine());
		} catch (Exception ex){
			GUI.zeigeText("Verbindung zum Server konnte nicht hergestellt werden: "
					+ ex.getMessage()
					+ " | Bitte neue Serveradresse eingeben.");
			verbindeZuServer();
		}

		warteAufBefehle();
	}

	public static void warteAufSpieler() throws IOException {
		while (!spielGestartet && spieler.size() < 4){
			Socket clientSocket = listener.accept();

			if (!spielGestartet){
				spielerAnzahl++;
				clientIn.put(spielerAnzahl, new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
				clientOut.put(spielerAnzahl, new PrintWriter(clientSocket.getOutputStream(), true));
				String neuerSpielerName = clientIn.get(spielerAnzahl).readLine();
				GUI.executeAsync(() -> GUI.neuerSpieler(neuerSpielerName));
				clientOut.get(spielerAnzahl).println(spielerAnzahl);
				spieler.put(spielerAnzahl, new Spieler(spielerAnzahl, neuerSpielerName));
			}
		}
	}

	private static void spielAblauf(){
		int beendet = 0;
		while (beendet < spielerAnzahl) { //solange Spiel nicht beendet ist
			for (int spielernummer : spieler.keySet()) { //für jeden Spieler nacheinander durchgehen
				Spieler aktuellerSpieler = spieler.get(spielernummer);

				if (aktuellerSpieler.beendet == false) {

					int neuesFeld = 0;
					int augenzahl = 0;

					String endText = "Zug beendet! Bitte Bestätigen...";

					if (aktuellerSpieler.dreimalWuerfeln() == true) { //darf Spieler dreimal würfeln?
						Netzwerk.zeigeText(aktuellerSpieler.name
								+ " ist dran und darf dreimal würfeln! Würfelbecher anklicken zum würfeln...");
						if (Wuerfel.dreimalWuerfeln(spielernummer) == true) { //wurde nach 3 mal würfeln eine 6 gewürfelt?
							augenzahl = 6;
						} else {
							endText = "Keine 6 gewürfelt - Zug beenden! Bitte Bestätigen...";
						}
					} else {
						Netzwerk.zeigeText(aktuellerSpieler.name + " ist dran. Würfelbecher anklicken zum würfeln...");
						augenzahl = Wuerfel.einmalWuerfeln(spielernummer);
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
					Netzwerk.zeigeText(endText);
					Netzwerk.warteAufBeenden(spielernummer); //warte auf Bestätigung des Spielers
				}
			}
		}

		Netzwerk.zeigeText("-----------Spiel beendet!--------------");
		Netzwerk.ende(plaetze); //Zeige Plätze an
	}

	private static void warteAufBefehle(){
		while (!spielBeendet){
			try {
				String befehl = in.readLine();
				if (befehl.startsWith("ZEIGESPIELFELD")) {
					GUI.zeigeSpielfeld();
				} else if (befehl.startsWith("ZEIGETEXT")) {
					GUI.zeigeText(befehl.substring(10));
				} else if (befehl.startsWith("ERZEUGESPIELFIGUR")) {
					GUI.erzeugeSpielfigur(Integer.parseInt(befehl.substring(18, 19)), Integer.parseInt(befehl.substring(20)));
				} else if (befehl.startsWith("SETZESPIELFIGUR")) {
					GUI.setzeSpielfigur(Integer.parseInt(befehl.substring(16, 17)), Integer.parseInt(befehl.substring(18, 19)), Integer.parseInt(befehl.substring(20)));
				} else if (befehl.startsWith("WARTEAUFBEENDEN")) {
					GUI.warteAufBeenden();
					out.println();
				} else if (befehl.startsWith("WARTEAUFWUERFEL")) {
					GUI.warteAufWuerfel();
					out.println();
				} else if (befehl.startsWith("ZEIGEAUGENZAHL")) {
					GUI.zeigeAugenzahl(Integer.parseInt(befehl.substring(15)));
				}else if (befehl.startsWith("WARTEWUERFELKLICK")) {
					GUI.warteWuerfelKlick();
					out.println();
				} else if (befehl.startsWith("WELCHESPIELFIGUR")) {
					int figurnummer = GUI.welcheSpielfigur(spielernummer);
					out.println(figurnummer);
				} else if (befehl.startsWith("ENDE")) {
					HashMap<Integer, String> plaetze = new HashMap<>();
					String[] platz = befehl.split("|");
					for(int i = 1; i < platz.length; i++)
						plaetze.put(i, platz[i]);
					GUI.ende(plaetze);
				}
			} catch (IOException e) {
				System.out.println("Fehler beim Spielablauf! - " + e);
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
