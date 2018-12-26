include(ExternalProject)
find_package(Git REQUIRED)

# Get benchmark
ExternalProject_Add(
    benchmark_src
    PREFIX "vendor/benchmark"
    GIT_REPOSITORY "https://github.com/google/benchmark.git"
    GIT_TAG v1.2.0
    TIMEOUT 10
    CMAKE_ARGS
        -DCMAKE_INSTALL_PREFIX=${CMAKE_BINARY_DIR}/vendor/benchmark
        -DCMAKE_C_COMPILER=${CMAKE_C_COMPILER}
        -DCMAKE_CXX_COMPILER=${CMAKE_CXX_COMPILER}
        -DCMAKE_CXX_FLAGS=${CMAKE_CXX_FLAGS}
        -DCMAKE_BUILD_TYPE:STRING=${CMAKE_BUILD_TYPE}
    UPDATE_COMMAND ""
)

# Prepare benchmark
ExternalProject_Get_Property(benchmark_src install_dir)
set(BENCHMARK_INCLUDE_DIR ${install_dir}/include)
set(BENCHMARK_LIBRARY_PATH ${install_dir}/lib/libbenchmark.a)
file(MAKE_DIRECTORY ${BENCHMARK_INCLUDE_DIR})
add_library(benchmark STATIC IMPORTED)
set_property(TARGET benchmark PROPERTY IMPORTED_LOCATION ${BENCHMARK_LIBRARY_PATH})
set_property(TARGET benchmark APPEND PROPERTY INTERFACE_INCLUDE_DIRECTORIES ${BENCHMARK_INCLUDE_DIR})

# Dependencies
add_dependencies(benchmark benchmark_src)
