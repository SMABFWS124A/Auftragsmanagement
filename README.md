🧾 Auftragsmanagement-System "Auftragscockpit"
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

Kunden anlegen
Kunden bearbeiten
Kunden löschen
Übersicht aller Kunden in tabellarischer Darstellung

____________________________________________

📦 Artikelverwaltung

Artikel erstellen
Artikel bearbeiten
Artikel löschen
Automatische Margenberechnung (Deckungsbeiträge)
Prüfung von Lagerbeständen
Export der Artikelübersicht als PDF

____________________________________________

🏭 Lieferantenverwaltung

Lieferanten anlegen
Lieferanten bearbeiten
Lieferanten löschen
Verwaltung von Kontaktdaten und Stammdaten

____________________________________________

🛒 Bestellwesen (Beschaffungsprozesse)

Erfassen von Bestellpositionen
Erstellen und Absenden von Bestellungen
Validierungslogiken (Mindestmenge, Lagerbestand, doppelte Artikel)
Automatische Bestellnummernvergabe

____________________________________________

📑 Kundenaufträge

Kundenaufträge erstellen und verwalten
Validierung der Bestellpositionen
Festlegung des Auftragsstatus (NEU → AUSGELIEFERT)
Automatische Lagerbestandsanpassung bei Auslieferung
Ereignisbasierte Verarbeitung (Logging, Bestandsänderung)

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

🛠️ Technologie-Stack

Backend:

Java 17
Spring Boot (REST-API, DI/IoC, JPA)
Spring Data JPA
OpenPDF für PDF-Generierung

Frontend:

HTML5
CSS3
JavaScript
Grid-basiertes UI-Layout
Integration externer Dienste (YouTube, Google Maps über iFrames)

____________________________________________

🔧 Installation & Setup

Repository klonen:

git clone https://github.com/SMABFWS124A/Auftragsmanagement


Mit einer IDE der Wahl (IntelliJ, Eclipse, VS Code) öffnen
Maven Dependencies laden
Spring Boot Anwendung starten
Browser öffnen und die Schnittstellen bzw. HTML-Oberfläche aufrufen

____________________________________________

📅 Geplante Erweiterungen

Rollen- und Rechtemanagement (RBAC)
Erweiterte Lagerbestandsüberwachung
(Weitere) Exportfunktionen (PDF, CSV, Excel)
Automatisierte Mailbenachrichtigungen
Dashboard mit Diagrammen und KPI-Analysen
Volltextsuche in Artikeln und Kunden

