# Projektowe Wytyczne

## Ograniczenia Środowiskowe
- **ZAKAZ budowania lokalnego:** Nigdy nie próbuj uruchamiać `./gradlew`, `gradle` ani budować projektu lokalnie w środowisku Termux. Telefon nie posiada zainstalowanego Android SDK.
- **CI/CD:** Budowanie projektu oraz generowanie pliku APK odbywa się automatycznie w chmurze za pomocą GitHub Actions.

## Zadania Inżynierskie
Działania są ograniczone wyłącznie do:
1. Edycji plików Java (`.java`) oraz plików XML (`.xml`).
2. Zarządzania zmianami w kontroli wersji: `git commit` oraz `git push`.
