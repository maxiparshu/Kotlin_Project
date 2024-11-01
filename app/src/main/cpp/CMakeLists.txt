cmake_minimum_required(VERSION 3.4.1)

project("login")

add_library(${CMAKE_PROJECT_NAME} SHARED
        login.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log)