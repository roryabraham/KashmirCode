/* 
 * File:   memory.h
 * Author: Tom Burger
 *
 * Created on July 30, 2018, 6:47 PM
 */

#ifndef MEMORY_H
#define	MEMORY_H

#include "storage.h"

enum cache_type {
    NO_CACHE,
    CACHE_DIRECT,
    CACHE_ASSOCIATIVE_FULL,
    CACHE_ASSOCIATIVE_NWAY,
    MAX_CACHE_TYPES
};

// initialize the memory subsystem and clear any statistics.
// This also establishes the type of caching that is desired.
void memory_init(enum cache_type ct);

// load a 4 byte value from the specified address.
// The value is returned as the function result.
// The address must be a properly aligned address for a 4 byte
// int.  That is, the address mod 4 must equal 0.
int  memory_load(memory_address addr);

// store a 4 byte value into memory.
// The address must be a properly aligned address for a 4 byte int.
void memory_store(memory_address addr, int value);

// flush will write any dirty values back to the storage.
void memory_flush();

// output the accumulated statistics.  This includes the number
// of memory_load and memory_store operations, the storage statistics,
// and any cache statistics.
void memory_stats();

#endif	/* MEMORY_H */

