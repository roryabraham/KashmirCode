#!/bin/bash

export compile="gcc -std=c99 -Wall -c -fpic -g"
export LD_LIBRARY_PATH=./libs

$compile -o objs/storage.o           storage.c
$compile -o objs/trace.o             trace.c
$compile -o objs/memory.o            memory.c
$compile -o objs/queue.o             queue.c
$compile -o objs/cache_direct.o      cache_direct.c
$compile -o objs/cache_associative_full.o cache_associative_full.c
$compile -o objs/cache_associative_nway.o cache_associative_nway.c

gcc -shared -o libs/libcache.so objs/*.o

gcc -std=c99 -Wall -g  \
    -o test1 test1.c \
    -lcache -L./libs

gcc -std=c99 -Wall -g  \
    -o test1a test1a.c \
    -lcache -L./libs

gcc -std=c99 -Wall -g \
    -o test2 test2.c \
    -lcache -L./libs

gcc -std=c99 -Wall -g \
    -o test3 test3.c \
    -lcache -L./libs

gcc -std=c99 -Wall -g \
    -o test_matrix_sum_16x16 test_matrix_sum_16x16.c \
    -lcache -L./libs

gcc -std=c99 -Wall -g \
    -o test_matrix_sum_8x4 test_matrix_sum_8x4.c \
    -lcache -L./libs

gcc -std=c99 -Wall -g \
    -o test_transpose_8x8 test_transpose_8x8.c \
    -lcache -L./libs

gcc -std=c99 -Wall -g \
    -o test_transpose_8x8_improved test_transpose_8x8_improved.c \
    -lcache -L./libs

