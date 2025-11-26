## Auftragsmanagement-System "Auftragscockpit"
![Logo](src/main/resources/static/logo.png)

____________________________________________

📦 Auftragsmanagement-System

Ein modular aufgebautes, webbasiertes Verwaltungssystem für Kunden, Artikel, Lieferanten, Bestellungen und Kundenaufträge.
Das Projekt dient als zentrale Plattform, um betriebliche Bestell- und Auftragsprozesse digital, transparent und effizient abzubilden.

____________________________________________

🚀 Funktionsübersicht

Das System stellt eine Vielzahl an Kernfunktionen bereit, die typische Unternehmensprozesse abdecken.

____________________________________________

👥 Kundenverwaltung

- Kunden anlegen (Vorname, Nachname, E-Mail)
- Kunden bearbeiten (Vorname, Nachname, E-Mail)
- Kunden löschen
- Übersicht aller Kunden in tabellarischer Darstellung

____________________________________________

📦 Artikelverwaltung

- Artikel erstellen (Aritkelnummer, Name, EK, VK, Kategorie, Lagerbestand, Beschreibung, Ist Artikel aktiv?)
- Artikel bearbeiten
- Artikel löschen
- Automatische Margenberechnung (Deckungsbeiträge)
- Prüfung von Lagerbeständen
- Export der Artikelübersicht als PDF

____________________________________________

🏭 Lieferantenverwaltung

- Lieferanten anlegen (Firmenname, Ansprechpartner, E-Mail, Telefon, Addresse)
- Lieferanten bearbeiten
- Lieferanten löschen
- Detaillierte Anzeige aller Informationen zu einem Lieferanten ("Details")
- Verwaltung von Kontaktdaten und Stammdaten

____________________________________________

🛒 Bestellwesen (Beschaffungsprozesse)

- Erfassen von Bestellpositionen
- Erstellen und Absenden von Bestellungen
- Validierungslogiken (Mindestmenge, Lagerbestand, doppelte Artikel)
- Automatische Bestellnummernvergabe

____________________________________________

📑 Kundenaufträge

- Kundenaufträge erstellen und verwalten
- Validierung der Bestellpositionen (Auswahl mind. 1 oder mehrere)
- Festlegung des Auftragsstatus (NEU → AUSGELIEFERT)
- Automatische Lagerbestandsanpassung bei Auslieferung
- Ereignisbasierte Verarbeitung (Logging, Bestandsänderung)

____________________________________________

🎯 Ziel des Projekts

Das Auftragsmanagement-System ist so konzipiert, dass es eine moderne und robuste Basis für Geschäftsprozesse bildet.
Es ermöglicht:

- strukturierte und konsistente Datenhaltung
- klare, nachvollziehbare Prozesslogik
- intuitive Bedienbarkeit durch eine leichtgewichtige Web-Oberfläche
- leichte Erweiterbarkeit für neue Geschäftsbereiche (z. B. Lagerlogistik, Reporting, Rollenverwaltung)
- modulare, wiederverwendbare Architekturkomponenten

____________________________________________

Entity-Relationship-Diagramm

[![ERD](src/main/resources/static/Entity-Relationship-Diagramm.svg)](src/main/resources/static/Entity-Relationship-Diagramm.svg)
____________________________________________

🛠️ Technologie-Stack

Backend:

- Java 17
- Spring Boot (REST-API, DI/IoC, JPA)
- Spring Data JPA
- OpenPDF für PDF-Generierung

Frontend:

- HTML5
- CSS3
- JavaScript
- Grid-basiertes UI-Layout
- Integration externer Dienste (YouTube, Google Maps über iFrames)

____________________________________________

🔧 Installation & Setup

Repository klonen:

git clone https://github.com/SMABFWS124A/Auftragsmanagement


Mit einer IDE der Wahl (IntelliJ, Eclipse, VS Code) öffnen, Maven Dependencies laden, Spring Boot Anwendung starten
=> Browser öffnen und die Schnittstellen bzw. HTML-Oberfläche aufrufen über: http://localhost:8080/login.html

Anmeldedaten:

| Rolle | Benutzername / E-Mail | Passwort | Hinweis | 
| :--- | :--- | :--- | :--- | 
| **Testbenutzer** | `test@test.de` | `Test123` | Vollständiger Zugriff auf alle Verwaltungsbereiche  für Testzwecke. | 

____________________________________________

📅 Geplante Erweiterungen

- Rollen- und Rechtemanagement (RBAC)
- Erweiterte Lagerbestandsüberwachung
- Automatisierte Mailbenachrichtigungen
- Dashboard mit Diagrammen und KPI-Analysen
- Volltextsuche in Artikeln und Kunden
- (Weitere) Exportfunktionen (PDF, CSV, Excel)

____________________________________________

Viel Spaß beim Benutzen und Testen der Anwendung!




