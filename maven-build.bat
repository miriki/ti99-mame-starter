@echo off
chcp 65001 >nul
setlocal ENABLEDELAYEDEXPANSION

:: ============================================================================
::  MINI-CI-SERVER FÜR MAME_TI99_STARTER + TI99IMAGETOOLS
::  All singing, all dancing – mit Bells, Whistles und unnötigen Schritten
:: ============================================================================

:: Farben
for /f "delims=" %%a in ('echo prompt $E^| cmd') do set "ESC=%%a"
set COLOR_HEAD=%ESC%[35m
set COLOR_INFO=%ESC%[36m
set COLOR_OK=%ESC%[32m
set COLOR_WARN=%ESC%[33m
set COLOR_ERR=%ESC%[31m
set COLOR_RESET=%ESC%[0m

:: ============================================================================
::  KONFIGURATION
:: ============================================================================

set SRCLIB1DIR=C:\Users\mritt\eclipse-workspace\Ti99ImageTools
set REP1VER=1.0.0
set REPLIB1DIR=C:\Users\mritt\.m2\repository\com\miriki\ti99\imagetools\%REP1VER%
set REPLIB1BAS=imagetools-%REP1VER%

set SRCAPP1DIR=C:\Users\mritt\eclipse-workspace\MAME_TI99_Starter
set APP1VER=1.0.0
set TRGAPP1DIR=%SRCAPP1DIR%\target
set TRGAPP1BAS=mame-%APP1VER%

set RELAPP1DIR=C:\Users\mritt\eclipse-workspace\releases\MAME_TI99_Starter
set RELAPP1BAS=MAME_TI99_Starter

:: ============================================================================
::  START
:: ============================================================================

call :section "1. Altes .m2-Repo der Library löschen (unnötig, aber gewünscht)"

cd /d "%REPLIB1DIR%" 2>nul
if %errorlevel% neq 0 (
    echo %COLOR_WARN%Repo-Verzeichnis existiert nicht – wird übersprungen.%COLOR_RESET%
) else (
    echo %COLOR_INFO%Lösche alte Repo-Dateien...%COLOR_RESET%
    del "%REPLIB1BAS%.*" 2>nul
    del "_remote.repositories" 2>nul
    cd ..
    rmdir "%REP1VER%" /s /q 2>nul
)
echo %COLOR_OK%Repo-Bereinigung abgeschlossen.%COLOR_RESET%

:: ============================================================================

call :section "2. Altes Release löschen (unnötig, aber gewünscht)"

cd /d "%RELAPP1DIR%"
echo %COLOR_INFO%Lösche altes Release...%COLOR_RESET%
del "%RELAPP1BAS%.*" 2>nul
echo %COLOR_OK%Release-Verzeichnis bereinigt.%COLOR_RESET%

:: ============================================================================

call :section "3. Library neu bauen und installieren"

cd /d "%SRCLIB1DIR%"
echo %COLOR_INFO%Starte Maven-Build der Library...%COLOR_RESET%
call mvn clean install
call :check_error "Library-Build fehlgeschlagen"

echo %COLOR_OK%Library erfolgreich installiert.%COLOR_RESET%

:: ============================================================================

call :section "4. Kontrolle: Maven sieht die richtige Library?"

cd /d "%SRCAPP1DIR%"
echo %COLOR_INFO%Prüfe Dependency-Tree...%COLOR_RESET%
call mvn dependency:tree -Dincludes=com.miriki.ti99:imagetools
call :check_error "Dependency-Tree fehlgeschlagen"

echo %COLOR_OK%Dependency-Check abgeschlossen.%COLOR_RESET%

:: ============================================================================

call :section "5. Starter neu bauen"

cd /d "%SRCAPP1DIR%"
echo %COLOR_INFO%Starte Maven-Build der Applikation...%COLOR_RESET%
call mvn clean package
call :check_error "Starter-Build fehlgeschlagen"

echo %COLOR_OK%Starter erfolgreich gebaut.%COLOR_RESET%

:: ============================================================================

call :section "6. Starter-JAR ins Release kopieren"

cd /d "%RELAPP1DIR%"
echo %COLOR_INFO%Kopiere neues JAR ins Release...%COLOR_RESET%
copy "%TRGAPP1DIR%\%TRGAPP1BAS%.jar" "%RELAPP1BAS%.jar" >nul
call :check_error "Kopieren des Starter-JAR fehlgeschlagen"

echo %COLOR_OK%Release erfolgreich aktualisiert.%COLOR_RESET%

:: ============================================================================

call :section "7. Starte das neue Release"

echo %COLOR_INFO%Starte Anwendung...%COLOR_RESET%
java -jar "%RELAPP1BAS%.jar"
call :check_error "Anwendung konnte nicht gestartet werden"

echo %COLOR_OK%Fertig.%COLOR_RESET%
rem exit /b 0
goto :eof

:: ============================================================================
::  FUNKTION: Abschnittsüberschrift
:: ============================================================================
:section
echo.
echo %COLOR_HEAD%======================================================================
echo   %~1
echo ======================================================================%COLOR_RESET%
echo.
exit /b

:: ============================================================================
::  FUNKTION: Fehler prüfen
:: ============================================================================
:check_error
if %errorlevel% neq 0 (
    echo %COLOR_ERR%FEHLER: %~1%COLOR_RESET%
    echo Batch wird beendet.
    exit /b %errorlevel%
)
exit /b
