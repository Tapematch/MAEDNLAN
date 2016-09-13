package vsys.projekt.maednlan;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;

public class GUI {

	static Shell shlMaedn;
	static Display display;
	static int letzteAusgewaehlteFigur;
	static Label lblWelcome;
	static Label lblMaedn;
	static HashMap<Integer, Canvas> spielfiguren = new HashMap<Integer, Canvas>();
	static Text textfeld;
    static Table playerTable;
	static Canvas wuerfel;

	public static void executeAsync(Runnable function){
		display.asyncExec(function);
	}

	public static int holeSpielerAnzahl() { //Abfrage Spieleranzahl

		GUI.zeigeText("Wie viele Spieler?");

		Label label = new Label(shlMaedn, SWT.NONE);
		label.setBounds(293, 467, 18, 15);
		label.setText("1");

		Label label_1 = new Label(shlMaedn, SWT.NONE);
		label_1.setText("2");
		label_1.setBounds(342, 467, 18, 15);

		Label label_2 = new Label(shlMaedn, SWT.NONE);
		label_2.setText("3");
		label_2.setBounds(389, 467, 18, 15);

		Label label_3 = new Label(shlMaedn, SWT.NONE);
		label_3.setText("4");
		label_3.setBounds(437, 467, 18, 15);

		Scale spielerAnzahlScale = new Scale(shlMaedn, SWT.NONE);
		spielerAnzahlScale.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		spielerAnzahlScale.setPageIncrement(1);
		spielerAnzahlScale.setMaximum(4);
		spielerAnzahlScale.setMinimum(1);
		spielerAnzahlScale.setSelection(1);
		spielerAnzahlScale.setBounds(285, 488, 170, 42);

		Button btnOk = new Button(shlMaedn, SWT.NONE);
		btnOk.setBounds(478, 488, 28, 25);
		btnOk.setText("OK");

		btnOk.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
					case SWT.Selection:
						btnOk.dispose();
						break;
				}
			}
		});

		while (!btnOk.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		int spielerAnzahl = spielerAnzahlScale.getSelection();
		spielerAnzahlScale.dispose();
		label.dispose();
		label_1.dispose();
		label_2.dispose();
		label_3.dispose();

		return spielerAnzahl;
	}

	public static void oeffneFenster() { //zeige Willkommensbildschirm

		display = Display.getDefault();
		shlMaedn = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);
		shlMaedn.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		shlMaedn.setImage(SWTResourceManager.getImage("img\\FigurRot.png"));
		shlMaedn.setSize(808, 851);
		shlMaedn.setText("MAEDN");

		textfeld = new Text(shlMaedn, SWT.BORDER); //Statustextfeld
		textfeld.setBounds(0, 801, 802, 21);

		lblWelcome = new Label(shlMaedn, SWT.NONE);
		lblWelcome.setImage(SWTResourceManager.getImage("img\\Maedn.png"));
		lblWelcome.setBounds(196, 72, 400, 284);

		lblMaedn = new Label(shlMaedn, SWT.NONE);
		lblMaedn.setAlignment(SWT.CENTER);
		lblMaedn.setFont(SWTResourceManager.getFont("Gabriola", 33, SWT.NORMAL));
		lblMaedn.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMaedn.setBounds(221, 0, 347, 81);
		lblMaedn.setText("Herzlich Willkommen bei");

		shlMaedn.open();
		shlMaedn.layout();
	}

	public static void zeigeText(String text) { //Zeige vorgegebenen Text im Statustextfeld an
		textfeld.setText(text);
		if (!display.readAndDispatch()) {
			display.sleep();
		}
	}

	public static void zeigeSpielfeld() { //zeige vorbereitetes Spielfeld an
		lblWelcome.dispose();
		lblMaedn.dispose();

		for (int spielfigurnummer : spielfiguren.keySet()) {
			Canvas spielfigur = spielfiguren.get(spielfigurnummer);
			spielfigur.setSize(33, 60);
		}

		shlMaedn.setBackgroundImage(
				SWTResourceManager.getImage("img\\Spielfeld.png"));

		if (!display.readAndDispatch()) {
			display.sleep();
		}
	}

	public static boolean server(){

        zeigeText("Als Server oder Client spielen?");

        Button radio1 = new Button(shlMaedn, SWT.RADIO);
        radio1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        radio1.setText("Spiel hosten");
        radio1.setBounds(285, 488, 170, 42);

        Button radio2 = new Button(shlMaedn, SWT.RADIO);
        radio2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        radio2.setText("Zu Spiel verbinden");
        radio2.setBounds(285, 520, 170, 42);

        Button btnOk = new Button(shlMaedn, SWT.NONE);
        btnOk.setBounds(478, 510, 28, 25);
        btnOk.setText("OK");

        btnOk.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                switch (e.type) {
                    case SWT.Selection:
                        if(!radio1.getSelection() && !radio2.getSelection()){
                            zeigeText("Bitte eine Auswahl treffen!");
                        } else {
                            btnOk.dispose();
                        }
                        break;
                }
            }
        });

        while (!btnOk.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        boolean server = radio1.getSelection();
        radio1.dispose();
        radio2.dispose();

        return server;
    }

    public static void zeigeSpieler(String eigenerName) {
        String ip = "127.0.0.1";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            zeigeText("Warte auf weitere Spieler... | Servername: " + InetAddress.getLocalHost().getHostName() + " |  IP: " + ip);
        } catch (UnknownHostException e) {
            zeigeText("Warte auf weitere Spieler... | Fehler beim Anzeigen der Servadresse!");
        }

        playerTable = new Table(shlMaedn, SWT.BORDER | SWT.FULL_SELECTION);
        playerTable.setBounds(269, 422, 264, 161);
        playerTable.setHeaderVisible(true);
        playerTable.setLinesVisible(true);


        TableColumn column = new TableColumn(playerTable, SWT.NONE);
        column.setText("Spielername");

        TableItem item = new TableItem(playerTable, SWT.NULL);
        item.setText(0, eigenerName);
        playerTable.getColumn(0).pack();

        if (!display.readAndDispatch()) {
            display.sleep();
        }
    }

    public static void neuerSpieler(String spielerName) {
        TableItem item = new TableItem(playerTable, SWT.NULL);
        item.setText(0, spielerName);
        playerTable.getColumn(0).pack();

        if (!display.readAndDispatch()) {
            display.sleep();
        }
    }

    public static void okButton() {
        Button btnOk = new Button(shlMaedn, SWT.NONE);
        btnOk.setBounds(269, 600, 28, 25);
        btnOk.setText("OK");

        btnOk.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                switch (e.type) {
                    case SWT.Selection:
                        btnOk.dispose();
                        break;
                }
            }
        });

        while (!btnOk.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        playerTable.dispose();
    }

    public static String holeServerAdresse() {
        zeigeText("Serveradresse eingeben...");

        Button button = new Button(shlMaedn, SWT.NONE);
        button.setText("OK");
        button.setBounds(475, 434, 28, 25);

        Text serverAdresseText;
        serverAdresseText = new Text(shlMaedn, SWT.BORDER);
        serverAdresseText.setBounds(272, 436, 183, 21);

        button.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                switch (e.type) {
                    case SWT.Selection:
                        button.dispose();
                        break;
                }
            }
        });

        while (!button.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        String serverAdresse = serverAdresseText.getText();
        serverAdresseText.dispose();

        return serverAdresse;
    }

	public static String holeSpielerName() { //Abfrage Spielername
		GUI.zeigeText("Trage deinen Namen ein!");

		Button button = new Button(shlMaedn, SWT.NONE);
		button.setText("OK");
		button.setBounds(475, 434, 28, 25);

		Text nameSpielerText;
		nameSpielerText = new Text(shlMaedn, SWT.BORDER);
		nameSpielerText.setBounds(272, 436, 183, 21);

		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					button.dispose();
					break;
				}
			}
		});

		while (!button.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		String spielerName = nameSpielerText.getText();
		nameSpielerText.dispose();

		return spielerName;

	}

	public static void erzeugeSpielfigur(int spielernummer, int figurnummer) { //erzeuge Spielfigur
		Canvas spielfigur = new Canvas(shlMaedn, SWT.TRANSPARENT);
		spielfigur.setSize(0, 0);
		spielfigur.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				switch (spielernummer) {
				case 1:
					e.gc.drawImage(
							SWTResourceManager.getImage("img\\FigurSchwarz.png"),
							0, 0);
					break;
				case 2:
					e.gc.drawImage(
							SWTResourceManager.getImage("img\\FigurGelb.png"), 0,
							0);
					break;
				case 3:
					e.gc.drawImage(
							SWTResourceManager.getImage("img\\FigurGruen.png"), 0,
							0);
					break;
				case 4:
					e.gc.drawImage(
							SWTResourceManager.getImage("img\\FigurRot.png"), 0,
							0);
					break;
				}
			}
		});

		spielfiguren.put(spielernummer * 10 + figurnummer, spielfigur);

	}
	
	
	/*
	 * Felder 100+	--> Zielfelder
	 * Felder <0 	--> Startbereich
	 * 10 			--> Spielernummer
	 * 1 			--> Feldnummer
	 */
	public static void setzeSpielfigur(int spielernummer, int figurnummer, int feldnummer) {
		if(wuerfel != null && !wuerfel.isDisposed())
			wuerfel.dispose();
		Canvas spielfigur = spielfiguren.get(spielernummer * 10 + figurnummer);
		switch (feldnummer) {
		case -11:
			spielfigur.setLocation(50, 610);
			break;
		case -12:
			spielfigur.setLocation(118, 610);
			break;
		case -13:
			spielfigur.setLocation(50, 680);
			break;
		case -14:
			spielfigur.setLocation(118, 680);
			break;

		case -21:
			spielfigur.setLocation(50, 10);
			break;
		case -22:
			spielfigur.setLocation(118, 10);
			break;
		case -23:
			spielfigur.setLocation(50, 80);
			break;
		case -24:
			spielfigur.setLocation(118, 80);
			break;

		case -31:
			spielfigur.setLocation(650, 10);
			break;
		case -32:
			spielfigur.setLocation(718, 10);
			break;
		case -33:
			spielfigur.setLocation(650, 80);
			break;
		case -34:
			spielfigur.setLocation(718, 80);
			break;

		case -41:
			spielfigur.setLocation(650, 610);
			break;
		case -42:
			spielfigur.setLocation(718, 610);
			break;
		case -43:
			spielfigur.setLocation(650, 680);
			break;
		case -44:
			spielfigur.setLocation(718, 680);
			break;

		case 10:
			spielfigur.setLocation(318, 680);
			break;
		case 11:
			spielfigur.setLocation(318, 610);
			break;
		case 12:
			spielfigur.setLocation(318, 547);
			break;
		case 13:
			spielfigur.setLocation(318, 479);
			break;
		case 14:
			spielfigur.setLocation(318, 420);
			break;
		case 15:
			spielfigur.setLocation(252, 420);
			break;
		case 16:
			spielfigur.setLocation(184, 420);
			break;
		case 17:
			spielfigur.setLocation(120, 420);
			break;
		case 18:
			spielfigur.setLocation(52, 420);
			break;
		case 19:
			spielfigur.setLocation(52, 350);
			break;

		case 20:
			spielfigur.setLocation(52, 284);
			break;
		case 21:
			spielfigur.setLocation(120, 284);
			break;
		case 22:
			spielfigur.setLocation(184, 284);
			break;
		case 23:
			spielfigur.setLocation(252, 284);
			break;
		case 24:
			spielfigur.setLocation(318, 284);
			break;
		case 25:
			spielfigur.setLocation(318, 219);
			break;
		case 26:
			spielfigur.setLocation(318, 149);
			break;
		case 27:
			spielfigur.setLocation(318, 83);
			break;
		case 28:
			spielfigur.setLocation(318, 18);
			break;
		case 29:
			spielfigur.setLocation(385, 18);
			break;

		case 30:
			spielfigur.setLocation(452, 18);
			break;
		case 31:
			spielfigur.setLocation(452, 83);
			break;
		case 32:
			spielfigur.setLocation(452, 149);
			break;
		case 33:
			spielfigur.setLocation(452, 219);
			break;
		case 34:
			spielfigur.setLocation(452, 284);
			break;
		case 35:
			spielfigur.setLocation(518, 284);
			break;
		case 36:
			spielfigur.setLocation(585, 284);
			break;
		case 37:
			spielfigur.setLocation(653, 284);
			break;
		case 38:
			spielfigur.setLocation(718, 284);
			break;
		case 39:
			spielfigur.setLocation(718, 350);
			break;

		case 40:
			spielfigur.setLocation(718, 420);
			break;
		case 41:
			spielfigur.setLocation(653, 420);
			break;
		case 42:
			spielfigur.setLocation(585, 420);
			break;
		case 43:
			spielfigur.setLocation(518, 420);
			break;
		case 44:
			spielfigur.setLocation(452, 420);
			break;
		case 45:
			spielfigur.setLocation(452, 479);
			break;
		case 46:
			spielfigur.setLocation(452, 547);
			break;
		case 47:
			spielfigur.setLocation(452, 610);
			break;
		case 48:
			spielfigur.setLocation(452, 680);
			break;
		case 49:
			spielfigur.setLocation(385, 680);
			break;

		case 110:
			spielfigur.setLocation(384, 618);
			break;
		case 111:
			spielfigur.setLocation(384, 550);
			break;
		case 112:
			spielfigur.setLocation(384, 486);
			break;
		case 113:
			spielfigur.setLocation(384, 418);
			break;

		case 120:
			spielfigur.setLocation(118, 350);
			break;
		case 121:
			spielfigur.setLocation(185, 350);
			break;
		case 122:
			spielfigur.setLocation(251, 350);
			break;
		case 123:
			spielfigur.setLocation(318, 350);
			break;

		case 130:
			spielfigur.setLocation(384, 82);
			break;
		case 131:
			spielfigur.setLocation(384, 150);
			break;
		case 132:
			spielfigur.setLocation(384, 218);
			break;
		case 133:
			spielfigur.setLocation(384, 282);
			break;

		case 140:
			spielfigur.setLocation(651, 350);
			break;
		case 141:
			spielfigur.setLocation(585, 350);
			break;
		case 142:
			spielfigur.setLocation(518, 350);
			break;
		case 143:
			spielfigur.setLocation(450, 350);
			break;
		}

	}
	
	public static void warteAufBeenden() { //Bestätigung des Spielers
		if(wuerfel != null && !wuerfel.isDisposed())
			wuerfel.dispose();
		Canvas beenden = new Canvas(shlMaedn, SWT.TRANSPARENT);
		beenden.setBounds(350, 350, 100, 100);
		beenden.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(SWTResourceManager.getImage("img\\Haken.png"), 0,
						0);
			}
		});

		beenden.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				beenden.dispose();
			}

			@Override
			public void mouseUp(MouseEvent arg0) {
			}

		});

		while (!beenden.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public static void warteAufWuerfel() { //Aktion des Spielers vor würfeln
		if(wuerfel != null && !wuerfel.isDisposed())
			wuerfel.dispose();
		wuerfel = new Canvas(shlMaedn, SWT.TRANSPARENT);
		wuerfel.setBounds(350, 350, 100, 100);
		wuerfel.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(
						SWTResourceManager.getImage("img\\Wuerfelbecher.png"), 0,
						0);
			}
		});

		wuerfel.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				wuerfel.dispose();
			}

			@Override
			public void mouseUp(MouseEvent arg0) {
			}

		});

		while (!wuerfel.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public static void zeigeAugenzahl(int augenzahl) { //zeige gewürfelte Augenzahl an
		if(wuerfel != null && !wuerfel.isDisposed())
			wuerfel.dispose();
		wuerfel = new Canvas(shlMaedn, SWT.TRANSPARENT);
		wuerfel.setBounds(375, 375, 50, 50);
		wuerfel.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				switch (augenzahl) {
				case 1:
					e.gc.drawImage(
							SWTResourceManager.getImage("img\\Wuerfel1.png"), 0,
							0);
					break;
				case 2:
					e.gc.drawImage(
							SWTResourceManager.getImage("img\\Wuerfel2.png"), 0,
							0);
					break;
				case 3:
					e.gc.drawImage(
							SWTResourceManager.getImage("img\\Wuerfel3.png"), 0,
							0);
					break;
				case 4:
					e.gc.drawImage(
							SWTResourceManager.getImage("img\\Wuerfel4.png"), 0,
							0);
					break;
				case 5:
					e.gc.drawImage(
							SWTResourceManager.getImage("img\\Wuerfel5.png"), 0,
							0);
					break;
				case 6:
					e.gc.drawImage(
							SWTResourceManager.getImage("img\\Wuerfel6.png"), 0,
							0);
					break;
				}

			}
		});

		if (!display.readAndDispatch()) {
			display.sleep();
		}
	}

	public static void warteWuerfelKlick(){
		wuerfel.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				wuerfel.dispose();
			}

			@Override
			public void mouseUp(MouseEvent arg0) {
			}

		});

		while (!wuerfel.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public static int welcheSpielfigur(int spielernummer) { //Abfrage Spielfigurnummer
		letzteAusgewaehlteFigur = 0;

		MouseListener mouseListener1 = new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				letzteAusgewaehlteFigur = 1;
			}

			@Override
			public void mouseUp(MouseEvent arg0) {
			}
		};
		MouseListener mouseListener2 = new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				letzteAusgewaehlteFigur = 2;
			}

			@Override
			public void mouseUp(MouseEvent arg0) {
			}
		};
		MouseListener mouseListener3 = new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				letzteAusgewaehlteFigur = 3;
			}

			@Override
			public void mouseUp(MouseEvent arg0) {
			}
		};
		MouseListener mouseListener4 = new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				letzteAusgewaehlteFigur = 4;
			}

			@Override
			public void mouseUp(MouseEvent arg0) {
			}
		};

		spielfiguren.get(spielernummer * 10 + 1).addMouseListener(mouseListener1);
		spielfiguren.get(spielernummer * 10 + 2).addMouseListener(mouseListener2);
		spielfiguren.get(spielernummer * 10 + 3).addMouseListener(mouseListener3);
		spielfiguren.get(spielernummer * 10 + 4).addMouseListener(mouseListener4);

		while (letzteAusgewaehlteFigur == 0) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		spielfiguren.get(spielernummer * 10 + 1).removeMouseListener(mouseListener1);
		spielfiguren.get(spielernummer * 10 + 2).removeMouseListener(mouseListener2);
		spielfiguren.get(spielernummer * 10 + 3).removeMouseListener(mouseListener3);
		spielfiguren.get(spielernummer * 10 + 4).removeMouseListener(mouseListener4);

		return letzteAusgewaehlteFigur;
	}

	public static void ende(HashMap<Integer, String> plaetze) { //Spielende, zeige Plätze an
		shlMaedn.setBackgroundImage(null);
		for (int spielfigurnummer : spielfiguren.keySet()) {
			spielfiguren.get(spielfigurnummer).dispose();
		}

		lblWelcome = new Label(shlMaedn, SWT.NONE);
		lblWelcome.setImage(SWTResourceManager.getImage("img\\Maedn.png"));
		lblWelcome.setBounds(196, 72, 400, 284);

		lblMaedn = new Label(shlMaedn, SWT.NONE);
		lblMaedn.setAlignment(SWT.CENTER);
		lblMaedn.setFont(SWTResourceManager.getFont("Gabriola", 33, SWT.NORMAL));
		lblMaedn.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblMaedn.setBounds(221, 0, 347, 81);
		lblMaedn.setText("Spiel beendet!");

		Table table = new Table(shlMaedn, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(269, 422, 264, 161);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		String[] titles = { "Platz", "Spieler" };
		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}

		for (int platz : plaetze.keySet()) {
			TableItem item = new TableItem(table, SWT.NULL);
			item.setText(0, platz + ". Platz");
			item.setText(1, plaetze.get(platz));
		}

		for (int i = 0; i < titles.length; i++) {
			table.getColumn(i).pack();
		}

		while (!shlMaedn.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}


}
