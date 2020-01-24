# Refresh

Das neue KvFG-Backend für die neue Vertretungsplan-PWA.
Gebaut mit Spring und Spring Boot.

## Vorkenntnisse

Refresh ist in Java geschrieben, also werden Kentnisse in dieser Sprache empfohlen. Dazu kommen HTTP, Apache, allgemeines Web-Wissen
und natürlich Spring mit Spring Boot. Auch sollte man über eine Portion Linux, LDAP und Apache-Kentnisse verfügen.

## Wichtig

Bei Veränderungen **immer** die Autoren updaten! Dann ist klar, wer was verbrochen hat. ;)
Konfiguration erfolgt über die Datei **src/main/resources/application.yml**,
deren Werte können aber auf der Kommandozeile beim Start (refresh.service) überschrieben werden,
z.B. Passwörter für de Zugriff auf die Datenbanken refresh (session-logging) und raumwetter (Raumwetter-Anzeige)!

## Maintenance

Derzeit ist Refresh als Upstart-Job realisiert. Mit dem ``service``-Befehl kann man auf Refresh zugreifen
(der Service heißt auch ``refresh``). Die Befehle funktionieren wie folgt: ``service refresh <befehl>``

Befehle (gültig für alle Upstart-Jobs):
* ``status`` gibt den derzeitigen Status aus.
* ``restart`` startet den laufenden Refresh-Service neu.
* ``start`` startet Refresh. Wenn Refresh bereits läuft muss ``restart`` verwendet werden.
* ``stop`` stoppt Refresh. Muss mit ``restart``/``start`` gestartet werden.


Im ``scripts``-Ordner gibt es Skripte zum Updaten, Reparieren etc. von Refresh.
* ``update.sh``: Pullt Änderungen von ``https://git.kvfg.eu/elag/refresh``, baut mit Maven die neue jar-Datei,
stoppt den laufenden Refresh-Service, überschreibt die Jar und startet den Service erneut.

#### Logging

Der derzeit laufende Prozess loggt in die Datei ``/opt/refresh/log/spring.log``. Die Log-Datei wird
ungefähr einen Tag behalten und danach gelöscht.
Sie liegt solang gzippt in ``/opt/refresh/log/spring.log.YEAR-MONTH-DAY.0.gz``.
Im laufenden Betrieb können Admins über die URL ``/logging/exceptions`` alle Exceptions sehen, die nicht
von der internen Logik behandelt wurden. Wenn möglich sollte die fehlenden Fälle dann hinzugefügt werden.

#### Mensa funktioniert nicht

Mein Beileid. Bitte höre [dies](https://www.youtube.com/watch?v=4Js-XbNj6Tk) an während du weiter liest.
Da die Mensa alle 3 Tage ihr Website-Design ändert,
hatte schon die letzte App kein funktionierendes Mensa-Plan-Parsing gehabt.
Solltest du kein Java/nicht programmieren können, hast du quasi nur noch eine Chance.
**Hat die Mensa an mehr oder weniger Tagen als früher (4 Tage) offen und das macht die App kaputt?**
Wenn ja, in der config einfach ``mensa.day-count`` auf die richtige Anzahl einstellen.
Wenn nein, mein Beileid. Lösche einfach das gesamte Mensa-Package um die Mensa-Funktion zu deaktivieren.
Solltest du programmieren können, viel Spaß. Ich habe versucht das Mensa-Parsing so einfach wie möglich
zu gestalten und so viel wie möglich zu kommentieren.

#### InvalidClassException

Spring serialisiert die Session-Objekte einfach in die Datenbank hinein. Das hat zur Folge, dass,
sollte Java geupdatet werden, vielleicht die Objekte nicht wieder zu aus der Datenbank deserialisiert
werden können. In diesem Fall müssen alle Einträge in den Tabellen ``spring_session`` und ``spring_session_attributes``
gelöscht werden (*NICHT* die Tabellen droppen).

### Umziehen

1. Einen neuen Ordner ``refresh-build`` im neuen Zielordner, in dem die Jar landen soll, erstellen.
2. In den Build-Ordnern dass Repository ``https://git.kvfg.eu/elag/refresh`` clonen.
3. In dem Zielordner Update-Skript kopieren und dort laufen lassen.


### Login (ent-)sperren

Alle Änderungen sind in der ``application.yml`` durchzuführen.

**Login für...**
* ...alle Nutzer sperren/entsperren, die **nicht explizit** erlaubt sind:
``security.login.disabled`` auf true/false setzen.
* ...einen Nutzer sperren/entsperren, der **nicht explizit** erlaubt ist:
den Benutzernamen bei ``security.login.denied`` hinzufügen/entfernen.
* ...eine Klasse **explizit** erlauben: den Klassennamen in
Großbuchstaben zu ```security.login.allowed.grades``` hinzufügen/entfernen.
* ...einen Nutzer **explizit** erlauben: den Benutzernamen zu
``security.login.allowed.users`` hinzufügen/entfernen.

Achtung: Administratoren können sich immer anmelden.

## Endpoints

Jeder Controller besitzt einen Endpoint-Bereich. Der StationController ist also für den ``/station``-Bereich zuständig.
Dies gilt auch für alle anderen Controller, was aber nicht heißt, dass der URL-Pfad eins zu eins mit dem Controller-Namen übereinstimmt
(z.B. InternalInformationController -> ``/internal``).

## Cross-Origin

Derzeit werden die CORS-Header per Apache gesetzt werden. Irgendwas oder irgendwer entfernt diese tollen Header, wenn sie von refresh gesendet werden.
Also übernimmt Apache die Aufgabe, diese zu jeder Antwort hinzuzufügen.
Apache ist schon konfiguriert, die entsprechenden Einstellungen müssen nur noch "ein"-kommentiert werden.

## Author(en)
Lukas Nasarek (2017/2018), email: siehe commits

## Antworten

### Fehler

"Normale" Fehler: 


| Exception | HTTP-Statuscode | Code |
|---|---|---|
| MissingServletRequestParameterException | 400 | 1 |
| NoHandlerFoundException | 404 | 1 |
| HttpRequestMethodNotSupportedException | 405 | 1 |
| InvalidClassException | 500 | 10 |

Wetter-Fehler: 


| Exception | HTTP-Statuscode | Code |
|---|---|---|
| TypeNotFoundException | 404 | 2 |
| StationNotFoundException | 404 | 3 |
| DataAccessException | 404 | 4 |
| ImpossibleIntervalException | 409 | 1 |


Login-Fehler:


| Exception | HTTP-Statuscode | Code |
|---|---|---|
| Unauthorized requests | 401 | 1 |
| BadCredentialsException | 401 | 2 |
| LoginDisabledException | 401 | 3 |
| LoginDeniedException | 401 | 4 |