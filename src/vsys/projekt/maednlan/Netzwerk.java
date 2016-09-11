package vsys.projekt.maednlan;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created by philt on 11.09.2016.
 */
public class Netzwerk {
    public static void zeigeSpielfeld() {
        GUI.zeigeSpielfeld();
        for(PrintWriter out : Spiel.clientOut.values()){
            out.println("ZEIGESPIELFELD");
        }
    }

    public static void zeigeText(String text) { //Zeige vorgegebenen Text im Statustextfeld an
        GUI.zeigeText(text);
          for(PrintWriter out : Spiel.clientOut.values()){
              out.println("ZEIGETEXT " + text);
          }
    }

    public static void erzeugeSpielfigur(int spielernummer, int figurnummer) {
        GUI.erzeugeSpielfigur(spielernummer, figurnummer);
        for(PrintWriter out : Spiel.clientOut.values()){
            out.println("ERZEUGESPIELFIGUR " + spielernummer + " " + figurnummer);
        }
    }

    public static void setzeSpielfigur(int spielernummer, int figurnummer, int feldnummer) {
        GUI.setzeSpielfigur(spielernummer, figurnummer, feldnummer);
        for(PrintWriter out : Spiel.clientOut.values()){
            out.println("SETZESPIELFIGUR " + spielernummer + " " + figurnummer + " " + feldnummer);
        }
    }

    public static void warteAufBeenden(int spielernummer) {
        if(spielernummer == 1) {
            GUI.warteAufBeenden();
        } else {
            Spiel.clientOut.get(spielernummer).println("WARTEAUFBEENDEN");
            try {
                Spiel.clientIn.get(spielernummer).readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void warteAufWuerfel(int spielernummer) {
        if(spielernummer == 1) {
            GUI.warteAufWuerfel();
        } else {
            Spiel.clientOut.get(spielernummer).println("WARTEAUFWUERFEL");
            try {
                Spiel.clientIn.get(spielernummer).readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void zeigeAugenzahl(int augenzahl) {
        GUI.zeigeAugenzahl(augenzahl);
        for(PrintWriter out : Spiel.clientOut.values()){
            out.println("ZEIGEAUGENZAHL " + augenzahl);
        }
    }

    public static void warteWuerfelKlick(int spielernummer) {
        if(spielernummer == 1) {
            GUI.warteWuerfelKlick();
        } else {
            Spiel.clientOut.get(spielernummer).println("WARTEWUERFELKLICK");
            try {
                Spiel.clientIn.get(spielernummer).readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int welcheSpielfigur(int spielernummer){
        int figurnummer = 0;
        if(spielernummer == 1) {
            figurnummer = GUI.welcheSpielfigur(spielernummer);
        } else {
            Spiel.clientOut.get(spielernummer).println("WELCHESPIELFIGUR");
            try {
                figurnummer = Integer.parseInt(Spiel.clientIn.get(spielernummer).readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return figurnummer;
    }

    public static void ende(HashMap<Integer, String> plaetze) {
        GUI.ende(plaetze);
        String befehl = "ENDE";
        for (int i = 1; i <= plaetze.size(); i++)
            befehl = befehl + "|" + plaetze.get(i);
        for(PrintWriter out : Spiel.clientOut.values()){
            out.println(befehl);
        }
    }
}
