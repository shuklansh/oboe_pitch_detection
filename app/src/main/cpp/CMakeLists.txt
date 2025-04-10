cmake_minimum_required(VERSION 3.22.1)

project(oboepitch)

# Oboe path relative to your project, assuming you've cloned or copied oboe as a submodule.
set(OBOE_DIR ${CMAKE_SOURCE_DIR}/oboe)

# Add oboe (assumes you added oboe as a submodule or downloaded it in a folder named 'oboe')
add_subdirectory(${OBOE_DIR})

# Create your native library
add_library(oboepitch SHARED native-lib.cpp)

# Include the oboe headers
target_include_directories(oboepitch PRIVATE ${OBOE_DIR}/include)

# Link against oboe and system libraries
target_link_libraries(
        oboepitch
        oboe
        log
        android
)
