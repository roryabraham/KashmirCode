/* 
 * File:   storage.h
 * Author: Tom Burger
 *
 * Created on July 30, 2018, 7:03 PM
 */

#ifndef STORAGE_H
#define	STORAGE_H

#define MAX_STORAGE_SIZE (1024 * 1024)
#define OFFSET_BITS 4      // address bits that are an offset into cache block
#define CACHE_BLOCK_SIZE (1 << OFFSET_BITS)
#define CACHE_ENTRIES 16
#define CACHE_SIZE  (CACHE_ENTRIES * CACHE_BLOCK_SIZE)

typedef unsigned int memory_address;
typedef unsigned char cache_line[CACHE_BLOCK_SIZE];

void storage_init();
int  storage_load(memory_address addr);
void storage_load_line(memory_address addr, cache_line cl);
void storage_store(memory_address addr, int value);
void  storage_store_line(memory_address addr, cache_line cl);
void storage_stats();

#endif	/* STORAGE_H */

