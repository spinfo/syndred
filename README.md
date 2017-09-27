# Syndred

A Syntax-Driven Editor for Lexical Resources


## Theoretischer Rahmen

Der syntaxkontrollierte Editor ist ein flexibles Werkzeug für unterschiedliche, dynamische und kollaborative Projekte, die in der Sprachlichen Informationsverarbeitung verfolgt werden. Die inhärente Struktur textueller Daten im Sinne domänenspezifischer, formaler Sprachen wird durch Produktionssysteme bzw. Grammatiken in [EBNF](https://en.wikipedia.org/wiki/Extended_Backus-Naur_form) (Extended Backus-Naur form) modelliert. Diese grammatikalischen Regeln wiederum steuern einen Parser, der hierarchische Strukturen für textuelle Daten in Gestalt von Strukturbäumen erzeugt und dabei als Seiteneffekt die formale Korrektheit der Texteingabe überprüft.


### Technisches Konzept

Eine netzwerkbasierte, dezentrale Architektur, genauer eine Web-Applikation mit clientseitigem Editor und serverseitigem Parser sind ideal für kollaboratives Arbeiten an textuellen Daten. Diese Systemarchitektur hat nicht nur den Vorteil der Plattformunabhängigkeit, weiter wird auch die Rechenleistung und die Persistierung anfallender Daten an zentraler Stelle von leistungsfähiger Hardware übernommen.


### Zielsetzung

Ziel ist die Implementierung eines Editors im Split-View Design, welches einerseits das Erstellen und Bearbeiten der EBNF-Grammatiken und andererseits syntaxkontrolliertes Editieren von textuellen Daten nebeneinander zulässt. Damit soll die Möglichkeit geboten werden Grammatiken aus bestehenden Textdaten abzuleiten oder Arbeiten an textuellem Material von bestehenden Grammatiken anleiten und Prüfen zu lassen, wobei sowohl die Grammatik wie auch die Textdaten in direktem, visuellen Bezug zueinander stehen.


## Technische Umsetzung

Auf Seite der Clients wird das auf React basierende Editor-Framework [DraftJS](https://draftjs.org) eingesetzt, welches mittels im JSON-Format abgewickelter Kommunikation an eine serverseitige Java-Applikation auf basis des [Spring-Frameworks](https://spring.io) gekoppelt ist. Es handelt sich hierbei um eine lose Kopplung, da es ohne Weiteres möglich ist, die clientseitige Schnittstelle als native Software zu implementieren oder ein alternatives Web-Frontend anzubinden.


### Installation

Die Anwendung nutzt [Maven](https://maven.apache.org) zur Auflösung externer Abhängigkeiten und kann sowohl innerhalb der [Eclipse IDE](https://www.eclipse.org) als Java-Applikation gestartet werden, als auch mittels Maven zu einer monolithischen JAR paketiert werden. Hierzu muss zunächst dieses Repository geclont werden und der frontend-Pfad entweder in Eclipse als bestehendes Projekt importiert oder aus diesem heraus die Maven Paketierung aufgerufen werden.

Um die Paketierung mittels Maven anzustoßen muss sichergestellt werden, dass die Kommandozeileninterfaces `git` und `mvn` installiert sind, woraufhin folgende Schritte vorgenommen werden müssen:

	git clone https://github.com/spinfo/syndred
	cd syndred/frontend
	mvn clean
	mvn install
	java -jar target/*.jar

Wurde die Applikation korrekt gestartet, so ist das DraftJS-Webinterface auf Port 8080 des ausführenden Gerätes anzutreffen und in der Java-Konsole werden die Vorgänge des Parser ausgegeben.

