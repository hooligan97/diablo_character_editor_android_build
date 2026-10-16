cmake_minimum_required(VERSION 3.22.1)
project("diablo_character_editor")

add_library( # Nazwa biblioteki.
             native-lib

             # Typ biblioteki (współdzielona).
             SHARED

             # Ścieżka do pliku źródłowego.
             native-lib.cpp )
