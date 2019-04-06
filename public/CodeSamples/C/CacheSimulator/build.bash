#!/bin/bash
export compile="gcc -std=c99 -Wall -c -g "

$compile -o objs/storage.o           storage.c
$compile -o objs/trace.o             trace.c
$compile -o objs/memory.o            memory.c
$compile -o objs/queue.o             queue.c
$compile -o objs/cache_direct.o      cache_direct.c
$compile -o objs/cache_associative_full.o cache_associative_full.c
$compile -o objs/cache_associative_nway.o cache_associative_nway.c

ar -rv libs/libcache.a objs/*.o

gcc -std=c99 -Wall -g  \
    -o test1 test1.c \
    libs/libcache.a

gcc -std=c99 -Wall -g  \
    -o test1a test1a.c \
    libs/libcache.a

gcc -std=c99 -Wall -g \
    -o test2 test2.c \
    libs/libcache.a

gcc -std=c99 -Wall -g \
    -o test3 test3.c \
    libs/libcache.a

gcc -std=c99 -Wall -g \
    -o test_matrix_sum_16x16 test_matrix_sum_16x16.c \
    libs/libcache.a

gcc -std=c99 -Wall -g \
    -o test_matrix_sum_8x4 test_matrix_sum_8x4.c \
    libs/libcache.a

gcc -std=c99 -Wall -g \
    -o test_transpose_8x8 test_transpose_8x8.c \
    libs/libcache.a

gcc -std=c99 -Wall -g \
    -o test_transpose_8x8_improved test_transpose_8x8_improved.c \
    libs/libcache.a

