cmake_minimum_required(VERSION 3.21)
project(diablo_character_editor_android LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(PROJECT_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/../../../../")
list(APPEND CMAKE_MODULE_PATH "${PROJECT_ROOT}/3p")

set(DEVILUTIONX_STATIC_ZLIB ON)
set(DEVILUTIONX_STATIC_BZIP2 ON)

add_subdirectory("${PROJECT_ROOT}/3p/zlib" "${CMAKE_CURRENT_BINARY_DIR}/3p/zlib")
add_subdirectory("${PROJECT_ROOT}/3p/bzip2" "${CMAKE_CURRENT_BINARY_DIR}/3p/bzip2")
add_subdirectory("${PROJECT_ROOT}/3p/PKWare" "${CMAKE_CURRENT_BINARY_DIR}/3p/PKWare")
add_subdirectory("${PROJECT_ROOT}/3p/libmpq" "${CMAKE_CURRENT_BINARY_DIR}/3p/libmpq")

add_library(d1edit STATIC
    "${PROJECT_ROOT}/src/hero_json.cpp"
    "${PROJECT_ROOT}/src/codec.cpp"
    "${PROJECT_ROOT}/src/sha.cpp"
    "${PROJECT_ROOT}/src/encrypt.cpp"
    "${PROJECT_ROOT}/src/pfile.cpp"
    "${PROJECT_ROOT}/src/mpq/mpq_common.cpp"
    "${PROJECT_ROOT}/src/mpq/mpq_reader.cpp"
    "${PROJECT_ROOT}/src/mpq/mpq_writer.cpp"
)

target_include_directories(d1edit
    PUBLIC
        "${PROJECT_ROOT}/src"
        "${PROJECT_ROOT}/3p/PKWare"
)

target_compile_features(d1edit PUBLIC cxx_std_20)

target_link_libraries(d1edit
    PUBLIC
        PKWare
        libmpq
        ZLIB::ZLIB
        BZip2::BZip2
)

add_library(diablo_character_editor SHARED
    native-lib.cpp
)

target_link_libraries(diablo_character_editor PRIVATE d1edit)
